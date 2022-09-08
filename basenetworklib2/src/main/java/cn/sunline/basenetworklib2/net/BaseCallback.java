package cn.sunline.basenetworklib2.net;


import androidx.annotation.NonNull;

import java.io.UnsupportedEncodingException;

import cn.sunline.uicommonlib.utils.Logger;

/**
 * <p>文件描述：所有http响应的回调处理基类，响应的数据为字节数组<p>
 * <p>作者: zengll<p>
 * <p>创建时间：2018/8/17<p>
 */
public abstract class BaseCallback implements ICallback {

    private IResponseDataHandler handler;

    public BaseCallback(@NonNull IResponseDataHandler handler){
        this.handler = handler;
    }

    public BaseCallback(){

    }

    /**
     * view是否可用
     * @return
     */
    public boolean isDataValid(){
        return this.handler == null || this.handler.doDataHandle("");
    }

    @Override
    public void onResponseDataOk(byte[] data){
        boolean isDataValid = this.handler == null || this.handler.doDataHandle(data);

        if(isDataValid){
            final String stringData = getResponseString(data,"utf-8");
            onParseData(stringData);
        }
    }

    @Override
    public void onResponseDataOk(String data){
        boolean isDataValid = this.handler == null || this.handler.doDataHandle(data);

        if(isDataValid){
            onParseData(data);
        }
    }

    @Override
    public void onResponseFail(final Throwable throwable) {
        Logger.d("BaseCallback",throwable.getMessage());
        if(null != this.handler) {
            this.handler.doThrowableHandle(throwable);
        }
    }

    /**
     * 将response转为string
     * @param stringBytes
     * @param charset
     * @return
     */
    private String getResponseString(byte[] stringBytes, String charset) {
        try {
            String e = stringBytes == null?null:new String(stringBytes, charset);
            return e != null && e.startsWith("\ufeff")?e.substring(1):e;
        } catch (UnsupportedEncodingException var3) {
            return null;
        }
    }

    public abstract void onParseData(String data);
}
