package io.github.jiangbyte.voxel.boot.portal.cases.profile.identity;

/**
 * 实名附件视图。
 *
 * Author: Charlie
 */
public record RealNameCaseAttachmentView(
        String objectName,
        String id,
        String originalName,
        String contentType,
        Long size,
        String url
) {
}
