package io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.Account;

/**
 * 跨上下文文件门面：供 Account/Profile 等按 ID 解析文件元数据。
 *
 * Author: Charlie
 */
public interface FileApi {

    /**
     * 按主键查询文件快照；不存在返回 empty。
     */
    Optional<FileInfo> getById(String id);

    /**
     * 按主键批量查询。
     */
    List<FileInfo> listByIds(Collection<String> ids);

    /**
     * 按 objectName 查询；不存在返回 empty。
     */
    Optional<FileInfo> getByObjectName(String objectName);
}
