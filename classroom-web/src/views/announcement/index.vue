<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { useUserStore } from '@/stores/user'
import { getAnnouncementPage, createAnnouncement, updateAnnouncement, deleteAnnouncement, getMyCourses } from '@/api'
import {
  NCard, NTag, NEmpty, NSpin, NInputNumber, NButton, NSpace, NModal, NForm,
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
  if (!dt) return ''
  return dt.replace('T', ' ').replace(/\.\d+$/, '')
}

// ---- 教师：发布/编辑公告 ----
const formVisible = ref(false)
const isEditing = ref(false)
const courseOptions = ref<any[]>([])
const annForm = ref({ id: null as number | null, courseId: null as number | null, title: '', content: '', type: 1 })
const typeOptions = [
  { label: '通知', value: 1 },
  { label: '公告', value: 2 },
  { label: '提醒', value: 3 }
]

async function loadData() {
  loading.value = true
  try {
    const params: any = { current: 1, size: 50 }
    if (courseId.value) params.courseId = courseId.value
    const res: any = await getAnnouncementPage(params)
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
  isEditing.value = false
  annForm.value = { id: null, courseId: null, title: '', content: '', type: 1 }
  formVisible.value = true
}

function openEdit(item: any) {
  loadCourses()
  isEditing.value = true
  annForm.value = { id: item.id, courseId: item.courseId, title: item.title, content: item.content, type: item.type || 1 }
  formVisible.value = true
}

async function handleSave() {
  if (!annForm.value.title) { message.warning('请输入标题'); return }
  try {
    let res: any
    if (isEditing.value) {
      res = await updateAnnouncement(annForm.value)
    } else {
      if (!annForm.value.courseId) { message.warning('请选择课程'); return }
      res = await createAnnouncement(annForm.value)
    }
    if (res.code === 200) {
      message.success(isEditing.value ? '修改成功' : '发布成功')
      formVisible.value = false
      loadData()
    } else message.error(res.message)
  } catch (e: any) { message.error(e?.response?.data?.message || '操作失败') }
}

function handleDelete(item: any) {
  dialog.warning({
    title: '删除公告',
    content: `确定删除公告「${item.title}」吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        const res: any = await deleteAnnouncement(item.id)
        if (res.code === 200) { message.success('已删除'); loadData() }
        else message.error(res.message)
      } catch { message.error('删除失败') }
    }
  })
}

onMounted(() => { loadFilterCourses(); loadData() })
</script>

<template>
  <div>
    <div class="page-header">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <div>
          <h2>{{ userStore.isTeacher() ? '公告管理' : '公告通知' }}</h2>
          <p v-if="userStore.isTeacher()">发布和管理课程公告</p>
          <p v-else>查看课程公告</p>
        </div>
        <n-button v-if="userStore.isTeacher()" type="primary" @click="openCreate">+ 发布公告</n-button>
      </div>
    </div>

    <n-space :size="12" style="margin-bottom: 20px;">
      <n-select v-model:value="courseId" :options="filterCourseOptions" placeholder="选择课程" clearable style="width: 200px;" />
      <n-button type="primary" @click="loadData">查询</n-button>
    </n-space>

    <n-spin :show="loading">
      <template v-if="dataList.length">
        <n-card v-for="item in dataList" :key="item.id" :bordered="false" style="border-radius: var(--radius); margin-bottom: 16px;">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
            <h3 style="font-size: 16px; font-weight: 600;">{{ item.title }}</h3>
            <n-space :size="8">
              <n-tag size="small" type="info">课程{{ item.courseId }}</n-tag>
              <!-- 教师：编辑 / 删除 -->
              <template v-if="userStore.isTeacher()">
                <n-button size="tiny" type="info" secondary @click="openEdit(item)">编辑</n-button>
                <n-button size="tiny" type="error" quaternary @click="handleDelete(item)">删除</n-button>
              </template>
            </n-space>
          </div>
          <p style="color: var(--text-secondary); line-height: 1.8; white-space: pre-wrap;">{{ item.content }}</p>
          <p style="color: #999; font-size: 12px; margin-top: 12px;">{{ fmt(item.createTime) }}</p>
        </n-card>
      </template>
      <n-empty v-else description="暂无公告" style="padding: 60px 0;" />
    </n-spin>

    <!-- 教师：发布/编辑公告弹窗 -->
    <n-modal v-model:show="formVisible" preset="dialog" :title="isEditing ? '编辑公告' : '发布公告'" positive-text="保存" negative-text="取消" @positive-click="handleSave" style="width: 520px;">
      <n-form label-placement="left" label-width="80" style="margin-top: 16px;">
        <n-form-item v-if="!isEditing" label="选择课程" required>
          <n-select v-model:value="annForm.courseId" :options="courseOptions" placeholder="请选择课程" />
        </n-form-item>
        <n-form-item label="标题" required>
          <n-input v-model:value="annForm.title" placeholder="请输入公告标题" />
        </n-form-item>
        <n-form-item label="类型">
          <n-select v-model:value="annForm.type" :options="typeOptions" />
        </n-form-item>
        <n-form-item label="内容">
          <n-input v-model:value="annForm.content" type="textarea" :rows="5" placeholder="请输入公告内容" />
        </n-form-item>
      </n-form>
    </n-modal>
  </div>
</template>
