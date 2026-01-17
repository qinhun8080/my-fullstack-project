<template>
  <canvas ref="captchaCanvas" width="100" height="40" @click="generateCaptcha" title="点击换一张"
    class="captcha-canvas"></canvas>
</template>

<script setup>
import { ref, onMounted, defineEmits, defineExpose } from 'vue'

const emit = defineEmits(['code-updated'])
const captchaCanvas = ref(null)

const generateCaptcha = () => {
  if (!captchaCanvas.value) return
  const ctx = captchaCanvas.value.getContext('2d')
  const width = 100
  const height = 40

  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789'.split('')

  // 1. 背景
  ctx.fillStyle = '#f3f4f6'
  ctx.fillRect(0, 0, width, height)

  let generatedCode = ''
  let spacedCode = ''
  const random = (max) => Math.floor(Math.random() * max)

  // 2. 生成随机验证码
  for (let i = 0; i < 4; i++) {
    const char = chars[random(chars.length)]
    generatedCode += char
    spacedCode += char + ' '
  }

  // 3. 通知父组件并打印验证码
  emit('code-updated', generatedCode)
  console.log('当前验证码：', generatedCode) // ✅ 输出到控制台

  // 4. 绘制文字
  ctx.fillStyle = '#333'
  ctx.font = "italic bold 22px 'Songti SC', 'SimSun', 'STSong', serif"
  ctx.textBaseline = 'middle'
  ctx.fillText(spacedCode, 8, height / 2)

  // 5. 干扰点
  for (let i = 0; i < 25; i++) {
    ctx.fillStyle = `rgba(${random(255)}, ${random(255)}, ${random(255)}, 0.5)`
    ctx.beginPath()
    ctx.arc(random(width), random(height), 1, 0, 2 * Math.PI)
    ctx.fill()
  }

  // 6. 干扰线
  for (let i = 0; i < 4; i++) {
    ctx.strokeStyle = `rgba(${random(255)}, ${random(255)}, ${random(255)}, 0.5)`
    ctx.beginPath()
    ctx.moveTo(random(width), random(height))
    ctx.lineTo(random(width), random(height))
    ctx.stroke()
  }
}

// 页面加载时生成初始验证码
onMounted(() => {
  generateCaptcha()
})

// 暴露方法供父组件调用
defineExpose({
  generateCaptcha
})
</script>

<style scoped>
.captcha-canvas {
  cursor: pointer;
  border-radius: 0.5rem;
  border: 1px solid #D1D5DB;
  height: 40px;
  width: 100px;
  user-select: none;
  background: #f9fafb;
}
</style>
