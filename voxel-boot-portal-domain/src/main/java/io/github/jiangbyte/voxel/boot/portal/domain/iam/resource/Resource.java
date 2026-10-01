package io.github.jiangbyte.voxel.boot.portal.domain.iam.resource;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.event.ResourceCreated;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.event.ResourceDeleted;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.event.ResourceUpdated;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

import java.util.UUID;

/**
 * 资源聚合根：维护菜单/按钮/API 编码、类型、树结构与可见性等不变量。
 *
 * Author: Charlie
 */
public class Resource extends AggregateRoot<String> {

    private String id;
    private String parentId;
    private String code;
    private String name;
    private ResourceType resourceType;
    private String moduleId;
    private String path;
    private String component;
    private String redirect;
    private String icon;
    private String color;
    private String href;
    private Integer sort;
    private Integer visible;
    private Integer cache;
    private Integer affix;
    private ResourceStatus status;
    private String description;
    private String layout;
    private String extra;

    private Resource() {
    }

    /**
     * 新建资源并登记创建事件。
     */
    public static Resource create(String parentId,
                                  String code,
                                  String name,
                                  String resourceType,
                                  String moduleId,
                                  String path,
                                  String component,
                                  String redirect,
                                  String icon,
                                  String color,
                                  String href,
                                  Integer sort,
                                  Integer visible,
                                  Integer cache,
                                  Integer affix,
                                  String status,
                                  String description,
                                  String layout,
                                  String extra) {
        // 1. 校验编码与名称
        String normalizedCode = requireCode(code);
        String normalizedName = requireName(name);
        // 2. 解析类型与状态
        ResourceType type = ResourceType.from(resourceType);
        ResourceStatus resourceStatus = ResourceStatus.from(status);
        // 3. 组装聚合并分配标识
        Resource resource = new Resource();
        resource.id = UUID.randomUUID().toString().replace("-", "");
        resource.parentId = blankToNull(parentId);
        resource.code = normalizedCode;
        resource.name = normalizedName;
        resource.resourceType = type;
        resource.moduleId = blankToNull(moduleId);
        resource.path = path;
        resource.component = component;
        resource.redirect = redirect;
        resource.icon = icon;
        resource.color = color;
        resource.href = href;
        resource.sort = sort == null ? 99 : sort;
        resource.visible = visible == null ? 1 : Flags.of(visible);
        resource.cache = Flags.of(cache);
        resource.affix = Flags.of(affix);
        resource.status = resourceStatus;
        resource.description = description;
        resource.layout = layout;
        resource.extra = normalizeExtra(extra);
        // 4. 登记领域事件
        resource.registerEvent(new ResourceCreated(resource.id, resource.code));
        return resource;
    }

    /**
     * 从持久化状态重建聚合（不触发领域事件）。
     */
    public static Resource reconstitute(String id,
                                        String parentId,
                                        String code,
                                        String name,
                                        String resourceType,
                                        String moduleId,
                                        String path,
                                        String component,
                                        String redirect,
                                        String icon,
                                        String color,
                                        String href,
                                        Integer sort,
                                        Integer visible,
                                        Integer cache,
                                        Integer affix,
                                        String status,
                                        String description,
                                        String layout,
                                        String extra) {
        Resource resource = new Resource();
        resource.id = id;
        resource.parentId = parentId;
        resource.code = code;
        resource.name = name;
        resource.resourceType = ResourceType.from(resourceType);
        resource.moduleId = moduleId;
        resource.path = path;
        resource.component = component;
        resource.redirect = redirect;
        resource.icon = icon;
        resource.color = color;
        resource.href = href;
        resource.sort = sort == null ? 99 : sort;
        resource.visible = visible == null ? 1 : Flags.of(visible);
        resource.cache = Flags.of(cache);
        resource.affix = Flags.of(affix);
        resource.status = ResourceStatus.from(status);
        resource.description = description;
        resource.layout = layout;
        resource.extra = normalizeExtra(extra);
        return resource;
    }

    /**
     * 更新资源属性并登记更新事件。
     */
    public void update(String parentId,
                       String code,
                       String name,
                       String resourceType,
                       String moduleId,
                       String path,
                       String component,
                       String redirect,
                       String icon,
                       String color,
                       String href,
                       Integer sort,
                       Integer visible,
                       Integer cache,
                       Integer affix,
                       String status,
                       String description,
                       String layout,
                       String extra) {
        // 1. 禁止将自身设为父节点
        if (parentId != null && parentId.equals(this.id)) {
            throw new DomainException("Resource parentId must not equal itself");
        }
        // 2. 校验并覆盖字段
        this.code = requireCode(code);
        this.name = requireName(name);
        this.resourceType = ResourceType.from(resourceType);
        this.status = ResourceStatus.from(status);
        this.parentId = blankToNull(parentId);
        this.moduleId = blankToNull(moduleId);
        this.path = path;
        this.component = component;
        this.redirect = redirect;
        this.icon = icon;
        this.color = color;
        this.href = href;
        this.sort = sort == null ? 99 : sort;
        this.visible = visible == null ? 1 : Flags.of(visible);
        this.cache = Flags.of(cache);
        this.affix = Flags.of(affix);
        this.description = description;
        this.layout = layout;
        this.extra = normalizeExtra(extra);
        // 3. 登记更新事件
        registerEvent(new ResourceUpdated(this.id, this.code));
    }

    /**
     * 标记删除。
     */
    public void markDeleted() {
        registerEvent(new ResourceDeleted(this.id, this.code));
    }

    private static String requireCode(String code) {
        if (code == null || code.isBlank()) {
            throw new DomainException("Resource code must not be blank");
        }
        return code.trim();
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Resource name must not be blank");
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

    public String getParentId() {
        return parentId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getPath() {
        return path;
    }

    public String getComponent() {
        return component;
    }

    public String getRedirect() {
        return redirect;
    }

    public String getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }

    public String getHref() {
        return href;
    }

    public Integer getSort() {
        return sort;
    }

    public Integer getVisible() {
        return visible;
    }

    public Integer getCache() {
        return cache;
    }

    public Integer getAffix() {
        return affix;
    }

    public ResourceStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getLayout() {
        return layout;
    }

    public String getExtra() {
        return extra;
    }
}
