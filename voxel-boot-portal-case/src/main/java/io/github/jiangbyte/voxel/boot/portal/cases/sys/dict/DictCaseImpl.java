package io.github.jiangbyte.voxel.boot.portal.cases.sys.dict;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.Dict;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.repository.DictRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 字典写用例：创建、更新、删除，编排聚合与仓储。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class DictCaseImpl implements IDictCase {

    private final DictRepository dictRepository;


    /**
     * 创建字典项。
     */
    @Transactional
    public void create(CreateDictCommand command) {
        // 1. 校验编码全局唯一
        ensureCodeUnique(command.code(), null);
        // 2. 工厂创建聚合（含分类/编码不变量）
        Dict dict = Dict.create(
                command.code(),
                command.label(),
                command.value(),
                command.color(),
                command.category(),
                command.parentId(),
                command.status(),
                command.sort());
        // 3. 持久化并派发领域事件
        dictRepository.save(dict);
    }

    /**
     * 更新字典项。
     */
    @Transactional
    public void update(UpdateDictCommand command) {
        // 1. 按主键加载聚合，不存在则 404
        Dict dict = dictRepository.findById(command.id())
                .orElseThrow(() -> new DomainException(404, "Dict not found"));
        // 2. 校验编码未被其他项占用
        ensureCodeUnique(command.code(), command.id());
        // 3. 聚合内更新并登记事件
        dict.update(
                command.code(),
                command.label(),
                command.value(),
                command.color(),
                command.category(),
                command.parentId(),
                command.status(),
                command.sort());
        // 4. 保存
        dictRepository.save(dict);
    }

    /**
     * 批量删除字典项。
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
            dictRepository.findById(id).ifPresent(dict -> {
                dict.markDeleted();
                dictRepository.remove(dict);
            });
        }
    }

    /**
     * 确保编码唯一；excludeId 用于更新时排除自身。
     */
    private void ensureCodeUnique(String code, String excludeId) {
        dictRepository.findByCode(code).ifPresent(existing -> {
            if (excludeId == null || !excludeId.equals(existing.getId())) {
                throw new DomainException("Dict code already exists");
            }
        });
    }
}
