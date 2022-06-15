package cn.sunline.basenetworklib2.net;

/**
 * author：admin on 2017/8/15.
 * mail:zengll@hztxt.com.cn
 * function: 业务接口结果的回调
 */
public interface ICallback {
    void onResponseDataOk(byte[] data);
    void onResponseDataOk(String data);
    void onResponseFail(Throwable throwable) ;
}
