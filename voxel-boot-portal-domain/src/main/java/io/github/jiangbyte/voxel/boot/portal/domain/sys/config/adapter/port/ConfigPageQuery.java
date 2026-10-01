package io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port;

/**
 * 配置分页查询。
 *
 * Author: Charlie
 */
public record ConfigPageQuery(long current, long size, String configKey, String category) {
}
