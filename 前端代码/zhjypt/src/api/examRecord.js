import { get } from '../http/request.js'

// 获取学生某个考试的所有记录
export const getStudentExamRecords = (studentId, examId) => {
  return get('/exam/GetStudentExamRecords', { studentId, examId })
}

// 获取学生最新的考试记录
export const getLatestExamRecord = (studentId, examId) => {
  return get('/exam/GetLatestExamRecord', { studentId, examId })
}

// 获取学生考试次数
export const getExamAttemptCount = (studentId, examId) => {
  return get('/exam/GetExamAttemptCount', { studentId, examId })
}

// 检查考试是否已提交
export const checkExamSubmitted = (studentId, examId) => {
  return get('/exam/CheckExamSubmitted', { studentId, examId })
}

// 创建考试记录
export const createExamRecord = (examData) => {
  return get('/exam/CreateExamRecord', examData)
}

// 提交考试
export const submitExamRecord = (recordData) => {
  return get('/exam/SubmitExamRecord', recordData)
}

// 获取需要批改的考试记录
export const getRecordsForGrading = (courseId) => {
  return get('/exam/GetRecordsForGrading', { courseId })
}

// 批改考试记录
export const gradeExamRecord = (recordId, subjectiveScore, feedback) => {
  return get('/exam/GradeExamRecord', { recordId, subjectiveScore, feedback })
}

// 获取教师待批改数量
export const getPendingGradingCount = (teacherId) => {
  return get('/exam/GetPendingGradingCount', { teacherId })
}

// 获取学生待完成考试数量
export const getPendingExamCount = (studentId) => {
  return get('/exam/GetPendingExamCount', { studentId })
}
