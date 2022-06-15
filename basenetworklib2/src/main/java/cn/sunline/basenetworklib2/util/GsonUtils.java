package cn.sunline.basenetworklib2.util;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import cn.sunline.uicommonlib.utils.FileUtil;


public class GsonUtils {
    public static <T> void writerJsonToFile(File file, Gson gson, Type typeofT, T t)throws Exception {
        FileUtil.createFileAndDirIfNotExist(file);

        OutputStream out = new FileOutputStream(file);
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));//设计编码
        gson.toJson(t, typeofT, writer);
        writer.flush();
        writer.close();

    }

    public static <T> T readJsonFromFile(File file, Gson gson, Type typeofT)throws Exception {
        InputStream input = new FileInputStream(file);
        JsonReader reader = new JsonReader(new InputStreamReader(input));
        T t = gson.fromJson(reader, typeofT);
        reader.close();
        return t;
    }

    public static Map<String, Object> getGsonSerializedNameValues(Object obj){
        Field[] declaredFields = obj.getClass().getDeclaredFields();

        Map<String, Object > map = new HashMap<>();

        for (Field field: declaredFields) {
            field.setAccessible(true);
//            System.out.println(field.toString());
            SerializedName annotation = field.getAnnotation(SerializedName.class);
            if (annotation != null) {
                try {
                    Object fieldObj = field.get(obj);

                    map.put(annotation.value(), fieldObj);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return map;

    }

}
