<template>
  <view class="container">
    <view class="login-card">
      <view class="header">
        <text class="title">欢迎登录</text>
      </view>

      <view class="form-area">
        
        <view class="input-group">
          <text class="label">用户名</text>
          <input 
            class="input-field" 
            v-model="username" 
            placeholder="请输入用户名" 
            placeholder-class="placeholder-style" 
          />
        </view>

        <view class="input-group">
          <text class="label">密码</text>
          <view class="password-wrapper">
            <input 
              class="input-field" 
              :password="!showPassword" 
              v-model="password" 
              placeholder="请输入密码" 
              placeholder-class="placeholder-style" 
            />
            <view class="password-toggle" @click="togglePasswordVisibility">
              <uni-icons :type="showPassword ? 'eye-filled' : 'eye-slash-filled'" size="20" color="#6b7280"></uni-icons>
            </view>
          </view>
        </view>

        <view class="options-grid">
         <view class="remember-me">
           <checkbox-group @change="handleCheckboxChange">
             <label class="checkbox-label">
               <checkbox value="1" :checked="rememberMe" style="transform:scale(0.7)" color="#6366f1" />
               <text class="checkbox-text">记住我</text>
             </label>
           </checkbox-group>
         </view>
          <view class="forgot-link" @click="goToForgotPassword">
            <text>忘记密码？</text>
          </view>
        </view>

        <button 
          class="login-btn" 
          @click="handleLogin" 
          :disabled="loggingIn" 
          :class="{loading: loggingIn}"
        >
          {{ loggingIn ? '登 录 中...' : '登 录' }}
        </button>

        <view class="footer-links">
          <text class="footer-link" @click="goToRegister">注册账号</text>
          
          <view class="footer-btn" @click="showPasswordTip">
            <uni-icons type="star" size="14" color="#6366f1"></uni-icons>
            <text class="footer-link">密码建议</text>
          </view>
        </view>

      </view>
    </view>
  </view>
</template>

<script>
  // 确保 IP 地址正确
  const API_BASE_URL = 'http://127.0.0.1:8081/api';

  export default {
    data() {
      return {
        username: '',
        password: '',
        loggingIn: false,
        showPassword: false,
        rememberMe: false
      }
    },
    methods: {
      togglePasswordVisibility() {
        this.showPassword = !this.showPassword;
      },
      handleCheckboxChange(e) {
          // 你的原有逻辑：判断数组长度
          this.rememberMe = e.detail.value.length > 0;
          console.log('记住我状态:', this.rememberMe);
      },
      showPasswordTip() {
        uni.showToast({ title: '密码建议功能开发中', icon: 'none' });
      },
      handleLogin() {
        if (!this.username || !this.password) {
          uni.showToast({ title: '请输入账号和密码', icon: 'none' });
          return;
        }

        this.loggingIn = true;

        uni.request({
          url: API_BASE_URL + '/login',
          method: 'POST',
          // [原有逻辑已恢复] 保持 Session/Cookie 传递能力
          withCredentials: true, 
          header: {
             // 显式指定 JSON，防止部分环境默认表单提交
            'content-type': 'application/json'
          },
          data: {
            username: this.username,
            password: this.password,
            // [原有逻辑已恢复] 传递记住我状态
            rememberMe: this.rememberMe 
          },
          success: (res) => {
            console.log('登录结果:', res.data);
            if (res.data && res.data.success) {
              
              // 1. [新增] 必须存储 Token 才能让后续接口鉴权通过
              if (res.data.token) {
                 uni.setStorageSync('token', res.data.token);
              }
              
              // 2. 你的原有存储逻辑
              uni.setStorageSync('hasLogin', true);
              uni.setStorageSync('userInfo', res.data.userData);
              
              uni.showToast({ title: '登录成功', icon: 'success' });
              
              setTimeout(() => {
                uni.switchTab({
                  url: '/pages/index/index'
                });
              }, 1000);
            } else {
              uni.showToast({
                title: res.data.message || '登录失败',
                icon: 'none'
              });
            }
          },
          fail: (err) => {
            console.error('请求失败:', err);
            uni.showToast({ title: '无法连接服务器', icon: 'none' });
          },
          complete: () => {
            this.loggingIn = false;
          },
        });
      },
      goToRegister() {
          uni.navigateTo({ url: '/pages/login/register' });
      },
      goToForgotPassword() {
          uni.navigateTo({ url: '/pages/login/forgot-password' });
      },
    }
  }
</script>

<style lang="scss" scoped>
  /* 全局容器：模拟 Web 版的 body 背景 */
  .container {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #f0f2f5; /* 灰色背景 */
    padding: 20rpx;
  }

  /* 登录卡片：白色、圆角、阴影 */
  .login-card {
    background: #ffffff;
    width: 100%;
    max-width: 650rpx; /* 限制最大宽度，类似 Web 版的 width: 420px */
    padding: 60rpx 40rpx;
    border-radius: 24rpx;
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05); /* 轻柔阴影 */
  }

  .header {
    margin-bottom: 40rpx;
    text-align: center;
  }

  .title {
    font-size: 40rpx;
    font-weight: bold;
    color: #111827; /* 深黑色标题 */
  }

  /* 输入框组 */
  .input-group {
    margin-bottom: 30rpx;
  }

  .label {
    display: block;
    margin-bottom: 12rpx;
    font-size: 28rpx;
    font-weight: 500;
    color: #6b7280; /* 标签灰色 */
  }

  /* 输入框样式：方框、边框 */
  .input-field {
    width: 100%;
    height: 90rpx;
    padding: 0 20rpx;
    border: 2rpx solid #d1d5db; /* 浅灰边框 */
    border-radius: 12rpx;
    font-size: 30rpx;
    color: #333;
    box-sizing: border-box; /* 确保 padding 不撑大宽度 */
    background-color: #fff;
  }

  .placeholder-style {
    color: #9ca3af;
  }

  /* 密码框包装器，用于定位眼睛图标 */
  .password-wrapper {
    position: relative;
    width: 100%;
  }

  .password-toggle {
    position: absolute;
    right: 20rpx;
    top: 50%;
    transform: translateY(-50%);
    z-index: 10;
    padding: 10rpx;
  }

  /* 选项行：记住我 - 忘记密码 */
  .options-grid {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40rpx;
  }

  .checkbox-label {
    display: flex;
    align-items: center;
  }

  .checkbox-text {
    font-size: 28rpx;
    color: #6b7280;
    margin-left: 4rpx;
  }

  .forgot-link {
    text {
      font-size: 28rpx;
      color: #6366f1; /* 靛蓝色链接 */
      font-weight: 500;
    }
  }

  /* 登录按钮 */
  .login-btn {
    background-color: #6366f1; /* 纯色背景，不再是渐变 */
    color: white;
    font-size: 32rpx;
    font-weight: 600;
    border-radius: 16rpx;
    height: 96rpx;
    line-height: 96rpx;
    border: none;
    margin-bottom: 30rpx;
    transition: opacity 0.2s;

    &:active {
      opacity: 0.9;
    }

    &.loading {
      opacity: 0.7;
    }
  }

  /* 底部链接 */
  .footer-links {
    display: flex;
    justify-content: center;
    gap: 30rpx;
    margin-top: 10rpx;
  }

  .footer-btn {
    display: flex;
    align-items: center;
    gap: 6rpx;
  }

  .footer-link {
    font-size: 28rpx;
    color: #6366f1;
    font-weight: 500;
  }
</style>