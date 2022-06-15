package cn.sunline.basenetworklib2.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import cn.sunline.basenetworklib2.util.EnumIntTypeAdapterFactory;
import cn.sunline.basenetworklib2.util.JsonUtils;
import cn.sunline.basenetworklib2.util.NullStringToEmptyAdapterFactory;
import cn.sunline.basenetworklib2.util.ReflectUtils;
import cn.sunline.uicommonlib.utils.Logger;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * author：admin on 2017/8/16.
 * mail:zengll@hztxt.com.cn
 * function:    在子线程解析json得到泛型对象，同时在主线程中将泛型对象返回给调用者
 */
public abstract class BaseJsonCallback<T> extends BaseCallback {

    private boolean needFormatJson = false;

    private static Gson mGson;

    protected BaseJsonCallback(IResponseDataHandler handler){
        super(handler);
    }

    public BaseJsonCallback(){

    }

    public static void configGsonBuilder(GsonBuilder builder){
        if(null == builder){
            builder = new GsonBuilder();
        }

        mGson =  builder .registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory())
                .registerTypeAdapterFactory(EnumIntTypeAdapterFactory.create())
                .create();
    }

    public BaseJsonCallback<T> setFormatJsonFlag(boolean formatFlag){
        this.needFormatJson = formatFlag;
        return this;
    }

    @Override
    public final void onParseData(String data) {
        Logger.d("BaseJsonCallback",data);
        String formatJson = needFormatJson ? JsonUtils.formatJson(data) : data;
        try {
            final T result = parseJsonResponse(formatJson);

            AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                @Override
                public void call() {
                    if (!isDataValid()){
                        return;
                    }
                    if(result==null){
                        onResponseFail(AppException.json(new Exception("response.data = null")));
                        return;
                    }
                    try {
                        onSuccess(result);
                    }catch (Exception e){
                        Logger.e("BaseJsonCallback",null != e.getMessage() ? e.getMessage(): "null");
                    }
                }
            });
        }catch (final JsonSyntaxException e){
            AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                @Override
                public void call() {
                    if (!isDataValid()){
                        return;
                    }
                    onResponseFail(AppException.json(e));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResponseFail(final Throwable throwable) {
        super.onResponseFail(throwable);
    }

    /**
     * 解析数据，将json转化为对象
     * @param json
     * @return
     */
    private  T parseJsonResponse(String json) throws Exception{
        if(mGson == null){
            throw new Exception("Gson has not init");
        }

        return mGson.fromJson(json, ReflectUtils.getSuperclassTypeParameter(getClass()));
    }


    public abstract void onSuccess(T data);

}
