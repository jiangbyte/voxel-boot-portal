package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.identity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 实名工单 PO，对应表 real_name_case。
 *
 * Author: Charlie
 */
@Data
@TableName(value = "real_name_case", autoResultMap = true)
public class RealNameCasePO {

    @TableId(value = "case_id", type = IdType.INPUT)
    private String caseId;
    private String businessType;
    private String verifyChannel;
    private String status;
    private String accountId;
    private String targetAccountHintCipher;
    private String applicantContactCipher;
    private String documentType;
    private String realNameCipher;
    private String documentNoCipher;
    private String documentNoHash;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> attachmentIds = new ArrayList<>();
    private String payloadCipher;
    private String handlerDeptId;
    private String provider;
    private String providerOrderNo;
    private String submitterId;
    private String reviewerId;
    private LocalDateTime reviewedAt;
    private String rejectReason;
    private LocalDateTime expireAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}
