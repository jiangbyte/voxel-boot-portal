package io.github.jiangbyte.voxel.boot.portal.cases.sys.banner;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.repository.BannerRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.BannerStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port.BannerInteractionCounter;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port.BannerLinkGuard;

/**
 * 用例接口。
 *
 * Author: Charlie
 */
public interface IBannerCase {
    void create(CreateBannerCommand command);
    void update(UpdateBannerCommand command);
    void delete(List<String> ids);
    void interaction(String id);
}
