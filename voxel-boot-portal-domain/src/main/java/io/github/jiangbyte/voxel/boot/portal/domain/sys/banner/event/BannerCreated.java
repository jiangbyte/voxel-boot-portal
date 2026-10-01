package io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;

/**
 * Banner 已创建。
 *
 * Author: Charlie
 */
public record BannerCreated(String aggregateId, String title, LocalDateTime occurredAt) implements DomainEvent {

    public BannerCreated(String aggregateId, String title) {
        this(aggregateId, title, LocalDateTime.now());
    }
}
