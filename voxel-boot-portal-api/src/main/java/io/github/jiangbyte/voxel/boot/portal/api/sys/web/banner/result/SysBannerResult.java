package io.github.jiangbyte.voxel.boot.portal.api.sys.web.banner.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "SysBannerResult")
@Data
public class SysBannerResult {
    private String id;
    private String title;
    private String image;
    private String imageUrl;
    private String url;
    private String linkType;
    private String summary;
    private String description;
    private String category;
    private String type;
    private String position;
    private List<String> targetAccountTypes;
    private Integer sort;
    private Long interactionCount;
    private String status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
