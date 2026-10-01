package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.banner;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;

/**
 * Banner 表 MyBatis-Plus Mapper。
 *
 * Author: Charlie
 */
@Mapper
public interface BannerMapper extends BaseMapper<BannerPO> {
}
