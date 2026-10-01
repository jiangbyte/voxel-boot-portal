package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.weakpassword;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.adapter.port.WeakPasswordApi;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import lombok.RequiredArgsConstructor;

/**
 * 跨上下文弱密码 API 适配：按明文精确匹配是否在拒绝列表中。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class WeakPasswordApiProvider implements WeakPasswordApi {

    private final WeakPasswordMapper weakPasswordMapper;


    @Override
    public boolean contains(String password) {
        // 1. 空口令视为未命中，避免无意义查询
        if (!StringUtils.hasText(password)) {
            return false;
        }
        // 2. 按 trim 后明文精确计数判断是否存在
        Long count = weakPasswordMapper.selectCount(Wrappers.<WeakPasswordPO>lambdaQuery()
                .eq(WeakPasswordPO::getPassword, password.trim()));
        return count != null && count > 0;
    }
}
