import axios from 'axios';

/* axios功能封装  */

// 使用 /api 前缀，通过 nginx 反向代理到后端
// 生产环境：通过 nginx 代理 /api -> backend:8080
// 开发环境：直接使用 localhost:8080
const baseURL = process.env.VUE_APP_API_BASE_URL || 
                (process.env.NODE_ENV === 'production' ? '/api' : 'http://localhost:8080');

const service = axios.create({
    timeout: 5000,
    baseURL: baseURL,
    withCredentials:  true
});

// request interceptor(请求拦截器)
service.interceptors.request.use(
    config => {
        return config;
    },
    error => {
        console.log(error);
        return Promise.reject();
    }
);

// response interceptor（接收拦截器）
service.interceptors.response.use(
    response => {
        if (response.status === 200) {
            return response.data;
        } else {
            Promise.reject();
        }
    },
    error => {
        console.log(error);
        return Promise.reject();
    }
);

export default service;
