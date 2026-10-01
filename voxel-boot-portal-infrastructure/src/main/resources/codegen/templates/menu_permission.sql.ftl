<#-- Author: Charlie -->
-- 由 Voxel 代码生成器生成。
-- Author: ${plan.author}
-- 生成时间：${generated_at}
-- db_vendor: ${db_vendor}
-- 执行前请按需调整 module_id/parent_id。
BEGIN;

INSERT INTO sys_resource (id, parent_id, code, name, resource_type, module_id, path, component, icon, sort, is_visible, is_cache, is_affix, status, description, extra)
VALUES (
  '${menu_permission.menu_id}',
  <#if plan.parent_resource_id?has_content>'${plan.parent_resource_id?replace("'", "''")}'<#else>NULL</#if>,
  '${permissionPrefix?replace(":", "_")}',
  '${plan.menu_name?replace("'", "''")}',
  'MENU',
  <#if plan.resource_module_id?has_content>'${plan.resource_module_id?replace("'", "''")}'<#else>NULL</#if>,
  '${plan.menu_path?replace("'", "''")}',
  '${plan.component_path?replace("'", "''")}',
  <#if plan.icon?has_content>'${plan.icon?replace("'", "''")}'<#else>NULL</#if>,
  ${plan.sort},
  true,
  false,
  false,
  'ENABLED',
  <#if plan.description?has_content>'${plan.description?replace("'", "''")}'<#else>NULL</#if>,
  '{}'
)
<#if db_vendor == "mysql">
ON DUPLICATE KEY UPDATE
  name = VALUES(name), path = VALUES(path), component = VALUES(component), updated_at = CURRENT_TIMESTAMP(6);
<#else>
ON CONFLICT (id) DO UPDATE
SET name = EXCLUDED.name, path = EXCLUDED.path, component = EXCLUDED.component, updated_at = now();
</#if>

<#list menu_permission.actions as action>
INSERT INTO sys_resource (id, parent_id, code, name, resource_type, module_id, sort, is_visible, is_cache, is_affix, status, extra)
VALUES (
  '${action.resource_id}',
  '${menu_permission.menu_id}',
  '${permissionPrefix?replace(":", "_")}_${action.key}',
  '${action.label}${plan.main_business_name}',
  'BUTTON',
  <#if plan.resource_module_id?has_content>'${plan.resource_module_id?replace("'", "''")}'<#else>NULL</#if>,
  ${action.sort},
  false,
  false,
  false,
  'ENABLED',
  '{}'
)
<#if db_vendor == "mysql">
ON DUPLICATE KEY UPDATE name = VALUES(name), updated_at = CURRENT_TIMESTAMP(6);
<#else>
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, updated_at = now();
</#if>

INSERT INTO sys_iam_relation (id, subject_type, subject_id, relation_type, target_type, target_id, target_key, grant_mode, data_scope, custom_scope_dept_ids, is_primary, sort, status, description, extra)
VALUES (
  '${action.relation_id}',
  'RESOURCE',
  '${action.resource_id}',
  'RESOURCE_PERMISSION',
  'PERMISSION',
  '',
  '${permissionPrefix}:${action.key}',
  'CASCADE',
  'ALL',
  '[]',
  false,
  ${action.sort},
  'ENABLED',
  '${action.label}${plan.main_business_name}',
  '{}'
)
<#if db_vendor == "mysql">
ON DUPLICATE KEY UPDATE description = VALUES(description), updated_at = CURRENT_TIMESTAMP(6);
<#else>
ON CONFLICT (id)
DO UPDATE SET description = EXCLUDED.description, updated_at = now();
</#if>

</#list>
COMMIT;
