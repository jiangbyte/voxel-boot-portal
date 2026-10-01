package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.notice;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告已读记录，对应表 sys_notice_read。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_notice_read")
public class NoticeReadPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String noticeId;

    private String accountType;

    private String accountId;

    private LocalDateTime readAt;
}
