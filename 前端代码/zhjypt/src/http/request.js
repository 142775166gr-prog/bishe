import axios from 'axios';
import qs from 'qs'
import { getSessionStorage } from '../utils/common';
const instance = axios.create({
    baseURL: 'http://localhost:9999/',
});

instance.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded'; //设置post请求是的header信息

// 添加请求拦截器
instance.interceptors.request.use(function (config) {  
    // 在发送请求之前：自动携带 JWT token
    try {
        const cur = getSessionStorage('curuser') || getSessionStorage('curadmin');
        const token = (cur && cur.token) ? cur.token : (cur && cur.info && cur.info.token ? cur.info.token : null);
        if (token) {
            config.headers = config.headers || {};
            config.headers.Authorization = `Bearer ${token}`;
        }
    } catch (e) {
        // ignore
    }
    return config;
  }, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  });

// 添加响应拦截器
instance.interceptors.response.use(function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    // console.log('对响应数据做点什么')
    // console.log(response)
    return response.data;
  }, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
  });


 /**
 * get 请求方法
 * @param url
 * @param params
 * @returns {Promise}
 */
export function get(url, params = {}) {
    return new Promise((resolve, reject) => {
        instance
            .get(url, {
                params: params
            })
            .then(response => {
                resolve(response)
            })
            .catch(err => {
                reject(err)
            })
    })
}

/**
 * post 请求方法
 * @param url
 * @param data
 * @returns {Promise}
 */
export function post(url, data = {}) {
    return new Promise((resolve, reject) => {
        instance.post(url, qs.stringify(data)).then(
            response => {
                // console.log(response.data.code)
                resolve(response)
            },
            err => {
                reject(err)
            }
        )
    })
}
/**
 * post JSON 请求方法
 * @param url
 * @param data
 * @returns {Promise}
 */
export function postJson(url, data = {}) {
    return new Promise((resolve, reject) => {
        instance.post(url, data, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(
            response => {
                resolve(response)
            },
            err => {
                reject(err)
            }
        )
    })
}

/**
 * 文件上传请求方法
 * @param url
 * @param data
 * @returns {Promise}
 */
 export function upload(url, data) {
    return new Promise((resolve, reject) => {
        instance.post(url, data, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }).then(
            response => {
                // console.log(response.data.code)
                resolve(response)
            },
            err => {
                reject(err)
            }
        )
    })
}

/**
 * delete 请求方法
 * @param url
 * @param params
 * @returns {Promise}
 */
export function del(url, params = {}) {
    return new Promise((resolve, reject) => {
        instance
            .delete(url, {
                params: params
            })
            .then(response => {
                resolve(response)
            })
            .catch(err => {
                reject(err)
            })
    })
}
