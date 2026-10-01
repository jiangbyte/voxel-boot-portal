package ${basePackage}.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.jiangbyte.io.common.core.exception.BizException;
import github.jiangbyte.io.common.core.param.IdsParam;
import github.jiangbyte.io.common.mybatis.datasource.ReadDataSource;
import ${basePackage}.convert.${entityName}Convert;
import ${basePackage}.entity.${entityName};
import ${basePackage}.mapper.${entityName}Mapper;
import ${paramPackage}.${entityName}AddParam;
import ${paramPackage}.${entityName}EditParam;
import ${paramPackage}.${entityName}PageParam;
import ${basePackage}.service.${entityName}Service;
<#if hasSub && !isSubEntity && subEntityName??>
import ${basePackage}.convert.${subEntityName}Convert;
import ${basePackage}.entity.${subEntityName};
import ${basePackage}.mapper.${subEntityName}Mapper;
import ${paramPackage}.${subEntityName}AddParam;
import ${paramPackage}.${subEntityName}EditParam;
import ${paramPackage}.${subEntityName}PageParam;
</#if>
<#if hasTree && !isSubEntity>
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
</#if>
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
<#if hasTree && !isSubEntity>
import java.util.Set;
import java.util.stream.Collectors;
</#if>

<#if businessName?? && businessName?has_content>
/**
 * ${businessName}服务实现：维护与查询。
 *
 * Author: ${author}
 */
<#else>
/**
 * ${entityName} 服务实现：维护与查询。
 *
 * Author: ${author}
 */
</#if>
@Service
@RequiredArgsConstructor
public class ${entityName}ServiceImpl extends ServiceImpl<${entityName}Mapper, ${entityName}> implements ${entityName}Service {

    private final ${entityName}Convert ${varName}Convert;
<#if hasSub && !isSubEntity && subEntityName??>
    private final ${subEntityName}Mapper ${subVarName}Mapper;
    private final ${subEntityName}Convert ${subVarName}Convert;
</#if>

    @Override
    @Transactional
    public void create(${entityName}AddParam param) {
        // 入参转实体并持久化
        ${entityName} entity = ${varName}Convert.toEntity(param);
        this.save(entity);
    }

    @Override
    @Transactional
    public void update(${entityName}EditParam param) {
        // 按主键加载
        ${entityName} entity = this.getById(param.getId());
        if (entity == null) {
            // 资源不存在
            throw new BizException(404, "${entityName} not found");
        }
        // 合并编辑入参并更新
        ${varName}Convert.update(param, entity);
        this.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(IdsParam param) {
        if (param.getIds() == null || param.getIds().isEmpty()) {
            return;
        }
        // 批量删除
        this.removeByIds(param.getIds());
    }

    @Override
    @ReadDataSource
    public ${entityName} detail(String id) {
        // 按主键加载
        ${entityName} entity = this.getById(id);
        if (entity == null) {
            // 资源不存在
            throw new BizException(404, "${entityName} not found");
        }
        return entity;
    }

    @Override
    @ReadDataSource
    public Page<${entityName}> page(${entityName}PageParam param) {
        // 组装条件并分页查询
        return this.getBaseMapper().selectPage(new Page<>(param.getCurrent(), param.getSize()),
                Wrappers.<${entityName}>lambdaQuery()
<#list queryFields as field>
<#if ((field.queryOperator)!"EQ") == "LIKE">
                        .like(StringUtils.hasText(param.get${field.propertyName?cap_first}()), ${entityName}::get${field.propertyName?cap_first}, param.get${field.propertyName?cap_first}())
<#else>
                        .eq(param.get${field.propertyName?cap_first}() != null<#if field.javaType == "String"> && StringUtils.hasText(param.get${field.propertyName?cap_first}())</#if>, ${entityName}::get${field.propertyName?cap_first}, param.get${field.propertyName?cap_first}())
</#if>
</#list>
                        .orderByDesc(${entityName}::getCreatedAt));
    }
<#if hasTree && !isSubEntity>

    @Override
    @ReadDataSource
    public List<Tree<String>> tree(String keyword) {
        // 查询节点列表
        List<${entityName}> rows = this.list(Wrappers.<${entityName}>lambdaQuery()
<#if treeLabelProperty?? && treeLabelProperty?has_content>
                .like(StringUtils.hasText(keyword), ${entityName}::get${treeLabelProperty?cap_first}, keyword)
</#if>
                .orderByAsc(${entityName}::getCreatedAt));
        if (rows.isEmpty()) {
            return List.of();
        }

        // 构建树结构
        Set<String> ids = rows.stream().map(${entityName}::getId).collect(Collectors.toSet());
        TreeNodeConfig config = new TreeNodeConfig();
        config.setIdKey("id");
        config.setParentIdKey("parent_id");
        config.setNameKey("name");
        config.setWeightKey("weight");
        config.setChildrenKey("children");
        return TreeUtil.build(rows, null, config, (row, tree) -> {
            String parentId = row.get${treeParentProperty?cap_first}();
            if (!StringUtils.hasText(parentId) || !ids.contains(parentId)) {
                parentId = null;
            }
            BeanUtil.beanToMap(row, false, true).forEach((key, value) -> {
                if (!"children".equals(key)) {
                    tree.putExtra(StrUtil.toUnderlineCase(key), value);
                }
            });
            tree.setId(row.getId());
            tree.setParentId(parentId);
<#if treeLabelProperty?? && treeLabelProperty?has_content>
            tree.setName(row.get${treeLabelProperty?cap_first}());
<#else>
            tree.setName(row.getId());
</#if>
            tree.setWeight(0);
        });
    }
</#if>
<#if hasSub && !isSubEntity && subEntityName??>

    @Override
    @Transactional
    public void childCreate(${subEntityName}AddParam param) {
        // 入参转子实体并插入
        ${subEntityName} entity = ${subVarName}Convert.toEntity(param);
        ${subVarName}Mapper.insert(entity);
    }

    @Override
    @Transactional
    public void childUpdate(${subEntityName}EditParam param) {
        // 按主键加载子实体
        ${subEntityName} entity = ${subVarName}Mapper.selectById(param.getId());
        if (entity == null) {
            // 资源不存在
            throw new BizException(404, "${subEntityName} not found");
        }
        // 合并编辑入参并更新
        ${subVarName}Convert.update(param, entity);
        ${subVarName}Mapper.updateById(entity);
    }

    @Override
    @Transactional
    public void childDelete(IdsParam param) {
        if (param.getIds() == null || param.getIds().isEmpty()) {
            return;
        }
        // 批量删除子实体
        ${subVarName}Mapper.deleteBatchIds(param.getIds());
    }

    @Override
    @ReadDataSource
    public ${subEntityName} childDetail(String id) {
        // 按主键加载子实体
        ${subEntityName} entity = ${subVarName}Mapper.selectById(id);
        if (entity == null) {
            // 资源不存在
            throw new BizException(404, "${subEntityName} not found");
        }
        return entity;
    }

    @Override
    @ReadDataSource
    public Page<${subEntityName}> childPage(${subEntityName}PageParam param) {
        // 按外键分页查询子实体
        return ${subVarName}Mapper.selectPage(new Page<>(param.getCurrent(), param.getSize()),
                Wrappers.<${subEntityName}>lambdaQuery()
                        .eq(StringUtils.hasText(param.get${subForeignProperty?cap_first}()), ${subEntityName}::get${subForeignProperty?cap_first}, param.get${subForeignProperty?cap_first}())
                        .orderByDesc(${subEntityName}::getCreatedAt));
    }
</#if>
}