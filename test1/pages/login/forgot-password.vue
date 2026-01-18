<template>
  <view class="container">
    <view class="login-card">
      <view class="header">
        <text class="title">安全重置</text>
      </view>

      <view class="form-area">
        
        <view class="input-group">
          <text class="label">用户名</text>
          <input 
            class="input-field" 
            v-model="form.username" 
            placeholder="请输入您的用户名" 
            placeholder-class="placeholder-style" 
          />
        </view>

        <view class="input-group">
          <text class="label">预留手机号</text>
          <input 
            class="input-field" 
            v-model="form.phone" 
            type="number"
            maxlength="11"
            placeholder="请输入注册时绑定的手机号" 
            placeholder-class="placeholder-style" 
          />
        </view>

        <view class="input-group">
          <text class="label">新密码</text>
          <view class="password-wrapper">
            <input 
              class="input-field" 
              :password="!showPassword" 
              v-model="form.newPassword" 
              placeholder="请输入新密码" 
              placeholder-class="placeholder-style" 
            />
            <view class="password-toggle" @click="showPassword = !showPassword">
              <uni-icons :type="showPassword ? 'eye-filled' : 'eye-slash-filled'" size="20" color="#6b7280"></uni-icons>
            </view>
          </view>
        </view>

        <view class="input-group">
          <text class="label">确认新密码</text>
          <view class="password-wrapper">
            <input 
              class="input-field" 
              :password="!showConfirmPassword" 
              v-model="form.confirmPassword" 
              placeholder="请再次输入新密码" 
              placeholder-class="placeholder-style" 
            />
            <view class="password-toggle" @click="showConfirmPassword = !showConfirmPassword">
              <uni-icons :type="showConfirmPassword ? 'eye-filled' : 'eye-slash-filled'" size="20" color="#6b7280"></uni-icons>
            </view>
          </view>
        </view>

        <button 
          class="login-btn" 
          @click="handleReset" 
          :disabled="isSubmitting" 
          :class="{loading: isSubmitting}"
        >
          {{ isSubmitting ? '重置中...' : '确认重置' }}
        </button>

        <view class="footer-links">
          <text class="footer-link" @click="goBackLogin">
             返回登录
          </text>
        </view>

      </view>
    </view>
  </view>
</template>

<script>
  const API_BASE_URL = 'http://127.0.0.1:8081/api';

  export default {
    data() {
      return {
        form: {
          username: '',
          phone: '',
          newPassword: '',
          confirmPassword: ''
        },
        showPassword: false,
        showConfirmPassword: false,
        isSubmitting: false
      }
    },
    methods: {
      goBackLogin() {
        uni.navigateBack();
      },
      handleReset() {
        // 1. 前端校验
        if (!this.form.username || !this.form.phone) {
          uni.showToast({ title: '请填写用户名和手机号', icon: 'none' });
          return;
        }
        if (!this.form.newPassword || !this.form.confirmPassword) {
          uni.showToast({ title: '请输入并确认新密码', icon: 'none' });
          return;
        }
        if (this.form.newPassword !== this.form.confirmPassword) {
          uni.showToast({ title: '两次输入的密码不一致', icon: 'none' });
          return;
        }

        this.isSubmitting = true;

        // 2. 发起请求
        uni.request({
          url: API_BASE_URL + '/user/reset-password',
          method: 'POST',
          data: {
            username: this.form.username,
            phone: this.form.phone,
            newPassword: this.form.newPassword
          },
          success: (res) => {
            if (res.data && res.data.success) {
              uni.showToast({ title: '重置成功', icon: 'success' });
              
              // 延迟 1.5秒后返回登录页
              setTimeout(() => {
                uni.navigateBack();
              }, 1500);
            } else {
              uni.showToast({
                title: res.data.message || '重置失败',
                icon: 'none',
                duration: 3000 // 错误信息多停留一会
              });
            }
          },
          fail: () => {
            uni.showToast({ title: '连接服务器失败', icon: 'none' });
          },
          complete: () => {
            this.isSubmitting = false;
          }
        });
      }
    }
  }
</script>

<style lang="scss" scoped>
  /* * 直接复用 Login.vue 的样式 
   * 确保风格完全一致
   */
  .container {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #f0f2f5; 
    padding: 20rpx;
  }

  .login-card {
    background: #ffffff;
    width: 100%;
    max-width: 650rpx;
    padding: 60rpx 40rpx;
    border-radius: 24rpx;
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
  }

  .header {
    margin-bottom: 40rpx;
    text-align: center;
  }

  .title {
    font-size: 40rpx;
    font-weight: bold;
    color: #111827;
  }

  .input-group {
    margin-bottom: 30rpx;
  }

  .label {
    display: block;
    margin-bottom: 12rpx;
    font-size: 28rpx;
    font-weight: 500;
    color: #6b7280;
  }

  .input-field {
    width: 100%;
    height: 90rpx;
    padding: 0 20rpx;
    border: 2rpx solid #d1d5db;
    border-radius: 12rpx;
    font-size: 30rpx;
    color: #333;
    box-sizing: border-box;
    background-color: #fff;
  }

  .placeholder-style {
    color: #9ca3af;
  }

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

  .login-btn {
    background-color: #6366f1;
    color: white;
    font-size: 32rpx;
    font-weight: 600;
    border-radius: 16rpx;
    height: 96rpx;
    line-height: 96rpx;
    border: none;
    margin-top: 20rpx; /* 调整间距 */
    margin-bottom: 30rpx;
    transition: opacity 0.2s;

    &:active {
      opacity: 0.9;
    }
    &.loading {
      opacity: 0.7;
    }
  }

  .footer-links {
    display: flex;
    justify-content: center;
    margin-top: 10rpx;
  }

  .footer-link {
    font-size: 28rpx;
    color: #6366f1;
    font-weight: 500;
  }
</style>