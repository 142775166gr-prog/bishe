import { get, post } from '../http/request.js'

export function addadmin(params) {
    return get('/Admin/AddAdmin', params)
}

export function updateadmin(params) {
    return get('/Admin/UpdateAdmin', params)
}

export function deleteadmin(params) {
    return get('/Admin/DeleteAdmin', params)
}

export function getadmin(params) {
    return get('/Admin/GetAdmin', params)
}

export function getadminlist(params) {
    return get('/Admin/GetAdminList', params)
}

export function getadminlistbypage(params = {}, pageNum = 1, pageSize = 10) {
    const requestParams = Object.assign({}, params, { pageNum, pageSize })
    return get('/Admin/GetAdminListByPage', requestParams)
}

export function adminlogin(params) {
    return get('/Admin/AdminLogin', params)
}