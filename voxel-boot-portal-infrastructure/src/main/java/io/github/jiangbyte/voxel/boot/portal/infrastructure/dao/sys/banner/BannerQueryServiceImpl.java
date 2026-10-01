package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.banner;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port.BannerPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port.BannerQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port.BannerView;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileApi;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;
import lombok.RequiredArgsConstructor;

/**
 * Banner 只读查询实现。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class BannerQueryServiceImpl implements BannerQueryService {

    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };

    private final BannerMapper bannerMapper;
    private final FileApi fileApi;
    private final ObjectMapper objectMapper;


    @Override
    public BannerView detail(String id) {
        // 1. 按主键加载
        BannerPO po = bannerMapper.selectById(id);
        // 2. 不存在则 404
        if (po == null) {
            throw new DomainException(404, "Banner not found");
        }
        // 3. 转为视图并解析图片 URL
        return toView(po);
    }

    @Override
    public PageResult<BannerView> page(BannerPageQuery query) {
        // 1. 数据库分页查询（MySQL JSON_CONTAINS 过滤可见账户类型）
        Page<BannerPO> page = bannerMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<BannerPO>lambdaQuery()
                        .apply(StringUtils.hasText(query.targetAccountType()),
                                "JSON_CONTAINS(COALESCE(target_account_types, JSON_ARRAY()), JSON_QUOTE({0}), '$')",
                                query.targetAccountType())
                        .eq(StringUtils.hasText(query.category()), BannerPO::getCategory, query.category())
                        .eq(StringUtils.hasText(query.type()), BannerPO::getType, query.type())
                        .eq(StringUtils.hasText(query.position()), BannerPO::getPosition, query.position())
                        .eq(StringUtils.hasText(query.status()), BannerPO::getStatus, query.status())
                        .orderByAsc(BannerPO::getSort)
                        .orderByDesc(BannerPO::getCreatedAt));
        // 2. 映射为应用层 PageResult
        List<BannerView> records = page.getRecords().stream().map(this::toView).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<BannerView> adminList(String position, String category, String type) {
        return listVisible(position, category, type, "ADMIN");
    }

    @Override
    public List<BannerView> portalList(String position, String category, String type) {
        return listVisible(position, category, type, "PORTAL");
    }

    private List<BannerView> listVisible(String position, String category, String type, String accountType) {
        // 1. 投放位必填
        if (!StringUtils.hasText(position)) {
            throw new DomainException("position is required");
        }
        // 2. 查询当前窗口内对目标账户类型可见的启用 Banner
        LocalDateTime now = LocalDateTime.now();
        List<BannerPO> list = bannerMapper.selectList(Wrappers.<BannerPO>lambdaQuery()
                .eq(BannerPO::getPosition, position)
                .eq(BannerPO::getStatus, "ENABLED")
                .apply("JSON_CONTAINS(COALESCE(target_account_types, JSON_ARRAY()), JSON_QUOTE({0}), '$')", accountType)
                .eq(StringUtils.hasText(category), BannerPO::getCategory, category)
                .eq(StringUtils.hasText(type), BannerPO::getType, type)
                .and(wrapper -> wrapper.isNull(BannerPO::getStartAt).or().le(BannerPO::getStartAt, now))
                .and(wrapper -> wrapper.isNull(BannerPO::getEndAt).or().ge(BannerPO::getEndAt, now))
                .orderByAsc(BannerPO::getSort));
        return list.stream().map(this::toView).toList();
    }

    private BannerView toView(BannerPO po) {
        String imageUrl = resolveImageUrl(po.getImage());
        return new BannerView(
                po.getId(),
                po.getTitle(),
                po.getImage(),
                imageUrl,
                po.getUrl(),
                po.getLinkType(),
                po.getSummary(),
                po.getDescription(),
                po.getCategory(),
                po.getType(),
                po.getPosition(),
                parseStringList(po.getTargetAccountTypes()),
                po.getSort(),
                po.getInteractionCount(),
                po.getStatus(),
                po.getStartAt(),
                po.getEndAt(),
                po.getCreatedAt(),
                po.getUpdatedAt());
    }

    private String resolveImageUrl(String image) {
        if (!StringUtils.hasText(image)) {
            return image;
        }
        // 已是绝对 URL 则直接返回；否则尝试按 objectName 解析
        if (image.startsWith("http://") || image.startsWith("https://") || image.startsWith("/")) {
            return image;
        }
        return fileApi.getByObjectName(image)
                .map(info -> StringUtils.hasText(info.url()) ? info.url() : image)
                .orElse(image);
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
