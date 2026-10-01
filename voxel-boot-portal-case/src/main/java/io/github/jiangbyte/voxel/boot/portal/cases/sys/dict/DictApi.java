package io.github.jiangbyte.voxel.boot.portal.cases.sys.dict;

import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.port.DictItem;

/**
 * 跨上下文字典门面：按类型列出启用项。
 *
 * Author: Charlie
 */
public interface DictApi {

    /**
     * 按字典类型列出启用的字典项。
     */
    List<DictItem> listByType(String dictType);
}
