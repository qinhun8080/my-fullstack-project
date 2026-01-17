// src/router/index.js

import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '../components/LoginPage.vue'
import AdminDashboard from '../components/AdminDashboard.vue'
import axios from 'axios' // [!!] 1. 导入 axios
import Register from '../components/Register.vue'
import ForgotPassword from '../components/ForgotPassword.vue'

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
      meta: { requiresAuth: false } // [!!] 2. 标记此页面不需要登录
    },
    {
      path: '/dashboard', 
      name: 'dashboard', 
      component: AdminDashboard,
      meta: { requiresAuth: true } // [!!] 3. 标记此页面 *需要* 登录
    },
    {
    path: '/register',
    name: 'Register',
    component: Register
   },
  {
    path: '/forgot-password', // 新增路由
    name: 'ForgotPassword',
    component: ForgotPassword
  }
  
  ]
})

// ----------------------------------------------------------------
// [!!] 4. 添加全局前置路由守卫
// ----------------------------------------------------------------
router.beforeEach(async (to, from, next) => {

  // 检查目标路由是否需要登录
  const requiresAuth = to.meta.requiresAuth;

  // 访问不需要登录的页面 (如 /login)
  if (!requiresAuth) {
    // 我们额外检查一下，如果用户 *已经* 登录了，还想访问登录页
    // 那么我们直接把他重定向到 /dashboard，不让他重复登录
    try {
      const response = await axios.get('http://localhost:8081/api/session-status', {
        withCredentials: true 
      });
      if (response.data.loggedIn && to.path === '/login') {
        console.log('用户已登录，重定向到 /dashboard');
        next({ path: '/dashboard' }); // 已登录，去主页
        return;
      }
    } catch (error) {
      // API 失败，就当下未登录处理，放行
      console.warn('Session 检查失败（访问登录页时）:', error.message);
    }
    
    // 正常访问登录页 (因为未登录)，放行
    console.log('允许访问登录页');
    next();
    return;
  }

  // ----------------------------------------------------------------
  // 访问 *需要* 登录的页面 (如 /dashboard)
  // ----------------------------------------------------------------
  try {
    // 每次都带上 Cookie 询问后端
    const response = await axios.get('http://localhost:8081/api/session-status', {
      withCredentials: true 
    });

    if (response.data.loggedIn) {
      // 确认已登录，放行
      console.log('用户已登录，允许访问 /dashboard');
      next();
    } else {
      // 确认未登录，重定向到登录页
      console.log('用户未登录，重定向到 /login');
      next({ path: '/login' });
    }
  } catch (error) {
    // 如果API请求失败 (例如后端挂了)，也跳转到登录页
    console.error('Session检查失败（访问仪表板时）:', error.message);
    next({ path: '/login' });
  }
});


export default router