package io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.adapter.repository;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.OperationAuditLog;

/**
 * 操作审计日志仓储端口。
 *
 * Author: Charlie
 */
public interface OperationAuditLogRepository {

    void save(OperationAuditLog log);

    Optional<OperationAuditLog> findById(String id);
}
