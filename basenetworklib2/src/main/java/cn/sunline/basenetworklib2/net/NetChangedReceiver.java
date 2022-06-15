package cn.sunline.basenetworklib2.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Observable;
import java.util.Observer;


public class NetChangedReceiver extends BroadcastReceiver {

    /**
     * 没有连接网络
     */
    public static final int NETWORK_NONE = -1;
    /**
     * 移动网络
     */
    public static final int NETWORK_MOBILE = 0;
    /**
     * 无线网络
     */
    public static final int NETWORK_WIFI = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // 当前接受到的广播的标识(意图)为网络状态的标识时做相应判断
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int mode = getNetWorkState(context);
            /**
             * 通知
             */
            mObservable.setChanged();
            mObservable.notifyObservers(mode);
        }
    }
    public static int getNetWorkState(Context context) {
        // 得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }
    /**
     * 观察者
     */
    private static NetworkChangedObserver mObservable = new NetworkChangedObserver();

    public static void addObserver(Observer observer) {
        mObservable.addObserver(observer);
    }

    public static void deleteObserver(Observer observer) {
        mObservable.deleteObserver(observer);
    }

    static class NetworkChangedObserver extends Observable {

        @Override
        public synchronized void setChanged() {
            super.setChanged();
        }
    }
}
