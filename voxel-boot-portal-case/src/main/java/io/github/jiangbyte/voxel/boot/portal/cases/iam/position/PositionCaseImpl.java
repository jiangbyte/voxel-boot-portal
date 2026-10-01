package io.github.jiangbyte.voxel.boot.portal.cases.iam.position;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.Position;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.adapter.repository.PositionRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 岗位写用例：创建、更新、删除。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class PositionCaseImpl implements IPositionCase {

    private final PositionRepository positionRepository;


    /**
     * 创建岗位。
     */
    @Transactional
    public void create(CreatePositionCommand command) {
        // 1. 工厂创建聚合
        Position position = Position.create(
                command.name(),
                command.category(),
                command.ownerDeptId(),
                command.sort(),
                command.virtual(),
                command.status(),
                command.description(),
                command.extra());
        // 2. 持久化
        positionRepository.save(position);
    }

    /**
     * 更新岗位。
     */
    @Transactional
    public void update(UpdatePositionCommand command) {
        // 1. 加载聚合
        Position position = positionRepository.findById(command.id())
                .orElseThrow(() -> new DomainException(404, "Position not found"));
        // 2. 聚合内更新并保存
        position.update(
                command.name(),
                command.category(),
                command.ownerDeptId(),
                command.sort(),
                command.virtual(),
                command.status(),
                command.description(),
                command.extra());
        positionRepository.save(position);
    }

    /**
     * 批量删除岗位。
     */
    @Transactional
    public void delete(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 逐个加载并删除
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }
            positionRepository.findById(id).ifPresent(position -> {
                position.markDeleted();
                positionRepository.remove(position);
            });
        }
    }
}
