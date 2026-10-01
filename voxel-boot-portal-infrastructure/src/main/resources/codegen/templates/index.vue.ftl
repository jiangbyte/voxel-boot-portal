<!--
  由 Voxel 代码生成器生成。
  Author: ${plan.author}
  生成时间：${generated_at}
-->

<script setup lang="tsx">
<#if plan.gen_type != "TREE">
import type { PaginationProps } from 'naive-ui'
</#if>
import type { ProDataTableColumns, ProSearchFormColumns } from 'pro-naive-ui'
import { Icon } from '@iconify/vue/offline'
import { ${api_export_name} } from '@/api'
<#if plan.gen_type != "TREE">
import { readPageMeta<#if main.has_table_bool || (sub?? && sub.has_table_bool)>, wireBool</#if> } from '@/utils/wire'
</#if>
import { <#if main.has_query_dict || (sub?? && sub.has_query_dict)>dictList, </#if><#if main.has_table_dict || (sub?? && sub.has_table_dict)>createTagColor, dictTypeColor, dictTypeData, displayValue, </#if>formatDateTime, hasPermission, normalizeSearchValues, renderButtonIcon } from '@/utils'
import { NButton, NFlex, NIcon<#if plan.gen_type == "LEFT_TREE_TABLE">, NInput, NInputGroup</#if><#if main.has_table_tag || (sub?? && sub.has_table_tag)>, NTag</#if> } from 'naive-ui'
import { createProSearchForm, ProCard, ProDataTable, ProSearchForm } from 'pro-naive-ui'
import { computed, onMounted, reactive, ref } from 'vue'
<#if has_sub && (sub??)>
import ChildModalDetail from './components/children/ChildModalDetail.vue'
import ChildModalForm from './components/children/ChildModalForm.vue'
</#if>
<#if plan.gen_type != "LEFT_TREE_TABLE">
import ModalDetail from './components/ModalDetail.vue'
import ModalForm from './components/ModalForm.vue'

const formModalRef = ref<any>(null)
const detailModalRef = ref<any>(null)
</#if>
<#if has_sub && (sub??)>
const childFormModalRef = ref<any>(null)
const childDetailModalRef = ref<any>(null)
</#if>
const state = reactive({
<#if plan.gen_type != "TREE" && plan.gen_type != "LEFT_TREE_TABLE">
  rows: [] as any[],
  total: 0,
  loading: false,
  page: 1,
  pageSize: 20,
</#if>
<#if plan.gen_type != "LEFT_TREE_TABLE">
  searchValues: {} as any,
  checkedRowKeys: [] as string[],
</#if>
<#if has_tree>
  treeRows: [] as any[],
  treeLoading: false,
<#if plan.gen_type == "LEFT_TREE_TABLE">
  selectedTreeKeys: [] as string[],
  treeKeyword: '',
</#if>
</#if>
<#if has_sub && (sub??)>
  childRows: [] as any[],
  childTotal: 0,
  childLoading: false,
<#if plan.gen_type == "MASTER_DETAIL">
  childDrawerVisible: false,
</#if>
  childSearchValues: {} as any,
  childCheckedRowKeys: [] as string[],
  childPage: 1,
  childPageSize: 20,
  selectedMasterId: null as string | null,
</#if>
})

<#if plan.gen_type != "LEFT_TREE_TABLE">
const hasCheckedRows = computed(() => state.checkedRowKeys.length > 0)
</#if>
<#if plan.gen_type == "LEFT_TREE_TABLE">
const treeData = computed(() => buildTreeNodes(state.treeRows))
<#elseif plan.gen_type == "TREE">
const filteredTreeRows = computed(() => filterTreeRows(state.treeRows, state.searchValues))
</#if>
<#if has_sub && (sub??)>
const hasChildCheckedRows = computed(() => state.childCheckedRowKeys.length > 0)
const canCreateChild = computed(() => Boolean(state.selectedMasterId))
</#if>

<#if plan.gen_type != "LEFT_TREE_TABLE">
const searchForm = createProSearchForm<any>({
  defaultCollapsed: true,
  onSubmit(values) {
    state.searchValues = normalizeSearchValues(values)
<#if plan.gen_type == "TREE">
  },
  onReset() {
    state.searchValues = {}
  },
<#else>
    state.page = 1
    fetchPage()
  },
  onReset() {
    state.searchValues = {}
    state.page = 1
    fetchPage()
  },
</#if>
})

const searchColumns = computed<ProSearchFormColumns<any>>(() => [
<#list main.query_fields as field>
<#if field.dict_code?? && field.dict_code?has_content>
  {
    title: '${field.label}',
    path: '${field.name}',
    field: 'select',
    fieldProps: {
      options: dictList('${field.dict_code}'),
    },
  },
<#else>
  { title: '${field.label}', path: '${field.name}', field: 'input' },
</#if>
<#else>
  { title: '关键词', path: 'keyword', field: 'input' },
</#list>
])
</#if>

<#if plan.gen_type != "TREE" && plan.gen_type != "LEFT_TREE_TABLE">
const pagination = computed<PaginationProps>(() => ({
  page: state.page,
  pageSize: state.pageSize,
  itemCount: state.total,
  showSizePicker: true,
  pageSizes: [10, 20, 30, 50],
  prefix: ({ itemCount }) => `${r"${itemCount}"} 条`,
  onUpdatePage: (value) => {
    state.page = value
    fetchPage()
  },
  onUpdatePageSize: (value) => {
    state.pageSize = value
    state.page = 1
    fetchPage()
  },
}))
</#if>
<#if has_sub && (sub??)>

const childSearchForm = createProSearchForm<any>({
  defaultCollapsed: true,
  onSubmit(values) {
    state.childSearchValues = normalizeSearchValues(values)
    state.childPage = 1
    fetchChildPage()
  },
  onReset() {
    state.childSearchValues = {}
    state.childPage = 1
    fetchChildPage()
  },
})

const childSearchColumns = computed<ProSearchFormColumns<any>>(() => [
<#list sub.query_fields as field>
<#if field.dict_code?? && field.dict_code?has_content>
  {
    title: '${field.label}',
    path: '${field.name}',
    field: 'select',
    fieldProps: {
      options: dictList('${field.dict_code}'),
    },
  },
<#else>
  { title: '${field.label}', path: '${field.name}', field: 'input' },
</#if>
<#else>
  { title: '关键词', path: 'keyword', field: 'input' },
</#list>
])

const childPagination = computed<PaginationProps>(() => ({
  page: state.childPage,
  pageSize: state.childPageSize,
  itemCount: state.childTotal,
  showSizePicker: true,
  pageSizes: [10, 20, 30, 50],
  prefix: ({ itemCount }) => `${r"${itemCount}"} 条`,
  onUpdatePage: (value) => {
    state.childPage = value
    fetchChildPage()
  },
  onUpdatePageSize: (value) => {
    state.childPageSize = value
    state.childPage = 1
    fetchChildPage()
  },
}))
</#if>

<#if plan.gen_type != "LEFT_TREE_TABLE">
const tableColumns = computed<ProDataTableColumns<any>>(() => [
  { type: 'selection', fixed: 'left' },
<#list main.table_fields as field><#if (field?index gte 8)><#break></#if>
<#if field.dict_code?? && field.dict_code?has_content>
  {
    title: '${field.label}',
    path: '${field.name}',
    width: 150,
    ellipsis: { tooltip: true },
    render: row => (
      <NTag color={createTagColor(dictTypeColor('${field.dict_code}', row.${field.name}))} bordered={false}>
        {dictTypeData('${field.dict_code}', row.${field.name}) || displayValue(row.${field.name})}
      </NTag>
    ),
  },
<#elseif field.is_bool>
  {
    title: '${field.label}',
    path: '${field.name}',
    width: 120,
    render: row => (
      <NTag type={wireBool(row.${field.name}) ? 'success' : 'default'} bordered={false}>
        {wireBool(row.${field.name}) ? '是' : '否'}
      </NTag>
    ),
  },
<#elseif field.is_datetime>
  { title: '${field.label}', path: '${field.name}', width: 190, render: row => formatDateTime(row.${field.name}) },
<#else>
  { title: '${field.label}', path: '${field.name}', width: 150, ellipsis: { tooltip: true } },
</#if>
</#list>
  { title: '更新时间', path: 'updated_at', width: 190, render: row => formatDateTime(row.updated_at) },
  {
    title: '操作',
    key: 'actions',
    width: <#if has_sub && (sub??)>170<#else>130</#if>,
    fixed: 'right',
    render: row => (
      <NFlex size={12}>
        {hasPermission('${permissionPrefix}:detail') ? (
          <NButton type="info" size="small" text={true} onClick={() => openDetailModal(row.${main.pk_name})}>
            {renderButtonIcon('icon-park-outline:preview-open')}
          </NButton>
        ) : null}
        {hasPermission('${permissionPrefix}:update') ? (
          <NButton type="primary" size="small" text={true} onClick={() => openEditModal(row.${main.pk_name})}>
            {renderButtonIcon('icon-park-outline:edit')}
          </NButton>
        ) : null}
<#if has_sub && (sub??)>
        <NButton type="info" size="small" text={true} onClick={() => selectMaster(row.${main.pk_name})}>
          {renderButtonIcon('icon-park-outline:list-view')}
        </NButton>
</#if>
        {hasPermission('${permissionPrefix}:delete') ? (
          <NButton type="error" size="small" text={true} onClick={() => confirmDelete(row.${main.pk_name})}>
            {renderButtonIcon('icon-park-outline:delete')}
          </NButton>
        ) : null}
      </NFlex>
    ),
  },
])
</#if>
<#if has_sub && (sub??)>

const childColumns = computed<ProDataTableColumns<any>>(() => [
  { type: 'selection', fixed: 'left' },
<#list sub.table_fields as field><#if (field?index gte 8)><#break></#if>
<#if field.dict_code?? && field.dict_code?has_content>
  {
    title: '${field.label}',
    path: '${field.name}',
    width: 150,
    ellipsis: { tooltip: true },
    render: row => (
      <NTag color={createTagColor(dictTypeColor('${field.dict_code}', row.${field.name}))} bordered={false}>
        {dictTypeData('${field.dict_code}', row.${field.name}) || displayValue(row.${field.name})}
      </NTag>
    ),
  },
<#elseif field.is_bool>
  {
    title: '${field.label}',
    path: '${field.name}',
    width: 120,
    render: row => (
      <NTag type={wireBool(row.${field.name}) ? 'success' : 'default'} bordered={false}>
        {wireBool(row.${field.name}) ? '是' : '否'}
      </NTag>
    ),
  },
<#elseif field.is_datetime>
  { title: '${field.label}', path: '${field.name}', width: 190, render: row => formatDateTime(row.${field.name}) },
<#else>
  { title: '${field.label}', path: '${field.name}', width: 150, ellipsis: { tooltip: true } },
</#if>
</#list>
  { title: '更新时间', path: 'updated_at', width: 190, render: row => formatDateTime(row.updated_at) },
  {
    title: '操作',
    key: 'actions',
    width: 130,
    fixed: 'right',
    render: row => (
      <NFlex size={12}>
        {hasPermission('${permissionPrefix}:detail') ? (
          <NButton type="info" size="small" text={true} onClick={() => openChildDetailModal(row.${sub.pk_name})}>
            {renderButtonIcon('icon-park-outline:preview-open')}
          </NButton>
        ) : null}
        {hasPermission('${permissionPrefix}:update') ? (
          <NButton type="primary" size="small" text={true} onClick={() => openChildEditModal(row.${sub.pk_name})}>
            {renderButtonIcon('icon-park-outline:edit')}
          </NButton>
        ) : null}
        {hasPermission('${permissionPrefix}:delete') ? (
          <NButton type="error" size="small" text={true} onClick={() => confirmChildDelete(row.${sub.pk_name})}>
            {renderButtonIcon('icon-park-outline:delete')}
          </NButton>
        ) : null}
      </NFlex>
    ),
  },
])
</#if>

onMounted(() => {
<#if has_tree>
  fetchTree()
</#if>
<#if plan.gen_type == "LEFT_TREE_TABLE">
  fetchChildPage()
<#elseif plan.gen_type == "TREE">
<#else>
  fetchPage()
</#if>
})

<#if plan.gen_type != "TREE" && plan.gen_type != "LEFT_TREE_TABLE">
async function fetchPage() {
  state.loading = true
  try {
    const response = await ${api_export_name}.page({ current: state.page, size: state.pageSize, ...state.searchValues })
    const data = response.data ?? {}
    state.rows = data.records ?? []
    const pageMeta = readPageMeta(data, { current: state.page, size: state.pageSize })
    state.total = pageMeta.total
    state.page = pageMeta.current
    state.pageSize = pageMeta.size
    state.checkedRowKeys = state.checkedRowKeys.filter(key => state.rows.some(item => item.${main.pk_name} === key))
  } finally {
    state.loading = false
  }
}
</#if>
<#if has_tree>

async function fetchTree() {
  state.treeLoading = true
  try {
    const response = await ${api_export_name}.tree(<#if plan.gen_type == "LEFT_TREE_TABLE">{ keyword: state.treeKeyword || undefined }</#if>)
    state.treeRows = response.data ?? []
  } finally {
    state.treeLoading = false
  }
}

<#if plan.gen_type == "LEFT_TREE_TABLE">
async function searchTree() {
  state.selectedTreeKeys = []
  state.selectedMasterId = null
  state.childPage = 1
  await Promise.all([fetchTree(), fetchChildPage()])
}

async function resetTreeSearch() {
  state.treeKeyword = ''
  await searchTree()
}

function handleTreeSelect(keys: Array<string | number>) {
  state.selectedTreeKeys = keys.map(String)
  state.selectedMasterId = state.selectedTreeKeys[0] ?? null
  state.childPage = 1
  fetchChildPage()
}

function buildTreeNodes(items: any[]): any[] {
  return items.map(item => ({
    key: item.${main.pk_name},
    label: item.${plan.tree_label_field},
    children: item.children?.length ? buildTreeNodes(item.children) : undefined,
  }))
}
<#elseif plan.gen_type == "TREE">

function filterTreeRows(items: any[], searchValues: any): any[] {
  return items
    .map((item) => {
      const children = filterTreeRows(item.children ?? [], searchValues)
      if (matchesTreeRow(item, searchValues) || children.length) {
        return { ...item, children }
      }
      return null
    })
    .filter(Boolean)
}

function matchesTreeRow(item: any, searchValues: any) {
<#list main.query_fields as field>
<#if field?is_first>
  const conditions = [
</#if>
<#if field.query_operator == "EQ">
    equalsValue(item.${field.name}, searchValues.${field.name}),
<#else>
    containsValue(item.${field.name}, searchValues.${field.name}),
</#if>
<#if field?is_last>
  ]
  return conditions.every(Boolean)
</#if>
<#else>
  const keyword = searchValues.keyword
  if (keyword === undefined || keyword === null || keyword === '') {
    return true
  }
  return Object.entries(item).some(([key, value]) => key !== 'children' && containsValue(value, keyword))
</#list>
}

function containsValue(source: unknown, target: unknown) {
  if (target === undefined || target === null || target === '') {
    return true
  }
  return String(source ?? '').toLowerCase().includes(String(target).toLowerCase())
}

function equalsValue(source: unknown, target: unknown) {
  if (target === undefined || target === null || target === '') {
    return true
  }
  return String(source ?? '') === String(target)
}
</#if>
</#if>
<#if plan.gen_type == "MASTER_DETAIL" && has_sub && (sub??)>

async function selectMaster(id: string) {
  state.selectedMasterId = id
<#if plan.gen_type == "MASTER_DETAIL">
  state.childDrawerVisible = true
</#if>
  state.childCheckedRowKeys = []
  state.childPage = 1
  await fetchChildPage()
}
</#if>
<#if has_sub && (sub??)>

async function fetchChildPage() {
  state.childLoading = true
  try {
    const response = await ${api_export_name}.childPage({
      current: state.childPage,
      size: state.childPageSize,
      ${plan.sub_foreign_key}: state.selectedMasterId,
      ...state.childSearchValues,
    })
    const data = response.data ?? {}
    state.childRows = data.records ?? []
    const childPageMeta = readPageMeta(data, { current: state.childPage, size: state.childPageSize })
    state.childTotal = childPageMeta.total
    state.childPage = childPageMeta.current
    state.childPageSize = childPageMeta.size
    state.childCheckedRowKeys = state.childCheckedRowKeys.filter(key => state.childRows.some(item => item.${sub.pk_name} === key))
  } finally {
    state.childLoading = false
  }
}
</#if>

<#if plan.gen_type != "LEFT_TREE_TABLE">
function openDetailModal(id: string) {
  detailModalRef.value?.openModal(id)
}

function openCreateModal() {
  formModalRef.value?.openModal()
}

function openEditModal(id: string) {
  formModalRef.value?.openModal(id)
}
</#if>
<#if has_sub && (sub??)>

function openChildDetailModal(id: string) {
  childDetailModalRef.value?.openModal(id)
}

function openChildCreateModal() {
  childFormModalRef.value?.openModal(undefined, { ${plan.sub_foreign_key}: state.selectedMasterId })
}

function openChildEditModal(id: string) {
  childFormModalRef.value?.openModal(id)
}
</#if>

<#if plan.gen_type != "LEFT_TREE_TABLE">
function handleCheckedRowKeys(keys: Array<string | number>) {
  state.checkedRowKeys = keys.map(String)
}
</#if>
<#if has_sub && (sub??)>

function handleChildCheckedRowKeys(keys: Array<string | number>) {
  state.childCheckedRowKeys = keys.map(String)
}
</#if>

<#if plan.gen_type != "LEFT_TREE_TABLE">
function confirmDelete(value: string | string[]) {
  const ids = Array.isArray(value) ? value : [value]
  if (!ids.length) {
    return
  }
  window.$dialog.warning({
    title: ids.length > 1 ? '批量删除' : '删除',
    content: ids.length > 1 ? `删除 ${r"${ids.length}"} 条记录?` : '删除该记录?',
    positiveText: '确认',
    negativeText: '取消',
    onPositiveClick: () => deleteRows(ids),
  })
}

async function deleteRows(ids: string[]) {
  await ${api_export_name}.remove({ ids })
  state.checkedRowKeys = state.checkedRowKeys.filter(key => !ids.includes(key))
<#if plan.gen_type == "MASTER_DETAIL" && sub??>
  if (state.selectedMasterId && ids.includes(state.selectedMasterId)) {
    state.selectedMasterId = null
    state.childDrawerVisible = false
    state.childRows = []
    state.childTotal = 0
    state.childCheckedRowKeys = []
  }
</#if>
  window.$message.success('删除成功')
<#if plan.gen_type == "TREE">
  await fetchTree()
<#else>
  await fetchPage()
</#if>
}
</#if>
<#if has_sub && (sub??)>

function confirmChildDelete(value: string | string[]) {
  const ids = Array.isArray(value) ? value : [value]
  if (!ids.length) {
    return
  }
  window.$dialog.warning({
    title: ids.length > 1 ? '批量删除' : '删除',
    content: ids.length > 1 ? `删除 ${r"${ids.length}"} 条明细?` : '删除该明细?',
    positiveText: '确认',
    negativeText: '取消',
    onPositiveClick: () => deleteChildRows(ids),
  })
}

async function deleteChildRows(ids: string[]) {
  await ${api_export_name}.childRemove({ ids })
  state.childCheckedRowKeys = state.childCheckedRowKeys.filter(key => !ids.includes(key))
  window.$message.success('删除成功')
  await fetchChildPage()
}
</#if>
</script>

<template>
<#if plan.gen_type == "LEFT_TREE_TABLE">
  <div class="generated-left-tree-table">
    <ProCard class="generated-tree" content-class="h-full min-h-0 overflow-hidden">
      <NFlex class="generated-tree-layout" vertical :size="12">
        <NInputGroup>
          <NInput
            v-model:value="state.treeKeyword"
            clearable
            placeholder="搜索${plan.main_business_name}"
            @keyup.enter="searchTree"
          />
          <NButton type="primary" :loading="state.treeLoading" title="搜索" @click="searchTree">
            <template #icon><NIcon><Icon icon="icon-park-outline:search" /></NIcon></template>
          </NButton>
          <NButton :disabled="!state.treeKeyword" title="重置" @click="resetTreeSearch">
            <template #icon><NIcon><Icon icon="icon-park-outline:refresh" /></NIcon></template>
          </NButton>
        </NInputGroup>
        <div class="generated-tree-body">
          <NSpin
            :show="state.treeLoading"
            class="generated-tree-spin"
            content-class="generated-tree-spin-content"
          >
            <NScrollbar class="generated-tree-scroll" content-class="generated-tree-scroll-content">
              <NTree
                block-line
                block-node
                show-line
                :data="treeData"
                :selected-keys="state.selectedTreeKeys"
                key-field="key"
                label-field="label"
                children-field="children"
                @update:selected-keys="handleTreeSelect"
              />
            </NScrollbar>
          </NSpin>
        </div>
      </NFlex>
    </ProCard>

    <NFlex class="min-w-0 min-h-0 h-full" vertical>
      <ProCard content-class="pb-0!">
        <ProSearchForm
          :form="childSearchForm"
          :columns="childSearchColumns"
          :reset-button-props="{ content: '重置' }"
          :search-button-props="{ content: '搜索' }"
        />
      </ProCard>
      <ProDataTable
        class="min-h-0 flex-1"
        remote
        title="${(plan.sub_business_name)?has_content?then(plan.sub_business_name, "明细")}"
        row-key="${sub.pk_name}"
        :scroll-x="1300"
        :columns="childColumns"
        :data="state.childRows"
        :loading="state.childLoading"
        :pagination="childPagination"
        :checked-row-keys="state.childCheckedRowKeys"
        :on-update-checked-row-keys="handleChildCheckedRowKeys"
      >
        <template #toolbar>
          <NFlex>
            <NButton v-if="hasPermission('${permissionPrefix}:create')" type="primary" text :disabled="!canCreateChild" @click="openChildCreateModal">
              <template #icon><NIcon><Icon icon="icon-park-outline:plus" /></NIcon></template>
            </NButton>
            <NButton text :loading="state.childLoading" @click="fetchChildPage">
              <template #icon><NIcon><Icon icon="icon-park-outline:refresh" /></NIcon></template>
            </NButton>
            <NButton v-if="hasPermission('${permissionPrefix}:delete')" type="error" text :disabled="!hasChildCheckedRows" @click="confirmChildDelete(state.childCheckedRowKeys)">
              <template #icon><NIcon><Icon icon="icon-park-outline:delete" /></NIcon></template>
            </NButton>
          </NFlex>
        </template>
      </ProDataTable>
    </NFlex>

    <ChildModalDetail ref="childDetailModalRef" />
    <ChildModalForm ref="childFormModalRef" @saved="fetchChildPage" />
  </div>
<#else>
  <NFlex class="h-full min-h-0" vertical>
    <ProCard content-class="pb-0!">
      <ProSearchForm
        :form="searchForm"
        :columns="searchColumns"
        :reset-button-props="{ content: '重置' }"
        :search-button-props="{ content: '搜索' }"
      />
    </ProCard>

    <ProDataTable
      class="min-h-0 flex-1"
<#if plan.gen_type != "TREE">
      remote
</#if>
      title="${plan.menu_name}"
      row-key="${main.pk_name}"
      :scroll-x="1300"
      :columns="tableColumns"
<#if plan.gen_type == "TREE">
      :data="filteredTreeRows"
      :loading="state.treeLoading"
      :pagination="false"
      default-expand-all
<#else>
      :data="state.rows"
      :loading="state.loading"
      :pagination="pagination"
</#if>
      :checked-row-keys="state.checkedRowKeys"
      :on-update-checked-row-keys="handleCheckedRowKeys"
    >
      <template #toolbar>
        <NFlex>
          <NButton v-if="hasPermission('${permissionPrefix}:create')" type="primary" text @click="openCreateModal">
            <template #icon><NIcon><Icon icon="icon-park-outline:plus" /></NIcon></template>
          </NButton>
          <NButton text :loading="<#if plan.gen_type == "TREE">state.treeLoading<#else>state.loading</#if>" @click="<#if plan.gen_type == "TREE">fetchTree<#else>fetchPage</#if>">
            <template #icon><NIcon><Icon icon="icon-park-outline:refresh" /></NIcon></template>
          </NButton>
          <NButton v-if="hasPermission('${permissionPrefix}:delete')" type="error" text :disabled="!hasCheckedRows" @click="confirmDelete(state.checkedRowKeys)">
            <template #icon><NIcon><Icon icon="icon-park-outline:delete" /></NIcon></template>
          </NButton>
        </NFlex>
      </template>
    </ProDataTable>
<#if plan.gen_type == "MASTER_DETAIL" && sub??>

    <NDrawer v-model:show="state.childDrawerVisible" :width="960" placement="right">
      <NDrawerContent title="${(plan.sub_business_name)?has_content?then(plan.sub_business_name, "明细")}管理" closable>
        <NFlex style="height: calc(100vh - 110px)" vertical>
          <ProCard content-class="pb-0!">
            <ProSearchForm
              :form="childSearchForm"
              :columns="childSearchColumns"
              :reset-button-props="{ content: '重置' }"
              :search-button-props="{ content: '搜索' }"
            />
          </ProCard>
          <ProDataTable
            class="min-h-0 flex-1"
            remote
            title="${(plan.sub_business_name)?has_content?then(plan.sub_business_name, "明细")}"
            row-key="${sub.pk_name}"
            :scroll-x="1300"
            :columns="childColumns"
            :data="state.childRows"
            :loading="state.childLoading"
            :pagination="childPagination"
            :checked-row-keys="state.childCheckedRowKeys"
            :on-update-checked-row-keys="handleChildCheckedRowKeys"
          >
            <template #toolbar>
              <NFlex>
                <NButton v-if="hasPermission('${permissionPrefix}:create')" type="primary" text :disabled="!canCreateChild" @click="openChildCreateModal">
                  <template #icon><NIcon><Icon icon="icon-park-outline:plus" /></NIcon></template>
                </NButton>
                <NButton text :loading="state.childLoading" @click="fetchChildPage">
                  <template #icon><NIcon><Icon icon="icon-park-outline:refresh" /></NIcon></template>
                </NButton>
                <NButton v-if="hasPermission('${permissionPrefix}:delete')" type="error" text :disabled="!hasChildCheckedRows" @click="confirmChildDelete(state.childCheckedRowKeys)">
                  <template #icon><NIcon><Icon icon="icon-park-outline:delete" /></NIcon></template>
                </NButton>
              </NFlex>
            </template>
          </ProDataTable>
        </NFlex>
      </NDrawerContent>
    </NDrawer>
</#if>

    <ModalDetail ref="detailModalRef" />
    <ModalForm ref="formModalRef" @saved="<#if plan.gen_type == "TREE">fetchTree<#else>fetchPage</#if>" />
<#if has_sub && (sub??)>
    <ChildModalDetail ref="childDetailModalRef" />
    <ChildModalForm ref="childFormModalRef" @saved="fetchChildPage" />
</#if>
  </NFlex>
</#if>
</template>
<#if plan.gen_type == "LEFT_TREE_TABLE">

<style scoped>
.generated-left-tree-table {
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  gap: 12px;
  height: 100%;
  min-height: 0;
}

.generated-tree {
  min-height: 0;
}

.generated-tree-layout {
  height: 100%;
  min-height: 0;
}

.generated-tree-body {
  min-height: 0;
  flex: 1;
}

.generated-tree-spin,
.generated-tree-spin :deep(.generated-tree-spin-content),
.generated-tree-scroll {
  height: 100%;
  min-height: 0;
}

.generated-tree-scroll :deep(.generated-tree-scroll-content) {
  min-width: max-content;
  padding-right: 8px;
}

@media (max-width: 900px) {
  .generated-left-tree-table {
    grid-template-columns: minmax(0, 1fr);
    grid-template-rows: minmax(260px, 34vh) minmax(0, 1fr);
  }
}
</style>
</#if>