package io.github.jiangbyte.voxel.boot.portal;

import com.fasterxml.jackson.databind.Module;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.web.jackson.LocalDateTimeJacksonModule;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.web.jackson.StringlyTypedJacksonModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.JacksonModule;

/**
 * 门户启动入口。
 *
 * Author: Charlie
 */
@SpringBootApplication(scanBasePackages = "io.github.jiangbyte.voxel.boot.portal")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 注册数值字符串化 Jackson Module（凡数字出入参均为字符串，与是否 0/1 无关）。
     */
    @Bean
    public Module heiStringlyTypedJacksonModule() {
        return new StringlyTypedJacksonModule();
    }

    /**
     * Boot4 / Jackson3：LocalDateTime 使用 spring.jackson.date-format。
     */
    @Bean
    public JacksonModule heiLocalDateTimeJacksonModule(
            @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}") String pattern) {
        return new LocalDateTimeJacksonModule(pattern);
    }
}
