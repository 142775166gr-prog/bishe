import { get } from '../http/request.js'

export function addExamObjective(params) {
    return get('/Exam-objective/AddExamObjective', params)
}

export function updateExamObjective(params) {
    return get('/Exam-objective/UpdateExamObjective', params)
}

export function deleteExamObjective(params) {
    return get('/Exam-objective/DeleteExamObjective', params)
}

export function getExamObjective(params) {
    return get('/Exam-objective/GetExamObjective', params)
}

export function getExamObjectiveList(params) {
    return get('/Exam-objective/GetExamObjectiveList', params)
}

export function getExamObjectiveListByPage(params = {}, pageNum = 1, pageSize = 10) {
    const requestParams = Object.assign({}, params, { pageNum, pageSize })
    return get('/Exam-objective/GetExamObjectiveListByPage', requestParams)
}
