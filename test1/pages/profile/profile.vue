<template>
	<view class="container">
		<view class="header-card">
			<view class="avatar-section">
				<view class="avatar-container">
					<image class="avatar" :src="fullAvatarUrl" mode="aspectFill"></image>
					<view class="avatar-border"></view>
				</view>
				<view class="user-info">
					<text class="nickname">{{ userInfo.nickname || userInfo.username }}</text>
					<text class="role-tag">用户ID: {{ userInfo.id }}</text>
				</view>
			</view>
			<view class="edit-btn" @click="goToEdit">
				<text>编辑资料</text>
				<uni-icons type="forward" size="16" color="#999"></uni-icons>
			</view>
		</view>

		<view class="info-list card">
			<view class="list-header">
				<text class="list-title">个人信息</text>
			</view>
			<view class="list-item">
				<view class="item-content">
					<view class="label-section">
						<uni-icons type="phone" size="18" color="#5a7dff" class="item-icon"></uni-icons>
						<text class="label">手机号</text>
					</view>
					<text class="value">{{ userInfo.phone || '未填写' }}</text>
				</view>
			</view>
			<view class="divider"></view>
			<view class="list-item">
				<view class="item-content">
					<view class="label-section">
						<uni-icons type="person" size="18" color="#5a7dff" class="item-icon"></uni-icons>
						<text class="label">性别</text>
					</view>
					<text class="value">{{ genderText }}</text>
				</view>
			</view>
			<view class="divider"></view>
			<view class="list-item">
				<view class="item-content">
					<view class="label-section">
						<uni-icons type="calendar" size="18" color="#5a7dff" class="item-icon"></uni-icons>
						<text class="label">出生日期</text>
					</view>
					<text class="value">{{ userInfo.birthday || '未填写' }}</text>
				</view>
			</view>
		</view>

		<view class="action-section">
			<button class="logout-btn" @click="handleLogout">
				<text class="btn-text">退出登录</text>
			</button>
		</view>
	</view>
</template>

<script>
	// 替换为你电脑的 IP
	const API_BASE_URL = 'http://127.0.0.1:8081'; 
	const DEFAULT_AVATAR = '/static/default-avatar.png'; // 请确保 static 目录下有一张默认图片

	export default {
		data() {
			return {
				userInfo: {}
			}
		},
		computed: {
			// 计算完整的头像 URL
			fullAvatarUrl() {
				if (this.userInfo.avatarUrl) {
					// 兼容处理：如果数据库存的是完整http链接就不拼接，否则拼接
					if (this.userInfo.avatarUrl.startsWith('http')) return this.userInfo.avatarUrl;
					// 数据库存的是 /images/xxx.jpg，我们需要拼接 http://ip:8081
					return API_BASE_URL + this.userInfo.avatarUrl;
				}
				return DEFAULT_AVATAR;
			},
			// 性别转换
			genderText() {
				const g = this.userInfo.gender;
				if (g === 1) return '男';
				if (g === 2) return '女';
				return '保密';
			}
		},
		onShow() {
			// 每次进入页面，都去后端拉取最新数据
			this.fetchLatestUserInfo();
		},
		methods: {
			fetchLatestUserInfo() {
				// 1. 获取本地存储的用户ID 和 Token [修改点]
				const localUser = uni.getStorageSync('userInfo');
				const token = uni.getStorageSync('token'); // 获取 Token

				if (!localUser || !localUser.id) {
					uni.reLaunch({ url: '/pages/login/login' });
					return;
				}

				// 2. 调用后端 /api/user/info 接口
				uni.request({
					url: API_BASE_URL + '/api/user/info',
					method: 'GET',
					data: { id: localUser.id },
					// [修改点] 添加 Header 携带 Token
					header: {
						'Authorization': token ? 'Bearer ' + token : ''
					},
					success: (res) => {
						if (res.data.success) {
							this.userInfo = res.data.data;
							// 更新本地缓存，保持同步
							uni.setStorageSync('userInfo', this.userInfo);
						}
					},
					fail: (err) => {
						console.error('获取用户信息失败', err);
					}
				});
			},
			goToEdit() {
				uni.navigateTo({
					url: '/pages/profile-edit/profile-edit'
				});
			},
			handleLogout() {
				uni.showModal({
					title: '提示',
					content: '确定要退出登录吗？',
					success: (res) => {
						if (res.confirm) {
							uni.removeStorageSync('userInfo');
							uni.removeStorageSync('hasLogin');
							uni.removeStorageSync('token'); // [修改点] 清除 Token
							uni.reLaunch({ url: '/pages/login/login' });
						}
					}
				});
			}
		}
	}
</script>

<style lang="scss">
	page { 
		background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%); 
		padding: 20rpx; 
		box-sizing: border-box; 
		min-height: 100vh;
	}
	
	.container {
		display: flex;
		flex-direction: column;
		min-height: 100vh;
	}
	
	.card { 
		background-color: #fff; 
		border-radius: 24rpx; 
		padding: 0;
		margin-bottom: 30rpx;
		box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.05);
		overflow: hidden;
		transition: transform 0.3s ease, box-shadow 0.3s ease;
	}
	
	.card:active {
		transform: translateY(4rpx);
		box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.05);
	}
	
	/* 顶部用户信息卡片 */
	.header-card {
		@extend .card;
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 40rpx 30rpx;
		background: linear-gradient(135deg, #6a85b6 0%, #bac8e0 100%);
		color: white;
		position: relative;
		overflow: visible;
	}
	
	.avatar-section {
		display: flex;
		align-items: center;
		flex: 1;
	}
	
	.avatar-container {
		position: relative;
		margin-right: 30rpx;
	}
	
	.avatar {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
		background: linear-gradient(135deg, #fff 0%, #f0f0f0 100%);
		margin-right: 30rpx;
		z-index: 2;
		position: relative;
		border: 4rpx solid rgba(255, 255, 255, 0.3);
		box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.1);
	}
	
	.avatar-border {
		position: absolute;
		top: -6rpx;
		left: -6rpx;
		width: 132rpx;
		height: 132rpx;
		border-radius: 50%;
		background: linear-gradient(135deg, rgba(255,255,255,0.6) 0%, rgba(255,255,255,0.2) 100%);
		z-index: 1;
	}
	
	.user-info {
		display: flex;
		flex-direction: column;
		flex: 1;
	}
	
	.nickname { 
		font-size: 36rpx; 
		font-weight: bold; 
		margin-bottom: 10rpx; 
		text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
	}
	
	.role-tag { 
		font-size: 24rpx; 
		opacity: 0.9;
		background: rgba(255, 255, 255, 0.2);
		padding: 6rpx 16rpx;
		border-radius: 20rpx;
		align-self: flex-start;
	}
	
	.edit-btn { 
		display: flex; 
		align-items: center; 
		font-size: 26rpx; 
		background: rgba(255, 255, 255, 0.2);
		padding: 12rpx 20rpx;
		border-radius: 30rpx;
		backdrop-filter: blur(10px);
		transition: all 0.3s ease;
	}
	
	.edit-btn:active {
		background: rgba(255, 255, 255, 0.3);
		transform: scale(0.95);
	}
	
	/* 信息列表 */
	.info-list {
		padding: 0;
	}
	
	.list-header {
		padding: 30rpx 30rpx 20rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}
	
	.list-title {
		font-size: 32rpx;
		font-weight: 600;
		color: #333;
	}
	
	.list-item {
		padding: 0 30rpx;
	}
	
	.item-content {
		display: flex;
		justify-content: space-between;
		align-items: center;
		height: 100rpx;
	}
	
	.label-section {
		display: flex;
		align-items: center;
	}
	
	.item-icon {
		margin-right: 20rpx;
	}
	
	.label { 
		font-size: 30rpx; 
		color: #333; 
		font-weight: 500;
	}
	
	.value { 
		font-size: 30rpx; 
		color: #666; 
	}
	
	.divider { 
		height: 1rpx; 
		background: linear-gradient(to right, #f0f0f0 0%, #e0e0e0 50%, #f0f0f0 100%);
		margin: 0 30rpx;
	}
	
	/* 操作区域 */
	.action-section {
		margin-top: auto;
		padding: 40rpx 0;
	}
	
	.logout-btn {
		background: linear-gradient(135deg, #ff6b6b 0%, #ff4757 100%);
		color: white;
		font-size: 32rpx;
		border-radius: 16rpx;
		box-shadow: 0 6rpx 20rpx rgba(255, 107, 107, 0.3);
		border: none;
		height: 90rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		transition: all 0.3s ease;
		position: relative;
		overflow: hidden;
	}
	
	.logout-btn::after {
		content: '';
		position: absolute;
		top: 0;
		left: -100%;
		width: 100%;
		height: 100%;
		background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
		transition: left 0.5s;
	}
	
	.logout-btn:active::after {
		left: 100%;
	}
	
	.logout-btn:active {
		transform: translateY(4rpx);
		box-shadow: 0 2rpx 10rpx rgba(255, 107, 107, 0.3);
	}
	
	.btn-text {
		font-weight: 500;
		letter-spacing: 2rpx;
	}
</style>