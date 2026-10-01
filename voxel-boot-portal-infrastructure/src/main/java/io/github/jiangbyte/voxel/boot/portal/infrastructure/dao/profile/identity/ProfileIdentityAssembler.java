package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.identity;

import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.ProfileIdentity;

/**
 * 实名快照聚合与 PO 转换。
 *
 * Author: Charlie
 */
public final class ProfileIdentityAssembler {

    private ProfileIdentityAssembler() {
    }

    public static ProfileIdentity toAggregate(ProfileIdentityPO po) {
        if (po == null) {
            return null;
        }
        return ProfileIdentity.reconstitute(
                po.getAccountId(), po.getStatus(), po.getDocumentType(),
                po.getRealNameCipher(), po.getDocumentNoCipher(), po.getDocumentNoHash(),
                po.getVerifyChannel(), po.getProvider(), po.getProviderOrderNo(),
                po.getVerifiedAt(), po.getSourceCaseId(), po.getRevokedAt(), po.getRevokedBy());
    }

    public static ProfileIdentityPO toPO(ProfileIdentity identity) {
        ProfileIdentityPO po = new ProfileIdentityPO();
        po.setAccountId(identity.getAccountId());
        po.setStatus(identity.getStatus().name());
        po.setDocumentType(identity.getDocumentType());
        po.setRealNameCipher(identity.getRealNameCipher());
        po.setDocumentNoCipher(identity.getDocumentNoCipher());
        po.setDocumentNoHash(identity.getDocumentNoHash());
        po.setVerifyChannel(identity.getVerifyChannel());
        po.setProvider(identity.getProvider());
        po.setProviderOrderNo(identity.getProviderOrderNo());
        po.setVerifiedAt(identity.getVerifiedAt());
        po.setSourceCaseId(identity.getSourceCaseId());
        po.setRevokedAt(identity.getRevokedAt());
        po.setRevokedBy(identity.getRevokedBy());
        return po;
    }
}
