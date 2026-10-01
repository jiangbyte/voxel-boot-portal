package io.github.jiangbyte.voxel.boot.portal.cases.sys.file;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileDownloadPayload;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileUrlView;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileView;

import java.io.InputStream;
import java.util.List;

/**
 * 文件用例：上传存储、元数据维护、下载与访问 URL。
 *
 * Author: Charlie
 */
public interface IFileCase {

    FileView register(CreateFileCommand command);

    /**
     * 真实上传：put 对象存储后落库 object key，返回前解析访问 URL。
     */
    FileView upload(String originalName, String contentType, long size, InputStream inputStream,
                    String storageProvider);

    void update(UpdateFileCommand command);

    void delete(List<String> ids);

    /**
     * 按 id 加载对象流。
     */
    FileDownloadPayload download(String id);

    /**
     * 按 objectName 解析公开（或默认预签）访问 URL。
     */
    FileUrlView resolvePublicUrl(String objectName);

    /**
     * 按 objectName 生成带 TTL 的预签名 URL。
     */
    FileUrlView resolvePresignedUrl(String objectName);
}
