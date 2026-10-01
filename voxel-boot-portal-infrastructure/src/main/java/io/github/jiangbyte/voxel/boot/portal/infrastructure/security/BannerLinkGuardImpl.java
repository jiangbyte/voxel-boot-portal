package io.github.jiangbyte.voxel.boot.portal.infrastructure.security;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port.BannerLinkGuard;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Component;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;

/**
 * Banner 链接安全校验：委托 SafeLinkValidator，异常转为领域异常。
 *
 * Author: Charlie
 */
@Component
public class BannerLinkGuardImpl implements BannerLinkGuard {

    @Override
    public void validate(String linkType, String url) {
        try {
            SafeLinkValidator.validateBannerLink(linkType, url);
        } catch (IllegalArgumentException ex) {
            throw new DomainException(ex.getMessage());
        }
    }
}
