<template>
    <div class="login-page-container">
        <div class="login-card">
            <h1>安全重置</h1>
            <form @submit.prevent="handleReset">

                <div class="input-group">
                    <label for="username">账号</label>
                    <input id="username" type="text" v-model="form.username" placeholder="请输入用户名" required />
                </div>

                <div class="input-group">
                    <label for="phone">预留手机号</label>
                    <input id="phone" type="text" v-model="form.phone" placeholder="请输入注册时绑定的手机号" required />
                </div>

                <div class="input-group">
                    <label for="new-password">新密码</label>
                    <div class="password-wrapper">
                        <input id="new-password" :type="passwordFieldType" v-model="form.newPassword"
                            placeholder="请输入新密码" required />
                        <button type="button" class="password-toggle-btn" @click="togglePasswordVisibility">
                            <i class="fa-regular" :class="isPasswordVisible ? 'fa-eye-slash' : 'fa-eye'"></i>
                        </button>
                    </div>
                </div>

                <div class="input-group">
                    <label for="confirm-password">确认密码</label>
                    <div class="password-wrapper">
                        <input id="confirm-password" :type="passwordFieldType" v-model="form.confirmPassword"
                            placeholder="请再次输入新密码" required />
                    </div>
                </div>

                <div class="input-group">
                    <label for="checkcode">验证码</label>
                    <div class="captcha-group">
                        <input id="checkcode" type="text" v-model="form.captcha" maxlength="4" placeholder="验证码"
                            required />
                        <CaptchaCanvas ref="captchaComponentRef" @code-updated="updateCaptchaCode" />
                        <a href="#" @click.prevent="refreshCaptcha" class="reload">换一张</a>
                    </div>
                </div>

                <button type="submit" class="login-btn" :disabled="isLoading">
                    {{ isLoading ? '验证并重置...' : '确认重置' }}
                </button>

                <div class="footer-links">
                    <router-link to="/login">
                        <i class="fa-solid fa-arrow-left"></i> 返回登录
                    </router-link>
                </div>

            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import CaptchaCanvas from './CaptchaCanvas.vue'

const router = useRouter()
const isLoading = ref(false)

// [修改] 表单数据增加 phone
const form = ref({
    username: '',
    phone: '',
    newPassword: '',
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

// --- 提交重置 ---
const handleReset = async () => {
    if (form.value.captcha.toLowerCase() !== correctCaptchaCode.value.toLowerCase()) {
        alert('验证码错误！');
        refreshCaptcha();
        form.value.captcha = '';
        return;
    }

    if (form.value.newPassword !== form.value.confirmPassword) {
        alert('两次输入的密码不一致！');
        return;
    }

    isLoading.value = true;
    try {
        // 发送请求，包含 phone
        const response = await axios.post('http://localhost:8081/api/reset-password', {
            username: form.value.username,
            phone: form.value.phone,       // [关键] 传给后端
            newPassword: form.value.newPassword
        });

        if (response.data.success) {
            alert(response.data.message);
            router.push('/login');
        } else {
            alert(response.data.message); // "账号与手机号不匹配"
            refreshCaptcha();
        }
    } catch (error) {
        console.error('重置请求失败:', error);
        alert('连接服务器失败，请稍后再试');
    } finally {
        isLoading.value = false;
    }
}
</script>

<style scoped>
/* 样式与之前保持完全一致，无需改动 */
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
    margin-top: 1rem;
}

.login-btn:hover {
    background: #4f46e5;
    transform: translateY(-2px);
}

.login-btn:disabled {
    background: #a5a6f6;
    cursor: not-allowed;
    transform: none;
}

.footer-links {
    margin-top: 1.5rem;
    text-align: center;
}

.footer-links a {
    color: #6366f1;
    text-decoration: none;
    font-weight: 500;
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
}

.footer-links a:hover {
    text-decoration: underline;
}
</style>