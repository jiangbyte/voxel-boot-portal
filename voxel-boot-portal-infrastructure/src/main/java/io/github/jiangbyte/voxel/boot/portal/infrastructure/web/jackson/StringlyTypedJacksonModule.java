package io.github.jiangbyte.voxel.boot.portal.infrastructure.web.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * HTTP JSON 线型：数值（含 int/long/float 等）一律序列化为字符串、入站从字符串解析。
 * 业务 0/1 标志只是普通整数取值，无单独线型规则。
 *
 * Author: Charlie
 */
public final class StringlyTypedJacksonModule extends SimpleModule {

    public static final String MODULE_NAME = "voxel-stringly-typed";

    public StringlyTypedJacksonModule() {
        super(MODULE_NAME);
        // 1. 数值出站一律字符串
        addSerializer(Byte.class, ToStringNumberSerializer.INSTANCE);
        addSerializer(Byte.TYPE, ToStringNumberSerializer.INSTANCE);
        addSerializer(Short.class, ToStringNumberSerializer.INSTANCE);
        addSerializer(Short.TYPE, ToStringNumberSerializer.INSTANCE);
        addSerializer(Integer.class, ToStringNumberSerializer.INSTANCE);
        addSerializer(Integer.TYPE, ToStringNumberSerializer.INSTANCE);
        addSerializer(Long.class, ToStringNumberSerializer.INSTANCE);
        addSerializer(Long.TYPE, ToStringNumberSerializer.INSTANCE);
        addSerializer(Float.class, ToStringNumberSerializer.INSTANCE);
        addSerializer(Float.TYPE, ToStringNumberSerializer.INSTANCE);
        addSerializer(Double.class, ToStringNumberSerializer.INSTANCE);
        addSerializer(Double.TYPE, ToStringNumberSerializer.INSTANCE);
        addSerializer(BigInteger.class, ToStringNumberSerializer.INSTANCE);
        addSerializer(BigDecimal.class, ToStringNumberSerializer.INSTANCE);

        // 2. 入站：字符串数字解析为对应数值类型
        addDeserializer(Integer.class, StringlyIntDeserializer.INSTANCE);
        addDeserializer(Integer.TYPE, StringlyIntDeserializer.INSTANCE);
        addDeserializer(Long.class, StringlyLongDeserializer.INSTANCE);
        addDeserializer(Long.TYPE, StringlyLongDeserializer.INSTANCE);
        addDeserializer(Double.class, StringlyDoubleDeserializer.INSTANCE);
        addDeserializer(Double.TYPE, StringlyDoubleDeserializer.INSTANCE);
        addDeserializer(Float.class, StringlyFloatDeserializer.INSTANCE);
        addDeserializer(Float.TYPE, StringlyFloatDeserializer.INSTANCE);
        addDeserializer(BigDecimal.class, StringlyBigDecimalDeserializer.INSTANCE);
    }

    static final class ToStringNumberSerializer extends JsonSerializer<Object> {
        static final ToStringNumberSerializer INSTANCE = new ToStringNumberSerializer();

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }
            gen.writeString(String.valueOf(value));
        }
    }

    static final class StringlyIntDeserializer extends JsonDeserializer<Integer> {
        static final StringlyIntDeserializer INSTANCE = new StringlyIntDeserializer();

        @Override
        public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return parseInt(p.getValueAsString());
        }
    }

    static final class StringlyLongDeserializer extends JsonDeserializer<Long> {
        static final StringlyLongDeserializer INSTANCE = new StringlyLongDeserializer();

        @Override
        public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getValueAsString();
            if (text == null || text.isBlank()) {
                return null;
            }
            return Long.parseLong(text.trim());
        }
    }

    static final class StringlyDoubleDeserializer extends JsonDeserializer<Double> {
        static final StringlyDoubleDeserializer INSTANCE = new StringlyDoubleDeserializer();

        @Override
        public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getValueAsString();
            if (text == null || text.isBlank()) {
                return null;
            }
            return Double.parseDouble(text.trim());
        }
    }

    static final class StringlyFloatDeserializer extends JsonDeserializer<Float> {
        static final StringlyFloatDeserializer INSTANCE = new StringlyFloatDeserializer();

        @Override
        public Float deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getValueAsString();
            if (text == null || text.isBlank()) {
                return null;
            }
            return Float.parseFloat(text.trim());
        }
    }

    static final class StringlyBigDecimalDeserializer extends JsonDeserializer<BigDecimal> {
        static final StringlyBigDecimalDeserializer INSTANCE = new StringlyBigDecimalDeserializer();

        @Override
        public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getValueAsString();
            if (text == null || text.isBlank()) {
                return null;
            }
            return new BigDecimal(text.trim());
        }
    }

    private static Integer parseInt(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return Integer.parseInt(text.trim());
    }
}
