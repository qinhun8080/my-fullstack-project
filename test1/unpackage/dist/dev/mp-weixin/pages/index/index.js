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
      // [修改点 2] 初始化经纬度数据 (默认给一个坐标，防止报错)
      latitude: 39.909,
      longitude: 116.397,
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
    // [修改点 3] 新增打开地图的方法
    openLocation() {
      const lat = parseFloat(this.latitude);
      const lon = parseFloat(this.longitude);
      if (isNaN(lat) || isNaN(lon)) {
        common_vendor.index.showToast({ title: "无有效定位信息", icon: "none" });
        return;
      }
      common_vendor.index.openLocation({
        latitude: lat,
        longitude: lon,
        name: "导盲杖当前位置",
        // 地点名称
        scale: 18
        // 缩放比例 (5-18)
      });
    },
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
            if (res.data.latitude && res.data.longitude) {
              this.latitude = res.data.latitude;
              this.longitude = res.data.longitude;
            }
            if (res.data.historyAlerts) {
              this.historyAlerts = res.data.historyAlerts;
            }
          }
        },
        fail: (err) => {
          common_vendor.index.__f__("error", "at pages/index/index.vue:195", "API请求失败:", err);
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
    i: common_vendor.p({
      type: "paperplane-filled",
      size: "24",
      color: "#19be6b"
    }),
    j: common_vendor.o((...args) => $options.openLocation && $options.openLocation(...args)),
    k: common_vendor.t($data.distance),
    l: $data.distance !== "--"
  }, $data.distance !== "--" ? {} : {}, {
    m: $options.isDanger ? 1 : "",
    n: common_vendor.p({
      type: "phone-filled",
      size: "24",
      color: "#3498db"
    }),
    o: common_vendor.o((...args) => $options.openCallModal && $options.openCallModal(...args)),
    p: $data.historyAlerts.length > 0
  }, $data.historyAlerts.length > 0 ? {
    q: common_vendor.t($data.historyAlerts.length)
  } : {}, {
    r: common_vendor.o((...args) => $options.openHistoryModal && $options.openHistoryModal(...args)),
    s: $data.showHistoryModal || $data.showCallModal
  }, $data.showHistoryModal || $data.showCallModal ? {
    t: common_vendor.o((...args) => $options.closeAllModals && $options.closeAllModals(...args))
  } : {}, {
    v: $data.showHistoryModal
  }, $data.showHistoryModal ? common_vendor.e({
    w: common_vendor.o($options.closeAllModals),
    x: common_vendor.p({
      type: "closeempty",
      size: "24",
      color: "#999"
    }),
    y: $data.historyAlerts.length === 0
  }, $data.historyAlerts.length === 0 ? {} : {}, {
    z: common_vendor.f($data.historyAlerts, (item, index, i0) => {
      return {
        a: "9658e22c-6-" + i0,
        b: common_vendor.t(item),
        c: index
      };
    }),
    A: common_vendor.p({
      type: "info-filled",
      color: "#ff4d4f",
      size: "18"
    }),
    B: common_vendor.o((...args) => $options.closeAllModals && $options.closeAllModals(...args))
  }) : {}, {
    C: $data.showCallModal
  }, $data.showCallModal ? {
    D: common_vendor.p({
      type: "person-filled",
      size: "50",
      color: "#fff"
    }),
    E: common_vendor.p({
      type: "phone-filled",
      size: "30",
      color: "#fff"
    }),
    F: common_vendor.o((...args) => $options.closeAllModals && $options.closeAllModals(...args))
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
