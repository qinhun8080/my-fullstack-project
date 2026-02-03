<template>
  <view class="container">
    <view class="login-card">
      <view class="header">
        <text class="title">安全重置</text>
      </view>

      <view class="form-area">
        
        <view class="input-group">
          <text class="label">用户名</text>
          <view class="input-wrapper">
            <input 
              class="input-field" 
              v-model="form.username" 
              placeholder="请输入您的用户名" 
              placeholder-class="placeholder-style" 
            />
          </view>
        </view>

        <view class="input-group">
          <text class="label">预留手机号</text>
          <view class="input-wrapper">
            <input 
              class="input-field" 
              v-model="form.phone" 
              type="number"
              maxlength="11"
              placeholder="请输入注册时绑定的手机号" 
              placeholder-class="placeholder-style" 
            />
          </view>
        </view>

        <view class="input-group">
          <text class="label">新密码</text>
          <view class="input-wrapper">
            <input 
              class="input-field" 
              :password="!showPassword" 
              v-model="form.newPassword" 
              placeholder="请输入新密码" 
              placeholder-class="placeholder-style" 
            />
            <view class="password-toggle" @click="showPassword = !showPassword">
              <uni-icons :type="showPassword ? 'eye-filled' : 'eye-slash-filled'" size="20" color="#c0c4cc"></uni-icons>
            </view>
          </view>
        </view>

        <view class="input-group">
          <text class="label">确认新密码</text>
          <view class="input-wrapper">
            <input 
              class="input-field" 
              :password="!showConfirmPassword" 
              v-model="form.confirmPassword" 
              placeholder="请再次输入新密码" 
              placeholder-class="placeholder-style" 
            />
            <view class="password-toggle" @click="showConfirmPassword = !showConfirmPassword">
              <uni-icons :type="showConfirmPassword ? 'eye-filled' : 'eye-slash-filled'" size="20" color="#c0c4cc"></uni-icons>
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
  /* 容器样式保持不变 */
  .container {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: #f0f2f5; 
    position: relative;
    padding: 20rpx;
  }
  .login-card {
    background: #ffffff;
    width: 100%;
    max-width: 650rpx;
    padding: 60rpx 40rpx;
    border-radius: 24rpx;
    box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.05);
    z-index: 1;
  }
  .header { margin-bottom: 40rpx; text-align: center; }
  .title { font-size: 40rpx; font-weight: bold; color: #333; }
  
  .input-group { margin-bottom: 30rpx; }
  .label { display: block; margin-bottom: 12rpx; font-size: 28rpx; font-weight: 600; color: #606266; }

  /* --- 核心修改：增强输入框样式 --- */
  .input-wrapper {
      display: flex;
      align-items: center;
      /* 背景色加深一点，与纯白卡片区分 */
      background: #f5f7fa; 
      /* 增加极细边框 */
      border: 2rpx solid #e4e7ed; 
      border-radius: 12rpx;
      padding: 0 24rpx;
      height: 96rpx;
      transition: all 0.3s;
  }
  
  /* 聚焦时的状态：变白、蓝边框、微发光 */
  .input-wrapper:focus-within {
      background: #ffffff;
      border-color: #1890FF;
      box-shadow: 0 0 0 6rpx rgba(24, 144, 255, 0.1);
  }

  .input-field { flex: 1; height: 100%; font-size: 30rpx; color: #333; }
  .placeholder-style { color: #c0c4cc; }
  .password-toggle { padding: 10rpx; display: flex; align-items: center; }

  /* 按钮样式微调 */
  .login-btn {
    background: linear-gradient(135deg, #3B9DFF 0%, #1890FF 100%);
    color: white;
    font-size: 32rpx;
    font-weight: 600;
    border-radius: 48rpx;
    height: 96rpx;
    line-height: 96rpx;
    box-shadow: 0 8rpx 16rpx rgba(24, 144, 255, 0.25);
    border: none;
    margin-top: 30rpx;
    margin-bottom: 40rpx;
  }
  .login-btn:active { opacity: 0.9; transform: scale(0.99); }
  .footer-links { display: flex; justify-content: center; }
  .footer-link { font-size: 28rpx; color: #1890FF; font-weight: 500; }
</style>
