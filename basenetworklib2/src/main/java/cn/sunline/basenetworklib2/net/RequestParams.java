package cn.sunline.basenetworklib2.net;

import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.sunline.basenetworklib2.util.EnumIntTypeAdapterFactory;
import cn.sunline.basenetworklib2.util.NullStringToEmptyAdapterFactory;
import cn.sunline.basenetworklib2.util.URLUtils;

/**
 * author：admin on 2017/8/16.
 * mail:zengll@hztxt.com.cn
 * function:
 */
public class RequestParams {
    protected final Map<String,Object> urlParams;
    protected  final Map<String, Pair<String, File>> mediaTypeFilePairParams;

    private static Gson mGson;

    public RequestParams(){
        this.urlParams = new HashMap<>();
        this.mediaTypeFilePairParams = new HashMap<>();
    }

    public static void configGsonBuilder(GsonBuilder builder){
        if(null == builder){
            builder = new GsonBuilder();
        }

        mGson =  builder .registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory())
                .registerTypeAdapterFactory(EnumIntTypeAdapterFactory.create())
                .create();
    }

    public void put(String key, Object value){
        this.urlParams.put(key,value);
    }

    public void put(String key, Pair<String, File> mediaTypeFilePair){
        this.mediaTypeFilePairParams.put(key,mediaTypeFilePair);
    }

    public String getParamString() {
        return URLUtils.getUrlParamString(this.urlParams);
    }

    public Map<String,Object> getUrlParams(){
        return this.urlParams;
    }


    public String convertUrlParamsToJson(){
//        JsonObject object = new JsonObject();
//
//        for (String paramKey: urlParams.keySet()){
//            String paramValue = urlParams.get(paramKey);
//            object.addProperty(paramKey, paramValue);
//        }

        if(mGson == null){
            mGson = new Gson();
        }

        return mGson.toJson(urlParams);
    }
}
