package cn.sunline.basenetworklib2.util;

/**
 * Description:
 * Created by zenglulin@youxiang.com
 * <p>
 * Date: 2022/4/24
 */

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class EnumIntTypeAdapterFactory implements TypeAdapterFactory {

    public static EnumIntTypeAdapterFactory create() {
        return new EnumIntTypeAdapterFactory();
    }

    private EnumIntTypeAdapterFactory() {
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if (rawType == null || rawType == Enum.class) {
            return null;
        }
        if (!Enum.class.isAssignableFrom(rawType)) {
            return null;
        }
        if (!rawType.isEnum()) {
            return null;
        }

        if (rawType.getAnnotation(IgnoreEnumIntConvert.class) != null) {
            return null;
        }
        return (TypeAdapter<T>) new EnumTypeAdapter(rawType);
    }



    final class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
        private final Map<Integer, T> nameToConstant = new HashMap<>();
        private final Map<T, Integer> constantToName = new HashMap<>();

        public EnumTypeAdapter(Class<T> classOfT) {
            T[] enumConstants = classOfT.getEnumConstants();
            if (enumConstants == null) {
                throw new NullPointerException(classOfT.getName() + ".getEnumConstants() == null");
            }
            for (T constant : enumConstants) {
                String name = constant.name();
                Field field;
                try {
                    field = classOfT.getField(name);
                } catch (NoSuchFieldException e) {
                    throw new AssertionError(e);
                }
                SerializedName annotation = field.getAnnotation(SerializedName.class);
                if (annotation == null) {
                    throw new IllegalArgumentException("Enum class Field must Annotation with SerializedName：" + classOfT.getName() + "." + name);
                }
                String value = annotation.value();
                Integer intValue;
                try {
                    intValue = Integer.valueOf(value);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Enum class Field must Annotation with SerializedName And value is int type current is："
                            + value + "\n\t\tin " + classOfT.getName() + "." + name, e);
                }
                T previous = nameToConstant.put(intValue, constant);
                if (previous != null) {
                    throw new IllegalArgumentException("Enum class fields are repeatedly identified by the serializedName annotation：" +
                            "\n\t\tserializedName = " + intValue + " And two enum are" +
                            "\n\t\t1." + classOfT.getName() + "." + previous.name() +
                            "\n\t\t2." + classOfT.getName() + "." + constant.name());
                }
                constantToName.put(constant, intValue);
            }
        }

        @Override
        public T read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return nameToConstant.get(in.nextInt());
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            out.value(value == null ? null : constantToName.get(value));
        }
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface IgnoreEnumIntConvert {
    }
}

