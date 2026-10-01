package io.github.jiangbyte.voxel.boot.portal.domain.profile.portal;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.portal.event.PortalProfileUpdated;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.Account;

/**
 * 门户端用户资料聚合：主键为 accountId。
 *
 * Author: Charlie
 */
public class PortalProfile extends AggregateRoot<String> {

    private String accountId;
    private String nickname;
    private String avatar;
    private String signature;
    private String phone;
    private String email;

    private PortalProfile() {
    }

    public static PortalProfile create(String accountId) {
        String id = requireAccountId(accountId);
        PortalProfile profile = new PortalProfile();
        profile.accountId = id;
        profile.registerEvent(new PortalProfileUpdated(id));
        return profile;
    }

    public static PortalProfile reconstitute(String accountId,
                                             String nickname,
                                             String avatar,
                                             String signature,
                                             String phone,
                                             String email) {
        PortalProfile profile = new PortalProfile();
        profile.accountId = accountId;
        profile.nickname = nickname;
        profile.avatar = avatar;
        profile.signature = signature;
        profile.phone = phone;
        profile.email = email;
        return profile;
    }

    /**
     * 更新展示资料；avatar 为 null 表示不改动，空串表示清空。
     */
    public void updateProfile(String nickname, String avatar, String signature) {
        this.nickname = blankToNull(nickname);
        if (avatar != null) {
            this.avatar = blankToNull(avatar);
        }
        this.signature = blankToNull(signature);
        registerEvent(new PortalProfileUpdated(this.accountId));
    }

    public void updateAvatar(String avatar) {
        this.avatar = blankToNull(avatar);
        registerEvent(new PortalProfileUpdated(this.accountId));
    }

    public void updatePhone(String phone) {
        this.phone = blankToNull(phone);
        registerEvent(new PortalProfileUpdated(this.accountId));
    }

    public void updateEmail(String email) {
        this.email = blankToNull(email);
        registerEvent(new PortalProfileUpdated(this.accountId));
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
    public String getId() { return accountId; }
    public String getAccountId() { return accountId; }
    public String getNickname() { return nickname; }
    public String getAvatar() { return avatar; }
    public String getSignature() { return signature; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
}
