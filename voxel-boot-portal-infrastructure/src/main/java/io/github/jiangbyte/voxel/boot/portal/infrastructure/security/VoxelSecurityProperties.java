package io.github.jiangbyte.voxel.boot.portal.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全相关配置：鉴权白名单等。
 *
 * Author: Charlie
 */
@ConfigurationProperties(prefix = "voxel.security")
public class VoxelSecurityProperties {

    private List<String> ignoreUrls = new ArrayList<>();

    public List<String> getIgnoreUrls() {
        return ignoreUrls;
    }

    public void setIgnoreUrls(List<String> ignoreUrls) {
        this.ignoreUrls = ignoreUrls != null ? ignoreUrls : new ArrayList<>();
    }
}
