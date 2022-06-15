package cn.sunline.basenetworklib2.net;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <p>文件描述：OkHttp 请求头和响应头的设置<p>
 * <p>作者: zengll<p>
 * <p>创建时间：2018/11/26<p>
 */
public class RequestIntercepter implements Interceptor {
    private Headers.Builder mHeadBuilder;
    public RequestIntercepter(Headers.Builder builder){
        this.mHeadBuilder = builder;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .headers(mHeadBuilder.build())
                .build();

        return chain.proceed(request);
    }
}
