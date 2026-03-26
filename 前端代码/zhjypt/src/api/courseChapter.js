import { get } from '../http/request.js'

// 根据课程ID获取章节列表
export function getChaptersByCourseId(courseId) {
  return get('/course-chapter/list', { courseId })
}

// 创建章节
export function createChapter(params) {
  return get('/course-chapter/create', params)
}

// 更新章节
export function updateChapter(params) {
  return get('/course-chapter/update', params)
}

// 删除章节
export function deleteChapter(chapterId, courseId) {
  return get('/course-chapter/delete', { chapterId, courseId })
}

// 发布章节
export function publishChapter(chapterId) {
  return get('/course-chapter/publish', { chapterId })
}

// 取消发布章节
export function unpublishChapter(chapterId) {
  return get('/course-chapter/unpublish', { chapterId })
}

// 更新章节排序
export function updateChapterSort(chapterId, sortOrder) {
  return get('/course-chapter/sort', { chapterId, sortOrder })
}