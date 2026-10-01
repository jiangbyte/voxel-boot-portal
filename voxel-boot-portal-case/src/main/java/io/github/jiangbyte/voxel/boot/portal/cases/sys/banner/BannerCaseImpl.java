package io.github.jiangbyte.voxel.boot.portal.cases.sys.banner;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.repository.BannerRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.BannerStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port.BannerInteractionCounter;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port.BannerLinkGuard;
import lombok.RequiredArgsConstructor;

/**
 * Banner 写用例：创建、更新、删除。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class BannerCaseImpl implements IBannerCase {

    private final BannerRepository bannerRepository;
    private final BannerLinkGuard bannerLinkGuard;
    private final BannerInteractionCounter bannerInteractionCounter;


    /**
     * 创建 Banner。
     */
    @Transactional
    public void create(CreateBannerCommand command) {
        // 1. 校验链接安全性
        bannerLinkGuard.validate(command.linkType(), command.url());
        // 2. 工厂创建聚合（可见账户类型序列化为 JSON 数组字符串）
        Banner banner = Banner.create(
                command.title(),
                command.image(),
                command.url(),
                command.linkType(),
                command.summary(),
                command.description(),
                command.category(),
                command.type(),
                command.position(),
                toJsonArray(command.targetAccountTypes()),
                command.sort(),
                command.status(),
                command.startAt(),
                command.endAt());
        // 3. 持久化
        bannerRepository.save(banner);
    }

    /**
     * 更新 Banner。
     */
    @Transactional
    public void update(UpdateBannerCommand command) {
        // 1. 校验链接安全性
        bannerLinkGuard.validate(command.linkType(), command.url());
        // 2. 加载聚合
        Banner banner = bannerRepository.findById(command.id())
                .orElseThrow(() -> new DomainException(404, "Banner not found"));
        // 3. 聚合内更新并保存
        banner.update(
                command.title(),
                command.image(),
                command.url(),
                command.linkType(),
                command.summary(),
                command.description(),
                command.category(),
                command.type(),
                command.position(),
                toJsonArray(command.targetAccountTypes()),
                command.sort(),
                command.status(),
                command.startAt(),
                command.endAt());
        bannerRepository.save(banner);
    }

    /**
     * 批量删除 Banner。
     */
    @Transactional
    public void delete(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 逐个加载并删除
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }
            bannerRepository.findById(id).ifPresent(banner -> {
                banner.markDeleted();
                bannerRepository.remove(banner);
            });
        }
    }

    /**
     * 门户端 Banner 互动计数。
     */
    @Transactional
    public void interaction(String id) {
        // 1. 加载聚合
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new DomainException(404, "Banner not found"));
        // 2. 校验对 PORTAL 可见（启用、目标含 PORTAL、在展示窗口内）
        if (banner.getStatus() != BannerStatus.ENABLED || !containsPortal(banner.getTargetAccountTypes())) {
            throw new DomainException("Banner is not publicly visible");
        }
        LocalDateTime now = LocalDateTime.now();
        if (banner.getStartAt() != null && banner.getStartAt().isAfter(now)) {
            throw new DomainException("Banner is not publicly visible");
        }
        if (banner.getEndAt() != null && banner.getEndAt().isBefore(now)) {
            throw new DomainException("Banner is not publicly visible");
        }
        // 3. 计数：Redis 增量优先，否则 DB +1
        bannerInteractionCounter.increment(id);
    }

    private static boolean containsPortal(String targetAccountTypesJson) {
        if (!StringUtils.hasText(targetAccountTypesJson)) {
            return false;
        }
        String lower = targetAccountTypesJson.toLowerCase(Locale.ROOT);
        return lower.contains("\"portal\"");
    }

    private static String toJsonArray(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            String value = values.get(i) == null ? "" : values.get(i);
            sb.append('"')
                    .append(value.replace("\\", "\\\\").replace("\"", "\\\""))
                    .append('"');
        }
        sb.append(']');
        return sb.toString();
    }
}
