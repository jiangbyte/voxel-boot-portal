package io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.port;

import java.time.LocalDateTime;

/**
 * 字典只读视图：供查询用例与接口层装配响应。
 *
 * Author: Charlie
 */
public record DictView(
        String id,
        String code,
        String label,
        String value,
        String color,
        String category,
        String parentId,
        String status,
        Integer sort,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
