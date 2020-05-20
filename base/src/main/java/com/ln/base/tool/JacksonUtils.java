package com.ln.base.tool;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ln.base.tool.threeten.InstantDeserializer;
import com.ln.base.tool.threeten.JSR310StringParsableDeserializer;
import com.ln.base.tool.threeten.LocalDateDeserializer;
import com.ln.base.tool.threeten.LocalDateKeyDeserializer;
import com.ln.base.tool.threeten.LocalDateSerializer;
import com.ln.base.tool.threeten.LocalDateTimeDeserializer;
import com.ln.base.tool.threeten.LocalDateTimeKeyDeserializer;
import com.ln.base.tool.threeten.LocalDateTimeSerializer;
import com.ln.base.tool.threeten.OffsetDateTimeKeyDeserializer;
import com.ln.base.tool.threeten.OffsetDateTimeSerializer;
import com.ln.base.tool.threeten.ZoneOffsetKeyDeserializer;
import com.ln.base.tool.threeten.ZonedDateTimeKeyDeserializer;
import com.ln.base.tool.threeten.ZonedDateTimeKeySerializer;
import com.ln.base.tool.threeten.ZonedDateTimeSerializer;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class JacksonUtils {

    //private    final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ";//2019-10-21T09:11:12:345+08:00

    public static final JacksonUtils INSTANCE = new JacksonUtils();
    private final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";//2019-10-21T09:11:12:345
    private final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private final DateFormat DATETIME_FORMAT = new SimpleDateFormat(DATETIME_FORMAT_PATTERN, Locale.getDefault());
    private final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN);
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
    private ObjectMapper objectMapper = null;

    public ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(DATETIME_FORMAT);
            objectMapper.setTimeZone(TimeZone.getTimeZone("UTC"));
//            objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            // JsonInclude.Include.ALWAYS 默认
            // JsonInclude.Include.NON_DEFAULT 属性为默认值不序列化
            // JsonInclude.Include.NON_EMPTY 属性为 空（””） 或者为 NULL 都不序列化
            // JsonInclude.Include.NON_NULL 属性为NULL 不序列化, 实体转json时，只对VO起作用，Map List不起作用
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            // 忽略在JSON字符串中存在，而在Java实体中不存在的属性
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            /*objectMapper.registerModule(new JavaTimeModule())
                    .registerModule(new ParameterNamesModule())
                    .registerModule(new Jdk8Module());*/
            //objectMapper.findAndRegisterModules();
            addModule(objectMapper);
        }
        return objectMapper;
    }

    public <T> T toEntity(@Nullable String json, @Nullable JavaType javaType) {
        if (StringUtils.isEmpty(json) || javaType == null) {
            return null;
        }
        try {
            return getObjectMapper().readValue(json, javaType);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
        return null;
    }

    public <T> T toEntity(@Nullable String json, @Nullable Class<T> classOfT) {
        if (StringUtils.isEmpty(json) || classOfT == null) {
            return null;
        }
        try {
            return getObjectMapper().readValue(json, classOfT);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
        return null;
    }

    public String toJson(@Nullable Object src) {
        if (null == src) {
            return null;
        }
        try {
            return getObjectMapper().writeValueAsString(src);
        } catch (JsonProcessingException e) {
            Log.e(e.getMessage(), e);
        }
        return null;
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
    public <T> List<T> toList(@Nullable String json, @Nullable Class<? extends List> collectionClass, @Nullable Class<T> elementClass)
            throws IOException {
        if (StringUtils.isEmpty(json) || null == collectionClass || null == elementClass) {
            return null;
        }
        JavaType javaType = getObjectMapper().getTypeFactory().constructCollectionType(collectionClass, elementClass);
        return getObjectMapper().readValue(json, javaType);
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
    public <K, V> Map<K, V> toMap(@Nullable String json, @Nullable Class<? extends Map> mapClass, @Nullable Class<K> keyClass,
                                  Class<V> valueClass) throws IOException {
        if (StringUtils.isEmpty(json) || null == mapClass || null == keyClass) {
            return null;
        }
        JavaType javaType = getObjectMapper().getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
        return getObjectMapper().readValue(json, javaType);
    }

    /**
     * 将POJO转换为json字符串
     *
     * @param src 需要格式化对象
     * @return 格式化后的带换行的json串
     */
    public String toJsonViewStr(@Nullable Object src) {
        if (null == src) {
            return "Empty/Null json object";
        }
        try {
            return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(src);
        } catch (JsonGenerationException e) {
            Log.e(e.getMessage(), e);
        } catch (JsonMappingException e) {
            Log.e(e.getMessage(), e);
        } catch (IOException e) {
            Log.e(e.getMessage(), e);
        }
        return "Invalid Json";
    }

    private void addModule(ObjectMapper objectMapper) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        module.addSerializer(LocalDate.class, new LocalDateSerializer());
        module.addSerializer(ZoneOffset.class, new ToStringSerializer(ZoneOffset.class));
        module.addSerializer(ZonedDateTime.class, ZonedDateTimeSerializer.INSTANCE);
        module.addSerializer(OffsetDateTime.class, OffsetDateTimeSerializer.INSTANCE);

        module.addKeySerializer(ZonedDateTime.class, ZonedDateTimeKeySerializer.INSTANCE);

        module.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        module.addDeserializer(ZoneOffset.class, JSR310StringParsableDeserializer.ZONE_OFFSET);
        module.addDeserializer(ZonedDateTime.class, InstantDeserializer.ZONED_DATE_TIME);
        module.addDeserializer(OffsetDateTime.class, InstantDeserializer.OFFSET_DATE_TIME);

        module.addKeyDeserializer(LocalDate.class, new LocalDateKeyDeserializer());
        module.addKeyDeserializer(LocalDateTime.class, new LocalDateTimeKeyDeserializer());
        module.addKeyDeserializer(ZoneOffset.class, ZoneOffsetKeyDeserializer.INSTANCE);
        module.addKeyDeserializer(ZonedDateTime.class, ZonedDateTimeKeyDeserializer.INSTANCE);
        module.addKeyDeserializer(OffsetDateTime.class, OffsetDateTimeKeyDeserializer.INSTANCE);

        objectMapper.registerModule(module);
    }
}