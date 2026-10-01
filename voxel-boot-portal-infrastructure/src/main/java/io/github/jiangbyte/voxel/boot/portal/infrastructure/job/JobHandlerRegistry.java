package io.github.jiangbyte.voxel.boot.portal.infrastructure.job;

import io.github.jiangbyte.voxel.boot.portal.domain.job.adapter.port.JobHandler;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.Job;

/**
 * JobHandler SPI 注册表：按真实类 FQCN 索引。
 *
 * Author: Charlie
 */
@Component
public class JobHandlerRegistry {

    private final Map<String, JobHandler> handlerMap;

    public JobHandlerRegistry(ApplicationContext applicationContext) {
        Map<String, JobHandler> handlers = applicationContext.getBeansOfType(JobHandler.class);
        Map<String, JobHandler> indexed = new HashMap<>();
        handlers.values().forEach(handler -> {
            Class<?> userClass = ClassUtils.getUserClass(handler.getClass());
            indexed.putIfAbsent(userClass.getName(), handler);
        });
        this.handlerMap = Map.copyOf(indexed);
    }

    /**
     * 按 handler 标识解析。
     */
    public JobHandler resolve(String handler) {
        JobHandler resolved = handlerMap.get(handler);
        if (resolved == null) {
            throw new DomainException("Job handler not found: " + handler);
        }
        return resolved;
    }
}
