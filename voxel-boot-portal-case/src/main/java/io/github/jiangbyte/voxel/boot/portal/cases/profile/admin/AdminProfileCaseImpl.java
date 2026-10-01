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
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileAccessUrlPort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.admin.AdminProfile;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.admin.adapter.repository.AdminProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;

/**
 * 管理端资料写用例，并实现 {@link ProfileUserAdminApi}。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class AdminProfileCaseImpl implements IAdminProfileCase, ProfileUserAdminApi {

    private static final long AVATAR_MAX_SIZE = 2 * 1024 * 1024;
    private static final Set<String> AVATAR_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final AdminProfileRepository adminProfileRepository;
    private final SessionPort sessionPort;
    private final IFileCase fileApplicationService;
    private final FileAccessUrlPort fileAccessUrlPort;
    private final IamRelationService iamRelationService;
    private final RoleQueryService roleQueryService;
    private final DeptQueryService deptQueryService;
    private final GroupQueryService groupQueryService;


    /**
     * 当前登录用户摘要。
     */
    @Transactional(readOnly = true)
    public AdminMeView currentMe() {
        // 1. 解析当前会话账号
        String accountId = requireCurrentAccountId();
        // 2. 加载或构造空资料视图
        AdminProfileView profile = adminProfileRepository.findById(accountId)
                .map(this::toView)
                .orElse(new AdminProfileView(accountId, null, null, null, null, null, null));
        // 3. 组装 me 摘要
        return new AdminMeView(accountId, profile.nickname(), profile.avatar(), profile);
    }

    /**
     * 更新当前用户资料。
     */
    @Transactional
    public void updateCurrentProfile(UpdateAdminProfileCommand command) {
        // 1. 解析当前会话账号
        String accountId = requireCurrentAccountId();
        // 2. 加载或创建资料聚合
        AdminProfile profile = adminProfileRepository.findById(accountId)
                .orElseGet(() -> AdminProfile.create(accountId));
        // 3. 更新字段并保存
        profile.updateProfile(command.nickname(), command.avatar(), command.signature(), command.remark());
        adminProfileRepository.save(profile);
    }

    /**
     * 上传并替换当前用户头像。
     */
    @Transactional
    public AvatarUpdateView uploadAvatar(String originalFilename, String contentType, long size,
                                        java.io.InputStream inputStream) {
        // 1. 校验文件存在、大小与 MIME（由 trigger 从 MultipartFile 拆出）
        String accountId = requireCurrentAccountId();
        if (originalFilename == null || originalFilename.isBlank() || size <= 0) {
            throw new DomainException("File is required");
        }
        if (size > AVATAR_MAX_SIZE) {
            throw new DomainException("Avatar exceeds 2MB");
        }
        String ct = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
        if (!AVATAR_TYPES.contains(ct)) {
            throw new DomainException("Avatar must be jpeg/png/webp");
        }
        // 2. 真实上传头像并写入资料
        AdminProfile profile = adminProfileRepository.findById(accountId)
                .orElseGet(() -> AdminProfile.create(accountId));
        FileView uploaded = fileApplicationService.upload(
                originalFilename, ct, size, inputStream, null);
        profile.updateAvatar(uploaded.objectName());
        adminProfileRepository.save(profile);
        // 3. 组装返回（url 已由存储解析）
        String url = uploaded.url() == null || uploaded.url().isBlank() ? uploaded.objectName() : uploaded.url();
        return new AvatarUpdateView(url, uploaded.id(), uploaded.objectName(), url);
    }

    /**
     * 查询当前用户组织信息。
     */
    @Transactional(readOnly = true)
    public OrgInfoView orgInfo() {
        // 1. 读取授权关系
        String accountId = requireCurrentAccountId();
        AccountAuthorization auth = iamRelationService.getAccountAuthorization(accountId);
        // 2. 批量翻译角色/部门/组名称，禁止循环 detail
        return new OrgInfoView(
                resolveRoleNames(auth.getRoleIds()),
                resolveDeptNames(auth.getDeptIds()),
                resolveGroupNames(auth.getGroupIds()));
    }

    @Override
    @Transactional(readOnly = true)
    public AdminProfileView getByAccountId(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            return null;
        }
        return adminProfileRepository.findById(accountId).map(this::toView).orElse(null);
    }

    @Override
    @Transactional
    public void ensureProfile(String accountId) {
        // 1. 空账号直接返回
        if (accountId == null || accountId.isBlank()) {
            return;
        }
        // 2. 不存在则创建空白资料
        if (adminProfileRepository.findById(accountId).isEmpty()) {
            adminProfileRepository.save(AdminProfile.create(accountId));
        }
    }

    @Override
    @Transactional
    public void updatePhone(String accountId, String phone) {
        if (accountId == null || accountId.isBlank()) {
            return;
        }
        AdminProfile profile = adminProfileRepository.findById(accountId)
                .orElseGet(() -> AdminProfile.create(accountId));
        profile.updatePhone(phone);
        adminProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public void updateEmail(String accountId, String email) {
        if (accountId == null || accountId.isBlank()) {
            return;
        }
        AdminProfile profile = adminProfileRepository.findById(accountId)
                .orElseGet(() -> AdminProfile.create(accountId));
        profile.updateEmail(email);
        adminProfileRepository.save(profile);
    }

    private String requireCurrentAccountId() {
        String accountId = sessionPort.currentAdminAccountId();
        if (accountId == null || accountId.isBlank()) {
            throw new DomainException(401, "Not logged in");
        }
        return accountId;
    }

    private AdminProfileView toView(AdminProfile profile) {
        // 1. 落库 avatar 为 object key；读时解析为可访问 URL，供前端 NAvatar 直接展示
        return new AdminProfileView(
                profile.getAccountId(),
                profile.getNickname(),
                resolveAvatarUrl(profile.getAvatar()),
                profile.getSignature(),
                profile.getPhone(),
                profile.getEmail(),
                profile.getRemark());
    }

    private String resolveAvatarUrl(String avatar) {
        if (!StringUtils.hasText(avatar)) {
            return null;
        }
        String resolved = fileAccessUrlPort.resolveAccessUrl(avatar, null);
        return StringUtils.hasText(resolved) ? resolved : avatar;
    }

    private List<OrgInfoView.IdNameView> resolveRoleNames(List<String> roleIds) {
        // 1. 一次批量加载角色，禁止循环 detail
        List<String> ids = nullSafe(roleIds);
        Map<String, String> nameById = new HashMap<>();
        for (RoleView role : roleQueryService.listByIds(ids)) {
            nameById.put(role.id(), role.name());
        }
        // 2. 按入参顺序组装；缺失名称回退为 ID
        List<OrgInfoView.IdNameView> roles = new ArrayList<>(ids.size());
        for (String roleId : ids) {
            roles.add(new OrgInfoView.IdNameView(roleId, nameById.getOrDefault(roleId, roleId)));
        }
        return roles;
    }

    private List<OrgInfoView.IdNameView> resolveDeptNames(List<String> deptIds) {
        // 1. 一次批量加载部门，禁止循环 detail
        List<String> ids = nullSafe(deptIds);
        Map<String, String> nameById = new HashMap<>();
        for (DeptView dept : deptQueryService.listByIds(ids)) {
            nameById.put(dept.id(), dept.name());
        }
        // 2. 按入参顺序组装；缺失名称回退为 ID
        List<OrgInfoView.IdNameView> depts = new ArrayList<>(ids.size());
        for (String deptId : ids) {
            depts.add(new OrgInfoView.IdNameView(deptId, nameById.getOrDefault(deptId, deptId)));
        }
        return depts;
    }

    private List<OrgInfoView.IdNameView> resolveGroupNames(List<String> groupIds) {
        // 1. 一次批量加载用户组，禁止循环 detail
        List<String> ids = nullSafe(groupIds);
        Map<String, String> nameById = new HashMap<>();
        for (GroupView group : groupQueryService.listByIds(ids)) {
            nameById.put(group.id(), group.name());
        }
        // 2. 按入参顺序组装；缺失名称回退为 ID
        List<OrgInfoView.IdNameView> groups = new ArrayList<>(ids.size());
        for (String groupId : ids) {
            groups.add(new OrgInfoView.IdNameView(groupId, nameById.getOrDefault(groupId, groupId)));
        }
        return groups;
    }

    private static List<String> nullSafe(List<String> list) {
        return list == null ? List.of() : list;
    }
}
