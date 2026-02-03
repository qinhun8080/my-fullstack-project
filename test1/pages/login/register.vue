<template>
  <view class="container">
    <view class="register-card">
      <view class="header">
        <text class="title">用户注册</text>
      </view>

      <view class="form-area">
        
        <view class="input-group">
          <text class="label">账号 <text class="required">*</text></text>
          <view class="input-wrapper">
            <input class="input-field" v-model="form.username" placeholder="设置您的账号" placeholder-class="placeholder-style" />
          </view>
        </view>

        <view class="input-group">
          <text class="label">昵称</text>
          <view class="input-wrapper">
            <input class="input-field" v-model="form.nickname" placeholder="怎么称呼您 (选填)" placeholder-class="placeholder-style" />
          </view>
        </view>

        <view class="input-group">
          <text class="label">性别</text>
          <view class="input-wrapper">
            <picker @change="bindGenderChange" :value="genderIndex" :range="genderOptions" range-key="label" style="width:100%">
              <view class="input-field picker-view" :class="{ empty: !genderText }">
                {{ genderText || '请选择性别' }}
              </view>
            </picker>
             <uni-icons type="bottom" size="16" color="#c0c4cc"></uni-icons>
          </view>
        </view>

        <view class="input-group">
          <text class="label">出生日期</text>
          <view class="input-wrapper">
            <picker mode="date" :value="form.birthday" start="1900-01-01" end="2030-12-31" @change="bindDateChange" style="width:100%">
              <view class="input-field picker-view" :class="{ empty: !form.birthday }">
                {{ form.birthday || '请选择出生日期' }}
              </view>
            </picker>
            <uni-icons type="calendar" size="16" color="#c0c4cc"></uni-icons>
          </view>
        </view>

        <view class="input-group">
          <text class="label">手机号码</text>
          <view class="input-wrapper">
            <input class="input-field" type="number" maxlength="11" v-model="form.phone" placeholder="方便找回密码 (选填)" placeholder-class="placeholder-style" />
          </view>
        </view>

        <view class="input-group">
          <text class="label">密码 <text class="required">*</text></text>
          <view class="input-wrapper">
            <input class="input-field" :password="!showPassword" v-model="form.password" placeholder="设置密码" placeholder-class="placeholder-style" />
            <view class="password-toggle" @click="showPassword = !showPassword">
              <uni-icons :type="showPassword ? 'eye-filled' : 'eye-slash-filled'" size="20" color="#c0c4cc"></uni-icons>
            </view>
          </view>
        </view>

        <view class="input-group">
          <text class="label">确认密码 <text class="required">*</text></text>
          <view class="input-wrapper">
            <input class="input-field" :password="!showConfirmPassword" v-model="form.confirmPassword" placeholder="请再次输入密码" placeholder-class="placeholder-style" />
            <view class="password-toggle" @click="showConfirmPassword = !showConfirmPassword">
              <uni-icons :type="showConfirmPassword ? 'eye-filled' : 'eye-slash-filled'" size="20" color="#c0c4cc"></uni-icons>
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
				genderOptions: [{
						label: '男',
						value: 1
					},
					{
						label: '女',
						value: 0
					},
					{
						label: '其他',
						value: 2
					}
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
				if (!this.form.username) return uni.showToast({
					title: '请输入账号',
					icon: 'none'
				});
				if (!this.form.password) return uni.showToast({
					title: '请输入密码',
					icon: 'none'
				});
				if (this.form.password !== this.form.confirmPassword) {
					return uni.showToast({
						title: '两次密码不一致',
						icon: 'none'
					});
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
							uni.showToast({
								title: '注册成功',
								icon: 'success'
							});
							setTimeout(() => {
								this.goToLogin();
							}, 1500);
						} else {
							uni.showToast({
								title: res.data.message || '注册失败',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						console.error(err);
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						});
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
	/* 复用前面页面的优秀样式 */
	.container {
		min-height: 100vh;
		display: flex;
		justify-content: center;
		align-items: center;
		background: #f0f2f5;
		padding: 20rpx;
	}

	.register-card {
		background: rgba(255, 255, 255, 0.98);
		width: 100%;
		max-width: 680rpx;
		padding: 50rpx 40rpx;
		border-radius: 24rpx;
		box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.05);
	}

	.header {
		margin-bottom: 40rpx;
		text-align: center;
	}

	.title {
		font-size: 40rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
	}

	.input-group {
		margin-bottom: 30rpx;
	}

	.label {
		display: block;
		margin-bottom: 12rpx;
		font-size: 28rpx;
		font-weight: 600;
		color: #606266;
	}

	.required {
		color: #ff4d4f;
		margin-left: 4rpx;
	}

	/* --- 增强版输入框容器 --- */
	.input-wrapper {
		display: flex;
		align-items: center;
		background: #f5f7fa;
		/* 浅灰蓝背景 */
		border: 2rpx solid #e4e7ed;
		/* 浅边框 */
		border-radius: 12rpx;
		padding: 0 24rpx;
		height: 90rpx;
		/* 注册页输入项多，高度稍微紧凑一点 */
		transition: all 0.3s;
	}

	.input-wrapper:focus-within {
		background: #fff;
		border-color: #1890FF;
		box-shadow: 0 0 0 6rpx rgba(24, 144, 255, 0.1);
	}

	.input-field {
		flex: 1;
		height: 100%;
		font-size: 30rpx;
		color: #333;
		display: flex;
		align-items: center;
	}

	.placeholder-style {
		color: #c0c4cc;
	}

	.picker-view.empty {
		color: #c0c4cc;
	}

	/* Picker空值颜色 */

	.register-btn {
		background: linear-gradient(135deg, #3B9DFF 0%, #1890FF 100%);
		color: white;
		font-size: 32rpx;
		font-weight: 600;
		border-radius: 50rpx;
		height: 96rpx;
		line-height: 96rpx;
		box-shadow: 0 10rpx 20rpx rgba(24, 144, 255, 0.3);
		border: none;
		margin-top: 40rpx;
		margin-bottom: 30rpx;
	}

	.register-btn:active {
		opacity: 0.9;
		transform: scale(0.99);
	}

	.footer-links {
		display: flex;
		justify-content: center;
		gap: 10rpx;
	}

	.footer-text {
		font-size: 28rpx;
		color: #999;
	}

	.footer-link {
		font-size: 28rpx;
		color: #1890FF;
		font-weight: 600;
	}
</style>