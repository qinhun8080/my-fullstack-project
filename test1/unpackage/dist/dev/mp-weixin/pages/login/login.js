"use strict";
const common_vendor = require("../../common/vendor.js");
const API_BASE_URL = "http://127.0.0.1:8081/api";
const _sfc_main = {
  data() {
    return {
      username: "",
      password: "",
      loggingIn: false,
      showPassword: false,
      rememberMe: false
      // 新增记住我状态
    };
  },
  methods: {
    togglePasswordVisibility() {
      this.showPassword = !this.showPassword;
    },
    // 切换记住我
    handleCheckboxChange(e) {
      this.rememberMe = e.detail.value.length > 0;
      common_vendor.index.__f__("log", "at pages/login/login.vue:95", "记住我状态:", this.rememberMe);
    },
    showPasswordTip() {
      common_vendor.index.showToast({ title: "密码建议功能开发中", icon: "none" });
    },
    handleLogin() {
      if (!this.username || !this.password) {
        common_vendor.index.showToast({ title: "请输入账号和密码", icon: "none" });
        return;
      }
      this.loggingIn = true;
      common_vendor.index.request({
        url: API_BASE_URL + "/login",
        method: "POST",
        withCredentials: true,
        data: {
          username: this.username,
          password: this.password,
          rememberMe: this.rememberMe
          // 现在传递真实状态
        },
        success: (res) => {
          common_vendor.index.__f__("log", "at pages/login/login.vue:118", "登录结果:", res.data);
          if (res.data && res.data.success) {
            common_vendor.index.setStorageSync("hasLogin", true);
            common_vendor.index.setStorageSync("userInfo", res.data.userData);
            common_vendor.index.showToast({ title: "登录成功", icon: "success" });
            setTimeout(() => {
              common_vendor.index.switchTab({
                url: "/pages/index/index"
              });
            }, 1e3);
          } else {
            common_vendor.index.showToast({
              title: res.data.message || "登录失败",
              icon: "none"
            });
          }
        },
        fail: (err) => {
          common_vendor.index.__f__("error", "at pages/login/login.vue:136", "请求失败:", err);
          common_vendor.index.showToast({ title: "无法连接服务器", icon: "none" });
        },
        complete: () => {
          this.loggingIn = false;
        }
      });
    },
    goToRegister() {
      common_vendor.index.navigateTo({
        // 请确保这里的文件路径与你在 pages.json 中配置的一致
        // 如果你的注册页面叫 register.vue，请改为 '/pages/register/register'
        url: "/pages/login/register"
      });
    },
    goToForgotPassword() {
      common_vendor.index.navigateTo({
        url: "/pages/login/forgot-password"
      });
    }
  }
};
if (!Array) {
  const _component_uni_icons = common_vendor.resolveComponent("uni-icons");
  _component_uni_icons();
}
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: $data.username,
    b: common_vendor.o(($event) => $data.username = $event.detail.value),
    c: !$data.showPassword,
    d: $data.password,
    e: common_vendor.o(($event) => $data.password = $event.detail.value),
    f: common_vendor.p({
      type: $data.showPassword ? "eye-filled" : "eye-slash-filled",
      size: "20",
      color: "#6b7280"
    }),
    g: common_vendor.o((...args) => $options.togglePasswordVisibility && $options.togglePasswordVisibility(...args)),
    h: $data.rememberMe,
    i: common_vendor.o((...args) => $options.handleCheckboxChange && $options.handleCheckboxChange(...args)),
    j: common_vendor.o((...args) => $options.goToForgotPassword && $options.goToForgotPassword(...args)),
    k: common_vendor.t($data.loggingIn ? "登 录 中..." : "登 录"),
    l: common_vendor.o((...args) => $options.handleLogin && $options.handleLogin(...args)),
    m: $data.loggingIn,
    n: $data.loggingIn ? 1 : "",
    o: common_vendor.o((...args) => $options.goToRegister && $options.goToRegister(...args)),
    p: common_vendor.p({
      type: "star",
      size: "14",
      color: "#6366f1"
    }),
    q: common_vendor.o((...args) => $options.showPasswordTip && $options.showPasswordTip(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-e4e4508d"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/login/login.js.map
