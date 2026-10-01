package io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.adapter.port;

import java.time.LocalDateTime;

/**
 * 弱密码只读视图：供查询用例与接口层装配响应。
 *
 * Author: Charlie
 */
public record WeakPasswordView(
        String id,
        String password,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
