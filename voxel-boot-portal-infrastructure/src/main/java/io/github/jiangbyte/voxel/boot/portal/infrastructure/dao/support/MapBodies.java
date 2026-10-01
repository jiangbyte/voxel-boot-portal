package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.support;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Map 请求体字段读取辅助。
 *
 * Author: Charlie
 */
public final class MapBodies {

    private MapBodies() {
    }

    public static String str(Map<String, Object> body, String key) {
        if (body == null || body.get(key) == null) {
            return null;
        }
        String v = String.valueOf(body.get(key));
        return v.isBlank() || "null".equals(v) ? null : v;
    }

    public static Integer integer(Map<String, Object> body, String key) {
        Object v = body == null ? null : body.get(key);
        if (v == null) {
            return null;
        }
        if (v instanceof Number n) {
            return n.intValue();
        }
        String s = String.valueOf(v);
        if (s.isBlank()) {
            return null;
        }
        return Integer.parseInt(s);
    }


    @SuppressWarnings("unchecked")
    public static List<String> stringList(Map<String, Object> body, String key) {
        Object v = body == null ? null : body.get(key);
        if (!(v instanceof List<?> list)) {
            return Collections.emptyList();
        }
        return list.stream().filter(i -> i != null).map(String::valueOf).toList();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> map(Map<String, Object> body, String key) {
        Object v = body == null ? null : body.get(key);
        if (v instanceof Map<?, ?> m) {
            return (Map<String, Object>) m;
        }
        return null;
    }
}
