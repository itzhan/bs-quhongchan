<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getCourseMemberPage, removeCourseMember, getCoursePage } from "@/api/classroom";
import { message } from "@/utils/message";

defineOptions({ name: "CourseMember" });

const loading = ref(false);
const dataList = ref([]);
const total = ref(0);
const courseOptions = ref<any[]>([]);
const queryParams = reactive({ current: 1, size: 10, courseId: null as number | null });

async function loadCourses() {
  const res = await getCoursePage({ current: 1, size: 200 });
  if (res.code === 200) { courseOptions.value = res.data.records; }
}

async function loadData() {
  loading.value = true;
  try {
    const params: any = { current: queryParams.current, size: queryParams.size };
    if (queryParams.courseId) params.courseId = queryParams.courseId;
    const res = await getCourseMemberPage(params);
    if (res.code === 200) { dataList.value = res.data.records; total.value = res.data.total; }
  } finally { loading.value = false; }
}

async function handleRemove(row: any) {
  const res = await removeCourseMember(row.id);
  if (res.code === 200) { message("移除成功", { type: "success" }); loadData(); }
}

function handleSizeChange(val: number) { queryParams.size = val; loadData(); }
function handleCurrentChange(val: number) { queryParams.current = val; loadData(); }
onMounted(() => { loadCourses(); loadData(); });
</script>

<template>
  <div class="main-content">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryParams" class="mb-4">
        <el-form-item label="课程">
          <el-select v-model="queryParams.courseId" placeholder="全部课程" clearable filterable style="width: 220px">
            <el-option v-for="c in courseOptions" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="loadData">搜索</el-button></el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="courseId" label="课程ID" width="100" align="center" />
        <el-table-column prop="studentId" label="学生ID" width="100" align="center" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="120" />
        <el-table-column prop="role" label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === 'TEACHER' ? 'warning' : 'success'">{{ row.role === 'TEACHER' ? '教师' : '学生' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="加入时间" width="180" />
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-popconfirm title="确定移除该成员吗？" @confirm="handleRemove(row)">
              <template #reference><el-button size="small" type="danger" link>移除</el-button></template>
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
