package io.github.jiangbyte.voxel.boot.portal.domain.iam.client.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

import java.util.List;
import java.util.Map;

public interface ClientModuleAdminService {
    void create(Map<String, Object> body);
    void update(Map<String, Object> body);
    void delete(List<String> ids);
    ClientModuleView detail(String id);
    PageResult<ClientModuleView> page(long current, long size, String code, String name, String accountType, String status);
    List<ClientModuleView> selector(String accountType);
}
