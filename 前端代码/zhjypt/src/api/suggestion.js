import { get } from '../http/request.js'

// 教师创建建议
export const createSuggestion = (params) => {
  return get('/suggestion/create', params)
}

// 标记为已读
export const markSuggestionAsRead = (suggestionId, studentId) => {
  return get('/suggestion/markread', { suggestionId, studentId })
}

// 收藏/取消收藏
export const toggleSuggestionFavorite = (suggestionId, studentId) => {
  return get('/suggestion/togglefavorite', { suggestionId, studentId })
}

// 获取学生收到的建议列表
export const getStudentSuggestions = (studentId, suggestionType, isRead, isFavorite, pageNum = 1, pageSize = 10) => {
  return get('/suggestion/student/list', { studentId, suggestionType, isRead, isFavorite, pageNum, pageSize })
}

// 获取教师发送的建议列表
export const getTeacherSuggestions = (teacherId, pageNum = 1, pageSize = 10) => {
  return get('/suggestion/teacher/list', { teacherId, pageNum, pageSize })
}

// 获取建议详情
export const getSuggestionDetail = (suggestionId) => {
  return get('/suggestion/detail', { suggestionId })
}

// 删除建议
export const deleteSuggestion = (suggestionId, userId, userType) => {
  return get('/suggestion/delete', { suggestionId, userId, userType })
}

// 获取学生未读建议数量
export const getStudentUnreadSuggestionCount = (studentId) => {
  return get('/suggestion/student/unread/count', { studentId })
}
