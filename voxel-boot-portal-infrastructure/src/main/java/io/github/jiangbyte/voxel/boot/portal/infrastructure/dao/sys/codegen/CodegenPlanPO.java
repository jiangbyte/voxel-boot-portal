package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.codegen;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 代码生成方案 PO，对应表 sys_codegen_plan。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_codegen_plan")
public class CodegenPlanPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String name;
    private String genType;
    private String author;
    private String description;
    private String tableName;
    private String pkColumn;
    private String entityName;
    private String modulePath;
    private String businessName;
    private String apiPrefix;
    private String permissionPrefix;
    private String resourceModuleId;
    private String parentResourceId;
    private String menuName;
    private String menuPath;
    private String componentPath;
    private String icon;
    private Integer sort;
    private String treeParentField;
    private String treeLabelField;
    private String subTable;
    private String subPk;
    private String subForeignKey;
    private String subEntityName;
    private String subBusinessName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}
