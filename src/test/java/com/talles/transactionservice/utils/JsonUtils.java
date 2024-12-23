package com.talles.transactionservice.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.talles.transactionservice.utils.date.ZonedDateTimeDeserializer;

import java.io.IOException;
import java.time.ZonedDateTime;

public class JsonUtils {

    public static byte[] toJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.writeValueAsBytes(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer());
        mapper.registerModule(module);
        return mapper.readValue(json, clazz);
    }
}
