package io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.port;

/**
 * 密码哈希端口：由基础设施提供 BCrypt 实现。
 *
 * Author: Charlie
 */
public interface PasswordHashingPort {

    /**
     * 对明文密码编码。
     */
    String encode(String rawPassword);

    /**
     * 校验明文与哈希是否匹配。
     */
    boolean matches(String rawPassword, String passwordHash);
}
