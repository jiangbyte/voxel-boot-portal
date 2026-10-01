package io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port;

/**
 * 任务日志分页查询条件。
 *
 * Author: Charlie
 */
public record JobLogPageQuery(long current, long size, String jobId, Integer success) {
}
