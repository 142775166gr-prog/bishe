import { get } from '../http/request.js'

// 根据课程ID获取考试列表
export const getExamsByCourseId = (courseId) => {
  return get('/exam/GetExamsByCourseId', { courseId })
}

// 创建考试
export const createExam = (examData) => {
  return get('/exam/AddExam', examData)
}

// 更新考试
export const updateExam = (examData) => {
  return get('/exam/UpdateExam', examData)
}

// 删除考试
export const deleteExam = (examId) => {
  return get('/exam/DeleteExam', { examId })
}

// 发布考试
export const publishExam = (examId) => {
  return get('/exam/PublishExam', { examId })
}

// 取消发布考试
export const unpublishExam = (examId) => {
  return get('/exam/UnpublishExam', { examId })
}

// 获取学生可参加的考试列表
export const getAvailableExams = (courseId, studentId) => {
  return get('/exam/GetAvailableExams', { courseId, studentId })
}

// 获取考试详情
export const getExamById = (examId) => {
  return get('/exam/GetExam', { examId })
}

// 保存学生答案
export const saveStudentAnswers = (recordId, answers) => {
  return get('/exam/SaveStudentAnswers', {
    recordId: recordId,
    answers: JSON.stringify(answers)
  })
}

// 获取考试分页列表
export const getExamPage = (params, pageNum, pageSize) => {
  return get('/exam/page', { ...params, pageNum, pageSize })
}
