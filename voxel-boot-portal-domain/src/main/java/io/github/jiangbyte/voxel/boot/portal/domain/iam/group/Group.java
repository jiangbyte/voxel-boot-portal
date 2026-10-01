package io.github.jiangbyte.voxel.boot.portal.domain.iam.group;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.event.GroupCreated;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.event.GroupDeleted;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.event.GroupUpdated;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

import java.util.UUID;

/**
 * 用户组聚合根：维护名称、所属部门与启用状态等不变量。
 *
 * Author: Charlie
 */
public class Group extends AggregateRoot<String> {

    private String id;
    private String name;
    private String ownerDeptId;
    private String description;
    private GroupStatus status;
    private String extra;

    private Group() {
    }

    /**
     * 新建用户组并登记创建事件。
     */
    public static Group create(String name, String ownerDeptId, String description, String status, String extra) {
        // 1. 校验名称
        String normalizedName = requireName(name);
        // 2. 解析状态与扩展
        GroupStatus groupStatus = GroupStatus.from(status);
        String normalizedExtra = normalizeExtra(extra);
        // 3. 组装聚合并分配标识
        Group group = new Group();
        group.id = UUID.randomUUID().toString().replace("-", "");
        group.name = normalizedName;
        group.ownerDeptId = blankToNull(ownerDeptId);
        group.description = description;
        group.status = groupStatus;
        group.extra = normalizedExtra;
        // 4. 登记领域事件
        group.registerEvent(new GroupCreated(group.id, group.name));
        return group;
    }

    /**
     * 从持久化状态重建聚合（不触发领域事件）。
     */
    public static Group reconstitute(String id, String name, String ownerDeptId, String description, String status, String extra) {
        Group group = new Group();
        group.id = id;
        group.name = name;
        group.ownerDeptId = ownerDeptId;
        group.description = description;
        group.status = GroupStatus.from(status);
        group.extra = normalizeExtra(extra);
        return group;
    }

    /**
     * 更新用户组属性并登记更新事件。
     */
    public void update(String name, String ownerDeptId, String description, String status, String extra) {
        // 1. 校验名称
        this.name = requireName(name);
        // 2. 覆盖可变字段
        this.ownerDeptId = blankToNull(ownerDeptId);
        this.description = description;
        this.status = GroupStatus.from(status);
        this.extra = normalizeExtra(extra);
        // 3. 登记更新事件
        registerEvent(new GroupUpdated(this.id, this.name));
    }

    /**
     * 标记删除。
     */
    public void markDeleted() {
        registerEvent(new GroupDeleted(this.id, this.name));
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Group name must not be blank");
        }
        return name.trim();
    }

    private static String normalizeExtra(String extra) {
        return extra == null || extra.isBlank() ? "{}" : extra.trim();
    }

    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwnerDeptId() {
        return ownerDeptId;
    }

    public String getDescription() {
        return description;
    }

    public GroupStatus getStatus() {
        return status;
    }

    public String getExtra() {
        return extra;
    }
}
