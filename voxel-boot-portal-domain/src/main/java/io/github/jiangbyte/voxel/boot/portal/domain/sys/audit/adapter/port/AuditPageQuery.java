package io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.adapter.port;

/**
 * 操作审计分页查询条件。
 *
 * Author: Charlie
 */
public record AuditPageQuery(
        long current,
        long size,
        String module,
        String action,
        String excludeAction,
        String accountId,
        Integer success
) {
}
