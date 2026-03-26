import { get } from '../http/request.js'

export function addExamSubjective(params) {
    return get('/Exam-subjective/AddExamSubjective', params)
}

export function updateExamSubjective(params) {
    return get('/Exam-subjective/UpdateExamSubjective', params)
}

export function deleteExamSubjective(params) {
    return get('/Exam-subjective/DeleteExamSubjective', params)
}

export function getExamSubjective(params) {
    return get('/Exam-subjective/GetExamSubjective', params)
}

export function getExamSubjectiveList(params) {
    return get('/Exam-subjective/GetExamSubjectiveList', params)
}

export function getExamSubjectiveListByPage(params = {}, pageNum = 1, pageSize = 10) {
    const requestParams = Object.assign({}, params, { pageNum, pageSize })
    return get('/Exam-subjective/GetExamSubjectiveListByPage', requestParams)
}
