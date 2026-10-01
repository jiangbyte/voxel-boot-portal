package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository.Repository;

import java.util.List;
import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.RealNameCase;

/**
 * 实名工单仓储端口。
 *
 * Author: Charlie
 */
public interface RealNameCaseRepository extends Repository<RealNameCase, String> {

    /** 账号在指定业务下是否有 PENDING 工单。 */
    long countPendingByAccount(String accountId, String businessType);

    /** 证件哈希是否已有 PENDING 工单（排除指定账号）。 */
    Optional<RealNameCase> findPendingByDocumentHash(String documentNoHash, String excludeAccountId);

    /** 账号最近一条 PENDING 工单。 */
    Optional<RealNameCase> findLatestPendingByAccount(String accountId);

    /** 我的工单分页。 */
    List<RealNameCase> pageByAccount(String accountId, String businessType, String status, long offset, long limit);

    long countByAccount(String accountId, String businessType, String status);

    /** 审核分页。 */
    List<RealNameCase> pageForReview(String businessType, String status, String accountId, long offset, long limit);

    long countForReview(String businessType, String status, String accountId);
}
