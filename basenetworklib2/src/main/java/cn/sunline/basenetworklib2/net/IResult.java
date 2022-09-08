package cn.sunline.basenetworklib2.net;

/**
 * Description:
 * Created by zenglulin@youxiang.com
 * <p>
 * Date: 2022/8/3
 */
public interface IResult<T> {
    String getDesc();
    boolean isSuccess();
    T getData();
}
