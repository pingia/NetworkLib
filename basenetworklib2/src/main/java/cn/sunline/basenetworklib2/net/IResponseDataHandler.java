package cn.sunline.basenetworklib2.net;

/**
 * <p>文件描述：网络响应的一些前置和公用处理<p>
 * <p>作者: zengll<p>
 * <p>创建时间：2018/8/20<p>
 */
public interface IResponseDataHandler {
    boolean doDataHandle(byte[] data);  //对数据做一些前置的处理，如果某些条件下，这些数据应该被丢弃（无效数据），方法需要返回false
    boolean doDataHandle(String data);
    void doThrowableHandle(Throwable throwable);
}
