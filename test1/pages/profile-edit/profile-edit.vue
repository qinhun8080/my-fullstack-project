<template>
	<view class="container">
		<view class="form-card">
			
			<view class="form-item" @click="chooseAvatar">
				<text class="label">头像</text>
				<view class="item-right">
					<image class="avatar-preview" :src="displayAvatar" mode="aspectFill"></image>
					<uni-icons type="right" size="16" color="#999"></uni-icons>
				</view>
			</view>

			<view class="form-item">
				<text class="label">帐号</text>
				<view class="item-right">
					<text class="input-display">{{ form.username }}</text>
				</view>
			</view>
			
			<view class="form-item">
				<text class="label">昵称</text>
				<view class="item-right">
					<input class="input" v-model="form.nickname" placeholder-class="input-placeholder" placeholder="请输入昵称" />
				</view>
			</view>
			
			<view class="form-item">
				<text class="label">性别</text>
				<picker mode="selector" :range="genderOptions" :value="form.gender" @change="onGenderChange" style="flex: 1;">
					<view class="item-right">
						<text class="input" :class="{'input-placeholder': form.gender === 0}">
							{{ genderText }}
						</text>
						<uni-icons type="right" size="16" color="#999"></uni-icons>
					</view>
				</picker>
			</view>
			
			<view class="form-item">
				<text class="label">生日</text>
				<picker mode="date" :value="form.birthday" @change="onBirthdayChange" style="flex: 1;">
					<view class="item-right">
						<text class="input" :class="{'input-placeholder': !form.birthday}">
							{{ form.birthday || '请选择生日' }}
						</text>
						<uni-icons type="right" size="16" color="#999"></uni-icons>
					</view>
				</picker>
			</view>
			
			<view class="form-item">
				<text class="label">新密码</text>
				<view class="item-right">
					<input class="input" type="password" v-model="form.password" placeholder-class="input-placeholder" placeholder="不修改请留空" />
				</view>
			</view>
			
			<view class="form-item">
				<text class="label">确认密码</text>
				<view class="item-right">
					<input class="input" type="password" v-model="confirmPassword" placeholder-class="input-placeholder" placeholder="请再次输入新密码" />
				</view>
			</view>
			
			<view class="form-item">
				<text class="label">手机号</text>
				<view class="item-right">
					<input class="input" type="number" v-model="form.phone" placeholder-class="input-placeholder" placeholder="请输入手机号" />
				</view>
			</view>
		</view>
		
		<view class="button-group">
			<button class="btn btn-cancel" @click="onCancel">取消</button>
			<button class="btn btn-confirm" @click="onSubmit" :loading="isSubmitting">确认修改</button>
		</view>
	</view>
</template>

<script>
	// 确保这里的 IP 和你的后端一致 (真机调试不能用 localhost)
	const API_BASE_URL = 'http://127.0.0.1:8081'; 
	const DEFAULT_AVATAR = '/static/avatar.png'; // 默认头像

	export default {
		data() {
			return {
				// 对应后端的 User 实体
				form: {
					id: null,
					username: '', // 帐号只读
					nickname: '',
					gender: 0,    // 0:保密, 1:男, 2:女
					birthday: '',
					phone: '',
					avatarUrl: '', // 存相对路径 /images/xxx.jpg
					password: ''   // 可选，修改密码时才填
				},
				confirmPassword: '', // 前端辅助字段
				genderOptions: ['保密', '男', '女'], // 对应索引 0, 1, 2
				isSubmitting: false
			}
		},
		computed: {
			// 计算头像完整路径 (用于 <image src="...">)
			displayAvatar() {
				if (this.form.avatarUrl) {
					// 如果已经是完整 http 路径则直接用，否则拼接
					if (this.form.avatarUrl.startsWith('http')) return this.form.avatarUrl;
					return API_BASE_URL + this.form.avatarUrl;
				}
				return DEFAULT_AVATAR;
			},
			// 计算性别显示文本
			genderText() {
				// 简单的索引映射
				return this.genderOptions[this.form.gender] || '请选择性别';
			}
		},
		onLoad() {
			this.loadUserProfile();
		},
		methods: {
			// 1. 加载本地缓存的用户信息回显
			loadUserProfile() {
				const userInfo = uni.getStorageSync('userInfo');
				if (userInfo) {
					// 浅拷贝数据，避免直接修改缓存
					this.form.id = userInfo.id;
					this.form.username = userInfo.username;
					this.form.nickname = userInfo.nickname;
					this.form.gender = userInfo.gender || 0;
					this.form.birthday = userInfo.birthday;
					this.form.phone = userInfo.phone;
					this.form.avatarUrl = userInfo.avatarUrl;
					// 密码字段初始为空，不回显加密后的哈希值
					this.form.password = ''; 
				} else {
					uni.showToast({ title: '未登录', icon: 'none' });
					setTimeout(() => uni.reLaunch({ url: '/pages/login/login' }), 1500);
				}
			},

			// 2. 选择并上传头像
			chooseAvatar() {
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					success: (res) => {
						const tempFilePath = res.tempFilePaths[0];
						this.uploadAvatar(tempFilePath);
					}
				});
			},

			// 3. 执行文件上传
			uploadAvatar(filePath) {
				uni.showLoading({ title: '上传中...' });
				uni.uploadFile({
					url: API_BASE_URL + '/api/upload', // 你的后端上传接口
					filePath: filePath,
					name: 'file',
					success: (uploadRes) => {
						// uploadFile 返回的是 String，需要手动 parse
						const data = JSON.parse(uploadRes.data);
						if (data.success) {
							// 后端返回相对路径 "/images/uuid.jpg"
							this.form.avatarUrl = data.filePath;
							uni.showToast({ title: '上传成功', icon: 'none' });
						} else {
							uni.showToast({ title: '上传失败', icon: 'none' });
						}
					},
					fail: () => {
						uni.showToast({ title: '网络错误', icon: 'none' });
					},
					complete: () => {
						uni.hideLoading();
					}
				});
			},

			// 4. 性别选择器回调
			onGenderChange(e) {
				this.form.gender = parseInt(e.detail.value);
			},

			// 5. 生日选择器回调
			onBirthdayChange(e) {
				this.form.birthday = e.detail.value;
			},

			// 6. 提交保存
			onSubmit() {
				// --- 简单校验 ---
				if (this.form.password && this.form.password !== this.confirmPassword) {
					uni.showToast({ title: '两次输入的密码不一致', icon: 'none' });
					return;
				}
				
				this.isSubmitting = true;

				// 构建提交的数据 (如果不修改密码，发送 null 或空串给后端)
				// 注意：这里我们不发送 username，因为一般不允许改帐号
				const postData = {
					...this.form
				};
				// 如果密码为空，后端逻辑会忽略它
				if (!postData.password) {
					postData.password = null;
				}

				uni.request({
					url: API_BASE_URL + '/api/user/update',
					method: 'POST',
					data: postData,
					success: (res) => {
						if (res.data.success) {
							uni.showToast({ title: '保存成功' });
							// 更新本地缓存
							uni.setStorageSync('userInfo', res.data.data);
							
							// 1.5秒后返回上一页
							setTimeout(() => {
								this.onCancel();
							}, 1500);
						} else {
							uni.showToast({ title: res.data.message || '保存失败', icon: 'none' });
						}
					},
					fail: () => {
						uni.showToast({ title: '请求失败，请检查网络', icon: 'none' });
					},
					complete: () => {
						this.isSubmitting = false;
					}
				});
			},

			onCancel() {
				uni.navigateBack({ delta: 1 });
			}
		}
	}
</script>

<style lang="scss" scoped>
	/* 保持你原有的样式不变 */
	.form-card {
		padding: 10rpx 30rpx;
		background-color: #ffffff;
		border-radius: 16rpx;
		margin-top: 20rpx;
	}

	.form-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 30rpx 0;
		border-bottom: 1rpx solid #f5f5f5;
		
		&:last-child {
			border-bottom: none;
		}
	}
	
	.label {
		font-size: 30rpx;
		color: #333;
		width: 180rpx;
		flex-shrink: 0;
	}
	
	.item-right {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: flex-end;
		/* 确保点击区域铺满 */
		min-height: 40rpx; 
	}
	
	.input {
		font-size: 30rpx;
		text-align: right;
		color: #333;
		width: 100%; /* 让 input 在容器内占满 */
	}
	
	.input-placeholder {
		color: #999;
	}
	
	.input-display {
		font-size: 30rpx;
		color: #999;
	}
	
	.avatar-preview {
		width: 100rpx;
		height: 100rpx;
		border-radius: 50%;
		background-color: #eee;
		margin-right: 10rpx;
		border: 1rpx solid #f0f0f0;
	}
	
	.button-group {
		display: flex;
		justify-content: space-between;
		margin-top: 60rpx;
		padding: 0 10rpx;
	}
	
	.btn {
		width: calc(50% - 20rpx);
		height: 88rpx;
		line-height: 88rpx;
		border-radius: 44rpx;
		font-size: 30rpx;
		font-weight: bold;
	}
	
	.btn-cancel {
		background-color: #f0f0f0;
		color: #555;
	}
	
	.btn-confirm {
		background-color: #007AFF;
		color: #ffffff;
	}
	
	/* 按钮点击态 */
	.btn-confirm:active {
		opacity: 0.8;
	}
</style>