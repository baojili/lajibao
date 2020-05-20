package com.ln.base.tool.threeten;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import org.threeten.bp.DateTimeException;

import java.io.IOException;
import java.util.Arrays;

/**
 * Base class that indicates that all JSR310 datatypes are deserialized from scalar JSON types.
 *
 * @author Nick Williams
 * @since 2.2
 */
abstract class JSR310DeserializerBase<T> extends StdScalarDeserializer<T> {
    private static final long serialVersionUID = 1L;

    protected JSR310DeserializerBase(Class<T> supportedType) {
        super(supportedType);
    }

    @Override
    public Object deserializeWithType(JsonParser parser, DeserializationContext context,
                                      TypeDeserializer typeDeserializer)
            throws IOException {
        return typeDeserializer.deserializeTypedFromAny(parser, context);
    }

    protected <BOGUS> BOGUS _reportWrongToken(DeserializationContext context,
                                              JsonToken exp, String unit) throws IOException {
        context.reportWrongTokenException(this, exp,
                "Expected %s for '%s' of %s value",
                exp.name(), unit, handledType().getName());
        return null;
    }

    protected <BOGUS> BOGUS _reportWrongToken(JsonParser parser, DeserializationContext context,
                                              JsonToken... expTypes) throws IOException {
        // 20-Apr-2016, tatu: No multiple-expected-types handler yet, construct message
        //    here
        return context.reportInputMismatch(handledType(),
                "Unexpected token (%s), expected one of %s for %s value",
                parser.getCurrentToken(),
                Arrays.asList(expTypes).toString(),
                handledType().getName());
    }

    @SuppressWarnings("unchecked")
    protected <R> R _handleDateTimeException(DeserializationContext context,
                                             DateTimeException e0, String value) throws JsonMappingException {
        try {
            return (R) context.handleWeirdStringValue(handledType(), value,
                    "Failed to deserialize %s: (%s) %s",
                    handledType().getName(), e0.getClass().getName(), e0.getMessage());

        } catch (JsonMappingException e) {
            e.initCause(e0);
            throw e;
        } catch (IOException e) {
            if (null == e.getCause()) {
                e.initCause(e0);
            }
            throw JsonMappingException.fromUnexpectedIOE(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected <R> R _handleUnexpectedToken(DeserializationContext context,
                                           JsonParser parser, String message, Object... args) throws JsonMappingException {
        try {
            return (R) context.handleUnexpectedToken(handledType(), parser.getCurrentToken(),
                    parser, message, args);

        } catch (JsonMappingException e) {
            throw e;
        } catch (IOException e) {
            throw JsonMappingException.fromUnexpectedIOE(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected <R> R _handleUnexpectedToken(DeserializationContext context,
                                           JsonParser parser, JsonToken... expTypes) throws JsonMappingException {
        return _handleUnexpectedToken(context, parser,
                "Unexpected token (%s), expected one of %s for %s value",
                parser.currentToken(),
                Arrays.asList(expTypes),
                handledType().getName());
    }

    /**
     * Helper method used to peel off spurious wrappings of DateTimeException
     *
     * @param e DateTimeException to peel
     * @return DateTimeException that does not have another DateTimeException as its cause.
     */
    protected DateTimeException _peelDTE(DateTimeException e) {
        while (true) {
            Throwable t = e.getCause();
            if (t != null && t instanceof DateTimeException) {
                e = (DateTimeException) t;
                continue;
            }
            break;
        }
        return e;
    }
}

