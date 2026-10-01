package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.position;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.adapter.port.PositionPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.adapter.port.PositionQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.adapter.port.PositionView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.Position;
import lombok.RequiredArgsConstructor;

/**
 * 岗位只读查询实现。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class PositionQueryServiceImpl implements PositionQueryService {

    private final PositionMapper positionMapper;


    @Override
    public PositionView detail(String id) {
        // 1. 按主键加载
        PositionPO po = positionMapper.selectById(id);
        // 2. 不存在则 404
        if (po == null) {
            throw new DomainException(404, "Position not found");
        }
        // 3. 转为视图
        return toView(po);
    }

    @Override
    public PageResult<PositionView> page(PositionPageQuery query) {
        // 1. 数据库分页查询
        Page<PositionPO> page = positionMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<PositionPO>lambdaQuery()
                        .like(StringUtils.hasText(query.name()), PositionPO::getName, query.name())
                        .eq(StringUtils.hasText(query.category()), PositionPO::getCategory, query.category())
                        .eq(StringUtils.hasText(query.status()), PositionPO::getStatus, query.status())
                        .orderByAsc(PositionPO::getSort)
                        .orderByDesc(PositionPO::getCreatedAt));
        // 2. 映射为应用层 PageResult
        List<PositionView> records = page.getRecords().stream().map(this::toView).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    private PositionView toView(PositionPO po) {
        return new PositionView(
                po.getId(),
                po.getName(),
                po.getCategory(),
                po.getOwnerDeptId(),
                po.getSort(),
                po.getIsVirtual(),
                po.getStatus(),
                po.getDescription(),
                po.getExtra(),
                po.getCreatedAt(),
                po.getUpdatedAt());
    }
}
