package io.github.jiangbyte.voxel.boot.portal.cases.sys.dict;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.Dict;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.repository.DictRepository;
import java.util.List;

/**
 * 用例接口（xfg-ddd Case）。
 *
 * Author: Charlie
 */
public interface IDictCase {
    void create(CreateDictCommand command);
    void update(UpdateDictCommand command);
    void delete(List<String> ids);
}
