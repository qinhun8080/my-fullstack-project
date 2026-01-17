<template>
  <div class="login-page-container">
    <div class="login-card">
      <h1>欢迎登录</h1>
      <form @submit.prevent="handleLogin">

        <div class="input-group">
          <label for="username">用户名</label>
          <input id="username" type="text" v-model="form.username" placeholder="请输入用户名" required />
        </div>

        <div class="input-group">
          <label for="password">密码</label>
          <div class="password-wrapper">
            <input id="password" :type="passwordFieldType" v-model="form.password" placeholder="请输入密码" required />
            <button type="button" class="password-toggle-btn" @click="togglePasswordVisibility">
              <i class="fa-regular" :class="passwordFieldType === 'password' ? 'fa-eye' : 'fa-eye-slash'"></i>
            </button>
          </div>
        </div>

        <div class="input-group">
          <label for="checkcode">验证码</label>
          <div class="captcha-group">
            <input id="checkcode" type="text" v-model="form.captcha" maxlength="4" placeholder="验证码" required />
            <CaptchaCanvas ref="captchaComponentRef" @code-updated="updateCaptchaCode" />
            <a href="#" @click.prevent="refreshCaptcha" class="reload">换一张</a>
          </div>
        </div>

        <div class="options-grid">
          <div class="remember-me">
            <input type="checkbox" v-model="form.rememberMe" id="rememberMe" />
            <label for="rememberMe">记住我</label>
          </div>
          <div class="links">
            <router-link to="/forgot-password">忘记密码？</router-link>
          </div>
        </div>

        <button type="submit" class="login-btn">登 录</button>

        <div class="footer-links">
          <router-link to="/register">注册账号</router-link>

          <button type="button" class="gemini-tool-btn" @click="openPasswordModal">
            <i class="fa-solid fa-wand-magic-sparkles"></i> 密码建议
          </button>
        </div>
      </form>
    </div>

    <div class="modal-overlay" v-if="isModalVisible" @click.self="closePasswordModal">
      <div class="modal">
        <div class="modal-header">
          <h2>安全密码建议</h2>
          <button class="close-modal-btn" @click="closePasswordModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="loading" v-if="isLoadingPassword">
            <div class="loading-spinner"></div>
          </div>
          <div class="result" v-else>
            <div class="password-display">
              <span class="password-text">{{ suggestedPassword }}</span>
              <button type="button" class="copy-btn" @click="copyPassword" title="复制密码">
                <i class="fa-regular fa-copy"></i>
              </button>
            </div>
          </div>
          <p class="copy-success" :class="{ visible: copySuccessMessage }">已复制到剪贴板!</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 1. 从 'vue' 中导入 onMounted
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import CaptchaCanvas from './CaptchaCanvas.vue'

const router = useRouter()

const form = ref({
  username: '',
  password: '',
  rememberMe: false,
  captcha: ''
})

// ... (密码显隐 和 验证码逻辑 保持不变) ...
const isPasswordVisible = ref(false)
const passwordFieldType = computed(() => (isPasswordVisible.value ? 'text' : 'password'))
const togglePasswordVisibility = () => (isPasswordVisible.value = !isPasswordVisible.value)
const correctCaptchaCode = ref('')
const captchaComponentRef = ref(null)
const updateCaptchaCode = (newCode) => {
  correctCaptchaCode.value = newCode;
}
const refreshCaptcha = () => {
  captchaComponentRef.value?.generateCaptcha();
}


// --- 登录逻辑 (保持不变) ---
const handleLogin = async () => {
  if (!form.value.captcha) {
    alert('请输入验证码！');
    return;
  }
  if (form.value.captcha.toLowerCase() !== correctCaptchaCode.value.toLowerCase()) {
    alert('验证码错误！');
    refreshCaptcha();
    form.value.captcha = '';
    return;
  }

  const loginData = {
    username: form.value.username,
    password: form.value.password,
    rememberMe: form.value.rememberMe
  }

  try {
    const response = await axios.post(
      'http://localhost:8081/api/login',
      loginData,
      { withCredentials: true } // [!!] 关键：添加这一行
    );
    if (response.data.success) {
      alert(response.data.message);
      router.push('/dashboard');
    } else {
      alert(response.data.message);
      refreshCaptcha();
    }
  } catch (error) {
    console.error('登录请求失败:', error);
    alert('登录失败，无法连接到服务器。请检查后端是否已启动，以及CORS配置。');
    refreshCaptcha();
  }
}

// --- 密码建议弹窗逻辑 (保持不变) ---
const isModalVisible = ref(false)
const isLoadingPassword = ref(false)
const suggestedPassword = ref('')
// ... (省略了弹窗的几个函数, 保持不变即可) ...
const openPasswordModal = () => { /* ... */ }
const closePasswordModal = () => { /* ... */ }
const suggestPassword = () => { /* ... */ }
const copyPassword = async () => { /* ... */ }


// -----------------------------------------------------------------
// [!!] 新增部分：自动填充逻辑
// -----------------------------------------------------------------

/**
 * 这是一个帮助函数，用于从 document.cookie 字符串中按名称读取Cookie
 */
const getCookie = (name) => {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) {
    // .pop() 获取数组最后一个元素 (即 "cookieValue; otherCookie=...")
    // .split(';') 分割
    // .shift() 获取分割后的第一个元素 (即 "cookieValue")
    return parts.pop().split(';').shift();
  }
  return null; // 如果找不到，返回null
}

// 2. 使用 onMounted 钩子
onMounted(() => {
  // 当组件加载时，立即检查 "remember-user" cookie
  const rememberedUser = getCookie('remember-user');

  if (rememberedUser) {
    // 如果找到了cookie（例如，值是 "admin"）
    console.log('在Cookie中找到了用户:', rememberedUser);

    // a. 将用户名自动填入表单
    form.value.username = rememberedUser;

    // b. 自动勾选 "记住我" 复选框
    form.value.rememberMe = true;
  } else {
    console.log('没有找到 "remember-user" cookie。');
  }
});

</script>

<style scoped>
.login-page-container {
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f0f2f5;
  min-height: 100vh;
  width: 100%;
  position: absolute;
  top: 0;
  left: 0;
}

.login-card {
  background: #fff;
  padding: 3rem;
  border-radius: 1.5rem;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  width: 420px;
  animation: fade-in 0.5s ease-out;
}

@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

h1 {
  text-align: center;
  margin-bottom: 2rem;
  font-size: 2rem;
  color: #111827;
}

.input-group {
  margin-bottom: 1.5rem;
}

/* 通用 label (默认竖向) */
label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #6b7280;
}

input {
  width: 100%;
  padding: 0.8rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 0.5rem;
  font-size: 1rem;
}

.password-wrapper {
  position: relative;
}

.password-toggle-btn {
  position: absolute;
  right: 0.5rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  color: #6b7280;
}

/* "记住我/忘记密码" 区域 */
.options-grid {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  /* 登录按钮上方的间距 */
}

/* “记住我” 区域：水平对齐复选框与文字 */
.remember-me {
  display: flex;
  align-items: center;
  /* 垂直居中 */
  gap: 0.4rem;
  /* 复选框与文字的距离 */
  flex-direction: row;
  /* 保证水平排列 */
}

.remember-me input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.remember-me label {
  margin: 0;
  display: inline-block;
  color: #6b7280;
  font-weight: 500;
  cursor: pointer;
  white-space: nowrap;
  /* 防止文字换行 */
}


.options-grid .links a {
  color: #6366f1;
  text-decoration: none;
  font-weight: 500;
}

.captcha-group {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.reload {
  color: #6366f1;
  text-decoration: none;
  font-weight: 500;
  white-space: nowrap;
}

.login-btn {
  width: 100%;
  padding: 0.9rem;
  background: #6366f1;
  color: white;
  border: none;
  border-radius: 0.75rem;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: 0.2s;
}

.login-btn:hover {
  background: #4f46e5;
  transform: translateY(-2px);
}

/* 底部链接区域 (实现横向并统一字体) */
.footer-links {
  margin-top: 1.5rem;
  text-align: center;
  display: flex;
  justify-content: center;
  gap: 1.5rem;
  /* 链接/按钮间距 */
}

.footer-links a,
.footer-links button {
  color: #6366f1;
  background: none;
  border: none;
  cursor: pointer;
  font-weight: 500;
  font-family: inherit;
  /* 继承字体 */
  font-size: inherit;
  /* 继承字号 */
  text-decoration: none;
}

/* --- 弹窗样式 --- */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal {
  background: white;
  padding: 2rem;
  border-radius: 1rem;
  width: 400px;
  animation: fade-in 0.3s;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.close-modal-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

.modal-body {
  margin-top: 1.5rem;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60px;
}

.loading-spinner {
  border: 4px solid #eee;
  border-top: 4px solid #6366f1;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0);
  }

  100% {
    transform: rotate(360deg);
  }
}

.password-display {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f3f4f6;
  padding: 1rem;
  border-radius: 0.5rem;
}

.password-text {
  font-family: monospace;
  font-size: 1.1rem;
  font-weight: bold;
  letter-spacing: 1px;
}

.copy-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.1rem;
  color: #6b7280;
}

.copy-success {
  text-align: center;
  color: green;
  font-size: 0.8rem;
  opacity: 0;
  transition: opacity 0.3s;
  height: 1.2rem;
  margin-top: 0.5rem;
}

.copy-success.visible {
  opacity: 1;
}
</style>