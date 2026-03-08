<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login, register } from '@/api'
import { NCard, NForm, NFormItem, NInput, NButton, NTabs, NTabPane, NSpace, NSelect, useMessage } from 'naive-ui'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const message = useMessage()
const activeTab = ref('login')
const loading = ref(false)

const loginForm = reactive({ username: '', password: '' })
const registerForm = reactive({ username: '', password: '', confirmPassword: '', realName: '', studentId: '', role: 'STUDENT', email: '', phone: '' })

const roleOptions = [
  { label: '学生', value: 'STUDENT' },
  { label: '教师', value: 'TEACHER' }
]

async function handleLogin() {
  if (!loginForm.username || !loginForm.password) { message.warning('请输入用户名和密码'); return }
  loading.value = true
  try {
    const res: any = await login(loginForm)
    if (res.code === 200) {
      userStore.setUser(res.data)
      message.success('登录成功')
      const redirect = (route.query.redirect as string) || '/'
      router.push(redirect)
    } else {
      message.error(res.message || '登录失败')
    }
  } catch (e: any) {
    message.error(e?.response?.data?.message || '登录失败')
  } finally { loading.value = false }
}

async function handleRegister() {
  if (registerForm.password !== registerForm.confirmPassword) { message.warning('两次密码不一致'); return }
  if (!registerForm.username || !registerForm.password || !registerForm.realName) { message.warning('请填写必填项'); return }
  loading.value = true
  try {
    const res: any = await register(registerForm)
    if (res.code === 200) {
      message.success('注册成功，请登录')
      activeTab.value = 'login'
      loginForm.username = registerForm.username
    } else {
      message.error(res.message || '注册失败')
    }
  } catch (e: any) {
    message.error(e?.response?.data?.message || '注册失败')
  } finally { loading.value = false }
}
</script>

<template>
  <div class="login-page">
    <div class="login-left">
      <div class="login-brand">
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none"><path d="M12 3L1 9L5 11.18V17.18L12 21L19 17.18V11.18L21 10.09V17H23V9L12 3ZM18.82 9L12 12.72L5.18 9L12 5.28L18.82 9ZM17 15.99L12 18.72L7 15.99V12.27L12 15L17 12.27V15.99Z" fill="#fff"/></svg>
        <h1>学生课堂管理系统</h1>
        <p>基于 Spring Boot 的智慧教学管理平台</p>
      </div>
    </div>
    <div class="login-right">
      <n-card class="login-card" :bordered="false">
        <n-tabs v-model:value="activeTab" type="segment" animated>
          <n-tab-pane name="login" tab="登录">
            <n-form class="form" @submit.prevent="handleLogin">
              <n-form-item label="用户名"><n-input v-model:value="loginForm.username" placeholder="请输入用户名" size="large" /></n-form-item>
              <n-form-item label="密码"><n-input v-model:value="loginForm.password" type="password" show-password-on="click" placeholder="请输入密码" size="large" @keyup.enter="handleLogin" /></n-form-item>
              <n-button type="primary" block size="large" :loading="loading" @click="handleLogin" style="margin-top: 8px;">登录</n-button>
            </n-form>
          </n-tab-pane>
          <n-tab-pane name="register" tab="注册">
            <n-form class="form" @submit.prevent="handleRegister">
              <n-form-item label="用户名" required><n-input v-model:value="registerForm.username" placeholder="请输入用户名" /></n-form-item>
              <n-form-item label="姓名" required><n-input v-model:value="registerForm.realName" placeholder="请输入真实姓名" /></n-form-item>
              <n-form-item label="学号/工号"><n-input v-model:value="registerForm.studentId" placeholder="请输入学号或工号" /></n-form-item>
              <n-form-item label="角色"><n-select v-model:value="registerForm.role" :options="roleOptions" /></n-form-item>
              <n-form-item label="密码" required><n-input v-model:value="registerForm.password" type="password" placeholder="请输入密码" /></n-form-item>
              <n-form-item label="确认密码" required><n-input v-model:value="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" /></n-form-item>
              <n-button type="primary" block size="large" :loading="loading" @click="handleRegister" style="margin-top: 8px;">注册</n-button>
            </n-form>
          </n-tab-pane>
        </n-tabs>
      </n-card>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  min-height: 100vh;
}
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #1e40af 0%, #2563eb 50%, #3b82f6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
.login-brand { text-align: center; }
.login-brand h1 { font-size: 28px; margin-top: 16px; }
.login-brand p { margin-top: 8px; opacity: 0.85; font-size: 15px; }
.login-right {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: var(--bg);
}
.login-card {
  width: 100%;
  border-radius: var(--radius);
  box-shadow: var(--shadow-md);
}
.form { padding-top: 16px; }

@media (max-width: 768px) {
  .login-left { display: none; }
  .login-right { width: 100%; padding: 24px; }
}
</style>
