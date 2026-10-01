package io.github.jiangbyte.voxel.boot.portal.infrastructure.security;

import cn.dev33.satoken.stp.StpLogic;

/**
 * Sa-Token 多账号体系入口：Admin / Portal 登录态常量与 StpLogic。
 *
 * Author: Charlie
 */
public final class StpKit {

    public static final String TYPE_ADMIN = "admin";
    public static final String TYPE_PORTAL = "portal";

    public static final StpLogic ADMIN = new StpLogic(TYPE_ADMIN);
    public static final StpLogic PORTAL = new StpLogic(TYPE_PORTAL);

    private StpKit() {
    }
}
