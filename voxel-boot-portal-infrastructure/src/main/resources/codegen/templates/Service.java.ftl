package ${basePackage}.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import github.jiangbyte.io.common.core.param.IdsParam;
import ${basePackage}.entity.${entityName};
import ${paramPackage}.${entityName}AddParam;
import ${paramPackage}.${entityName}EditParam;
import ${paramPackage}.${entityName}PageParam;
<#if hasSub && !isSubEntity && subEntityName??>
import ${basePackage}.entity.${subEntityName};
import ${paramPackage}.${subEntityName}AddParam;
import ${paramPackage}.${subEntityName}EditParam;
import ${paramPackage}.${subEntityName}PageParam;
</#if>
<#if hasTree && !isSubEntity>
import cn.hutool.core.lang.tree.Tree;
</#if>

import java.util.List;

/**
 * ${entityName} 服务接口：CRUD<#if hasTree && !isSubEntity>与树查询</#if><#if hasSub && !isSubEntity && subEntityName??>与子实体维护</#if>。
 *
 * Author: ${author}
 */
public interface ${entityName}Service extends IService<${entityName}> {

    /** 创建。 */
    void create(${entityName}AddParam param);

    /** 更新。 */
    void update(${entityName}EditParam param);

    /** 批量删除。 */
    void delete(IdsParam param);

    /** 查询详情。 */
    ${entityName} detail(String id);

    /** 分页查询。 */
    Page<${entityName}> page(${entityName}PageParam param);
<#if hasTree && !isSubEntity>

    /** 树形查询。 */
    List<Tree<String>> tree(String keyword);
</#if>
<#if hasSub && !isSubEntity && subEntityName??>

    /** 创建子实体。 */
    void childCreate(${subEntityName}AddParam param);

    /** 更新子实体。 */
    void childUpdate(${subEntityName}EditParam param);

    /** 删除子实体。 */
    void childDelete(IdsParam param);

    /** 查询子实体详情。 */
    ${subEntityName} childDetail(String id);

    /** 分页查询子实体。 */
    Page<${subEntityName}> childPage(${subEntityName}PageParam param);
</#if>
}