import { get } from '../http/request.js'

export function studentlogin(params) {
    return get('/Student/StudentLogin', params)
}

export function addstudent(params) {
    return get('/Student/AddStudent', params)
}

export function updatestudent(params) {
    return get('/Student/UpdateStudent', params)
}

export function deletestudent(params) {
    return get('/Student/DeleteStudent', params)
}

export function getstudent(params) {
    return get('/Student/GetStudent', params)
}

export function getstudentlist(params) {
    return get('/Student/GetStudentList', params)
}

export function getstudentlistbypage(params = {}, pageNum = 1, pageSize = 10) {
    const requestParams = Object.assign({}, params, { pageNum, pageSize })
    return get('/Student/GetStudentListByPage', requestParams)
}

export function resetstudentpassword(params) {
    return get('/Student/ResetStudentPassword', params)
}
