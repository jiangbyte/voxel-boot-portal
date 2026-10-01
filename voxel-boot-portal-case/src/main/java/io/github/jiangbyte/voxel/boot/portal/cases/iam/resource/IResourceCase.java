package io.github.jiangbyte.voxel.boot.portal.cases.iam.resource;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.Resource;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.repository.ResourceRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import java.util.List;

/**
 * 用例接口（xfg-ddd Case）。
 *
 * Author: Charlie
 */
public interface IResourceCase {
    void create(CreateResourceCommand command);
    void update(UpdateResourceCommand command);
    void delete(List<String> ids);
}
