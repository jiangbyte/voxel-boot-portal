package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.audit;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.OperationAuditLog;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.adapter.port.AuditView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 操作审计日志聚合/视图与 PO 转换。
 */
@Mapper
public interface OperationAuditLogAssembler {

    OperationAuditLogAssembler INSTANCE = Mappers.getMapper(OperationAuditLogAssembler.class);

    OperationAuditLogPO toPO(OperationAuditLog log);


    public static OperationAuditLog toDomain(OperationAuditLogPO po) {
        if (po == null) {
            return null;
        }
        return OperationAuditLog.rehydrate(
                po.getId(), po.getModule(), po.getResourceType(), po.getResourceId(),
                po.getAction(), po.getSummary(), po.getBeforeData(), po.getAfterData(),
                po.getAccountId(), po.getAccountType(), po.getRequestId(), po.getIp(),
                po.getUserAgent(), po.getSuccess(), po.getErrorMessage(), po.getOperatorName(),
                po.getActionName(), po.getActionType(), po.getModuleLabel(), po.getDurationMs(),
                po.getCreatedAt());
    }

    public static AuditView toView(OperationAuditLogPO po) {
        if (po == null) {
            return null;
        }
        return new AuditView(
                po.getId(), po.getModule(), po.getResourceType(), po.getResourceId(),
                po.getAction(), po.getSummary(), po.getBeforeData(), po.getAfterData(),
                po.getAccountId(), po.getAccountType(), po.getRequestId(), po.getIp(),
                po.getUserAgent(), po.getSuccess(), po.getErrorMessage(), po.getOperatorName(),
                po.getActionName(), po.getActionType(), po.getModuleLabel(), po.getDurationMs(),
                po.getCreatedAt());
    }
}
