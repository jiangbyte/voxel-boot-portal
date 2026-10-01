package io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.port;

import lombok.Data;

/**
 * 跨上下文字典项快照：供其他限界上下文只读消费。
 *
 * Author: Charlie
 */
@Data
public class DictItem {

    private String id;
    private String code;
    private String label;
    private String value;
    private String color;
    private String parentId;
    private Integer sort;
}
