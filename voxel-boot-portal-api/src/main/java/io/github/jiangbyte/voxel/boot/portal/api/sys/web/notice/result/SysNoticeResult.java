package io.github.jiangbyte.voxel.boot.portal.api.sys.web.notice.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "SysNoticeResult")
@Data
public class SysNoticeResult {
    private String id;
    private String kind;
    private String title;
    private String content;
    private String contentType;
    private String category;
    private String severity;
    private String targetScope;
    private List<String> targetAccountTypes;
    private List<String> targetAccountIds;
    private List<String> targetDeptIds;
    private List<String> targetRoleIds;
    private Map<String, Object> publishLocations;
    private Integer isPinned;
    private LocalDateTime pinnedUntil;
    private String senderAccountType;
    private String senderAccountId;
    private String sourceType;
    private String sourceId;
    private String status;
    private LocalDateTime publishAt;
    private LocalDateTime revokedAt;
    private LocalDateTime expireAt;
    private Integer viewCount;
    private Map<String, Object> extra;
    private Integer isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
