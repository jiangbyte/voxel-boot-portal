package ${paramPackage};

<#if businessName?? && businessName?has_content>
/**
 * 创建${businessName}入参。
 *
 * Author: ${author}
 */
<#else>
/**
 * 创建入参。
 *
 * Author: ${author}
 */
</#if>

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
<#list imports as imp>
import ${imp};
</#list>
<#assign hasNotBlank = false>
<#assign hasNotNull = false>
<#assign hasSize = false>
<#list formFields as field>
<#if field.javaType == "String" && field.isRequired!false>
<#assign hasNotBlank = true>
</#if>
<#if (field.javaType == "Integer" || field.javaType == "Long" || field.javaType == "BigDecimal" || field.javaType == "Boolean") && field.isRequired!false>
<#assign hasNotNull = true>
</#if>
<#if field.javaType == "String" && (field.maxLength?? && field.maxLength > 0)>
<#assign hasSize = true>
</#if>
</#list>
<#if hasNotBlank>
import jakarta.validation.constraints.NotBlank;
</#if>
<#if hasNotNull>
import jakarta.validation.constraints.NotNull;
</#if>
<#if hasSize>
import jakarta.validation.constraints.Size;
</#if>

@Schema(description = <#if businessName?? && businessName?has_content>"创建${businessName}入参。"<#else>"创建入参。"</#if>)
@Data
public class ${entityName}AddParam {
<#list formFields as field>
<#assign fieldDesc = field.comment!field.propertyName>
<#if field.javaType == "String" && field.isRequired!false>
    @NotBlank<#if field.maxLength?? && field.maxLength > 0>
    @Size(max = ${field.maxLength})</#if>
<#elseif (field.javaType == "Integer" || field.javaType == "Long" || field.javaType == "BigDecimal" || field.javaType == "Boolean") && field.isRequired!false>
    @NotNull
</#if>
    @Schema(description = ${fieldDesc?json_string})
    private ${field.javaType} ${field.propertyName};
</#list>
}
