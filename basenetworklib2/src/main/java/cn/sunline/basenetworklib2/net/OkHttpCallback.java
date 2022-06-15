package cn.sunline.basenetworklib2.net;


import java.io.IOException;
import java.net.HttpURLConnection;

import cn.sunline.uicommonlib.utils.Logger;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * <p>文件描述：OkHttp请求/响应对的回调<p>
 * <p>作者: zengll<p>
 * <p>创建时间：2018/8/17<p>
 */
public class OkHttpCallback implements Callback {
    private static final String TAG = OkHttpCallback.class.getSimpleName();

    private ICallback mCallback;
    public OkHttpCallback(ICallback callback){
        this.mCallback = callback;
    }

    @Override
    public void onFailure(final Call call, final IOException e) {
        //失败，经常是连接超时,未知主机这种异常，直接丢到主线程执行
        if(null != this.mCallback){
            AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                @Override
                public void call() {
                    mCallback.onResponseFail(AppException.io(e));
                }
            });
        }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final int code = response.code();
        final String msg = response.message();
        final String url = response.request().url().toString();

        byte[] resultBytes;
        if(code == HttpURLConnection.HTTP_OK){
            Logger.d(TAG, "response ok from request url : " + url );
            if(response.body()!=null && (resultBytes = response.body().bytes()) !=null){
                if(null != this.mCallback){
                    mCallback.onResponseDataOk(resultBytes);
                }
            }else{
                Logger.w(TAG, "i am so sad! empty data response from request url : " + url );
                if(null != this.mCallback){
                    AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                        @Override
                        public void call() {
                            mCallback.onResponseFail(AppException.http(code,msg));
                        }
                    });
                }
            }
        }else {
            Logger.e(TAG, "response status code : " + code + " from request url : " + url );

            if(null != this.mCallback){
                AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                    @Override
                    public void call() {
                        mCallback.onResponseFail(AppException.http(code,msg));
                    }
                });
            }
        }

    }
}