"use strict";
const common_vendor = require("../../common/vendor.js");
const API_BASE_URL = "http://127.0.0.1:8081";
const DEFAULT_AVATAR = "/static/default-avatar.png";
const _sfc_main = {
  data() {
    return {
      userInfo: {}
    };
  },
  computed: {
    // 计算完整的头像 URL
    fullAvatarUrl() {
      if (this.userInfo.avatarUrl) {
        return API_BASE_URL + this.userInfo.avatarUrl;
      }
      return DEFAULT_AVATAR;
    },
    // 性别转换
    genderText() {
      const g = this.userInfo.gender;
      if (g === 1)
        return "男";
      if (g === 2)
        return "女";
      return "保密";
    }
  },
  onShow() {
    this.fetchLatestUserInfo();
  },
  methods: {
    fetchLatestUserInfo() {
      const localUser = common_vendor.index.getStorageSync("userInfo");
      if (!localUser || !localUser.id) {
        common_vendor.index.reLaunch({ url: "/pages/login/login" });
        return;
      }
      common_vendor.index.request({
        url: API_BASE_URL + "/api/user/info",
        method: "GET",
        data: { id: localUser.id },
        success: (res) => {
          if (res.data.success) {
            this.userInfo = res.data.data;
            common_vendor.index.setStorageSync("userInfo", this.userInfo);
          }
        }
      });
    },
    goToEdit() {
      common_vendor.index.navigateTo({
        url: "/pages/profile-edit/profile-edit"
      });
    },
    handleLogout() {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要退出登录吗？",
        success: (res) => {
          if (res.confirm) {
            common_vendor.index.removeStorageSync("userInfo");
            common_vendor.index.removeStorageSync("hasLogin");
            common_vendor.index.reLaunch({ url: "/pages/login/login" });
          }
        }
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
    a: $options.fullAvatarUrl,
    b: common_vendor.t($data.userInfo.nickname || $data.userInfo.username),
    c: common_vendor.t($data.userInfo.id),
    d: common_vendor.p({
      type: "forward",
      size: "16",
      color: "#999"
    }),
    e: common_vendor.o((...args) => $options.goToEdit && $options.goToEdit(...args)),
    f: common_vendor.p({
      type: "phone",
      size: "18",
      color: "#5a7dff"
    }),
    g: common_vendor.t($data.userInfo.phone || "未填写"),
    h: common_vendor.p({
      type: "person",
      size: "18",
      color: "#5a7dff"
    }),
    i: common_vendor.t($options.genderText),
    j: common_vendor.p({
      type: "calendar",
      size: "18",
      color: "#5a7dff"
    }),
    k: common_vendor.t($data.userInfo.birthday || "未填写"),
    l: common_vendor.o((...args) => $options.handleLogout && $options.handleLogout(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/profile/profile.js.map
