package io.github.jiangbyte.voxel.boot.portal.cases.iam.role;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.Role;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.adapter.repository.RoleRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import java.util.List;

/**
 * 用例接口（xfg-ddd Case）。
 *
 * Author: Charlie
 */
public interface IRoleCase {
    void create(CreateRoleCommand command);
    void update(UpdateRoleCommand command);
    void delete(List<String> ids);
}
