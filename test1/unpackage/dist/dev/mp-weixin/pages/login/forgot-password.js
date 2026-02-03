"use strict";
const common_vendor = require("../../common/vendor.js");
const API_BASE_URL = "http://127.0.0.1:8081/api";
const _sfc_main = {
  data() {
    return {
      form: {
        username: "",
        phone: "",
        newPassword: "",
        confirmPassword: ""
      },
      showPassword: false,
      showConfirmPassword: false,
      isSubmitting: false
    };
  },
  methods: {
    goBackLogin() {
      common_vendor.index.navigateBack();
    },
    handleReset() {
      if (!this.form.username || !this.form.phone) {
        common_vendor.index.showToast({ title: "请填写用户名和手机号", icon: "none" });
        return;
      }
      if (!this.form.newPassword || !this.form.confirmPassword) {
        common_vendor.index.showToast({ title: "请输入并确认新密码", icon: "none" });
        return;
      }
      if (this.form.newPassword !== this.form.confirmPassword) {
        common_vendor.index.showToast({ title: "两次输入的密码不一致", icon: "none" });
        return;
      }
      this.isSubmitting = true;
      common_vendor.index.request({
        url: API_BASE_URL + "/user/reset-password",
        method: "POST",
        data: {
          username: this.form.username,
          phone: this.form.phone,
          newPassword: this.form.newPassword
        },
        success: (res) => {
          if (res.data && res.data.success) {
            common_vendor.index.showToast({ title: "重置成功", icon: "success" });
            setTimeout(() => {
              common_vendor.index.navigateBack();
            }, 1500);
          } else {
            common_vendor.index.showToast({
              title: res.data.message || "重置失败",
              icon: "none",
              duration: 3e3
              // 错误信息多停留一会
            });
          }
        },
        fail: () => {
          common_vendor.index.showToast({ title: "连接服务器失败", icon: "none" });
        },
        complete: () => {
          this.isSubmitting = false;
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
    a: $data.form.username,
    b: common_vendor.o(($event) => $data.form.username = $event.detail.value),
    c: $data.form.phone,
    d: common_vendor.o(($event) => $data.form.phone = $event.detail.value),
    e: !$data.showPassword,
    f: $data.form.newPassword,
    g: common_vendor.o(($event) => $data.form.newPassword = $event.detail.value),
    h: common_vendor.p({
      type: $data.showPassword ? "eye-filled" : "eye-slash-filled",
      size: "20",
      color: "#c0c4cc"
    }),
    i: common_vendor.o(($event) => $data.showPassword = !$data.showPassword),
    j: !$data.showConfirmPassword,
    k: $data.form.confirmPassword,
    l: common_vendor.o(($event) => $data.form.confirmPassword = $event.detail.value),
    m: common_vendor.p({
      type: $data.showConfirmPassword ? "eye-filled" : "eye-slash-filled",
      size: "20",
      color: "#c0c4cc"
    }),
    n: common_vendor.o(($event) => $data.showConfirmPassword = !$data.showConfirmPassword),
    o: common_vendor.t($data.isSubmitting ? "重置中..." : "确认重置"),
    p: common_vendor.o((...args) => $options.handleReset && $options.handleReset(...args)),
    q: $data.isSubmitting,
    r: $data.isSubmitting ? 1 : "",
    s: common_vendor.o((...args) => $options.goBackLogin && $options.goBackLogin(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-962ceba2"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/login/forgot-password.js.map
