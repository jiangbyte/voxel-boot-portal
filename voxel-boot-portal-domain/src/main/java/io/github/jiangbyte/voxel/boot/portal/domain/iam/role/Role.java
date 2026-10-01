package io.github.jiangbyte.voxel.boot.portal.domain.iam.role;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.event.RoleCreated;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.event.RoleDeleted;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.event.RoleUpdated;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 角色聚合根：维护编码、名称、状态与作用域等核心不变量。
 *
 * Author: Charlie
 */
public class Role extends AggregateRoot<String> {

    private static final Set<String> SCOPE_TYPES = Set.of("PLATFORM", "DEPT");

    private String id;
    private String code;
    private String name;
    private String category;
    private String scopeType;
    private String ownerDeptId;
    private Integer sort;
    private RoleStatus status;
    private Integer builtin;
    private String description;

    private Role() {
    }

    /**
     * 新建角色并登记创建事件。
     */
    public static Role create(String code,
                              String name,
                              String category,
                              String scopeType,
                              String ownerDeptId,
                              Integer sort,
                              String status,
                              Integer builtin,
                              String description) {
        // 1. 校验编码与名称
        String normalizedCode = requireCode(code);
        String normalizedName = requireName(name);
        // 2. 解析作用域与状态
        String scope = normalizeScopeType(scopeType);
        RoleStatus roleStatus = RoleStatus.from(status);
        // 3. 组装聚合并分配标识
        Role role = new Role();
        role.id = UUID.randomUUID().toString().replace("-", "");
        role.code = normalizedCode;
        role.name = normalizedName;
        // 库表 category NOT NULL：未传时默认 SYS
        role.category = (category == null || category.isBlank()) ? "SYS" : category.trim();
        role.scopeType = scope;
        role.ownerDeptId = blankToNull(ownerDeptId);
        role.sort = sort == null ? 99 : sort;
        role.status = roleStatus;
        role.builtin = Flags.of(builtin);
        role.description = description;
        // 4. 登记领域事件
        role.registerEvent(new RoleCreated(role.id, role.code));
        return role;
    }

    /**
     * 从持久化状态重建聚合（不触发领域事件）。
     */
    public static Role reconstitute(String id,
                                    String code,
                                    String name,
                                    String category,
                                    String scopeType,
                                    String ownerDeptId,
                                    Integer sort,
                                    String status,
                                    Integer builtin,
                                    String description) {
        Role role = new Role();
        role.id = id;
        role.code = code;
        role.name = name;
        role.category = category;
        role.scopeType = normalizeScopeType(scopeType);
        role.ownerDeptId = ownerDeptId;
        role.sort = sort == null ? 99 : sort;
        role.status = RoleStatus.from(status);
        role.builtin = Flags.of(builtin);
        role.description = description;
        return role;
    }

    /**
     * 更新角色属性并登记更新事件。
     */
    public void update(String code,
                       String name,
                       String category,
                       String scopeType,
                       String ownerDeptId,
                       Integer sort,
                       String status,
                       Integer builtin,
                       String description) {
        // 1. 校验编码与名称
        this.code = requireCode(code);
        this.name = requireName(name);
        // 2. 解析作用域与状态
        this.scopeType = normalizeScopeType(scopeType);
        this.status = RoleStatus.from(status);
        // 3. 覆盖可变字段
        this.category = (category == null || category.isBlank()) ? "SYS" : category.trim();
        this.ownerDeptId = blankToNull(ownerDeptId);
        this.sort = sort == null ? 99 : sort;
        this.builtin = Flags.of(builtin);
        this.description = description;
        // 4. 登记更新事件
        registerEvent(new RoleUpdated(this.id, this.code));
    }

    /**
     * 标记删除；内置角色禁止删除。
     */
    public void markDeleted() {
        if (java.util.Objects.equals(builtin, 1)) {
            throw new DomainException("内置角色不可删除");
        }
        registerEvent(new RoleDeleted(this.id, this.code));
    }

    private static String requireCode(String code) {
        if (code == null || code.isBlank()) {
            throw new DomainException("角色编码不能为空");
        }
        return code.trim();
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("角色名称不能为空");
        }
        return name.trim();
    }

    private static String normalizeScopeType(String scopeType) {
        // 1. 缺省平台级；与字典 ROLE_SCOPE_TYPE 对齐：PLATFORM / DEPT
        if (scopeType == null || scopeType.isBlank()) {
            return "PLATFORM";
        }
        String normalized = scopeType.trim().toUpperCase(Locale.ROOT);
        if (!SCOPE_TYPES.contains(normalized)) {
            throw new DomainException("角色范围类型必须是 PLATFORM 或 DEPT");
        }
        return normalized;
    }

    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getScopeType() {
        return scopeType;
    }

    public String getOwnerDeptId() {
        return ownerDeptId;
    }

    public Integer getSort() {
        return sort;
    }

    public RoleStatus getStatus() {
        return status;
    }

    public Integer getBuiltin() {
        return builtin;
    }

    public String getDescription() {
        return description;
    }
}
