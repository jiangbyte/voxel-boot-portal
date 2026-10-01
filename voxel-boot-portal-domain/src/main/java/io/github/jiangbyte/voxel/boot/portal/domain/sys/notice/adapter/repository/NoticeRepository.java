package io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository.Repository;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.Notice;

/**
 * 公告聚合仓储端口。
 *
 * Author: Charlie
 */
public interface NoticeRepository extends Repository<Notice, String> {

    /**
     * 删除聚合并派发其已登记的领域事件。
     */
    void remove(Notice aggregate);
}
