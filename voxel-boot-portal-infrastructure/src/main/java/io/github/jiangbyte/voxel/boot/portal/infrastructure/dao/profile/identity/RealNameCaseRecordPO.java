package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.identity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实名工单流水 PO，对应表 real_name_case_record。
 *
 * Author: Charlie
 */
@Data
@TableName("real_name_case_record")
public class RealNameCaseRecordPO {

    @TableId(value = "record_id", type = IdType.INPUT)
    private String recordId;
    private String caseId;
    private String accountId;
    private String businessType;
    private String action;
    private String statusBefore;
    private String statusAfter;
    private String verifyChannel;
    private String provider;
    private String operatorId;
    private String deptId;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
