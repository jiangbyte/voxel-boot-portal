package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.client;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.client.adapter.port.ClientModuleAdminService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.client.adapter.port.ClientModuleView;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.IamRelationTypes;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.support.MapBodies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

/**
 * 客户端模块管理实现。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class ClientModuleAdminServiceImpl implements ClientModuleAdminService {

    private final ClientModuleMapper mapper;


    @Override
    @Transactional
    public void create(Map<String, Object> body) {
        ClientModulePO po = new ClientModulePO();
        fill(po, body);
        mapper.insert(po);
    }

    @Override
    @Transactional
    public void update(Map<String, Object> body) {
        ClientModulePO po = mapper.selectById(MapBodies.str(body, "id"));
        if (po == null) {
            throw new DomainException(404, "Client module not found");
        }
        fill(po, body);
        mapper.updateById(po);
    }

    @Override
    @Transactional
    public void delete(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        mapper.deleteByIds(ids);
    }

    @Override
    public ClientModuleView detail(String id) {
        ClientModulePO po = mapper.selectById(id);
        if (po == null) {
            throw new DomainException(404, "Client module not found");
        }
        return toView(po);
    }

    @Override
    public PageResult<ClientModuleView> page(long current, long size, String code, String name,
                                             String accountType, String status) {
        Page<ClientModulePO> page = mapper.selectPage(new Page<>(current, size),
                Wrappers.<ClientModulePO>lambdaQuery()
                        .like(StringUtils.hasText(code), ClientModulePO::getCode, code)
                        .like(StringUtils.hasText(name), ClientModulePO::getName, name)
                        .eq(StringUtils.hasText(accountType), ClientModulePO::getAccountType, accountType)
                        .eq(StringUtils.hasText(status), ClientModulePO::getStatus, status)
                        .orderByAsc(ClientModulePO::getSort));
        return new PageResult<>(page.getRecords().stream().map(this::toView).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<ClientModuleView> selector(String accountType) {
        return mapper.selectList(Wrappers.<ClientModulePO>lambdaQuery()
                        .eq(ClientModulePO::getStatus, IamRelationTypes.STATUS_ENABLED)
                        .eq(StringUtils.hasText(accountType), ClientModulePO::getAccountType, accountType)
                        .orderByAsc(ClientModulePO::getSort))
                .stream().map(this::toView).toList();
    }

    private void fill(ClientModulePO po, Map<String, Object> body) {
        po.setName(MapBodies.str(body, "name"));
        po.setCode(MapBodies.str(body, "code"));
        po.setAccountType(MapBodies.str(body, "accountType"));
        po.setIcon(MapBodies.str(body, "icon"));
        po.setColor(MapBodies.str(body, "color"));
        po.setSort(MapBodies.integer(body, "sort"));
        String status = MapBodies.str(body, "status");
        po.setStatus(StringUtils.hasText(status) ? status : IamRelationTypes.STATUS_ENABLED);
        po.setDescription(MapBodies.str(body, "description"));
        po.setExtra(MapBodies.map(body, "extra"));
    }

    private ClientModuleView toView(ClientModulePO po) {
        return new ClientModuleView(po.getId(), po.getName(), po.getCode(), po.getAccountType(), po.getIcon(),
                po.getColor(), po.getSort(), po.getStatus(), po.getDescription(), po.getExtra(),
                po.getCreatedAt(), po.getUpdatedAt());
    }
}
