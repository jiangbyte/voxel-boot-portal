package io.github.jiangbyte.voxel.boot.portal.api.sys.web.codegen.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 字段批量更新入参。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "字段批量更新入参")
public class SysCodegenFieldsUpdateBatchParam {
    @NotBlank
    private String planId;
    private List<SysCodegenFieldUpdateItemParam> fields;
}
