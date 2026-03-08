<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { useUserStore } from '@/stores/user'
import { getMaterialPage, createMaterial, deleteMaterial, getMyCourses } from '@/api'
import {
  NCard, NDataTable, NButton, NTag, NInputNumber, NSpace, NModal, NForm,
  NFormItem, NInput, NSelect, useMessage, useDialog
} from 'naive-ui'

const userStore = useUserStore()
const message = useMessage()
const dialog = useDialog()
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

// ---- 教师：上传资料 ----
const createVisible = ref(false)
const courseOptions = ref<any[]>([])
const matForm = ref({ courseId: null as number | null, title: '', description: '', fileUrl: '' })

const columns = ref<any[]>([])

function buildColumns() {
  const base = [
    { title: 'ID', key: 'id', width: 80 },
    { title: '课程ID', key: 'courseId', width: 100 },
    { title: '资料标题', key: 'title', width: 200 },
    { title: '描述', key: 'description', ellipsis: { tooltip: true } },
    { title: '上传时间', key: 'createTime', width: 170, render: (row: any) => fmt(row.createTime) },
  ]
  if (userStore.isTeacher()) {
    base.push({
      title: '操作', key: 'action', width: 160, render: (row: any) =>
        h(NSpace, { size: 6 }, { default: () => [
          row.fileUrl ? h(NButton, { size: 'small', type: 'primary', onClick: () => window.open(row.fileUrl, '_blank') }, { default: () => '下载' }) : null,
          h(NButton, { size: 'small', type: 'error', quaternary: true, onClick: () => handleDelete(row) }, { default: () => '删除' })
        ]})
    })
  } else {
    base.push({
      title: '操作', key: 'action', width: 100, render: (row: any) =>
        row.fileUrl ? h(NButton, { size: 'small', type: 'primary', onClick: () => window.open(row.fileUrl, '_blank') }, { default: () => '下载' }) : h('span', '-')
    })
  }
  columns.value = base
}

async function loadData() {
  loading.value = true
  try {
    const params: any = { current: 1, size: 50 }
    if (courseId.value) params.courseId = courseId.value
    const res: any = await getMaterialPage(params)
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

function openCreate() {
  loadCourses()
  matForm.value = { courseId: null, title: '', description: '', fileUrl: '' }
  createVisible.value = true
}

async function handleCreate() {
  if (!matForm.value.courseId || !matForm.value.title) { message.warning('请填写必填项'); return }
  try {
    const res: any = await createMaterial(matForm.value)
    if (res.code === 200) {
      message.success('资料发布成功')
      createVisible.value = false
      loadData()
    } else message.error(res.message)
  } catch (e: any) { message.error(e?.response?.data?.message || '发布失败') }
}

function handleDelete(item: any) {
  dialog.warning({
    title: '删除资料',
    content: `确定删除资料「${item.title}」吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        const res: any = await deleteMaterial(item.id)
        if (res.code === 200) { message.success('已删除'); loadData() }
        else message.error(res.message)
      } catch { message.error('删除失败') }
    }
  })
}

onMounted(() => { loadFilterCourses(); buildColumns(); loadData() })
</script>

<template>
  <div>
    <div class="page-header">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <div>
          <h2>{{ userStore.isTeacher() ? '资料管理' : '资料下载' }}</h2>
          <p v-if="userStore.isTeacher()">上传和管理课程资料</p>
          <p v-else>下载课程学习资料</p>
        </div>
        <n-button v-if="userStore.isTeacher()" type="primary" @click="openCreate">+ 发布资料</n-button>
      </div>
    </div>

    <n-space :size="12" style="margin-bottom: 20px;">
      <n-select v-model:value="courseId" :options="filterCourseOptions" placeholder="选择课程" clearable style="width: 200px;" />
      <n-button type="primary" @click="loadData">查询</n-button>
    </n-space>

    <n-card :bordered="false" style="border-radius: var(--radius);">
      <n-data-table :columns="columns" :data="dataList" :loading="loading" :bordered="false" striped />
    </n-card>

    <!-- 教师：发布资料弹窗 -->
    <n-modal v-model:show="createVisible" preset="dialog" title="发布资料" positive-text="发布" negative-text="取消" @positive-click="handleCreate" style="width: 520px;">
      <n-form label-placement="left" label-width="80" style="margin-top: 16px;">
        <n-form-item label="选择课程" required>
          <n-select v-model:value="matForm.courseId" :options="courseOptions" placeholder="请选择课程" />
        </n-form-item>
        <n-form-item label="资料标题" required>
          <n-input v-model:value="matForm.title" placeholder="请输入标题" />
        </n-form-item>
        <n-form-item label="资料描述">
          <n-input v-model:value="matForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </n-form-item>
        <n-form-item label="文件链接">
          <n-input v-model:value="matForm.fileUrl" placeholder="输入文件下载链接" />
        </n-form-item>
      </n-form>
    </n-modal>
  </div>
</template>
