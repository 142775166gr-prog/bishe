import { get } from '../http/request.js'

export function addCourseExam(params) {
    return get('/Course-exam/AddCourseExam', params)
}

export function updateCourseExam(params) {
    return get('/Course-exam/UpdateCourseExam', params)
}

export function deleteCourseExam(params) {
    return get('/Course-exam/DeleteCourseExam', params)
}

export function getCourseExam(params) {
    return get('/Course-exam/GetCourseExam', params)
}

export function getCourseExamList(params) {
    return get('/Course-exam/GetCourseExamList', params)
}

export function getCourseExamListByPage(params = {}, pageNum = 1, pageSize = 10) {
    const requestParams = Object.assign({}, params, { pageNum, pageSize })
    return get('/Course-exam/GetCourseExamListByPage', requestParams)
}
