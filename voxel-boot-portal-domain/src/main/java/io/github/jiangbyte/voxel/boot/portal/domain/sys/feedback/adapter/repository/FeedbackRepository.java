package io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository.Repository;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.Feedback;

/**
 * 反馈聚合仓储端口。
 *
 * Author: Charlie
 */
public interface FeedbackRepository extends Repository<Feedback, String> {

    /**
     * 删除聚合并派发其已登记的领域事件。
     */
    void remove(Feedback aggregate);
}
