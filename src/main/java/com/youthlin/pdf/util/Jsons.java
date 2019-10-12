package com.youthlin.pdf.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author youthlin.chen
 * @date 2019-10-12 15:27
 */
@Slf4j
public class Jsons {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectWriter PRETTY_WRITER = OBJECT_MAPPER.writerWithDefaultPrettyPrinter();

    public static String toJsonPretty(Object data) {
        try {
            return PRETTY_WRITER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.warn("toJsonPretty_error. data={}", data, e);
            return "";
        }
    }

    public static String toJson(Object data) {
        try {
            return OBJECT_MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.warn("toJson_error. data={}", data, e);
            return "";
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (IOException e) {
            log.warn("fromJson_error. type={},json={}", type, json, e);
            return null;
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (IOException e) {
            log.warn("fromJson_error. type={},json={}", type, json, e);
            return null;
        }
    }

}
