import { get } from '../http/request.js'

// 获取学生选修的课程列表
export const getStudentCourses = (studentId) => {
  return get('/student-course/list', { studentId })
}

// 获取课程的选课学生列表（教师用）
export const getCourseStudents = (courseId) => {
  return get('/student-course/courseStudents', { courseId })
}

// 获取学生的章节学习详情
export const getStudentChapterProgress = (studentId, courseId) => {
  return get('/student-course/chapterProgress', { studentId, courseId })
}

// 学生选课
export const enrollCourse = (studentId, courseId) => {
  return get('/student-course/enroll', { studentId, courseId })
}

// 检查是否已选课
export const checkEnrolled = (studentId, courseId) => {
  return get('/student-course/check', { studentId, courseId })
}

// 退课
export const withdrawCourse = (studentId, courseId) => {
  return get('/student-course/withdraw', { studentId, courseId })
}

