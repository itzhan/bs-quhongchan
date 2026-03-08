<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getMyCourses, quitCourse, createCourse, deleteCourse, getCourseMembers, joinCourse } from '@/api'
import {
  NCard, NGrid, NGi, NButton, NTag, NEmpty, NSpin, NSpace, NModal, NForm, NFormItem,
  NInput, NInputNumber, NDataTable, useMessage, useDialog
} from 'naive-ui'

const router = useRouter()
const userStore = useUserStore()
const message = useMessage()
const dialog = useDialog()
const loading = ref(false)
const dataList = ref<any[]>([])

// ---- 教师：创建课程 ----
const createVisible = ref(false)
const courseForm = ref({ courseName: '', description: '', semester: '', maxStudents: 200 })

// ---- 教师：查看成员 ----
const memberVisible = ref(false)
const memberCourse = ref<any>({})
const memberList = ref<any[]>([])

function fmt(dt: string) {
  if (!dt) return '-'
  return dt.replace('T', ' ').replace(/\.\d+$/, '')
}

const memberColumns = [
  { title: '姓名', key: 'studentName', width: 120, render: (row: any) => row.studentName || '-' },
  { title: '学号', key: 'studentNo', width: 160, render: (row: any) => row.studentNo || '-' },
  { title: '加入时间', key: 'joinTime', width: 180, render: (row: any) => fmt(row.joinTime) },
  { title: '状态', key: 'status', width: 100, render: (row: any) => h(NTag, { type: row.status === 1 ? 'success' : 'default', size: 'small' }, { default: () => row.status === 1 ? '正常' : '已退出' }) },
]

// ---- 学生：加入课程 ----
const joinVisible = ref(false)
const joinCode = ref('')

async function loadData() {
  loading.value = true
  try {
    const res: any = await getMyCourses()
    if (res.code === 200) {
      dataList.value = res.data?.records || res.data || []
    }
  } finally { loading.value = false }
}

async function handleCreateCourse() {
  if (!courseForm.value.courseName) { message.warning('请输入课程名称'); return }
  try {
    const res: any = await createCourse(courseForm.value)
    if (res.code === 200) {
      message.success('课程创建成功')
      createVisible.value = false
      courseForm.value = { courseName: '', description: '', semester: '', maxStudents: 200 }
      loadData()
    } else message.error(res.message)
  } catch (e: any) { message.error(e?.response?.data?.message || '创建失败') }
}

function handleDelete(item: any) {
  dialog.warning({
    title: '删除课程',
    content: `确定删除课程「${item.courseName}」吗？此操作不可恢复！`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        const res: any = await deleteCourse(item.id)
        if (res.code === 200) { message.success('已删除'); loadData() }
        else message.error(res.message)
      } catch { message.error('删除失败') }
    }
  })
}

async function showMembers(item: any) {
  memberCourse.value = item
  try {
    const res: any = await getCourseMembers(item.id, { page: 1, size: 100 })
    memberList.value = res.data?.records || []
  } catch { memberList.value = [] }
  memberVisible.value = true
}

function handleQuit(item: any) {
  dialog.warning({
    title: '退出课程',
    content: `确定退出课程「${item.courseName}」吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        const res: any = await quitCourse(item.id)
        if (res.code === 200) { message.success('已退出'); loadData() }
        else message.error(res.message)
      } catch { message.error('操作失败') }
    }
  })
}

async function handleJoin() {
  if (!joinCode.value.trim()) { message.warning('请输入班课码'); return }
  try {
    const res: any = await joinCourse(joinCode.value.trim())
    if (res.code === 200) {
      message.success('加入成功')
      joinVisible.value = false
      joinCode.value = ''
      loadData()
    } else message.error(res.message)
  } catch (e: any) { message.error(e?.response?.data?.message || '加入失败') }
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="page-header">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <div>
          <h2>我的课程</h2>
          <p v-if="userStore.isTeacher()">管理您创建的课程</p>
          <p v-else>查看已加入的课程</p>
        </div>
        <n-space>
          <n-button v-if="userStore.isTeacher()" type="primary" @click="createVisible = true">+ 创建课程</n-button>
          <n-button v-if="userStore.isStudent()" type="primary" @click="joinVisible = true">+ 加入课程</n-button>
        </n-space>
      </div>
    </div>

    <n-spin :show="loading">
      <n-grid v-if="dataList.length" :cols="3" :x-gap="20" :y-gap="20" responsive="screen" item-responsive>
        <n-gi v-for="item in dataList" :key="item.id" span="3 m:1">
          <n-card :bordered="false" hoverable class="course-card">
            <h3 @click="router.push(`/course/${item.id}`)" style="cursor: pointer;">{{ item.courseName }}</h3>
            <n-space :size="6" style="margin: 8px 0;">
              <n-tag type="info" size="small" round>{{ item.semester || '暂无学期' }}</n-tag>
              <n-tag v-if="item.courseCode" size="small" round>班课码: {{ item.courseCode }}</n-tag>
            </n-space>
            <p style="font-size: 13px; color: var(--text-secondary); margin-bottom: 16px;">{{ item.description || '暂无描述' }}</p>
            <n-space>
              <n-button size="small" type="primary" secondary @click="router.push(`/course/${item.id}`)">查看详情</n-button>
              <template v-if="userStore.isTeacher()">
                <n-button size="small" type="info" secondary @click="showMembers(item)">查看成员</n-button>
                <n-button size="small" type="error" quaternary @click="handleDelete(item)">删除课程</n-button>
              </template>
              <n-button v-if="userStore.isStudent()" size="small" type="error" quaternary @click="handleQuit(item)">退出课程</n-button>
            </n-space>
          </n-card>
        </n-gi>
      </n-grid>
      <n-empty v-else :description="userStore.isTeacher() ? '暂未创建课程' : '暂未加入任何课程'" style="padding: 60px 0;" />
    </n-spin>

    <!-- 教师：创建课程弹窗 -->
    <n-modal v-model:show="createVisible" preset="dialog" title="创建课程" positive-text="创建" negative-text="取消" @positive-click="handleCreateCourse" style="width: 520px;">
      <n-form label-placement="left" label-width="80" style="margin-top: 16px;">
        <n-form-item label="课程名称" required><n-input v-model:value="courseForm.courseName" placeholder="请输入课程名称" /></n-form-item>
        <n-form-item label="学期"><n-input v-model:value="courseForm.semester" placeholder="如 2025-2026-1" /></n-form-item>
        <n-form-item label="最大人数"><n-input-number v-model:value="courseForm.maxStudents" :min="1" :max="500" style="width: 100%;" /></n-form-item>
        <n-form-item label="课程描述"><n-input v-model:value="courseForm.description" type="textarea" :rows="3" placeholder="请输入课程描述" /></n-form-item>
      </n-form>
    </n-modal>

    <!-- 教师：查看成员弹窗 -->
    <n-modal v-model:show="memberVisible" preset="card" :title="`${memberCourse.courseName} - 课程成员`" style="width: 600px;">
      <n-data-table :columns="memberColumns" :data="memberList" :bordered="false" size="small" />
    </n-modal>

    <!-- 学生：加入课程弹窗 -->
    <n-modal v-model:show="joinVisible" preset="dialog" title="加入课程" positive-text="加入" negative-text="取消" @positive-click="handleJoin" style="width: 400px;">
      <n-input v-model:value="joinCode" placeholder="请输入6位班课码" size="large" style="margin-top: 16px;" />
    </n-modal>
  </div>
</template>

<style scoped>
.course-card { border-radius: var(--radius); }
.course-card h3 { font-size: 16px; font-weight: 600; }
</style>
