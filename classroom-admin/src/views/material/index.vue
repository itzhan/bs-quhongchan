<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getMaterialPage, createMaterial, deleteMaterial, uploadFile } from "@/api/classroom";
import { message } from "@/utils/message";

defineOptions({ name: "MaterialList" });

const loading = ref(false);
const dataList = ref([]);
const total = ref(0);
const dialogVisible = ref(false);
const formRef = ref();
const queryParams = reactive({ current: 1, size: 10, courseId: null as number | null });
const form = reactive({ courseId: null as number | null, title: "", description: "", fileUrl: "" });

const rules = {
  courseId: [{ required: true, message: "请输入课程ID", trigger: "blur" }],
  title: [{ required: true, message: "请输入资料标题", trigger: "blur" }],
  fileUrl: [{ required: true, message: "请上传文件", trigger: "blur" }]
};

async function loadData() {
  loading.value = true;
  try {
    const params: any = { current: queryParams.current, size: queryParams.size };
    if (queryParams.courseId) params.courseId = queryParams.courseId;
    const res = await getMaterialPage(params);
    if (res.code === 200) { dataList.value = res.data.records; total.value = res.data.total; }
  } finally { loading.value = false; }
}

function openDialog() {
  dialogVisible.value = true;
  Object.assign(form, { courseId: null, title: "", description: "", fileUrl: "" });
}

async function handleUpload(file: any) {
  const formData = new FormData();
  formData.append("file", file.file);
  const res = await uploadFile(formData);
  if (res.code === 200) { form.fileUrl = res.data; message("上传成功", { type: "success" }); }
  return false;
}

async function handleSubmit() {
  await formRef.value?.validate();
  const res = await createMaterial({ ...form });
  if (res.code === 200) { message("上传成功", { type: "success" }); dialogVisible.value = false; loadData(); }
}

async function handleDelete(row: any) {
  const res = await deleteMaterial(row.id);
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
        <el-form-item label="课程ID"><el-input-number v-model="queryParams.courseId" :min="1" controls-position="right" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button type="success" @click="openDialog">上传资料</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="courseId" label="课程ID" width="100" align="center" />
        <el-table-column prop="title" label="资料标题" width="200" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="fileUrl" label="文件" width="120">
          <template #default="{ row }">
            <el-link :href="row.fileUrl" target="_blank" type="primary">下载</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-popconfirm title="确定删除该资料吗？" @confirm="handleDelete(row)">
              <template #reference><el-button size="small" type="danger" link>删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="mt-4 flex justify-end" v-model:current-page="queryParams.current" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>

    <el-dialog v-model="dialogVisible" title="上传资料" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="课程ID" prop="courseId"><el-input-number v-model="form.courseId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" placeholder="请输入资料标题" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="文件" prop="fileUrl">
          <el-upload :http-request="handleUpload" :show-file-list="false">
            <el-button type="primary">选择文件</el-button>
          </el-upload>
          <span v-if="form.fileUrl" class="ml-2 text-green-500">已上传</span>
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
