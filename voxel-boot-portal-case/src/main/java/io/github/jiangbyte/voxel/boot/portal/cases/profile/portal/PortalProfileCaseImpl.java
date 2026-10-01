package io.github.jiangbyte.voxel.boot.portal.cases.profile.portal;

import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.AccountApi;
import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.AccountInfo;
import io.github.jiangbyte.voxel.boot.portal.cases.sys.file.IFileCase;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileAccessUrlPort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.portal.PortalProfile;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.portal.adapter.repository.PortalProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;

/**
 * 门户资料写用例，并实现 {@link ProfileUserPortalApi}。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class PortalProfileCaseImpl implements IPortalProfileCase, ProfileUserPortalApi {

    private static final long AVATAR_MAX_SIZE = 2 * 1024 * 1024;
    private static final Set<String> AVATAR_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final PortalProfileRepository portalProfileRepository;
    private final SessionPort sessionPort;
    private final IFileCase fileApplicationService;
    private final FileAccessUrlPort fileAccessUrlPort;
    private final AccountApi accountApi;


    /**
     * 当前登录门户用户摘要。
     */
    @Transactional(readOnly = true)
    public PortalMeView currentMe() {
        // 1. 解析会话
        String accountId = requireCurrentAccountId();
        // 2. 加载资料
        PortalProfileView profile = portalProfileRepository.findById(accountId)
                .map(this::toView)
                .orElse(new PortalProfileView(accountId, null, null, null, null, null));
        // 3. 组装
        return new PortalMeView(accountId, profile.nickname(), profile.avatar(), profile);
    }

    /**
     * 更新当前资料。
     */
    @Transactional
    public void updateCurrentProfile(UpdatePortalProfileCommand command) {
        String accountId = requireCurrentAccountId();
        PortalProfile profile = portalProfileRepository.findById(accountId)
                .orElseGet(() -> PortalProfile.create(accountId));
        profile.updateProfile(command.nickname(), command.avatar(), command.signature());
        portalProfileRepository.save(profile);
    }

    /**
     * 上传头像。
     */
    @Transactional
    public AvatarUpdateView uploadAvatar(String originalFilename, String contentType, long size,
                                        java.io.InputStream inputStream) {
        // 1. 校验文件元数据（由 trigger 从 MultipartFile 拆出，避免 domain 依赖 spring-web）
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
        // 2. 真实上传头像并写资料
        PortalProfile profile = portalProfileRepository.findById(accountId)
                .orElseGet(() -> PortalProfile.create(accountId));
        FileView uploaded = fileApplicationService.upload(
                originalFilename, ct, size, inputStream, null);
        profile.updateAvatar(uploaded.objectName());
        portalProfileRepository.save(profile);
        // 3. 返回
        String url = uploaded.url() == null || uploaded.url().isBlank() ? uploaded.objectName() : uploaded.url();
        return new AvatarUpdateView(url, uploaded.id(), uploaded.objectName(), url);
    }

    /**
     * 公开空间资料。
     */
    @Transactional(readOnly = true)
    public PublicProfileView publicProfile(String accountId) {
        // 1. 校验入参
        if (accountId == null || accountId.isBlank()) {
            throw new DomainException("account_id is required");
        }
        // 2. 加载资料与主登录名
        PortalProfileView profile = portalProfileRepository.findById(accountId.trim())
                .map(this::toView)
                .orElse(new PortalProfileView(accountId.trim(), null, null, null, null, null));
        AccountInfo account = accountApi.getById(accountId.trim());
        String login = account == null ? null : account.getPrimaryIdentifier();
        // 3. 组装公开视图
        return new PublicProfileView(profile.accountId(), profile.nickname(), login, profile.avatar(), profile.signature());
    }

    @Override
    @Transactional(readOnly = true)
    public PortalProfileView getByAccountId(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            return null;
        }
        return portalProfileRepository.findById(accountId).map(this::toView).orElse(null);
    }

    @Override
    @Transactional
    public void ensureProfile(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            return;
        }
        if (portalProfileRepository.findById(accountId).isEmpty()) {
            portalProfileRepository.save(PortalProfile.create(accountId));
        }
    }

    @Override
    @Transactional
    public void updatePhone(String accountId, String phone) {
        if (accountId == null || accountId.isBlank()) {
            return;
        }
        PortalProfile profile = portalProfileRepository.findById(accountId)
                .orElseGet(() -> PortalProfile.create(accountId));
        profile.updatePhone(phone);
        portalProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public void updateEmail(String accountId, String email) {
        if (accountId == null || accountId.isBlank()) {
            return;
        }
        PortalProfile profile = portalProfileRepository.findById(accountId)
                .orElseGet(() -> PortalProfile.create(accountId));
        profile.updateEmail(email);
        portalProfileRepository.save(profile);
    }

    private String requireCurrentAccountId() {
        String accountId = sessionPort.currentPortalAccountId();
        if (accountId == null || accountId.isBlank()) {
            throw new DomainException(401, "Not logged in");
        }
        return accountId;
    }

    private PortalProfileView toView(PortalProfile profile) {
        // 1. 落库 avatar 为 object key；读时解析为可访问 URL
        return new PortalProfileView(
                profile.getAccountId(),
                profile.getNickname(),
                resolveAvatarUrl(profile.getAvatar()),
                profile.getSignature(),
                profile.getPhone(),
                profile.getEmail());
    }

    private String resolveAvatarUrl(String avatar) {
        if (!StringUtils.hasText(avatar)) {
            return null;
        }
        String resolved = fileAccessUrlPort.resolveAccessUrl(avatar, null);
        return StringUtils.hasText(resolved) ? resolved : avatar;
    }
}
