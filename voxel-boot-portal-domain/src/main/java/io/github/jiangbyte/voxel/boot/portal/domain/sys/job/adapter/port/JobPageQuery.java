package io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port;

/**
 * 任务分页查询条件。
 *
 * Author: Charlie
 */
public record JobPageQuery(long current, long size, String name, String triggerType, Integer enabled) {
}
