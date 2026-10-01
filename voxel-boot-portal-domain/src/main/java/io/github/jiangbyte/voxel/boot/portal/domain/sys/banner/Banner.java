package io.github.jiangbyte.voxel.boot.portal.domain.sys.banner;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.event.BannerCreated;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.event.BannerDeleted;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.event.BannerUpdated;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Banner 聚合根：维护标题、图片、投放位置、可见账户类型与展示窗口等不变量。
 *
 * Author: Charlie
 */
public class Banner extends AggregateRoot<String> {

    private String id;
    private String title;
    private String image;
    private String url;
    private String linkType;
    private String summary;
    private String description;
    private String category;
    private String type;
    private String position;
    private String targetAccountTypes;
    private Integer sort;
    private Long interactionCount;
    private BannerStatus status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private Banner() {
    }

    /**
     * 新建 Banner 并登记创建事件。
     */
    public static Banner create(String title,
                                String image,
                                String url,
                                String linkType,
                                String summary,
                                String description,
                                String category,
                                String type,
                                String position,
                                String targetAccountTypes,
                                Integer sort,
                                String status,
                                LocalDateTime startAt,
                                LocalDateTime endAt) {
        // 1. 校验必填字段
        String normalizedTitle = requireText(title, "Banner title");
        String normalizedImage = requireText(image, "Banner image");
        String normalizedCategory = requireText(category, "Banner category");
        String normalizedType = requireText(type, "Banner type");
        String normalizedPosition = requireText(position, "Banner position");
        // 2. 组装聚合并分配标识
        Banner banner = new Banner();
        banner.id = UUID.randomUUID().toString().replace("-", "");
        banner.title = normalizedTitle;
        banner.image = normalizedImage;
        banner.url = blankToNull(url);
        banner.linkType = blankToDefault(linkType, "URL");
        banner.summary = summary;
        banner.description = description;
        banner.category = normalizedCategory;
        banner.type = normalizedType;
        banner.position = normalizedPosition;
        banner.targetAccountTypes = normalizeJsonArray(targetAccountTypes);
        banner.sort = sort == null ? 0 : sort;
        banner.interactionCount = 0L;
        banner.status = BannerStatus.from(status);
        banner.startAt = startAt;
        banner.endAt = endAt;
        // 3. 登记领域事件
        banner.registerEvent(new BannerCreated(banner.id, banner.title));
        return banner;
    }

    /**
     * 从持久化状态重建聚合（不触发领域事件）。
     */
    public static Banner reconstitute(String id,
                                      String title,
                                      String image,
                                      String url,
                                      String linkType,
                                      String summary,
                                      String description,
                                      String category,
                                      String type,
                                      String position,
                                      String targetAccountTypes,
                                      Integer sort,
                                      Long interactionCount,
                                      String status,
                                      LocalDateTime startAt,
                                      LocalDateTime endAt) {
        Banner banner = new Banner();
        banner.id = id;
        banner.title = title;
        banner.image = image;
        banner.url = url;
        banner.linkType = blankToDefault(linkType, "URL");
        banner.summary = summary;
        banner.description = description;
        banner.category = category;
        banner.type = type;
        banner.position = position;
        banner.targetAccountTypes = normalizeJsonArray(targetAccountTypes);
        banner.sort = sort == null ? 0 : sort;
        banner.interactionCount = interactionCount == null ? 0L : interactionCount;
        banner.status = BannerStatus.from(status);
        banner.startAt = startAt;
        banner.endAt = endAt;
        return banner;
    }

    /**
     * 更新 Banner 属性并登记更新事件。
     */
    public void update(String title,
                       String image,
                       String url,
                       String linkType,
                       String summary,
                       String description,
                       String category,
                       String type,
                       String position,
                       String targetAccountTypes,
                       Integer sort,
                       String status,
                       LocalDateTime startAt,
                       LocalDateTime endAt) {
        // 1. 校验必填字段
        this.title = requireText(title, "Banner title");
        this.image = requireText(image, "Banner image");
        this.category = requireText(category, "Banner category");
        this.type = requireText(type, "Banner type");
        this.position = requireText(position, "Banner position");
        // 2. 覆盖可变字段（保留互动计数）
        this.url = blankToNull(url);
        this.linkType = blankToDefault(linkType, "URL");
        this.summary = summary;
        this.description = description;
        this.targetAccountTypes = normalizeJsonArray(targetAccountTypes);
        this.sort = sort == null ? 0 : sort;
        this.status = BannerStatus.from(status);
        this.startAt = startAt;
        this.endAt = endAt;
        // 3. 登记更新事件
        registerEvent(new BannerUpdated(this.id, this.title));
    }

    /**
     * 标记删除。
     */
    public void markDeleted() {
        registerEvent(new BannerDeleted(this.id, this.title));
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new DomainException(field + " must not be blank");
        }
        return value.trim();
    }

    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text.trim();
    }

    private static String blankToDefault(String text, String defaultValue) {
        return text == null || text.isBlank() ? defaultValue : text.trim();
    }

    private static String normalizeJsonArray(String raw) {
        if (raw == null || raw.isBlank()) {
            return "[]";
        }
        return raw.trim();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }

    public String getLinkType() {
        return linkType;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getPosition() {
        return position;
    }

    public String getTargetAccountTypes() {
        return targetAccountTypes;
    }

    public Integer getSort() {
        return sort;
    }

    public Long getInteractionCount() {
        return interactionCount;
    }

    public BannerStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }
}
