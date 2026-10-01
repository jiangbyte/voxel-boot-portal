package io.github.jiangbyte.voxel.boot.portal.api.sys.web.feedback.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "SysFeedbackResult")
@Data
public class SysFeedbackResult {
    private String id;
    private String title;
    private String content;
    private String category;
    private String contact;
    private List<String> attachObjectNames;
    private String status;
    private String reply;
    private String repliedBy;
    private LocalDateTime repliedAt;
    private String submitterAccountType;
    private String submitterAccountId;
    private String submitterAvatar;
    private String submitterNickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
