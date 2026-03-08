<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { getMessagePage, markMessageRead } from '@/api'
import { NCard, NDataTable, NButton, NTag, NBadge, useMessage } from 'naive-ui'

const msg = useMessage()
const loading = ref(false)
const dataList = ref<any[]>([])

const columns = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '标题', key: 'title', width: 200 },
  { title: '内容', key: 'content', ellipsis: { tooltip: true } },
  { title: '状态', key: 'isRead', width: 80, render: (row: any) => h(NTag, { type: row.isRead ? 'default' : 'error', size: 'small' }, { default: () => row.isRead ? '已读' : '未读' }) },
  { title: '发送时间', key: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 120, render: (row: any) => !row.isRead ? h(NButton, { size: 'small', type: 'primary', quaternary: true, onClick: () => handleRead(row) }, { default: () => '标记已读' }) : null }
]

async function loadData() {
  loading.value = true
  try {
    const res: any = await getMessagePage({ current: 1, size: 50 })
    if (res.code === 200) dataList.value = res.data.records || []
  } finally { loading.value = false }
}

async function handleRead(row: any) {
  try {
    const res: any = await markMessageRead(row.id)
    if (res.code === 200) { msg.success('已标记已读'); loadData() }
  } catch { msg.error('操作失败') }
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="page-header">
      <h2>消息中心</h2>
      <p>查看站内消息通知</p>
    </div>
    <n-card :bordered="false" style="border-radius: var(--radius);">
      <n-data-table :columns="columns" :data="dataList" :loading="loading" :bordered="false" striped />
    </n-card>
  </div>
</template>
