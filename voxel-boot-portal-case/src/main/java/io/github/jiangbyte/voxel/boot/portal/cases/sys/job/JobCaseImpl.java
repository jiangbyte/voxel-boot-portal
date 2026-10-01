package io.github.jiangbyte.voxel.boot.portal.cases.sys.job;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.Job;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobRunner;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobTriggerCalculator;
import lombok.RequiredArgsConstructor;

/**
 * 任务写用例：CRUD、启停与立即执行。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class JobCaseImpl implements IJobCase {

    private final JobRepository jobRepository;
    private final JobTriggerCalculator triggerCalculator;
    private final JobRunner jobRunner;


    /**
     * 创建任务。
     */
    @Transactional
    public void create(CreateJobCommand command) {
        // 1. 校验触发配置
        triggerCalculator.validate(command.triggerType(), command.triggerConfig());
        // 2. 计算首次 nextRunTime
        LocalDateTime next = triggerCalculator.computeNextRunTime(
                command.triggerType(), command.triggerConfig(), LocalDateTime.now());
        // 3. 工厂创建并保存
        Job job = Job.create(
                command.name(),
                command.handler(),
                command.triggerType(),
                command.triggerConfig(),
                command.params(),
                command.description(),
                command.sort(),
                command.enabled(),
                next);
        jobRepository.save(job);
    }

    /**
     * 更新任务。
     */
    @Transactional
    public void update(UpdateJobCommand command) {
        // 1. 加载
        Job job = jobRepository.findById(command.id())
                .orElseThrow(() -> new DomainException(404, "Job not found"));
        // 2. 校验触发配置
        triggerCalculator.validate(command.triggerType(), command.triggerConfig());
        // 3. 触发变更时重算 nextRunTime
        LocalDateTime next = triggerCalculator.computeNextRunTime(
                command.triggerType(), command.triggerConfig(), LocalDateTime.now());
        job.update(
                command.name(),
                command.handler(),
                command.triggerType(),
                command.triggerConfig(),
                command.params(),
                command.description(),
                command.sort(),
                command.enabled(),
                next);
        // 4. 保存
        jobRepository.save(job);
    }

    /**
     * 批量删除。
     */
    @Transactional
    public void delete(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 逐个删除
        for (String id : ids) {
            if (!StringUtils.hasText(id)) {
                continue;
            }
            jobRepository.findById(id).ifPresent(job -> {
                job.markDeleted();
                jobRepository.remove(job);
            });
        }
    }

    /**
     * 启停。
     */
    @Transactional
    public void updateEnabled(String id, Integer enabled) {
        // 1. 加载
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new DomainException(404, "Job not found"));
        // 2. 启用时重算下次执行时间
        LocalDateTime next = Flags.isOn(enabled)
                ? triggerCalculator.computeNextRunTime(job.getTriggerType(), job.getTriggerConfig(), LocalDateTime.now())
                : null;
        job.updateEnabled(enabled, next);
        // 3. 保存
        jobRepository.save(job);
    }

    /**
     * 立即执行（异步）。
     */
    public void runNow(String id, String executorAccountId) {
        // 1. 确认任务存在
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new DomainException(404, "Job not found"));
        String executor = StringUtils.hasText(executorAccountId) ? executorAccountId : "system";
        // 2. 立即执行（同步，保证 run-now 可观测）
        jobRunner.runJob(job.getId(), true, executor);
    }
}
