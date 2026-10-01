package io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.CodegenField;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.CodegenPlan;

import java.util.List;

/**
 * 代码生成渲染端口。
 *
 * Author: Charlie
 */
public interface CodegenRenderer {

    List<CodegenPreviewFileView> render(CodegenPlan plan, List<CodegenField> mainFields, List<CodegenField> subFields);
}
