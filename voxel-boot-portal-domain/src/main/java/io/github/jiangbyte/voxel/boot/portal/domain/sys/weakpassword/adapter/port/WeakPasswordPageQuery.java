package io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.adapter.port;

/**
 * 弱密码分页查询条件（应用层，不依赖接口入参类型）。
 *
 * Author: Charlie
 */
public record WeakPasswordPageQuery(
        long current,
        long size,
        String password,
        String keyword
) {
}
