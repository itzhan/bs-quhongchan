<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getProfile, updateProfile } from '@/api'
import { NCard, NForm, NFormItem, NInput, NButton, NAvatar, NDescriptions, NDescriptionsItem, NTag, NSpace, useMessage } from 'naive-ui'

const userStore = useUserStore()
const message = useMessage()
const loading = ref(false)
const editing = ref(false)
const profile = ref<any>({})
const form = reactive({ realName: '', email: '', phone: '' })

function fmt(dt: string) {
  if (!dt) return '-'
  return dt.replace('T', ' ').replace(/\.\d+$/, '')
}

async function loadProfile() {
  loading.value = true
  try {
    const res: any = await getProfile()
    if (res.code === 200) {
      profile.value = res.data
      form.realName = res.data.realName || ''
      form.email = res.data.email || ''
      form.phone = res.data.phone || ''
    }
  } finally { loading.value = false }
}

async function handleSave() {
  try {
    const res: any = await updateProfile(form)
    if (res.code === 200) {
      message.success('修改成功')
      editing.value = false
      loadProfile()
    } else message.error(res.message)
  } catch { message.error('修改失败') }
}

onMounted(() => loadProfile())
</script>

<template>
  <div>
    <div class="page-header">
      <h2>个人中心</h2>
      <p>查看和修改个人信息</p>
    </div>

    <n-card :bordered="false" style="border-radius: var(--radius); max-width: 680px;">
      <div style="display: flex; align-items: center; gap: 20px; margin-bottom: 28px;">
        <n-avatar :size="72" round :style="{ backgroundColor: userStore.isTeacher() ? '#f59e0b' : '#2563eb', fontSize: '28px' }">
          {{ profile.realName?.charAt(0) || userStore.username?.charAt(0) || 'U' }}
        </n-avatar>
        <div>
          <h3 style="font-size: 20px; font-weight: 600;">{{ profile.realName || profile.username }}</h3>
          <n-space :size="8" style="margin-top: 6px;">
            <n-tag :type="profile.role === 'ADMIN' ? 'error' : profile.role === 'TEACHER' ? 'warning' : 'success'" size="small">
              {{ profile.role === 'ADMIN' ? '管理员' : profile.role === 'TEACHER' ? '教师' : '学生' }}
            </n-tag>
          </n-space>
        </div>
      </div>

      <template v-if="!editing">
        <n-descriptions bordered :column="1" label-placement="left">
          <n-descriptions-item label="用户名">{{ profile.username || '-' }}</n-descriptions-item>
          <n-descriptions-item label="姓名">{{ profile.realName || '-' }}</n-descriptions-item>
          <n-descriptions-item label="角色">{{ profile.role === 'TEACHER' ? '教师' : profile.role === 'STUDENT' ? '学生' : profile.role || '-' }}</n-descriptions-item>
          <n-descriptions-item label="邮箱">{{ profile.email || '-' }}</n-descriptions-item>
          <n-descriptions-item label="手机号">{{ profile.phone || '-' }}</n-descriptions-item>
          <n-descriptions-item label="注册时间">{{ fmt(profile.createTime) }}</n-descriptions-item>
        </n-descriptions>
        <n-button type="primary" style="margin-top: 20px;" @click="editing = true">编辑信息</n-button>
      </template>

      <template v-else>
        <n-form label-placement="left" label-width="80">
          <n-form-item label="姓名"><n-input v-model:value="form.realName" placeholder="请输入姓名" /></n-form-item>
          <n-form-item label="邮箱"><n-input v-model:value="form.email" placeholder="请输入邮箱" /></n-form-item>
          <n-form-item label="手机号"><n-input v-model:value="form.phone" placeholder="请输入手机号" /></n-form-item>
        </n-form>
        <n-space>
          <n-button type="primary" @click="handleSave">保存</n-button>
          <n-button @click="editing = false">取消</n-button>
        </n-space>
      </template>
    </n-card>
  </div>
</template>
