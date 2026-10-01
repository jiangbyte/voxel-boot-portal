package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.RealNameCaseRecord;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.repository.RealNameCaseRecordRepository;
import org.springframework.stereotype.Repository;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.identity.RealNameCaseRecordMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.identity.RealNameCaseRecordPO;
import lombok.RequiredArgsConstructor;

/**
 * 实名工单流水仓储实现。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class RealNameCaseRecordRepositoryImpl implements RealNameCaseRecordRepository {

    private final RealNameCaseRecordMapper realNameCaseRecordMapper;


    @Override
    public void append(RealNameCaseRecord record) {
        RealNameCaseRecordPO po = new RealNameCaseRecordPO();
        po.setRecordId(record.getRecordId());
        po.setCaseId(record.getCaseId());
        po.setAccountId(record.getAccountId());
        po.setBusinessType(record.getBusinessType());
        po.setAction(record.getAction());
        po.setStatusBefore(record.getStatusBefore());
        po.setStatusAfter(record.getStatusAfter());
        po.setVerifyChannel(record.getVerifyChannel());
        po.setProvider(record.getProvider());
        po.setOperatorId(record.getOperatorId());
        po.setDeptId(record.getDeptId());
        po.setRemark(record.getRemark());
        po.setCreatedAt(record.getCreatedAt());
        realNameCaseRecordMapper.insert(po);
    }
}
