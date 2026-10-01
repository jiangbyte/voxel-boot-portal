package io.github.jiangbyte.voxel.boot.portal.cases.iam.org;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.Org;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.adapter.repository.OrgRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import java.util.List;

/**
 * 用例接口（xfg-ddd Case）。
 *
 * Author: Charlie
 */
public interface IOrgCase {
    void create(CreateOrgCommand command);
    void update(UpdateOrgCommand command);
    void delete(List<String> ids);
}
