package ${paramPackage};

<#if businessName?? && businessName?has_content>
/**
 * 编辑${businessName}入参。
 *
 * Author: ${author}
 */
<#else>
/**
 * 编辑入参。
 *
 * Author: ${author}
 */
</#if>

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
<#list imports as imp>
import ${imp};
</#list>
<#assign hasNotNull = false>
<#list formFields as field>
<#if (field.javaType == "Integer" || field.javaType == "Long" || field.javaType == "BigDecimal" || field.javaType == "Boolean") && field.isRequired!false>
<#assign hasNotNull = true>
</#if>
</#list>
<#if hasNotNull>
import jakarta.validation.constraints.NotNull;
</#if>

@Schema(description = <#if businessName?? && businessName?has_content>"编辑${businessName}入参。"<#else>"编辑入参。"</#if>)
@Data
public class ${entityName}EditParam {

    @NotBlank
    @Size(max = 64)
    @Schema(description = "主键ID")
    private String id;
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
