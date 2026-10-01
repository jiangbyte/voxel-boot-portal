package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.workspace;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作台快捷应用持久化对象，对应表 sys_workspace_shortcut。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_workspace_shortcut")
public class WorkspaceShortcutPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String accountId;

    private String resourceId;

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
