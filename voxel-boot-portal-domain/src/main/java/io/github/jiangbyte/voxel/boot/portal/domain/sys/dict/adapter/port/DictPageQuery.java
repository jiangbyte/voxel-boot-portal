package io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.port;

/**
 * 字典分页查询条件（应用层，不依赖接口入参类型）。
 *
 * Author: Charlie
 */
public record DictPageQuery(
        long current,
        long size,
        String code,
        String category,
        String parentId,
        String status
) {
}
