package com.ln.base.tool;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GsonUtils {

    public static final GsonUtils INSTANCE = new GsonUtils();
    private final String DATETIME_WITH_ZONE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";//2019-10-21T09:11:12:345+08:00
    private final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";//2019-10-21T09:11:12:345
    private final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private final DateFormat DATETIME_FORMAT = new SimpleDateFormat(DATETIME_FORMAT_PATTERN, Locale.getDefault());
    private final DateTimeFormatter DATETIME_WITH_ZONE_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_WITH_ZONE_FORMAT_PATTERN);
    private final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN);
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
    private Gson gson = null;

    public Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    //.excludeFieldsWithoutExposeAnnotation()
                    //.setPrettyPrinting()//格式化输出。设置后，gson序列号后的字符串为一个格式化的字符串
                    .setDateFormat(DATETIME_FORMAT_PATTERN)
                    .addDeserializationExclusionStrategy(new DeserializeExclutionStrategy())
                    .addSerializationExclusionStrategy(new SerializeExclutionStrategy())
                    .registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter())
                    .registerTypeAdapter(java.sql.Date.class, new SQLDateTypeAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                    .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                    .registerTypeAdapter(ZoneOffset.class, new ZoneOffsetTypeAdapter())
                    .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
                    .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeTypeAdapter())
                    .create();
        }
        return gson;
    }

    public <T> T toEntity(JsonElement json, Class<T> classOfT) {
        JsonInterfaceCheck.assetType(classOfT);
        try {
            return getGson().fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T toEntity(String json, Class<T> classOfT) {
        JsonInterfaceCheck.assetType(classOfT);
        try {
            return getGson().fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T toEntity(String json, Type type) {
        JsonInterfaceCheck.assetType(type);
        try {
            return getGson().fromJson(json, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toJson(Object src) {
        try {
            return getGson().toJson(src);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class DeserializeExclutionStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {//返回值决定要不要排除该字段，return true为排除
            //if ("finalField".equals(f.getName())) return true; //根据字段名排除
            Expose expose = f.getAnnotation(Expose.class); //获取Expose注解
            return expose != null && expose.deserialize() == false; //根据Expose注解排除
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {//直接排除某个类 ，return true为排除
            return false;
        }
    }

    private class SerializeExclutionStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {//返回值决定要不要排除该字段，return true为排除
            //if ("finalField".equals(f.getName())) return true; //根据字段名排除
            Expose expose = f.getAnnotation(Expose.class); //获取Expose注解
            return expose != null && expose.serialize() == false; //根据Expose注解排除
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {//直接排除某个类 ，return true为排除
            return false;
        }
    }

    private class TimestampTypeAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {

        @Override
        public JsonElement serialize(Timestamp src, Type arg1, JsonSerializationContext arg2) {
            String dateFormatAsString = DATETIME_FORMAT.format(new Date(src.getTime()));
            return new JsonPrimitive(dateFormatAsString);
        }

        @Override
        public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!(json instanceof JsonPrimitive)) {
                throw new JsonParseException("The date should be a string value");
            }
            try {
                Date date = DATETIME_FORMAT.parse(json.getAsString());
                return new Timestamp(date.getTime());
            } catch (ParseException e) {
                throw new JsonParseException(e);
            }
        }
    }

    private class SQLDateTypeAdapter implements JsonSerializer<java.sql.Date> {

        @Override
        public JsonElement serialize(java.sql.Date src, Type arg1, JsonSerializationContext arg2) {
            String dateFormatAsString = DATETIME_FORMAT.format(new java.sql.Date(src.getTime()));
            return new JsonPrimitive(dateFormatAsString);
        }
    }

    private class LocalDateTimeTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(DATETIME_FORMATTER));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String datetime = json.getAsJsonPrimitive().getAsString();
            return LocalDateTime.parse(datetime, DATETIME_FORMATTER);
        }
    }

    private class LocalDateTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(DATE_FORMATTER));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String datetime = json.getAsJsonPrimitive().getAsString();
            return LocalDate.parse(datetime, DATE_FORMATTER);
        }
    }

    private class ZoneOffsetTypeAdapter implements JsonSerializer<ZoneOffset>, JsonDeserializer<ZoneOffset> {
        @Override
        public JsonElement serialize(ZoneOffset src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getId());
        }

        @Override
        public ZoneOffset deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String zoneOffset = json.getAsJsonPrimitive().getAsString();
            return ZoneOffset.of(zoneOffset);
        }
    }

    private class ZonedDateTimeTypeAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
        @Override
        public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(DATETIME_WITH_ZONE_FORMATTER));
        }

        @Override
        public ZonedDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String datetime = json.getAsJsonPrimitive().getAsString();
            return ZonedDateTime.parse(datetime, DATETIME_WITH_ZONE_FORMATTER);
        }
    }

    private class OffsetDateTimeTypeAdapter implements JsonSerializer<OffsetDateTime>, JsonDeserializer<OffsetDateTime> {
        @Override
        public JsonElement serialize(OffsetDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(DATETIME_WITH_ZONE_FORMATTER));
        }

        @Override
        public OffsetDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String datetime = json.getAsJsonPrimitive().getAsString();
            return OffsetDateTime.parse(datetime, DATETIME_WITH_ZONE_FORMATTER);
        }
    }
}