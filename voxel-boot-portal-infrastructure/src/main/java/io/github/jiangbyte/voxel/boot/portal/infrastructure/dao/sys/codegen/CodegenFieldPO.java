package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.codegen;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 代码生成字段 PO，对应表 sys_codegen_field。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_codegen_field")
public class CodegenFieldPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String planId;
    private String tableRole;
    private String columnName;
    private String label;
    private String dbType;
    private String valueType;
    private String uiType;
    private String widget;
    private String dictCode;
    private String queryOperator;
    private Integer inTable;
    private Integer inForm;
    private Integer inDetail;
    private Integer inQuery;
    private Integer primaryKey;
    private Integer required;
    private Integer uniqueFlag;
    private Integer nullable;
    private Integer maxLength;
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}
