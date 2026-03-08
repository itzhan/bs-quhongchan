<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getUserPage, updateUserStatus, resetUserPassword } from "@/api/classroom";
import { message } from "@/utils/message";

defineOptions({ name: "SystemUser" });

const loading = ref(false);
const dataList = ref([]);
const total = ref(0);
const queryParams = reactive({
  current: 1,
  size: 10,
  username: "",
  role: "",
  status: null as number | null
});

const roleOptions = [
  { label: "全部", value: "" },
  { label: "管理员", value: "ADMIN" },
  { label: "教师", value: "TEACHER" },
  { label: "学生", value: "STUDENT" }
];

const statusOptions = [
  { label: "全部", value: null },
  { label: "正常", value: 1 },
  { label: "禁用", value: 0 }
];

async function loadData() {
  loading.value = true;
  try {
    const params: any = { current: queryParams.current, size: queryParams.size };
    if (queryParams.username) params.username = queryParams.username;
    if (queryParams.role) params.role = queryParams.role;
    if (queryParams.status !== null) params.status = queryParams.status;
    const res = await getUserPage(params);
    if (res.code === 200) {
      dataList.value = res.data.records;
      total.value = res.data.total;
    }
  } finally {
    loading.value = false;
  }
}

function handleSearch() { queryParams.current = 1; loadData(); }
function handleReset() {
  queryParams.username = "";
  queryParams.role = "";
  queryParams.status = null;
  queryParams.current = 1;
  loadData();
}

async function handleStatusChange(row: any) {
  const newStatus = row.status === 1 ? 0 : 1;
  const res = await updateUserStatus(row.id, newStatus);
  if (res.code === 200) { message("操作成功", { type: "success" }); loadData(); }
}

async function handleResetPassword(row: any) {
  const res = await resetUserPassword(row.id);
  if (res.code === 200) { message("密码已重置为 123456", { type: "success" }); }
}

function handleSizeChange(val: number) { queryParams.size = val; loadData(); }
function handleCurrentChange(val: number) { queryParams.current = val; loadData(); }

onMounted(() => loadData());
</script>

<template>
  <div class="main-content">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryParams" class="mb-4">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="queryParams.role" placeholder="请选择角色" clearable>
            <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option v-for="item in statusOptions" :key="String(item.value)" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="studentId" label="学号/工号" width="150" />
        <el-table-column prop="role" label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : row.role === 'TEACHER' ? 'warning' : 'success'">
              {{ row.role === 'ADMIN' ? '管理员' : row.role === 'TEACHER' ? '教师' : '学生' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :type="row.status === 1 ? 'danger' : 'success'" link @click="handleStatusChange(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-popconfirm title="确定重置该用户密码吗？" @confirm="handleResetPassword(row)">
              <template #reference>
                <el-button size="small" type="warning" link>重置密码</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="mt-4 flex justify-end" v-model:current-page="queryParams.current" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>
  </div>
</template>

<style scoped>
.main-content { margin: 24px 24px 0 !important; }
</style>
