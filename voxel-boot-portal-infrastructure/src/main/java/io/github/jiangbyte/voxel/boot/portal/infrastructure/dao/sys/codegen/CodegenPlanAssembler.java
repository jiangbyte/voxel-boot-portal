package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.codegen;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.CodegenPlan;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenPlanView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 代码生成方案转换。
 */
@Mapper
public interface CodegenPlanAssembler {

    CodegenPlanAssembler INSTANCE = Mappers.getMapper(CodegenPlanAssembler.class);

    CodegenPlanPO toPO(CodegenPlan plan);


    public static CodegenPlan toAggregate(CodegenPlanPO po) {
        if (po == null) {
            return null;
        }
        return CodegenPlan.rehydrate(
                po.getId(), po.getName(), po.getGenType(), po.getAuthor(), po.getDescription(),
                po.getTableName(), po.getPkColumn(), po.getEntityName(), po.getModulePath(),
                po.getBusinessName(), po.getApiPrefix(), po.getPermissionPrefix(),
                po.getResourceModuleId(), po.getParentResourceId(), po.getMenuName(),
                po.getMenuPath(), po.getComponentPath(), po.getIcon(), po.getSort(),
                po.getTreeParentField(), po.getTreeLabelField(), po.getSubTable(),
                po.getSubPk(), po.getSubForeignKey(), po.getSubEntityName(), po.getSubBusinessName());
    }

    public static CodegenPlanView toView(CodegenPlanPO po) {
        if (po == null) {
            return null;
        }
        return new CodegenPlanView(
                po.getId(), po.getName(), po.getGenType(), po.getAuthor(), po.getDescription(),
                po.getTableName(), po.getPkColumn(), po.getEntityName(), po.getModulePath(),
                po.getBusinessName(), po.getApiPrefix(), po.getPermissionPrefix(),
                po.getResourceModuleId(), po.getParentResourceId(), po.getMenuName(),
                po.getMenuPath(), po.getComponentPath(), po.getIcon(), po.getSort(),
                po.getTreeParentField(), po.getTreeLabelField(), po.getSubTable(),
                po.getSubPk(), po.getSubForeignKey(), po.getSubEntityName(), po.getSubBusinessName(),
                po.getCreatedAt(), po.getUpdatedAt());
    }
}
