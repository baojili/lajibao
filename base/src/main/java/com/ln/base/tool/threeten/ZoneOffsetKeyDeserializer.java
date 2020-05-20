package com.ln.base.tool.threeten;

import com.fasterxml.jackson.databind.DeserializationContext;

import org.threeten.bp.DateTimeException;
import org.threeten.bp.ZoneOffset;

import java.io.IOException;

public class ZoneOffsetKeyDeserializer extends Jsr310KeyDeserializer {

    public static final ZoneOffsetKeyDeserializer INSTANCE = new ZoneOffsetKeyDeserializer();

    private ZoneOffsetKeyDeserializer() {
        // singleton
    }

    @Override
    protected ZoneOffset deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return ZoneOffset.of(key);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, ZoneOffset.class, e, key);
        }
    }
}
