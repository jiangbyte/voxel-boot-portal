package io.github.jiangbyte.voxel.boot.portal.cases.iam.dept;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.Dept;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.adapter.repository.DeptRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 部门写用例：创建、更新、删除。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class DeptCaseImpl implements IDeptCase {

    private final DeptRepository deptRepository;


    /**
     * 创建部门。
     */
    @Transactional
    public void create(CreateDeptCommand command) {
        // 1. 若指定父节点则校验存在
        ensureParentExists(command.parentId());
        // 2. 工厂创建聚合
        Dept dept = Dept.create(
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
        deptRepository.save(dept);
    }

    /**
     * 更新部门。
     */
    @Transactional
    public void update(UpdateDeptCommand command) {
        // 1. 加载聚合
        Dept dept = deptRepository.findById(command.id())
                .orElseThrow(() -> new DomainException(404, "Dept not found"));
        // 2. 校验父节点（存在且非自身）
        ensureParentExists(command.parentId());
        // 3. 聚合内更新并保存
        dept.update(
                command.parentId(),
                command.masterId(),
                command.deputyMasterId(),
                command.name(),
                command.category(),
                command.sort(),
                command.virtual(),
                command.status(),
                command.extra());
        deptRepository.save(dept);
    }

    /**
     * 批量删除部门；存在子节点时拒绝删除。
     */
    @Transactional
    public void delete(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 逐个加载；有子部门则拒绝
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }
            deptRepository.findById(id).ifPresent(dept -> {
                if (deptRepository.existsByParentId(dept.getId())) {
                    throw new DomainException("Dept has children and cannot be deleted");
                }
                dept.markDeleted();
                deptRepository.remove(dept);
            });
        }
    }

    private void ensureParentExists(String parentId) {
        if (parentId == null || parentId.isBlank()) {
            return;
        }
        deptRepository.findById(parentId)
                .orElseThrow(() -> new DomainException(404, "Parent dept not found"));
    }
}
