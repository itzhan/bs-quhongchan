<script setup lang="ts">
import { ref, markRaw, onMounted } from "vue";
import ReCol from "@/components/ReCol";
import { useDark, randomGradient } from "@pureadmin/utils";
import WelcomeTable from "./components/table/index.vue";
import { ReNormalCountTo } from "@/components/ReCountTo";
import { useRenderFlicker } from "@/components/ReFlicker";
import { ChartBar, ChartLine } from "./components/charts";
import { cardConfigs, dayjs } from "./data";
import { getDashboardStats } from "@/api/classroom";

defineOptions({
  name: "Welcome"
});

const { isDark } = useDark();

// 后端返回的统计数据
const stats = ref<any>({});
const loading = ref(true);

// 卡片数据（响应式）
const cardDataList = ref(
  cardConfigs.map(c => ({
    ...c,
    value: 0
  }))
);

// 进度条数据
const progressData = ref([
  { label: "签到率", percentage: 0, duration: 110, color: "#41b6ff" },
  { label: "作业提交率", percentage: 0, duration: 105, color: "#26ce83" },
  { label: "批改率", percentage: 0, duration: 100, color: "#7846e5" }
]);

// 最新动态
const recentActivities = ref<any[]>([]);

onMounted(async () => {
  try {
    const res: any = await getDashboardStats();
    if (res.code === 200 && res.data) {
      const d = res.data;
      stats.value = d;

      // 填入卡片数值
      cardDataList.value = cardConfigs.map(c => ({
        ...c,
        value: d[c.key] || 0
      }));

      // 进度条
      progressData.value = [
        { label: "签到率", percentage: d.checkinRate || 0, duration: 110, color: "#41b6ff" },
        { label: "作业提交率", percentage: d.submissionRate || 0, duration: 105, color: "#26ce83" },
        { label: "批改率", percentage: d.gradingRate || 0, duration: 100, color: "#7846e5" }
      ];

      // 最新动态
      recentActivities.value = (d.recentActivities || []).map((a: any) => ({
        ...a,
        displayTime: a.createTime
          ? `${a.createTime} ${["周日","周一","周二","周三","周四","周五","周六"][dayjs(a.createTime).day()]}`
          : ""
      }));
    }
  } catch (e) {
    console.error("获取仪表盘数据失败", e);
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div>
    <el-row :gutter="24" justify="space-around">
      <!-- ===== 顶部四卡片 ===== -->
      <re-col
        v-for="(item, index) in cardDataList"
        :key="index"
        v-motion
        class="mb-[18px]"
        :value="6"
        :md="12"
        :sm="12"
        :xs="24"
        :initial="{ opacity: 0, y: 100 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 80 * (index + 1) } }"
      >
        <el-card class="line-card" shadow="never">
          <div class="flex justify-between">
            <span class="text-md font-medium">{{ item.name }}</span>
            <div
              class="w-8 h-8 flex justify-center items-center rounded-md"
              :style="{ backgroundColor: isDark ? 'transparent' : item.bgColor }"
            >
              <IconifyIconOffline
                :icon="item.icon"
                :color="item.color"
                width="18"
                height="18"
              />
            </div>
          </div>
          <div class="flex justify-between items-start mt-3">
            <div class="w-1/2">
              <ReNormalCountTo
                :duration="item.duration"
                :fontSize="'1.6em'"
                :startVal="0"
                :endVal="item.value"
              />
              <p class="font-medium text-text_color_regular text-sm mt-1">
                {{ item.name === '签到活动' ? '累计活动数' : '系统注册量' }}
              </p>
            </div>
          </div>
        </el-card>
      </re-col>

      <!-- ===== 柱状图：课程统计 ===== -->
      <re-col
        v-motion
        class="mb-[18px]"
        :value="18"
        :xs="24"
        :initial="{ opacity: 0, y: 100 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 400 } }"
      >
        <el-card class="bar-card" shadow="never">
          <div class="flex justify-between">
            <span class="text-md font-medium">课程数据概览</span>
            <el-tag type="info" size="small">Top 课程</el-tag>
          </div>
          <div class="flex justify-between items-start mt-3">
            <ChartBar
              :courseNames="stats.courseNames || []"
              :studentData="stats.courseStudentCounts || []"
              :assignmentData="stats.courseAssignmentCounts || []"
            />
          </div>
        </el-card>
      </re-col>

      <!-- ===== 进度条：签到率 / 提交率 / 批改率 ===== -->
      <re-col
        v-motion
        class="mb-[18px]"
        :value="6"
        :xs="24"
        :initial="{ opacity: 0, y: 100 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 480 } }"
      >
        <el-card shadow="never">
          <div class="flex justify-between">
            <span class="text-md font-medium">教学数据</span>
          </div>
          <div
            v-for="(item, index) in progressData"
            :key="index"
            :class="[
              'flex',
              'justify-between',
              'items-start',
              index === 0 ? 'mt-8' : 'mt-[2.15rem]'
            ]"
          >
            <el-progress
              :text-inside="true"
              :percentage="item.percentage"
              :stroke-width="21"
              :color="item.color"
              striped
              striped-flow
              :duration="item.duration"
            />
            <span class="text-nowrap ml-2 text-text_color_regular text-sm">
              {{ item.label }}
            </span>
          </div>
        </el-card>
      </re-col>

      <!-- ===== 数据统计表格：最近课程 ===== -->
      <re-col
        v-motion
        class="mb-[18px]"
        :value="18"
        :xs="24"
        :initial="{ opacity: 0, y: 100 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 560 } }"
      >
        <el-card shadow="never">
          <div class="flex justify-between">
            <span class="text-md font-medium">最近课程</span>
          </div>
          <el-scrollbar max-height="504" class="mt-3">
            <WelcomeTable :tableData="stats.recentCourses || []" />
          </el-scrollbar>
        </el-card>
      </re-col>

      <!-- ===== 最新动态：操作日志 ===== -->
      <re-col
        v-motion
        class="mb-[18px]"
        :value="6"
        :xs="24"
        :initial="{ opacity: 0, y: 100 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 640 } }"
      >
        <el-card shadow="never">
          <div class="flex justify-between">
            <span class="text-md font-medium">最新动态</span>
          </div>
          <el-scrollbar max-height="504" class="mt-3">
            <el-timeline>
              <el-timeline-item
                v-for="(item, index) in recentActivities"
                :key="index"
                center
                placement="top"
                :icon="
                  markRaw(
                    useRenderFlicker({
                      background: randomGradient({ randomizeHue: true })
                    })
                  )
                "
                :timestamp="item.displayTime"
              >
                <p class="text-text_color_regular text-sm">
                  {{ item.username }} {{ item.operation }}
                </p>
              </el-timeline-item>
            </el-timeline>
            <el-empty
              v-if="recentActivities.length === 0 && !loading"
              description="暂无操作记录"
              :image-size="60"
            />
          </el-scrollbar>
        </el-card>
      </re-col>
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
:deep(.el-card) {
  --el-card-border-color: none;

  /* 进度条宽度 */
  .el-progress--line {
    width: 85%;
  }

  /* 进度条字体大小 */
  .el-progress-bar__innerText {
    font-size: 15px;
  }

  /* 隐藏 el-scrollbar 滚动条 */
  .el-scrollbar__bar {
    display: none;
  }

  /* el-timeline 每一项上下、左右边距 */
  .el-timeline-item {
    margin: 0 6px;
  }
}

:deep(.el-timeline.is-start) {
  padding-left: 0;
}

.main-content {
  margin: 20px 20px 0 !important;
}
</style>
