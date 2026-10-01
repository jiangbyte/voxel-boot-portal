package io.github.jiangbyte.voxel.boot.portal.api.sys.web.codegen.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 代码生成方案响应。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "代码生成方案响应")
public class SysCodegenPlanResult {
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
