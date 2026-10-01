package ${basePackage}.entity;

<#if businessName?? && businessName?has_content>
/**
 * ${businessName}实体，对应表 {@code ${tableName}}。
 *
 * Author: ${author}
 */
<#else>
/**
 * 实体，对应表 {@code ${tableName}}。
 *
 * Author: ${author}
 */
</#if>

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableName;
<#if hasJson || (hasTree && !isSubEntity)>
import com.baomidou.mybatisplus.annotation.TableField;
</#if>
<#if hasJson>
import github.jiangbyte.io.common.mybatis.handler.JacksonJsonTypeHandler;
</#if>
import github.jiangbyte.io.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
<#list imports as imp>
import ${imp};
</#list>
<#if hasTree && !isSubEntity>
import java.util.ArrayList;
import java.util.List;
</#if>

@Schema(description = <#if businessName?? && businessName?has_content>"${businessName}实体，对应表 ${tableName}。"<#else>"实体，对应表 ${tableName}。"</#if>)
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(<#if hasJson>value = "${tableName}", autoResultMap = true<#else>"${tableName}"</#if>)
public class ${entityName} extends BaseEntity {
<#list fields as field>
<#assign fieldDesc = field.comment!field.propertyName>
<#if field.isJson>
    @TableField(typeHandler = JacksonJsonTypeHandler.class)
</#if>
    @Schema(description = ${fieldDesc?json_string})
    private ${field.javaType} ${field.propertyName};
</#list>
<#if hasTree && !isSubEntity>

    @TableField(exist = false)
    @Schema(description = "子节点列表")
    private List<${entityName}> children = new ArrayList<>();
</#if>
}
