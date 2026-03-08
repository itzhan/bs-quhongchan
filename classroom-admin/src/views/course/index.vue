<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getCoursePage, createCourse, updateCourse, deleteCourse } from "@/api/classroom";
import { message } from "@/utils/message";

defineOptions({ name: "CourseList" });

const loading = ref(false);
const dataList = ref([]);
const total = ref(0);
const dialogVisible = ref(false);
const dialogTitle = ref("新增课程");
const formRef = ref();
const queryParams = reactive({ current: 1, size: 10, courseName: "" });
const form = reactive({
  id: null as number | null,
  courseName: "",
  semester: "",
  description: "",
  checkinWeight: 20,
  assignmentWeight: 30,
  examWeight: 50
});

const rules = {
  courseName: [{ required: true, message: "请输入课程名称", trigger: "blur" }],
  semester: [{ required: true, message: "请输入学期", trigger: "blur" }]
};

async function loadData() {
  loading.value = true;
  try {
    const params: any = { current: queryParams.current, size: queryParams.size };
    if (queryParams.courseName) params.courseName = queryParams.courseName;
    const res = await getCoursePage(params);
    if (res.code === 200) { dataList.value = res.data.records; total.value = res.data.total; }
  } finally { loading.value = false; }
}

function handleSearch() { queryParams.current = 1; loadData(); }
function handleReset() { queryParams.courseName = ""; queryParams.current = 1; loadData(); }

function openDialog(title: string, row?: any) {
  dialogTitle.value = title;
  dialogVisible.value = true;
  if (row) { Object.assign(form, row); }
  else { Object.assign(form, { id: null, courseName: "", semester: "", description: "", checkinWeight: 20, assignmentWeight: 30, examWeight: 50 }); }
}

async function handleSubmit() {
  await formRef.value?.validate();
  const data = { ...form };
  if (form.id) {
    const res = await updateCourse(form.id, data);
    if (res.code === 200) { message("修改成功", { type: "success" }); dialogVisible.value = false; loadData(); }
  } else {
    const res = await createCourse(data);
    if (res.code === 200) { message("新增成功", { type: "success" }); dialogVisible.value = false; loadData(); }
  }
}

async function handleDelete(row: any) {
  const res = await deleteCourse(row.id);
  if (res.code === 200) { message("删除成功", { type: "success" }); loadData(); }
}

function handleSizeChange(val: number) { queryParams.size = val; loadData(); }
function handleCurrentChange(val: number) { queryParams.current = val; loadData(); }
onMounted(() => loadData());
</script>

<template>
  <div class="main-content">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryParams" class="mb-4">
        <el-form-item label="课程名称">
          <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="openDialog('新增课程')">新增课程</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="courseName" label="课程名称" width="180" />
        <el-table-column prop="courseCode" label="课程码" width="120" align="center">
          <template #default="{ row }"><el-tag type="info">{{ row.courseCode }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="150" />
        <el-table-column label="权重配置" width="250">
          <template #default="{ row }">签到{{ row.checkinWeight }}% / 作业{{ row.assignmentWeight }}% / 考试{{ row.examWeight }}%</template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openDialog('编辑课程', row)">编辑</el-button>
            <el-popconfirm title="确定删除该课程吗？" @confirm="handleDelete(row)">
              <template #reference><el-button size="small" type="danger" link>删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="mt-4 flex justify-end" v-model:current-page="queryParams.current" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程名称" prop="courseName"><el-input v-model="form.courseName" placeholder="请输入课程名称" /></el-form-item>
        <el-form-item label="学期" prop="semester"><el-input v-model="form.semester" placeholder="例：2024-2025-2" /></el-form-item>
        <el-form-item label="课程描述"><el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入课程描述" /></el-form-item>
        <el-form-item label="签到权重%"><el-input-number v-model="form.checkinWeight" :min="0" :max="100" /></el-form-item>
        <el-form-item label="作业权重%"><el-input-number v-model="form.assignmentWeight" :min="0" :max="100" /></el-form-item>
        <el-form-item label="考试权重%"><el-input-number v-model="form.examWeight" :min="0" :max="100" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.main-content { margin: 24px 24px 0 !important; }
</style>
