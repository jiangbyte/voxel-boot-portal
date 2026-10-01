package io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port;

/**
 * 任务执行端口：按 handler 解析 SPI 并写执行日志。
 *
 * Author: Charlie
 */
public interface JobRunner {

    /**
     * 执行任务。
     *
     * @param jobId    任务 ID
     * @param force    true 表示人工立即执行，跳过到期校验
     * @param executor 执行人账号或 system
     */
    void runJob(String jobId, boolean force, String executor);
}
