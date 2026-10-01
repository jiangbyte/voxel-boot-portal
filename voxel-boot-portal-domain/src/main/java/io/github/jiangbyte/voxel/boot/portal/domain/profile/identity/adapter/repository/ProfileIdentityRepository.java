package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.ProfileIdentity;

/**
 * 实名快照仓储端口。
 *
 * Author: Charlie
 */
public interface ProfileIdentityRepository extends Repository<ProfileIdentity, String> {

    /** 按证件哈希查找已认证快照（排除指定账号）。 */
    Optional<ProfileIdentity> findVerifiedByDocumentHash(String documentNoHash, String excludeAccountId);

    /** 分页查询。 */
    List<ProfileIdentity> page(String status, String accountId, String documentType, long offset, long limit);

    /** 分页总数。 */
    long count(String status, String accountId, String documentType);

    /** 批量按 ID 加载。 */
    List<ProfileIdentity> findByIds(Collection<String> accountIds);

    /** 全部已认证快照（姓名检索用，数据量可控场景）。 */
    List<ProfileIdentity> listVerified();
}
