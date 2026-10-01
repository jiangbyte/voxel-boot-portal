package io.github.jiangbyte.voxel.boot.portal.cases.profile.identity;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.IdentitySnapshotStatus;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.ProfileIdentity;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.repository.ProfileIdentityRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.RealNameCase;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.repository.RealNameCaseRepository;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.port.IdentityCryptoPort;

/**
 * 用例接口。
 *
 * Author: Charlie
 */
public interface IProfileIdentityCase {
    IdentityStatusView getStatusForAccount(String accountId);
    IdentityStatusView getUserStatusForAccount(String accountId);
    void upsertOnApprove(RealNameCase caseEntity);
    void revoke(String accountId, String operatorId);
    PageResult<IdentityPageView> page(String status, String accountId, String documentType, long current, long size);
    Map<String, String> getVerifiedRealNames(Collection<String> accountIds);
    Set<String> findAccountIdsByRealName(String name);
    Integer isVerified(String accountId);
}
