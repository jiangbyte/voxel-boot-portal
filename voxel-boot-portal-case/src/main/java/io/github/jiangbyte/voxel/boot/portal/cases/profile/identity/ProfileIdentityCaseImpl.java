package io.github.jiangbyte.voxel.boot.portal.cases.profile.identity;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.IdentitySnapshotStatus;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.ProfileIdentity;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.repository.ProfileIdentityRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.RealNameCase;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.repository.RealNameCaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.port.IdentityCryptoPort;
import lombok.RequiredArgsConstructor;

/**
 * 实名快照用例：状态查询、审核通过写入、撤销与分页。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class ProfileIdentityCaseImpl implements IProfileIdentityCase {

    private final ProfileIdentityRepository profileIdentityRepository;
    private final RealNameCaseRepository realNameCaseRepository;
    private final IdentityCryptoPort identityCryptoPort;


    /**
     * 管理侧状态（含通道信息）。
     */
    @Transactional(readOnly = true)
    public IdentityStatusView getStatusForAccount(String accountId) {
        return toStatusView(accountId, false);
    }

    /**
     * 用户侧状态（脱敏通道/provider）。
     */
    @Transactional(readOnly = true)
    public IdentityStatusView getUserStatusForAccount(String accountId) {
        return toStatusView(accountId, true);
    }

    /**
     * 审核通过后 upsert 实名快照。
     */
    @Transactional
    public void upsertOnApprove(RealNameCase caseEntity) {
        // 1. 加载已有快照
        ProfileIdentity existing = profileIdentityRepository.findById(caseEntity.getAccountId()).orElse(null);
        // 2. 新建或覆盖为 VERIFIED
        if (existing == null) {
            ProfileIdentity created = ProfileIdentity.verifyFromCase(
                    caseEntity.getAccountId(),
                    caseEntity.getDocumentType(),
                    caseEntity.getRealNameCipher(),
                    caseEntity.getDocumentNoCipher(),
                    caseEntity.getDocumentNoHash(),
                    caseEntity.getVerifyChannel(),
                    caseEntity.getProvider(),
                    caseEntity.getProviderOrderNo(),
                    caseEntity.getCaseId());
            profileIdentityRepository.save(created);
            return;
        }
        existing.applyApprovedCase(
                caseEntity.getDocumentType(),
                caseEntity.getRealNameCipher(),
                caseEntity.getDocumentNoCipher(),
                caseEntity.getDocumentNoHash(),
                caseEntity.getVerifyChannel(),
                caseEntity.getProvider(),
                caseEntity.getProviderOrderNo(),
                caseEntity.getCaseId());
        profileIdentityRepository.save(existing);
    }

    /**
     * 撤销已认证快照。
     */
    @Transactional
    public void revoke(String accountId, String operatorId) {
        ProfileIdentity identity = profileIdentityRepository.findById(accountId)
                .orElseThrow(() -> new DomainException(404, "Verified identity not found"));
        identity.revoke(operatorId);
        profileIdentityRepository.save(identity);
    }

    /**
     * 快照分页。
     */
    @Transactional(readOnly = true)
    public PageResult<IdentityPageView> page(String status, String accountId, String documentType, long current, long size) {
        long page = Math.max(current, 1);
        long pageSize = Math.min(Math.max(size, 1), 100);
        long offset = (page - 1) * pageSize;
        long total = profileIdentityRepository.count(status, accountId, documentType);
        List<IdentityPageView> records = profileIdentityRepository.page(status, accountId, documentType, offset, pageSize)
                .stream()
                .map(this::toPageView)
                .toList();
        return new PageResult<>(records, total, page, pageSize);
    }

    @Transactional(readOnly = true)
    public Map<String, String> getVerifiedRealNames(Collection<String> accountIds) {
        Map<String, String> map = new HashMap<>();
        if (accountIds == null || accountIds.isEmpty()) {
            return map;
        }
        for (ProfileIdentity identity : profileIdentityRepository.findByIds(accountIds)) {
            if (identity.getStatus() != IdentitySnapshotStatus.VERIFIED) {
                continue;
            }
            if (identity.getRealNameCipher() == null || identity.getRealNameCipher().isBlank()) {
                continue;
            }
            map.put(identity.getAccountId(), identityCryptoPort.decrypt(identity.getRealNameCipher()));
        }
        return map;
    }

    @Transactional(readOnly = true)
    public Set<String> findAccountIdsByRealName(String name) {
        Set<String> matched = new HashSet<>();
        if (name == null || name.isBlank()) {
            return matched;
        }
        String keyword = name.trim().toLowerCase(Locale.ROOT);
        for (ProfileIdentity identity : profileIdentityRepository.listVerified()) {
            String realName = identityCryptoPort.decrypt(identity.getRealNameCipher());
            if (realName != null && realName.toLowerCase(Locale.ROOT).contains(keyword)) {
                matched.add(identity.getAccountId());
            }
        }
        return matched;
    }

    @Transactional(readOnly = true)
    public Integer isVerified(String accountId) {
        return profileIdentityRepository.findById(accountId)
                .map(identity -> Flags.of(identity.getStatus() == IdentitySnapshotStatus.VERIFIED))
                .orElse(0);
    }

    private IdentityStatusView toStatusView(String accountId, boolean sanitizeUser) {
        // 1. 加载快照
        ProfileIdentity identity = accountId == null || accountId.isBlank()
                ? null
                : profileIdentityRepository.findById(accountId).orElse(null);
        String status = identity == null ? IdentitySnapshotStatus.UNVERIFIED.name() : identity.getStatus().name();
        String documentType = identity == null ? null : identity.getDocumentType();
        String verifyChannel = identity == null ? null : identity.getVerifyChannel();
        String provider = identity == null ? null : identity.getProvider();
        var verifiedAt = identity == null ? null : identity.getVerifiedAt();
        var revokedAt = identity == null ? null : identity.getRevokedAt();
        String realNameMasked = null;
        String documentNoMasked = null;
        if (identity != null && identity.getRealNameCipher() != null) {
            realNameMasked = identityCryptoPort.maskRealName(identityCryptoPort.decrypt(identity.getRealNameCipher()));
        }
        if (identity != null && identity.getDocumentNoCipher() != null) {
            documentNoMasked = identityCryptoPort.maskDocumentNo(identityCryptoPort.decrypt(identity.getDocumentNoCipher()));
        }
        // 2. 附加 PENDING 工单
        RealNameCaseSummaryView pending = null;
        if (accountId != null && !accountId.isBlank()) {
            pending = realNameCaseRepository.findLatestPendingByAccount(accountId)
                    .map(this::toSummary)
                    .orElse(null);
        }
        // 3. 用户侧脱敏
        if (sanitizeUser) {
            verifyChannel = null;
            provider = null;
            if (pending != null) {
                pending = new RealNameCaseSummaryView(
                        pending.caseId(), pending.accountId(), pending.businessType(), null,
                        pending.status(), pending.documentType(), null, null,
                        pending.createdAt(), pending.reviewedAt(), pending.rejectReason());
            }
        }
        return new IdentityStatusView(status, documentType, realNameMasked, documentNoMasked,
                verifyChannel, provider, verifiedAt, revokedAt, pending);
    }

    private IdentityPageView toPageView(ProfileIdentity identity) {
        String realNameMasked = identity.getRealNameCipher() == null ? null
                : identityCryptoPort.maskRealName(identityCryptoPort.decrypt(identity.getRealNameCipher()));
        String documentNoMasked = identity.getDocumentNoCipher() == null ? null
                : identityCryptoPort.maskDocumentNo(identityCryptoPort.decrypt(identity.getDocumentNoCipher()));
        return new IdentityPageView(
                identity.getAccountId(),
                identity.getStatus().name(),
                identity.getDocumentType(),
                realNameMasked,
                documentNoMasked,
                identity.getVerifyChannel(),
                identity.getProvider(),
                identity.getVerifiedAt(),
                identity.getRevokedAt());
    }

    private RealNameCaseSummaryView toSummary(RealNameCase entity) {
        String realNameMasked = entity.getRealNameCipher() == null ? null
                : identityCryptoPort.maskRealName(identityCryptoPort.decrypt(entity.getRealNameCipher()));
        String documentNoMasked = entity.getDocumentNoCipher() == null ? null
                : identityCryptoPort.maskDocumentNo(identityCryptoPort.decrypt(entity.getDocumentNoCipher()));
        return new RealNameCaseSummaryView(
                entity.getCaseId(),
                entity.getAccountId(),
                entity.getBusinessType(),
                entity.getVerifyChannel(),
                entity.getStatus().name(),
                entity.getDocumentType(),
                realNameMasked,
                documentNoMasked,
                entity.getCreatedAt(),
                entity.getReviewedAt(),
                entity.getRejectReason());
    }
}
