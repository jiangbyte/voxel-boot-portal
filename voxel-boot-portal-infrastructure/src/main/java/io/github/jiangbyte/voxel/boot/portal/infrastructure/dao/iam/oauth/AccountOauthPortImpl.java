package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.oauth;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.AccountOauthPort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthBindingInfo;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

/**
 * {@link AccountOauthPort} 基于 MyBatis-Plus 的实现。
 *
 * Author: Charlie
 */
@Component
@RequiredArgsConstructor
public class AccountOauthPortImpl implements AccountOauthPort {

    private final AccountOauthBindingMapper mapper;


    @Override
    public List<OauthBindingInfo> listByAccount(String accountId) {
        return mapper.selectList(Wrappers.<AccountOauthBindingPO>lambdaQuery()
                        .eq(AccountOauthBindingPO::getAccountId, accountId)
                        .orderByDesc(AccountOauthBindingPO::getBoundAt))
                .stream().map(this::toInfo).toList();
    }

    @Override
    public int countByAccount(String accountId) {
        Long count = mapper.selectCount(Wrappers.<AccountOauthBindingPO>lambdaQuery()
                .eq(AccountOauthBindingPO::getAccountId, accountId));
        return count == null ? 0 : count.intValue();
    }

    @Override
    public OauthBindingInfo findByProviderOpenId(String provider, String openId) {
        AccountOauthBindingPO po = mapper.selectOne(Wrappers.<AccountOauthBindingPO>lambdaQuery()
                .eq(AccountOauthBindingPO::getProvider, normalize(provider))
                .eq(AccountOauthBindingPO::getOpenId, openId)
                .last("LIMIT 1"));
        return toInfo(po);
    }

    @Override
    public OauthBindingInfo findByWechatUnionId(String unionId) {
        if (!StringUtils.hasText(unionId)) {
            return null;
        }
        AccountOauthBindingPO po = mapper.selectOne(Wrappers.<AccountOauthBindingPO>lambdaQuery()
                .eq(AccountOauthBindingPO::getUnionId, unionId.trim())
                .in(AccountOauthBindingPO::getProvider, List.of("WECHAT_OPEN", "WECHAT_MP"))
                .last("LIMIT 1"));
        return toInfo(po);
    }

    @Override
    public void upsertBinding(String accountId, String provider, String openId, String unionId,
                              String nickname, String avatar, String rawProfileJson) {
        // 1. 按账号+提供商查找已有绑定
        String prov = normalize(provider);
        AccountOauthBindingPO existing = mapper.selectOne(Wrappers.<AccountOauthBindingPO>lambdaQuery()
                .eq(AccountOauthBindingPO::getAccountId, accountId)
                .eq(AccountOauthBindingPO::getProvider, prov)
                .last("LIMIT 1"));
        LocalDateTime now = LocalDateTime.now();
        if (existing == null) {
            // 2. 新建
            AccountOauthBindingPO po = new AccountOauthBindingPO();
            po.setId(UUID.randomUUID().toString().replace("-", ""));
            po.setAccountId(accountId);
            po.setProvider(prov);
            po.setOpenId(openId);
            po.setUnionId(blankToNull(unionId));
            po.setNickname(nickname);
            po.setAvatar(avatar);
            po.setRawProfile(StringUtils.hasText(rawProfileJson) ? rawProfileJson : "{}");
            po.setBoundAt(now);
            mapper.insert(po);
            return;
        }
        // 3. 更新快照
        existing.setOpenId(openId);
        existing.setUnionId(blankToNull(unionId));
        existing.setNickname(nickname);
        existing.setAvatar(avatar);
        existing.setRawProfile(StringUtils.hasText(rawProfileJson) ? rawProfileJson : "{}");
        existing.setBoundAt(now);
        mapper.updateById(existing);
    }

    @Override
    public void unbind(String accountId, String provider) {
        mapper.delete(Wrappers.<AccountOauthBindingPO>lambdaQuery()
                .eq(AccountOauthBindingPO::getAccountId, accountId)
                .eq(AccountOauthBindingPO::getProvider, normalize(provider)));
    }

    private OauthBindingInfo toInfo(AccountOauthBindingPO po) {
        if (po == null) {
            return null;
        }
        OauthBindingInfo info = new OauthBindingInfo();
        info.setId(po.getId());
        info.setAccountId(po.getAccountId());
        info.setProvider(po.getProvider());
        info.setOpenId(po.getOpenId());
        info.setUnionId(po.getUnionId());
        info.setNickname(po.getNickname());
        info.setAvatar(po.getAvatar());
        info.setRawProfile(po.getRawProfile());
        info.setBoundAt(po.getBoundAt());
        return info;
    }

    private static String normalize(String provider) {
        return provider == null ? "" : provider.trim().toUpperCase(Locale.ROOT);
    }

    private static String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
