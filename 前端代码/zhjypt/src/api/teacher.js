import { get } from '../http/request.js'

export function teacherlogin(params) {
    return get('/Teacher/TeacherLogin', params)
}

export function addteacher(params) {
    return get('/Teacher/AddTeacher', params)
}

export function updateteacher(params) {
    return get('/Teacher/UpdateTeacher', params)
}

export function deleteteacher(params) {
    return get('/Teacher/DeleteTeacher', params)
}

export function getteacher(params) {
    return get('/Teacher/GetTeacher', params)
}

export function getteacherlist(params) {
    return get('/Teacher/GetTeacherList', params)
}

export function getteacherlistbypage(params = {}, pageNum = 1, pageSize = 10) {
    const requestParams = Object.assign({}, params, { pageNum, pageSize })
    return get('/Teacher/GetTeacherListByPage', requestParams)
}

export function resetteacherpassword(params) {
    return get('/Teacher/ResetTeacherPassword', params)
}