package io.github.jiangbyte.voxel.boot.portal.domain.sys.dict;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.ValueObject;

/**
 * 字典聚合标识值对象。
 *
 * Author: Charlie
 */
public record DictId(String value) implements ValueObject {

    public DictId {
        if (value == null || value.isBlank()) {
            throw new DomainException("Dict id must not be blank");
        }
    }

    /**
     * 由原始字符串构造标识。
     */
    public static DictId of(String value) {
        return new DictId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
