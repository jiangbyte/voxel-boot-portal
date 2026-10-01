package io.github.jiangbyte.voxel.boot.portal.trigger.job;

import io.github.jiangbyte.voxel.boot.portal.domain.job.adapter.port.JobHandler;
import org.springframework.stereotype.Component;

/**
 * 样例任务处理器：sys_job.handler 可指向本类 FQCN 以验证 run-now。
 *
 * Author: Charlie
 */
@Component
public class SampleJobHandler implements JobHandler {

    @Override
    public String execute(String params) {
        // 1. 规范化参数
        String payload = params == null || params.isBlank() ? "{}" : params.trim();
        // 2. 返回执行结果摘要
        return "sample-job-ok:" + payload;
    }
}
