package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 持久化自动配置：扫描 MyBatis Mapper，注册分页插件。
 *
 * Author: Charlie
 */
@Configuration
@MapperScan("io.github.jiangbyte.voxel.boot.portal.infrastructure.dao")
public class PersistenceAutoConfiguration {

    /**
     * MyBatis-Plus 分页拦截器（默认按 MySQL 方言，可由多数据源环境再扩展）。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
