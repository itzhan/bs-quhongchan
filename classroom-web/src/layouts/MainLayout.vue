<script setup lang="ts">
import { ref, computed, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  NLayout, NLayoutHeader, NLayoutContent, NLayoutFooter, NMenu, NButton, NAvatar,
  NDropdown, NSpace, NTag, NIcon
} from 'naive-ui'
import {
  HomeOutline, BookOutline, LibraryOutline, CheckmarkCircleOutline,
  CreateOutline, StatsChartOutline, MegaphoneOutline, FolderOpenOutline,
  ChatbubblesOutline, PersonOutline, SchoolOutline, EaselOutline
} from '@vicons/ionicons5'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

function icon(comp: any) {
  return () => h(NIcon, null, { default: () => h(comp) })
}

const menuOptions = computed(() => {
  if (!userStore.isLoggedIn()) {
    return [
      { label: '首页', key: '/', icon: icon(HomeOutline) },
      { label: '课程中心', key: '/course', icon: icon(BookOutline) },
    ]
  }

  if (userStore.isTeacher()) {
    return [
      { label: '首页', key: '/', icon: icon(HomeOutline) },
      { label: '我的课程', key: '/my-course', icon: icon(LibraryOutline) },
      {
        label: '教学管理', key: '/teaching', icon: icon(EaselOutline),
        children: [
          { label: '签到管理', key: '/checkin', icon: icon(CheckmarkCircleOutline) },
          { label: '作业管理', key: '/assignment', icon: icon(CreateOutline) },
          { label: '成绩管理', key: '/grade', icon: icon(StatsChartOutline) },
        ]
      },
      {
        label: '课程资源', key: '/resource', icon: icon(FolderOpenOutline),
        children: [
          { label: '公告管理', key: '/announcement', icon: icon(MegaphoneOutline) },
          { label: '资料管理', key: '/material', icon: icon(FolderOpenOutline) },
          { label: '课程留言', key: '/message', icon: icon(ChatbubblesOutline) },
        ]
      },
    ]
  }

  // 学生端
  return [
    { label: '首页', key: '/', icon: icon(HomeOutline) },
    { label: '课程中心', key: '/course', icon: icon(BookOutline) },
    {
      label: '我的学习', key: '/study', icon: icon(SchoolOutline),
      children: [
        { label: '我的课程', key: '/my-course', icon: icon(LibraryOutline) },
        { label: '课堂签到', key: '/checkin', icon: icon(CheckmarkCircleOutline) },
        { label: '作业中心', key: '/assignment', icon: icon(CreateOutline) },
        { label: '成绩查询', key: '/grade', icon: icon(StatsChartOutline) },
      ]
    },
    {
      label: '课程资源', key: '/resource', icon: icon(FolderOpenOutline),
      children: [
        { label: '公告通知', key: '/announcement', icon: icon(MegaphoneOutline) },
        { label: '资料下载', key: '/material', icon: icon(FolderOpenOutline) },
        { label: '消息中心', key: '/message', icon: icon(ChatbubblesOutline) },
      ]
    },
  ]
})

const activeKey = computed(() => '/' + (route.path.split('/')[1] || ''))

function handleMenuUpdate(key: string) {
  router.push(key)
}

const userDropdownOptions = [
  { label: '个人中心', key: 'profile' },
  { type: 'divider', key: 'd1' },
  { label: '退出登录', key: 'logout' }
]

function handleUserAction(key: string) {
  if (key === 'profile') router.push('/profile')
  else if (key === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}

const roleLabel = computed(() => {
  if (userStore.isTeacher()) return '教师'
  if (userStore.isStudent()) return '学生'
  if (userStore.isAdmin()) return '管理员'
  return ''
})

const roleType = computed(() => {
  if (userStore.isTeacher()) return 'warning'
  if (userStore.isAdmin()) return 'error'
  return 'info'
})
</script>

<template>
  <n-layout class="layout-wrapper">
    <n-layout-header bordered class="header">
      <div class="header-inner container">
        <div class="logo" @click="router.push('/')">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 3L1 9L5 11.18V17.18L12 21L19 17.18V11.18L21 10.09V17H23V9L12 3ZM18.82 9L12 12.72L5.18 9L12 5.28L18.82 9ZM17 15.99L12 18.72L7 15.99V12.27L12 15L17 12.27V15.99Z" fill="#2563eb"/>
          </svg>
          <span>学生课堂管理系统</span>
        </div>
        <n-menu
          mode="horizontal"
          :value="activeKey"
          :options="menuOptions"
          @update:value="handleMenuUpdate"
          class="nav-menu"
        />
        <div class="header-right">
          <template v-if="userStore.isLoggedIn()">
            <n-dropdown :options="userDropdownOptions" @select="handleUserAction" trigger="click">
              <n-space align="center" :size="8" style="cursor: pointer;">
                <n-tag :type="roleType" size="small" round>{{ roleLabel }}</n-tag>
                <n-avatar :size="32" round :style="{ backgroundColor: userStore.isTeacher() ? '#f59e0b' : '#2563eb' }">
                  {{ userStore.realName?.charAt(0) || userStore.username?.charAt(0) || 'U' }}
                </n-avatar>
                <span class="username">{{ userStore.realName || userStore.username }}</span>
              </n-space>
            </n-dropdown>
          </template>
          <template v-else>
            <n-button type="primary" @click="router.push('/login')">登录</n-button>
          </template>
        </div>
      </div>
    </n-layout-header>

    <n-layout-content class="main-content">
      <div class="container page-container">
        <router-view />
      </div>
    </n-layout-content>

    <n-layout-footer bordered class="footer">
      <div class="container footer-inner">
        <p>&copy; 2025 学生课堂管理系统 - 基于Spring Boot的学生课堂管理系统的设计与实现</p>
      </div>
    </n-layout-footer>
  </n-layout>
</template>

<style scoped>
.layout-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #fff;
  box-shadow: var(--shadow);
}
.header-inner {
  display: flex;
  align-items: center;
  height: 60px;
  gap: 24px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
  color: var(--primary);
  cursor: pointer;
  white-space: nowrap;
  flex-shrink: 0;
}
.nav-menu {
  flex: 1;
  overflow-x: auto;
}
.nav-menu :deep(.n-menu-item-content-header) {
  font-size: 14px;
}
.header-right {
  flex-shrink: 0;
}
.username {
  font-size: 14px;
  color: var(--text);
}
.main-content {
  flex: 1;
}
.page-container {
  padding-top: 28px;
  padding-bottom: 40px;
  min-height: calc(100vh - 160px);
}
.footer {
  background: #fff;
  text-align: center;
  padding: 16px 0;
  font-size: 13px;
  color: var(--text-secondary);
}
</style>
