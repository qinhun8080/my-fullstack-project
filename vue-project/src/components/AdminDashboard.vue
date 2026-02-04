<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <img src="../assets/Gemini_Generated_Image_n5w7aan5w7aan5w7.png" class="sidebar-logo" alt="Logo">
        <h1 class="sidebar-title">智行物联网智能导盲<br />监测管理系统</h1>
      </div>
      <nav class="sidebar-nav">
        <ul>
          <li>
            <a href="#" class="nav-item" :class="{ active: currentView === 'home' }" @click.prevent="setView('home')">
              首页
            </a>
          </li>
          <li>
            <a href="#" class="nav-item" :class="{ active: currentView === 'device' }"
              @click.prevent="setView('device')">
              设备管理
            </a>
          </li>
          <li>
            <a href="#" class="nav-item" :class="{ active: currentView === 'userManagement' }"
              @click.prevent="setView('userManagement')">
              用户管理
            </a>
          </li>
          <li>
            <a href="#" class="nav-item" :class="{ active: currentView === 'profile' }"
              @click.prevent="setView('profile')">
              个人中心
            </a>
          </li>
        </ul>
      </nav>
    </aside>

    <main class="main-content">
      <header class="main-header">
        <div class="welcome-message">
          <span>{{ userInfo.nickname || username }}, 欢迎回来!</span>
        </div>
        <button class="logout-button" @click="handleLogout">退出登录</button>
      </header>

      <div class="content-body">
        <div class="cards-container" v-if="currentView === 'home'">
          <div class="info-card">
            <div class="user-info-section">
              <img class="avatar" :src="userInfo.avatar || 'https://via.placeholder.com/80'" alt="avatar" />
              <div class="user-details">
                <p class="nickname">{{ userInfo.nickname }}</p>
                <p class="last-login">
                  上次登录时间: {{ userInfo.lastLoginTime }}
                </p>
                <p class="admin-id">管理员编号: {{ userInfo.adminId }}</p>
              </div>
            </div>
            <button class="edit-button" @click="showEditUserModal = true">
              编辑
            </button>
          </div>
          <div class="info-card">
            <div class="product-info-section">
              <h3 class="product-title">
                <i class="fas fa-cogs"></i> 产品版本信息
              </h3>
              <p class="version-item">
                <i class="fas fa-microchip"></i>
                <strong>硬件:</strong>
                <span class="version-number">{{ productInfo.hardwareVersion }}</span>
              </p>
              <p class="version-item">
                <i class="fas fa-desktop"></i>
                <strong>客户端:</strong>
                <span class="version-number">{{ productInfo.clientVersion }}</span>
              </p>
            </div>
            <button class="edit-button" @click="showProductModal = true">
              <i class="fas fa-edit"></i> 编辑
            </button>
          </div>
        </div>

        <div v-if="currentView === 'device'">
          <div class="device-grid-container">
            <div class="device-card" v-for="device in deviceList" :key="device.id" :class="device.status"
              @click="selectDevice(device)">
              <div class="device-card-header">
                <i class="fas fa-server device-icon"></i>
                <h3 class="device-name">{{ device.name }}</h3>
              </div>
              <div class="device-card-body">
                <div class="device-status">
                  <span class="status-indicator" :class="device.status"></span>
                  <span class="status-text">
                    {{ device.status === 'online' ? '设备在线' : '设备离线' }}
                  </span>
                </div>
                <p class="device-location">
                  <i class="fas fa-map-marker-alt"></i>
                  {{ device.location }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <div v-if="currentView === 'userManagement'" class="user-management-container">
          <div class="user-table-header">
            <h2><i class="fas fa-users-cog"></i> 用户管理</h2>
            <button class="add-user-btn" @click="openAddUserModal">
              <i class="fas fa-plus"></i> 新增用户
            </button>
          </div>

          <table class="user-management-table">
            <thead>
              <tr>
                <th>账号</th>
                <th>昵称</th>
                <th>性别</th>
                <th>电话号码</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="userList.length === 0">
                <td colspan="5">暂无用户数据</td>
              </tr>
              <tr v-for="user in userList" :key="user.id">
                <td>{{ user.username }}</td>
                <td>{{ user.nickname }}</td>
                <td>{{ formatGender(user.gender) }}</td>
                <td>{{ user.phone }}</td>
                <td class="action-buttons-cell">
                  <button class="action-btn-sm btn-detail" @click="openUserEditModal(user)">
                    <i class="fas fa-eye"></i> 详情
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="profile-container" v-if="currentView === 'profile'">
          <div class="profile-card">
            <div class="profile-grid">
              <aside class="profile-summary">
                <div class="summary-avatar-wrap">
                  <img :src="userInfo.avatar" alt="avatar" class="profile-avatar" />
                </div>
                <div class="summary-info">
                  <h2 class="profile-nickname">{{ userInfo.nickname }}</h2>
                  <p class="profile-admin-id">管理员编号: {{ userInfo.adminId }}</p>
                  <p class="profile-quick"><span>最后登录：</span><strong>{{ userInfo.lastLoginTime }}</strong></p>
                  <p class="profile-quick"><span>账号：</span><strong>{{ userInfo.username }}</strong></p>
                </div>
              </aside>

              <section class="profile-body">
                <h3 class="profile-section-title">
                  <i class="fas fa-id-badge"></i> 账户详情
                </h3>
                <div class="profile-details-grid">
                  <div class="detail-item">
                    <span class="detail-label">登录账号:</span>
                    <span class="detail-value">{{ userInfo.username }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">手机号码:</span>
                    <span class="detail-value">{{ userInfo.phone || '未设置' }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">用户性别:</span>
                    <span class="detail-value">{{ formatGender(userInfo.gender) }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">上次登录:</span>
                    <span class="detail-value">{{ userInfo.lastLoginTime }}</span>
                  </div>
                </div>
              </section>
            </div>
          </div>
        </div>

        <transition name="modal-fade">
          <div class="modal-overlay" v-if="showEditUserModal" @click.self="showEditUserModal = false">
            <div class="modal-content">
              <div class="modal-header">
                <h2>编辑个人信息</h2>
                <button class="close-button" @click="showEditUserModal = false">
                  <i class="fas fa-times">×</i>
                </button>
              </div>

              <div class="modal-body">
                <div class="avatar-upload-section">
                  <div class="avatar-wrapper">
                    <img :src="editForm.avatar || 'https://via.placeholder.com/80'" alt="avatar"
                      class="preview-avatar" />
                    <div class="avatar-overlay" @click="triggerFileUpload">
                      <i class="fas fa-camera"></i>
                      <span>更换头像</span>
                    </div>
                  </div>
                  <input type="file" ref="fileInput" style="display: none" accept="image/*"
                    @change="handleAvatarChange" />
                </div>
                <div class="form-grid">
                  <div class="form-group">
                    <label><i class="fas fa-user"></i> 昵称</label>
                    <input type="text" v-model="editForm.nickname" placeholder="请输入昵称" class="styled-input" />
                  </div>
                  <div class="form-group">
                    <label><i class="fas fa-venus-mars"></i> 性别</label>
                    <select v-model="editForm.gender" class="styled-input">
                      <option value="">请选择性别</option>
                      <option value="male">男</option>
                      <option value="female">女</option>
                      <option value="other">其他</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label><i class="fas fa-mobile-alt"></i> 手机号</label>
                    <input type="tel" v-model="editForm.phone" placeholder="请输入手机号" class="styled-input" />
                  </div>
                  <div class="form-group password-group">
                    <label><i class="fas fa-lock"></i> 新密码</label>
                    <div class="password-input-wrapper">
                      <input :type="showPassword ? 'text' : 'password'" v-model="editForm.password" placeholder="请输入新密码"
                        class="styled-input" />
                      <button type="button" class="password-toggle" @click="showPassword = !showPassword">
                        <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                      </button>
                    </div>
                  </div>
                  <div class="form-group password-group">
                    <label><i class="fas fa-lock"></i> 确认密码</label>
                    <div class="password-input-wrapper">
                      <input :type="showConfirmPassword ? 'text' : 'password'" v-model="editForm.confirmPassword"
                        placeholder="请再次输入密码" class="styled-input" :class="{ error: passwordMismatch }" />
                      <button type="button" class="password-toggle" @click="showConfirmPassword = !showConfirmPassword">
                        <i :class="showConfirmPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                      </button>
                    </div>
                    <span class="error-message" v-if="passwordMismatch">
                      两次输入的密码不一致
                    </span>
                  </div>
                </div>
              </div>

              <div class="modal-footer">
                <button class="cancel-btn" @click="showEditUserModal = false">
                  <i class="fas fa-times"></i> 取消
                </button>
                <button class="submit-btn" @click="handleSubmit">
                  <i class="fas fa-check"></i> 确定
                </button>
              </div>
            </div>
          </div>
        </transition>

        <transition name="modal-fade">
          <div class="modal-overlay" v-if="showProductModal" @click.self="showProductModal = false">
            <div class="modal-content product-modal">
              <div class="modal-header">
                <h2><i class="fas fa-cogs"></i> 编辑产品版本</h2>
                <button class="close-button" @click="showProductModal = false">
                  <i class="fas fa-times">×</i>
                </button>
              </div>
              <div class="modal-body">
                <div class="version-edit-form">
                  <div class="form-group">
                    <label><i class="fas fa-microchip"></i> 硬件版本</label>
                    <div class="version-input-wrapper">
                      <input type="text" v-model="productForm.hardwareVersion" class="styled-input version-input"
                        :class="{ error: showHardwareHint }" placeholder="例如: V2.5.1"
                        @input="validateVersionFormat($event, 'hardware')" />
                      <span class="version-format-hint" v-if="showHardwareHint">格式：V主版本.次版本.修订号（如：V2.5.1）</span>
                    </div>
                  </div>
                  <div class="form-group">
                    <label><i class="fas fa-desktop"></i> 客户端版本</label>
                    <div class="version-input-wrapper">
                      <input type="text" v-model="productForm.clientVersion" class="styled-input version-input"
                        :class="{ error: showClientHint }" placeholder="例如: V1.3.2"
                        @input="validateVersionFormat($event, 'client')" />
                      <span class="version-format-hint" v-if="showClientHint">格式：V主版本.次版本.修订号（如：V1.3.2）</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="modal-footer">
                <button class="cancel-btn" @click="showProductModal = false"><i class="fas fa-times"></i> 取消</button>
                <button class="submit-btn" @click="handleProductSubmit" :disabled="!isProductFormValid"><i
                    class="fas fa-check"></i> 确定</button>
              </div>
            </div>
          </div>
        </transition>

        <transition name="modal-fade">
          <div class="modal-overlay" v-if="showDeviceDetailModal && selectedDevice"
            @click.self="closeDeviceDetailModal">
            <div class="modal-content device-detail-modal-content">
              <div class="modal-header">
                <div class="modal-title-wrapper">
                  <h2>{{ selectedDevice.name }}</h2>
                  <span class="status-tag" :class="selectedDevice.status">{{ selectedDevice.status === 'online' ? '在线' :
                    '离线' }}</span>
                </div>
                <button class="close-button" @click="closeDeviceDetailModal">×<i class="fas fa-times"></i></button>
              </div>
              <div class="modal-body">
                <div class="device-detail-card">
                  <div class="detail-item">
                    <span class="detail-label">上次离线时间:</span>
                    <span class="detail-value">{{ selectedDevice.lastOfflineTime }}</span>
                  </div>

                  <div class="detail-item">
                    <span class="detail-label">监测状态:</span>
                    <span class="detail-value" :style="{ color: getMonitorStatus(selectedDevice.distance).color }">
                      {{ getMonitorStatus(selectedDevice.distance).text }}
                    </span>
                  </div>

                  <div class="detail-item">
                    <span class="detail-label">超声波测距:</span>
                    <span class="detail-value">
                      {{ selectedDevice.distance && selectedDevice.distance !== 'N/A' ? selectedDevice.distance + ' cm'
                        : 'N/A' }}
                    </span>
                  </div>

                  <div class="detail-item">
                    <span class="detail-label">温度:</span>
                    <span class="detail-value">{{ selectedDevice.temperature }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">湿度:</span>
                    <span class="detail-value">{{ selectedDevice.humidity }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">位置:</span>
                    <span class="detail-value">{{ selectedDevice.location }}</span>
                  </div>
                </div>
              </div>
              <div class="modal-footer">
                <button class="action-btn btn-call" @click="handleCallDevice(selectedDevice)"><i
                    class="fas fa-bullhorn"></i> 呼叫</button>
                <button class="action-btn btn-modify" @click="handleModifyDevice(selectedDevice)"><i
                    class="fas fa-edit"></i> 修改</button>
                <button class="action-btn btn-delete" @click="handleDeleteDevice(selectedDevice)"><i
                    class="fas fa-trash-alt"></i> 删除</button>
              </div>
            </div>
          </div>
        </transition>

        <transition name="modal-fade">
          <div class="modal-overlay" v-if="showUserEditModal && selectedUser" @click.self="closeUserEditModal">
            <div class="modal-content">
              <div class="modal-header">
                <div class="modal-title-wrapper">

                  <h2>管理用户: {{ selectedUser.username }}</h2>
                </div>
                <button class="close-button" @click="closeUserEditModal"><i class="fas fa-times">×</i></button>
              </div>
              <div class="modal-body">
                <div class="form-grid">
                  <div class="form-group"><label><i class="fas fa-id-card"></i> 账号</label><input type="text"
                      v-model="userEditForm.username" class="styled-input" disabled /></div>
                  <div class="form-group"><label><i class="fas fa-user"></i> 昵称</label><input type="text"
                      v-model="userEditForm.nickname" class="styled-input" /></div>
                  <div class="form-group"><label><i class="fas fa-venus-mars"></i> 性别</label><select
                      v-model="userEditForm.gender" class="styled-input">
                      <option value="male">男</option>
                      <option value="female">女</option>
                      <option value="other">其他</option>
                    </select></div>
                  <div class="form-group"><label><i class="fas fa-mobile-alt"></i> 手机号</label><input type="tel"
                      v-model="userEditForm.phone" class="styled-input" /></div>
                  <div class="form-group password-group"><label><i class="fas fa-lock"></i> 重置密码 (选填)</label><input
                      type="password" v-model="userEditForm.password" placeholder="留空则不修改密码" class="styled-input" />
                  </div>
                </div>
              </div>
              <div class="modal-footer">
                <button class="action-btn btn-modify" @click="handleUserEditSubmit" :disabled="!isUserEditFormValid"><i
                    class="fas fa-edit"></i> 修改</button>
                <button class="action-btn btn-delete" @click="handleUserDeleteFromModal(selectedUser)"><i
                    class="fas fa-trash-alt"></i> 删除</button>
              </div>
            </div>
          </div>
        </transition>

        <transition name="modal-fade">
          <div class="modal-overlay" v-if="showAddUserModal" @click.self="showAddUserModal = false">
            <div class="modal-content">
              <div class="modal-header">
                <h2><i class="fas fa-user-plus"></i> 新增用户</h2><button class="close-button"
                  @click="showAddUserModal = false"><i class="fas fa-times">×</i></button>
              </div>
              <div class="modal-body">
                <div class="form-grid">
                  <div class="form-group"><label><i class="fas fa-id-card"></i> 账号</label><input type="text"
                      v-model="addUserForm.username" class="styled-input" /></div>
                  <div class="form-group"><label><i class="fas fa-user"></i> 昵称</label><input type="text"
                      v-model="addUserForm.nickname" class="styled-input" /></div>
                  <div class="form-group password-group"><label><i class="fas fa-lock"></i> 密码</label><input
                      type="password" v-model="addUserForm.password" class="styled-input" /></div>
                  <div class="form-group password-group"><label><i class="fas fa-lock"></i> 确认密码</label><input
                      type="password" v-model="addUserForm.confirmPassword" class="styled-input" /></div>
                  <div class="form-group"><label><i class="fas fa-venus-mars"></i> 性别</label><select
                      v-model="addUserForm.gender" class="styled-input">
                      <option value="male">男</option>
                      <option value="female">女</option>
                      <option value="other">其他</option>
                    </select></div>
                  <div class="form-group"><label><i class="fas fa-mobile-alt"></i> 手机号</label><input type="tel"
                      v-model="addUserForm.phone" class="styled-input" /></div>
                </div>
              </div>
              <div class="modal-footer">
                <button class="cancel-btn" @click="showAddUserModal = false"><i class="fas fa-times"></i> 取消</button>
                <button class="submit-btn" @click="handleAddUserSubmit" :disabled="!isAddUserFormValid"><i
                    class="fas fa-check"></i> 确定</button>
              </div>
            </div>
          </div>
        </transition>

      </div>
    </main>
  </div>
</template>

<script setup>
import axios from 'axios'
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

// --- 1. 配置后端地址 ---
const API_BASE_URL = 'http://localhost:8081/api'
const IMG_BASE_URL = 'http://localhost:8081' // 图片基础路径

const router = useRouter()

// --- 状态管理 ---
const currentView = ref('home')
const selectedDevice = ref(null)
const showDeviceDetailModal = ref(false)

// --- 基础数据 ---
const username = ref('Admin888')

// [修改] 增加 id 字段，方便更新时使用
const userInfo = ref({
  id: null,
  username: '',
  nickname: '加载中...',
  lastLoginTime: '',
  adminId: '',
  avatar: 'https://via.placeholder.com/120',
  gender: '',
  phone: '',
})

const productInfo = ref({
  hardwareVersion: 'V2.5.1',
  clientVersion: 'V1.3.2',
})

// --- [核心] 辅助函数：解析头像地址 ---
const resolveAvatarUrl = (path) => {
  if (!path) return 'https://via.placeholder.com/120'
  if (path.startsWith('http')) return path
  if (path.startsWith('/images')) return `${IMG_BASE_URL}${path}`
  return path
}

// --- 设备管理数据 ---
const deviceList = ref([])
let dataTimer = null

// --- 模拟获取设备数据 ---
// --- 模拟获取设备数据 (修改后) ---
const fetchDeviceData = async () => {
  // 获取当前管理员的时间作为模拟数据
  const adminTime = userInfo.value.lastLoginTime || new Date().toLocaleString();

  // 初始化一个 liveDevice 对象 (默认离线)
  let liveDevice = {
    id: 'CS-001',
    name: 'CS-001 ',
    status: 'offline',
    location: 'A栋-301室',
    lastOfflineTime: adminTime, // 绑定管理员最后登录时间
    temperature: 'N/A',
    humidity: 'N/A',
    distance: 'N/A'
  }

  try {
    // [修改] 使用 axios.get，路径带上 /api
    const response = await axios.get('/api/device-data');
    const data = response.data; // axios 的响应体在 .data 中

    // 更新设备状态
    liveDevice.status = data.deviceStatus === '在线' ? 'online' : 'offline';

    // 只有非 N/A 且非 ERR 时才添加单位
    liveDevice.temperature = (data.temp !== 'N/A' && data.temp !== 'ERR') ? `${data.temp}°C` : 'N/A';
    liveDevice.humidity = (data.humi !== 'N/A' && data.humi !== 'ERR') ? `${data.humi}%` : 'N/A';
    liveDevice.distance = (data.distance !== 'N/A' && data.distance !== 'ERR') ? data.distance : 'N/A';

  } catch (error) {
    // 这里的 error 包含了网络错误或 4xx/5xx 响应错误
    console.error("获取设备数据失败:", error);
  }

  // 更新列表：liveDevice 使用实时数据，其他为模拟数据
  deviceList.value = [
    liveDevice,
    { id: 'DEV-002', name: 'CS-002', status: 'offline', location: 'B栋-机房', lastOfflineTime: adminTime, temperature: 'N/A', humidity: 'N/A' },
    { id: 'DEV-003', name: 'CS-003', status: 'online', location: 'C栋-全区域', lastOfflineTime: '2025-10-22 11:00:00', temperature: '23.8°C', humidity: '52%' },
    { id: 'DEV-004', name: 'CS-004', status: 'online', location: '园区-南门', lastOfflineTime: '2025-10-20 01:00:00', temperature: '22.0°C', humidity: '60%' },
    { id: 'DEV-005', name: 'CS-005', status: 'offline', location: '园区-北门', lastOfflineTime: adminTime, temperature: 'N/A', humidity: 'N/A' },
    { id: 'DEV-006', name: 'CS-006', status: 'online', location: 'D栋-1号梯', lastOfflineTime: '2025-10-23 18:45:00', temperature: '25.0°C', humidity: '58%' },
  ]
}

// --- 用户管理数据 (模拟) ---
const userList = ref([])
const selectedUser = ref(null)
const showAddUserModal = ref(false)
const showUserEditModal = ref(false)
const userEditForm = ref({ id: '', username: '', nickname: '', gender: '', phone: '', password: '' })
const addUserForm = ref({ username: '', nickname: '', password: '', confirmPassword: '', gender: '', phone: '' })
// --- 修改 fetchUserList 函数 ---
// --- 获取用户列表 (修改后) ---
const fetchUserList = async () => {
  try {
    // [修改] 
    // 1. 使用 axios.get (后端 AdminController 是 @GetMapping)
    // 2. 路径补全为 /api/admin/user-list (因为 baseURL 是 localhost:8081)
    // 3. 不再需要手动写 headers，拦截器会自动带上 Token
    const response = await axios.get('/api/admin/user-list');

    const resData = response.data;

    if (resData.success) {
      // 这里的 resData.data 是后端传来的 Admin 对象数组
      // 需要映射为前端表格所需的格式
      userList.value = resData.data.map(u => {
        // 1. 性别转换逻辑 (后端: 1男, 0女 -> 前端: male, female)
        let genderStr = 'other';
        if (u.gender === 1) genderStr = 'male';
        if (u.gender === 0) genderStr = 'female';

        // 2. 返回符合前端模板的数据结构
        return {
          id: u.id,
          username: u.username,
          nickname: u.nickname || '未设置昵称',
          phone: u.phone || '暂无',
          gender: genderStr,
          // 保存原始对象，方便编辑时回显数据
          originalData: u
        };
      });
    } else {
      console.error('获取用户列表失败:', resData.message);
    }
  } catch (error) {
    console.error('网络请求错误:', error);
  }
}



// --- [核心] 检查 Session 并加载真实用户信息 ---
// 务必确保在 <script setup> 顶部导入了 axios
// import axios from 'axios' 

const checkSession = async () => {
  try {
    const response = await axios.get('/api/user/me') // 使用拦截器自动带 Token
    const data = response.data

    if (data.loggedIn && data.userData) {
      const u = data.userData
      const displayAvatar = resolveAvatarUrl(u.avatarUrl)

      userInfo.value = {
        id: u.id,
        username: u.username,
        nickname: u.nickname || u.username || '未设置昵称',
        phone: u.phone,
        gender: u.gender === 1 ? 'male' : (u.gender === 0 ? 'female' : 'other'),
        avatar: displayAvatar,
        adminId: `NO.${u.id}`,
        lastLoginTime: new Date().toLocaleString()
      }
    } else {
      // [!!关键!!] 后端说没登录，说明 Token 无效了，必须删掉！
      console.warn('Session 无效，强制清理')
      forceLogout()
    }
  } catch (e) {
    console.error('Session 检查失败:', e)
    // 401 在 main.js 会被拦截，但其他错误也建议清理
    forceLogout()
  }
}

// 提取一个公用的强制退出函数
const forceLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.push('/login')
}

// --- 组件挂载 ---
onMounted(() => {
  checkSession() // 先检查登录
  fetchDeviceData()
  dataTimer = setInterval(fetchDeviceData, 5000)
  fetchUserList()
})

onUnmounted(() => {
  if (dataTimer) {
    clearInterval(dataTimer)
  }
})

// --- 模态框状态 ---
const showEditUserModal = ref(false)
const showProductModal = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const showHardwareHint = ref(false)
const showClientHint = ref(false)

// --- 个人信息表单数据 ---
const editForm = ref({
  avatar: 'https://via.placeholder.com/80',
  nickname: '',
  password: '',
  confirmPassword: '',
  gender: '',
  phone: '',
})

// --- [核心] 文件上传相关 ---
const selectedAvatarFile = ref(null) // 存储用户选择的文件对象
const fileInput = ref(null)

const productForm = ref({
  hardwareVersion: '',
  clientVersion: '',
})

// --- 计算属性 ---
const passwordMismatch = computed(() => {
  return (
    editForm.value.password &&
    editForm.value.confirmPassword &&
    editForm.value.password !== editForm.value.confirmPassword
  )
})

// [优化] 表单验证逻辑
const isFormValid = computed(() => {
  const basicValid = editForm.value.nickname && editForm.value.phone
  if (!basicValid) return false

  if (editForm.value.password) {
    return editForm.value.confirmPassword && (editForm.value.password === editForm.value.confirmPassword)
  }
  return true
})

const isProductFormValid = computed(() => {
  const versionPattern = /^V\d+\.\d+\.\d+$/
  return (
    versionPattern.test(productForm.value.hardwareVersion) &&
    versionPattern.test(productForm.value.clientVersion)
  )
})

const isUserEditFormValid = computed(() => userEditForm.value.nickname && userEditForm.value.phone)
const isAddUserFormValid = computed(() => {
  const passwordsMatch = addUserForm.value.password === addUserForm.value.confirmPassword
  return addUserForm.value.username && addUserForm.value.nickname && addUserForm.value.password && addUserForm.value.phone && passwordsMatch
})

// --- 方法 ---

const setView = (viewName) => {
  currentView.value = viewName
  closeDeviceDetailModal()
  showAddUserModal.value = false
  showUserEditModal.value = false
}

const selectDevice = (device) => { selectedDevice.value = device; showDeviceDetailModal.value = true }
const closeDeviceDetailModal = () => { showDeviceDetailModal.value = false; setTimeout(() => { selectedDevice.value = null }, 300) }
const handleCallDevice = (device) => { alert(`正在呼叫: ${device.name}`) }
// [新增] 判断设备监测状态
const getMonitorStatus = (distanceVal) => {
  const val = parseFloat(distanceVal)
  // 如果不是数字（例如 'N/A'），显示待机或未知
  if (isNaN(val)) return { text: '待机中', color: '#64748b' } // 灰色

  // 核心逻辑：小于30显示跌倒
  if (val < 30) {
    return { text: '⚠️ 可能跌倒', color: '#ef4444' } // 红色
  } else {
    return { text: '正常', color: '#10b981' } // 绿色
  }
}
const handleModifyDevice = (device) => { alert(`修改设备: ${device.name}`) }
const handleDeleteDevice = (device) => { if (confirm(`删除 ${device.name}?`)) { closeDeviceDetailModal(); fetchDeviceData() } }

// --- [核心] 个人信息修改逻辑 ---

// 1. 触发文件选择
const triggerFileUpload = () => {
  fileInput.value.click()
}

// 2. 处理文件变动 (预览 + 保存文件对象)
const handleAvatarChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    selectedAvatarFile.value = file // 保存文件对象供上传用
    const reader = new FileReader()
    reader.onload = (e) => {
      editForm.value.avatar = e.target.result // Base64 仅用于预览
    }
    reader.readAsDataURL(file)
  }
}

// 3. 提交修改 (上传 -> 拿路径 -> 存库)
const handleSubmit = async () => {
  // 手动检查验证
  if (!editForm.value.nickname) return alert('昵称不能为空')
  if (!editForm.value.phone) return alert('手机号不能为空')
  if (passwordMismatch.value) return alert('两次密码不一致')

  try {
    let finalAvatarPath = null

    // --- A. 图片上传 (已修复，使用 axios) ---
    if (selectedAvatarFile.value) {
      const formData = new FormData()
      formData.append('file', selectedAvatarFile.value)

      // ✅ 这里是对的，axios 会自动带 Token 和 Content-Type
      const uploadRes = await axios.post(`${API_BASE_URL}/upload`, formData);
      const uploadData = uploadRes.data;

      if (uploadData.success) {
        finalAvatarPath = uploadData.filePath
      } else {
        return alert('图片上传失败: ' + uploadData.message)
      }
    } else {
      // 没选新图，保持原样
      const current = userInfo.value.avatar
      if (current && current.includes(IMG_BASE_URL)) {
        finalAvatarPath = current.replace(IMG_BASE_URL, '')
      } else {
        finalAvatarPath = current
      }
    }

    // --- B. 准备数据 ---
    let genderInt = 2
    if (editForm.value.gender === 'male') genderInt = 1
    if (editForm.value.gender === 'female') genderInt = 0

    const payload = {
      id: userInfo.value.id,
      nickname: editForm.value.nickname,
      phone: editForm.value.phone,
      gender: genderInt,
      avatarUrl: finalAvatarPath,
      password: editForm.value.password || ''
    }

    // --- C. 调用更新接口 (🔴 必须也改成 axios) ---
    // ❌ 原代码使用 fetch 会导致 403，因为没有 Token
    // const updateRes = await fetch(...) 

    // ✅ 修正为 axios.post，会自动携带 Token，并且自动处理 JSON
    const updateRes = await axios.post(`${API_BASE_URL}/admin/update`, payload);
    const updateData = updateRes.data;

    if (updateData.success) {
      // --- D. 更新本地视图 ---
      userInfo.value.nickname = editForm.value.nickname
      userInfo.value.phone = editForm.value.phone
      userInfo.value.gender = editForm.value.gender

      if (finalAvatarPath) {
        userInfo.value.avatar = resolveAvatarUrl(finalAvatarPath)
      }

      alert('个人信息更新成功！')
      showEditUserModal.value = false
      selectedAvatarFile.value = null
    } else {
      alert('更新失败: ' + updateData.message)
    }

  } catch (error) {
    console.error('更新错误:', error)
    // axios 的错误对象里通常有 response
    if (error.response && error.response.status === 403) {
      alert('请求失败：权限不足或登录已过期 (403)');
    } else {
      alert('请求失败，请检查后端服务是否启动');
    }
  }
}

// --- 产品版本逻辑 ---
const validateVersionFormat = (event, type) => {
  const input = event.target.value
  const isValid = /^V\d+\.\d+\.\d+$/.test(input)
  if (type === 'hardware') showHardwareHint.value = !isValid && input !== ''
  else showClientHint.value = !isValid && input !== ''
}
const handleProductSubmit = () => {
  if (!isProductFormValid.value) return
  productInfo.value.hardwareVersion = productForm.value.hardwareVersion
  productInfo.value.clientVersion = productForm.value.clientVersion
  showProductModal.value = false
}

// --- 用户管理逻辑 ---
const formatGender = (gender) => {
  if (gender === 'male') return '男'
  if (gender === 'female') return '女'
  if (gender === 'other') return '其他'
  return '未设置'
}
const openUserEditModal = (user) => { selectedUser.value = user; userEditForm.value = { ...user, password: '' }; showUserEditModal.value = true }
const closeUserEditModal = () => { showUserEditModal.value = false; selectedUser.value = null }
// --- 修改 handleUserEditSubmit ---
const handleUserEditSubmit = async () => {
  if (!isUserEditFormValid.value) return;

  // 1. 转换性别
  let genderInt = 2;
  if (userEditForm.value.gender === 'male') genderInt = 1;
  if (userEditForm.value.gender === 'female') genderInt = 0;

  // 2. 准备数据 (MyBatisPlus 根据 ID 更新)
  const payload = {
    id: userEditForm.value.id,
    nickname: userEditForm.value.nickname,
    phone: userEditForm.value.phone,
    gender: genderInt,
    // 如果密码框里有内容，传过去；如果是空的，后端根据逻辑会忽略更新
    password: userEditForm.value.password || ''
  };

  try {
    // [修改] 改用 axios.post，路径去掉 baseURL (main.js已配置)，不需要手动写 header
    const response = await axios.post('/admin/user/update', payload);
    const data = response.data; // axios 直接拿 .data

    if (data.success) {
      alert('用户信息修改成功！');
      closeUserEditModal();
      fetchUserList();
    } else {
      alert('修改失败: ' + data.message);
    }
  } catch (error) {
    console.error(error);
    alert('请求失败');
  }
}
// --- 修改 handleUserDeleteFromModal ---
const handleUserDeleteFromModal = async (user) => {
  if (!confirm(`确定要彻底删除用户 "${user.nickname}" 吗? 此操作不可恢复。`)) {
    return;
  }

  try {
    const response = await axios.post('/admin/user/delete', { id: user.id });
    const data = response.data;

    if (data.success) {
      alert('用户已删除');
      closeUserEditModal();
      fetchUserList(); // 刷新列表
    } else {
      alert('删除失败: ' + data.message);
    }
  } catch (error) {
    console.error(error);
    alert('请求失败');
  }
}
const openAddUserModal = () => { addUserForm.value = { username: '', nickname: '', password: '', confirmPassword: '', gender: '', phone: '' }; showAddUserModal.value = true }
// --- 修改 handleAddUserSubmit ---
const handleAddUserSubmit = async () => {
  // 1. 表单校验
  if (!isAddUserFormValid.value) return;

  // 2. 准备数据，转换性别格式
  let genderInt = 2; // 默认其他
  if (addUserForm.value.gender === 'male') genderInt = 1;
  if (addUserForm.value.gender === 'female') genderInt = 0;

  const payload = {
    username: addUserForm.value.username,
    nickname: addUserForm.value.nickname,
    password: addUserForm.value.password, // 必须传明文密码
    phone: addUserForm.value.phone,
    gender: genderInt
  };

  try {
    const response = await axios.post('/admin/user/add', payload);
    const data = response.data;

    if (data.success) {
      alert('新增用户成功！');
      showAddUserModal.value = false;
      fetchUserList(); // 刷新列表
    } else {
      alert('新增失败: ' + data.message);
    }
  } catch (error) {
    console.error(error);
    alert('请求失败，请检查网络');
  }
}

// --- 监听器 ---
watch(showEditUserModal, (newVal) => {
  if (newVal) {
    // 打开模态框时，回显当前数据
    editForm.value = {
      avatar: userInfo.value.avatar, // 这里已经是绝对路径，可以直接预览
      nickname: userInfo.value.nickname || '',
      password: '',
      confirmPassword: '',
      gender: userInfo.value.gender || '',
      phone: userInfo.value.phone || '',
    }
    selectedAvatarFile.value = null // 重置文件选择
  }
})

watch(showProductModal, (newVal) => {
  if (newVal) {
    productForm.value = { ...productInfo.value }
  }
})

// --- 退出登录 ---
const handleLogout = async () => {
  if (!confirm('您确定要退出登录吗？')) return

  try {
    // 1. 调用后端退出接口 (使用 axios，不要用 fetch)
    // 注意：即使后端由 Stateless 模式不需要这一步，调用一下也无妨
    await axios.post('/logout')
  } catch (error) {
    console.warn('后端退出接口调用失败，但这不重要，前端强制下线:', error)
  } finally {
    // [!!关键!!] 2. 无论后端成功与否，前端必须彻底清除 Token
    localStorage.removeItem('token')
    localStorage.removeItem('user') // 如果你存了这个也删掉

    // 3. 跳转回登录页
    router.push('/login')
  }
}
</script>

<style scoped>
:root {
  --sidebar-bg: linear-gradient(180deg, #1e293b 0%, #334155 100%);
  --sidebar-text: #e2e8f0;
  --sidebar-text-active: #ffffff;
  --sidebar-active-bg: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  --main-bg: #f8fafc;
  --header-bg: #ffffff;
  --card-bg: #ffffff;
  --border-color: #f1f5f9;
  --text-primary: #1e293b;
  --text-secondary: #64748b;
  --primary-btn: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  --primary-btn-hover: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  --danger-btn: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  --danger-btn-hover: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  --success-color: #10b981;
  --danger-color: #ef4444;
  --warning-btn: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  --warning-btn-hover: linear-gradient(135deg, #d97706 0%, #b45309 100%);
  --card-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  --card-shadow-hover: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

* {
  box-sizing: border-box;
}

.admin-layout {
  display: flex;
  min-height: 100vh;
  width: 100%;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background-color: var(--main-bg);
  overflow: hidden;
}

/* 侧边栏美化 */
.sidebar {
  width: 280px;
  flex-shrink: 0;
  background: var(--sidebar-bg);
  color: var(--sidebar-text);
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 10;
}

.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
}

.sidebar-header {
  padding: 32px 24px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--sidebar-text-active);
  line-height: 1.4;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  background: linear-gradient(135deg, #ffffff 0%, #e2e8f0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sidebar-nav {
  padding: 24px 0;
  flex-grow: 1;
}

.sidebar-nav ul {
  list-style: none;
  margin: 0;
  padding: 0;
}

.sidebar-nav li {
  margin: 0 16px 12px;
}

.sidebar-logo {
  width: 70px;
  /* 稍微加大一点尺寸，看起来更大气 */
  height: 70px;
  /* 强制正方形 */
  border-radius: 50%;
  /* 核心：变圆 */
  object-fit: cover;
  /* 防止图片变形 */

  /* 美化部分 */
  border: 3px solid rgba(255, 255, 255, 0.2);
  /* 半透明白色边框 */
  background-color: white;
  /* 如果你的图片是透明底，加个白底会更清晰 */
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
  /* 柔和的投影 */
  margin-bottom: 16px;
  /* 调整下边距 */

  /* 动效 */
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  /* 有弹性的过渡 */
}

/* 鼠标放上去时的可爱动效 */
.sidebar-header:hover .sidebar-logo {
  transform: scale(1.1) rotate(5deg);
  /* 放大并微微旋转 */
  border-color: #ffffff;
  /* 边框变亮 */
  box-shadow: 0 0 20px rgba(59, 130, 246, 0.6);
  /* 发出蓝色的光晕 */
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  text-decoration: none;
  color: var(--sidebar-text);
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;
  position: relative;
  overflow: hidden;
}

.nav-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 4px;
  background: var(--sidebar-active-bg);
  transform: translateX(-100%);
  transition: transform 0.3s ease;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateX(4px);
  color: var(--sidebar-text-active);
}

.nav-item:hover::before {
  transform: translateX(0);
}

.nav-item.active {
  background: var(--sidebar-active-bg);
  color: var(--sidebar-text-active);
  font-weight: 600;
  transform: translateX(0);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.nav-item.active::before {
  transform: translateX(0);
}

/* 主内容区美化 */
.main-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  background: var(--main-bg);
}

.main-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 32px;
  background: var(--header-bg);
  border-bottom: 1px solid var(--border-color);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 5;
}

.welcome-message {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
  position: relative;
}

.welcome-message::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 40px;
  height: 3px;
  background: var(--primary-btn);
  border-radius: 2px;
}

.logout-button {
  padding: 10px 20px;
  border: none;
  border-radius: 10px;
  background: var(--danger-btn);
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.logout-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.logout-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(239, 68, 68, 0.3);
}

.logout-button:hover::before {
  left: 100%;
}

.content-body {
  padding: 32px;
  flex-grow: 1;
}

/* 卡片容器美化 */
.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.info-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 28px;
  box-shadow: var(--card-shadow);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  height: 100%;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.8);
  position: relative;
  overflow: hidden;
}

.info-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--primary-btn);
  border-radius: 16px 16px 0 0;
}

.info-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--card-shadow-hover);
}

.user-info-section {
  display: flex;
  align-items: center;
  flex-grow: 1;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-right: 20px;
  object-fit: cover;
  border: 4px solid #e2e8f0;
  transition: all 0.3s ease;
}

.info-card:hover .avatar {
  border-color: #3b82f6;
  transform: scale(1.05);
}

.user-details p {
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.user-details p:last-child {
  margin-bottom: 0;
}

.nickname {
  font-size: 1.6rem;
  font-weight: 700;
  color: var(--text-primary);
  background: linear-gradient(135deg, #1e293b 0%, #3b82f6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.last-login,
.admin-id {
  font-size: 0.95rem;
  color: var(--text-secondary);
  opacity: 0.8;
}

.product-info-section {
  flex-grow: 1;
}

.product-title {
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-title i {
  background: var(--primary-btn);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 1.4rem;
}

.version-item {
  font-size: 1.05rem;
  color: var(--text-secondary);
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.version-item i {
  background: var(--primary-btn);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  width: 20px;
  text-align: center;
  font-size: 1.1rem;
}

.version-item strong {
  color: var(--text-primary);
  min-width: 70px;
  display: inline-block;
  font-weight: 600;
}

.version-number {
  font-family: 'JetBrains Mono', 'Consolas', monospace;
  color: #3b82f6;
  font-weight: 600;
  letter-spacing: 0.5px;
  padding: 4px 8px;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 6px;
  margin-left: 4px;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.edit-button {
  padding: 10px 20px;
  border: none;
  border-radius: 10px;
  background: var(--primary-btn);
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.edit-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.edit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.3);
}

.edit-button:hover::before {
  left: 100%;
}

/* 设备网格美化 */
.device-grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.device-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--card-shadow);
  border-left: 6px solid;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.device-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.03) 0%, transparent 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.device-card:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: var(--card-shadow-hover);
}

.device-card:hover::before {
  opacity: 1;
}

.device-card.online {
  border-left-color: var(--success-color);
}

.device-card.offline {
  border-left-color: var(--danger-color);
}

.device-card-header {
  display: flex;
  align-items: center;
  gap: 16px;
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 16px;
  margin-bottom: 16px;
  position: relative;
  z-index: 2;
}

.device-icon {
  font-size: 1.8rem;
  background: var(--primary-btn);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.device-name {
  margin: 0;
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--text-primary);
}

.device-card-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
  position: relative;
  z-index: 2;
}

.device-status {
  display: flex;
  align-items: center;
  font-weight: 600;
}

.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 12px;
  position: relative;
}

.status-indicator.online {
  background-color: var(--success-color);
  box-shadow: 0 0 12px rgba(16, 185, 129, 0.6);
  animation: pulse 2s infinite;
}

.status-indicator.offline {
  background-color: var(--danger-color);
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7);
  }

  70% {
    box-shadow: 0 0 0 10px rgba(16, 185, 129, 0);
  }

  100% {
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0);
  }
}

.status-text {
  color: var(--text-primary);
  font-weight: 600;
}

.device-location {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--text-secondary);
  font-size: 0.95rem;
  margin: 0;
}

.device-location i {
  color: var(--text-secondary);
  opacity: 0.7;
}

/* 状态标签美化 */
.status-tag {
  padding: 6px 16px;
  border-radius: 20px;
  font-weight: 700;
  font-size: 0.85rem;
  color: white;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-tag.online {
  background: var(--success-color);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
}

.status-tag.offline {
  background: var(--danger-color);
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
}

/* 设备详情卡片美化 */
.device-detail-card {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 16px;
  padding: 28px;
  border: 1px solid var(--border-color);
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  position: relative;
  overflow: hidden;
}

/* 个人中心（Profile）布局优化 */
.profile-card {
  max-width: 1100px;
  margin: 0 auto;
  background: var(--card-bg);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--card-shadow);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.profile-grid {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 28px;
  align-items: start;
}

.profile-summary {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  border: 1px solid var(--border-color);
}

.summary-avatar-wrap {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.profile-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
  border: 4px solid #e2e8f0;
}

.summary-info .profile-nickname {
  font-size: 1.4rem;
  margin: 0 0 6px 0;
}

.profile-quick {
  color: var(--text-secondary);
  font-size: 0.95rem;
  margin: 6px 0;
}

.profile-body {
  padding: 8px 4px;
}

.profile-section-title {
  margin-bottom: 18px;
}

.profile-details-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.profile-details-grid .detail-item {
  background: #ffffff;
  border-radius: 10px;
  padding: 16px;
  border: 1px solid var(--border-color);
}

@media (max-width: 900px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }

  .profile-details-grid {
    grid-template-columns: 1fr;
  }

  .profile-summary {
    flex-direction: row;
    gap: 16px;
    align-items: center;
    text-align: left;
    padding: 16px;
  }

  .summary-avatar-wrap {
    width: 88px;
    height: 88px;
  }
}

.device-detail-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--primary-btn);
}

.detail-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.detail-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.detail-label {
  display: block;
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 8px;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.detail-value {
  font-weight: 700;
  color: var(--text-primary);
  font-family: 'JetBrains Mono', 'Consolas', monospace;
  font-size: 1.1rem;
  background: linear-gradient(135deg, #1e293b 0%, #3b82f6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 模态框美化 */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
  transform: scale(1.1) translateY(-20px);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(8px);
  animation: overlayFade 0.3s ease;
}

@keyframes overlayFade {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

.modal-content {
  background: white;
  border-radius: 20px;
  width: 90%;
  max-width: 600px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  transform: translateY(0);
  transition: transform 0.3s ease;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.modal-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--primary-btn);
}

.device-detail-modal-content {
  max-width: 700px;
}

.modal-header {
  padding: 28px 32px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
  border-radius: 20px 20px 0 0;
  flex-shrink: 0;
}

.modal-title-wrapper {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-grow: 1;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.6rem;
  font-weight: 700;
  color: var(--text-primary);
  background: linear-gradient(135deg, #1e293b 0%, #3b82f6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.modal-back-btn {
  background: rgba(59, 130, 246, 0.1);
  border: 1px solid rgba(59, 130, 246, 0.2);
  width: auto;
  height: auto;
  padding: 8px 16px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  color: #3b82f6;
  transition: all 0.3s ease;
  flex-shrink: 0;
  font-size: 0.9rem;
  font-weight: 600;
}

.modal-back-btn:hover {
  background: rgba(59, 130, 246, 0.2);
  transform: translateX(-2px);
}

.close-button {
  background: rgba(0, 0, 0, 0.05);
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #64748b;
  transition: all 0.3s ease;
  flex-shrink: 0;
  font-size: 30px
}

.close-button:hover {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  transform: rotate(90deg);
}

.modal-body {
  padding: 32px;
  overflow-y: auto;
  flex-grow: 1;
}

/* 表单样式美化 */
.avatar-upload-section {
  text-align: center;
  margin-bottom: 32px;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;
}

.preview-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #e2e8f0;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  opacity: 0;
  transition: all 0.3s ease;
  cursor: pointer;
  backdrop-filter: blur(2px);
}

.avatar-overlay:hover {
  opacity: 1;
  transform: scale(1.05);
}

.avatar-overlay i {
  font-size: 24px;
  margin-bottom: 8px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.form-group {
  margin-bottom: 0;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  color: var(--text-primary);
  font-weight: 600;
  font-size: 0.95rem;
}

.form-group label i {
  background: var(--primary-btn);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  width: 16px;
  text-align: center;
}

.styled-input {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: #f8fafc;
  font-family: inherit;
}

.styled-input:focus {
  border-color: #3b82f6;
  background: white;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
  outline: none;
  transform: translateY(-1px);
}

.password-group {
  position: relative;
}

.password-input-wrapper {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #64748b;
  cursor: pointer;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.password-toggle:hover {
  background: rgba(0, 0, 0, 0.05);
  color: #3b82f6;
}

.error-message {
  color: #ef4444;
  font-size: 12px;
  margin-top: 6px;
  display: block;
  font-weight: 500;
}

.styled-input.error {
  border-color: #ef4444;
  box-shadow: 0 0 0 4px rgba(239, 68, 68, 0.1);
}

.modal-footer {
  padding: 24px 32px;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  flex-shrink: 0;
  background: #f8fafc;
  border-radius: 0 0 20px 20px;
}

.modal-footer button {
  padding: 12px 28px;
  border-radius: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
  border: none;
  font-size: 0.95rem;
  position: relative;
  overflow: hidden;
}

.modal-footer button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.modal-footer button:hover::before {
  left: 100%;
}

.cancel-btn {
  border: 2px solid #e2e8f0;
  background: white;
  color: #64748b;
}

.cancel-btn:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.submit-btn {
  border: none;
  background: var(--primary-btn);
  color: white;
}

.submit-btn:hover:not(:disabled) {
  background: var(--primary-btn-hover);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.3);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}

.action-btn {
  border: none;
  color: white;
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.action-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.action-btn:hover::before {
  left: 100%;
}

.btn-call {
  background: var(--primary-btn);
}

.btn-call:hover {
  background: var(--primary-btn-hover);
}

.btn-modify {
  background: var(--warning-btn);
}

.btn-modify:hover {
  background: var(--warning-btn-hover);
}

.btn-delete {
  background: var(--danger-btn);
}

.btn-delete:hover {
  background: var(--danger-btn-hover);
}

/* 用户管理表格美化 */
.user-management-container {
  background: var(--card-bg);
  border-radius: 20px;
  padding: 32px;
  box-shadow: var(--card-shadow);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.user-table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}

.user-table-header h2 {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, #1e293b 0%, #3b82f6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.add-user-btn {
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
  border: none;
  background: var(--primary-btn);
  color: white;
  position: relative;
  overflow: hidden;
}

.add-user-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.add-user-btn:hover {
  background: var(--primary-btn-hover);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.3);
}

.add-user-btn:hover::before {
  left: 100%;
}

.user-management-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.user-management-table th,
.user-management-table td {
  padding: 16px 20px;
  border: 1px solid var(--border-color);
  text-align: left;
  vertical-align: middle;
}

.user-management-table th {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  font-weight: 700;
  color: var(--text-primary);
  border-bottom: 2px solid var(--border-color);
}

.user-management-table tbody tr {
  transition: all 0.3s ease;
}

.user-management-table tbody tr:nth-child(even) {
  background: #fdfdfd;
}

.user-management-table tbody tr:hover {
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  transform: translateX(4px);
}

.action-buttons-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.action-btn-sm {
  background: none;
  border: none;
  padding: 6px 12px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s ease;
}

.action-btn-sm:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.action-btn-sm.btn-detail {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.action-btn-sm.btn-detail:hover {
  background: rgba(59, 130, 246, 0.2);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .cards-container {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .device-grid-container {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }

  .form-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .admin-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    height: auto;
  }

  .device-grid-container {
    grid-template-columns: 1fr;
  }

  .modal-content {
    width: 95%;
    margin: 20px;
  }

  .device-detail-card {
    grid-template-columns: 1fr;
  }

  .profile-details-grid {
    grid-template-columns: 1fr;
  }
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 加载动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.info-card,
.device-card,
.user-management-container {
  animation: fadeInUp 0.6s ease;
}

/* 在现有的 <style scoped> 中添加以下样式 */

/* 设备详情弹窗优化 */
.device-detail-modal-content .modal-header .status-tag.offline {
  background: #ef4444 !important;
  color: white !important;
  font-weight: 700;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

/* 按钮对比度优化 */
.device-detail-modal-content .modal-footer {
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
  padding: 24px 32px;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.device-detail-modal-content .action-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  color: white;
  transition: all 0.3s ease;
  min-width: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.device-detail-modal-content .btn-call {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.device-detail-modal-content .btn-modify {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3);
}

.device-detail-modal-content .btn-delete {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
}

.device-detail-modal-content .action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 设备详情项优化 */
.device-detail-card .detail-item {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}

.device-detail-card .detail-label {
  font-weight: 600;
  color: #64748b;
  font-size: 0.9rem;
  margin-bottom: 4px;
}

.device-detail-card .detail-value {
  font-weight: 600;
  color: #1e293b;
  font-size: 1rem;
  font-family: 'JetBrains Mono', monospace;
}
</style>