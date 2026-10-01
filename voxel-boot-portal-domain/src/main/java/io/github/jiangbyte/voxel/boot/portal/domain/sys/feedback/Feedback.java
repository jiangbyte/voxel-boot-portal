package io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.event.FeedbackCreated;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.event.FeedbackDeleted;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.event.FeedbackUpdated;

import java.time.LocalDateTime;
import java.util.UUID;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;

/**
 * 意见反馈聚合根：维护提交内容、附件对象名、处理状态与回复等不变量。
 *
 * Author: Charlie
 */
public class Feedback extends AggregateRoot<String> {

    private String id;
    private String title;
    private String content;
    private String category;
    private String contact;
    /** JSON 数组字符串，与 Banner.targetAccountTypes 同模式。 */
    private String attachObjectNames;
    private FeedbackStatus status;
    private String reply;
    private String repliedBy;
    private LocalDateTime repliedAt;
    private String submitterAccountType;
    private String submitterAccountId;

    private Feedback() {
    }

    /**
     * 新建反馈（默认 PENDING），并登记创建事件。
     */
    public static Feedback submit(String title,
                                  String content,
                                  String category,
                                  String contact,
                                  String attachObjectNames,
                                  String submitterAccountType,
                                  String submitterAccountId) {
        // 1. 校验内容与提交人必填
        String normalizedContent = requireContent(content);
        String normalizedAccountType = requireText(submitterAccountType, "Submitter account type");
        String normalizedAccountId = requireText(submitterAccountId, "Submitter account id");
        // 2. 标题为空时取内容前 64 字；分类默认 GENERAL
        String normalizedTitle = resolveTitle(title, normalizedContent);
        String normalizedCategory = blankToDefault(category, "GENERAL");
        // 3. 组装聚合：状态 PENDING，附件规范化为空数组
        Feedback feedback = new Feedback();
        feedback.id = UUID.randomUUID().toString().replace("-", "");
        feedback.title = normalizedTitle;
        feedback.content = normalizedContent;
        feedback.category = normalizedCategory;
        feedback.contact = blankToNull(contact);
        feedback.attachObjectNames = normalizeJsonArray(attachObjectNames);
        feedback.status = FeedbackStatus.PENDING;
        feedback.submitterAccountType = normalizedAccountType.trim().toUpperCase();
        feedback.submitterAccountId = normalizedAccountId;
        // 4. 登记领域事件
        feedback.registerEvent(new FeedbackCreated(feedback.id, feedback.title));
        return feedback;
    }

    /**
     * 从持久化状态重建聚合（不触发领域事件）。
     */
    public static Feedback reconstitute(String id,
                                        String title,
                                        String content,
                                        String category,
                                        String contact,
                                        String attachObjectNames,
                                        String status,
                                        String reply,
                                        String repliedBy,
                                        LocalDateTime repliedAt,
                                        String submitterAccountType,
                                        String submitterAccountId) {
        Feedback feedback = new Feedback();
        feedback.id = id;
        feedback.title = title;
        feedback.content = content;
        feedback.category = category;
        feedback.contact = contact;
        feedback.attachObjectNames = normalizeJsonArray(attachObjectNames);
        feedback.status = FeedbackStatus.from(status);
        feedback.reply = reply;
        feedback.repliedBy = repliedBy;
        feedback.repliedAt = repliedAt;
        feedback.submitterAccountType = submitterAccountType;
        feedback.submitterAccountId = submitterAccountId;
        return feedback;
    }

    /**
     * 管理端更新状态与回复；回复非 null 时记录回复人与时间。
     */
    public void updateReply(String status, String reply, String repliedBy) {
        // 1. 有状态则解析并覆盖
        if (status != null && !status.isBlank()) {
            this.status = FeedbackStatus.from(status);
        }
        // 2. reply 非 null（含空串）时写入，并记录回复人/时间
        if (reply != null) {
            this.reply = reply;
            this.repliedBy = requireText(repliedBy, "Replied by");
            this.repliedAt = LocalDateTime.now();
        }
        // 3. 登记更新事件
        registerEvent(new FeedbackUpdated(this.id, this.title));
    }

    /**
     * 标记删除并登记删除事件。
     */
    public void markDeleted() {
        registerEvent(new FeedbackDeleted(this.id, this.title));
    }

    private static String resolveTitle(String title, String content) {
        if (title != null && !title.isBlank()) {
            return title.trim();
        }
        if (content.length() > 64) {
            return content.substring(0, 64);
        }
        return content;
    }

    private static String requireContent(String content) {
        if (content == null || content.isBlank()) {
            throw new DomainException("Feedback content must not be blank");
        }
        return content;
    }

    private static String requireText(String text, String label) {
        if (text == null || text.isBlank()) {
            throw new DomainException(label + " must not be blank");
        }
        return text.trim();
    }

    private static String normalizeJsonArray(String json) {
        return json == null || json.isBlank() ? "[]" : json.trim();
    }

    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text.trim();
    }

    private static String blankToDefault(String text, String defaultValue) {
        return text == null || text.isBlank() ? defaultValue : text.trim();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public String getContact() {
        return contact;
    }

    public String getAttachObjectNames() {
        return attachObjectNames;
    }

    public FeedbackStatus getStatus() {
        return status;
    }

    public String getReply() {
        return reply;
    }

    public String getRepliedBy() {
        return repliedBy;
    }

    public LocalDateTime getRepliedAt() {
        return repliedAt;
    }

    public String getSubmitterAccountType() {
        return submitterAccountType;
    }

    public String getSubmitterAccountId() {
        return submitterAccountId;
    }
}
