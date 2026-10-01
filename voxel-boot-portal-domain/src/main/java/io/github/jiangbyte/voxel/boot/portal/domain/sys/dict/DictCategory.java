package io.github.jiangbyte.voxel.boot.portal.domain.sys.dict;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

/**
 * 字典分类：仅允许 SYS / BIZ。
 *
 * Author: Charlie
 */
public enum DictCategory {

    SYS,
    BIZ;

    /**
     * 解析分类；空值允许（表示未指定），非空则必须落在白名单。
     */
    public static DictCategory from(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String normalized = raw.trim().toUpperCase();
        for (DictCategory category : values()) {
            if (category.name().equals(normalized)) {
                return category;
            }
        }
        throw new DomainException("Dict category must be SYS or BIZ");
    }

    /**
     * 转为持久化/对外字符串；未指定时返回 null。
     */
    public static String toValue(DictCategory category) {
        return category == null ? null : category.name();
    }
}
