<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { useUserStore } from '@/stores/user'
import { getAssignmentPage, submitAssignment, createAssignment, getSubmissionPage, gradeSubmission, getMyCourses } from '@/api'
import {
  NCard, NDataTable, NButton, NTag, NInputNumber, NSpace, NModal, NForm,
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

// ---- 学生：提交作业 ----
const submitVisible = ref(false)
const currentAssignment = ref<any>({})
const submitForm = ref({ content: '', attachmentUrl: '' })

// ---- 教师：发布作业 ----
const createVisible = ref(false)
const courseOptions = ref<any[]>([])
const assignmentForm = ref({ courseId: null as number | null, title: '', content: '', deadline: null as number | null })

// ---- 教师：查看提交 & 批改 ----
const listVisible = ref(false)
const submissionList = ref<any[]>([])
const listTitle = ref('')
const gradeVisible = ref(false)
const gradeTarget = ref<any>({})
const gradeForm = ref({ score: 0, feedback: '' })

// 学生列
const studentColumns = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '课程ID', key: 'courseId', width: 100 },
  { title: '作业标题', key: 'title', width: 200 },
  { title: '作业内容', key: 'content', ellipsis: { tooltip: true } },
  { title: '截止时间', key: 'deadline', width: 170, render: (row: any) => fmt(row.deadline) },
  { title: '发布时间', key: 'createTime', width: 170, render: (row: any) => fmt(row.createTime) },
  { title: '操作', key: 'action', width: 120, render: (row: any) =>
    h(NButton, { size: 'small', type: 'primary', onClick: () => openSubmit(row) }, { default: () => '提交作业' })
  }
]

// 教师列
const teacherColumns = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '课程ID', key: 'courseId', width: 100 },
  { title: '作业标题', key: 'title', width: 200 },
  { title: '作业内容', key: 'content', ellipsis: { tooltip: true } },
  { title: '截止时间', key: 'deadline', width: 170, render: (row: any) => fmt(row.deadline) },
  { title: '操作', key: 'action', width: 160, render: (row: any) =>
    h(NButton, { size: 'small', type: 'info', secondary: true, onClick: () => showSubmissions(row) }, { default: () => '查看提交' })
  }
]

// 提交列表列
const submissionColumns = [
  { title: '学生ID', key: 'studentId', width: 100 },
  { title: '提交内容', key: 'content', ellipsis: { tooltip: true } },
  { title: '附件', key: 'attachmentUrl', width: 80, render: (row: any) => row.attachmentUrl ? h('a', { href: row.attachmentUrl, target: '_blank' }, '查看') : '-' },
  { title: '分数', key: 'score', width: 80, render: (row: any) => row.score !== null && row.score !== undefined ? row.score : '-' },
  { title: '状态', key: 'status', width: 100, render: (row: any) => h(NTag, { type: row.status === 2 ? 'success' : row.status === 1 ? 'info' : 'default', size: 'small' }, { default: () => row.status === 2 ? '已批改' : row.status === 1 ? '已提交' : '未提交' }) },
  { title: '操作', key: 'action', width: 100, render: (row: any) =>
    h(NButton, { size: 'small', type: 'warning', onClick: () => openGrade(row) }, { default: () => '批改' })
  }
]

const columns = userStore.isTeacher() ? teacherColumns : studentColumns

async function loadData() {
  loading.value = true
  try {
    const params: any = { current: 1, size: 50 }
    if (courseId.value) params.courseId = courseId.value
    const res: any = await getAssignmentPage(params)
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

// ---- 学生：提交作业 ----
function openSubmit(row: any) {
  currentAssignment.value = row
  submitForm.value = { content: '', attachmentUrl: '' }
  submitVisible.value = true
}

async function handleSubmit() {
  if (!submitForm.value.content) { message.warning('请输入作业内容'); return }
  try {
    const res: any = await submitAssignment(currentAssignment.value.id, submitForm.value)
    if (res.code === 200) { message.success('提交成功'); submitVisible.value = false }
    else message.error(res.message)
  } catch (e: any) { message.error(e?.response?.data?.message || '提交失败') }
}

// ---- 教师：发布作业 ----
function toISOStr(ts: number | null) {
  if (!ts) return null
  const d = new Date(ts)
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

function openCreate() {
  loadCourses()
  assignmentForm.value = { courseId: null, title: '', content: '', deadline: null }
  createVisible.value = true
}

async function handleCreate() {
  if (!assignmentForm.value.courseId || !assignmentForm.value.title) { message.warning('请填写必填项'); return }
  const payload = {
    courseId: assignmentForm.value.courseId,
    title: assignmentForm.value.title,
    content: assignmentForm.value.content,
    deadline: toISOStr(assignmentForm.value.deadline),
  }
  try {
    const res: any = await createAssignment(payload)
    if (res.code === 200) {
      message.success('作业发布成功')
      createVisible.value = false
      loadData()
    } else message.error(res.message)
  } catch (e: any) { message.error(e?.response?.data?.message || '发布失败') }
}

// ---- 教师：查看提交 ----
async function showSubmissions(row: any) {
  listTitle.value = row.title
  currentAssignment.value = row
  try {
    const res: any = await getSubmissionPage({ assignmentId: row.id, current: 1, size: 200 })
    submissionList.value = res.data?.records || []
  } catch { submissionList.value = [] }
  listVisible.value = true
}

// ---- 教师：批改 ----
function openGrade(row: any) {
  gradeTarget.value = row
  gradeForm.value = { score: row.score || 0, feedback: row.feedback || '' }
  gradeVisible.value = true
}

async function handleGrade() {
  try {
    const res: any = await gradeSubmission(gradeTarget.value.id, gradeForm.value.score, gradeForm.value.feedback)
    if (res.code === 200) {
      message.success('批改成功')
      gradeVisible.value = false
      if (listVisible.value && currentAssignment.value.id) showSubmissions(currentAssignment.value)
    } else message.error(res.message)
  } catch (e: any) { message.error(e?.response?.data?.message || '批改失败') }
}

onMounted(() => { loadFilterCourses(); loadData() })
</script>

<template>
  <div>
    <div class="page-header">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <div>
          <h2>{{ userStore.isTeacher() ? '作业管理' : '作业中心' }}</h2>
          <p v-if="userStore.isTeacher()">发布作业、查看提交和批改</p>
          <p v-else>查看作业并提交</p>
        </div>
        <n-button v-if="userStore.isTeacher()" type="primary" @click="openCreate">+ 发布作业</n-button>
      </div>
    </div>

    <n-space :size="12" style="margin-bottom: 20px;">
      <n-select v-model:value="courseId" :options="filterCourseOptions" placeholder="选择课程" clearable style="width: 200px;" />
      <n-button type="primary" @click="loadData">查询</n-button>
    </n-space>

    <n-card :bordered="false" style="border-radius: var(--radius);">
      <n-data-table :columns="columns" :data="dataList" :loading="loading" :bordered="false" striped />
    </n-card>

    <!-- 学生：提交作业 -->
    <n-modal v-model:show="submitVisible" preset="dialog" title="提交作业" positive-text="提交" negative-text="取消" @positive-click="handleSubmit" style="width: 500px;">
      <p style="margin-bottom: 12px; color: var(--text-secondary);">作业：{{ currentAssignment.title }}</p>
      <n-input v-model:value="submitForm.content" type="textarea" :rows="5" placeholder="请输入作业内容" style="margin-bottom: 12px;" />
      <n-input v-model:value="submitForm.attachmentUrl" placeholder="附件链接（可选）" />
    </n-modal>

    <!-- 教师：发布作业 -->
    <n-modal v-model:show="createVisible" preset="dialog" title="发布作业" positive-text="发布" negative-text="取消" @positive-click="handleCreate" style="width: 520px;">
      <n-form label-placement="left" label-width="80" style="margin-top: 16px;">
        <n-form-item label="选择课程" required>
          <n-select v-model:value="assignmentForm.courseId" :options="courseOptions" placeholder="请选择课程" />
        </n-form-item>
        <n-form-item label="作业标题" required>
          <n-input v-model:value="assignmentForm.title" placeholder="请输入标题" />
        </n-form-item>
        <n-form-item label="作业内容">
          <n-input v-model:value="assignmentForm.content" type="textarea" :rows="4" placeholder="请输入作业要求" />
        </n-form-item>
        <n-form-item label="截止时间">
          <n-date-picker v-model:value="assignmentForm.deadline" type="datetime" clearable style="width: 100%;" />
        </n-form-item>
      </n-form>
    </n-modal>

    <!-- 教师：查看提交列表 -->
    <n-modal v-model:show="listVisible" preset="card" :title="`提交列表 - ${listTitle}`" style="width: 800px;">
      <n-data-table :columns="submissionColumns" :data="submissionList" :bordered="false" size="small" />
    </n-modal>

    <!-- 教师：批改弹窗 -->
    <n-modal v-model:show="gradeVisible" preset="dialog" title="批改作业" positive-text="确定" negative-text="取消" @positive-click="handleGrade" style="width: 450px;">
      <n-form label-placement="left" label-width="60" style="margin-top: 16px;">
        <n-form-item label="分数">
          <n-input-number v-model:value="gradeForm.score" :min="0" :max="100" style="width: 100%;" />
        </n-form-item>
        <n-form-item label="评语">
          <n-input v-model:value="gradeForm.feedback" type="textarea" :rows="3" placeholder="请输入评语（可选）" />
        </n-form-item>
      </n-form>
    </n-modal>
  </div>
</template>
