package io.github.jiangbyte.voxel.boot.portal.cases.iam.position;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.Position;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.adapter.repository.PositionRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import java.util.List;

/**
 * 用例接口。
 *
 * Author: Charlie
 */
public interface IPositionCase {
    void create(CreatePositionCommand command);
    void update(UpdatePositionCommand command);
    void delete(List<String> ids);
}
