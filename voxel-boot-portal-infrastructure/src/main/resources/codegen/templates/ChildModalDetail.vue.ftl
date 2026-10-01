<!--
  由 Voxel 代码生成器生成。
  Author: ${plan.author}
  生成时间：${generated_at}
-->

<script setup lang="ts">
import { ${api_export_name} } from '@/api'
<#if target.has_detail_bool>
import { wireBool } from '@/utils/wire'
</#if>
import { <#if target.has_detail_dict>createTagColor, dictTypeColor, dictTypeData, </#if>displayValue, formatDateTime } from '@/utils'
<#if target.has_detail_editor>
import { <#if target.has_detail_richtext>RichTextPreview, </#if><#if target.has_detail_markdown>MdPreview, </#if><#if target.has_detail_code>MonacoPreview</#if> } from '@/components/editor'
</#if>
import { reactive } from 'vue'

const state = reactive({
  showModal: false,
  loading: false,
  detail: {} as any,
})

async function openModal(id: string) {
  state.detail = {}
  state.showModal = true
  await fetchDetail(id)
}

async function fetchDetail(id: string) {
  state.loading = true
  try {
    const response = await ${api_export_name}.childDetail({ id })
    state.detail = response.data ?? {}
  } finally {
    state.loading = false
  }
}
<#if target.has_detail_json>

function formatJsonValue(value: unknown) {
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

defineExpose({
  openModal,
})
</script>

<template>
  <NModal v-model:show="state.showModal" preset="card" draggable :mask-closable="false" title="${(plan.sub_business_name)?has_content?then(plan.sub_business_name, "明细")}详情" style="width: <#if target.has_detail_editor>960px<#else>680px</#if>">
    <NScrollbar class="max-h-[min(620px,calc(100vh-300px))] pr-16px">
      <NSpin :show="state.loading">
        <NDescriptions label-placement="left" bordered :column="1">
<#list target.detail_fields as field>
          <NDescriptionsItem label="${field.label}">
<#if field.dict_code?? && field.dict_code?has_content>
            <NTag :color="createTagColor(dictTypeColor('${field.dict_code}', state.detail.${field.name}))" :bordered="false">
              ${"{{"} dictTypeData('${field.dict_code}', state.detail.${field.name}) || displayValue(state.detail.${field.name}) ${"}" }}
            </NTag>
<#elseif field.value_type == "datetime">
            ${"{{"} formatDateTime(state.detail.${field.name}) ${"}" }}
<#elseif field.widget == "richtext">
            <RichTextPreview :value="state.detail.${field.name}" />
<#elseif field.widget == "markdown">
            <MdPreview :value="state.detail.${field.name}" />
<#elseif field.widget == "code">
            <MonacoPreview :value="state.detail.${field.name}" language="${field.code_language}" height="240px" />
<#elseif field.widget == "icon">
            <NFlex v-if="state.detail.${field.name}" align="center" :size="8">
              <NovaIcon :icon="String(state.detail.${field.name})" :size="18" />
              ${"{{"} displayValue(state.detail.${field.name}) ${"}" }}
            </NFlex>
            <span v-else>-</span>
<#elseif field.is_bool>
            ${"{{"} wireBool(state.detail.${field.name}) ? '是' : '否' ${"}" }}
<#elseif field.is_json>
            <NCode :code="formatJsonValue(state.detail.${field.name})" language="json" word-wrap />
<#else>
            ${"{{"} displayValue(state.detail.${field.name}) ${"}" }}
</#if>
          </NDescriptionsItem>
</#list>
          <NDescriptionsItem label="创建时间">${"{{"} formatDateTime(state.detail.created_at) ${"}" }}</NDescriptionsItem>
          <NDescriptionsItem label="更新时间">${"{{"} formatDateTime(state.detail.updated_at) ${"}" }}</NDescriptionsItem>
        </NDescriptions>
      </NSpin>
    </NScrollbar>
  </NModal>
</template>
