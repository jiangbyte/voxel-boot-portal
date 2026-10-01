package io.github.jiangbyte.voxel.boot.portal.domain.iam.position;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.event.PositionCreated;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.event.PositionDeleted;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.event.PositionUpdated;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

import java.util.UUID;

/**
 * 岗位聚合根：维护名称、类别、所属部门、排序与启用状态等不变量。
 *
 * Author: Charlie
 */
public class Position extends AggregateRoot<String> {

    private String id;
    private String name;
    private String category;
    private String ownerDeptId;
    private Integer sort;
    private Integer virtual;
    private PositionStatus status;
    private String description;
    private String extra;

    private Position() {
    }

    /**
     * 新建岗位并登记创建事件。
     */
    public static Position create(String name,
                                  String category,
                                  String ownerDeptId,
                                  Integer sort,
                                  Integer virtual,
                                  String status,
                                  String description,
                                  String extra) {
        // 1. 校验名称与类别
        String normalizedName = requireName(name);
        String normalizedCategory = requireCategory(category);
        // 2. 解析状态与扩展字段
        PositionStatus positionStatus = PositionStatus.from(status);
        // 3. 组装聚合并分配标识
        Position position = new Position();
        position.id = UUID.randomUUID().toString().replace("-", "");
        position.name = normalizedName;
        position.category = normalizedCategory;
        position.ownerDeptId = blankToNull(ownerDeptId);
        position.sort = sort == null ? 99 : sort;
        position.virtual = Flags.of(virtual);
        position.status = positionStatus;
        position.description = description;
        position.extra = normalizeExtra(extra);
        // 4. 登记领域事件
        position.registerEvent(new PositionCreated(position.id, position.name));
        return position;
    }

    /**
     * 从持久化状态重建聚合（不触发领域事件）。
     */
    public static Position reconstitute(String id,
                                        String name,
                                        String category,
                                        String ownerDeptId,
                                        Integer sort,
                                        Integer virtual,
                                        String status,
                                        String description,
                                        String extra) {
        Position position = new Position();
        position.id = id;
        position.name = name;
        position.category = category;
        position.ownerDeptId = ownerDeptId;
        position.sort = sort == null ? 99 : sort;
        position.virtual = Flags.of(virtual);
        position.status = PositionStatus.from(status);
        position.description = description;
        position.extra = normalizeExtra(extra);
        return position;
    }

    /**
     * 更新岗位属性并登记更新事件。
     */
    public void update(String name,
                       String category,
                       String ownerDeptId,
                       Integer sort,
                       Integer virtual,
                       String status,
                       String description,
                       String extra) {
        // 1. 校验名称与类别
        this.name = requireName(name);
        this.category = requireCategory(category);
        // 2. 覆盖可变字段
        this.ownerDeptId = blankToNull(ownerDeptId);
        this.sort = sort == null ? 99 : sort;
        this.virtual = Flags.of(virtual);
        this.status = PositionStatus.from(status);
        this.description = description;
        this.extra = normalizeExtra(extra);
        // 3. 登记更新事件
        registerEvent(new PositionUpdated(this.id, this.name));
    }

    /**
     * 标记删除。
     */
    public void markDeleted() {
        registerEvent(new PositionDeleted(this.id, this.name));
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Position name must not be blank");
        }
        return name.trim();
    }

    private static String requireCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new DomainException("Position category must not be blank");
        }
        return category.trim();
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

    public String getCategory() {
        return category;
    }

    public String getOwnerDeptId() {
        return ownerDeptId;
    }

    public Integer getSort() {
        return sort;
    }

    public Integer getVirtual() {
        return virtual;
    }

    public PositionStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getExtra() {
        return extra;
    }
}
