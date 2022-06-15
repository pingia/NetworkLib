package cn.sunline.basenetworklib2.net;

import java.io.File;

import okhttp3.HttpUrl;

/**
 * author：admin on 2017/8/16.
 * mail:zengll@hztxt.com.cn
 * function:
 */
public class HttpEngine {
    private IHttpClient mHttpClient;

    private static class SingleInstanceHolder{
        private static final HttpEngine INSTANCE = new HttpEngine();
    }

    public static final HttpEngine getInstance(){
        return SingleInstanceHolder.INSTANCE;
    }

    public void setHttpClient(IHttpClient client){
        mHttpClient = client;
    }

    public IHttpClient getHttpClient(){
        return this.mHttpClient;
    }

    public void post(HttpUrl url, RequestParams params, ICallback callback){
        //调用第三方网络客户端来发送请求。
        if(null != mHttpClient) {
            mHttpClient.post(url, params, callback);
        }
    }

    public void postJson(HttpUrl url, String json, ICallback callback){
        if(null != mHttpClient) {
            mHttpClient.postJson(url, json, callback);
        }
    }

    public void postFormData(HttpUrl url, RequestParams params, ICallback callback){
        if(null != mHttpClient) {
            mHttpClient.postFormData(url, params, callback);
        }
    }

    public void postFile(HttpUrl url, String fileType, File file, ICallback callback){
        if(null != mHttpClient) {
            mHttpClient.postFile(url, fileType, file,  callback);
        }
    }

    public void get(HttpUrl url, RequestParams params, ICallback callback){
        if(null != mHttpClient) {
            mHttpClient.get(url, params, callback);
        }
    }

    public void download(HttpUrl url,String path,IDownloadCallBack callBack){
        if(null != mHttpClient) {
            mHttpClient.download(url, path, callBack);
        }
    }
}
