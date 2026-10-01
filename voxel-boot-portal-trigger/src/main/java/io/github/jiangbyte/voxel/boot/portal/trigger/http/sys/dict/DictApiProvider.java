package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.dict;

import io.github.jiangbyte.voxel.boot.portal.cases.sys.dict.DictApi;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.port.DictItem;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.port.DictQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 跨上下文字典 API 适配：委托查询服务。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class DictApiProvider implements DictApi {

    private final DictQueryService dictQueryService;


    @Override
    public List<DictItem> listByType(String dictType) {
        return dictQueryService.listByType(dictType);
    }
}
