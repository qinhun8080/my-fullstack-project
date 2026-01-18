<script>
	const API_BASE_URL = 'http://127.0.0.1:8081/api';

	export default {
		onLaunch: function() {
			console.log('App Launch');
			this.checkLoginStatus();
		},
		methods: {
			checkLoginStatus() {
				// 1. 先看本地有没有标记为已登录
				const hasLogin = uni.getStorageSync('hasLogin');

				if (!hasLogin) {
					// 本地没记录，直接去登录页
					this.redirectToLogin();
				} else {
					// 2. (可选但推荐) 调用后端接口确认 Session 是否过期
					// 因为 Session 可能会在服务器端过期(30分钟)，但前端还存着 hasLogin=true
					uni.request({
						url: API_BASE_URL + '/session-status',
						method: 'GET',
						withCredentials: true, // 必须携带 Cookie
						success: (res) => {
							// 后端返回: { loggedIn: true/false, userData: ... }
							if (res.data && res.data.loggedIn) {
								console.log('Session有效，保持登录状态');
								// 更新最新的用户信息
								uni.setStorageSync('userInfo', res.data.userData);
							} else {
								console.log('Session已过期，请重新登录');
								// 清除本地状态并跳转
								uni.removeStorageSync('hasLogin');
								uni.removeStorageSync('userInfo');
								this.redirectToLogin();
							}
						},
						fail: () => {
							// 网络错误时，为了安全也建议跳回登录，或者提示用户
							// 这里简单处理：不做强制跳转，允许用户离线看缓存界面(如果需要)
						}
					});
				}
			},
			redirectToLogin() {
				// 关闭所有页面，打开登录页
				uni.reLaunch({
					url: '/pages/login/login'
				});
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f7f8fa;
		padding: 20rpx;
		font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Helvetica,
			Segoe UI, Arial, Roboto, 'PingFang SC', 'miui', 'Hiragino Sans GB',
			'Microsoft Yahei', sans-serif;
		color: #333;
		box-sizing: border-box;
	}

	view,
	text,
	image,
	button {
		box-sizing: border-box;
	}

	.card {
		background-color: #ffffff;
		border-radius: 16rpx;
		padding: 30rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
		margin-bottom: 20rpx;
	}

	button::after {
		border: none;
	}
	
	button {
		padding: 0;
		margin: 0;
		background-color: transparent;
		line-height: 1.5;
	}
</style>