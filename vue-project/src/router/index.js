// src/router/index.js

import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '../components/LoginPage.vue'
import AdminDashboard from '../components/AdminDashboard.vue'
import Register from '../components/Register.vue'
import ForgotPassword from '../components/ForgotPassword.vue'

// 注意：这里不再需要引入 axios，因为路由守卫只检查本地有没有 Token
// Token 的有效性校验交由 axios 拦截器在发起数据请求时处理

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: LoginPage,
      meta: { requiresAuth: false } // 不需要登录
    },
    {
      path: '/dashboard', 
      name: 'dashboard', 
      component: AdminDashboard,
      meta: { requiresAuth: true } // 需要登录
    },
    {
      path: '/register',
      name: 'Register',
      component: Register,
      meta: { requiresAuth: false } // 注册不需要登录
    },
    {
      path: '/forgot-password',
      name: 'ForgotPassword',
      component: ForgotPassword,
      meta: { requiresAuth: false } // 忘记密码不需要登录
    }
  ]
})

// ----------------------------------------------------------------
// 全局前置路由守卫 (Token 版)
// ----------------------------------------------------------------
router.beforeEach((to, from, next) => {
  // 1. 获取目标路由是否需要认证
  const requiresAuth = to.meta.requiresAuth;
  
  // 2. 检查本地是否有 Token
  const token = localStorage.getItem('token');

  // 3. 特殊处理：如果用户已登录（有Token）还想去登录页，直接踢回 Dashboard
  if (to.path === '/login') {
    if (token) {
      console.log('用户已登录，重定向到 /dashboard');
      next('/dashboard');
      return;
    } else {
      next(); // 没登录去登录页，放行
      return;
    }
  }

  // 4. 标准权限判断
  if (requiresAuth) {
    if (token) {
      // 有 Token，放行
      // (注意：这里只判断“有没有”，Token是否过期由后续的API请求拦截器判断)
      next();
    } else {
      // 没 Token，强制跳转登录页
      console.log('无 Token，重定向到 /login');
      next('/login');
    }
  } else {
    // 不需要认证的页面 (如注册、忘记密码)，直接放行
    next();
  }
});

export default router