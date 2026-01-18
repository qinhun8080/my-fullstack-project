"use strict";
Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const common_vendor = require("./common/vendor.js");
if (!Math) {
  "./pages/index/index.js";
  "./pages/profile/profile.js";
  "./pages/profile-edit/profile-edit.js";
  "./pages/login/login.js";
  "./pages/login/register.js";
  "./pages/login/forgot-password.js";
}
const API_BASE_URL = "http://127.0.0.1:8081/api";
const _sfc_main = {
  onLaunch: function() {
    common_vendor.index.__f__("log", "at App.vue:6", "App Launch");
    this.checkLoginStatus();
  },
  methods: {
    checkLoginStatus() {
      const hasLogin = common_vendor.index.getStorageSync("hasLogin");
      if (!hasLogin) {
        this.redirectToLogin();
      } else {
        common_vendor.index.request({
          url: API_BASE_URL + "/session-status",
          method: "GET",
          withCredentials: true,
          // 必须携带 Cookie
          success: (res) => {
            if (res.data && res.data.loggedIn) {
              common_vendor.index.__f__("log", "at App.vue:27", "Session有效，保持登录状态");
              common_vendor.index.setStorageSync("userInfo", res.data.userData);
            } else {
              common_vendor.index.__f__("log", "at App.vue:31", "Session已过期，请重新登录");
              common_vendor.index.removeStorageSync("hasLogin");
              common_vendor.index.removeStorageSync("userInfo");
              this.redirectToLogin();
            }
          },
          fail: () => {
          }
        });
      }
    },
    redirectToLogin() {
      common_vendor.index.reLaunch({
        url: "/pages/login/login"
      });
    }
  }
};
function createApp() {
  const app = common_vendor.createSSRApp(_sfc_main);
  return {
    app
  };
}
createApp().app.mount("#app");
exports.createApp = createApp;
//# sourceMappingURL=../.sourcemap/mp-weixin/app.js.map
