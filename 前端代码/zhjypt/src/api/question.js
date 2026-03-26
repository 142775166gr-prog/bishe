import { get } from '../http/request.js'

// 根据课程ID获取题目列表
export const getQuestionsByCourseId = (courseId) => {
  return get('/question/GetQuestionsByCourseId', { courseId })
}

// 创建题目
export const createQuestion = (questionData) => {
  return get('/question/AddQuestion', questionData)
}

// 更新题目
export const updateQuestion = (questionData) => {
  return get('/question/UpdateQuestion', questionData)
}

// 删除题目
export const deleteQuestion = (questionId) => {
  return get('/question/DeleteQuestion', { questionId })
}

// 根据考试ID获取题目列表
export const getQuestionsByExamId = (examId) => {
  return get('/question/GetQuestionsByExamId', { examId })
}

// 获取题目详情（包含选项）
export const getQuestionWithOptions = (questionId) => {
  return get('/question/GetQuestion', { questionId })
}