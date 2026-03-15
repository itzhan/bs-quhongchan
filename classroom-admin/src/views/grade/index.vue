<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getGradePage, getGradeStats, getCoursePage, getCourseMemberPage } from "@/api/classroom";
import { message } from "@/utils/message";

defineOptions({ name: "GradeList" });

const loading = ref(false);
const dataList = ref([]);
const total = ref(0);
const statsDialogVisible = ref(false);
const statsData = ref<any>({});
const courseOptions = ref<any[]>([]);
const studentOptions = ref<any[]>([]);
const queryParams = reactive({ current: 1, size: 10, courseId: null as number | null, studentId: null as number | null });

async function loadCourses() {
  const res = await getCoursePage({ current: 1, size: 200 });
  if (res.code === 200) { courseOptions.value = res.data.records; }
}

async function loadStudents() {
  if (!queryParams.courseId) { studentOptions.value = []; return; }
  const res = await getCourseMemberPage({ current: 1, size: 200, courseId: queryParams.courseId });
  if (res.code === 200) { studentOptions.value = res.data.records; }
}

async function onCourseChange() {
  queryParams.studentId = null;
  await loadStudents();
  loadData();
}

async function loadData() {
  loading.value = true;
  try {
    const params: any = { current: queryParams.current, size: queryParams.size };
    if (queryParams.courseId) params.courseId = queryParams.courseId;
    if (queryParams.studentId) params.studentId = queryParams.studentId;
    const res = await getGradePage(params);
    if (res.code === 200) { dataList.value = res.data.records; total.value = res.data.total; }
  } finally { loading.value = false; }
}

async function handleStats() {
  if (!queryParams.courseId) { message("请先选择课程", { type: "warning" }); return; }
  const res = await getGradeStats(queryParams.courseId);
  if (res.code === 200) { statsData.value = res.data; statsDialogVisible.value = true; }
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
          <el-select v-model="queryParams.courseId" placeholder="全部课程" clearable filterable style="width: 200px" @change="onCourseChange">
            <el-option v-for="c in courseOptions" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学生">
          <el-select v-model="queryParams.studentId" placeholder="全部学生" clearable filterable style="width: 200px" :disabled="!queryParams.courseId">
            <el-option v-for="s in studentOptions" :key="s.studentId" :label="`${s.realName || s.studentName || ''} (${s.studentNo || s.userName || s.studentId})`" :value="s.studentId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button type="warning" @click="handleStats">统计分析</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="courseName" label="课程名称" width="150" />
        <el-table-column prop="studentId" label="学生ID" width="100" align="center" />
        <el-table-column prop="studentNo" label="学号" width="150" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="checkinScore" label="签到成绩" width="100" align="center" />
        <el-table-column prop="assignmentScore" label="作业成绩" width="100" align="center" />
        <el-table-column prop="examScore" label="考试成绩" width="100" align="center" />
        <el-table-column prop="totalScore" label="总成绩" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.totalScore >= 60 ? 'success' : 'danger'" effect="dark">{{ row.totalScore?.toFixed?.(1) ?? row.totalScore }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="计算时间" width="180" />
      </el-table>
      <el-pagination class="mt-4 flex justify-end" v-model:current-page="queryParams.current" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>

    <el-dialog v-model="statsDialogVisible" title="成绩统计" width="400px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="平均分">{{ statsData.avg }}</el-descriptions-item>
        <el-descriptions-item label="最高分">{{ statsData.max }}</el-descriptions-item>
        <el-descriptions-item label="最低分">{{ statsData.min }}</el-descriptions-item>
        <el-descriptions-item label="及格率">{{ statsData.passRate }}%</el-descriptions-item>
        <el-descriptions-item label="总人数">{{ statsData.count }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<style scoped>
.main-content { margin: 24px 24px 0 !important; }
</style>
