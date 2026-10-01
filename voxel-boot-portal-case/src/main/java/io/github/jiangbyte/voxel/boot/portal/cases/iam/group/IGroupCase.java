package io.github.jiangbyte.voxel.boot.portal.cases.iam.group;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.Group;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.adapter.repository.GroupRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import java.util.List;

/**
 * 用例接口。
 *
 * Author: Charlie
 */
public interface IGroupCase {
    void create(CreateGroupCommand command);
    void update(UpdateGroupCommand command);
    void delete(List<String> ids);
}
