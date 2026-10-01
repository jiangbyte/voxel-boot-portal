package io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.adapter.port;

/**
 * 跨上下文弱密码门面：供认证/改密链路判断口令是否在拒绝列表中。
 *
 * Author: Charlie
 */
public interface WeakPasswordApi {

    /**
     * 判断明文口令是否命中弱密码库。
     */
    boolean contains(String password);
}
