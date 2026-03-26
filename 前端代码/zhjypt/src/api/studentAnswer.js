import { get, post } from '../http/request.js'

// 保存学生答案
export const saveStudentAnswers = (answerData) => {
  return get('/studentAnswer/SaveAnswers', answerData)
}

// 获取学生答案
export const getStudentAnswers = (recordId) => {
  return get('/studentAnswer/GetAnswersByRecord', { recordId })
}

// 批改主观题
export const gradeSubjectiveAnswers = (recordId, gradingData) => {
  return post('/studentAnswer/GradeSubjectiveAnswers', {
    recordId: recordId,
    gradingData: JSON.stringify(gradingData)
  })
}

// 测试API
export const testStudentAnswerAPI = () => {
  return get('/studentAnswer/test')
}

// 批量保存学生答案
export const batchSaveStudentAnswers = (answerData) => {
  return post('/studentAnswer/BatchSaveAnswers', {
    recordId: answerData.recordId,
    answers: JSON.stringify(answerData.answers)
  })
}