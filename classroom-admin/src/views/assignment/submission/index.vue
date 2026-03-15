<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getSubmissionPage, gradeSubmission, getCoursePage, getAssignmentPage } from "@/api/classroom";
import { message } from "@/utils/message";

defineOptions({ name: "AssignmentSubmission" });

const loading = ref(false);
const dataList = ref([]);
const total = ref(0);
const gradeDialogVisible = ref(false);
const courseOptions = ref<any[]>([]);
const assignmentOptions = ref<any[]>([]);
const queryParams = reactive({ current: 1, size: 10, courseId: null as number | null, assignmentId: null as number | null });
const gradeForm = reactive({ id: null as number | null, score: 0, comment: "" });

async function loadCourses() {
  const res = await getCoursePage({ current: 1, size: 200 });
  if (res.code === 200) { courseOptions.value = res.data.records; }
}

async function loadAssignments() {
  const params: any = { current: 1, size: 200 };
  if (queryParams.courseId) params.courseId = queryParams.courseId;
  const res = await getAssignmentPage(params);
  if (res.code === 200) { assignmentOptions.value = res.data.records; }
}

async function onCourseChange() {
  queryParams.assignmentId = null;
  await loadAssignments();
  loadData();
}

async function loadData() {
  loading.value = true;
  try {
    const params: any = { current: queryParams.current, size: queryParams.size };
    if (queryParams.assignmentId) params.assignmentId = queryParams.assignmentId;
    if (queryParams.courseId) params.courseId = queryParams.courseId;
    const res = await getSubmissionPage(params);
    if (res.code === 200) { dataList.value = res.data.records; total.value = res.data.total; }
  } finally { loading.value = false; }
}

function openGradeDialog(row: any) {
  gradeDialogVisible.value = true;
  gradeForm.id = row.id;
  gradeForm.score = row.score || 0;
  gradeForm.comment = row.comment || "";
}

async function handleGrade() {
  const res = await gradeSubmission(gradeForm.id!, { score: gradeForm.score, comment: gradeForm.comment });
  if (res.code === 200) { message("评分成功", { type: "success" }); gradeDialogVisible.value = false; loadData(); }
}

function handleSizeChange(val: number) { queryParams.size = val; loadData(); }
function handleCurrentChange(val: number) { queryParams.current = val; loadData(); }
onMounted(() => { loadCourses(); loadAssignments(); loadData(); });
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
        <el-form-item label="作业">
          <el-select v-model="queryParams.assignmentId" placeholder="全部作业" clearable filterable style="width: 200px">
            <el-option v-for="a in assignmentOptions" :key="a.id" :label="a.title" :value="a.id" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="loadData">搜索</el-button></el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="assignmentId" label="作业ID" width="100" align="center" />
        <el-table-column prop="studentId" label="学生ID" width="100" align="center" />
        <el-table-column prop="content" label="提交内容" show-overflow-tooltip />
        <el-table-column prop="attachmentUrl" label="附件" width="120">
          <template #default="{ row }">
            <el-link v-if="row.attachmentUrl" :href="row.attachmentUrl" target="_blank" type="primary">下载</el-link>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="isLate" label="迟交" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isLate ? 'danger' : 'success'">{{ row.isLate ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分数" width="80" align="center">
          <template #default="{ row }">{{ row.score !== null ? row.score : '未评' }}</template>
        </el-table-column>
        <el-table-column prop="feedback" label="评语" show-overflow-tooltip />
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 2 ? 'success' : row.status === 1 ? 'warning' : 'info'" size="small">
              {{ row.status === 2 ? '已批改' : row.status === 1 ? '已提交' : '未提交' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openGradeDialog(row)">评分</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="mt-4 flex justify-end" v-model:current-page="queryParams.current" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>

    <el-dialog v-model="gradeDialogVisible" title="作业评分" width="400px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="分数"><el-input-number v-model="gradeForm.score" :min="0" :max="100" style="width:100%" /></el-form-item>
        <el-form-item label="评语"><el-input v-model="gradeForm.comment" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="gradeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleGrade">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.main-content { margin: 24px 24px 0 !important; }
</style>
