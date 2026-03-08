<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getCheckinRecordPage } from "@/api/classroom";

defineOptions({ name: "CheckinRecord" });

const loading = ref(false);
const dataList = ref([]);
const total = ref(0);
const queryParams = reactive({ current: 1, size: 10, checkinId: null as number | null, courseId: null as number | null });

async function loadData() {
  loading.value = true;
  try {
    const params: any = { current: queryParams.current, size: queryParams.size };
    if (queryParams.checkinId) params.checkinId = queryParams.checkinId;
    if (queryParams.courseId) params.courseId = queryParams.courseId;
    const res = await getCheckinRecordPage(params);
    if (res.code === 200) { dataList.value = res.data.records; total.value = res.data.total; }
  } finally { loading.value = false; }
}

function handleSizeChange(val: number) { queryParams.size = val; loadData(); }
function handleCurrentChange(val: number) { queryParams.current = val; loadData(); }
onMounted(() => loadData());
</script>

<template>
  <div class="main-content">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryParams" class="mb-4">
        <el-form-item label="签到ID"><el-input-number v-model="queryParams.checkinId" :min="1" controls-position="right" /></el-form-item>
        <el-form-item label="课程ID"><el-input-number v-model="queryParams.courseId" :min="1" controls-position="right" /></el-form-item>
        <el-form-item><el-button type="primary" @click="loadData">搜索</el-button></el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="checkinId" label="签到ID" width="100" align="center" />
        <el-table-column prop="userId" label="用户ID" width="100" align="center" />
        <el-table-column prop="userName" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'danger'">
              {{ row.status === 1 ? '已签到' : row.status === 2 ? '迟到' : '缺勤' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkinTime" label="签到时间" width="180" />
        <el-table-column prop="createTime" label="记录时间" width="180" />
      </el-table>
      <el-pagination class="mt-4 flex justify-end" v-model:current-page="queryParams.current" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>
  </div>
</template>

<style scoped>
.main-content { margin: 24px 24px 0 !important; }
</style>
