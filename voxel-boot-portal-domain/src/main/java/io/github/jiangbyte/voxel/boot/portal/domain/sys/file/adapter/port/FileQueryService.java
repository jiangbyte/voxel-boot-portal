package io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

import java.util.List;

/**
 * 文件查询用例端口。
 *
 * Author: Charlie
 */
public interface FileQueryService {

    /**
     * 按主键查询详情。
     */
    FileView detail(String id);

    /**
     * 按主键批量查询。
     */
    List<FileView> listByIds(List<String> ids);

    /**
     * 按 objectName 查询；不存在返回 null。
     */
    FileView findByObjectName(String objectName);

    /**
     * 分页查询。
     */
    PageResult<FileView> page(FilePageQuery query);
}
