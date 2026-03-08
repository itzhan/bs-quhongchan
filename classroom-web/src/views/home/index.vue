<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { NCard, NButton, NGrid, NGi, NStatistic, NIcon, NSpace, NTag } from 'naive-ui'
import {
  BookOutline, PeopleOutline, CheckmarkCircleOutline,
  TrophyOutline, RocketOutline, ShieldCheckmarkOutline
} from '@vicons/ionicons5'

const router = useRouter()
const userStore = useUserStore()

const features = [
  { icon: BookOutline, title: '课程管理', desc: '创建课程、加入课程、课程码快速入课' },
  { icon: CheckmarkCircleOutline, title: '课堂签到', desc: '支持普通签到、定位签到、手势签到' },
  { icon: TrophyOutline, title: '作业管理', desc: '发布作业、在线提交、教师评分' },
  { icon: RocketOutline, title: '成绩分析', desc: '加权成绩自动计算、统计分析' },
  { icon: PeopleOutline, title: '互动交流', desc: '课程公告、站内消息、资料共享' },
  { icon: ShieldCheckmarkOutline, title: '权限管理', desc: '学生、教师、管理员三角色体系' }
]

const stats = [
  { label: '注册用户', value: '1200+' },
  { label: '开设课程', value: '86' },
  { label: '签到次数', value: '15000+' },
  { label: '提交作业', value: '8600+' }
]
</script>

<template>
  <div class="home-page">
    <!-- Hero -->
    <section class="hero">
      <div class="hero-content">
        <n-tag type="info" size="small" round style="margin-bottom: 16px;">基于 Spring Boot 构建</n-tag>
        <h1>学生课堂管理系统</h1>
        <p class="hero-desc">
          为高校师生打造的一站式课堂管理平台，覆盖课程管理、课堂签到、作业提交、
          成绩分析等核心教学场景，助力智慧教学。
        </p>
        <n-space :size="16" style="margin-top: 28px;">
          <n-button type="primary" size="large" @click="router.push(userStore.isLoggedIn() ? '/course' : '/login')">
            {{ userStore.isLoggedIn() ? '进入课程' : '立即登录' }}
          </n-button>
          <n-button size="large" secondary @click="router.push('/course')">浏览课程</n-button>
        </n-space>
      </div>
      <div class="hero-visual">
        <svg width="320" height="240" viewBox="0 0 320 240" fill="none">
          <rect x="20" y="40" width="280" height="160" rx="16" fill="#dbeafe" />
          <rect x="40" y="60" width="120" height="80" rx="8" fill="#2563eb" opacity="0.15" />
          <rect x="40" y="65" width="80" height="8" rx="4" fill="#2563eb" opacity="0.6" />
          <rect x="40" y="80" width="100" height="6" rx="3" fill="#2563eb" opacity="0.3" />
          <rect x="40" y="93" width="60" height="6" rx="3" fill="#2563eb" opacity="0.3" />
          <rect x="40" y="115" width="50" height="20" rx="6" fill="#2563eb" />
          <rect x="180" y="60" width="100" height="120" rx="8" fill="#fff" />
          <circle cx="230" cy="90" r="20" fill="#dbeafe" />
          <rect x="210" y="120" width="40" height="6" rx="3" fill="#2563eb" opacity="0.4" />
          <rect x="200" y="135" width="60" height="6" rx="3" fill="#2563eb" opacity="0.2" />
          <rect x="205" y="155" width="50" height="16" rx="6" fill="#2563eb" opacity="0.8" />
        </svg>
      </div>
    </section>

    <!-- Stats -->
    <section class="stats-section">
      <n-grid :cols="4" :x-gap="20" :y-gap="20" responsive="screen" :item-responsive="true">
        <n-gi v-for="(s, i) in stats" :key="i" span="4 m:2 l:1">
          <n-card class="stat-card" :bordered="false">
            <n-statistic :label="s.label" :value="s.value" />
          </n-card>
        </n-gi>
      </n-grid>
    </section>

    <!-- Features -->
    <section class="features-section">
      <div class="section-header">
        <h2>核心功能</h2>
        <p>覆盖课堂教学全流程的功能模块</p>
      </div>
      <n-grid :cols="3" :x-gap="20" :y-gap="20" responsive="screen" :item-responsive="true">
        <n-gi v-for="(f, i) in features" :key="i" span="3 m:1">
          <n-card class="feature-card" hoverable :bordered="false">
            <div class="feature-icon">
              <n-icon :component="f.icon" :size="28" color="#2563eb" />
            </div>
            <h3>{{ f.title }}</h3>
            <p>{{ f.desc }}</p>
          </n-card>
        </n-gi>
      </n-grid>
    </section>
  </div>
</template>

<style scoped>
.home-page { padding-bottom: 40px; }

.hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 48px 0 56px;
  gap: 40px;
}
.hero-content { max-width: 520px; }
.hero-content h1 {
  font-size: 36px;
  font-weight: 700;
  line-height: 1.3;
  background: linear-gradient(135deg, #1e40af, #2563eb);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.hero-desc {
  font-size: 16px;
  color: var(--text-secondary);
  margin-top: 16px;
  line-height: 1.8;
}
.hero-visual {
  flex-shrink: 0;
}

.stats-section {
  margin-bottom: 48px;
}
.stat-card {
  text-align: center;
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  border-radius: var(--radius);
}

.features-section { margin-bottom: 40px; }
.section-header {
  text-align: center;
  margin-bottom: 32px;
}
.section-header h2 {
  font-size: 24px;
  font-weight: 600;
}
.section-header p {
  color: var(--text-secondary);
  margin-top: 8px;
}
.feature-card {
  text-align: center;
  border-radius: var(--radius);
  transition: transform 0.2s;
}
.feature-card:hover { transform: translateY(-4px); }
.feature-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: var(--primary-light);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}
.feature-card h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}
.feature-card p {
  font-size: 13px;
  color: var(--text-secondary);
}

@media (max-width: 768px) {
  .hero { flex-direction: column; text-align: center; padding: 32px 0; }
  .hero-content h1 { font-size: 28px; }
  .hero-visual { display: none; }
}
</style>
