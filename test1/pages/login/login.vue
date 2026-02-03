<template>
	<view class="container">
		<view class="login-card">
			<view class="header">
				<text class="title">欢迎登录</text>
			</view>

			<view class="form-area">

				<view class="input-group">
					<text class="label">用户名</text>
					<view class="input-wrapper">
						<input class="input-field" v-model="username" placeholder="请输入用户名"
							placeholder-class="placeholder-style" />
					</view>
				</view>

				<view class="input-group">
					<text class="label">密码</text>
					<view class="input-wrapper">
						<input class="input-field" :password="!showPassword" v-model="password" placeholder="请输入密码"
							placeholder-class="placeholder-style" />
						<view class="password-toggle" @click="togglePasswordVisibility">
							<uni-icons :type="showPassword ? 'eye-filled' : 'eye-slash-filled'" size="20"
								color="#c0c4cc"></uni-icons>
						</view>
					</view>
				</view>

				<view class="options-grid">
					<view class="remember-me">
						<checkbox-group @change="handleCheckboxChange">
							<label class="checkbox-label">
								<checkbox value="1" :checked="rememberMe" style="transform:scale(0.7)"
									color="#1890FF" />
								<text class="checkbox-text">记住我</text>
							</label>
						</checkbox-group>
					</view>
					<view class="forgot-link" @click="goToForgotPassword">
						<text>忘记密码？</text>
					</view>
				</view>

				<button class="login-btn" @click="handleLogin" :disabled="loggingIn" :class="{loading: loggingIn}">
					{{ loggingIn ? '登 录 中...' : '登 录' }}
				</button>

				<view class="footer-links">
					<text class="footer-link" @click="goToRegister">注册账号</text>
					<view class="footer-btn" @click="showPasswordTip">
						<uni-icons type="star" size="14" color="#1890FF"></uni-icons>
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
				uni.showToast({
					title: '密码建议功能开发中',
					icon: 'none'
				});
			},
			handleLogin() {
				if (!this.username || !this.password) {
					uni.showToast({
						title: '请输入账号和密码',
						icon: 'none'
					});
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

							uni.showToast({
								title: '登录成功',
								icon: 'success'
							});

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
						uni.showToast({
							title: '无法连接服务器',
							icon: 'none'
						});
					},
					complete: () => {
						this.loggingIn = false;
					},
				});
			},
			goToRegister() {
				uni.navigateTo({
					url: '/pages/login/register'
				});
			},
			goToForgotPassword() {
				uni.navigateTo({
					url: '/pages/login/forgot-password'
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
	/* 复制上方 Forgot Password 页面的样式，保持风格完全一致 */
	.container {
		min-height: 100vh;
		display: flex;
		justify-content: center;
		align-items: center;
		background: #f0f2f5;
		position: relative;
		overflow: hidden;
	}

	.login-card {
		background: rgba(255, 255, 255, 0.98);
		width: 90%;
		max-width: 680rpx;
		padding: 60rpx 50rpx;
		border-radius: 24rpx;
		box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.06);
		z-index: 1;
	}

	.header {
		margin-bottom: 50rpx;
		text-align: left;
	}

	.title {
		font-size: 48rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
	}

	.input-group {
		margin-bottom: 40rpx;
	}

	.label {
		display: block;
		margin-bottom: 16rpx;
		font-size: 28rpx;
		font-weight: 600;
		color: #606266;
	}

	/* --- 优化后的输入框 --- */
	.input-wrapper {
		display: flex;
		align-items: center;
		background: #f5f7fa;
		/* 明显但不突兀的背景 */
		border: 2rpx solid #e4e7ed;
		/* 柔和边框 */
		border-radius: 12rpx;
		padding: 0 24rpx;
		height: 100rpx;
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
	}

	.placeholder-style {
		color: #c0c4cc;
	}

	.options-grid {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 50rpx;
	}

	.checkbox-label {
		display: flex;
		align-items: center;
	}

	.checkbox-text {
		font-size: 26rpx;
		color: #666;
		margin-left: 10rpx;
	}

	.forgot-link text {
		font-size: 26rpx;
		color: #1890FF;
	}

	.login-btn {
		background: linear-gradient(135deg, #3B9DFF 0%, #1890FF 100%);
		color: white;
		font-size: 34rpx;
		font-weight: 600;
		border-radius: 50rpx;
		height: 100rpx;
		line-height: 100rpx;
		box-shadow: 0 10rpx 20rpx rgba(24, 144, 255, 0.3);
		border: none;
		transition: transform 0.1s;
	}

	.login-btn:active {
		transform: scale(0.98);
		opacity: 0.9;
	}

	.footer-links {
		margin-top: 40rpx;
		text-align: center;
		display: flex;
		justify-content: center;
		gap: 30rpx;
	}

	.footer-link {
		font-size: 28rpx;
		color: #1890FF;
		font-weight: 600;
		display: flex;
		align-items: center;
		gap: 6rpx;
	}
</style>