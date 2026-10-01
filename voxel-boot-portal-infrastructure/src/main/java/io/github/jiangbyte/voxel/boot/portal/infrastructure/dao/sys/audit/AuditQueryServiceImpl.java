package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.audit;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.adapter.port.AuditPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.adapter.port.AuditQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.adapter.port.AuditView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 操作审计查询实现。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class AuditQueryServiceImpl implements AuditQueryService {

    private final OperationAuditLogMapper mapper;


    @Override
    public PageResult<AuditView> page(AuditPageQuery query) {
        // 1. 条件分页
        Page<OperationAuditLogPO> page = mapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<OperationAuditLogPO>lambdaQuery()
                        .eq(StringUtils.hasText(query.module()), OperationAuditLogPO::getModule, query.module())
                        .eq(StringUtils.hasText(query.action()), OperationAuditLogPO::getAction, query.action())
                        .ne(!StringUtils.hasText(query.action()) && StringUtils.hasText(query.excludeAction()),
                                OperationAuditLogPO::getAction, query.excludeAction())
                        .eq(StringUtils.hasText(query.accountId()), OperationAuditLogPO::getAccountId, query.accountId())
                        .eq(query.success() != null, OperationAuditLogPO::getSuccess, query.success())
                        .orderByDesc(OperationAuditLogPO::getCreatedAt));
        // 2. 映射视图
        List<AuditView> records = page.getRecords().stream().map(OperationAuditLogAssembler::toView).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public PageResult<AuditView> myPage(String accountId, AuditPageQuery query) {
        // 1. 强制限定当前账号
        AuditPageQuery mine = new AuditPageQuery(
                query.current(), query.size(), query.module(), query.action(),
                query.excludeAction(), accountId, query.success());
        // 2. 复用分页
        return page(mine);
    }

    @Override
    public AuditView detail(String id) {
        // 1. 按主键加载
        OperationAuditLogPO po = mapper.selectById(id);
        // 2. 不存在 404
        if (po == null) {
            throw new DomainException(404, "Audit log not found");
        }
        return OperationAuditLogAssembler.toView(po);
    }

    @Override
    public AuditView myDetail(String id, String accountId) {
        // 1. 加载详情
        AuditView view = detail(id);
        // 2. 非本人视为不存在，避免信息泄露
        if (accountId == null || !accountId.equals(view.accountId())) {
            throw new DomainException(404, "Audit log not found");
        }
        return view;
    }
}
