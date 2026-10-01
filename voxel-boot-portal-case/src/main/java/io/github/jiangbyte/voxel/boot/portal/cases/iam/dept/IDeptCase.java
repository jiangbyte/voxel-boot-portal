package io.github.jiangbyte.voxel.boot.portal.cases.iam.dept;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.Dept;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.adapter.repository.DeptRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import java.util.List;

/**
 * 用例接口（xfg-ddd Case）。
 *
 * Author: Charlie
 */
public interface IDeptCase {
    void create(CreateDeptCommand command);
    void update(UpdateDeptCommand command);
    void delete(List<String> ids);
}
