package io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;

/**
 * Banner 已更新。
 *
 * Author: Charlie
 */
public record BannerUpdated(String aggregateId, String title, LocalDateTime occurredAt) implements DomainEvent {

    public BannerUpdated(String aggregateId, String title) {
        this(aggregateId, title, LocalDateTime.now());
    }
}
