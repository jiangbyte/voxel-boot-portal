package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.dict;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据字典持久化对象，对应表 sys_dict。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_dict")
public class DictPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String code;

    private String label;

    private String value;

    private String color;

    private String category;

    private String parentId;

    private String status;

    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}
