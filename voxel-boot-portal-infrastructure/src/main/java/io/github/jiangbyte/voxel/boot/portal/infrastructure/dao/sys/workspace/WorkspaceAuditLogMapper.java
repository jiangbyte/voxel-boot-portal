package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.workspace;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工作台审计日志只读 Mapper。
 *
 * Author: Charlie
 */
@Mapper
public interface WorkspaceAuditLogMapper extends BaseMapper<WorkspaceAuditLogPO> {
}
