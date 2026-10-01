package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.resource;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.Resource;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.ResourceStatus;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.ResourceType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 资源聚合与持久化对象互转。
 */
@Mapper
public interface ResourceAssembler {

    ResourceAssembler INSTANCE = Mappers.getMapper(ResourceAssembler.class);

    @Mapping(target = "resourceType", expression = "java(resource.getResourceType() == null ? io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.ResourceType.MENU.name() : resource.getResourceType().name())")
    @Mapping(source = "visible", target = "isVisible")
    @Mapping(source = "cache", target = "isCache")
    @Mapping(source = "affix", target = "isAffix")
    @Mapping(target = "status", expression = "java(resource.getStatus() == null ? io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.ResourceStatus.ENABLED.name() : resource.getStatus().name())")
    @Mapping(target = "extra", expression = "java(resource.getExtra() == null ? \"{}\" : resource.getExtra())")
    ResourcePO toPO(Resource resource);


    /**
     * 持久化对象转聚合。
     */
    public static Resource toAggregate(ResourcePO po) {
        if (po == null) {
            return null;
        }
        return Resource.reconstitute(
                po.getId(),
                po.getParentId(),
                po.getCode(),
                po.getName(),
                po.getResourceType(),
                po.getModuleId(),
                po.getPath(),
                po.getComponent(),
                po.getRedirect(),
                po.getIcon(),
                po.getColor(),
                po.getHref(),
                po.getSort(),
                po.getIsVisible(),
                po.getIsCache(),
                po.getIsAffix(),
                po.getStatus(),
                po.getDescription(),
                po.getLayout(),
                po.getExtra());
    }
}
