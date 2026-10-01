<#-- Author: Charlie -->
/**
 * 由 Voxel 代码生成器生成。
 * Author: ${plan.author}
 * 生成时间：${generated_at}
 */
import { API_PREFIX } from '@/constants/api'
import { http } from '@/utils'

const prefix = `${r"${API_PREFIX}"}${plan.api_prefix}`

export function page(params: any) {
  return http.get<any>(`${r"${prefix}"}/page`, { params })
}

export function detail(params: any) {
  return http.get<any>(`${r"${prefix}"}/detail`, { params })
}

export function create(data: any) {
  return http.post<any>(`${r"${prefix}"}/create`, data)
}

export function update(data: any) {
  return http.post<any>(`${r"${prefix}"}/update`, data)
}

export function remove(data: any) {
  return http.post<any>(`${r"${prefix}"}/delete`, data)
}
<#if has_tree>

export function tree(params?: any) {
  return http.get<any>(`${r"${prefix}"}/tree`, { params })
}
</#if>
<#if has_sub && sub??>

export function childPage(params: any) {
  return http.get<any>(`${r"${prefix}"}/children/page`, { params })
}

export function childDetail(params: any) {
  return http.get<any>(`${r"${prefix}"}/children/detail`, { params })
}

export function childCreate(data: any) {
  return http.post<any>(`${r"${prefix}"}/children/create`, data)
}

export function childUpdate(data: any) {
  return http.post<any>(`${r"${prefix}"}/children/update`, data)
}

export function childRemove(data: any) {
  return http.post<any>(`${r"${prefix}"}/children/delete`, data)
}
</#if>
