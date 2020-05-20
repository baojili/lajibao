package com.ln.base.tool.threeten;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class OffsetDateTimeSerializer extends InstantSerializerBase<OffsetDateTime> {
    public static final OffsetDateTimeSerializer INSTANCE = new OffsetDateTimeSerializer();
    private static final long serialVersionUID = 1L;

    protected OffsetDateTimeSerializer() {
        super(OffsetDateTime.class, dt -> dt.toInstant().toEpochMilli(),
                OffsetDateTime::toEpochSecond, OffsetDateTime::getNano,
                DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    protected OffsetDateTimeSerializer(OffsetDateTimeSerializer base,
                                       Boolean useTimestamp, DateTimeFormatter formatter) {
        this(base, useTimestamp, null, formatter);
    }

    protected OffsetDateTimeSerializer(OffsetDateTimeSerializer base,
                                       Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter) {
        super(base, useTimestamp, useNanoseconds, formatter);
    }

    @Override
    protected JSR310FormattedSerializerBase<?> withFormat(Boolean useTimestamp,
                                                          DateTimeFormatter formatter, JsonFormat.Shape shape) {
        return new OffsetDateTimeSerializer(this, useTimestamp, formatter);
    }

    @Override
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new OffsetDateTimeSerializer(this, _useTimestamp, writeNanoseconds, _formatter);
    }
}
