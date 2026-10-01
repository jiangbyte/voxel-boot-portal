package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.weakpassword;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.adapter.port.WeakPasswordPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.adapter.port.WeakPasswordQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.adapter.port.WeakPasswordView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 弱密码只读查询实现：Mapper 投影，过滤口径与 boot 一致。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class WeakPasswordQueryServiceImpl implements WeakPasswordQueryService {

    private final WeakPasswordMapper weakPasswordMapper;


    @Override
    public WeakPasswordView detail(String id) {
        // 1. 按主键加载
        WeakPasswordPO po = weakPasswordMapper.selectById(id);
        // 2. 不存在则抛领域异常
        if (po == null) {
            throw new DomainException(404, "Weak password not found");
        }
        // 3. 转为只读视图
        return toView(po);
    }

    @Override
    public PageResult<WeakPasswordView> page(WeakPasswordPageQuery query) {
        // 1. 取 password / keyword 中首个非空作为模糊过滤条件
        String filter = firstText(query.password(), query.keyword());
        // 2. 数据库分页查询，按 id 倒序
        Page<WeakPasswordPO> page = weakPasswordMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<WeakPasswordPO>lambdaQuery()
                        .like(StringUtils.hasText(filter), WeakPasswordPO::getPassword, filter)
                        .orderByDesc(WeakPasswordPO::getId));
        // 3. 映射为应用层 PageResult
        List<WeakPasswordView> records = page.getRecords().stream().map(this::toView).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<WeakPasswordView> list(String password, String keyword) {
        // 1. 取 password / keyword 中首个非空作为模糊过滤条件
        String filter = firstText(password, keyword);
        // 2. 列表查询并按 id 倒序
        List<WeakPasswordPO> list = weakPasswordMapper.selectList(Wrappers.<WeakPasswordPO>lambdaQuery()
                .like(StringUtils.hasText(filter), WeakPasswordPO::getPassword, filter)
                .orderByDesc(WeakPasswordPO::getId));
        // 3. 转为只读视图
        return list.stream().map(this::toView).toList();
    }

    private WeakPasswordView toView(WeakPasswordPO po) {
        return new WeakPasswordView(
                po.getId(),
                po.getPassword(),
                po.getCreatedAt(),
                po.getUpdatedAt());
    }

    private static String firstText(String primary, String fallback) {
        if (StringUtils.hasText(primary)) {
            return primary;
        }
        return StringUtils.hasText(fallback) ? fallback : null;
    }
}
