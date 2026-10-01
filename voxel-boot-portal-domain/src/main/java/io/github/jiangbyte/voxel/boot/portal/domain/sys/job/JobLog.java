package io.github.jiangbyte.voxel.boot.portal.domain.sys.job;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 任务执行日志实体。
 *
 * Author: Charlie
 */
public class JobLog {

    private String id;
    private String jobId;
    private Map<String, Object> params;
    private LocalDateTime startedAt;
    private Long durationMs;
    private Integer success;
    private String result;
    private String executor;
    private String ip;
    private String processId;
    private String appDir;
    private LocalDateTime createdAt;

    private JobLog() {
    }

    /**
     * 创建一条执行日志。
     */
    public static JobLog create(String jobId,
                                Map<String, Object> params,
                                LocalDateTime startedAt,
                                long durationMs,
                                Integer success,
                                String result,
                                String executor,
                                String ip,
                                String processId,
                                String appDir) {
        // 1. 分配标识
        JobLog log = new JobLog();
        log.id = UUID.randomUUID().toString().replace("-", "");
        log.createdAt = LocalDateTime.now();
        // 2. 填充执行快照
        log.jobId = jobId;
        log.params = params;
        log.startedAt = startedAt;
        log.durationMs = durationMs;
        log.success = success;
        log.result = result;
        log.executor = executor;
        log.ip = ip;
        log.processId = processId;
        log.appDir = appDir;
        return log;
    }

    public static JobLog rehydrate(String id, String jobId, Map<String, Object> params, LocalDateTime startedAt,
                                   Long durationMs, Integer success, String result, String executor,
                                   String ip, String processId, String appDir, LocalDateTime createdAt) {
        JobLog log = new JobLog();
        log.id = id;
        log.jobId = jobId;
        log.params = params;
        log.startedAt = startedAt;
        log.durationMs = durationMs;
        log.success = success;
        log.result = result;
        log.executor = executor;
        log.ip = ip;
        log.processId = processId;
        log.appDir = appDir;
        log.createdAt = createdAt;
        return log;
    }

    public String getId() { return id; }
    public String getJobId() { return jobId; }
    public Map<String, Object> getParams() { return params; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public Long getDurationMs() { return durationMs; }
    public Integer getSuccess() { return success; }
    public String getResult() { return result; }
    public String getExecutor() { return executor; }
    public String getIp() { return ip; }
    public String getProcessId() { return processId; }
    public String getAppDir() { return appDir; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
