package io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

/**
 * 操作审计查询端口。
 *
 * Author: Charlie
 */
public interface AuditQueryService {

    PageResult<AuditView> page(AuditPageQuery query);

    PageResult<AuditView> myPage(String accountId, AuditPageQuery query);

    AuditView detail(String id);

    AuditView myDetail(String id, String accountId);
}
