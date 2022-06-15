package cn.sunline.basenetworklib2.net;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * author：admin on 2017/8/22.
 * mail:zengll@hztxt.com.cn
 * function:
 */
public class AppException extends Exception {
    public static final int JSON_EXCEPTION = 1;//数据格式异常
    public static final int RUNTIME_EXCEPTION = 2;//未知错误
    public static final int NET_EXCEPTION = 3;//网络错误
    public final static int HTTP_CODE_EXCEPTION = 4;//网络异常
    public final static int NO_NETWORK_EXCEPTION = 5;//网络无法连接,请检查您的网络设置然后重试
    public final static int BUSINESS_EXCEPTION = 6;  //所有业务异常归于这个类型


    private int type;// 异常的类型
    private int code; //异常状态码 可以是网络错误状态码或业务状态码
    private String msg; //完整错误信息
    private String desc; //自定义的错误描述信息
    private int descResId; //自定义的错误描述信息的资源文件id

    private AppException(int type, Exception exp, String desc) {
        super(exp);
        this.type = type;
        this.msg = exp.getMessage();
        this.desc = desc;
    }

    private AppException(int type, int code, String msg, String desc) {
        super(msg);
        this.type = type;
        this.code = code;
        this.msg = msg;
        this.desc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public int getType() {
        return this.type;
    }

    public String getMsg(){
        return this.msg;
    }

    public static AppException http(int code) {
        return http(code,null);
    }

    public static AppException http(int code,String msg) {
        return new AppException(HTTP_CODE_EXCEPTION, code, msg,"http状态异常");
    }

    // io异常
    public static AppException io(Exception e) {
        if (e instanceof UnknownHostException || e instanceof ConnectException) {
            return new AppException(NO_NETWORK_EXCEPTION, e,"未知主机");
        } else if (e instanceof IOException) {
            return new AppException(NET_EXCEPTION, e,"网络异常");
        }
        return run(e);
    }

    public static AppException json(Exception e) {
        return new AppException(JSON_EXCEPTION, e,"json格式异常");
    }

    public static AppException run(Exception e) {
        return new AppException(RUNTIME_EXCEPTION, e,"运行时异常");
    }

    public static AppException business(int errorCode,String errorMsg) {
        return new AppException(BUSINESS_EXCEPTION, errorCode,errorMsg,errorMsg);
    }

    public String getDesc(){
        return desc;
    }
}
