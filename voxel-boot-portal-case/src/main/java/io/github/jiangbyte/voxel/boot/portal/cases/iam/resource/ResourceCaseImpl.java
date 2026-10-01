package io.github.jiangbyte.voxel.boot.portal.cases.iam.resource;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.Resource;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.repository.ResourceRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 资源写用例：创建、更新、删除。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class ResourceCaseImpl implements IResourceCase {

    private final ResourceRepository resourceRepository;


    /**
     * 创建资源。
     */
    @Transactional
    public void create(CreateResourceCommand command) {
        // 1. 校验模块内编码唯一
        ensureCodeUnique(command.moduleId(), command.code(), null);
        // 2. 工厂创建聚合
        Resource resource = Resource.create(
                command.parentId(),
                command.code(),
                command.name(),
                command.resourceType(),
                command.moduleId(),
                command.path(),
                command.component(),
                command.redirect(),
                command.icon(),
                command.color(),
                command.href(),
                command.sort(),
                command.visible(),
                command.cache(),
                command.affix(),
                command.status(),
                command.description(),
                command.layout(),
                command.extra());
        // 3. 持久化
        resourceRepository.save(resource);
    }

    /**
     * 更新资源。
     */
    @Transactional
    public void update(UpdateResourceCommand command) {
        // 1. 加载聚合
        Resource resource = resourceRepository.findById(command.id())
                .orElseThrow(() -> new DomainException(404, "Resource not found"));
        // 2. 校验模块内编码唯一
        ensureCodeUnique(command.moduleId(), command.code(), command.id());
        // 3. 聚合内更新并保存
        resource.update(
                command.parentId(),
                command.code(),
                command.name(),
                command.resourceType(),
                command.moduleId(),
                command.path(),
                command.component(),
                command.redirect(),
                command.icon(),
                command.color(),
                command.href(),
                command.sort(),
                command.visible(),
                command.cache(),
                command.affix(),
                command.status(),
                command.description(),
                command.layout(),
                command.extra());
        resourceRepository.save(resource);
    }

    /**
     * 批量删除资源；存在子节点时拒绝删除。
     */
    @Transactional
    public void delete(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 逐个加载；有子资源则拒绝
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }
            resourceRepository.findById(id).ifPresent(resource -> {
                if (resourceRepository.existsByParentId(resource.getId())) {
                    throw new DomainException("Resource has children and cannot be deleted");
                }
                resource.markDeleted();
                resourceRepository.remove(resource);
            });
        }
    }

    private void ensureCodeUnique(String moduleId, String code, String excludeId) {
        resourceRepository.findByModuleIdAndCode(moduleId, code).ifPresent(existing -> {
            if (excludeId == null || !excludeId.equals(existing.getId())) {
                throw new DomainException("Resource code already exists in module");
            }
        });
    }
}
