<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCourseDetail } from '@/api'
import { NCard, NDescriptions, NDescriptionsItem, NTag, NSpin } from 'naive-ui'

const route = useRoute()
const loading = ref(false)
const course = ref<any>({})

async function loadData() {
  loading.value = true
  try {
    const res: any = await getCourseDetail(Number(route.params.id))
    if (res.code === 200) course.value = res.data
  } finally { loading.value = false }
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="page-header"><h2>课程详情</h2></div>
    <n-spin :show="loading">
      <n-card :bordered="false" style="border-radius: var(--radius);">
        <h2 style="margin-bottom: 20px;">{{ course.courseName }}</h2>
        <n-descriptions bordered :column="2" label-placement="left">
          <n-descriptions-item label="课程码"><n-tag type="info">{{ course.courseCode }}</n-tag></n-descriptions-item>
          <n-descriptions-item label="学期">{{ course.semester }}</n-descriptions-item>
          <n-descriptions-item label="签到权重">{{ course.checkinWeight }}%</n-descriptions-item>
          <n-descriptions-item label="作业权重">{{ course.assignmentWeight }}%</n-descriptions-item>
          <n-descriptions-item label="考试权重">{{ course.examWeight }}%</n-descriptions-item>
          <n-descriptions-item label="创建时间">{{ course.createTime }}</n-descriptions-item>
          <n-descriptions-item label="课程描述" :span="2">{{ course.description || '暂无描述' }}</n-descriptions-item>
        </n-descriptions>
      </n-card>
    </n-spin>
  </div>
</template>
