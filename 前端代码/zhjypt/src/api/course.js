import { get } from '../http/request.js'

// 分页查询课程列表（管理员）
export function getCoursePage(params, pageNum, pageSize) {
  return get('/course/page', {
    ...params,
    pageNum,
    pageSize
  })
}

// 分页查询教师课程列表
export function getTeacherCoursePage(teacherId, params, pageNum, pageSize) {
  return get('/course/teacher/page', {
    teacherId,
    ...params,
    pageNum,
    pageSize
  })
}

// 分页查询已发布课程列表（学生）
export function getPublishedCoursePage(params, pageNum, pageSize) {
  return get('/course/published/page', {
    ...params,
    pageNum,
    pageSize
  })
}

// 创建课程
export function createCourse(params) {
  return get('/course/create', params)
}

// 更新课程
export function updateCourse(params) {
  return get('/course/update', params)
}

// 发布课程
export function publishCourse(courseId, teacherId) {
  return get('/course/publish', {
    courseId,
    teacherId
  })
}

// 下架课程
export function unpublishCourse(courseId, teacherId) {
  return get('/course/unpublish', {
    courseId,
    teacherId
  })
}

// 删除课程
export function deleteCourse(courseId, teacherId) {
  return get('/course/delete', {
    courseId,
    teacherId
  })
}

// 获取课程详情
export function getCourseDetail(courseId) {
  return get('/course/detail', {
    courseId
  })
}

// 根据ID获取课程信息
export function getCourseById(courseId) {
  return get('/course/getbyid', {
    courseId
  })
}

// 获取教师课程列表
export function getTeacherCoursesList(teacherId) {
  return get('/course/teacher/list', {
    teacherId
  })
}

// 获取教师的所有课程（别名）
export function getCoursesByTeacherId(teacherId) {
  return get('/course/teacher/list', {
    teacherId
  })
}

// 获取所有课程（管理员用）
export function getAllCourses() {
  return get('/course/all')
}

// 获取已发布课程列表
export function getPublishedCoursesList() {
  return get('/course/published/list')
}

// 根据分类获取课程
export function getCoursesByCategoryId(categoryId) {
  return get('/course/category', {
    categoryId
  })
}

// 课程分类相关API
export function getCourseCategoriesList() {
  return get('/course-category/list')
}

export function getCategoryDetail(categoryId) {
  return get('/course-category/detail', {
    categoryId
  })
}

// 兼容旧版本API（保持向后兼容）
export function addcourse(params) {
  return createCourse(params)
}

export function updatecourse(params) {
  return updateCourse(params)
}

export function deletecourse(params) {
  return deleteCourse(params.courseId, params.teacherId)
}

export function getcourse(params) {
  return getCourseDetail(params.courseId)
}

export function getcourselistbypage(params = {}, pageNum = 1, pageSize = 10) {
  return getCoursePage(params, pageNum, pageSize)
}