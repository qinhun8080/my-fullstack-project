<template>
  <router-view />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 把 :root 变量放这里 */
:root {
  --sidebar-bg: #2c3e50;
  --sidebar-text: #ecf0f1;
  --sidebar-text-active: #ffffff;
  --sidebar-active-bg: #3498db;
  --main-bg: #f4f7f6;
  --card-bg: #ffffff;
  --header-bg: #ffffff;
  --text-primary: #333;
  --text-secondary: #7f8c8d;
  --border-color: #e0e0e0;
  --primary-btn: #3498db;
  --primary-btn-hover: #2980b9;
  --danger-btn: #e74c3c;
  --danger-btn-hover: #c0392b;
  --warning-btn: #f39c12;
  --warning-btn-hover: #e67e22;
}

html, body, #app {
  width: 100%;
  height: 100vh;
  margin: 0;
  padding: 0;
  font-family: 'Inter', Arial, sans-serif;
  background-color: var(--main-bg, #f0f2f5);
  overflow-x: hidden;
}
</style>
