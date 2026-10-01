package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.Notice;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.adapter.repository.NoticeRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.notice.NoticeAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.notice.NoticeMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.notice.NoticePO;
import lombok.RequiredArgsConstructor;

/**
 * 公告仓储实现：PO 映射与领域事件派发。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {

    private final NoticeMapper noticeMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<Notice> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(NoticeAssembler.toAggregate(noticeMapper.selectById(id)));
    }

    @Override
    public void save(Notice aggregate) {
        // 1. 聚合转 PO
        NoticePO po = NoticeAssembler.toPO(aggregate);
        // 2. 按主键判断插入或更新
        NoticePO existing = noticeMapper.selectById(po.getId());
        if (existing == null) {
            noticeMapper.insert(po);
        } else {
            noticeMapper.updateById(po);
        }
        // 3. 派发领域事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        noticeMapper.deleteById(id);
    }

    @Override
    public void remove(Notice aggregate) {
        // 1. 删除持久化记录
        if (aggregate == null || !StringUtils.hasText(aggregate.getId())) {
            return;
        }
        noticeMapper.deleteById(aggregate.getId());
        // 2. 派发删除等已登记事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }
}
