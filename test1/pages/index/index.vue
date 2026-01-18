<template>
	<view class="container">
		
		<view class="top-bar">
			<view class="location">
				<text>定位</text>
				<uni-icons type="map-pin-ellipse" size="22" color="#333" style="margin-left: 4rpx;"></uni-icons>
			</view>
			<view class="search-box">
				<uni-icons type="search" size="18" color="#999"></uni-icons>
				<text class="search-placeholder">搜索</text>
			</view>
		</view>

		<view class="card info-card">
			<view class="left-info">
				<view class="info-line">设备状态 ({{ deviceStatus }})</view>
				<view class="info-line">当前温度</view>
				<view class="data-value temp-value">
					{{ temp }} <text v-if="temp !== '--'" class="unit">°C</text>
				</view>
			</view>
			
			<view class="right-data">
				<view class="info-line">当前湿度</view>
				<uni-icons type="droplet" size="34" color="#a0cfff"></uni-icons>
				<view class="data-value humi-value">
					{{ humi }} <text v-if="humi !== '--'" class="unit">%RH</text>
				</view>
			</view>
		</view>

		<view class="grid-container">
			<view class="grid-item card">
				<text class="grid-text">查看位置</text>
			</view>
			
			<view class="grid-item card">
				<text class="grid-text">超声波测距</text>
				<view class="grid-value" :class="{ 'danger-text': isDanger }">
					{{ distance }} <text v-if="distance !== '--'" style="font-size: 24rpx; color: #666;">cm</text>
				</view>
			</view>
			
			<view class="grid-item card" @click="openCallModal">
				<text class="grid-text">呼叫</text>
				<uni-icons type="phone-filled" size="24" color="#3498db" style="margin-top: 10rpx;"></uni-icons>
			</view>
			
			<view class="grid-item card warning-grid" @click="openHistoryModal">
				<text class="grid-text">历史预警</text>
				<view v-if="historyAlerts.length > 0" class="big-alert-num">
					{{ historyAlerts.length }}
				</view>
				<text v-else style="font-size: 24rpx; color: #ccc; margin-top: 10rpx;">无记录</text>
			</view>
		</view>
		
		<view class="modal-mask" v-if="showHistoryModal || showCallModal" @click="closeAllModals"></view>
		
		<view class="modal-content" v-if="showHistoryModal">
			<view class="modal-header">
				<text class="modal-title">危险预警记录</text>
				<uni-icons type="closeempty" size="24" color="#999" @click="closeAllModals"></uni-icons>
			</view>
			<scroll-view scroll-y="true" class="modal-body">
				<view v-if="historyAlerts.length === 0" class="empty-tip">暂无危险预警</view>
				<view v-for="(item, index) in historyAlerts" :key="index" class="alert-item">
					<uni-icons type="info-filled" color="#ff4d4f" size="18"></uni-icons>
					<text class="alert-text">{{ item }}</text>
				</view>
			</scroll-view>
			<view class="modal-footer">
				<button class="close-btn" @click="closeAllModals">关闭</button>
			</view>
		</view>
		
		<view class="modal-content call-modal-box" v-if="showCallModal">
			<view class="call-avatar-area">
				<view class="avatar-circle">
					<uni-icons type="person-filled" size="50" color="#fff"></uni-icons>
				</view>
				<text class="call-name">管理员中心</text>
				<text class="call-status">正在呼叫...</text>
			</view>
			<view class="call-actions">
				<view class="hangup-btn" @click="closeAllModals">
					<uni-icons type="phone-filled" size="30" color="#fff"></uni-icons>
				</view>
				<text style="margin-top: 10rpx; color: #666; font-size: 24rpx;">挂断</text>
			</view>
		</view>
		
	</view>
</template>

<script>
	const API_BASE_URL = 'http://127.0.0.1:8081/api';

	export default {
		data() {
			return {
				temp: '--', 
				humi: '--',
				distance: '--', 
				deviceStatus: '查询中...',
				historyAlerts: [], 
				showHistoryModal: false,
				showCallModal: false,
				dataTimer: null
			}
		},
		computed: {
			isDanger() {
				const val = parseFloat(this.distance);
				return !isNaN(val) && val < 30;
			}
		},
		onShow() {
			this.fetchBackendData();
			this.dataTimer = setInterval(() => {
				this.fetchBackendData(); 
			}, 5000); 
		},
		onHide() {
			if (this.dataTimer) {
				clearInterval(this.dataTimer);
				this.dataTimer = null;
			}
		},
		methods: {
			openHistoryModal() {
				this.showHistoryModal = true;
				this.showCallModal = false;
			},
			openCallModal() {
				this.showCallModal = true;
				this.showHistoryModal = false;
			},
			closeAllModals() {
				this.showHistoryModal = false;
				this.showCallModal = false;
			},
			fetchBackendData() {
				uni.request({
					url: API_BASE_URL + '/device-data', 
					method: 'GET',
					success: (res) => {
						if (res.data) {
							this.deviceStatus = res.data.deviceStatus || 'N/A';
							this.temp = res.data.temp;
							this.humi = res.data.humi;
							this.distance = res.data.distance; 
							if (res.data.historyAlerts) {
								this.historyAlerts = res.data.historyAlerts;
							}
						}
					},
					fail: (err) => {
						console.error("API请求失败:", err);
					}
				});
			}
		}
	}
</script>

<style lang="scss">
	.container { min-height: 100vh; }
	.top-bar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 25rpx; padding: 0 10rpx; }
	.location { display: flex; align-items: center; font-size: 34rpx; font-weight: bold; }
	.search-box { display: flex; align-items: center; background-color: #ffffff; border-radius: 30rpx; padding: 12rpx 24rpx; flex: 1; margin-left: 20rpx; box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05); }
	.search-placeholder { margin-left: 10rpx; color: #999; font-size: 30rpx; }
	
	/* 卡片样式 */
	.info-card { display: flex; justify-content: space-between; align-items: center; padding-top: 20rpx; padding-bottom: 20rpx; }
	.left-info .info-line, .right-data .info-line { font-size: 34rpx; color: #666; line-height: 1.6; }
	
	/* 数值样式 */
	.data-value { font-size: 68rpx; font-weight: bold; margin-top: 18rpx; .unit { font-size: 36rpx; font-weight: normal; margin-left: 8rpx; } }
	.temp-value { color: #f39c12; }
	.humi-value { color: #3498db; }
	
	/* 右侧数据列布局 */
	.right-data { display: flex; flex-direction: column; align-items: center; font-size: 26rpx; color: #666; uni-icons { margin: 18rpx 0; } }

	/* 九宫格样式 */
	.grid-container { display: flex; flex-wrap: wrap; justify-content: space-between; }
	.grid-item { 
		width: calc(50% - 10rpx); 
		display: flex; 
		flex-direction: column; 
		align-items: center; 
		justify-content: center; 
		min-height: 180rpx; 
		.grid-text { font-size: 36rpx; font-weight: bold; color: #333; }
	}
	
	/* [新增] 九宫格里的距离数值样式 */
	.grid-value {
		font-size: 40rpx;
		font-weight: bold;
		color: #333;
		margin-top: 10rpx;
	}
	.danger-text { color: #ff4d4f !important; }

	.warning-grid { border-left: 10rpx solid #ff4d4f; }
	.big-alert-num { font-size: 60rpx; font-weight: bold; color: #ff4d4f; margin-top: 10rpx; line-height: 1; }

	/* 弹窗通用样式 */
	.modal-mask { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); z-index: 999; }
	.modal-content { position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 80%; background-color: #fff; border-radius: 20rpx; z-index: 1000; padding: 30rpx; box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.2); }
	
	.modal-header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 20rpx; border-bottom: 1rpx solid #eee; margin-bottom: 20rpx; }
	.modal-title { font-size: 34rpx; font-weight: bold; color: #333; }
	.modal-body { max-height: 500rpx; min-height: 200rpx; }
	.alert-item { display: flex; align-items: flex-start; padding: 15rpx 0; border-bottom: 1rpx solid #f9f9f9; }
	.alert-text { font-size: 28rpx; color: #555; margin-left: 15rpx; line-height: 1.4; }
	.empty-tip { text-align: center; color: #999; margin-top: 80rpx; }
	.modal-footer { margin-top: 30rpx; }
	.close-btn { background-color: #f5f5f5; color: #666; font-size: 30rpx; border-radius: 40rpx; }
	.close-btn::after { border: none; }

	/* 呼叫弹窗专属 */
	.call-modal-box { display: flex; flex-direction: column; align-items: center; padding: 50rpx 30rpx; }
	.call-avatar-area { display: flex; flex-direction: column; align-items: center; margin-bottom: 60rpx; }
	.avatar-circle { width: 120rpx; height: 120rpx; background-color: #ddd; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-bottom: 20rpx; }
	.call-name { font-size: 36rpx; font-weight: bold; color: #333; margin-bottom: 10rpx; }
	.call-status { font-size: 28rpx; color: #666; }
	.call-actions { display: flex; flex-direction: column; align-items: center; }
	.hangup-btn { width: 120rpx; height: 120rpx; background-color: #ff4d4f; border-radius: 50%; display: flex; align-items: center; justify-content: center; box-shadow: 0 4rpx 12rpx rgba(255, 77, 79, 0.4); }
	.hangup-btn:active { background-color: #d9363e; }
</style>