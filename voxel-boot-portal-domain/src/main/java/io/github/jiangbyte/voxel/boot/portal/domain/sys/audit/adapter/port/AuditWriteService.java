package io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.OperationAuditLog;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.adapter.repository.OperationAuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * 操作审计写用例：供 AOP 切面落库成功写操作。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class AuditWriteService {

    private final OperationAuditLogRepository repository;


    /**
     * 持久化一条审计日志。
     */
    @Transactional
    public void append(OperationAuditLog log) {
        // 1. 空对象直接返回
        if (log == null) {
            return;
        }
        // 2. 落库
        repository.save(log);
    }
}
