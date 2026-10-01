package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.codegen;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port.ResourceQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port.ResourceTreeQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenApplicationService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenDatabaseColumnView;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenDatabaseTableView;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenFieldView;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenPlanPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenPlanView;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenPreviewFileView;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenPreviewView;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenRenderer;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenSchemaPort;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.CodegenField;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.repository.CodegenFieldRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.CodegenPlan;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.repository.CodegenPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;

/**
 * 代码生成查询实现：分页、schema、预览与下载。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class CodegenQueryServiceImpl implements CodegenQueryService {

    private static final Set<String> RELATION_TYPES = Set.of("LEFT_TREE_TABLE", "MASTER_DETAIL");

    private final CodegenPlanMapper planMapper;
    private final CodegenFieldMapper fieldMapper;
    private final CodegenPlanRepository planRepository;
    private final CodegenFieldRepository fieldRepository;
    private final CodegenSchemaPort schemaPort;
    private final CodegenRenderer renderer;
    private final CodegenApplicationService codegenApplicationService;
    private final ResourceQueryService resourceQueryService;


    @Override
    public CodegenPlanView detail(String id) {
        CodegenPlanPO po = planMapper.selectById(id);
        if (po == null) {
            throw new DomainException(404, "Codegen plan not found");
        }
        return CodegenPlanAssembler.toView(po);
    }

    @Override
    public PageResult<CodegenPlanView> page(CodegenPlanPageQuery query) {
        Page<CodegenPlanPO> page = planMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<CodegenPlanPO>lambdaQuery()
                        .like(StringUtils.hasText(query.name()), CodegenPlanPO::getName, query.name())
                        .like(StringUtils.hasText(query.tableName()), CodegenPlanPO::getTableName, query.tableName())
                        .eq(StringUtils.hasText(query.genType()), CodegenPlanPO::getGenType, query.genType())
                        .orderByDesc(CodegenPlanPO::getUpdatedAt));
        List<CodegenPlanView> records = page.getRecords().stream().map(CodegenPlanAssembler::toView).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<CodegenDatabaseTableView> tables() {
        return schemaPort.listTables();
    }

    @Override
    public List<CodegenDatabaseColumnView> tableColumns(String tableName) {
        return schemaPort.listColumns(tableName);
    }

    @Override
    public List<CodegenFieldView> fields(String planId, String tableRole) {
        if (planMapper.selectById(planId) == null) {
            throw new DomainException(404, "Codegen plan not found");
        }
        return fieldMapper.selectList(Wrappers.<CodegenFieldPO>lambdaQuery()
                        .eq(CodegenFieldPO::getPlanId, planId)
                        .eq(StringUtils.hasText(tableRole), CodegenFieldPO::getTableRole, tableRole)
                        .orderByAsc(CodegenFieldPO::getTableRole)
                        .orderByAsc(CodegenFieldPO::getSort)
                        .orderByAsc(CodegenFieldPO::getId))
                .stream()
                .map(CodegenFieldAssembler::toView)
                .toList();
    }

    @Override
    public List<Tree<String>> parentResources(String moduleId) {
        // 1. 复用 IAM 资源树（仅菜单）
        return resourceQueryService.tree(new ResourceTreeQuery(moduleId, "ADMIN"));
    }

    @Override
    public CodegenPreviewView preview(String id) {
        // 1. 加载方案
        CodegenPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new DomainException(404, "Codegen plan not found"));
        // 2. 主表字段为空则反射同步
        List<CodegenField> mainFields = fieldRepository.listByPlanId(plan.getId(), "MAIN");
        if (mainFields.isEmpty()) {
            codegenApplicationService.syncReflectedFields(plan);
            mainFields = fieldRepository.listByPlanId(plan.getId(), "MAIN");
        }
        // 3. 关联类型子表
        List<CodegenField> subFields = List.of();
        if (RELATION_TYPES.contains(plan.getGenType()) && StringUtils.hasText(plan.getSubTable())) {
            subFields = fieldRepository.listByPlanId(plan.getId(), "SUB");
            if (subFields.isEmpty()) {
                codegenApplicationService.syncReflectedFields(plan);
                subFields = fieldRepository.listByPlanId(plan.getId(), "SUB");
            }
        }
        // 4. 渲染
        return new CodegenPreviewView(renderer.render(plan, mainFields, subFields));
    }

    @Override
    public byte[] download(String id) {
        // 1. 复用预览
        CodegenPreviewView preview = preview(id);
        // 2. 打 zip
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(bos)) {
            for (CodegenPreviewFileView file : preview.files()) {
                zos.putNextEntry(new ZipEntry(file.path()));
                zos.write(file.content().getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
            zos.finish();
            return bos.toByteArray();
        } catch (Exception ex) {
            throw new DomainException("Failed to build codegen zip: " + ex.getMessage());
        }
    }
}
