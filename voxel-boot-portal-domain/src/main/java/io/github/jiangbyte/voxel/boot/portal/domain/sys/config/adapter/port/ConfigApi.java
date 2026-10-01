package io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port;

/**
 * 跨上下文配置门面：按 key 读取配置值。
 *
 * Author: Charlie
 */
public interface ConfigApi {

    /**
     * 按配置键读取值；不存在时返回 null。
     */
    String getByKey(String key);

    /**
     * 按配置键读取值；不存在或空白时返回默认值。
     */
    String getByKey(String key, String defaultValue);
}
