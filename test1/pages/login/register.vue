<template>
  <view class="container">
    <view class="register-card">
      <view class="header">
        <text class="title">用户注册</text>
      </view>

      <view class="form-area">
        
        <view class="input-group">
          <text class="label">账号 <text class="required">*</text></text>
          <input class="input-field" v-model="form.username" placeholder="设置您的账号" placeholder-class="placeholder-style" />
        </view>

        <view class="input-group">
          <text class="label">昵称</text>
          <input class="input-field" v-model="form.nickname" placeholder="怎么称呼您 (选填)" placeholder-class="placeholder-style" />
        </view>

        <view class="input-group">
          <text class="label">性别</text>
          <picker @change="bindGenderChange" :value="genderIndex" :range="genderOptions" range-key="label">
            <view class="input-field picker-view" :class="{ empty: !genderText }">
              {{ genderText || '请选择性别' }}
            </view>
          </picker>
        </view>

        <view class="input-group">
          <text class="label">出生日期</text>
          <picker mode="date" :value="form.birthday" start="1900-01-01" end="2030-12-31" @change="bindDateChange">
            <view class="input-field picker-view" :class="{ empty: !form.birthday }">
              {{ form.birthday || '请选择出生日期' }}
            </view>
          </picker>
        </view>

        <view class="input-group">
          <text class="label">手机号码</text>
          <input class="input-field" type="number" maxlength="11" v-model="form.phone" placeholder="方便找回密码 (选填)" placeholder-class="placeholder-style" />
        </view>

        <view class="input-group">
          <text class="label">密码 <text class="required">*</text></text>
          <view class="password-wrapper">
            <input class="input-field" :password="!showPassword" v-model="form.password" placeholder="设置密码" placeholder-class="placeholder-style" />
            <view class="password-toggle" @click="showPassword = !showPassword">
              <uni-icons :type="showPassword ? 'eye-filled' : 'eye-slash-filled'" size="20" color="#6b7280"></uni-icons>
            </view>
          </view>
        </view>

        <view class="input-group">
          <text class="label">确认密码 <text class="required">*</text></text>
          <view class="password-wrapper">
            <input class="input-field" :password="!showConfirmPassword" v-model="form.confirmPassword" placeholder="请再次输入密码" placeholder-class="placeholder-style" />
            <view class="password-toggle" @click="showConfirmPassword = !showConfirmPassword">
              <uni-icons :type="showConfirmPassword ? 'eye-filled' : 'eye-slash-filled'" size="20" color="#6b7280"></uni-icons>
            </view>
          </view>
        </view>

        <button class="register-btn" @click="handleUserRegister" :disabled="isSubmitting" :class="{loading: isSubmitting}">
          {{ isSubmitting ? '正在注册...' : '注 册' }}
        </button>

        <view class="footer-links">
          <text class="footer-text">已有账号？</text>
          <text class="footer-link" @click="goToLogin">去登录</text>
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
          nickname: '',
          gender: '', // 传给后端的数值 (1, 0, 2)
          birthday: '',
          phone: '',
          password: '',
          confirmPassword: ''
        },
        // 性别选择器配置
        genderOptions: [
          { label: '男', value: 1 },
          { label: '女', value: 0 },
          { label: '其他', value: 2 }
        ],
        genderIndex: -1, // picker 的索引
        genderText: '', // 显示在输入框的文字

        showPassword: false,
        showConfirmPassword: false,
        isSubmitting: false
      }
    },
    methods: {
      goToLogin() {
        uni.navigateBack(); 
      },

      // 处理性别选择
      bindGenderChange(e) {
        this.genderIndex = e.detail.value;
        const selected = this.genderOptions[this.genderIndex];
        this.genderText = selected.label;
        this.form.gender = selected.value; // 保存 1, 0, 2 到表单
      },

      // 处理日期选择
      bindDateChange(e) {
        this.form.birthday = e.detail.value; // YYYY-MM-DD
      },

      handleUserRegister() {
        if (!this.form.username) return uni.showToast({ title: '请输入账号', icon: 'none' });
        if (!this.form.password) return uni.showToast({ title: '请输入密码', icon: 'none' });
        if (this.form.password !== this.form.confirmPassword) {
          return uni.showToast({ title: '两次密码不一致', icon: 'none' });
        }

        this.isSubmitting = true;

        uni.request({
          url: API_BASE_URL + '/user/register',
          method: 'POST',
          data: {
            username: this.form.username,
            nickname: this.form.nickname,
            gender: this.form.gender === '' ? null : this.form.gender, // 确保空值传null
            birthday: this.form.birthday,
            phone: this.form.phone,
            password: this.form.password
          },
          success: (res) => {
            if (res.data && res.data.success) {
              uni.showToast({ title: '注册成功', icon: 'success' });
              setTimeout(() => { this.goToLogin(); }, 1500);
            } else {
              uni.showToast({ title: res.data.message || '注册失败', icon: 'none' });
            }
          },
          fail: (err) => {
            console.error(err);
            uni.showToast({ title: '网络请求失败', icon: 'none' });
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
  /* 全局容器 */
  .container {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #f0f2f5;
    padding: 20rpx;
  }

  .register-card {
    background: #ffffff;
    width: 100%;
    max-width: 650rpx;
    padding: 60rpx 40rpx;
    border-radius: 24rpx;
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
  }

  .header { margin-bottom: 40rpx; text-align: center; }
  .title { font-size: 40rpx; font-weight: bold; color: #111827; }

  .input-group { margin-bottom: 30rpx; }
  .label { display: block; margin-bottom: 12rpx; font-size: 28rpx; font-weight: 500; color: #6b7280; }
  .required { color: #ef4444; margin-left: 4rpx; }

  /* 输入框样式 */
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
    /* 垂直居中 */
    display: flex;
    align-items: center; 
  }

  /* 模拟 picker 的输入框样式 */
  .picker-view {
    justify-content: flex-start;
  }
  .picker-view.empty {
    color: #9ca3af; /* 与 placeholder 颜色一致 */
  }

  .placeholder-style { color: #9ca3af; }

  .password-wrapper { position: relative; width: 100%; }
  .password-toggle {
    position: absolute; right: 20rpx; top: 50%; transform: translateY(-50%); z-index: 10; padding: 10rpx;
  }

  .register-btn {
    background-color: #6366f1; color: white; font-size: 32rpx; font-weight: 600; border-radius: 16rpx; height: 96rpx; line-height: 96rpx; border: none; margin-top: 40rpx; margin-bottom: 30rpx; transition: opacity 0.2s;
  }
  .register-btn:active { opacity: 0.9; }
  .register-btn.loading { opacity: 0.7; }

  .footer-links { display: flex; justify-content: center; align-items: center; gap: 10rpx; margin-top: 10rpx; }
  .footer-text { font-size: 28rpx; color: #6b7280; }
  .footer-link { font-size: 28rpx; color: #6366f1; font-weight: 600; }
</style>