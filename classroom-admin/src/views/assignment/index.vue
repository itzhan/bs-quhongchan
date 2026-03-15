<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getAssignmentPage, createAssignment, updateAssignment, deleteAssignment, getCoursePage } from "@/api/classroom";
import { message } from "@/utils/message";

defineOptions({ name: "AssignmentList" });

const loading = ref(false);
const dataList = ref([]);
const total = ref(0);
const dialogVisible = ref(false);
const dialogTitle = ref("新增作业");
const formRef = ref();
const courseOptions = ref<any[]>([]);
const queryParams = reactive({ current: 1, size: 10, courseId: null as number | null });
const form = reactive({ id: null as number | null, courseId: null as number | null, title: "", content: "", deadline: "", attachmentUrl: "" });

const rules = {
  courseId: [{ required: true, message: "请选择课程", trigger: "change" }],
  title: [{ required: true, message: "请输入作业标题", trigger: "blur" }],
  deadline: [{ required: true, message: "请选择截止时间", trigger: "change" }]
};

async function loadCourses() {
  const res = await getCoursePage({ current: 1, size: 200 });
  if (res.code === 200) { courseOptions.value = res.data.records; }
}

async function loadData() {
  loading.value = true;
  try {
    const params: any = { current: queryParams.current, size: queryParams.size };
    if (queryParams.courseId) params.courseId = queryParams.courseId;
    const res = await getAssignmentPage(params);
    if (res.code === 200) { dataList.value = res.data.records; total.value = res.data.total; }
  } finally { loading.value = false; }
}

function openDialog(title: string, row?: any) {
  dialogTitle.value = title;
  dialogVisible.value = true;
  if (row) { Object.assign(form, row); }
  else { Object.assign(form, { id: null, courseId: queryParams.courseId, title: "", content: "", deadline: "", attachmentUrl: "" }); }
}

async function handleSubmit() {
  await formRef.value?.validate();
  if (form.id) {
    const res = await updateAssignment(form.id, { ...form });
    if (res.code === 200) { message("修改成功", { type: "success" }); dialogVisible.value = false; loadData(); }
  } else {
    const res = await createAssignment({ ...form });
    if (res.code === 200) { message("发布成功", { type: "success" }); dialogVisible.value = false; loadData(); }
  }
}

async function handleDelete(row: any) {
  const res = await deleteAssignment(row.id);
  if (res.code === 200) { message("删除成功", { type: "success" }); loadData(); }
}

/** 根据课程ID找课程名 */
function getCourseName(courseId: number) {
  const c = courseOptions.value.find(c => c.id === courseId);
  return c ? c.courseName : courseId;
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
          <el-select v-model="queryParams.courseId" placeholder="全部课程" clearable filterable style="width: 220px" @change="loadData">
            <el-option v-for="c in courseOptions" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button type="success" @click="openDialog('发布作业')">发布作业</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column label="所属课程" width="150">
          <template #default="{ row }">{{ getCourseName(row.courseId) }}</template>
        </el-table-column>
        <el-table-column prop="title" label="作业标题" width="200" />
        <el-table-column prop="content" label="作业内容" show-overflow-tooltip />
        <el-table-column prop="deadline" label="截止时间" width="180" />
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openDialog('编辑作业', row)">编辑</el-button>
            <el-popconfirm title="确定删除该作业吗？" @confirm="handleDelete(row)">
              <template #reference><el-button size="small" type="danger" link>删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="mt-4 flex justify-end" v-model:current-page="queryParams.current" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="form.courseId" placeholder="请选择课程" filterable style="width: 100%">
            <el-option v-for="c in courseOptions" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" placeholder="请输入作业标题" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入作业内容" /></el-form-item>
        <el-form-item label="截止时间" prop="deadline"><el-date-picker v-model="form.deadline" type="datetime" placeholder="选择截止时间" style="width:100%" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="附件链接"><el-input v-model="form.attachmentUrl" placeholder="附件URL" /></el-form-item>
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
