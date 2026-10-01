package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 实名工单流水（非聚合根实体值）。
 *
 * Author: Charlie
 */
public class RealNameCaseRecord {

    private final String recordId;
    private final String caseId;
    private final String accountId;
    private final String businessType;
    private final String action;
    private final String statusBefore;
    private final String statusAfter;
    private final String verifyChannel;
    private final String provider;
    private final String operatorId;
    private final String deptId;
    private final String remark;
    private final LocalDateTime createdAt;

    private RealNameCaseRecord(String recordId,
                               String caseId,
                               String accountId,
                               String businessType,
                               String action,
                               String statusBefore,
                               String statusAfter,
                               String verifyChannel,
                               String provider,
                               String operatorId,
                               String deptId,
                               String remark,
                               LocalDateTime createdAt) {
        this.recordId = recordId;
        this.caseId = caseId;
        this.accountId = accountId;
        this.businessType = businessType;
        this.action = action;
        this.statusBefore = statusBefore;
        this.statusAfter = statusAfter;
        this.verifyChannel = verifyChannel;
        this.provider = provider;
        this.operatorId = operatorId;
        this.deptId = deptId;
        this.remark = remark;
        this.createdAt = createdAt;
    }

    /**
     * 基于工单动作创建流水。
     */
    public static RealNameCaseRecord of(RealNameCase caseEntity,
                                        String action,
                                        String statusBefore,
                                        String statusAfter,
                                        String operatorId,
                                        String remark) {
        return new RealNameCaseRecord(
                UUID.randomUUID().toString().replace("-", ""),
                caseEntity.getCaseId(),
                caseEntity.getAccountId(),
                caseEntity.getBusinessType(),
                action,
                statusBefore,
                statusAfter,
                caseEntity.getVerifyChannel(),
                caseEntity.getProvider(),
                operatorId,
                null,
                remark,
                LocalDateTime.now());
    }

    public static RealNameCaseRecord reconstitute(String recordId,
                                                  String caseId,
                                                  String accountId,
                                                  String businessType,
                                                  String action,
                                                  String statusBefore,
                                                  String statusAfter,
                                                  String verifyChannel,
                                                  String provider,
                                                  String operatorId,
                                                  String deptId,
                                                  String remark,
                                                  LocalDateTime createdAt) {
        return new RealNameCaseRecord(recordId, caseId, accountId, businessType, action,
                statusBefore, statusAfter, verifyChannel, provider, operatorId, deptId, remark, createdAt);
    }

    public String getRecordId() { return recordId; }
    public String getCaseId() { return caseId; }
    public String getAccountId() { return accountId; }
    public String getBusinessType() { return businessType; }
    public String getAction() { return action; }
    public String getStatusBefore() { return statusBefore; }
    public String getStatusAfter() { return statusAfter; }
    public String getVerifyChannel() { return verifyChannel; }
    public String getProvider() { return provider; }
    public String getOperatorId() { return operatorId; }
    public String getDeptId() { return deptId; }
    public String getRemark() { return remark; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
