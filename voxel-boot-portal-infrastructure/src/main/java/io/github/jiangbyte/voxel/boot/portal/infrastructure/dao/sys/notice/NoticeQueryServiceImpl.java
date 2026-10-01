package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.notice;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.adapter.port.NoticePageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.adapter.port.NoticeQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.adapter.port.NoticeView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.Notice;
import lombok.RequiredArgsConstructor;

/**
 * 公告只读查询实现（含门户可见性与已读标记）。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class NoticeQueryServiceImpl implements NoticeQueryService {

    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };

    private static final String KIND_ANNOUNCEMENT = "ANNOUNCEMENT";
    private static final String TARGET_ALL = "ALL";
    private static final String TARGET_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    private static final String TARGET_SPECIFIC = "SPECIFIC";
    private static final String PUBLISHED = "PUBLISHED";

    private final NoticeMapper noticeMapper;
    private final NoticeReadMapper noticeReadMapper;
    private final SessionPort sessionPort;
    private final ObjectMapper objectMapper;


    @Override
    public NoticeView detail(String id) {
        // 1. 按主键加载
        NoticePO po = noticeMapper.selectById(id);
        // 2. 不存在则 404
        if (po == null) {
            throw new DomainException(404, "Notice not found");
        }
        // 3. 转为视图
        return toView(po, null);
    }

    @Override
    public PageResult<NoticeView> page(NoticePageQuery query) {
        // 1. 管理端分页（不过滤可见性）
        Page<NoticePO> page = noticeMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<NoticePO>lambdaQuery()
                        .eq(StringUtils.hasText(query.kind()), NoticePO::getKind, query.kind())
                        .like(StringUtils.hasText(query.title()), NoticePO::getTitle, query.title())
                        .eq(StringUtils.hasText(query.category()), NoticePO::getCategory, query.category())
                        .eq(StringUtils.hasText(query.status()), NoticePO::getStatus, query.status())
                        .eq(StringUtils.hasText(query.severity()), NoticePO::getSeverity, query.severity())
                        .orderByDesc(NoticePO::getIsPinned)
                        .orderByDesc(NoticePO::getPublishAt)
                        .orderByDesc(NoticePO::getCreatedAt));
        List<NoticeView> records = page.getRecords().stream().map(po -> toView(po, null)).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public PageResult<NoticeView> portalList(NoticePageQuery query) {
        // 1. 可匿名：默认 PORTAL；已登录则用真实账号
        String accountType = "PORTAL";
        String accountId = sessionPort.currentPortalAccountId();
        if (StringUtils.hasText(accountId)) {
            accountType = "PORTAL";
        }
        // 2. 门户列表固定公告 kind
        return pagePublished(query, accountType, accountId, KIND_ANNOUNCEMENT);
    }

    @Override
    public PageResult<NoticeView> myPage(NoticePageQuery query) {
        // 1. 门户登录：按可见性过滤；管理端回落为已发布列表（无门户可见性）
        String accountId = sessionPort.currentPortalAccountId();
        if (StringUtils.hasText(accountId)) {
            String kind = StringUtils.hasText(query.kind()) ? query.kind().toUpperCase(Locale.ROOT) : null;
            return pagePublished(query, "PORTAL", accountId, kind);
        }
        Page<NoticePO> page = noticeMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<NoticePO>lambdaQuery()
                        .eq(NoticePO::getStatus, PUBLISHED)
                        .eq(StringUtils.hasText(query.kind()), NoticePO::getKind, query.kind())
                        .orderByDesc(NoticePO::getIsPinned)
                        .orderByDesc(NoticePO::getPublishAt)
                        .orderByDesc(NoticePO::getCreatedAt));
        return new PageResult<>(page.getRecords().stream().map(po -> toView(po, null)).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public NoticeView myDetail(String id) {
        // 1. 登录校验
        String accountId = requirePortalAccountId();
        // 2. 加载并校验已发布 + 可见
        NoticePO po = noticeMapper.selectById(id);
        if (po == null || !PUBLISHED.equals(po.getStatus()) || !visibleTo(po, "PORTAL", accountId)) {
            throw new DomainException(404, "消息不存在");
        }
        // 3. 公告累加浏览次数
        if (KIND_ANNOUNCEMENT.equals(po.getKind())) {
            int next = (po.getViewCount() == null ? 0 : po.getViewCount()) + 1;
            po.setViewCount(next);
            noticeMapper.updateById(po);
        }
        // 4. 标记已读并返回
        markReadInternal(List.of(id), "PORTAL", accountId);
        return toView(po, Flags.of(true));
    }

    @Override
    public int unreadCount() {
        // 1. 仅门户会话统计未读；管理端无门户账号时返回 0
        String accountId = sessionPort.currentPortalAccountId();
        if (!StringUtils.hasText(accountId)) {
            return 0;
        }
        // 2. 拉取可见已发布 ID，排除已读（批处理）
        int unread = 0;
        final int batchSize = 500;
        long offset = 0;
        while (true) {
            List<String> ids = listVisiblePublishedIds("PORTAL", accountId, null, offset, batchSize);
            if (ids.isEmpty()) {
                break;
            }
            Set<String> readIds = loadReadIds("PORTAL", accountId, ids);
            for (String noticeId : ids) {
                if (!readIds.contains(noticeId)) {
                    unread++;
                }
            }
            if (ids.size() < batchSize) {
                break;
            }
            offset += batchSize;
        }
        return unread;
    }

    @Override
    public void markRead(List<String> ids) {
        // 1. 登录后写入已读
        String accountId = requirePortalAccountId();
        markReadInternal(ids == null ? List.of() : ids, "PORTAL", accountId);
    }

    @Override
    public void markAllRead() {
        // 1. 登录
        String accountId = requirePortalAccountId();
        // 2. 分批拉取可见已发布并标记已读
        final int batchSize = 500;
        long offset = 0;
        while (true) {
            List<String> publishedIds = listVisiblePublishedIds("PORTAL", accountId, null, offset, batchSize);
            if (publishedIds.isEmpty()) {
                break;
            }
            markReadInternal(publishedIds, "PORTAL", accountId);
            if (publishedIds.size() < batchSize) {
                break;
            }
            offset += batchSize;
        }
    }

    @Override
    public List<String> listVisiblePublishedIds(String accountType, String accountId, String kind, long offset, int limit) {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<NoticePO> qw = Wrappers.<NoticePO>lambdaQuery()
                .select(NoticePO::getId)
                .eq(NoticePO::getStatus, PUBLISHED)
                .and(w -> w.ne(NoticePO::getKind, KIND_ANNOUNCEMENT)
                        .or().isNull(NoticePO::getExpireAt)
                        .or().gt(NoticePO::getExpireAt, now))
                .eq(StringUtils.hasText(kind), NoticePO::getKind, kind)
                .orderByAsc(NoticePO::getId)
                .last("LIMIT " + Math.max(limit, 1) + " OFFSET " + Math.max(offset, 0));
        applyVisibilitySql(qw, accountType, accountId);
        return noticeMapper.selectList(qw).stream().map(NoticePO::getId).toList();
    }

    /**
     * 写入已读记录（供应用层 markRead / markAllRead 复用）。
     */
    public void markReadInternal(List<String> noticeIds, String accountType, String accountId) {
        if (noticeIds == null || noticeIds.isEmpty() || !StringUtils.hasText(accountId)) {
            return;
        }
        List<String> distinctIds = noticeIds.stream().filter(StringUtils::hasText).distinct().toList();
        if (distinctIds.isEmpty()) {
            return;
        }
        Set<String> existing = loadReadIds(accountType, accountId, distinctIds);
        LocalDateTime now = LocalDateTime.now();
        for (String noticeId : distinctIds) {
            if (existing.contains(noticeId)) {
                continue;
            }
            NoticeReadPO read = new NoticeReadPO();
            read.setId(UUID.randomUUID().toString().replace("-", ""));
            read.setNoticeId(noticeId);
            read.setAccountType(accountType);
            read.setAccountId(accountId);
            read.setReadAt(now);
            noticeReadMapper.insert(read);
        }
    }

    private PageResult<NoticeView> pagePublished(NoticePageQuery query,
                                                 String accountType,
                                                 String accountId,
                                                 String kind) {
        // 1. 已发布 + 公告过期过滤 + 可见性
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<NoticePO> qw = Wrappers.<NoticePO>lambdaQuery()
                .eq(NoticePO::getStatus, PUBLISHED)
                .and(w -> w.ne(NoticePO::getKind, KIND_ANNOUNCEMENT)
                        .or().isNull(NoticePO::getExpireAt)
                        .or().gt(NoticePO::getExpireAt, now))
                .eq(StringUtils.hasText(kind), NoticePO::getKind, kind)
                .orderByDesc(NoticePO::getIsPinned)
                .orderByDesc(NoticePO::getPublishAt);
        applyVisibilitySql(qw, accountType, accountId);
        Page<NoticePO> page = noticeMapper.selectPage(new Page<>(query.current(), query.size()), qw);
        // 2. 回填已读标记
        Set<String> readIds = Set.of();
        if (StringUtils.hasText(accountId) && !page.getRecords().isEmpty()) {
            readIds = loadReadIds(accountType, accountId,
                    page.getRecords().stream().map(NoticePO::getId).toList());
        }
        Set<String> finalReadIds = readIds;
        List<NoticeView> records = page.getRecords().stream()
                .map(po -> toView(po, StringUtils.hasText(accountId)
                        ? Flags.of(finalReadIds.contains(po.getId())) : null))
                .toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    private void applyVisibilitySql(LambdaQueryWrapper<NoticePO> qw, String accountType, String accountId) {
        // ALL / ACCOUNT_TYPE：JSON 空或包含账户类型；SPECIFIC：JSON 包含账户 ID
        qw.and(w -> {
            w.and(typeScope -> typeScope
                    .in(NoticePO::getTargetScope, List.of(TARGET_ALL, TARGET_ACCOUNT_TYPE))
                    .and(typeMatch -> typeMatch
                            .isNull(NoticePO::getTargetAccountTypes)
                            .or()
                            .apply("(JSON_LENGTH(COALESCE(target_account_types, JSON_ARRAY())) = 0 OR JSON_CONTAINS(COALESCE(target_account_types, JSON_ARRAY()), JSON_QUOTE({0}), '$'))",
                                    accountType)));
            if (StringUtils.hasText(accountId)) {
                w.or(specific -> specific
                        .eq(NoticePO::getTargetScope, TARGET_SPECIFIC)
                        .apply("JSON_CONTAINS(COALESCE(target_account_ids, JSON_ARRAY()), JSON_QUOTE({0}), '$')",
                                accountId));
            }
        });
    }

    private boolean visibleTo(NoticePO n, String accountType, String accountId) {
        String scope = n.getTargetScope() == null ? TARGET_ALL : n.getTargetScope();
        List<String> types = parseStringList(n.getTargetAccountTypes());
        boolean typeMatch = types.isEmpty() || types.stream().anyMatch(t -> accountType.equalsIgnoreCase(t));
        if (TARGET_ALL.equals(scope) || TARGET_ACCOUNT_TYPE.equals(scope)) {
            return typeMatch;
        }
        if (TARGET_SPECIFIC.equals(scope)) {
            List<String> ids = parseStringList(n.getTargetAccountIds());
            return StringUtils.hasText(accountId) && ids.contains(accountId);
        }
        return false;
    }

    private Set<String> loadReadIds(String accountType, String accountId, List<String> noticeIds) {
        if (noticeIds == null || noticeIds.isEmpty()) {
            return Set.of();
        }
        Set<String> readIds = new HashSet<>();
        final int batch = 500;
        for (int i = 0; i < noticeIds.size(); i += batch) {
            List<String> chunk = noticeIds.subList(i, Math.min(i + batch, noticeIds.size()));
            noticeReadMapper.selectList(Wrappers.<NoticeReadPO>lambdaQuery()
                            .eq(NoticeReadPO::getAccountType, accountType)
                            .eq(NoticeReadPO::getAccountId, accountId)
                            .in(NoticeReadPO::getNoticeId, chunk))
                    .forEach(item -> readIds.add(item.getNoticeId()));
        }
        return readIds;
    }

    private String requirePortalAccountId() {
        String accountId = sessionPort.currentPortalAccountId();
        if (!StringUtils.hasText(accountId)) {
            throw new DomainException(401, "未登录");
        }
        return accountId;
    }

    private NoticeView toView(NoticePO po, Integer isRead) {
        return new NoticeView(
                po.getId(), po.getKind(), po.getTitle(), po.getContent(), po.getContentType(),
                po.getCategory(), po.getSeverity(), po.getTargetScope(), po.getTargetAccountTypes(),
                po.getTargetAccountIds(), po.getTargetDeptIds(), po.getTargetRoleIds(),
                po.getPublishLocations(), po.getIsPinned(), po.getPinnedUntil(),
                po.getSenderAccountType(), po.getSenderAccountId(), po.getSourceType(), po.getSourceId(),
                po.getStatus(), po.getPublishAt(), po.getRevokedAt(), po.getExpireAt(),
                po.getViewCount(), po.getExtra(), isRead, po.getCreatedAt(), po.getUpdatedAt());
    }

    private List<String> parseStringList(String raw) {
        if (!StringUtils.hasText(raw)) {
            return List.of();
        }
        try {
            List<String> list = objectMapper.readValue(raw, STRING_LIST);
            return list == null ? List.of() : list;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
