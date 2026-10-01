package io.github.jiangbyte.voxel.boot.portal.cases.profile.admin;

import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.adapter.port.DeptQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.adapter.port.DeptView;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.adapter.port.GroupQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.adapter.port.GroupView;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.AccountAuthorization;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.IamRelationService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.adapter.port.RoleQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.adapter.port.RoleView;
import io.github.jiangbyte.voxel.boot.portal.cases.sys.file.IFileCase;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.admin.AdminProfile;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.admin.adapter.repository.AdminProfileRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 用例接口（xfg-ddd Case）。
 *
 * Author: Charlie
 */
public interface IAdminProfileCase {
    AdminMeView currentMe();
    void updateCurrentProfile(UpdateAdminProfileCommand command);
    AvatarUpdateView uploadAvatar(String originalFilename, String contentType, long size,
                                java.io.InputStream inputStream);
    OrgInfoView orgInfo();
    AdminProfileView getByAccountId(String accountId);
    void ensureProfile(String accountId);
    void updatePhone(String accountId, String phone);
    void updateEmail(String accountId, String email);
}
