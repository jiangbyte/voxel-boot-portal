package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.feedback;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 意见反馈持久化对象，对应表 sys_feedback。
 * <p>attachObjectNames 以 JSON 字符串存储（与 BannerPO.targetAccountTypes 一致）。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_feedback")
public class FeedbackPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String title;

    private String content;

    private String category;

    private String contact;

    private String attachObjectNames;

    private String status;

    private String reply;

    private String repliedBy;

    private LocalDateTime repliedAt;

    private String submitterAccountType;

    private String submitterAccountId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}
