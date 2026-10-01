<!--
  由 Voxel 代码生成器生成。
  Author: ${plan.author}
  生成时间：${generated_at}
-->

<script setup lang="ts">
import type { FormInst, FormRules } from 'naive-ui'
import { ${api_export_name} } from '@/api'
<#if target.has_form_bool || target.has_form_int || target.has_form_float>
import { wireFields } from '@/utils/wire'
</#if>
import { createRequiredRule<#if target.has_form_datetime>, toApiDateTime, toFormDateTime</#if> } from '@/utils'
<#if target.has_form_icon>
import IconSelect from '@/components/common/IconSelect.vue'
</#if>
<#if target.has_form_editor>
import { <#if target.has_form_richtext>RichTextEditor, </#if><#if target.has_form_markdown>MdEditor, </#if><#if target.has_form_code>MonacoEditor</#if> } from '@/components/editor'
</#if>
import { computed, reactive, ref } from 'vue'

const emit = defineEmits<{
  saved: []
}>()

const formRef = ref<FormInst | null>(null)
const defaultFormData: Record<string, any> = {
<#list target.form_fields as field>
<#if has_tree_parent_form && field.name == plan.tree_parent_field>
  ${field.name}: null,
<#else>
  ${field.name}: ${field.vue_default},
</#if>
</#list>
}
const state = reactive({
  showModal: false,
  loading: false,
<#if has_tree_parent_form>
  treeLoading: false,
</#if>
  submitLoading: false,
  dataId: null as string | null,
  formModel: normalizeFormData(),
<#if has_tree_parent_form>
  treeRows: [] as any[],
</#if>
})

const modalTitle = computed(() => state.dataId ? '编辑${plan.main_business_name}' : '新增${plan.main_business_name}')
<#if has_tree_parent_form>
const parentTreeOptions = computed(() =>
  buildParentTreeOptions(state.treeRows, state.dataId),
)
</#if>
const rules = computed<FormRules>(() => ({
<#list target.form_fields as field><#if field.required || field.is_json>
  ${field.name}: [
<#if field.required>
<#if field.value_type == "bool">
    {
      validator: () => typeof state.formModel.${field.name} === 'boolean',
      message: '请选择${field.label}',
      trigger: 'change',
    },
<#elseif (field.value_type == "int" || field.value_type == "float")>
    {
      validator: () => typeof state.formModel.${field.name} === 'number' && Number.isFinite(state.formModel.${field.name}),
      message: '请输入${field.label}',
      trigger: ['input', 'blur'],
    },
<#else>
    createRequiredRule('${field.label}', <#if field.widget == "dict" || field.value_type == "bool" || field.is_datetime || field.widget == "icon">'change'<#else>'input'</#if>),
</#if>
</#if>
<#if field.is_json>
    {
      validator: () => isValidJsonValue(state.formModel.${field.name}),
      message: '请输入合法 JSON 对象',
      trigger: ['input', 'blur'],
    },
</#if>
  ],
</#if></#list>
}))

async function openModal(id?: string, defaults: Partial<typeof defaultFormData> = {}) {
  state.dataId = id ?? null
  state.formModel = normalizeFormData(defaults)
  state.showModal = true
<#if has_tree_parent_form>
  await fetchTreeRows()
</#if>
  if (id) {
    await fetchDetail(id)
  }
}
<#if has_tree_parent_form>

async function fetchTreeRows() {
  state.treeLoading = true
  try {
    const response = await ${api_export_name}.tree()
    state.treeRows = response.data ?? []
  } finally {
    state.treeLoading = false
  }
}
</#if>

async function fetchDetail(id: string) {
  state.loading = true
  try {
    const response = await ${api_export_name}.detail({ id })
    state.formModel = normalizeFormData(response.data ?? {})
  } finally {
    state.loading = false
  }
}

function normalizeFormData(data: Record<string, any> = {}): Record<string, any> {
  return {
    ...defaultFormData,
    ...data,
<#if target.has_form_bool || target.has_form_int || target.has_form_float>
    ...wireFields(data, {
<#list target.form_fields as field><#if field.is_bool>
      ${field.name}: 'bool',
</#if><#if field.value_type == "int">
      ${field.name}: 'int',
</#if><#if field.value_type == "float">
      ${field.name}: 'float',
</#if></#list>
    }, defaultFormData),
</#if>
<#list target.form_fields as field><#if field.is_datetime>
    ${field.name}: toFormDateTime(data.${field.name}),
</#if></#list>
<#list target.form_fields as field><#if field.is_json>
    ${field.name}: stringifyJsonValue(data.${field.name}),
</#if></#list>
  }
}
<#if target.needs_submit_normalize || has_tree_parent_form>

function normalizeSubmitData(data: Record<string, any>): Record<string, any> {
  return {
    ...data,
<#if has_tree_parent_form>
    ${plan.tree_parent_field}: data.${plan.tree_parent_field} === '' ? null : (data.${plan.tree_parent_field} ?? null),
</#if>
<#list target.form_fields as field><#if field.is_datetime>
    ${field.name}: toApiDateTime(data.${field.name}),
</#if></#list>
<#list target.form_fields as field><#if field.is_json>
    ${field.name}: parseJsonValue(data.${field.name}),
</#if></#list>
  }
}
</#if>
<#if target.has_form_json>

function parseJsonValue(value: unknown) {
  const text = String(value ?? '').trim()
  if (!text) {
    return {}
  }
  const parsed = JSON.parse(text)
  if (Array.isArray(parsed) || typeof parsed !== 'object' || parsed === null) {
    throw new Error('JSON value must be an object')
  }
  return parsed
}

function isValidJsonValue(value: unknown) {
  try {
    parseJsonValue(value)
    return true
  } catch {
    return false
  }
}

function stringifyJsonValue(value: unknown) {
  if (value === undefined || value === null || value === '') {
    return '{}'
  }
  if (typeof value === 'string') {
    try {
      return JSON.stringify(JSON.parse(value), null, 2)
    } catch {
      return value
    }
  }
  return JSON.stringify(value, null, 2)
}
</#if>

function closeModal() {
  state.showModal = false
  state.submitLoading = false
}
<#if has_tree_parent_form>

function buildParentTreeOptions(items: any[], editingId: string | null, disabledParent = false): any[] {
  return items.map((item) => {
    const itemId = String(item.${main.pk_name} ?? '')
    const disabled = disabledParent || (editingId !== null && itemId === editingId)
    return {
      key: item.${main.pk_name},
      label: String(item.${plan.tree_label_field} ?? item.${main.pk_name} ?? ''),
      disabled,
      children: buildParentTreeOptions(item.children ?? [], editingId, disabled),
    }
  })
}
</#if>

async function submitForm() {
  await formRef.value?.validate()
  state.submitLoading = true
  try {
    const payload = ${(target.needs_submit_normalize || has_tree_parent_form)?then("normalizeSubmitData(state.formModel)", "state.formModel")}
    if (state.dataId) {
      await ${api_export_name}.update({ ...payload, ${target.pk_name}: state.dataId })
      window.$message.success('更新成功')
    } else {
      await ${api_export_name}.create(payload)
      window.$message.success('创建成功')
    }
    emit('saved')
    closeModal()
  } finally {
    state.submitLoading = false
  }
}

defineExpose({
  openModal,
})
</script>

<template>
  <NModal
    v-model:show="state.showModal"
    preset="card"
    draggable
    :mask-closable="false"
    :title="modalTitle"
    style="width: <#if target.has_form_editor>960px<#else>720px</#if>"
    :segmented="{ content: true, action: true }"
  >
    <NSpin :show="state.loading<#if has_tree_parent_form> || state.treeLoading</#if>">
      <NScrollbar class="max-h-[min(620px,calc(100vh-300px))] pr-16px">
        <NForm ref="formRef" :model="state.formModel" :rules="rules" label-placement="left" label-width="110" :disabled="state.loading<#if has_tree_parent_form> || state.treeLoading</#if> || state.submitLoading">
<#list target.form_fields as field>
          <NFormItem label="<#if has_tree_parent_form && field.name == plan.tree_parent_field>父级<#else>${field.label}</#if>" path="${field.name}">
<#if has_tree_parent_form && field.name == plan.tree_parent_field>
            <NTreeSelect
              v-model:value="state.formModel.${field.name}"
              clearable
              filterable
              :options="parentTreeOptions"
              :loading="state.treeLoading"
              key-field="key"
              label-field="label"
              children-field="children"
              class="w-full"
            />
<#elseif field.widget == "number" || (field.value_type == "int" || field.value_type == "float")>
            <NInputNumber v-model:value="state.formModel.${field.name}" class="w-full" />
<#elseif field.widget == "richtext">
            <RichTextEditor v-model:value="state.formModel.${field.name}" :height="360" class="w-full" />
<#elseif field.widget == "markdown">
            <MdEditor v-model:value="state.formModel.${field.name}" :height="360" class="w-full" />
<#elseif field.widget == "code">
            <MonacoEditor v-model:value="state.formModel.${field.name}" language="${field.code_language}" :height="360" class="w-full" />
<#elseif field.widget == "icon">
            <IconSelect v-model:value="state.formModel.${field.name}" class="w-full" />
<#elseif field.widget == "textarea">
            <NInput v-model:value="state.formModel.${field.name}" type="textarea" :autosize="{ minRows: 3, maxRows: 8 }" />
<#elseif field.widget == "dict" && field.dict_code>
            <DictSelect v-model="state.formModel.${field.name}" dict-code="${field.dict_code}" />
<#elseif field.is_json>
            <NInput v-model:value="state.formModel.${field.name}" type="textarea" :autosize="{ minRows: 4, maxRows: 12 }" />
<#elseif field.value_type == "bool">
            <NSwitch v-model:value="state.formModel.${field.name}" />
<#elseif field.widget == "datetime" || field.value_type == "datetime">
            <NDatePicker v-model:formatted-value="state.formModel.${field.name}" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" class="w-full" clearable />
<#else>
            <NInput v-model:value="state.formModel.${field.name}" />
</#if>
          </NFormItem>
</#list>
        </NForm>
      </NScrollbar>
    </NSpin>

    <template #action>
      <NSpace justify="end">
        <NButton @click="closeModal">取消</NButton>
        <NButton type="primary" :loading="state.submitLoading" @click="submitForm">确认</NButton>
      </NSpace>
    </template>
  </NModal>
</template>
