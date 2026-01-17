import axios from 'axios'

// 配置基础路径
axios.defaults.baseURL = 'http://localhost:8081';

// [新增] 请求拦截器：每次请求都带上 Token
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

// [新增] 响应拦截器：处理 Token 过期 (403/401)
axios.interceptors.response.use(response => {
  return response;
}, error => {
  if (error.response && (error.response.status === 401 || error.response.status === 403)) {
    // Token 失效，清除并跳转登录
    localStorage.removeItem('token');
    window.location.href = '/login';
  }
  return Promise.reject(error);
});
import { createPinia } from 'pinia' // 1. 导入 Pinia (你之前选了)

import App from './App.vue'
import router from './router' // 2. 导入你创建的 router

const app = createApp(App)

app.use(createPinia()) // 3. 注册 Pinia
app.use(router) // 4. [关键] 注册路由

app.mount('#app')