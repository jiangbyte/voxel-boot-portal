package io.github.jiangbyte.voxel.boot.portal.infrastructure.security;

import cn.dev33.satoken.stp.StpInterface;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.AccountAuthorization;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.IamRelationService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Sa-Token 权限/角色提供者：从关系图加载当前登录账号的权限码与角色码。
 *
 * Author: Charlie
 */
@Component
@RequiredArgsConstructor
public class SessionPermissionProvider implements StpInterface {

    private final IamRelationService iamRelationService;


    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 1. 未登录不返回权限
        if (loginId == null) {
            return List.of();
        }
        // 2. 从关系图加载授权；超管已在聚合中注入 *:*:*
        AccountAuthorization auth = iamRelationService.getAccountAuthorization(String.valueOf(loginId));
        List<String> keys = auth.getPermissionKeys();
        return keys == null ? List.of() : new ArrayList<>(keys);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 1. 未登录不返回角色
        if (loginId == null) {
            return List.of();
        }
        // 2. 返回角色编码；无角色时返回空（不再伪造 superadmin）
        AccountAuthorization auth = iamRelationService.getAccountAuthorization(String.valueOf(loginId));
        List<String> codes = auth.getRoleCodes();
        if (codes == null || codes.isEmpty()) {
            return List.of();
        }
        // 3. 超管角色码保留，便于会话侧快捷判断
        return new ArrayList<>(codes);
    }
}
