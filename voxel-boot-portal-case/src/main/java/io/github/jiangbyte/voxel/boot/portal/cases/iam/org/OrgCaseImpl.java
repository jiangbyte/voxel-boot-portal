package io.github.jiangbyte.voxel.boot.portal.cases.iam.org;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.Org;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.adapter.repository.OrgRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 组织写用例：创建、更新、删除。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class OrgCaseImpl implements IOrgCase {

    private final OrgRepository orgRepository;


    /**
     * 创建组织。
     */
    @Transactional
    public void create(CreateOrgCommand command) {
        // 1. 若指定父节点则校验存在
        ensureParentExists(command.parentId());
        // 2. 工厂创建聚合
        Org org = Org.create(
                command.parentId(),
                command.masterId(),
                command.deputyMasterId(),
                command.name(),
                command.category(),
                command.sort(),
                command.virtual(),
                command.status(),
                command.extra());
        // 3. 持久化
        orgRepository.save(org);
    }

    /**
     * 更新组织。
     */
    @Transactional
    public void update(UpdateOrgCommand command) {
        // 1. 加载聚合
        Org org = orgRepository.findById(command.id())
                .orElseThrow(() -> new DomainException(404, "Org not found"));
        // 2. 校验父节点（存在且非自身）
        ensureParentExists(command.parentId());
        // 3. 聚合内更新并保存
        org.update(
                command.parentId(),
                command.masterId(),
                command.deputyMasterId(),
                command.name(),
                command.category(),
                command.sort(),
                command.virtual(),
                command.status(),
                command.extra());
        orgRepository.save(org);
    }

    /**
     * 批量删除组织；存在子节点时拒绝删除。
     */
    @Transactional
    public void delete(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 逐个加载；有子组织则拒绝
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }
            orgRepository.findById(id).ifPresent(org -> {
                if (orgRepository.existsByParentId(org.getId())) {
                    throw new DomainException("Org has children and cannot be deleted");
                }
                org.markDeleted();
                orgRepository.remove(org);
            });
        }
    }

    private void ensureParentExists(String parentId) {
        if (parentId == null || parentId.isBlank()) {
            return;
        }
        orgRepository.findById(parentId)
                .orElseThrow(() -> new DomainException(404, "Parent org not found"));
    }
}
