package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.identity;

import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.RealNameCase;

/**
 * 实名工单聚合与 PO 转换。
 *
 * Author: Charlie
 */
public final class RealNameCaseAssembler {

    private RealNameCaseAssembler() {
    }

    public static RealNameCase toAggregate(RealNameCasePO po) {
        if (po == null) {
            return null;
        }
        return RealNameCase.reconstitute(
                po.getCaseId(), po.getBusinessType(), po.getVerifyChannel(), po.getStatus(),
                po.getAccountId(), po.getTargetAccountHintCipher(), po.getApplicantContactCipher(),
                po.getDocumentType(), po.getRealNameCipher(), po.getDocumentNoCipher(), po.getDocumentNoHash(),
                po.getAttachmentIds(), po.getPayloadCipher(), po.getHandlerDeptId(),
                po.getProvider(), po.getProviderOrderNo(), po.getSubmitterId(), po.getReviewerId(),
                po.getReviewedAt(), po.getRejectReason(), po.getExpireAt(), po.getCreatedAt());
    }

    public static RealNameCasePO toPO(RealNameCase entity) {
        RealNameCasePO po = new RealNameCasePO();
        po.setCaseId(entity.getCaseId());
        po.setBusinessType(entity.getBusinessType());
        po.setVerifyChannel(entity.getVerifyChannel());
        po.setStatus(entity.getStatus().name());
        po.setAccountId(entity.getAccountId());
        po.setTargetAccountHintCipher(entity.getTargetAccountHintCipher());
        po.setApplicantContactCipher(entity.getApplicantContactCipher());
        po.setDocumentType(entity.getDocumentType());
        po.setRealNameCipher(entity.getRealNameCipher());
        po.setDocumentNoCipher(entity.getDocumentNoCipher());
        po.setDocumentNoHash(entity.getDocumentNoHash());
        po.setAttachmentIds(entity.getAttachmentIds() == null
                ? new java.util.ArrayList<>()
                : new java.util.ArrayList<>(entity.getAttachmentIds()));
        po.setPayloadCipher(entity.getPayloadCipher());
        po.setHandlerDeptId(entity.getHandlerDeptId());
        po.setProvider(entity.getProvider());
        po.setProviderOrderNo(entity.getProviderOrderNo());
        po.setSubmitterId(entity.getSubmitterId());
        po.setReviewerId(entity.getReviewerId());
        po.setReviewedAt(entity.getReviewedAt());
        po.setRejectReason(entity.getRejectReason());
        po.setExpireAt(entity.getExpireAt());
        po.setCreatedAt(entity.getCreatedAt());
        return po;
    }
}
