import { get } from '../http/request.js'

// 学生创建咨询
export const createConsultation = (params) => {
  return get('/consultation/create', params)
}

// 教师回复咨询
export const replyConsultation = (params) => {
  return get('/consultation/reply', params)
}

// 关闭咨询
export const closeConsultation = (consultationId) => {
  return get('/consultation/close', { consultationId })
}

// 标记为已读
export const markConsultationAsRead = (consultationId, studentId) => {
  return get('/consultation/markread', { consultationId, studentId })
}

// 设置优先级
export const setConsultationPriority = (consultationId, priority, teacherId) => {
  return get('/consultation/setpriority', { consultationId, priority, teacherId })
}

// 获取学生咨询列表
export const getStudentConsultations = (studentId, status, pageNum = 1, pageSize = 10) => {
  return get('/consultation/student/list', { studentId, status, pageNum, pageSize })
}

// 获取教师咨询列表
export const getTeacherConsultations = (teacherId, status, courseId, pageNum = 1, pageSize = 10) => {
  return get('/consultation/teacher/list', { teacherId, status, courseId, pageNum, pageSize })
}

// 获取咨询详情
export const getConsultationDetail = (consultationId) => {
  return get('/consultation/detail', { consultationId })
}

// 删除咨询
export const deleteConsultation = (consultationId, userId, userType) => {
  return get('/consultation/delete', { consultationId, userId, userType })
}

// 获取教师未回复咨询数量
export const getTeacherUnrepliedCount = (teacherId) => {
  return get('/consultation/teacher/unreplied/count', { teacherId })
}

// 获取学生未读回复数量
export const getStudentUnreadReplyCount = (studentId) => {
  return get('/consultation/student/unread/count', { studentId })
}
