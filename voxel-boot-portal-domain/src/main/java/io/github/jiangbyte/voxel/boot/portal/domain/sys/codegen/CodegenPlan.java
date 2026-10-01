package io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.event.CodegenPlanCreated;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.event.CodegenPlanDeleted;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.event.CodegenPlanUpdated;

import java.util.UUID;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Entity;

/**
 * 代码生成方案聚合根。
 *
 * Author: Charlie
 */
public class CodegenPlan extends AggregateRoot<String> {

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

    private CodegenPlan() {
    }

    /**
     * 新建代码生成方案。
     */
    public static CodegenPlan create(String name, String genType, String author, String description,
                                     String tableName, String pkColumn, String entityName, String modulePath,
                                     String businessName, String apiPrefix, String permissionPrefix,
                                     String resourceModuleId, String parentResourceId, String menuName,
                                     String menuPath, String componentPath, String icon, Integer sort,
                                     String treeParentField, String treeLabelField, String subTable,
                                     String subPk, String subForeignKey, String subEntityName,
                                     String subBusinessName) {
        // 1. 校验核心必填
        requireText(name, "Plan name");
        requireText(genType, "Gen type");
        requireText(author, "Author");
        requireText(tableName, "Table name");
        requireText(pkColumn, "PK column");
        requireText(entityName, "Entity name");
        requireText(modulePath, "Module path");
        requireText(businessName, "Business name");
        requireText(apiPrefix, "API prefix");
        requireText(permissionPrefix, "Permission prefix");
        requireText(menuName, "Menu name");
        requireText(menuPath, "Menu path");
        requireText(componentPath, "Component path");
        // 2. 组装
        CodegenPlan plan = new CodegenPlan();
        plan.id = UUID.randomUUID().toString().replace("-", "");
        plan.apply(name, genType, author, description, tableName, pkColumn, entityName, modulePath,
                businessName, apiPrefix, permissionPrefix, resourceModuleId, parentResourceId, menuName,
                menuPath, componentPath, icon, sort, treeParentField, treeLabelField, subTable, subPk,
                subForeignKey, subEntityName, subBusinessName);
        // 3. 登记创建事件
        plan.registerEvent(new CodegenPlanCreated(plan.id));
        return plan;
    }

    /**
     * 更新方案字段。
     */
    public void update(String name, String genType, String author, String description,
                       String tableName, String pkColumn, String entityName, String modulePath,
                       String businessName, String apiPrefix, String permissionPrefix,
                       String resourceModuleId, String parentResourceId, String menuName,
                       String menuPath, String componentPath, String icon, Integer sort,
                       String treeParentField, String treeLabelField, String subTable,
                       String subPk, String subForeignKey, String subEntityName,
                       String subBusinessName) {
        // 1. 校验核心必填
        requireText(name, "Plan name");
        requireText(genType, "Gen type");
        requireText(tableName, "Table name");
        // 2. 写回
        apply(name, genType, author, description, tableName, pkColumn, entityName, modulePath,
                businessName, apiPrefix, permissionPrefix, resourceModuleId, parentResourceId, menuName,
                menuPath, componentPath, icon, sort, treeParentField, treeLabelField, subTable, subPk,
                subForeignKey, subEntityName, subBusinessName);
        // 3. 登记更新事件
        registerEvent(new CodegenPlanUpdated(this.id));
    }

    public void markDeleted() {
        registerEvent(new CodegenPlanDeleted(this.id));
    }

    private void apply(String name, String genType, String author, String description,
                       String tableName, String pkColumn, String entityName, String modulePath,
                       String businessName, String apiPrefix, String permissionPrefix,
                       String resourceModuleId, String parentResourceId, String menuName,
                       String menuPath, String componentPath, String icon, Integer sort,
                       String treeParentField, String treeLabelField, String subTable,
                       String subPk, String subForeignKey, String subEntityName,
                       String subBusinessName) {
        this.name = name.trim();
        this.genType = genType.trim();
        this.author = author == null ? "" : author.trim();
        this.description = blankToNull(description);
        this.tableName = tableName.trim();
        this.pkColumn = pkColumn.trim();
        this.entityName = entityName.trim();
        this.modulePath = modulePath.trim();
        this.businessName = businessName.trim();
        this.apiPrefix = apiPrefix.trim();
        this.permissionPrefix = permissionPrefix.trim();
        this.resourceModuleId = blankToNull(resourceModuleId);
        this.parentResourceId = blankToNull(parentResourceId);
        this.menuName = menuName.trim();
        this.menuPath = menuPath.trim();
        this.componentPath = componentPath.trim();
        this.icon = blankToNull(icon);
        this.sort = sort == null ? 0 : sort;
        this.treeParentField = blankToNull(treeParentField);
        this.treeLabelField = blankToNull(treeLabelField);
        this.subTable = blankToNull(subTable);
        this.subPk = blankToNull(subPk);
        this.subForeignKey = blankToNull(subForeignKey);
        this.subEntityName = blankToNull(subEntityName);
        this.subBusinessName = blankToNull(subBusinessName);
    }

    public static CodegenPlan rehydrate(String id, String name, String genType, String author, String description,
                                        String tableName, String pkColumn, String entityName, String modulePath,
                                        String businessName, String apiPrefix, String permissionPrefix,
                                        String resourceModuleId, String parentResourceId, String menuName,
                                        String menuPath, String componentPath, String icon, Integer sort,
                                        String treeParentField, String treeLabelField, String subTable,
                                        String subPk, String subForeignKey, String subEntityName,
                                        String subBusinessName) {
        CodegenPlan plan = new CodegenPlan();
        plan.id = id;
        plan.name = name;
        plan.genType = genType;
        plan.author = author;
        plan.description = description;
        plan.tableName = tableName;
        plan.pkColumn = pkColumn;
        plan.entityName = entityName;
        plan.modulePath = modulePath;
        plan.businessName = businessName;
        plan.apiPrefix = apiPrefix;
        plan.permissionPrefix = permissionPrefix;
        plan.resourceModuleId = resourceModuleId;
        plan.parentResourceId = parentResourceId;
        plan.menuName = menuName;
        plan.menuPath = menuPath;
        plan.componentPath = componentPath;
        plan.icon = icon;
        plan.sort = sort;
        plan.treeParentField = treeParentField;
        plan.treeLabelField = treeLabelField;
        plan.subTable = subTable;
        plan.subPk = subPk;
        plan.subForeignKey = subForeignKey;
        plan.subEntityName = subEntityName;
        plan.subBusinessName = subBusinessName;
        return plan;
    }

    @Override
    public String getId() { return id; }
    public String getName() { return name; }
    public String getGenType() { return genType; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getTableName() { return tableName; }
    public String getPkColumn() { return pkColumn; }
    public String getEntityName() { return entityName; }
    public String getModulePath() { return modulePath; }
    public String getBusinessName() { return businessName; }
    public String getApiPrefix() { return apiPrefix; }
    public String getPermissionPrefix() { return permissionPrefix; }
    public String getResourceModuleId() { return resourceModuleId; }
    public String getParentResourceId() { return parentResourceId; }
    public String getMenuName() { return menuName; }
    public String getMenuPath() { return menuPath; }
    public String getComponentPath() { return componentPath; }
    public String getIcon() { return icon; }
    public Integer getSort() { return sort; }
    public String getTreeParentField() { return treeParentField; }
    public String getTreeLabelField() { return treeLabelField; }
    public String getSubTable() { return subTable; }
    public String getSubPk() { return subPk; }
    public String getSubForeignKey() { return subForeignKey; }
    public String getSubEntityName() { return subEntityName; }
    public String getSubBusinessName() { return subBusinessName; }

    private static void requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new DomainException(label + " is required");
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
