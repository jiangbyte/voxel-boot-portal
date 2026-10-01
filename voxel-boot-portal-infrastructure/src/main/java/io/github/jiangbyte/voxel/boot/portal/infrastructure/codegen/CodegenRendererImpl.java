package io.github.jiangbyte.voxel.boot.portal.infrastructure.codegen;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenPreviewFileView;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenRenderer;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.CodegenField;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.CodegenPlan;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Entity;

/**
 * 代码生成渲染：优先 Freemarker 模板，失败则回退到确定性字符串模板，保证预览/下载非空。
 *
 * Author: Charlie
 */
@Component
public class CodegenRendererImpl implements CodegenRenderer {

    private final Configuration configuration;

    public CodegenRendererImpl() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "codegen/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setClassicCompatible(true);
        this.configuration = cfg;
    }

    @Override
    public List<CodegenPreviewFileView> render(CodegenPlan plan, List<CodegenField> mainFields, List<CodegenField> subFields) {
        // 1. 组装模板上下文
        Map<String, Object> ctx = buildContext(plan, mainFields, subFields == null ? List.of() : subFields);
        List<CodegenPreviewFileView> files = new ArrayList<>();
        String entity = plan.getEntityName();
        String basePackage = String.valueOf(ctx.get("basePackage"));
        String packagePath = basePackage.replace('.', '/');
        String moduleRoot = "module/" + firstSegment(plan.getModulePath());

        // 2. 尝试 Freemarker；任一失败则回退字符串模板
        files.add(renderOrFallback(
                moduleRoot + "/src/main/java/" + packagePath + "/entity/" + entity + ".java",
                "java", "Entity.java.ftl", ctx, fallbackEntity(plan, mainFields)));
        files.add(renderOrFallback(
                moduleRoot + "/src/main/java/" + packagePath + "/mapper/" + entity + "Mapper.java",
                "java", "Mapper.java.ftl", ctx, fallbackMapper(plan)));
        files.add(renderOrFallback(
                moduleRoot + "/src/main/java/" + packagePath + "/service/" + entity + "Service.java",
                "java", "Service.java.ftl", ctx, fallbackService(plan)));
        files.add(renderOrFallback(
                moduleRoot + "/src/main/java/" + packagePath + "/controller/Admin" + entity + "Controller.java",
                "java", "Controller.java.ftl", ctx, fallbackController(plan)));
        files.add(new CodegenPreviewFileView(
                "voxel-admin/src/api/" + toSnake(entity) + ".ts",
                "typescript",
                fallbackApiTs(plan)));
        files.add(new CodegenPreviewFileView(
                "voxel-admin/src/views/" + plan.getComponentPath() + "/index.vue",
                "vue",
                fallbackVue(plan, mainFields)));
        files.add(new CodegenPreviewFileView(
                "scripts/" + toSnake(entity) + "_menu_permission.sql",
                "sql",
                fallbackSql(plan)));
        return files;
    }

    private CodegenPreviewFileView renderOrFallback(String path, String language, String templateName,
                                                    Map<String, Object> ctx, String fallback) {
        try {
            Template template = configuration.getTemplate(templateName);
            StringWriter writer = new StringWriter();
            template.process(ctx, writer);
            String content = writer.toString();
            if (!StringUtils.hasText(content)) {
                content = fallback;
            }
            return new CodegenPreviewFileView(path, language, content);
        } catch (Exception ex) {
            return new CodegenPreviewFileView(path, language, fallback);
        }
    }

    private Map<String, Object> buildContext(CodegenPlan plan, List<CodegenField> mainFields, List<CodegenField> subFields) {
        Map<String, Object> ctx = new HashMap<>();
        String basePackage = plan.getModulePath() == null ? "io.github.jiangbyte.voxel.boot.portal" : plan.getModulePath().trim();
        ctx.put("author", plan.getAuthor());
        ctx.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        ctx.put("basePackage", basePackage);
        ctx.put("paramPackage", basePackage + ".param");
        ctx.put("permissionPrefix", plan.getPermissionPrefix());
        ctx.put("apiPrefix", plan.getApiPrefix());
        ctx.put("auditResourceType", "sys_" + toSnake(plan.getEntityName()));
        ctx.put("hasTree", false);
        ctx.put("hasSub", !subFields.isEmpty());
        ctx.put("genType", plan.getGenType());
        ctx.put("entityName", plan.getEntityName());
        ctx.put("tableName", plan.getTableName());
        ctx.put("businessName", plan.getBusinessName());
        ctx.put("pkColumn", plan.getPkColumn());
        ctx.put("menuName", plan.getMenuName());
        ctx.put("menuPath", plan.getMenuPath());
        ctx.put("componentPath", plan.getComponentPath());
        ctx.put("main", fieldMaps(mainFields));
        ctx.put("sub", fieldMaps(subFields));
        ctx.put("fields", fieldMaps(mainFields));
        ctx.put("columns", fieldMaps(mainFields));
        return ctx;
    }

    private static List<Map<String, Object>> fieldMaps(List<CodegenField> fields) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CodegenField field : fields) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("columnName", field.getColumnName());
            map.put("column_name", field.getColumnName());
            map.put("label", field.getLabel());
            map.put("dbType", field.getDbType());
            map.put("valueType", field.getValueType());
            map.put("uiType", field.getUiType());
            map.put("widget", field.getWidget());
            map.put("dictCode", field.getDictCode());
            map.put("dict_code", field.getDictCode());
            map.put("javaType", DbTypeMapper.toJavaType(field.getValueType()));
            map.put("property", toCamel(field.getColumnName()));
            map.put("primaryKey", field.getPrimaryKey());
            map.put("nullable", field.getNullable());
            map.put("required", field.getRequired());
            map.put("inTable", field.getInTable());
            map.put("inForm", field.getInForm());
            map.put("inDetail", field.getInDetail());
            map.put("inQuery", field.getInQuery());
            list.add(map);
        }
        return list;
    }

    private static String fallbackEntity(CodegenPlan plan, List<CodegenField> fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(plan.getModulePath()).append(".entity;\n\n");
        sb.append("/**\n * ").append(plan.getBusinessName()).append(" 实体（codegen）。\n * Author: ")
                .append(plan.getAuthor()).append("\n */\n");
        sb.append("public class ").append(plan.getEntityName()).append(" {\n");
        for (CodegenField field : fields) {
            sb.append("    private ").append(DbTypeMapper.toJavaType(field.getValueType()))
                    .append(' ').append(toCamel(field.getColumnName())).append(";\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    private static String fallbackMapper(CodegenPlan plan) {
        return "package " + plan.getModulePath() + ".mapper;\n\n"
                + "public interface " + plan.getEntityName() + "Mapper {\n}\n";
    }

    private static String fallbackService(CodegenPlan plan) {
        return "package " + plan.getModulePath() + ".service;\n\n"
                + "public interface " + plan.getEntityName() + "Service {\n}\n";
    }

    private static String fallbackController(CodegenPlan plan) {
        return "package " + plan.getModulePath() + ".controller;\n\n"
                + "/** Admin " + plan.getBusinessName() + " API */\n"
                + "public class Admin" + plan.getEntityName() + "Controller {\n"
                + "    // apiPrefix=" + plan.getApiPrefix() + "\n"
                + "}\n";
    }

    private static String fallbackApiTs(CodegenPlan plan) {
        return "export const " + toCamel(plan.getEntityName()) + "Api = {\n"
                + "  page: '" + plan.getApiPrefix() + "/page',\n"
                + "  detail: '" + plan.getApiPrefix() + "/detail',\n"
                + "};\n";
    }

    private static String fallbackVue(CodegenPlan plan, List<CodegenField> fields) {
        StringBuilder cols = new StringBuilder();
        for (CodegenField field : fields) {
            if (java.util.Objects.equals(field.getInTable(), 1)) {
                cols.append("  <!-- ").append(field.getColumnName()).append(" -->\n");
            }
        }
        return "<template>\n  <div>" + plan.getBusinessName() + "</div>\n"
                + cols + "</template>\n";
    }

    private static String fallbackSql(CodegenPlan plan) {
        return "-- menu for " + plan.getMenuName() + "\n"
                + "-- path=" + plan.getMenuPath() + " component=" + plan.getComponentPath() + "\n"
                + "-- permission=" + plan.getPermissionPrefix() + "\n";
    }

    private static String firstSegment(String modulePath) {
        if (!StringUtils.hasText(modulePath)) {
            return "module";
        }
        String[] parts = modulePath.split("\\.");
        return parts[parts.length - 1];
    }

    private static String toSnake(String name) {
        if (name == null || name.isBlank()) {
            return "generated";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append('_');
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    private static String toCamel(String column) {
        if (column == null || column.isBlank()) {
            return "field";
        }
        String[] parts = column.toLowerCase(Locale.ROOT).split("_");
        StringBuilder sb = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].isEmpty()) {
                continue;
            }
            sb.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
        }
        return sb.toString();
    }
}
