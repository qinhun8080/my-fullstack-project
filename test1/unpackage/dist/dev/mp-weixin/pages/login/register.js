"use strict";
const common_vendor = require("../../common/vendor.js");
const API_BASE_URL = "http://127.0.0.1:8081/api";
const _sfc_main = {
  data() {
    return {
      form: {
        username: "",
        nickname: "",
        gender: "",
        // 传给后端的数值 (1, 0, 2)
        birthday: "",
        phone: "",
        password: "",
        confirmPassword: ""
      },
      // 性别选择器配置
      genderOptions: [
        { label: "男", value: 1 },
        { label: "女", value: 0 },
        { label: "其他", value: 2 }
      ],
      genderIndex: -1,
      // picker 的索引
      genderText: "",
      // 显示在输入框的文字
      showPassword: false,
      showConfirmPassword: false,
      isSubmitting: false
    };
  },
  methods: {
    goToLogin() {
      common_vendor.index.navigateBack();
    },
    // 处理性别选择
    bindGenderChange(e) {
      this.genderIndex = e.detail.value;
      const selected = this.genderOptions[this.genderIndex];
      this.genderText = selected.label;
      this.form.gender = selected.value;
    },
    // 处理日期选择
    bindDateChange(e) {
      this.form.birthday = e.detail.value;
    },
    handleUserRegister() {
      if (!this.form.username)
        return common_vendor.index.showToast({ title: "请输入账号", icon: "none" });
      if (!this.form.password)
        return common_vendor.index.showToast({ title: "请输入密码", icon: "none" });
      if (this.form.password !== this.form.confirmPassword) {
        return common_vendor.index.showToast({ title: "两次密码不一致", icon: "none" });
      }
      this.isSubmitting = true;
      common_vendor.index.request({
        url: API_BASE_URL + "/user/register",
        method: "POST",
        data: {
          username: this.form.username,
          nickname: this.form.nickname,
          gender: this.form.gender === "" ? null : this.form.gender,
          // 确保空值传null
          birthday: this.form.birthday,
          phone: this.form.phone,
          password: this.form.password
        },
        success: (res) => {
          if (res.data && res.data.success) {
            common_vendor.index.showToast({ title: "注册成功", icon: "success" });
            setTimeout(() => {
              this.goToLogin();
            }, 1500);
          } else {
            common_vendor.index.showToast({ title: res.data.message || "注册失败", icon: "none" });
          }
        },
        fail: (err) => {
          common_vendor.index.__f__("error", "at pages/login/register.vue:153", err);
          common_vendor.index.showToast({ title: "网络请求失败", icon: "none" });
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
    c: $data.form.nickname,
    d: common_vendor.o(($event) => $data.form.nickname = $event.detail.value),
    e: common_vendor.t($data.genderText || "请选择性别"),
    f: !$data.genderText ? 1 : "",
    g: common_vendor.o((...args) => $options.bindGenderChange && $options.bindGenderChange(...args)),
    h: $data.genderIndex,
    i: $data.genderOptions,
    j: common_vendor.t($data.form.birthday || "请选择出生日期"),
    k: !$data.form.birthday ? 1 : "",
    l: $data.form.birthday,
    m: common_vendor.o((...args) => $options.bindDateChange && $options.bindDateChange(...args)),
    n: $data.form.phone,
    o: common_vendor.o(($event) => $data.form.phone = $event.detail.value),
    p: !$data.showPassword,
    q: $data.form.password,
    r: common_vendor.o(($event) => $data.form.password = $event.detail.value),
    s: common_vendor.p({
      type: $data.showPassword ? "eye-filled" : "eye-slash-filled",
      size: "20",
      color: "#6b7280"
    }),
    t: common_vendor.o(($event) => $data.showPassword = !$data.showPassword),
    v: !$data.showConfirmPassword,
    w: $data.form.confirmPassword,
    x: common_vendor.o(($event) => $data.form.confirmPassword = $event.detail.value),
    y: common_vendor.p({
      type: $data.showConfirmPassword ? "eye-filled" : "eye-slash-filled",
      size: "20",
      color: "#6b7280"
    }),
    z: common_vendor.o(($event) => $data.showConfirmPassword = !$data.showConfirmPassword),
    A: common_vendor.t($data.isSubmitting ? "正在注册..." : "注 册"),
    B: common_vendor.o((...args) => $options.handleUserRegister && $options.handleUserRegister(...args)),
    C: $data.isSubmitting,
    D: $data.isSubmitting ? 1 : "",
    E: common_vendor.o((...args) => $options.goToLogin && $options.goToLogin(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-838b72c9"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/login/register.js.map
