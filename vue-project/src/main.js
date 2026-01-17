// src/main.js

import './assets/main.css' // (保留 Vite 的默认 CSS)

import { createApp } from 'vue'
import { createPinia } from 'pinia' // 1. 导入 Pinia (你之前选了)

import App from './App.vue'
import router from './router' // 2. 导入你创建的 router

const app = createApp(App)

app.use(createPinia()) // 3. 注册 Pinia
app.use(router) // 4. [关键] 注册路由

app.mount('#app')