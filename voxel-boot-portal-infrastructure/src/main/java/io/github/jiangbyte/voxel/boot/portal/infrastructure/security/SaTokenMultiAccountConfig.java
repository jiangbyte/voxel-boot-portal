package io.github.jiangbyte.voxel.boot.portal.infrastructure.security;

import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 注册 Admin / Portal 多账号 StpLogic Bean，供 @SaCheckPermission(type=...) 解析。
 *
 * Author: Charlie
 */
@Configuration
public class SaTokenMultiAccountConfig {

    @Bean
    @Primary
    public StpLogic stpLogicAdmin() {
        return StpKit.ADMIN;
    }

    @Bean
    public StpLogic stpLogicPortal() {
        return StpKit.PORTAL;
    }
}
