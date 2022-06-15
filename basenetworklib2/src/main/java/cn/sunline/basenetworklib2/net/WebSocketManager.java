package cn.sunline.basenetworklib2.net;

import android.content.Context;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import cn.sunline.uicommonlib.utils.Logger;

/**
 * <p>文件描述：<p>
 * <p>作者: zengll<p>
 * <p>创建时间：2018/9/18<p>
 */
public class WebSocketManager {
    private PushChannel mChannel;

    private static WebSocketManager instance;

    private HashSet<String> mSubscribeHistory = new HashSet<>();

    private int lastNetState = NetChangedReceiver.NETWORK_NONE;//初始值

    private WebSocketManager(Context context,String url, ICallback callback){
        mChannel = new PushChannel(context,url, listener,callback);
        init();
    }

    private void init(){
        initNetChangedListener();

        doConnect();
    }

    public static WebSocketManager getInstance(Context context,String url, ICallback callback){
        if(null == instance){
            instance = new WebSocketManager(context.getApplicationContext(), url, callback);
        }

        return instance;
    }

    /**
     * 初始化网络监听
     */
    private void initNetChangedListener(){
        NetChangedReceiver.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                int mode = NetChangedReceiver.NETWORK_NONE;
                try {
                    mode = (int) arg;
                } catch (Exception e) {
                }
                if(lastNetState == NetChangedReceiver.NETWORK_NONE  && mode != NetChangedReceiver.NETWORK_NONE ){
                    //由无网络转为有网状态
                    reConnect();
                }
                lastNetState = mode;
            }
        });
    }

    public void doConnect(){
        mChannel.connect();
    }

    public void reConnect(){
        mChannel.reconnect();
    }

    public void doShutdown(){
        mChannel.doShutDown();
    }


    private ISocketConnStatusListener listener = new ISocketConnStatusListener() {
        @Override
        public void onStatusChange(ConnStatus status) {
            Logger.d("wsmanager", "connect: " + mChannel +"; 状态: " +status.getDesc());
            if(status == ConnStatus.CONNECTED){
                reRegister();
            }
        }
    };

    /**
     * 重连之后恢复订阅
     */
    private void reRegister(){
        Iterator<String> iterator = mSubscribeHistory.iterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            mChannel.sendMessage (str);
        }
    }

    public  void subscribMsg(String msg){
        mChannel.sendMessage (msg);
        mSubscribeHistory.add(msg);
    }

    public void unSubscribeMsg(String msg){
        mChannel.sendMessage (msg);
        mSubscribeHistory.clear();
    }
}
