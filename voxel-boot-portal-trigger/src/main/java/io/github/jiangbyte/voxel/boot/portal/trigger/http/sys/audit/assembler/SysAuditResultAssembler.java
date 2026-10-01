package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.audit.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.audit.result.SysAuditResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.adapter.port.AuditView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 AuditView 装配为 API 响应 SysAuditResult。
 */
@Mapper
public interface SysAuditResultAssembler {

    SysAuditResultAssembler INSTANCE = Mappers.getMapper(SysAuditResultAssembler.class);

    SysAuditResult toResult(AuditView view);
}
