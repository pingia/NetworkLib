package cn.sunline.basenetworklib2.net;

import java.io.IOException;
import java.util.Locale;

import cn.sunline.uicommonlib.utils.Logger;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <p>文件描述：统一的OkHttp请求响应日志拦截器<p>
 * <p>作者: zengll<p>
 * <p>创建时间：2018/8/17<p>
 */
public class LogingIntercepter implements Interceptor {
    private static final String TAG = LogingIntercepter.class.getSimpleName();
    private static final Locale LOCALE_FOR_CHINA = Locale.CHINA;
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = System.nanoTime();

        String requestLogFormat = "Send request %s on %s%n%s";
        Logger.d(TAG, String.format(LOCALE_FOR_CHINA, requestLogFormat, request.url(), chain.connection(), request.headers()));

        Response response = chain.proceed(request);
        long endTime = System.nanoTime();

        String responseLogFormat = "Received response for %s in %.1fms%n%s";
        Logger.d(TAG,String.format(LOCALE_FOR_CHINA, responseLogFormat, response.request().url(), (endTime-startTime)/1e6d, response.headers()));

        return response;
    }
}
