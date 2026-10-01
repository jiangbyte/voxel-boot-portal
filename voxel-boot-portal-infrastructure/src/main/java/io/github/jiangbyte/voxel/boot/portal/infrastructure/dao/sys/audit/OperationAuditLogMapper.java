package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.audit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作审计日志 Mapper。
 *
 * Author: Charlie
 */
@Mapper
public interface OperationAuditLogMapper extends BaseMapper<OperationAuditLogPO> {
}
