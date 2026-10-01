package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.OperationAuditLog;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.adapter.repository.OperationAuditLogRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.audit.OperationAuditLogAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.audit.OperationAuditLogMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.audit.OperationAuditLogPO;
import lombok.RequiredArgsConstructor;

/**
 * 操作审计日志仓储实现。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class OperationAuditLogRepositoryImpl implements OperationAuditLogRepository {

    private final OperationAuditLogMapper mapper;


    @Override
    public void save(OperationAuditLog log) {
        // 1. 转 PO
        OperationAuditLogPO po = OperationAuditLogAssembler.INSTANCE.toPO(log);
        // 2. 插入（审计日志仅追加）
        mapper.insert(po);
    }

    @Override
    public Optional<OperationAuditLog> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(OperationAuditLogAssembler.toDomain(mapper.selectById(id)));
    }
}
