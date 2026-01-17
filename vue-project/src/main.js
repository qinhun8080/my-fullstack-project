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
  // [修复] 只有在收到 401 (未授权) 时才强制跳转登录
  // 403 (禁止访问) 通常表示权限不足，但不代表没登录，不应该直接踢出，除非你确定要这么做
  if (error.response && error.response.status === 401) {
    console.warn('Token 失效，强制跳转登录页');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
  } 
  // 如果是 403，可以在控制台打印，或者弹窗提示，而不是直接踢出
  else if (error.response && error.response.status === 403) {
      console.error('权限不足 (403):', error.config.url);
      // 可选：如果不希望 403 踢出用户，就注释掉下面这行
      // window.location.href = '/login'; 
  }
  return Promise.reject(error);
});

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')