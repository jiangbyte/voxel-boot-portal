package io.github.jiangbyte.voxel.boot.portal.api.profile.web.admin.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 头像上传结果。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "头像上传结果")
public class AvatarUpdateResult {
    private String avatar;
    private String fileId;
    private String objectName;
    private String url;
}
