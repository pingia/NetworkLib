package cn.sunline.basenetworklib2.net;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.sunline.basenetworklib2.util.URLUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * author：admin on 2017/8/16.
 * mail:zengll@hztxt.com.cn
 * function: 使用OkHttp来请求网络数据
 */
public class OkHttpClientWrapper implements IHttpClient{
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient mOkHttpClient;
    private static OkHttpClientWrapper mWrapper;


    private Context mContext;

//    private Cookie mSession = null;
//    private SharedPreferences mCookieCache;
    public static OkHttpClientWrapper getInstance(Context context,CookieJar cookieJar,
                                                  OkHttpClient.Builder clientBuilder,
                                                  Headers.Builder builder){
        if(mWrapper == null){
            mWrapper = new OkHttpClientWrapper(context.getApplicationContext(), cookieJar, clientBuilder, builder);
        }
        return mWrapper;
    }

    private OkHttpClientWrapper (Context context,
                                 CookieJar cookieJar,
                                 OkHttpClient.Builder clientBuilder,
                                 Headers.Builder builder) {
        mContext = context;
        RequestIntercepter requestIntercepter = new RequestIntercepter(builder);
        LogingIntercepter logIntercepter = new LogingIntercepter();

        mOkHttpClient = clientBuilder
                .followSslRedirects(false)
                .followRedirects(false)
                .cookieJar(cookieJar)
                .addInterceptor(requestIntercepter)
                .addInterceptor(logIntercepter)
                .addNetworkInterceptor(logIntercepter)
                .build();
    }

    /**
     * 创建用于上传图片的RequestBody
     */
    private RequestBody createFileRequestBody(final @Nullable MediaType contentType, final File file) {
        return new RequestBody() {
            @Override
            public @Nullable
            MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                /*上传图片最终会用到MultipartBody，表单数据最终会写入Content-Length:xxx，xxx的值为contentLength()，
                 * 而我们的服务端api不支持Content-Length，需要禁止Content-Length的写入，写入逻辑见MultipartBody的
                 * writeOrCountBytes方法*/
                return /*file.length()*/-1;
            }

            @Override
            public void writeTo(@Nullable BufferedSink sink) throws IOException {
                Source source = null;
                try {
                    source = Okio.source(file);
                    sink.writeAll(source);
                } finally {
                    Util.closeQuietly(source);
                }
            }
        };
    }

    @Override
    public void post(HttpUrl url, RequestParams params, ICallback callback){
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (String key : params.urlParams.keySet()){
            Object value = params.urlParams.get(key);
            if(null != key && null != value ) {
                formBodyBuilder.addEncoded(key, value.toString());
            }
        }
        FormBody formBody = formBodyBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        mOkHttpClient.newCall(request).enqueue(new OkHttpCallback(callback));
    }

    @Override
    public void postJson(HttpUrl url, String json, ICallback callback){
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        mOkHttpClient.newCall(request).enqueue(new OkHttpCallback(callback));
    }

    @Override
    public void postFormData(HttpUrl url, RequestParams params, ICallback callback){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for(String fileKey : params.mediaTypeFilePairParams.keySet()){
            Pair<String, File> pair = params.mediaTypeFilePairParams.get(fileKey);
            if(null != pair) {
                if(null == pair.first) continue;

                MediaType mediaType = MediaType.parse(pair.first);
                if(null == mediaType) continue;

                File file = pair.second;
                RequestBody fileBody = createFileRequestBody(mediaType, file);
                builder.addFormDataPart(fileKey, file.getName(), fileBody);
            }
        }

        for(String paramName: params.urlParams.keySet()){
            Object paramValue = params.urlParams.get(paramName);
            if(null != paramValue) {
                builder.addFormDataPart(paramName, paramValue.toString());
            }
        }

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        mOkHttpClient.newCall(request).enqueue(new OkHttpCallback(callback));
    }

    @Override
    public void postFile(HttpUrl url, String fileType, File file, ICallback callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if(null == fileType) return;

        MediaType mediaType = MediaType.parse(fileType);
        if(null == mediaType) return;

        RequestBody fileBody = createFileRequestBody(mediaType, file);
        builder.addFormDataPart("", file.getName(), fileBody);

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        mOkHttpClient.newCall(request).enqueue(new OkHttpCallback(callback));
    }

    @Override
    public void get(HttpUrl url, RequestParams params, ICallback callback) {
        Request request;
        if(params == null){
            request = new Request.Builder()
                    .url(url)
                    .build();
        }else{
            request = new Request.Builder()
                    .url(URLUtils.makeURL(url.toString(),params.getParamString()))
                    .build();
        }
        mOkHttpClient.newCall(request).enqueue(new OkHttpCallback(callback));
    }

    @Override
    public void download(HttpUrl url, final String saveDir, final IDownloadCallBack callBack) {
        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                    @Override
                    public void call() {
                        // 下载失败
                        callBack.onFail();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = saveDir;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        final int progress = (int) (sum * 1.0f / total * 100);
                        AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                            @Override
                            public void call() {
                                // 下载中
                                callBack.onProgress(progress);
                            }
                        });
                    }
                    fos.flush();
                    AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                        @Override
                        public void call() {
                            // 下载完成
                            callBack.onSuccess();
                        }
                    });
                } catch (Exception e) {
                    AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                        @Override
                        public void call() {
                            // 下载完成
                            callBack.onFail();
                        }
                    });
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }
}