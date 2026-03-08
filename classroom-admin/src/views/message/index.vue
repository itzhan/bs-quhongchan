<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getMessagePage, sendMessage, markMessageRead } from "@/api/classroom";
import { message } from "@/utils/message";

defineOptions({ name: "MessageList" });

const loading = ref(false);
const dataList = ref([]);
const total = ref(0);
const dialogVisible = ref(false);
const formRef = ref();
const queryParams = reactive({ current: 1, size: 10 });
const form = reactive({ receiverId: null as number | null, title: "", content: "" });

const rules = {
  receiverId: [{ required: true, message: "请输入接收者ID", trigger: "blur" }],
  title: [{ required: true, message: "请输入消息标题", trigger: "blur" }],
  content: [{ required: true, message: "请输入消息内容", trigger: "blur" }]
};

async function loadData() {
  loading.value = true;
  try {
    const params: any = { current: queryParams.current, size: queryParams.size };
    const res = await getMessagePage(params);
    if (res.code === 200) { dataList.value = res.data.records; total.value = res.data.total; }
  } finally { loading.value = false; }
}

function openDialog() {
  dialogVisible.value = true;
  Object.assign(form, { receiverId: null, title: "", content: "" });
}

async function handleSubmit() {
  await formRef.value?.validate();
  const res = await sendMessage({ ...form });
  if (res.code === 200) { message("发送成功", { type: "success" }); dialogVisible.value = false; loadData(); }
}

async function handleRead(row: any) {
  if (row.isRead) return;
  const res = await markMessageRead(row.id);
  if (res.code === 200) { message("已标记为已读", { type: "success" }); loadData(); }
}

function handleSizeChange(val: number) { queryParams.size = val; loadData(); }
function handleCurrentChange(val: number) { queryParams.current = val; loadData(); }
onMounted(() => loadData());
</script>

<template>
  <div class="main-content">
    <el-card shadow="never">
      <div class="mb-4">
        <el-button type="success" @click="openDialog">发送消息</el-button>
        <el-button type="primary" @click="loadData">刷新</el-button>
      </div>

      <el-table v-loading="loading" :data="dataList" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="senderId" label="发送者ID" width="100" align="center" />
        <el-table-column prop="receiverId" label="接收者ID" width="100" align="center" />
        <el-table-column prop="title" label="标题" width="200" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="isRead" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isRead ? 'info' : 'danger'">{{ row.isRead ? '已读' : '未读' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发送时间" width="180" />
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-if="!row.isRead" size="small" type="primary" link @click="handleRead(row)">标记已读</el-button>
            <span v-else class="text-gray-400">已读</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="mt-4 flex justify-end" v-model:current-page="queryParams.current" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>

    <el-dialog v-model="dialogVisible" title="发送消息" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="接收者ID" prop="receiverId"><el-input-number v-model="form.receiverId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" placeholder="请输入消息标题" /></el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入消息内容" /></el-form-item>
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
