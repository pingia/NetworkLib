package cn.sunline.basenetworklib2.net;

import java.io.File;

import okhttp3.HttpUrl;

/**
 * author：admin on 2017/8/16.
 * mail:zengll@hztxt.com.cn
 * function:
 */
public interface IHttpClient {
     void post(HttpUrl url, RequestParams params, ICallback callback);
     void postJson(HttpUrl url, String json, ICallback callback);
     void postFormData(HttpUrl url, RequestParams params, ICallback callback);
     void postFile(HttpUrl url, String fileType, File file, ICallback callback);
     void get(HttpUrl url, RequestParams params, ICallback callback);
     void download(HttpUrl url, String saveDir, IDownloadCallBack listener);
}
