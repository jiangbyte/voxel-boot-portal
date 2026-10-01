package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.codegen;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.CodegenField;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenFieldView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 代码生成字段转换。
 */
@Mapper
public interface CodegenFieldAssembler {

    CodegenFieldAssembler INSTANCE = Mappers.getMapper(CodegenFieldAssembler.class);

    CodegenFieldPO toPO(CodegenField field);


    public static CodegenField toDomain(CodegenFieldPO po) {
        if (po == null) {
            return null;
        }
        return CodegenField.rehydrate(
                po.getId(), po.getPlanId(), po.getTableRole(), po.getColumnName(), po.getLabel(),
                po.getDbType(), po.getValueType(), po.getUiType(), po.getWidget(), po.getDictCode(),
                po.getQueryOperator(), po.getInTable(), po.getInForm(), po.getInDetail(), po.getInQuery(),
                po.getPrimaryKey(), po.getRequired(), po.getUniqueFlag(), po.getNullable(),
                po.getMaxLength(), po.getSort());
    }

    public static CodegenFieldView toView(CodegenFieldPO po) {
        if (po == null) {
            return null;
        }
        return new CodegenFieldView(
                po.getId(), po.getPlanId(), po.getTableRole(), po.getColumnName(), po.getLabel(),
                po.getDbType(), po.getValueType(), po.getUiType(), po.getWidget(), po.getDictCode(),
                po.getQueryOperator(), po.getInTable(), po.getInForm(), po.getInDetail(), po.getInQuery(),
                po.getPrimaryKey(), po.getRequired(), po.getUniqueFlag(), po.getNullable(),
                po.getMaxLength(), po.getSort());
    }
}
