package com.ln.base.tool.threeten;

import com.fasterxml.jackson.databind.DeserializationContext;

import org.threeten.bp.DateTimeException;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;

public class LocalDateTimeKeyDeserializer extends Jsr310KeyDeserializer {

    public static final LocalDateTimeKeyDeserializer INSTANCE = new LocalDateTimeKeyDeserializer();

    public LocalDateTimeKeyDeserializer() {
        // singleton
    }

    @Override
    protected LocalDateTime deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return LocalDateTime.parse(key, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, LocalDateTime.class, e, key);
        }
    }

}
