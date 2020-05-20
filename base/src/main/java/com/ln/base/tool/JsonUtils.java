package com.ln.base.tool;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.JsonMappingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    /**
     * It is used for json view
     */
    private static final int JSON_INDENT = 2;


    public static JacksonUtils jackson() {
        return JacksonUtils.INSTANCE;
    }

    public static GsonUtils gson() {
        return GsonUtils.INSTANCE;
    }

    public static <T> T toEntity(@Nullable String json, @Nullable Type javaType) {
        return gson().toEntity(json, javaType);
    }

    public static <T> T toEntity(@Nullable String json, @Nullable Class<T> classOfT) {
        return gson().toEntity(json, classOfT);
    }

    public static String toJson(@Nullable Object src) {
        return gson().toJson(src);
    }

    /**
     * 将json转化成List
     *
     * @param json
     * @param collectionClass
     * @param elementClass
     * @return
     * @throws com.fasterxml.jackson.core.JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    public static <T> List<T> toList(@Nullable String json, @Nullable Class<? extends List> collectionClass, @Nullable Class<T> elementClass)
            throws IOException {
        return jackson().toList(json, collectionClass, elementClass);
    }

    /**
     * 将json转化成Map
     *
     * @param json
     * @param mapClass
     * @param keyClass
     * @param valueClass
     * @return
     * @throws com.fasterxml.jackson.core.JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    public static <K, V> Map<K, V> toMap(@Nullable String json, @Nullable Class<? extends Map> mapClass, @Nullable Class<K> keyClass,
                                         Class<V> valueClass) throws IOException {
        return jackson().toMap(json, mapClass, keyClass, valueClass);
    }

    /**
     * 将POJO转换为json字符串
     *
     * @param src 需要格式化对象
     * @return 格式化后的带换行的json串
     */
    public static String toJsonViewStr(@Nullable Object src) {
        return jackson().toJsonViewStr(src);
    }

    /**
     * 格式化json字符串
     *
     * @param rawJsonStr 需要格式化的json串
     * @return 格式化后的带换行的json串
     */
    public static String toJsonViewStr(@Nullable String rawJsonStr) {
        if (StringUtils.isEmpty(rawJsonStr)) {
            return "Empty/Null json content";
        }
        try {
            rawJsonStr = rawJsonStr.trim();
            if (rawJsonStr.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(rawJsonStr);
                String message = jsonObject.toString(JSON_INDENT);
                return message;
            }
            if (rawJsonStr.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(rawJsonStr);
                String message = jsonArray.toString(JSON_INDENT);
                return message;
            }
            return "Invalid Json";
        } catch (JSONException e) {
            return "Invalid Json";
        }
    }

    public static Class<?> getRawType(@Nullable Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (!(rawType instanceof Class)) {
                throw new IllegalArgumentException();
            } else {
                return (Class) rawType;
            }
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();
        } else if (type instanceof TypeVariable) {
            return Object.class;
        } else if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);
        } else {
            String className = type == null ? "null" : type.getClass().getName();
            throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + className);
        }
    }
}