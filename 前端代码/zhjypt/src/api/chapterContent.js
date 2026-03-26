import { get, post } from '../http/request.js'

// 根据章节ID获取内容列表
export function getContentsByChapterId(chapterId) {
  return get('/chapter-content/list', { chapterId })
}

// 创建内容
export function createContent(params) {
  return get('/chapter-content/create', params)
}

// 更新内容
export function updateContent(params) {
  return get('/chapter-content/update', params)
}

// 删除内容
export function deleteContent(contentId, chapterId) {
  return get('/chapter-content/delete', { contentId, chapterId })
}

// 发布内容
export function publishContent(contentId) {
  return get('/chapter-content/publish', { contentId })
}

// 取消发布内容
export function unpublishContent(contentId) {
  return get('/chapter-content/unpublish', { contentId })
}

// 更新内容排序
export function updateContentSort(contentId, sortOrder) {
  return get('/chapter-content/sort', { contentId, sortOrder })
}

// 分析远程文件URL
export function analyzeUrl(url, contentType) {
  return post('/upload/analyze-url', { url, contentType })
}

// 获取远程文件大小
export function getFileSize(url) {
  return post('/upload/get-file-size', { url })
}