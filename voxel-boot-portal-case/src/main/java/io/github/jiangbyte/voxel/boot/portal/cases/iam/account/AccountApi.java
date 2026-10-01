package io.github.jiangbyte.voxel.boot.portal.cases.iam.account;

import java.time.LocalDateTime;

/**
 * 跨上下文账号门面：供 Auth 等上下文查询与变更账号。
 *
 * Author: Charlie
 */
public interface AccountApi {

    /** 按登录标识查找账号；不存在返回 null。 */
    AccountInfo findByIdentifier(String identifier, String identityType);

    /** 按 ID 查找；不存在返回 null。 */
    AccountInfo getById(String accountId);

    /** 读取指定类型身份标识；不存在返回 null。 */
    String findIdentifier(String accountId, String identityType);

    /** 校验明文密码是否匹配哈希。 */
    boolean matchesPassword(String rawPassword, String passwordHash);

    /** 编码明文密码。 */
    String encodePassword(String rawPassword);

    /** 读取账号密码哈希；不存在返回 null。 */
    String getPasswordHash(String accountId);

    /** 更新密码哈希。 */
    void updatePasswordHash(String accountId, String encodedPassword);

    /** 新增或更新身份；identifier 为空则移除该类型身份。 */
    void upsertIdentity(String accountId, String identityType, String identifier, boolean loginEnabled);

    /** 更新最近登录元数据。 */
    void updateLoginMeta(String accountId, String ip, LocalDateTime time, String device);

    /** 创建门户账号（密码须已编码）。 */
    AccountInfo createPortalAccount(String account, String email, String encodedPassword);

    /** 注销账号。 */
    void cancel(CancelAccountCommand command);
}
