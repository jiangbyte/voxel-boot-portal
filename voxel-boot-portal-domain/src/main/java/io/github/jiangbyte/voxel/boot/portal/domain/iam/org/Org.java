package io.github.jiangbyte.voxel.boot.portal.domain.iam.org;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.event.OrgCreated;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.event.OrgDeleted;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.event.OrgUpdated;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

import java.util.UUID;

/**
 * 组织聚合根：维护组织树节点名称、类别、排序与启用状态等不变量。
 *
 * Author: Charlie
 */
public class Org extends AggregateRoot<String> {

    private String id;
    private String parentId;
    private String masterId;
    private String deputyMasterId;
    private String name;
    private String category;
    private Integer sort;
    private Integer virtual;
    private OrgStatus status;
    private String extra;

    private Org() {
    }

    /**
     * 新建组织并登记创建事件。
     */
    public static Org create(String parentId,
                              String masterId,
                              String deputyMasterId,
                              String name,
                              String category,
                              Integer sort,
                              Integer virtual,
                              String status,
                              String extra) {
        // 1. 校验名称与类别
        String normalizedName = requireName(name);
        String normalizedCategory = requireCategory(category);
        // 2. 解析状态与扩展字段
        OrgStatus orgStatus = OrgStatus.from(status);
        String normalizedExtra = normalizeExtra(extra);
        // 3. 组装聚合并分配标识
        Org org = new Org();
        org.id = UUID.randomUUID().toString().replace("-", "");
        org.parentId = blankToNull(parentId);
        org.masterId = blankToNull(masterId);
        org.deputyMasterId = blankToNull(deputyMasterId);
        org.name = normalizedName;
        org.category = normalizedCategory;
        org.sort = sort == null ? 99 : sort;
        org.virtual = Flags.of(virtual);
        org.status = orgStatus;
        org.extra = normalizedExtra;
        // 4. 登记领域事件
        org.registerEvent(new OrgCreated(org.id, org.name));
        return org;
    }

    /**
     * 从持久化状态重建聚合（不触发领域事件）。
     */
    public static Org reconstitute(String id,
                                    String parentId,
                                    String masterId,
                                    String deputyMasterId,
                                    String name,
                                    String category,
                                    Integer sort,
                                    Integer virtual,
                                    String status,
                                    String extra) {
        Org org = new Org();
        org.id = id;
        org.parentId = parentId;
        org.masterId = masterId;
        org.deputyMasterId = deputyMasterId;
        org.name = name;
        org.category = category;
        org.sort = sort == null ? 99 : sort;
        org.virtual = Flags.of(virtual);
        org.status = OrgStatus.from(status);
        org.extra = normalizeExtra(extra);
        return org;
    }

    /**
     * 更新组织属性并登记更新事件。
     */
    public void update(String parentId,
                       String masterId,
                       String deputyMasterId,
                       String name,
                       String category,
                       Integer sort,
                       Integer virtual,
                       String status,
                       String extra) {
        // 1. 禁止将自身设为父节点
        if (parentId != null && parentId.equals(this.id)) {
            throw new DomainException("Org parentId must not equal itself");
        }
        // 2. 校验名称与类别并覆盖字段
        this.name = requireName(name);
        this.category = requireCategory(category);
        this.parentId = blankToNull(parentId);
        this.masterId = blankToNull(masterId);
        this.deputyMasterId = blankToNull(deputyMasterId);
        this.sort = sort == null ? 99 : sort;
        this.virtual = Flags.of(virtual);
        this.status = OrgStatus.from(status);
        this.extra = normalizeExtra(extra);
        // 3. 登记更新事件
        registerEvent(new OrgUpdated(this.id, this.name));
    }

    /**
     * 标记删除。
     */
    public void markDeleted() {
        registerEvent(new OrgDeleted(this.id, this.name));
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Org name must not be blank");
        }
        return name.trim();
    }

    private static String requireCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new DomainException("Org category must not be blank");
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

    public String getParentId() {
        return parentId;
    }

    public String getMasterId() {
        return masterId;
    }

    public String getDeputyMasterId() {
        return deputyMasterId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public Integer getSort() {
        return sort;
    }

    public Integer getVirtual() {
        return virtual;
    }

    public OrgStatus getStatus() {
        return status;
    }

    public String getExtra() {
        return extra;
    }
}
