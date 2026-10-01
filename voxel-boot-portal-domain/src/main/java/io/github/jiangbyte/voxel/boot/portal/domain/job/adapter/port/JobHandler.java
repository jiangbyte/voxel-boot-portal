package io.github.jiangbyte.voxel.boot.portal.domain.job.adapter.port;

/**
 * 任务处理器 SPI：调度引擎按 handler 名称查找实现并调用。
 * <p>基础设施暂不接入调度器，仅保留端口定义。
 *
 * Author: Charlie
 */
@FunctionalInterface
public interface JobHandler {

    /**
     * 执行任务。
     *
     * @param params 任务参数 JSON；无参数时可为 null
     * @return 执行结果摘要
     */
    String execute(String params) throws Exception;
}
