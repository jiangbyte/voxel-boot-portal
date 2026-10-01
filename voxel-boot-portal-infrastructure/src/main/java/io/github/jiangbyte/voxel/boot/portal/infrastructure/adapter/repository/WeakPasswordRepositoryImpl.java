package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.WeakPassword;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.adapter.repository.WeakPasswordRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.weakpassword.WeakPasswordAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.weakpassword.WeakPasswordMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.weakpassword.WeakPasswordPO;
import lombok.RequiredArgsConstructor;

/**
 * 弱密码聚合仓储实现：PO 映射与领域事件派发。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class WeakPasswordRepositoryImpl implements WeakPasswordRepository {

    private final WeakPasswordMapper weakPasswordMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<WeakPassword> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(WeakPasswordAssembler.toAggregate(weakPasswordMapper.selectById(id)));
    }

    @Override
    public Optional<WeakPassword> findByPassword(String password) {
        if (!StringUtils.hasText(password)) {
            return Optional.empty();
        }
        WeakPasswordPO po = weakPasswordMapper.selectOne(Wrappers.<WeakPasswordPO>lambdaQuery()
                .eq(WeakPasswordPO::getPassword, password.trim())
                .last("LIMIT 1"));
        return Optional.ofNullable(WeakPasswordAssembler.toAggregate(po));
    }

    @Override
    public void save(WeakPassword aggregate) {
        // 1. 聚合转 PO
        WeakPasswordPO po = WeakPasswordAssembler.INSTANCE.toPO(aggregate);
        // 2. 按主键判断插入或更新
        WeakPasswordPO existing = weakPasswordMapper.selectById(po.getId());
        if (existing == null) {
            weakPasswordMapper.insert(po);
        } else {
            weakPasswordMapper.updateById(po);
        }
        // 3. 取出并派发领域事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        weakPasswordMapper.deleteById(id);
    }

    @Override
    public void remove(WeakPassword aggregate) {
        // 1. 删除持久化记录
        if (aggregate == null || !StringUtils.hasText(aggregate.getId())) {
            return;
        }
        weakPasswordMapper.deleteById(aggregate.getId());
        // 2. 派发删除等已登记事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }
}
