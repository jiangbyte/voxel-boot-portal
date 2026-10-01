package io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port;

/**
 * 方案分页查询。
 *
 * Author: Charlie
 */
public record CodegenPlanPageQuery(long current, long size, String name, String tableName, String genType) {
}
