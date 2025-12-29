import axios from 'axios';

/* axios功能封装  */

// 使用 /api 前缀，通过代理访问后端
// 开发环境：vue.config.js 的代理将 /api 转发到 localhost:8081
// 生产环境：Nginx 将 /api 代理到 backend:8080
// 这样开发和生产环境使用相同的路径，代码统一
const baseURL = process.env.VUE_APP_API_BASE_URL || '/api';

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
