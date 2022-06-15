package cn.sunline.uicommonlib.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;


public class ToastUtil {

    private static String oldMsg;
    protected static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;

    public static void showToast(Context context, String s) {
        if(TextUtils.isEmpty(s)){
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), s, Toast.LENGTH_SHORT);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (s.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    if(!toast.getView().isAttachedToWindow()){
                        toast.show();
                    }
                }
            } else {
                oldMsg = s;
                toast.setText(s);
                if(!toast.getView().isAttachedToWindow()){
                    toast.show();
                }

            }
        }
        oneTime = twoTime;
    }


    public static void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }

}
