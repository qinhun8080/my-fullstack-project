"use strict";
const common_vendor = require("../../common/vendor.js");
const API_BASE_URL = "http://127.0.0.1:8081";
const DEFAULT_AVATAR = "/static/avatar.png";
const _sfc_main = {
  data() {
    return {
      // 对应后端的 User 实体
      form: {
        id: null,
        username: "",
        // 帐号只读
        nickname: "",
        gender: 0,
        // 0:保密, 1:男, 2:女
        birthday: "",
        phone: "",
        avatarUrl: "",
        // 存相对路径 /images/xxx.jpg
        password: ""
        // 可选，修改密码时才填
      },
      confirmPassword: "",
      // 前端辅助字段
      genderOptions: ["保密", "男", "女"],
      // 对应索引 0, 1, 2
      isSubmitting: false
    };
  },
  computed: {
    // 计算头像完整路径 (用于 <image src="...">)
    displayAvatar() {
      if (this.form.avatarUrl) {
        if (this.form.avatarUrl.startsWith("http"))
          return this.form.avatarUrl;
        return API_BASE_URL + this.form.avatarUrl;
      }
      return DEFAULT_AVATAR;
    },
    // 计算性别显示文本
    genderText() {
      return this.genderOptions[this.form.gender] || "请选择性别";
    }
  },
  onLoad() {
    this.loadUserProfile();
  },
  methods: {
    // 1. 加载本地缓存的用户信息回显
    loadUserProfile() {
      const userInfo = common_vendor.index.getStorageSync("userInfo");
      if (userInfo) {
        this.form.id = userInfo.id;
        this.form.username = userInfo.username;
        this.form.nickname = userInfo.nickname;
        this.form.gender = userInfo.gender || 0;
        this.form.birthday = userInfo.birthday;
        this.form.phone = userInfo.phone;
        this.form.avatarUrl = userInfo.avatarUrl;
        this.form.password = "";
      } else {
        common_vendor.index.showToast({ title: "未登录", icon: "none" });
        setTimeout(() => common_vendor.index.reLaunch({ url: "/pages/login/login" }), 1500);
      }
    },
    // 2. 选择并上传头像
    chooseAvatar() {
      common_vendor.index.chooseImage({
        count: 1,
        sizeType: ["compressed"],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0];
          this.uploadAvatar(tempFilePath);
        }
      });
    },
    // 3. 执行文件上传
    uploadAvatar(filePath) {
      common_vendor.index.showLoading({ title: "上传中..." });
      common_vendor.index.uploadFile({
        url: API_BASE_URL + "/api/upload",
        // 你的后端上传接口
        filePath,
        name: "file",
        success: (uploadRes) => {
          const data = JSON.parse(uploadRes.data);
          if (data.success) {
            this.form.avatarUrl = data.filePath;
            common_vendor.index.showToast({ title: "上传成功", icon: "none" });
          } else {
            common_vendor.index.showToast({ title: "上传失败", icon: "none" });
          }
        },
        fail: () => {
          common_vendor.index.showToast({ title: "网络错误", icon: "none" });
        },
        complete: () => {
          common_vendor.index.hideLoading();
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
      if (this.form.password && this.form.password !== this.confirmPassword) {
        common_vendor.index.showToast({ title: "两次输入的密码不一致", icon: "none" });
        return;
      }
      this.isSubmitting = true;
      const postData = {
        ...this.form
      };
      if (!postData.password) {
        postData.password = null;
      }
      common_vendor.index.request({
        url: API_BASE_URL + "/api/user/update",
        method: "POST",
        data: postData,
        success: (res) => {
          if (res.data.success) {
            common_vendor.index.showToast({ title: "保存成功" });
            common_vendor.index.setStorageSync("userInfo", res.data.data);
            setTimeout(() => {
              this.onCancel();
            }, 1500);
          } else {
            common_vendor.index.showToast({ title: res.data.message || "保存失败", icon: "none" });
          }
        },
        fail: () => {
          common_vendor.index.showToast({ title: "请求失败，请检查网络", icon: "none" });
        },
        complete: () => {
          this.isSubmitting = false;
        }
      });
    },
    onCancel() {
      common_vendor.index.navigateBack({ delta: 1 });
    }
  }
};
if (!Array) {
  const _component_uni_icons = common_vendor.resolveComponent("uni-icons");
  _component_uni_icons();
}
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: $options.displayAvatar,
    b: common_vendor.p({
      type: "right",
      size: "16",
      color: "#999"
    }),
    c: common_vendor.o((...args) => $options.chooseAvatar && $options.chooseAvatar(...args)),
    d: common_vendor.t($data.form.username),
    e: $data.form.nickname,
    f: common_vendor.o(($event) => $data.form.nickname = $event.detail.value),
    g: common_vendor.t($options.genderText),
    h: $data.form.gender === 0 ? 1 : "",
    i: common_vendor.p({
      type: "right",
      size: "16",
      color: "#999"
    }),
    j: $data.genderOptions,
    k: $data.form.gender,
    l: common_vendor.o((...args) => $options.onGenderChange && $options.onGenderChange(...args)),
    m: common_vendor.t($data.form.birthday || "请选择生日"),
    n: !$data.form.birthday ? 1 : "",
    o: common_vendor.p({
      type: "right",
      size: "16",
      color: "#999"
    }),
    p: $data.form.birthday,
    q: common_vendor.o((...args) => $options.onBirthdayChange && $options.onBirthdayChange(...args)),
    r: $data.form.password,
    s: common_vendor.o(($event) => $data.form.password = $event.detail.value),
    t: $data.confirmPassword,
    v: common_vendor.o(($event) => $data.confirmPassword = $event.detail.value),
    w: $data.form.phone,
    x: common_vendor.o(($event) => $data.form.phone = $event.detail.value),
    y: common_vendor.o((...args) => $options.onCancel && $options.onCancel(...args)),
    z: common_vendor.o((...args) => $options.onSubmit && $options.onSubmit(...args)),
    A: $data.isSubmitting
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-b59caf64"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/profile-edit/profile-edit.js.map
