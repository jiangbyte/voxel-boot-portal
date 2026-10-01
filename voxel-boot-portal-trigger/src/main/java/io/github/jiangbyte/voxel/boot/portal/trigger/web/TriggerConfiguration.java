package io.github.jiangbyte.voxel.boot.portal.trigger.web;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 接口层组件扫描配置。
 */
@AutoConfiguration
@ComponentScan(basePackages = "io.github.jiangbyte.voxel.boot.portal.trigger")
public class TriggerConfiguration {
}
