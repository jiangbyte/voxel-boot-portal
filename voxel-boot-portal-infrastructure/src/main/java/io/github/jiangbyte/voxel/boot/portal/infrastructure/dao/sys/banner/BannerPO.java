package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.banner;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;

/**
 * Banner 持久化对象，对应表 sys_banner。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_banner")
public class BannerPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String title;

    private String image;

    private String url;

    private String linkType;

    private String summary;

    private String description;

    private String category;

    private String type;

    private String position;

    private String targetAccountTypes;

    private Integer sort;

    private Long interactionCount;

    private String status;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}
