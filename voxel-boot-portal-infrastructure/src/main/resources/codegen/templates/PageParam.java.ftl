package ${paramPackage};

<#if businessName?? && businessName?has_content>
/**
 * ${businessName}分页查询入参。
 *
 * Author: ${author}
 */
<#else>
/**
 * 分页查询入参。
 *
 * Author: ${author}
 */
</#if>

import github.jiangbyte.io.common.core.domain.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = <#if businessName?? && businessName?has_content>"${businessName}分页查询入参。"<#else>"分页查询入参。"</#if>)
@Data
@EqualsAndHashCode(callSuper = true)
public class ${entityName}PageParam extends PageQuery {
<#list queryFields as field>
<#assign fieldDesc = field.comment!field.propertyName>
    @Schema(description = ${fieldDesc?json_string})
    private ${field.javaType} ${field.propertyName};
</#list>
}
