package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.group;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.adapter.port.GroupPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.adapter.port.GroupQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.adapter.port.GroupView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.Group;
import lombok.RequiredArgsConstructor;

/**
 * 用户组只读查询实现。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class GroupQueryServiceImpl implements GroupQueryService {

    private static final int IN_BATCH_SIZE = 500;

    private final GroupMapper groupMapper;


    @Override
    public GroupView detail(String id) {
        // 1. 按主键加载
        GroupPO po = groupMapper.selectById(id);
        // 2. 不存在则 404
        if (po == null) {
            throw new DomainException(404, "Group not found");
        }
        // 3. 转为视图
        return toView(po);
    }

    @Override
    public List<GroupView> listByIds(Collection<String> ids) {
        // 1. 去重过滤空 ID
        List<String> uniqueIds = normalizeIds(ids);
        if (uniqueIds.isEmpty()) {
            return List.of();
        }
        // 2. IN 分批查询，单批 ≤ 500，禁止循环单查
        List<GroupView> result = new ArrayList<>(uniqueIds.size());
        for (int i = 0; i < uniqueIds.size(); i += IN_BATCH_SIZE) {
            List<String> batch = uniqueIds.subList(i, Math.min(i + IN_BATCH_SIZE, uniqueIds.size()));
            for (GroupPO po : groupMapper.selectByIds(batch)) {
                result.add(toView(po));
            }
        }
        return result;
    }

    @Override
    public PageResult<GroupView> page(GroupPageQuery query) {
        // 1. 数据库分页查询
        Page<GroupPO> page = groupMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<GroupPO>lambdaQuery()
                        .like(StringUtils.hasText(query.name()), GroupPO::getName, query.name())
                        .eq(StringUtils.hasText(query.ownerDeptId()), GroupPO::getOwnerDeptId, query.ownerDeptId())
                        .eq(StringUtils.hasText(query.status()), GroupPO::getStatus, query.status())
                        .orderByDesc(GroupPO::getCreatedAt));
        // 2. 映射为应用层 PageResult
        List<GroupView> records = page.getRecords().stream().map(this::toView).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    private GroupView toView(GroupPO po) {
        return new GroupView(
                po.getId(),
                po.getName(),
                po.getOwnerDeptId(),
                po.getDescription(),
                po.getStatus(),
                po.getExtra(),
                po.getCreatedAt(),
                po.getUpdatedAt());
    }

    private static List<String> normalizeIds(Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return List.of();
        }
        return ids.stream().filter(StringUtils::hasText).distinct().toList();
    }
}
