package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.notice;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公告已读 Mapper。
 *
 * Author: Charlie
 */
@Mapper
public interface NoticeReadMapper extends BaseMapper<NoticeReadPO> {
}
