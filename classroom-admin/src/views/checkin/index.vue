<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getCheckinPage, createCheckin, endCheckin } from "@/api/classroom";
import { message } from "@/utils/message";

defineOptions({ name: "CheckinList" });

const loading = ref(false);
const dataList = ref([]);
const total = ref(0);
const dialogVisible = ref(false);
const formRef = ref();
const queryParams = reactive({ current: 1, size: 10, courseId: null as number | null });
const form = reactive({ courseId: null as number | null, type: 0, duration: 10, description: "" });

const typeOptions = [
  { label: "普通签到", value: 0 },
  { label: "定位签到", value: 1 },
  { label: "手势签到", value: 2 }
];

const rules = {
  courseId: [{ required: true, message: "请选择课程", trigger: "blur" }],
  duration: [{ required: true, message: "请输入签到时长", trigger: "blur" }]
};

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
  Object.assign(form, { courseId: null, type: 0, duration: 10, description: "" });
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

function handleSizeChange(val: number) { queryParams.size = val; loadData(); }
function handleCurrentChange(val: number) { queryParams.current = val; loadData(); }
onMounted(() => loadData());
</script>

<template>
  <div class="main-content">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryParams" class="mb-4">
        <el-form-item label="课程ID">
          <el-input-number v-model="queryParams.courseId" :min="1" controls-position="right" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button type="success" @click="openDialog">发起签到</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="courseId" label="课程ID" width="100" align="center" />
        <el-table-column prop="type" label="签到类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag>{{ row.type === 0 ? '普通签到' : row.type === 1 ? '定位签到' : '手势签到' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分)" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '进行中' : '已结束' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" title="发起签到" width="450px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="课程ID" prop="courseId"><el-input-number v-model="form.courseId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="签到类型"><el-select v-model="form.type" style="width:100%"><el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="时长(分)" prop="duration"><el-input-number v-model="form.duration" :min="1" :max="120" style="width:100%" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
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
