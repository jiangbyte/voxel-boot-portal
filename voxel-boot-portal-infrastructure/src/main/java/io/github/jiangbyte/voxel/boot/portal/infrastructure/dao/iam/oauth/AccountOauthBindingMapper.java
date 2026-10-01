package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.oauth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * OAuth 绑定 Mapper。
 *
 * Author: Charlie
 */
@Mapper
public interface AccountOauthBindingMapper extends BaseMapper<AccountOauthBindingPO> {
}
