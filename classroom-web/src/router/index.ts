import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/home/index.vue'), meta: { title: '首页' } },
      { path: 'course', name: 'Course', component: () => import('@/views/course/index.vue'), meta: { title: '课程中心', auth: true } },
      { path: 'course/:id', name: 'CourseDetail', component: () => import('@/views/course/detail.vue'), meta: { title: '课程详情', auth: true } },
      { path: 'my-course', name: 'MyCourse', component: () => import('@/views/my-course/index.vue'), meta: { title: '我的课程', auth: true } },
      { path: 'checkin', name: 'Checkin', component: () => import('@/views/checkin/index.vue'), meta: { title: '课堂签到', auth: true } },
      { path: 'assignment', name: 'Assignment', component: () => import('@/views/assignment/index.vue'), meta: { title: '作业中心', auth: true } },
      { path: 'grade', name: 'Grade', component: () => import('@/views/grade/index.vue'), meta: { title: '成绩查询', auth: true } },
      { path: 'announcement', name: 'Announcement', component: () => import('@/views/announcement/index.vue'), meta: { title: '公告通知', auth: true } },
      { path: 'material', name: 'Material', component: () => import('@/views/material/index.vue'), meta: { title: '资料下载', auth: true } },
      { path: 'message', name: 'Message', component: () => import('@/views/message/index.vue'), meta: { title: '消息中心', auth: true } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/profile/index.vue'), meta: { title: '个人中心', auth: true } }
    ]
  },
  { path: '/login', name: 'Login', component: () => import('@/views/login/index.vue'), meta: { title: '登录' } }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  document.title = (to.meta.title as string || '学生课堂管理系统') + ' - 学生课堂管理系统'
  if (to.meta.auth) {
    const userStore = useUserStore()
    if (!userStore.isLoggedIn()) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
  }
  next()
})

export default router
