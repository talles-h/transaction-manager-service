package com.talles.transactionservice.utils.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.talles.transactionservice.exception.DateTimeFormatException;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> implements ContextualDeserializer {

    private String fieldName;

    @Override
    public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String dateStr = jsonParser.getText();

        // Check if the string contains a time zone (either 'Z' or an offset)
        if (!dateStr.endsWith("Z")) {
            // Throw a custom exception if no time zone is provided
            throw new DateTimeFormatException(fieldName, "Time zone information is missing. It must be Z (UTC time)");
        }

        return ZonedDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            fieldName = beanProperty.getName(); // Access the field name
        } else {
            fieldName = "Unknown field"; // Default value if no field name is found
        }

        return this;
    }
}
