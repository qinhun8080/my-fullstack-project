import { createApp } from 'vue' // [必须] 之前漏了这个
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import axios from 'axios'

// 1. 配置基础路径 (确保和后端端口一致)
axios.defaults.baseURL = 'http://localhost:8081';

// 2. 请求拦截器：自动带上 Token
axios.interceptors.request.use(config => {
  // 从 localStorage 获取 Token
  const token = localStorage.getItem('token');
  if (token) {
    // [注意] 这里的格式必须是 "Bearer " + token
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

// 3. 响应拦截器：处理 Token 过期或无效 (401/403)
axios.interceptors.response.use(response => {
  return response;
}, error => {
  if (error.response && (error.response.status === 401 || error.response.status === 403)) {
    console.warn('Token 失效或无权限，强制跳转登录页');
    // 清除失效的 Token
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    // 强制跳转
    window.location.href = '/login';
  }
  return Promise.reject(error);
});

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')