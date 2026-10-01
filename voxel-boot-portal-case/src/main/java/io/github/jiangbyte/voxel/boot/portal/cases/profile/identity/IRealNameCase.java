package io.github.jiangbyte.voxel.boot.portal.cases.profile.identity;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileApi;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileInfo;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.IdentitySnapshotStatus;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.ProfileIdentity;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.repository.ProfileIdentityRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.RealNameBusinessType;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.RealNameCase;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.RealNameCaseRecord;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.repository.RealNameCaseRecordRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.repository.RealNameCaseRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.VerifyChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.port.IdentityCryptoPort;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.port.RealNameVerifyInitResult;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.port.RealNameVerifyPort;

/**
 * 用例接口（xfg-ddd Case）。
 *
 * Author: Charlie
 */
public interface IRealNameCase {
    RealNameCaseOptionsView options();
    void submit(String accountId, String businessType, String documentType, String realName, String documentNo, List<String> attachmentIds, String applicantContact);
    RealNameCaseInitView initThirdParty(String accountId, String businessType, String documentType, String realName, String documentNo, String provider);
    void callback(String caseId, String providerOrderNo, Integer success, String message);
    PageResult<RealNameCaseSummaryView> myPage(String accountId, String businessType, String status, long current, long size);
    PageResult<RealNameCaseSummaryView> reviewPage(String businessType, String status, String accountId, long current, long size);
    RealNameCaseDetailView detail(String caseId);
    void approve(String caseId, String reviewerId, String remark);
    void reject(String caseId, String reviewerId, String rejectReason);
}
