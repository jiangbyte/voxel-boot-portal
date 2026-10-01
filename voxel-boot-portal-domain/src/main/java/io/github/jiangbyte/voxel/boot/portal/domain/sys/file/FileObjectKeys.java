package io.github.jiangbyte.voxel.boot.portal.domain.sys.file;

import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.Locale;

/**
 * 文件对象键规范化工具（无 IO）。
 *
 * Author: Charlie
 */
public final class FileObjectKeys {

    private FileObjectKeys() {
    }

    /**
     * 是否已是浏览器可识别的绝对方案。
     */
    public static boolean isExternalUrl(String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        try {
            String scheme = URI.create(value.trim()).getScheme();
            if (scheme == null) {
                return false;
            }
            String lower = scheme.toLowerCase(Locale.ROOT);
            return "http".equals(lower) || "https".equals(lower)
                    || "data".equals(lower) || "blob".equals(lower);
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    /**
     * 是否疑似预签名 / 临时存储 URL。
     */
    public static boolean looksLikePresignedUrl(String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        String lower = value.toLowerCase(Locale.ROOT);
        return lower.contains("x-amz-")
                || lower.contains("x-oss-")
                || lower.contains("signature=")
                || lower.contains("x-goog-signature");
    }

    /**
     * 把任意引用转成纯 object key。
     */
    public static String toObjectKey(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        if (isExternalUrl(value)) {
            try {
                URI uri = URI.create(value.trim());
                return stripToObjectKey(uri.getPath());
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
        return stripToObjectKey(value);
    }

    /**
     * 剥 path-style 的 /{bucket}/uploads/... 首段。
     */
    public static String stripToObjectKey(String pathOrKey) {
        if (!StringUtils.hasText(pathOrKey)) {
            return null;
        }
        String normalized = pathOrKey.replace('\\', '/').replaceAll("^/+", "");
        if (normalized.startsWith("api/v1/files/")) {
            normalized = normalized.substring("api/v1/files/".length());
        }
        int slash = normalized.indexOf('/');
        if (slash > 0) {
            String rest = normalized.substring(slash + 1);
            if (rest.startsWith("uploads/")) {
                normalized = rest;
            }
        }
        return StringUtils.hasText(normalized) ? normalized : null;
    }
}
