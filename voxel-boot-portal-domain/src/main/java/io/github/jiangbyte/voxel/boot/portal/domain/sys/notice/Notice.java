package io.github.jiangbyte.voxel.boot.portal.domain.sys.notice;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.event.NoticeCreated;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.event.NoticeDeleted;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.event.NoticePublished;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.event.NoticeRevoked;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.event.NoticeUpdated;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 公告聚合根：维护标题、内容、投放范围与发布状态等不变量。
 *
 * Author: Charlie
 */
public class Notice extends AggregateRoot<String> {

    private String id;
    private NoticeKind kind;
    private String title;
    private String content;
    private String contentType;
    private String category;
    private String severity;
    private String targetScope;
    private String targetAccountTypes;
    private String targetAccountIds;
    private String targetDeptIds;
    private String targetRoleIds;
    private String publishLocations;
    private Integer pinned;
    private LocalDateTime pinnedUntil;
    private String senderAccountType;
    private String senderAccountId;
    private String sourceType;
    private String sourceId;
    private NoticeStatus status;
    private LocalDateTime publishAt;
    private LocalDateTime revokedAt;
    private LocalDateTime expireAt;
    private Integer viewCount;
    private String extra;

    private Notice() {
    }

    /**
     * 新建草稿公告并登记创建事件。
     */
    public static Notice create(String kind,
                                String title,
                                String content,
                                String contentType,
                                String category,
                                String severity,
                                String targetScope,
                                String targetAccountTypes,
                                String targetAccountIds,
                                String targetDeptIds,
                                String targetRoleIds,
                                String publishLocations,
                                Integer pinned,
                                LocalDateTime pinnedUntil,
                                LocalDateTime expireAt,
                                String extra) {
        // 1. 校验标题与内容
        String normalizedTitle = requireTitle(title);
        String normalizedContent = requireContent(content);
        // 2. 解析种类与默认投放
        NoticeKind noticeKind = NoticeKind.from(kind);
        // 3. 组装草稿聚合
        Notice notice = new Notice();
        notice.id = UUID.randomUUID().toString().replace("-", "");
        notice.kind = noticeKind;
        notice.title = normalizedTitle;
        notice.content = normalizedContent;
        notice.contentType = blankToDefault(contentType, "TEXT");
        notice.category = blankToNull(category);
        notice.severity = blankToDefault(severity, "INFO");
        notice.targetScope = blankToDefault(targetScope, "ALL");
        notice.targetAccountTypes = normalizeJsonArray(targetAccountTypes);
        notice.targetAccountIds = normalizeJsonArray(targetAccountIds);
        notice.targetDeptIds = normalizeJsonArray(targetDeptIds);
        notice.targetRoleIds = normalizeJsonArray(targetRoleIds);
        notice.publishLocations = normalizeJsonArray(publishLocations);
        notice.pinned = Flags.of(pinned);
        notice.pinnedUntil = pinnedUntil;
        notice.status = NoticeStatus.DRAFT;
        notice.expireAt = expireAt;
        notice.viewCount = 0;
        notice.extra = normalizeExtra(extra);
        // 4. 登记领域事件
        notice.registerEvent(new NoticeCreated(notice.id, notice.title));
        return notice;
    }

    /**
     * 从持久化状态重建聚合（不触发领域事件）。
     */
    public static Notice reconstitute(String id,
                                      String kind,
                                      String title,
                                      String content,
                                      String contentType,
                                      String category,
                                      String severity,
                                      String targetScope,
                                      String targetAccountTypes,
                                      String targetAccountIds,
                                      String targetDeptIds,
                                      String targetRoleIds,
                                      String publishLocations,
                                      Integer pinned,
                                      LocalDateTime pinnedUntil,
                                      String senderAccountType,
                                      String senderAccountId,
                                      String sourceType,
                                      String sourceId,
                                      String status,
                                      LocalDateTime publishAt,
                                      LocalDateTime revokedAt,
                                      LocalDateTime expireAt,
                                      Integer viewCount,
                                      String extra) {
        Notice notice = new Notice();
        notice.id = id;
        notice.kind = NoticeKind.from(kind);
        notice.title = title;
        notice.content = content;
        notice.contentType = contentType;
        notice.category = category;
        notice.severity = severity;
        notice.targetScope = targetScope;
        notice.targetAccountTypes = targetAccountTypes;
        notice.targetAccountIds = targetAccountIds;
        notice.targetDeptIds = targetDeptIds;
        notice.targetRoleIds = targetRoleIds;
        notice.publishLocations = publishLocations;
        notice.pinned = Flags.of(pinned);
        notice.pinnedUntil = pinnedUntil;
        notice.senderAccountType = senderAccountType;
        notice.senderAccountId = senderAccountId;
        notice.sourceType = sourceType;
        notice.sourceId = sourceId;
        notice.status = NoticeStatus.from(status);
        notice.publishAt = publishAt;
        notice.revokedAt = revokedAt;
        notice.expireAt = expireAt;
        notice.viewCount = viewCount == null ? 0 : viewCount;
        notice.extra = normalizeExtra(extra);
        return notice;
    }

    /**
     * 更新草稿内容；已发布公告仅允许改标题内容与置顶。
     */
    public void update(String kind,
                       String title,
                       String content,
                       String contentType,
                       String category,
                       String severity,
                       String targetScope,
                       String targetAccountTypes,
                       String targetAccountIds,
                       String targetDeptIds,
                       String targetRoleIds,
                       String publishLocations,
                       Integer pinned,
                       LocalDateTime pinnedUntil,
                       LocalDateTime expireAt,
                       String extra) {
        // 1. 已撤回不可再编辑
        if (this.status == NoticeStatus.REVOKED) {
            throw new DomainException("Revoked notice cannot be updated");
        }
        // 2. 覆盖内容字段
        this.kind = NoticeKind.from(kind);
        this.title = requireTitle(title);
        this.content = requireContent(content);
        this.contentType = blankToDefault(contentType, "TEXT");
        this.category = blankToNull(category);
        this.severity = blankToDefault(severity, "INFO");
        this.targetScope = blankToDefault(targetScope, "ALL");
        this.targetAccountTypes = normalizeJsonArray(targetAccountTypes);
        this.targetAccountIds = normalizeJsonArray(targetAccountIds);
        this.targetDeptIds = normalizeJsonArray(targetDeptIds);
        this.targetRoleIds = normalizeJsonArray(targetRoleIds);
        this.publishLocations = normalizeJsonArray(publishLocations);
        this.pinned = Flags.of(pinned);
        this.pinnedUntil = pinnedUntil;
        this.expireAt = expireAt;
        this.extra = normalizeExtra(extra);
        // 3. 登记更新事件
        registerEvent(new NoticeUpdated(this.id, this.title));
    }

    /**
     * 发布公告。
     */
    public void publish() {
        // 1. 仅草稿可发布
        if (this.status != NoticeStatus.DRAFT && this.status != NoticeStatus.REVOKED) {
            throw new DomainException("Only draft or revoked notice can be published");
        }
        // 2. 切换状态
        this.status = NoticeStatus.PUBLISHED;
        this.publishAt = LocalDateTime.now();
        this.revokedAt = null;
        // 3. 登记发布事件
        registerEvent(new NoticePublished(this.id, this.title));
    }

    /**
     * 撤回公告。
     */
    public void revoke() {
        // 1. 仅已发布可撤回
        if (this.status != NoticeStatus.PUBLISHED) {
            throw new DomainException("Only published notice can be revoked");
        }
        // 2. 切换状态
        this.status = NoticeStatus.REVOKED;
        this.revokedAt = LocalDateTime.now();
        // 3. 登记撤回事件
        registerEvent(new NoticeRevoked(this.id, this.title));
    }

    /**
     * 设置置顶。
     */
    public void pin(Integer pinned, LocalDateTime pinnedUntil) {
        this.pinned = Flags.of(pinned);
        this.pinnedUntil = Flags.isOn(this.pinned) ? pinnedUntil : null;
        registerEvent(new NoticeUpdated(this.id, this.title));
    }

    /**
     * 标记删除。
     */
    public void markDeleted() {
        registerEvent(new NoticeDeleted(this.id, this.title));
    }

    private static String requireTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new DomainException("Notice title must not be blank");
        }
        return title.trim();
    }

    private static String requireContent(String content) {
        if (content == null || content.isBlank()) {
            throw new DomainException("Notice content must not be blank");
        }
        return content;
    }

    private static String normalizeExtra(String extra) {
        return extra == null || extra.isBlank() ? "{}" : extra.trim();
    }

    private static String normalizeJsonArray(String json) {
        return json == null || json.isBlank() ? "[]" : json.trim();
    }

    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text;
    }

    private static String blankToDefault(String text, String defaultValue) {
        return text == null || text.isBlank() ? defaultValue : text.trim();
    }

    @Override
    public String getId() {
        return id;
    }

    public NoticeKind getKind() { return kind; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getContentType() { return contentType; }
    public String getCategory() { return category; }
    public String getSeverity() { return severity; }
    public String getTargetScope() { return targetScope; }
    public String getTargetAccountTypes() { return targetAccountTypes; }
    public String getTargetAccountIds() { return targetAccountIds; }
    public String getTargetDeptIds() { return targetDeptIds; }
    public String getTargetRoleIds() { return targetRoleIds; }
    public String getPublishLocations() { return publishLocations; }
    public Integer getPinned() { return pinned; }
    public LocalDateTime getPinnedUntil() { return pinnedUntil; }
    public String getSenderAccountType() { return senderAccountType; }
    public String getSenderAccountId() { return senderAccountId; }
    public String getSourceType() { return sourceType; }
    public String getSourceId() { return sourceId; }
    public NoticeStatus getStatus() { return status; }
    public LocalDateTime getPublishAt() { return publishAt; }
    public LocalDateTime getRevokedAt() { return revokedAt; }
    public LocalDateTime getExpireAt() { return expireAt; }
    public Integer getViewCount() { return viewCount; }
    public String getExtra() { return extra; }
}
