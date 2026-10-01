package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.feedback;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.adapter.port.FeedbackPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.adapter.port.FeedbackQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.adapter.port.FeedbackView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileApi;
import lombok.RequiredArgsConstructor;

/**
 * 反馈只读查询实现：Mapper 投影。
 * <p>附件以 attachObjectNames（JSON 落库解析后的列表）原样返回；当前 FileApi 能力不完整，
 * 跳过附件元数据/URL 与提交人资料补全，仅返回基础字段，保证存取正确。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class FeedbackQueryServiceImpl implements FeedbackQueryService {

    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };

    private final FeedbackMapper feedbackMapper;
    private final ObjectMapper objectMapper;


    @Override
    public FeedbackView detail(String id) {
        // 1. 按主键加载
        FeedbackPO po = feedbackMapper.selectById(id);
        // 2. 不存在则 404
        if (po == null) {
            throw new DomainException(404, "反馈不存在");
        }
        // 3. 转为视图（附件原样、资料字段留空）
        return toView(po);
    }

    @Override
    public PageResult<FeedbackView> page(FeedbackPageQuery query) {
        // 1. 数据库分页（禁止内存截断）
        Page<FeedbackPO> page = feedbackMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<FeedbackPO>lambdaQuery()
                        .like(StringUtils.hasText(query.title()), FeedbackPO::getTitle, query.title())
                        .eq(StringUtils.hasText(query.status()), FeedbackPO::getStatus, query.status())
                        .eq(StringUtils.hasText(query.category()), FeedbackPO::getCategory, query.category())
                        .eq(StringUtils.hasText(query.submitterAccountType()),
                                FeedbackPO::getSubmitterAccountType, query.submitterAccountType())
                        .orderByDesc(FeedbackPO::getCreatedAt));
        // 2. 映射为应用层 PageResult
        List<FeedbackView> records = page.getRecords().stream().map(this::toView).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public PageResult<FeedbackView> myPage(String accountType, String accountId, FeedbackPageQuery query) {
        // 1. 校验当前提交人身份
        requireAccount(accountType, accountId);
        // 2. 按提交人过滤分页
        Page<FeedbackPO> page = feedbackMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<FeedbackPO>lambdaQuery()
                        .eq(FeedbackPO::getSubmitterAccountType, accountType.trim().toUpperCase())
                        .eq(FeedbackPO::getSubmitterAccountId, accountId)
                        .orderByDesc(FeedbackPO::getCreatedAt));
        // 3. 映射视图
        List<FeedbackView> records = page.getRecords().stream().map(this::toView).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public FeedbackView myDetail(String id, String accountType, String accountId) {
        // 1. 校验当前提交人身份
        requireAccount(accountType, accountId);
        // 2. 按主键加载
        FeedbackPO po = feedbackMapper.selectById(id);
        if (po == null) {
            throw new DomainException(404, "反馈不存在");
        }
        // 3. 归属校验：账户类型与 ID 均需匹配
        if (!accountType.equalsIgnoreCase(po.getSubmitterAccountType())
                || !accountId.equals(po.getSubmitterAccountId())) {
            throw new DomainException(403, "无权查看");
        }
        // 4. 转为视图
        return toView(po);
    }

    private void requireAccount(String accountType, String accountId) {
        if (!StringUtils.hasText(accountType) || !StringUtils.hasText(accountId)) {
            throw new DomainException(401, "Not logged in");
        }
    }

    private FeedbackView toView(FeedbackPO po) {
        return new FeedbackView(
                po.getId(),
                po.getTitle(),
                po.getContent(),
                po.getCategory(),
                po.getContact(),
                parseStringList(po.getAttachObjectNames()),
                po.getStatus(),
                po.getReply(),
                po.getRepliedBy(),
                po.getRepliedAt(),
                po.getSubmitterAccountType(),
                po.getSubmitterAccountId(),
                null,
                null,
                po.getCreatedAt(),
                po.getUpdatedAt());
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
