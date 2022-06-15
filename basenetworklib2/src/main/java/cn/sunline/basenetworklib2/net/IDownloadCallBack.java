package cn.sunline.basenetworklib2.net;

public interface IDownloadCallBack {
    void onFail();
    void onSuccess();
    void onProgress(int progress);
}
