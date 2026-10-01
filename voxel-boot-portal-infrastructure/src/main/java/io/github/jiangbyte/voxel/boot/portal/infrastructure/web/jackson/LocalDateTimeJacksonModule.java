package io.github.jiangbyte.voxel.boot.portal.infrastructure.web.jackson;

import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime 线型：yyyy-MM-dd HH:mm:ss（国内单时区，配合 spring.jackson.date-format）。
 *
 * Author: Charlie
 */
public final class LocalDateTimeJacksonModule extends SimpleModule {

    public static final String MODULE_NAME = "voxel-local-date-time";
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public LocalDateTimeJacksonModule() {
        this(DEFAULT_PATTERN);
    }

    public LocalDateTimeJacksonModule(String pattern) {
        super(MODULE_NAME);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                pattern == null || pattern.isBlank() ? DEFAULT_PATTERN : pattern);
        addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
    }
}
