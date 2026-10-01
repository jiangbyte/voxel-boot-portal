package io.github.jiangbyte.voxel.boot.portal.cases.profile.admin;

/**
 * 跨上下文管理端资料门面。
 *
 * Author: Charlie
 */
public interface ProfileUserAdminApi {

    /**
     * 按账号 ID 读取资料；不存在返回 null。
     */
    AdminProfileView getByAccountId(String accountId);

    /**
     * 确保账号存在资料行（不存在则创建空白资料）。
     */
    void ensureProfile(String accountId);

    /** 同步资料手机号。 */
    void updatePhone(String accountId, String phone);

    /** 同步资料邮箱。 */
    void updateEmail(String accountId, String email);
}
