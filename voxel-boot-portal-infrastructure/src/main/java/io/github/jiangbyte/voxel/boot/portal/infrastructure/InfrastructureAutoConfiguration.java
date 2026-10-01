package io.github.jiangbyte.voxel.boot.portal.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.PersistenceAutoConfiguration;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.web.jackson.StringlyTypedJacksonModule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 基础设施自动配置：组件扫描与持久化装配。
 *
 * Author: Charlie
 */
@AutoConfiguration
@Import(PersistenceAutoConfiguration.class)
@ComponentScan(basePackages = {
        "io.github.jiangbyte.voxel.boot.portal.infrastructure",
        "io.github.jiangbyte.voxel.boot.portal.cases"
})
public class InfrastructureAutoConfiguration {

    /**
     * 兜底 ObjectMapper：含数值字符串化与 LocalDateTime 线型。
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new StringlyTypedJacksonModule());
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return mapper;
    }

    /**
     * 供 Web ObjectMapper 安装的线型 Module Bean。
     */
    @Bean
    public StringlyTypedJacksonModule stringlyTypedJacksonModule() {
        return new StringlyTypedJacksonModule();
    }
}
