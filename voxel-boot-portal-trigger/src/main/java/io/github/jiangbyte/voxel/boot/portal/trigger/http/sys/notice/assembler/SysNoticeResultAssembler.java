package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.notice.assembler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jiangbyte.voxel.boot.portal.api.sys.web.notice.result.SysNoticeResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.adapter.port.NoticeView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 将应用层 NoticeView 装配为 API 响应 SysNoticeResult。
 *
 * Author: Charlie
 */
@Mapper
public abstract class SysNoticeResultAssembler {

    public static final SysNoticeResultAssembler INSTANCE = Mappers.getMapper(SysNoticeResultAssembler.class);

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {};
    private static final TypeReference<Map<String, Object>> OBJECT_MAP = new TypeReference<>() {};

    @Mapping(source = "pinned", target = "isPinned")
    @Mapping(source = "targetAccountTypes", target = "targetAccountTypes", qualifiedByName = "readStringList")
    @Mapping(source = "targetAccountIds", target = "targetAccountIds", qualifiedByName = "readStringList")
    @Mapping(source = "targetDeptIds", target = "targetDeptIds", qualifiedByName = "readStringList")
    @Mapping(source = "targetRoleIds", target = "targetRoleIds", qualifiedByName = "readStringList")
    @Mapping(source = "publishLocations", target = "publishLocations", qualifiedByName = "readObjectMap")
    @Mapping(source = "extra", target = "extra", qualifiedByName = "readObjectMap")
    public abstract SysNoticeResult toResult(NoticeView view);

    /**
     * JSON 数组列解析为字符串列表。
     */
    @Named("readStringList")
    protected List<String> readStringList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            List<String> list = JSON.readValue(json, STRING_LIST);
            return list == null ? Collections.emptyList() : list;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    /**
     * JSON 对象列解析为 Map；空数组统一为空对象。
     */
    @Named("readObjectMap")
    protected Map<String, Object> readObjectMap(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        String trimmed = json.trim();
        if ("[]".equals(trimmed)) {
            return Collections.emptyMap();
        }
        try {
            Map<String, Object> map = JSON.readValue(trimmed, OBJECT_MAP);
            return map == null ? Collections.emptyMap() : map;
        } catch (Exception ex) {
            return Collections.emptyMap();
        }
    }
}
