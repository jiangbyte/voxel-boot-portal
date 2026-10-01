package ${basePackage}.convert;

<#if businessName?? && businessName?has_content>
/**
 * ${businessName} MapStruct 转换：入参与实体映射。
 *
 * Author: ${author}
 */
<#else>
/**
 * MapStruct 转换：入参与实体映射。
 *
 * Author: ${author}
 */
</#if>

import ${basePackage}.entity.${entityName};
import ${paramPackage}.${entityName}AddParam;
import ${paramPackage}.${entityName}EditParam;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ${entityName}Convert {

    /** 新增入参转实体。 */
    ${entityName} toEntity(${entityName}AddParam param);

    /** 编辑入参更新到实体。 */
    void update(${entityName}EditParam param, @MappingTarget ${entityName} entity);
}
