<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getCheckinPage, createCheckin, endCheckin, getCoursePage } from "@/api/classroom";
import { message } from "@/utils/message";

defineOptions({ name: "CheckinList" });

const loading = ref(false);
const dataList = ref([]);
const total = ref(0);
const dialogVisible = ref(false);
const formRef = ref();
const courseOptions = ref<any[]>([]);
const queryParams = reactive({ current: 1, size: 10, courseId: null as number | null });
const form = reactive({
  courseId: null as number | null,
  title: "",
  checkinType: 1,
  startTime: "",
  endTime: ""
});

const typeOptions = [
  { label: "普通签到", value: 1 },
  { label: "定位签到", value: 2 },
  { label: "手势签到", value: 3 }
];

const rules = {
  courseId: [{ required: true, message: "请选择课程", trigger: "change" }],
  startTime: [{ required: true, message: "请选择开始时间", trigger: "change" }],
  endTime: [{ required: true, message: "请选择截止时间", trigger: "change" }]
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
    const res = await getCheckinPage(params);
    if (res.code === 200) { dataList.value = res.data.records; total.value = res.data.total; }
  } finally { loading.value = false; }
}

function openDialog() {
  dialogVisible.value = true;
  Object.assign(form, { courseId: null, title: "", checkinType: 1, startTime: "", endTime: "" });
}

async function handleSubmit() {
  await formRef.value?.validate();
  const res = await createCheckin({ ...form });
  if (res.code === 200) { message("发起签到成功", { type: "success" }); dialogVisible.value = false; loadData(); }
}

async function handleEnd(row: any) {
  const res = await endCheckin(row.id);
  if (res.code === 200) { message("签到已结束", { type: "success" }); loadData(); }
}

/** 计算签到时长（分钟） */
function getDuration(row: any) {
  if (!row.startTime || !row.endTime) return "-";
  const start = new Date(row.startTime).getTime();
  const end = new Date(row.endTime).getTime();
  const min = Math.round((end - start) / 60000);
  return min > 0 ? `${min} 分钟` : "-";
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
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button type="success" @click="openDialog">发起签到</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="courseId" label="课程ID" width="100" align="center" />
        <el-table-column prop="title" label="签到标题" width="150" show-overflow-tooltip />
        <el-table-column prop="checkinType" label="签到类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag>{{ row.checkinType === 1 ? '普通签到' : row.checkinType === 2 ? '定位签到' : '手势签到' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时长" width="100" align="center">
          <template #default="{ row }">{{ getDuration(row) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '进行中' : '已结束' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="截止时间" width="180" />
        <el-table-column prop="createTime" label="发起时间" width="180" />
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" size="small" type="danger" link @click="handleEnd(row)">结束</el-button>
            <span v-else class="text-gray-400">已结束</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="mt-4 flex justify-end" v-model:current-page="queryParams.current" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>

    <el-dialog v-model="dialogVisible" title="发起签到" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="form.courseId" placeholder="请选择课程" filterable style="width: 100%">
            <el-option v-for="c in courseOptions" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="签到标题"><el-input v-model="form.title" placeholder="如：第3周签到" /></el-form-item>
        <el-form-item label="签到类型">
          <el-select v-model="form.checkinType" style="width: 100%">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
        <el-form-item label="截止时间" prop="endTime">
          <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择截止时间" style="width: 100%" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
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
