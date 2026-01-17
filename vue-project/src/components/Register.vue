<template>
  <div class="register-page-container">
    <div class="register-card">
      <h1>注册账号</h1>
      <form @submit.prevent="handleRegister">

        <div class="input-group">
          <label for="username">用户名</label>
          <input id="username" type="text" v-model="form.username" placeholder="请输入用户名" required />
        </div>

        <div class="input-group">
          <label for="nickname">昵称</label>
          <input id="nickname" type="text" v-model="form.nickname" placeholder="您的称呼 (选填)" />
        </div>

        <div class="input-group">
          <label for="gender">性别</label>
          <select id="gender" v-model="form.gender" class="styled-select">
            <option value="" disabled>请选择性别</option>
            <option :value="1">男</option>
            <option :value="0">女</option>
            <option :value="2">其他</option>
          </select>
        </div>

        <div class="input-group">
          <label for="birthday">出生日期</label>
          <input id="birthday" type="date" v-model="form.birthday" class="styled-date" />
        </div>

        <div class="input-group">
          <label for="phone">手机号码</label>
          <input id="phone" type="tel" v-model="form.phone" placeholder="请输入手机号" />
        </div>

        <div class="input-group">
          <label for="password">密码</label>
          <div class="password-wrapper">
            <input id="password" :type="passwordFieldType" v-model="form.password" placeholder="设置密码" required />
            <button type="button" class="password-toggle-btn" @click="togglePasswordVisibility">
              <i class="fa-regular" :class="passwordFieldType === 'password' ? 'fa-eye' : 'fa-eye-slash'"></i>
            </button>
          </div>
        </div>

        <div class="input-group">
          <label for="confirmPassword">确认密码</label>
          <div class="password-wrapper">
            <input id="confirmPassword" type="password" v-model="form.confirmPassword" placeholder="再次输入密码" required />
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

        <button type="submit" class="register-btn">注 册</button>

        <div class="footer-links">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import CaptchaCanvas from '../components/CaptchaCanvas.vue'

const router = useRouter()

const form = ref({
  username: '',
  nickname: '',
  gender: '',   // [新增] 默认为空
  birthday: '', // [新增]
  phone: '',
  password: '',
  confirmPassword: '',
  captcha: ''
})

// --- 密码显隐逻辑 ---
const isPasswordVisible = ref(false)
const passwordFieldType = computed(() => (isPasswordVisible.value ? 'text' : 'password'))
const togglePasswordVisibility = () => (isPasswordVisible.value = !isPasswordVisible.value)

// --- 验证码逻辑 ---
const correctCaptchaCode = ref('')
const captchaComponentRef = ref(null)
const updateCaptchaCode = (newCode) => {
  correctCaptchaCode.value = newCode;
}
const refreshCaptcha = () => {
  captchaComponentRef.value?.generateCaptcha();
}

// --- 注册逻辑 ---
const handleRegister = async () => {
  if (!form.value.captcha) return alert('请输入验证码！');
  if (form.value.captcha.toLowerCase() !== correctCaptchaCode.value.toLowerCase()) {
    alert('验证码错误！');
    refreshCaptcha();
    form.value.captcha = '';
    return;
  }
  if (form.value.password !== form.value.confirmPassword) return alert('两次输入的密码不一致！');

  // 构造数据
  const registerData = {
    username: form.value.username,
    nickname: form.value.nickname,
    gender: form.value.gender === '' ? null : form.value.gender, // 确保传给后端的是数字或null
    birthday: form.value.birthday, // 格式通常是 YYYY-MM-DD
    phone: form.value.phone,
    password: form.value.password
  }

  try {
    const response = await axios.post('http://localhost:8081/api/register', registerData);

    if (response.data.success) {
      alert(response.data.message);
      router.push('/login');
    } else {
      alert(response.data.message);
      refreshCaptcha();
    }
  } catch (error) {
    console.error('注册请求失败:', error);
    alert('注册失败，无法连接到服务器。');
    refreshCaptcha();
  }
}
</script>

<style scoped>
/* 样式保持一致，增加了 select 和 date 的样式 */
.register-page-container {
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

.register-card {
  background: #fff;
  padding: 2.5rem 3rem;
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
  margin-bottom: 1.5rem;
  font-size: 2rem;
  color: #111827;
}

.input-group {
  margin-bottom: 1.2rem;
}

label {
  display: block;
  margin-bottom: 0.4rem;
  font-weight: 500;
  color: #6b7280;
  font-size: 0.9rem;
}

/* 统一输入框样式 */
input,
.styled-select {
  width: 100%;
  padding: 0.8rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 0.5rem;
  font-size: 1rem;
  transition: border-color 0.2s;
  background: #fff;
  font-family: inherit;
}

input:focus,
.styled-select:focus {
  border-color: #6366f1;
  outline: none;
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
  font-size: 0.9rem;
}

.register-btn {
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
  margin-top: 1rem;
}

.register-btn:hover {
  background: #4f46e5;
  transform: translateY(-2px);
}

.footer-links {
  margin-top: 1.5rem;
  text-align: center;
  font-size: 0.95rem;
  color: #6b7280;
}

.footer-links a {
  color: #6366f1;
  text-decoration: none;
  font-weight: 600;
  margin-left: 0.5rem;
}

.footer-links a:hover {
  text-decoration: underline;
}
</style>