package io.github.jiangbyte.voxel.boot.portal.domain.profile.admin;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.admin.event.AdminProfileUpdated;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.Account;

/**
 * 管理端用户资料聚合：主键为 accountId，维护昵称/头像等展示信息。
 *
 * Author: Charlie
 */
public class AdminProfile extends AggregateRoot<String> {

    private String accountId;
    private String nickname;
    private String avatar;
    private String signature;
    private String phone;
    private String email;
    private String remark;

    private AdminProfile() {
    }

    /**
     * 为账号创建空白资料。
     */
    public static AdminProfile create(String accountId) {
        // 1. 校验账号 ID
        String id = requireAccountId(accountId);
        // 2. 组装空资料聚合
        AdminProfile profile = new AdminProfile();
        profile.accountId = id;
        // 3. 登记更新事件（首次落库视为资料就绪）
        profile.registerEvent(new AdminProfileUpdated(id));
        return profile;
    }

    /**
     * 从持久化状态重建聚合。
     */
    public static AdminProfile reconstitute(String accountId,
                                            String nickname,
                                            String avatar,
                                            String signature,
                                            String phone,
                                            String email,
                                            String remark) {
        AdminProfile profile = new AdminProfile();
        profile.accountId = accountId;
        profile.nickname = nickname;
        profile.avatar = avatar;
        profile.signature = signature;
        profile.phone = phone;
        profile.email = email;
        profile.remark = remark;
        return profile;
    }

    /**
     * 更新展示资料字段。
     * avatar 为 null 表示不改动（表单常省略头像字段）；空串表示清空。
     */
    public void updateProfile(String nickname, String avatar, String signature, String remark) {
        // 1. 覆盖可变展示字段；头像仅在显式传入时更新，避免「只改昵称」误清空
        this.nickname = blankToNull(nickname);
        if (avatar != null) {
            this.avatar = blankToNull(avatar);
        }
        this.signature = blankToNull(signature);
        this.remark = blankToNull(remark);
        // 2. 登记更新事件
        registerEvent(new AdminProfileUpdated(this.accountId));
    }

    /**
     * 更新头像对象名。
     */
    public void updateAvatar(String avatar) {
        this.avatar = blankToNull(avatar);
        registerEvent(new AdminProfileUpdated(this.accountId));
    }

    /**
     * 同步手机号展示字段。
     */
    public void updatePhone(String phone) {
        this.phone = blankToNull(phone);
        registerEvent(new AdminProfileUpdated(this.accountId));
    }

    /**
     * 同步邮箱展示字段。
     */
    public void updateEmail(String email) {
        this.email = blankToNull(email);
        registerEvent(new AdminProfileUpdated(this.accountId));
    }

    private static String requireAccountId(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new DomainException("Account id must not be blank");
        }
        return accountId.trim();
    }

    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text.trim();
    }

    @Override
    public String getId() {
        return accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getSignature() {
        return signature;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getRemark() {
        return remark;
    }
}
