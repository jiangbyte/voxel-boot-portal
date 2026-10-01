package io.github.jiangbyte.voxel.boot.portal.cases.sys.config;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.Config;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port.ConfigApi;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.repository.ConfigRepository;
import java.util.List;

/**
 * 用例接口（xfg-ddd Case）。
 *
 * Author: Charlie
 */
public interface IConfigCase {
    void create(CreateConfigCommand command);
    void update(UpdateConfigCommand command);
    void delete(List<String> ids);
    void batchSave(List<BatchSaveConfigItem> items);
    String getByKey(String key);
    String getByKey(String key, String defaultValue);
}
