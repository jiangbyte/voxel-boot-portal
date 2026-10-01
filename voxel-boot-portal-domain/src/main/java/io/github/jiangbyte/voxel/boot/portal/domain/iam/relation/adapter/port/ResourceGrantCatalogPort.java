package io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port;

import java.util.List;

/**
 * 授权树目录端口：管理端/客户端资源授予选项。
 *
 * Author: Charlie
 */
public interface ResourceGrantCatalogPort {

    /**
     * 管理端资源授权模块树（moduleClient 对应模块 client，如 ADMIN）。
     */
    List<ResourceGrantModuleOption> listAdminGrantModules(String moduleClient);

    /**
     * 客户端资源授权模块树（accountType）。
     */
    List<ResourceGrantModuleOption> listClientGrantModules(String accountType);
}
