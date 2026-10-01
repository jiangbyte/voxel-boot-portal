package io.github.jiangbyte.voxel.boot.portal.infrastructure.security;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.port.PasswordHashingPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码编码配置与 BCrypt 哈希端口实现。
 *
 * Author: Charlie
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * 提供 BCrypt PasswordEncoder Bean。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 应用层密码哈希端口的 BCrypt 实现。
     */
    @Component
    public static class BCryptPasswordHashingPort implements PasswordHashingPort {

        private final PasswordEncoder passwordEncoder;

        public BCryptPasswordHashingPort(PasswordEncoder passwordEncoder) {
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public String encode(String rawPassword) {
            return passwordEncoder.encode(rawPassword);
        }

        @Override
        public boolean matches(String rawPassword, String passwordHash) {
            if (rawPassword == null || passwordHash == null || passwordHash.isBlank()) {
                return false;
            }
            // cancelled: 前缀为注销作废口令，直接判定不匹配
            if (passwordHash.startsWith("cancelled:")) {
                return false;
            }
            return passwordEncoder.matches(rawPassword, passwordHash);
        }
    }
}
