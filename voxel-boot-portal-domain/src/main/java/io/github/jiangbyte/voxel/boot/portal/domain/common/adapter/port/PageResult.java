package io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port;

import java.util.List;

/**
 * 应用层分页结果，与基础设施 ORM 分页类型解耦。
 *
 * Author: Charlie
 */
public record PageResult<T>(List<T> records, long total, long current, long size) {
}
