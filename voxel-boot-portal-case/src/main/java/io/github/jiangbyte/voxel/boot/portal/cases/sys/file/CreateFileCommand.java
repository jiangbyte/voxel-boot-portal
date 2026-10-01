package io.github.jiangbyte.voxel.boot.portal.cases.sys.file;

/**
 * 登记文件元数据命令（上传占位）。
 *
 * Author: Charlie
 */
public record CreateFileCommand(
        String objectName,
        String originalName,
        String storageProvider,
        String bucket,
        String contentType,
        Long size,
        String url
) {
}
