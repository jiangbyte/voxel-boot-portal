package io.github.jiangbyte.voxel.boot.portal.cases.sys.weakpassword;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.WeakPassword;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.adapter.repository.WeakPasswordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 弱密码写用例：创建、更新、删除，编排聚合与仓储。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class WeakPasswordCaseImpl implements IWeakPasswordCase {

    private final WeakPasswordRepository weakPasswordRepository;


    /**
     * 创建弱密码项。
     */
    @Transactional
    public void create(CreateWeakPasswordCommand command) {
        // 1. 校验口令全局唯一
        ensurePasswordUnique(command.password(), null);
        // 2. 工厂创建聚合（含 trim 不变量）
        WeakPassword aggregate = WeakPassword.create(command.password());
        // 3. 持久化并派发领域事件
        weakPasswordRepository.save(aggregate);
    }

    /**
     * 更新弱密码项。
     */
    @Transactional
    public void update(UpdateWeakPasswordCommand command) {
        // 1. 按主键加载聚合，不存在则 404
        WeakPassword aggregate = weakPasswordRepository.findById(command.id())
                .orElseThrow(() -> new DomainException(404, "Weak password not found"));
        // 2. 校验口令未被其他项占用
        ensurePasswordUnique(command.password(), command.id());
        // 3. 聚合内更新并登记事件
        aggregate.update(command.password());
        // 4. 保存
        weakPasswordRepository.save(aggregate);
    }

    /**
     * 批量删除弱密码项。
     */
    @Transactional
    public void delete(List<String> ids) {
        // 1. 空列表直接返回，避免无意义删除
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 逐个加载、登记删除事件后移除（仓储侧派发事件）
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }
            weakPasswordRepository.findById(id).ifPresent(aggregate -> {
                aggregate.markDeleted();
                weakPasswordRepository.remove(aggregate);
            });
        }
    }

    /**
     * 确保口令唯一；excludeId 用于更新时排除自身。
     */
    private void ensurePasswordUnique(String password, String excludeId) {
        if (password == null || password.isBlank()) {
            return;
        }
        weakPasswordRepository.findByPassword(password.trim()).ifPresent(existing -> {
            if (excludeId == null || !excludeId.equals(existing.getId())) {
                throw new DomainException("Weak password already exists");
            }
        });
    }
}
