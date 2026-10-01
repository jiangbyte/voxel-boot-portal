package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.file;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件元数据持久化对象，对应表 sys_file。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_file")
public class FilePO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String objectName;

    private String originalName;

    private String storageProvider;

    private String bucket;

    private String contentType;

    private Long size;

    private String url;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}
