<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { useUserStore } from '@/stores/user'
import { getMyGrades, getGradePage, calculateGrade, calculateAllGrades, updateExamScore, getGradeStats, getMyCourses } from '@/api'
import {
  NCard, NDataTable, NTag, NInputNumber, NButton, NSpace, NModal, NForm,
  NFormItem, NSelect, NStatistic, NGrid, NGi, useMessage, useDialog
} from 'naive-ui'

const userStore = useUserStore()
const message = useMessage()
const dialog = useDialog()
const loading = ref(false)
const dataList = ref<any[]>([])
const courseId = ref<number | null>(null)

// ---- 课程下拉 ----
const courseOptions = ref<any[]>([])

// ---- 教师：录入考试分 ----
const examVisible = ref(false)
const examForm = ref({ courseId: null as number | null, studentId: null as number | null, examScore: 0 })

// ---- 教师：成绩统计 ----
const statsVisible = ref(false)
const statsData = ref<any>({})
const statsCourse = ref('')

// 通过课程ID查课程名
function courseNameById(id: number) {
  const opt = courseOptions.value.find((o: any) => o.value === id)
  return opt ? opt.label : `课程${id}`
}

// 学生列
const studentColumns = [
  { title: '课程', key: 'courseId', width: 140, render: (row: any) => courseNameById(row.courseId) },
  { title: '签到成绩', key: 'checkinScore', width: 100 },
  { title: '作业成绩', key: 'assignmentScore', width: 100 },
  { title: '考试成绩', key: 'examScore', width: 100 },
  { title: '总成绩', key: 'totalScore', width: 120, render: (row: any) => h(NTag, { type: (row.totalScore || 0) >= 60 ? 'success' : 'error', size: 'small' }, { default: () => row.totalScore?.toFixed(1) ?? '-' }) },
  { title: '计算时间', key: 'createTime', width: 180 }
]

// 教师列
const teacherColumns = [
  { title: '学生ID', key: 'studentId', width: 100 },
  { title: '课程', key: 'courseId', width: 140, render: (row: any) => courseNameById(row.courseId) },
  { title: '签到分', key: 'checkinScore', width: 80 },
  { title: '作业分', key: 'assignmentScore', width: 80 },
  { title: '考试分', key: 'examScore', width: 80 },
  { title: '总成绩', key: 'totalScore', width: 100, render: (row: any) => h(NTag, { type: (row.totalScore || 0) >= 60 ? 'success' : 'error', size: 'small' }, { default: () => row.totalScore?.toFixed(1) ?? '-' }) },
  { title: '操作', key: 'action', width: 200, render: (row: any) =>
    h(NSpace, { size: 6 }, { default: () => [
      h(NButton, { size: 'small', type: 'info', secondary: true, onClick: () => handleCalc(row) }, { default: () => '计算成绩' }),
      h(NButton, { size: 'small', type: 'warning', secondary: true, onClick: () => openExam(row) }, { default: () => '录入考试分' }),
    ]})
  }
]

const columns = userStore.isTeacher() ? teacherColumns : studentColumns

async function loadData() {
  loading.value = true
  try {
    const params: any = { current: 1, size: 50 }
    if (courseId.value) params.courseId = courseId.value

    let res: any
    if (userStore.isStudent()) {
      res = await getMyGrades(params)
    } else {
      res = await getGradePage(params)
    }
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

// 教师：计算成绩
async function handleCalc(row: any) {
  try {
    const res: any = await calculateGrade(row.courseId, row.studentId)
    if (res.code === 200) { message.success('成绩已重新计算'); loadData() }
    else message.error(res.message)
  } catch (e: any) { message.error(e?.response?.data?.message || '计算失败') }
}

// 教师：录入考试分
function openExam(row: any) {
  examForm.value = { courseId: row.courseId, studentId: row.studentId, examScore: row.examScore || 0 }
  examVisible.value = true
}

async function handleExam() {
  if (!examForm.value.courseId || !examForm.value.studentId) return
  try {
    const res: any = await updateExamScore(examForm.value.courseId, examForm.value.studentId, examForm.value.examScore)
    if (res.code === 200) {
      message.success('考试分已更新')
      examVisible.value = false
      loadData()
    } else message.error(res.message)
  } catch (e: any) { message.error(e?.response?.data?.message || '更新失败') }
}

// 教师：查看统计
async function showStats() {
  if (!courseId.value) { message.warning('请先选择课程'); return }
  try {
    const res: any = await getGradeStats(courseId.value)
    statsData.value = res.data || {}
    statsCourse.value = courseNameById(courseId.value)
    statsVisible.value = true
  } catch (e: any) { message.error(e?.response?.data?.message || '获取统计失败') }
}

// 教师：批量计算全班成绩
async function handleCalcAll() {
  if (!courseId.value) { message.warning('请先选择课程'); return }
  try {
    loading.value = true
    const res: any = await calculateAllGrades(courseId.value)
    if (res.code === 200) { message.success('全班成绩已重新计算'); loadData() }
    else message.error(res.message)
  } catch (e: any) { message.error(e?.response?.data?.message || '计算失败') }
  finally { loading.value = false }
}

onMounted(() => { loadCourses(); loadData() })
</script>

<template>
  <div>
    <div class="page-header">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <div>
          <h2>{{ userStore.isTeacher() ? '成绩管理' : '成绩查询' }}</h2>
          <p v-if="userStore.isTeacher()">管理学生成绩、录入考试分、计算总成绩</p>
          <p v-else>查看各课程成绩</p>
        </div>
      </div>
    </div>

    <n-space :size="12" style="margin-bottom: 20px;">
      <n-select v-model:value="courseId" :options="courseOptions" placeholder="选择课程" clearable style="width: 200px;" />
      <n-button type="primary" @click="loadData">查询</n-button>
      <n-button v-if="userStore.isTeacher()" type="warning" secondary @click="handleCalcAll">批量计算全班成绩</n-button>
      <n-button v-if="userStore.isTeacher()" type="info" secondary @click="showStats">查看统计</n-button>
    </n-space>

    <n-card :bordered="false" style="border-radius: var(--radius);">
      <n-data-table :columns="columns" :data="dataList" :loading="loading" :bordered="false" striped />
    </n-card>

    <!-- 教师：录入考试分弹窗 -->
    <n-modal v-model:show="examVisible" preset="dialog" title="录入考试分" positive-text="确定" negative-text="取消" @positive-click="handleExam" style="width: 400px;">
      <n-form label-placement="left" label-width="80" style="margin-top: 16px;">
        <n-form-item label="考试分数">
          <n-input-number v-model:value="examForm.examScore" :min="0" :max="100" style="width: 100%;" />
        </n-form-item>
      </n-form>
    </n-modal>

    <!-- 教师：成绩统计弹窗 -->
    <n-modal v-model:show="statsVisible" preset="card" :title="`成绩统计 - ${statsCourse}`" style="width: 600px;">
      <n-grid :cols="4" :x-gap="16" :y-gap="16">
        <n-gi><n-statistic label="平均分" :value="statsData.avg || '-'" /></n-gi>
        <n-gi><n-statistic label="最高分" :value="statsData.max != null ? Number(statsData.max).toFixed(1) : '-'" /></n-gi>
        <n-gi><n-statistic label="最低分" :value="statsData.min != null ? Number(statsData.min).toFixed(1) : '-'" /></n-gi>
        <n-gi><n-statistic label="及格率" :value="statsData.passRate != null ? statsData.passRate + '%' : '-'" /></n-gi>
      </n-grid>
    </n-modal>
  </div>
</template>
