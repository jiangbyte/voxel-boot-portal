package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.repository;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.RealNameCaseRecord;

/**
 * 实名工单流水仓储端口。
 *
 * Author: Charlie
 */
public interface RealNameCaseRecordRepository {

    /** 追加一条流水。 */
    void append(RealNameCaseRecord record);
}
