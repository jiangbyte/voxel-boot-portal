package io.github.jiangbyte.voxel.boot.portal.cases.iam.group;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.Group;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.adapter.repository.GroupRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 用户组写用例：创建、更新、删除。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class GroupCaseImpl implements IGroupCase {

    private final GroupRepository groupRepository;


    /**
     * 创建用户组。
     */
    @Transactional
    public void create(CreateGroupCommand command) {
        // 1. 校验名称唯一
        ensureNameUnique(command.name(), null);
        // 2. 工厂创建聚合
        Group group = Group.create(
                command.name(),
                command.ownerDeptId(),
                command.description(),
                command.status(),
                command.extra());
        // 3. 持久化
        groupRepository.save(group);
    }

    /**
     * 更新用户组。
     */
    @Transactional
    public void update(UpdateGroupCommand command) {
        // 1. 加载聚合
        Group group = groupRepository.findById(command.id())
                .orElseThrow(() -> new DomainException(404, "Group not found"));
        // 2. 校验名称唯一
        ensureNameUnique(command.name(), command.id());
        // 3. 聚合内更新并保存
        group.update(
                command.name(),
                command.ownerDeptId(),
                command.description(),
                command.status(),
                command.extra());
        groupRepository.save(group);
    }

    /**
     * 批量删除用户组。
     */
    @Transactional
    public void delete(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 逐个加载并删除
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }
            groupRepository.findById(id).ifPresent(group -> {
                group.markDeleted();
                groupRepository.remove(group);
            });
        }
    }

    private void ensureNameUnique(String name, String excludeId) {
        groupRepository.findByName(name).ifPresent(existing -> {
            if (excludeId == null || !excludeId.equals(existing.getId())) {
                throw new DomainException("Group name already exists");
            }
        });
    }
}
