<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getAuditLogPage } from "@/api/classroom";

defineOptions({ name: "AuditLogList" });

const loading = ref(false);
const dataList = ref([]);
const total = ref(0);
const queryParams = reactive({ current: 1, size: 10, module: "", operation: "" });

async function loadData() {
  loading.value = true;
  try {
    const params: any = { current: queryParams.current, size: queryParams.size };
    if (queryParams.module) params.module = queryParams.module;
    if (queryParams.operation) params.operation = queryParams.operation;
    const res = await getAuditLogPage(params);
    if (res.code === 200) { dataList.value = res.data.records; total.value = res.data.total; }
  } finally { loading.value = false; }
}

function handleSearch() { queryParams.current = 1; loadData(); }
function handleReset() { queryParams.module = ""; queryParams.operation = ""; queryParams.current = 1; loadData(); }
function handleSizeChange(val: number) { queryParams.size = val; loadData(); }
function handleCurrentChange(val: number) { queryParams.current = val; loadData(); }
onMounted(() => loadData());
</script>

<template>
  <div class="main-content">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryParams" class="mb-4">
        <el-form-item label="模块">
          <el-input v-model="queryParams.module" placeholder="请输入模块" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="操作">
          <el-input v-model="queryParams.operation" placeholder="请输入操作" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="userId" label="用户ID" width="100" align="center" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="operation" label="操作" width="120" />
        <el-table-column prop="method" label="方法" width="200" show-overflow-tooltip />
        <el-table-column prop="params" label="参数" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
      </el-table>
      <el-pagination class="mt-4 flex justify-end" v-model:current-page="queryParams.current" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>
  </div>
</template>

<style scoped>
.main-content { margin: 24px 24px 0 !important; }
</style>
