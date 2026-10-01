package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.relation;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * IAM 关系持久化对象，对应表 sys_iam_relation。
 *
 * Author: Charlie
 */
@Data
@TableName(value = "sys_iam_relation", autoResultMap = true)
public class IamRelationPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String subjectType;
    private String subjectId;
    private String accountType;
    private String relationType;
    private String targetType;
    private String targetId;
    private String targetKey;
    private String grantMode;
    private String dataScope;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> customScopeDeptIds;

    private Integer isPrimary;
    private Integer sort;
    private String status;
    private String description;
    private String reason;
    private LocalDateTime expiredAt;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> extra;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}
