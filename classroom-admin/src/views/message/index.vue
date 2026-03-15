<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getMessagePage, sendMessage, getCoursePage } from "@/api/classroom";
import { message } from "@/utils/message";

defineOptions({ name: "MessageList" });

const loading = ref(false);
const dataList = ref([]);
const total = ref(0);
const dialogVisible = ref(false);
const formRef = ref();
const courseOptions = ref<any[]>([]);
const queryParams = reactive({ current: 1, size: 10, courseId: null as number | null });
const form = reactive({ courseId: null as number | null, content: "", isAnonymous: 0 });

const rules = {
  courseId: [{ required: true, message: "请选择课程", trigger: "change" }],
  content: [{ required: true, message: "请输入留言内容", trigger: "blur" }]
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
    const res = await getMessagePage(params);
    if (res.code === 200) { dataList.value = res.data.records; total.value = res.data.total; }
  } finally { loading.value = false; }
}

function openDialog() {
  dialogVisible.value = true;
  Object.assign(form, { courseId: queryParams.courseId, content: "", isAnonymous: 0 });
}

async function handleSubmit() {
  await formRef.value?.validate();
  const res = await sendMessage({ ...form });
  if (res.code === 200) { message("发送成功", { type: "success" }); dialogVisible.value = false; loadData(); }
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
          <el-button type="primary" @click="loadData">刷新</el-button>
          <el-button type="success" @click="openDialog">发送留言</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="senderName" label="发送者" width="120">
          <template #default="{ row }">
            {{ row.isAnonymous === 1 ? '匿名' : (row.senderName || row.senderId) }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="留言内容" show-overflow-tooltip />
        <el-table-column prop="parentId" label="回复" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.parentId" size="small" type="info">#{{ row.parentId }}</el-tag>
            <span v-else class="text-gray-400">—</span>
          </template>
        </el-table-column>
        <el-table-column prop="isAnonymous" label="匿名" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isAnonymous === 1 ? 'warning' : 'info'" size="small">{{ row.isAnonymous === 1 ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发送时间" width="180" />
      </el-table>
      <el-pagination class="mt-4 flex justify-end" v-model:current-page="queryParams.current" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>

    <el-dialog v-model="dialogVisible" title="发送留言" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="form.courseId" placeholder="请选择课程" filterable style="width: 100%">
            <el-option v-for="c in courseOptions" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="留言内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入留言内容" /></el-form-item>
        <el-form-item label="匿名">
          <el-switch v-model="form.isAnonymous" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.main-content { margin: 24px 24px 0 !important; }
</style>
