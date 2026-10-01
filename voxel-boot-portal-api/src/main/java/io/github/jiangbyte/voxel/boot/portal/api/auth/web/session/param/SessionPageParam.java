package io.github.jiangbyte.voxel.boot.portal.api.auth.web.session.param;

import io.github.jiangbyte.voxel.boot.portal.api.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "在线会话分页查询")
@Data
@EqualsAndHashCode(callSuper = true)
public class SessionPageParam extends PageQuery {
    private String accountType;
    private String accountId;
    private String account;
    private String ip;
    private String keyword;
}
