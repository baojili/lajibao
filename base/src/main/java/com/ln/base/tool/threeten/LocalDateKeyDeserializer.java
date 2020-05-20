package com.ln.base.tool.threeten;

import com.fasterxml.jackson.databind.DeserializationContext;

import org.threeten.bp.DateTimeException;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;

public class LocalDateKeyDeserializer extends Jsr310KeyDeserializer {

    public static final LocalDateKeyDeserializer INSTANCE = new LocalDateKeyDeserializer();

    public LocalDateKeyDeserializer() {
        // singleton
    }

    @Override
    protected LocalDate deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return LocalDate.parse(key, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, LocalDate.class, e, key);
        }
    }
}
