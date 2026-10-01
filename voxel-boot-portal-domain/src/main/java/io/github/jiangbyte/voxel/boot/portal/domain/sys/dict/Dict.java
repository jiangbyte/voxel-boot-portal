package io.github.jiangbyte.voxel.boot.portal.domain.sys.dict;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.event.DictCreated;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.event.DictDeleted;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.event.DictUpdated;

import java.util.UUID;

/**
 * 数据字典聚合根：维护编码、分类与启停等核心不变量。
 *
 * Author: Charlie
 */
public class Dict extends AggregateRoot<String> {

    private String id;
    private String code;
    private String label;
    private String value;
    private String color;
    private DictCategory category;
    private String parentId;
    private DictStatus status;
    private Integer sort;

    private Dict() {
    }

    /**
     * 新建字典项并登记创建事件。
     */
    public static Dict create(String code,
                              String label,
                              String value,
                              String color,
                              String category,
                              String parentId,
                              String status,
                              Integer sort) {
        // 1. 校验编码非空（核心标识）
        String normalizedCode = requireCode(code);
        // 2. 解析分类白名单与状态默认值
        DictCategory parsedCategory = DictCategory.from(category);
        DictStatus parsedStatus = DictStatus.from(status);
        // 3. 组装聚合并分配标识
        Dict dict = new Dict();
        dict.id = UUID.randomUUID().toString().replace("-", "");
        dict.code = normalizedCode;
        dict.label = label;
        dict.value = value;
        dict.color = color;
        dict.category = parsedCategory;
        dict.parentId = blankToNull(parentId);
        dict.status = parsedStatus;
        dict.sort = sort == null ? 0 : sort;
        // 4. 登记领域事件供仓储派发
        dict.registerEvent(new DictCreated(dict.id, dict.code));
        return dict;
    }

    /**
     * 从持久化状态重建聚合（不触发领域事件）。
     */
    public static Dict reconstitute(String id,
                                    String code,
                                    String label,
                                    String value,
                                    String color,
                                    String category,
                                    String parentId,
                                    String status,
                                    Integer sort) {
        Dict dict = new Dict();
        dict.id = id;
        dict.code = code;
        dict.label = label;
        dict.value = value;
        dict.color = color;
        dict.category = DictCategory.from(category);
        dict.parentId = blankToNull(parentId);
        dict.status = DictStatus.from(status);
        dict.sort = sort == null ? 0 : sort;
        return dict;
    }

    /**
     * 更新字典项属性并登记更新事件。
     */
    public void update(String code,
                       String label,
                       String value,
                       String color,
                       String category,
                       String parentId,
                       String status,
                       Integer sort) {
        // 1. 校验编码非空
        this.code = requireCode(code);
        // 2. 解析分类与状态
        this.category = DictCategory.from(category);
        this.status = DictStatus.from(status);
        // 3. 覆盖可变字段
        this.label = label;
        this.value = value;
        this.color = color;
        this.parentId = blankToNull(parentId);
        this.sort = sort == null ? 0 : sort;
        // 4. 登记更新事件
        registerEvent(new DictUpdated(this.id, this.code));
    }

    /**
     * 标记删除并登记删除事件（由应用层在移除前调用）。
     */
    public void markDeleted() {
        registerEvent(new DictDeleted(this.id, this.code));
    }

    private static String requireCode(String code) {
        if (code == null || code.isBlank()) {
            throw new DomainException("Dict code must not be blank");
        }
        return code.trim();
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

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }

    public DictCategory getCategory() {
        return category;
    }

    public String getParentId() {
        return parentId;
    }

    public DictStatus getStatus() {
        return status;
    }

    public Integer getSort() {
        return sort;
    }
}
