package io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;

/**
 * Banner 已删除。
 *
 * Author: Charlie
 */
public record BannerDeleted(String aggregateId, String title, LocalDateTime occurredAt) implements DomainEvent {

    public BannerDeleted(String aggregateId, String title) {
        this(aggregateId, title, LocalDateTime.now());
    }
}
