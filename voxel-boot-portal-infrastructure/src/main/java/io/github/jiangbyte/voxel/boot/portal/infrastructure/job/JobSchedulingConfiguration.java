package io.github.jiangbyte.voxel.boot.portal.infrastructure.job;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobRunner;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.job.JobMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.job.JobPO;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 简易内存调度：扫描到期启用任务并触发执行。
 *
 * Author: Charlie
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class JobSchedulingConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JobSchedulingConfiguration.class);

    private final JobMapper jobMapper;
    private final JobRunner jobRunner;


    /**
     * 每秒扫描到期任务（最多 50 条）。
     */
    @Scheduled(fixedDelayString = "${voxel.job.scan-interval-ms:1000}")
    public void scanDueJobs() {
        // 1. 查询到期启用任务
        LocalDateTime now = LocalDateTime.now();
        List<JobPO> due = jobMapper.selectList(Wrappers.<JobPO>lambdaQuery()
                .eq(JobPO::getEnabled, true)
                .le(JobPO::getNextRunTime, now)
                .orderByAsc(JobPO::getNextRunTime)
                .last("LIMIT 50"));
        // 2. 逐个触发
        for (JobPO job : due) {
            try {
                jobRunner.runJob(job.getId(), false, "system");
            } catch (Exception ex) {
                log.warn("Scheduled job skipped, id={}, msg={}", job.getId(), ex.getMessage());
            }
        }
    }
}
