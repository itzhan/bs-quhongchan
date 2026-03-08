<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getGradePage, calculateGrade, getGradeStats } from "@/api/classroom";
import { message } from "@/utils/message";

defineOptions({ name: "GradeList" });

const loading = ref(false);
const dataList = ref([]);
const total = ref(0);
const calcDialogVisible = ref(false);
const statsDialogVisible = ref(false);
const statsData = ref<any>({});
const queryParams = reactive({ current: 1, size: 10, courseId: null as number | null });
const calcForm = reactive({ courseId: null as number | null, studentId: null as number | null, examScore: 0 });

async function loadData() {
  loading.value = true;
  try {
    const params: any = { current: queryParams.current, size: queryParams.size };
    if (queryParams.courseId) params.courseId = queryParams.courseId;
    const res = await getGradePage(params);
    if (res.code === 200) { dataList.value = res.data.records; total.value = res.data.total; }
  } finally { loading.value = false; }
}

function openCalcDialog() {
  calcDialogVisible.value = true;
  Object.assign(calcForm, { courseId: null, studentId: null, examScore: 0 });
}

async function handleCalc() {
  const res = await calculateGrade({ ...calcForm });
  if (res.code === 200) { message("成绩计算成功", { type: "success" }); calcDialogVisible.value = false; loadData(); }
}

async function handleStats(courseId: number) {
  const res = await getGradeStats(courseId);
  if (res.code === 200) { statsData.value = res.data; statsDialogVisible.value = true; }
}

function handleSizeChange(val: number) { queryParams.size = val; loadData(); }
function handleCurrentChange(val: number) { queryParams.current = val; loadData(); }
onMounted(() => loadData());
</script>

<template>
  <div class="main-content">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryParams" class="mb-4">
        <el-form-item label="课程ID"><el-input-number v-model="queryParams.courseId" :min="1" controls-position="right" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button type="success" @click="openCalcDialog">计算成绩</el-button>
          <el-button type="warning" @click="handleStats(queryParams.courseId!)" :disabled="!queryParams.courseId">统计分析</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="courseId" label="课程ID" width="100" align="center" />
        <el-table-column prop="userId" label="学生ID" width="100" align="center" />
        <el-table-column prop="userName" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="checkinScore" label="签到成绩" width="100" align="center" />
        <el-table-column prop="assignmentScore" label="作业成绩" width="100" align="center" />
        <el-table-column prop="examScore" label="考试成绩" width="100" align="center" />
        <el-table-column prop="totalScore" label="总成绩" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.totalScore >= 60 ? 'success' : 'danger'" effect="dark">{{ row.totalScore?.toFixed(1) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="计算时间" width="180" />
      </el-table>
      <el-pagination class="mt-4 flex justify-end" v-model:current-page="queryParams.current" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>

    <el-dialog v-model="calcDialogVisible" title="计算成绩" width="400px" destroy-on-close>
      <el-form label-width="90px">
        <el-form-item label="课程ID"><el-input-number v-model="calcForm.courseId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="学生ID"><el-input-number v-model="calcForm.studentId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="考试成绩"><el-input-number v-model="calcForm.examScore" :min="0" :max="100" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="calcDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCalc">计算</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="statsDialogVisible" title="成绩统计" width="400px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="平均分">{{ statsData.avgScore?.toFixed(1) }}</el-descriptions-item>
        <el-descriptions-item label="最高分">{{ statsData.maxScore?.toFixed(1) }}</el-descriptions-item>
        <el-descriptions-item label="最低分">{{ statsData.minScore?.toFixed(1) }}</el-descriptions-item>
        <el-descriptions-item label="及格率">{{ (statsData.passRate * 100)?.toFixed(1) }}%</el-descriptions-item>
        <el-descriptions-item label="总人数">{{ statsData.totalCount }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<style scoped>
.main-content { margin: 24px 24px 0 !important; }
</style>
