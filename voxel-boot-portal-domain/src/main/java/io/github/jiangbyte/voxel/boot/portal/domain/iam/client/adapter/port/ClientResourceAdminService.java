package io.github.jiangbyte.voxel.boot.portal.domain.iam.client.adapter.port;

import cn.hutool.core.lang.tree.Tree;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

import java.util.List;
import java.util.Map;

public interface ClientResourceAdminService {
    void create(Map<String, Object> body);
    void update(Map<String, Object> body);
    void delete(List<String> ids);
    ClientResourceView detail(String id);
    PageResult<ClientResourceView> page(long current, long size, String code, String name, String moduleId, String resourceType, String status);
    List<Tree<String>> tree(String moduleId);
    void bindPermission(Map<String, Object> body);
}
