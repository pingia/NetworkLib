package cn.sunline.basenetworklib2.net;

/**
 * <p>文件描述：Socket连接状态监听器<p>
 * <p>作者: zengll<p>
 * <p>创建时间：2018/8/17<p>
 */
public interface ISocketConnStatusListener {
    enum ConnStatus{
       CONNECTED("已连接"),
       DISCONNECTING("正在关闭连接"),
       DISCONNECTED("连接已关闭"),
       FAILED("连接失败");

        private String description;
        ConnStatus(String desc){
            this.description = desc;
        }

        String getDesc(){
            return description;
        }
    }

    void onStatusChange(ConnStatus status);
}


