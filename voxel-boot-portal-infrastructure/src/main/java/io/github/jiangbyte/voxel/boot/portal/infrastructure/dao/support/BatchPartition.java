package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 集合分批工具：单批默认 500，避免超长 IN。
 *
 * Author: Charlie
 */
public final class BatchPartition {

    public static final int DEFAULT_SIZE = 500;

    private BatchPartition() {
    }

    public static <T> List<List<T>> partition(Collection<T> source) {
        return partition(source, DEFAULT_SIZE);
    }

    public static <T> List<List<T>> partition(Collection<T> source, int size) {
        if (source == null || source.isEmpty()) {
            return List.of();
        }
        int batch = size <= 0 ? DEFAULT_SIZE : size;
        List<T> list = source instanceof List<T> l ? l : new ArrayList<>(source);
        if (list.size() <= batch) {
            return List.of(list);
        }
        List<List<T>> result = new ArrayList<>((list.size() + batch - 1) / batch);
        for (int i = 0; i < list.size(); i += batch) {
            result.add(list.subList(i, Math.min(i + batch, list.size())));
        }
        return Collections.unmodifiableList(result);
    }
}
