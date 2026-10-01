package io.github.jiangbyte.voxel.boot.portal.domain.iam.resource;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

import java.util.Locale;
import java.util.Set;

/**
 * 资源类型：菜单/按钮/API 等。
 *
 * Author: Charlie
 */
public enum ResourceType {

    CATALOG,
    MENU,
    PAGE,
    BUTTON,
    ACTION,
    API_GROUP,
    API;

    private static final Set<String> NAMES = Set.of(
            "CATALOG", "MENU", "PAGE", "BUTTON", "ACTION", "API_GROUP", "API");

    /**
     * 解析资源类型；空值默认 MENU。
     */
    public static ResourceType from(String raw) {
        if (raw == null || raw.isBlank()) {
            return MENU;
        }
        String normalized = raw.trim().toUpperCase(Locale.ROOT);
        if (!NAMES.contains(normalized)) {
            throw new DomainException("Resource type is invalid");
        }
        return ResourceType.valueOf(normalized);
    }
}
