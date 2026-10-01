package io.github.jiangbyte.voxel.boot.portal.cases.profile.portal;

/**
 * 跨上下文门户资料门面。
 *
 * Author: Charlie
 */
public interface ProfileUserPortalApi {

    PortalProfileView getByAccountId(String accountId);

    void ensureProfile(String accountId);

    void updatePhone(String accountId, String phone);

    void updateEmail(String accountId, String email);
}
