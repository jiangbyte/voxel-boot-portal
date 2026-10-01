package io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.port;

import cn.hutool.core.lang.tree.Tree;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

import java.util.List;

/**
 * 字典查询用例端口：详情、分页、树与按类型列表。
 *
 * Author: Charlie
 */
public interface DictQueryService {

    /**
     * 按主键查询详情。
     */
    DictView detail(String id);

    /**
     * 分页查询字典项。
     */
    PageResult<DictView> page(DictPageQuery query);

    /**
     * 按分类构建字典树。
     */
    List<Tree<String>> tree(String category);

    /**
     * 按类型列出启用项（跨上下文消费）。
     */
    List<DictItem> listByType(String dictType);
}
