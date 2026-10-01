package io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 资源模块/按钮/权限绑定管理端口。
 *
 * Author: Charlie
 */
public interface ResourceAdminExtrasService {

    void createModule(Map<String, Object> body);
    void updateModule(Map<String, Object> body);
    void deleteModules(List<String> ids);
    ResourceModuleView moduleDetail(String id);
    PageResult<ResourceModuleView> pageModules(long current, long size, String code, String name, String status);
    List<ResourceModuleView> moduleSelector();

    void createButton(Map<String, Object> body);
    void updateButton(Map<String, Object> body);
    void deleteButtons(List<String> ids);
    PageResult<ResourceButtonView> pageButtons(long current, long size, String parentId, String code, String name, String status);

    void bindPermission(Map<String, Object> body);

    List<ResourceView> listPortalCurrent();
}
