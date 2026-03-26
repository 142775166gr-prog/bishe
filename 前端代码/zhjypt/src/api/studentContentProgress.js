import { get, post } from '../http/request.js'

// 更新学习进度
export function updateProgress(studentId, courseId, chapterId, contentId, watchProgress) {
  return post('/student-content-progress/update', { 
    studentId, 
    courseId, 
    chapterId, 
    contentId, 
    watchProgress 
  })
}

// 标记内容为已完成
export function markCompleted(studentId, courseId, chapterId, contentId) {
  return post('/student-content-progress/complete', { 
    studentId, 
    courseId, 
    chapterId, 
    contentId 
  })
}

// 获取课程学习进度
export function getCourseProgress(studentId, courseId) {
  return get('/student-content-progress/course-progress', { studentId, courseId })
}

// 获取学生的具体内容学习进度
export function getContentProgress(studentId, contentId) {
  return get('/student-content-progress/content-progress', { studentId, contentId })
}

// 检查内容是否已完成
export function checkCompleted(studentId, contentId) {
  return get('/student-content-progress/check-completed', { studentId, contentId })
}