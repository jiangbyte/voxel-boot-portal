package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.Dict;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.repository.DictRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.dict.DictAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.dict.DictMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.dict.DictPO;
import lombok.RequiredArgsConstructor;

/**
 * 字典聚合仓储实现：PO 映射与领域事件派发。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class DictRepositoryImpl implements DictRepository {

    private final DictMapper dictMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<Dict> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(DictAssembler.toAggregate(dictMapper.selectById(id)));
    }

    @Override
    public Optional<Dict> findByCode(String code) {
        if (!StringUtils.hasText(code)) {
            return Optional.empty();
        }
        DictPO po = dictMapper.selectOne(Wrappers.<DictPO>lambdaQuery()
                .eq(DictPO::getCode, code)
                .last("LIMIT 1"));
        return Optional.ofNullable(DictAssembler.toAggregate(po));
    }

    @Override
    public void save(Dict aggregate) {
        // 1. 聚合转 PO
        DictPO po = DictAssembler.INSTANCE.toPO(aggregate);
        // 2. 按主键判断插入或更新
        DictPO existing = dictMapper.selectById(po.getId());
        if (existing == null) {
            dictMapper.insert(po);
        } else {
            dictMapper.updateById(po);
        }
        // 3. 取出并派发领域事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        dictMapper.deleteById(id);
    }

    @Override
    public void remove(Dict aggregate) {
        // 1. 删除持久化记录
        if (aggregate == null || !StringUtils.hasText(aggregate.getId())) {
            return;
        }
        dictMapper.deleteById(aggregate.getId());
        // 2. 派发删除等已登记事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }
}
