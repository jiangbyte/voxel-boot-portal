package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.workspace;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port.WorkspaceActivityItemView;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port.WorkspaceOverviewView;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port.WorkspaceQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port.WorkspaceShortcutQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 工作台总览查询：快捷应用 + 本人近期操作/登录。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class WorkspaceQueryServiceImpl implements WorkspaceQueryService {

    private static final int ACTIVITY_LIMIT = 10;

    private final SessionPort sessionPort;
    private final WorkspaceShortcutQueryService shortcutQueryService;
    private final WorkspaceAuditLogMapper auditLogMapper;


    @Override
    public WorkspaceOverviewView overview() {
        // 1. 校验登录
        String accountId = sessionPort.currentAdminAccountId();
        if (!StringUtils.hasText(accountId)) {
            throw new DomainException(401, "未登录");
        }
        // 2. 组装快捷应用与近期日志
        return new WorkspaceOverviewView(
                shortcutQueryService.listMine(),
                listRecentOperations(accountId),
                listRecentLogins(accountId));
    }

    private List<WorkspaceActivityItemView> listRecentOperations(String accountId) {
        Page<WorkspaceAuditLogPO> page = auditLogMapper.selectPage(
                new Page<>(1, ACTIVITY_LIMIT, false),
                Wrappers.<WorkspaceAuditLogPO>lambdaQuery()
                        .eq(WorkspaceAuditLogPO::getAccountId, accountId)
                        .and(w -> w.ne(WorkspaceAuditLogPO::getAction, "login")
                                .or()
                                .isNull(WorkspaceAuditLogPO::getAction))
                        .orderByDesc(WorkspaceAuditLogPO::getCreatedAt));
        return page.getRecords().stream().map(this::toActivityItem).toList();
    }

    private List<WorkspaceActivityItemView> listRecentLogins(String accountId) {
        Page<WorkspaceAuditLogPO> page = auditLogMapper.selectPage(
                new Page<>(1, ACTIVITY_LIMIT, false),
                Wrappers.<WorkspaceAuditLogPO>lambdaQuery()
                        .eq(WorkspaceAuditLogPO::getAccountId, accountId)
                        .eq(WorkspaceAuditLogPO::getAction, "login")
                        .orderByDesc(WorkspaceAuditLogPO::getCreatedAt));
        return page.getRecords().stream().map(this::toActivityItem).toList();
    }

    private WorkspaceActivityItemView toActivityItem(WorkspaceAuditLogPO log) {
        return new WorkspaceActivityItemView(
                log.getId(),
                log.getModule(),
                log.getModuleLabel(),
                log.getAction(),
                log.getActionName(),
                log.getActionType(),
                log.getSummary(),
                log.getSuccess(),
                log.getIp(),
                log.getUserAgent(),
                log.getOperatorName(),
                log.getDurationMs(),
                log.getResourceId(),
                log.getCreatedAt());
    }
}
