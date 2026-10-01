package io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository.Repository;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.WeakPassword;

/**
 * 弱密码聚合仓储端口。
 *
 * Author: Charlie
 */
public interface WeakPasswordRepository extends Repository<WeakPassword, String> {

    /**
     * 按口令明文精确查找。
     */
    Optional<WeakPassword> findByPassword(String password);

    /**
     * 删除聚合并派发其已登记的领域事件。
     */
    void remove(WeakPassword aggregate);
}
