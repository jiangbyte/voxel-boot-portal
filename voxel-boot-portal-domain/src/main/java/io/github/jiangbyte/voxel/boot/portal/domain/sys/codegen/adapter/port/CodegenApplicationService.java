package io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.CodegenField;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.repository.CodegenFieldRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.CodegenPlan;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.repository.CodegenPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;

/**
 * 代码生成写用例：方案 CRUD、字段批量更新与反射同步。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class CodegenApplicationService {

    private static final Set<String> AUDIT = Set.of("created_at", "created_by", "updated_at", "updated_by");
    private static final Set<String> RELATION_TYPES = Set.of("LEFT_TREE_TABLE", "MASTER_DETAIL");
    private static final Set<String> TREE_TYPES = Set.of("TREE", "LEFT_TREE_TABLE");

    private final CodegenPlanRepository planRepository;
    private final CodegenFieldRepository fieldRepository;
    private final CodegenSchemaPort schemaPort;


    /**
     * 创建方案并同步反射字段。
     */
    @Transactional
    public void create(SaveCodegenPlanCommand command) {
        // 1. 校验表结构与名称唯一
        validatePlan(command);
        if (planRepository.existsByName(command.name(), null)) {
            throw new DomainException("Codegen plan name already exists");
        }
        // 2. 创建聚合
        CodegenPlan plan = CodegenPlan.create(
                command.name(), command.genType(), command.author(), command.description(),
                command.tableName(), command.pkColumn(), command.entityName(), command.modulePath(),
                command.businessName(), command.apiPrefix(), command.permissionPrefix(),
                command.resourceModuleId(), command.parentResourceId(), command.menuName(),
                command.menuPath(), command.componentPath(), command.icon(), command.sort(),
                command.treeParentField(), command.treeLabelField(), command.subTable(),
                command.subPk(), command.subForeignKey(), command.subEntityName(), command.subBusinessName());
        planRepository.save(plan);
        // 3. 同步反射字段
        syncReflectedFields(plan);
    }

    /**
     * 更新方案并同步反射字段。
     */
    @Transactional
    public void update(SaveCodegenPlanCommand command) {
        // 1. 校验
        if (!StringUtils.hasText(command.id())) {
            throw new DomainException("id is required");
        }
        validatePlan(command);
        if (planRepository.existsByName(command.name(), command.id())) {
            throw new DomainException("Codegen plan name already exists");
        }
        // 2. 加载并更新
        CodegenPlan plan = planRepository.findById(command.id())
                .orElseThrow(() -> new DomainException(404, "Codegen plan not found"));
        plan.update(
                command.name(), command.genType(), command.author(), command.description(),
                command.tableName(), command.pkColumn(), command.entityName(), command.modulePath(),
                command.businessName(), command.apiPrefix(), command.permissionPrefix(),
                command.resourceModuleId(), command.parentResourceId(), command.menuName(),
                command.menuPath(), command.componentPath(), command.icon(), command.sort(),
                command.treeParentField(), command.treeLabelField(), command.subTable(),
                command.subPk(), command.subForeignKey(), command.subEntityName(), command.subBusinessName());
        planRepository.save(plan);
        // 3. 同步反射字段
        syncReflectedFields(plan);
    }

    /**
     * 批量删除方案及其字段。
     */
    @Transactional
    public void delete(List<String> ids) {
        // 1. 空列表返回
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 删除字段后删除方案
        List<CodegenPlan> plans = planRepository.findByIds(ids);
        fieldRepository.deleteByPlanIds(ids);
        for (CodegenPlan plan : plans) {
            plan.markDeleted();
            planRepository.remove(plan);
        }
    }

    /**
     * 全量替换方案字段配置。
     */
    @Transactional
    public void updateFieldsBatch(String planId, List<UpdateCodegenFieldItem> items) {
        // 1. 校验方案存在
        planRepository.findById(planId)
                .orElseThrow(() -> new DomainException(404, "Codegen plan not found"));
        // 2. 映射为领域字段
        List<CodegenField> fields = new ArrayList<>();
        if (items != null) {
            for (UpdateCodegenFieldItem item : items) {
                fields.add(CodegenField.create(
                        planId,
                        item.tableRole(),
                        item.columnName(),
                        item.label(),
                        item.dbType(),
                        item.valueType(),
                        item.uiType(),
                        item.widget(),
                        item.dictCode(),
                        item.queryOperator(),
                        item.inTable(),
                        item.inForm(),
                        item.inDetail(),
                        item.inQuery(),
                        item.primaryKey(),
                        item.required(),
                        item.uniqueFlag(),
                        item.nullable(),
                        item.maxLength(),
                        item.sort()));
            }
        }
        // 3. 替换落库
        fieldRepository.replaceAll(planId, fields);
    }

    /**
     * 按库表反射同步字段（供查询预览补齐）。
     */
    @Transactional
    public void syncReflectedFields(CodegenPlan plan) {
        // 1. 同步主表
        upsertReflected(plan.getId(), "MAIN", schemaPort.listColumns(plan.getTableName()));
        // 2. 关联类型再同步子表
        if (RELATION_TYPES.contains(plan.getGenType()) && StringUtils.hasText(plan.getSubTable())) {
            upsertReflected(plan.getId(), "SUB", schemaPort.listColumns(plan.getSubTable()));
        }
    }

    private void validatePlan(SaveCodegenPlanCommand request) {
        // 1. 主表列与主键
        List<CodegenDatabaseColumnView> mainColumns = schemaPort.listColumns(request.tableName());
        Set<String> mainNames = new HashSet<>();
        for (CodegenDatabaseColumnView column : mainColumns) {
            mainNames.add(column.columnName());
        }
        if (!mainNames.contains(request.pkColumn())) {
            throw new DomainException("Primary key field does not exist");
        }
        // 2. 树类型字段
        if (TREE_TYPES.contains(request.genType())) {
            if (!mainNames.contains(request.treeParentField())) {
                throw new DomainException("Tree parent field does not exist");
            }
            if (!mainNames.contains(request.treeLabelField())) {
                throw new DomainException("Tree label field does not exist");
            }
        }
        // 3. 关联子表
        if (RELATION_TYPES.contains(request.genType())) {
            if (!StringUtils.hasText(request.subTable())
                    || !StringUtils.hasText(request.subPk())
                    || !StringUtils.hasText(request.subForeignKey())) {
                throw new DomainException("Sub table configuration is incomplete");
            }
            Set<String> subNames = new HashSet<>();
            for (CodegenDatabaseColumnView column : schemaPort.listColumns(request.subTable())) {
                subNames.add(column.columnName());
            }
            if (!subNames.contains(request.subPk())) {
                throw new DomainException("Sub primary key field does not exist");
            }
            if (!subNames.contains(request.subForeignKey())) {
                throw new DomainException("Sub foreign key field does not exist");
            }
        }
    }

    private void upsertReflected(String planId, String tableRole, List<CodegenDatabaseColumnView> columns) {
        // 1. 已有字段索引
        Map<String, CodegenField> existing = new HashMap<>();
        for (CodegenField field : fieldRepository.listByPlanId(planId, tableRole)) {
            existing.put(field.getColumnName(), field);
        }
        // 2. 按列合并
        List<CodegenField> merged = new ArrayList<>();
        int index = 1;
        for (CodegenDatabaseColumnView column : columns) {
            String columnName = column.columnName();
            boolean isPk = Flags.isOn(column.primaryKey());
            boolean isAudit = AUDIT.contains(columnName);
            Integer nullable = Flags.of(column.nullable());
            CodegenField old = existing.get(columnName);
            String widget = defaultWidget(columnName, column.valueType());
            if (old == null) {
                merged.add(CodegenField.create(
                        planId, tableRole, columnName, column.label(), column.dbType(),
                        column.valueType(), column.uiType(), widget,
                        "status".equals(columnName) ? "COMMON_STATUS" : null,
                        defaultQueryOperator(columnName, column.valueType()),
                        Flags.of(!isAudit), Flags.of(!isPk && !isAudit), Flags.of(true),
                        Flags.of(Set.of("name", "title", "code", "status", "category", "type").contains(columnName)),
                        Flags.of(isPk), Flags.of(Flags.isOff(nullable) && !isPk && !isAudit), Flags.of(false), nullable,
                        column.maxLength(), column.maxLength() == null ? index : index));
            } else {
                merged.add(CodegenField.rehydrate(
                        old.getId(), planId, tableRole, columnName, column.label(), column.dbType(),
                        column.valueType(), column.uiType(),
                        old.getWidget() == null ? widget : old.getWidget(),
                        old.getDictCode(), old.getQueryOperator(),
                        old.getInTable(), old.getInForm(), old.getInDetail(), old.getInQuery(),
                        Flags.of(isPk), Flags.of(Flags.isOff(nullable) && !isPk && !isAudit), old.getUniqueFlag(), nullable,
                        column.maxLength(), old.getSort() == null ? index : old.getSort()));
            }
            index++;
        }
        // 3. 与其他角色字段合并后全量替换（保留另一角色）
        List<CodegenField> otherRole = fieldRepository.listByPlanId(planId, null).stream()
                .filter(f -> !tableRole.equals(f.getTableRole()))
                .toList();
        List<CodegenField> all = new ArrayList<>(otherRole);
        all.addAll(merged);
        fieldRepository.replaceAll(planId, all);
    }

    private static String defaultWidget(String columnName, String valueType) {
        if ("status".equals(columnName)) {
            return "dict";
        }
        if ("int".equals(valueType) || "float".equals(valueType)) {
            return "number";
        }
        if ("bool".equals(valueType)) {
            return "switch";
        }
        if ("datetime".equals(valueType)) {
            return "datetime";
        }
        if (columnName != null && (columnName.contains("content") || columnName.contains("description") || columnName.contains("remark"))) {
            return "textarea";
        }
        return "input";
    }

    private static String defaultQueryOperator(String columnName, String valueType) {
        if ("status".equals(columnName) || "int".equals(valueType) || "bool".equals(valueType)) {
            return "EQ";
        }
        if (Set.of("name", "title", "code", "category", "type").contains(columnName)) {
            return "LIKE";
        }
        return null;
    }
}
