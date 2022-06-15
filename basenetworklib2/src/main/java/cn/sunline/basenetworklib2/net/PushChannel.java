package cn.sunline.basenetworklib2.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cn.sunline.uicommonlib.utils.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * <p>文件描述：<p>
 * <p>作者: zengll<p>
 * <p>创建时间：2018/9/18<p>
 */
public class PushChannel {
    private static String TAG = PushChannel.class.getSimpleName();
    private WebSocket mWebSocketClient;

    private static final int SECOND_UNIT = 1000;
    private static final int MIN_RE_CONNECT_TIME = 5;       //最小重连时间间隔
    private static final int MAX_RE_CONNECT_TIME = 120;     //最大重连时间间隔
    private  int reConnectInterval = MIN_RE_CONNECT_TIME;   //重连时间间隔，单位：秒.
    private int mReconnectTimes = 0;
    private boolean notReconnecting = true;
    private Timer mTimer;

    private String mUrl;

    private ISocketConnStatusListener mConnectListener;
    private ICallback mDataCallback;

    private Context mContext;

    public PushChannel(@NonNull Context context, String url, ISocketConnStatusListener listener, ICallback callback){
        this.mContext = context;
        this.mUrl = url;
        mConnectListener = listener;
        mDataCallback = callback;
    }

    public String getUrl(){
        return mUrl;
    }

    /**
     * 连接websocket
     */
    public void connect(){
        Request request =
                new Request.Builder()
                .url(mUrl)
                .build();
        mWebSocketClient = new OkHttpClient().newBuilder()
                .pingInterval(5000, TimeUnit.MILLISECONDS)//心跳时间
                .build()
                .newWebSocket(request, mWslistener);
    }

    public void doShutDown(){
        if(null != mWebSocketClient){
            mWebSocketClient.cancel();
        }
    }

    /**
     * 订阅消息
     * @param topic
     */
    public void subscribe(String topic){

    }

    /**
     * 取消订阅
     * @param topic
     */
    public void unsubscribe(String topic){

    }

    public void sendMessage(String msg){
        Logger.d("wsmanager","msg:"+msg);
        mWebSocketClient.send(msg);
    }

    /**
     * 重连
     */
    public void reconnect(){
        if(isConnectIsNomarl()) {   //如果网络正常，才进行重连操作
            connect();
        }
    }

    private void updateReConnectInterval() {
        if (reConnectInterval <= MAX_RE_CONNECT_TIME) {
            reConnectInterval = reConnectInterval * 2;  //第一次重连间隔是5秒，之后间隔时间翻倍,重连时间最长不会超过2分钟
        } else {
            reConnectInterval = MIN_RE_CONNECT_TIME;
        }
    }

    /**
     * 重置重连时间
     */
    private void resetReConnectInterval(){
        reConnectInterval = MIN_RE_CONNECT_TIME;
    }

    private void reDoClientConnection() {
        if(mTimer == null){
            mTimer = new Timer();
        }
        if (isConnectIsNomarl() && notReconnecting) {
            notReconnecting = false;
            //只执行一次
            mTimer.schedule(new ReConnectTask(), SECOND_UNIT * reConnectInterval);
        }
    }

    private WebSocketListener mWslistener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            if(mConnectListener != null){
                mConnectListener.onStatusChange(ISocketConnStatusListener.ConnStatus.CONNECTED);
            }
            resetReConnectInterval();
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            if(null != mDataCallback){
                mDataCallback.onResponseDataOk(text);
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            try {
                Logger.d("wsmanager",new String(bytes.toByteArray(),"utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if(null != mDataCallback){
                mDataCallback.onResponseDataOk(bytes.toByteArray());
            }
        }


        public void onClosing(WebSocket webSocket, int code, String reason) {
            if(mConnectListener != null){
                mConnectListener.onStatusChange(ISocketConnStatusListener.ConnStatus.DISCONNECTING);
            }
        }


        public void onClosed(WebSocket webSocket, int code, String reason) {
            if(mConnectListener != null){
                mConnectListener.onStatusChange(ISocketConnStatusListener.ConnStatus.DISCONNECTED);
            }
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
//            Log.d("wsmanager","Throwable:"+t.getMessage());
            if(mConnectListener != null){
                mConnectListener.onStatusChange(ISocketConnStatusListener.ConnStatus.FAILED);
            }
            reDoClientConnection();
        }
    };

    public class ReConnectTask extends TimerTask {
        @Override
        public void run() {
            notReconnecting = true;
            connect();
            updateReConnectInterval();
            Logger.d("wsmanager", "reconnect run :" + (mReconnectTimes++) + "次");
        }
    }

    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(null == connectivityManager) return false;
        try {
            @SuppressLint("MissingPermission") NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                String name = info.getTypeName();
                Logger.d(TAG, "当前网络名称：" + name);
                return true;
            } else {
                Logger.d(TAG, "没有可用网络");
                return false;
            }
        }catch (SecurityException se){
            return false;
        }
    }
}
