package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.notice;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告持久化对象，对应表 sys_notice。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_notice")
public class NoticePO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String kind;
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
    private Integer isPinned;
    private LocalDateTime pinnedUntil;
    private String senderAccountType;
    private String senderAccountId;
    private String sourceType;
    private String sourceId;
    private String status;
    private LocalDateTime publishAt;
    private LocalDateTime revokedAt;
    private LocalDateTime expireAt;
    private Integer viewCount;
    private String extra;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}
