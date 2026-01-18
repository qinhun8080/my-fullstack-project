"use strict";
const common_vendor = require("../../common/vendor.js");
const API_BASE_URL = "http://127.0.0.1:8081/api";
const _sfc_main = {
  data() {
    return {
      temp: "--",
      humi: "--",
      distance: "--",
      deviceStatus: "查询中...",
      historyAlerts: [],
      showHistoryModal: false,
      showCallModal: false,
      dataTimer: null
    };
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
    }, 5e3);
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
      common_vendor.index.request({
        url: API_BASE_URL + "/device-data",
        method: "GET",
        success: (res) => {
          if (res.data) {
            this.deviceStatus = res.data.deviceStatus || "N/A";
            this.temp = res.data.temp;
            this.humi = res.data.humi;
            this.distance = res.data.distance;
            if (res.data.historyAlerts) {
              this.historyAlerts = res.data.historyAlerts;
            }
          }
        },
        fail: (err) => {
          common_vendor.index.__f__("error", "at pages/index/index.vue:160", "API请求失败:", err);
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
  return common_vendor.e({
    a: common_vendor.p({
      type: "map-pin-ellipse",
      size: "22",
      color: "#333"
    }),
    b: common_vendor.p({
      type: "search",
      size: "18",
      color: "#999"
    }),
    c: common_vendor.t($data.deviceStatus),
    d: common_vendor.t($data.temp),
    e: $data.temp !== "--"
  }, $data.temp !== "--" ? {} : {}, {
    f: common_vendor.p({
      type: "droplet",
      size: "34",
      color: "#a0cfff"
    }),
    g: common_vendor.t($data.humi),
    h: $data.humi !== "--"
  }, $data.humi !== "--" ? {} : {}, {
    i: common_vendor.t($data.distance),
    j: $data.distance !== "--"
  }, $data.distance !== "--" ? {} : {}, {
    k: $options.isDanger ? 1 : "",
    l: common_vendor.p({
      type: "phone-filled",
      size: "24",
      color: "#3498db"
    }),
    m: common_vendor.o((...args) => $options.openCallModal && $options.openCallModal(...args)),
    n: $data.historyAlerts.length > 0
  }, $data.historyAlerts.length > 0 ? {
    o: common_vendor.t($data.historyAlerts.length)
  } : {}, {
    p: common_vendor.o((...args) => $options.openHistoryModal && $options.openHistoryModal(...args)),
    q: $data.showHistoryModal || $data.showCallModal
  }, $data.showHistoryModal || $data.showCallModal ? {
    r: common_vendor.o((...args) => $options.closeAllModals && $options.closeAllModals(...args))
  } : {}, {
    s: $data.showHistoryModal
  }, $data.showHistoryModal ? common_vendor.e({
    t: common_vendor.o($options.closeAllModals),
    v: common_vendor.p({
      type: "closeempty",
      size: "24",
      color: "#999"
    }),
    w: $data.historyAlerts.length === 0
  }, $data.historyAlerts.length === 0 ? {} : {}, {
    x: common_vendor.f($data.historyAlerts, (item, index, i0) => {
      return {
        a: "2e764eba-5-" + i0,
        b: common_vendor.t(item),
        c: index
      };
    }),
    y: common_vendor.p({
      type: "info-filled",
      color: "#ff4d4f",
      size: "18"
    }),
    z: common_vendor.o((...args) => $options.closeAllModals && $options.closeAllModals(...args))
  }) : {}, {
    A: $data.showCallModal
  }, $data.showCallModal ? {
    B: common_vendor.p({
      type: "person-filled",
      size: "50",
      color: "#fff"
    }),
    C: common_vendor.p({
      type: "phone-filled",
      size: "30",
      color: "#fff"
    }),
    D: common_vendor.o((...args) => $options.closeAllModals && $options.closeAllModals(...args))
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
