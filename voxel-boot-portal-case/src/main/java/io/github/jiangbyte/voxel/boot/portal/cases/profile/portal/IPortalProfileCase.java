package io.github.jiangbyte.voxel.boot.portal.cases.profile.portal;

import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.AccountApi;
import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.AccountInfo;
import io.github.jiangbyte.voxel.boot.portal.cases.sys.file.IFileCase;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.portal.PortalProfile;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.portal.adapter.repository.PortalProfileRepository;
import java.util.Locale;
import java.util.Set;

/**
 * 用例接口。
 *
 * Author: Charlie
 */
public interface IPortalProfileCase {
    PortalMeView currentMe();
    void updateCurrentProfile(UpdatePortalProfileCommand command);
    AvatarUpdateView uploadAvatar(String originalFilename, String contentType, long size,
                                java.io.InputStream inputStream);
    PublicProfileView publicProfile(String accountId);
    PortalProfileView getByAccountId(String accountId);
    void ensureProfile(String accountId);
    void updatePhone(String accountId, String phone);
    void updateEmail(String accountId, String email);
}
