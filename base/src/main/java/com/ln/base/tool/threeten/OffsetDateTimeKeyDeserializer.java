package com.ln.base.tool.threeten;

import com.fasterxml.jackson.databind.DeserializationContext;

import org.threeten.bp.DateTimeException;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;

public class OffsetDateTimeKeyDeserializer extends Jsr310KeyDeserializer {

    public static final OffsetDateTimeKeyDeserializer INSTANCE = new OffsetDateTimeKeyDeserializer();

    private OffsetDateTimeKeyDeserializer() {
        // singleton
    }

    @Override
    protected OffsetDateTime deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return OffsetDateTime.parse(key, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, OffsetDateTime.class, e, key);
        }
    }
}
