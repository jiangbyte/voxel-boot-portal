package io.github.jiangbyte.voxel.boot.portal.cases.iam.role;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.Role;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.adapter.repository.RoleRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 角色写用例：创建、更新、删除。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class RoleCaseImpl implements IRoleCase {

    private final RoleRepository roleRepository;


    /**
     * 创建角色。
     */
    @Transactional
    public void create(CreateRoleCommand command) {
        // 1. 校验编码唯一
        ensureCodeUnique(command.code(), null);
        // 2. 工厂创建聚合
        Role role = Role.create(
                command.code(),
                command.name(),
                command.category(),
                command.scopeType(),
                command.ownerDeptId(),
                command.sort(),
                command.status(),
                command.builtin(),
                command.description());
        // 3. 持久化
        roleRepository.save(role);
    }

    /**
     * 更新角色。
     */
    @Transactional
    public void update(UpdateRoleCommand command) {
        // 1. 加载聚合
        Role role = roleRepository.findById(command.id())
                .orElseThrow(() -> new DomainException(404, "Role not found"));
        // 2. 校验编码唯一
        ensureCodeUnique(command.code(), command.id());
        // 3. 聚合内更新并保存
        role.update(
                command.code(),
                command.name(),
                command.category(),
                command.scopeType(),
                command.ownerDeptId(),
                command.sort(),
                command.status(),
                command.builtin(),
                command.description());
        roleRepository.save(role);
    }

    /**
     * 批量删除角色。
     */
    @Transactional
    public void delete(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 逐个加载、校验内置后删除
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }
            roleRepository.findById(id).ifPresent(role -> {
                role.markDeleted();
                roleRepository.remove(role);
            });
        }
    }

    private void ensureCodeUnique(String code, String excludeId) {
        roleRepository.findByCode(code).ifPresent(existing -> {
            if (excludeId == null || !excludeId.equals(existing.getId())) {
                throw new DomainException("Role code already exists");
            }
        });
    }
}
