<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCoursePage, joinCourse } from '@/api'
import { NCard, NGrid, NGi, NInput, NButton, NTag, NEmpty, NSpin, NSpace, NIcon, useMessage, NModal } from 'naive-ui'
import { SearchOutline, PeopleOutline } from '@vicons/ionicons5'

const router = useRouter()
const message = useMessage()
const loading = ref(false)
const dataList = ref<any[]>([])
const keyword = ref('')
const joinDialogVisible = ref(false)
const courseCode = ref('')

async function loadData() {
  loading.value = true
  try {
    const params: any = { current: 1, size: 50 }
    if (keyword.value) params.courseName = keyword.value
    const res: any = await getCoursePage(params)
    if (res.code === 200) dataList.value = res.data.records || []
  } finally { loading.value = false }
}

async function handleJoin() {
  if (!courseCode.value) { message.warning('请输入课程码'); return }
  try {
    const res: any = await joinCourse(courseCode.value)
    if (res.code === 200) { message.success('加入成功'); joinDialogVisible.value = false; courseCode.value = '' }
    else message.error(res.message)
  } catch (e: any) { message.error(e?.response?.data?.message || '加入失败') }
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="page-header">
      <h2>课程中心</h2>
      <p>浏览所有课程，使用课程码加入课程</p>
    </div>
    <n-space :size="12" style="margin-bottom: 24px;">
      <n-input v-model:value="keyword" placeholder="搜索课程名称" clearable style="width: 280px;" @keyup.enter="loadData">
        <template #prefix><n-icon :component="SearchOutline" /></template>
      </n-input>
      <n-button type="primary" @click="loadData">搜索</n-button>
      <n-button @click="joinDialogVisible = true" secondary type="info">课程码加入</n-button>
    </n-space>

    <n-spin :show="loading">
      <n-grid v-if="dataList.length" :cols="3" :x-gap="20" :y-gap="20" responsive="screen" item-responsive>
        <n-gi v-for="item in dataList" :key="item.id" span="3 m:1">
          <n-card hoverable :bordered="false" class="course-card" @click="router.push(`/course/${item.id}`)">
            <div class="course-top">
              <h3>{{ item.courseName }}</h3>
              <n-tag type="info" size="small" round>{{ item.semester }}</n-tag>
            </div>
            <p class="course-desc">{{ item.description || '暂无描述' }}</p>
            <div class="course-footer">
              <n-tag size="small" :bordered="false">课程码: {{ item.courseCode }}</n-tag>
            </div>
          </n-card>
        </n-gi>
      </n-grid>
      <n-empty v-else description="暂无课程" style="padding: 60px 0;" />
    </n-spin>

    <n-modal v-model:show="joinDialogVisible" preset="dialog" title="课程码加入" positive-text="加入" negative-text="取消" @positive-click="handleJoin">
      <n-input v-model:value="courseCode" placeholder="请输入6位课程码" size="large" />
    </n-modal>
  </div>
</template>

<style scoped>
.course-card { border-radius: var(--radius); transition: transform 0.2s; }
.course-card:hover { transform: translateY(-3px); }
.course-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
.course-top h3 { font-size: 16px; font-weight: 600; }
.course-desc { font-size: 13px; color: var(--text-secondary); margin-bottom: 16px; line-height: 1.6; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.course-footer { display: flex; justify-content: space-between; align-items: center; }
</style>
