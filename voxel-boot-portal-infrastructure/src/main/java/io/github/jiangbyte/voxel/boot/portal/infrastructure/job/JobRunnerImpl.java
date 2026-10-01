package io.github.jiangbyte.voxel.boot.portal.infrastructure.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jiangbyte.voxel.boot.portal.domain.job.adapter.port.JobHandler;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobRunner;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobTriggerCalculator;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.Job;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.JobLog;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.repository.JobLogRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;

/**
 * 任务执行器：解析 JobHandler SPI，更新任务状态并写执行日志。
 *
 * Author: Charlie
 */
@Component
@RequiredArgsConstructor
public class JobRunnerImpl implements JobRunner {

    private static final Logger log = LoggerFactory.getLogger(JobRunnerImpl.class);
    private static final String IP = resolveLocalIp();
    private static final String PROCESS_ID = String.valueOf(ProcessHandle.current().pid());
    private static final String APP_DIR = System.getProperty("user.dir", "");

    private final JobRepository jobRepository;
    private final JobLogRepository jobLogRepository;
    private final JobHandlerRegistry handlerRegistry;
    private final JobTriggerCalculator triggerCalculator;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;
    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();


    @Override
    public void runJob(String jobId, boolean force, String executor) {
        // 1. 进程内串行化同一任务
        Object lock = locks.computeIfAbsent(jobId, id -> new Object());
        synchronized (lock) {
            doRun(jobId, force, executor);
        }
    }

    private void doRun(String jobId, boolean force, String executor) {
        // 1. 加载并校验启用
        Job job = jobRepository.findById(jobId).orElse(null);
        if (job == null || !java.util.Objects.equals(job.getEnabled(), 1)) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        if (!force && job.getNextRunTime() != null && job.getNextRunTime().isAfter(now)) {
            return;
        }

        // 2. 调用 handler
        LocalDateTime start = now;
        Integer success = 0;
        String result;
        try {
            JobHandler handler = handlerRegistry.resolve(job.getHandler());
            result = handler.execute(paramJson(job.getParams()));
            success = Flags.of(true);
        } catch (Exception ex) {
            log.error("Job execute failed, id={}, name={}", job.getId(), job.getName(), ex);
            result = "failed: " + (ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage());
        }

        // 3. 更新任务并写日志
        long durationMs = Duration.between(start, LocalDateTime.now()).toMillis();
        LocalDateTime nextRunTime = triggerCalculator.computeNextRunTime(
                job.getTriggerType(), job.getTriggerConfig(), LocalDateTime.now());
        String operator = StringUtils.hasText(executor) ? executor : "system";
        Integer finalSuccess = success;
        String finalResult = result;
        transactionTemplate.executeWithoutResult(status -> {
            Job latest = jobRepository.findById(jobId).orElse(null);
            if (latest == null) {
                return;
            }
            latest.markRun(start, nextRunTime, finalResult);
            jobRepository.save(latest);
            jobLogRepository.save(JobLog.create(
                    latest.getId(),
                    latest.getParams(),
                    start,
                    durationMs,
                    finalSuccess,
                    finalResult,
                    operator,
                    IP,
                    PROCESS_ID,
                    APP_DIR));
        });
        log.info("Job done, id={}, name={}, success={}, result={}", job.getId(), job.getName(), success, result);
    }

    private String paramJson(Map<String, Object> param) {
        if (param == null || param.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(param);
        } catch (Exception ex) {
            return param.toString();
        }
    }

    private static String resolveLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {
            return "127.0.0.1";
        }
    }
}
