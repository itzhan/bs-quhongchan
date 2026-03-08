<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { useUserStore } from '@/stores/user'
import { getCheckinPage, doCheckin, createCheckin, endCheckin, getCheckinRecords, getMyCourses } from '@/api'
import {
  NCard, NDataTable, NButton, NTag, NInputNumber, NSpace, NSpin, NModal, NForm,
  NFormItem, NInput, NSelect, NDatePicker, useMessage
} from 'naive-ui'

const userStore = useUserStore()
const message = useMessage()
const loading = ref(false)
const dataList = ref<any[]>([])
const courseId = ref<number | null>(null)
const filterCourseOptions = ref<any[]>([])

async function loadFilterCourses() {
  try {
    const res: any = await getMyCourses()
    const list = res.data?.records || res.data || []
    filterCourseOptions.value = [{ label: '全部课程', value: null }, ...list.map((c: any) => ({ label: c.courseName, value: c.id }))]
  } catch { filterCourseOptions.value = [] }
}

function fmt(dt: string) {
  if (!dt) return '-'
  return dt.replace('T', ' ').replace(/\.\d+$/, '')
}

// ---- 教师：发起签到 ----
const createVisible = ref(false)
const courseOptions = ref<any[]>([])
const checkinForm = ref({
  courseId: null as number | null,
  title: '',
  checkinType: 1,
  startTime: null as number | null,
  endTime: null as number | null
})
const typeOptions = [
  { label: '普通签到', value: 1 },
  { label: '定位签到', value: 2 },
  { label: '手势签到', value: 3 }
]

// ---- 教师：查看签到记录 ----
const recordVisible = ref(false)
const recordList = ref<any[]>([])
const recordTitle = ref('')
const recordColumns = [
  { title: '姓名', key: 'studentName', width: 100, render: (row: any) => row.studentName || '-' },
  { title: '学号', key: 'studentNo', width: 140, render: (row: any) => row.studentNo || '-' },
  { title: '签到时间', key: 'checkinTime', width: 180, render: (row: any) => fmt(row.checkinTime) },
  { title: '状态', key: 'status', width: 100, render: (row: any) => {
    const map: Record<number, { type: string; label: string }> = {
      0: { type: 'default', label: '未签到' },
      1: { type: 'success', label: '已签到' },
      2: { type: 'warning', label: '迟到' },
      3: { type: 'info', label: '请假' }
    }
    const s = map[row.status] || map[0]
    return h(NTag, { type: s.type as any, size: 'small' }, { default: () => s.label })
  }},
]

// ---- 学生列 ----
const studentColumns = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '签到标题', key: 'title', width: 180 },
  { title: '课程ID', key: 'courseId', width: 100 },
  { title: '签到类型', key: 'checkinType', width: 120, render: (row: any) => {
    const map: Record<number, string> = { 1: '普通签到', 2: '定位签到', 3: '手势签到' }
    return map[row.checkinType] || '未知'
  }},
  { title: '开始时间', key: 'startTime', width: 170, render: (row: any) => fmt(row.startTime) },
  { title: '截止时间', key: 'endTime', width: 170, render: (row: any) => fmt(row.endTime) },
  { title: '状态', key: 'status', width: 90, render: (row: any) => h(NTag, { type: row.status === 1 ? 'success' : 'default', size: 'small' }, { default: () => row.status === 1 ? '进行中' : '已结束' }) },
  { title: '操作', key: 'action', width: 120, render: (row: any) =>
    row.status === 1
      ? h(NButton, { size: 'small', type: 'primary', onClick: () => handleStudentCheckin(row) }, { default: () => '立即签到' })
      : h('span', { style: 'color: #999;' }, '已结束')
  }
]

// ---- 教师列 ----
const teacherColumns = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '签到标题', key: 'title', width: 160 },
  { title: '课程ID', key: 'courseId', width: 90 },
  { title: '类型', key: 'checkinType', width: 100, render: (row: any) => {
    const map: Record<number, string> = { 1: '普通签到', 2: '定位签到', 3: '手势签到' }
    return map[row.checkinType] || '未知'
  }},
  { title: '开始时间', key: 'startTime', width: 170, render: (row: any) => fmt(row.startTime) },
  { title: '截止时间', key: 'endTime', width: 170, render: (row: any) => fmt(row.endTime) },
  { title: '状态', key: 'status', width: 90, render: (row: any) => h(NTag, { type: row.status === 1 ? 'success' : 'default', size: 'small' }, { default: () => row.status === 1 ? '进行中' : '已结束' }) },
  { title: '操作', key: 'action', width: 180, render: (row: any) =>
    h(NSpace, { size: 8 }, { default: () => [
      row.status === 1
        ? h(NButton, { size: 'small', type: 'warning', onClick: () => handleEndCheckin(row) }, { default: () => '结束签到' })
        : null,
      h(NButton, { size: 'small', type: 'info', secondary: true, onClick: () => showRecords(row) }, { default: () => '查看记录' })
    ]})
  }
]

const columns = userStore.isTeacher() ? teacherColumns : studentColumns

async function loadData() {
  loading.value = true
  try {
    const params: any = { current: 1, size: 50 }
    if (courseId.value) params.courseId = courseId.value
    const res: any = await getCheckinPage(params)
    if (res.code === 200) dataList.value = res.data.records || []
  } finally { loading.value = false }
}

async function loadCourses() {
  try {
    const res: any = await getMyCourses()
    const list = res.data?.records || res.data || []
    courseOptions.value = list.map((c: any) => ({ label: c.courseName, value: c.id }))
  } catch { courseOptions.value = [] }
}

// 学生签到
async function handleStudentCheckin(row: any) {
  try {
    const res: any = await doCheckin(row.id)
    if (res.code === 200) { message.success('签到成功'); loadData() }
    else message.error(res.message || '签到失败')
  } catch (e: any) { message.error(e?.response?.data?.message || '签到失败') }
}

function toISOStr(ts: number | null) {
  if (!ts) return null
  const d = new Date(ts)
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

// 教师发起签到
async function handleCreateCheckin() {
  if (!checkinForm.value.courseId) { message.warning('请选择课程'); return }
  if (!checkinForm.value.title) { message.warning('请输入签到标题'); return }
  if (!checkinForm.value.startTime || !checkinForm.value.endTime) { message.warning('请选择开始和截止时间'); return }
  const payload = {
    courseId: checkinForm.value.courseId,
    title: checkinForm.value.title,
    checkinType: checkinForm.value.checkinType,
    startTime: toISOStr(checkinForm.value.startTime),
    endTime: toISOStr(checkinForm.value.endTime),
  }
  try {
    const res: any = await createCheckin(payload)
    if (res.code === 200) {
      message.success('签到已发起')
      createVisible.value = false
      checkinForm.value = { courseId: null, title: '', checkinType: 1, startTime: null, endTime: null }
      loadData()
    } else message.error(res.message)
  } catch (e: any) { message.error(e?.response?.data?.message || '发起失败') }
}

// 教师结束签到
async function handleEndCheckin(row: any) {
  try {
    const res: any = await endCheckin(row.id)
    if (res.code === 200) { message.success('签到已结束'); loadData() }
    else message.error(res.message)
  } catch (e: any) { message.error(e?.response?.data?.message || '操作失败') }
}

// 教师查看签到记录
async function showRecords(row: any) {
  recordTitle.value = row.title
  try {
    const res: any = await getCheckinRecords(row.id, { page: 1, size: 200 })
    recordList.value = res.data?.records || []
  } catch { recordList.value = [] }
  recordVisible.value = true
}

function openCreate() {
  loadCourses()
  checkinForm.value = { courseId: null, title: '', checkinType: 1, startTime: null, endTime: null }
  createVisible.value = true
}

onMounted(() => { loadFilterCourses(); loadData() })
</script>

<template>
  <div>
    <div class="page-header">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <div>
          <h2>{{ userStore.isTeacher() ? '签到管理' : '课堂签到' }}</h2>
          <p v-if="userStore.isTeacher()">发起、管理和查看签到记录</p>
          <p v-else>查看当前可签到的课堂，进行签到操作</p>
        </div>
        <n-button v-if="userStore.isTeacher()" type="primary" @click="openCreate">+ 发起签到</n-button>
      </div>
    </div>

    <n-space :size="12" style="margin-bottom: 20px;">
      <n-select v-model:value="courseId" :options="filterCourseOptions" placeholder="选择课程" clearable style="width: 200px;" />
      <n-button type="primary" @click="loadData">查询</n-button>
    </n-space>

    <n-card :bordered="false" style="border-radius: var(--radius);">
      <n-data-table :columns="columns" :data="dataList" :loading="loading" :bordered="false" striped />
    </n-card>

    <!-- 教师：发起签到弹窗 -->
    <n-modal v-model:show="createVisible" preset="dialog" title="发起签到" positive-text="发起" negative-text="取消" @positive-click="handleCreateCheckin" style="width: 520px;">
      <n-form label-placement="left" label-width="80" style="margin-top: 16px;">
        <n-form-item label="选择课程" required>
          <n-select v-model:value="checkinForm.courseId" :options="courseOptions" placeholder="请选择课程" />
        </n-form-item>
        <n-form-item label="签到标题" required>
          <n-input v-model:value="checkinForm.title" placeholder="如: 第3周 数据结构" />
        </n-form-item>
        <n-form-item label="签到类型">
          <n-select v-model:value="checkinForm.checkinType" :options="typeOptions" />
        </n-form-item>
        <n-form-item label="开始时间" required>
          <n-date-picker v-model:value="checkinForm.startTime" type="datetime" style="width: 100%;" />
        </n-form-item>
        <n-form-item label="截止时间" required>
          <n-date-picker v-model:value="checkinForm.endTime" type="datetime" style="width: 100%;" />
        </n-form-item>
      </n-form>
    </n-modal>

    <!-- 教师：签到记录弹窗 -->
    <n-modal v-model:show="recordVisible" preset="card" :title="`签到记录 - ${recordTitle}`" style="width: 650px;">
      <n-data-table :columns="recordColumns" :data="recordList" :bordered="false" size="small" />
    </n-modal>
  </div>
</template>
