package io.github.jiangbyte.voxel.boot.portal.infrastructure.security;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Sa-Token 鉴权拦截：按 Admin/Portal 前缀校验登录，白名单放行。
 *
 * Author: Charlie
 */
@AutoConfiguration
@Configuration
@EnableConfigurationProperties(VoxelSecurityProperties.class)
@RequiredArgsConstructor
public class SaTokenSecurityConfig implements WebMvcConfigurer {

    private final VoxelSecurityProperties securityProperties;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] ignorePatterns = buildIgnorePatterns();
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 1. Admin API：白名单外须管理端登录
            SaRouter.match("/api/*/admin/**")
                    .notMatch(ignorePatterns)
                    .check(r -> StpKit.ADMIN.checkLogin());
            // 2. Portal API：白名单外须门户端登录
            SaRouter.match("/api/*/portal/**")
                    .notMatch(ignorePatterns)
                    .check(r -> StpKit.PORTAL.checkLogin());
        })).addPathPatterns("/**");
    }

    private String[] buildIgnorePatterns() {
        List<String> patterns = new ArrayList<>(securityProperties.getIgnoreUrls());
        patterns.add("/actuator");
        patterns.add("/actuator/**");
        patterns.add("/doc.html");
        patterns.add("/doc.html/**");
        patterns.add("/v3/api-docs");
        patterns.add("/v3/api-docs/**");
        patterns.add("/swagger-ui/**");
        patterns.add("/webjars/**");
        return patterns.toArray(String[]::new);
    }
}
