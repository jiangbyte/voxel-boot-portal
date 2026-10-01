package io.github.jiangbyte.voxel.boot.portal.api.sys.web.codegen.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 保存代码生成方案入参。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "保存代码生成方案入参")
public class SysCodegenPlanSaveParam {
    private String id;
    @NotBlank @Size(max = 128)
    private String name;
    @NotBlank @Size(max = 32)
    private String genType = "TABLE";
    @NotBlank @Size(max = 64)
    private String author;
    private String description;
    @NotBlank @Size(max = 128)
    private String tableName;
    @NotBlank @Size(max = 128)
    private String pkColumn = "id";
    @NotBlank @Size(max = 128)
    private String entityName;
    @NotBlank @Size(max = 255)
    private String modulePath;
    @NotBlank @Size(max = 128)
    private String businessName;
    @NotBlank @Size(max = 255)
    private String apiPrefix;
    @NotBlank @Size(max = 128)
    private String permissionPrefix;
    private String resourceModuleId;
    private String parentResourceId;
    @NotBlank @Size(max = 64)
    private String menuName;
    @NotBlank @Size(max = 255)
    private String menuPath;
    @NotBlank @Size(max = 255)
    private String componentPath;
    private String icon;
    private Integer sort = 0;
    private String treeParentField;
    private String treeLabelField;
    private String subTable;
    private String subPk;
    private String subForeignKey;
    private String subEntityName;
    private String subBusinessName;
}
