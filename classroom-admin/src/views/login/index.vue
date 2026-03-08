<script setup lang="ts">
import Motion from "./utils/motion";
import { useRouter } from "vue-router";
import { message } from "@/utils/message";
import { debounce } from "@pureadmin/utils";
import { useEventListener } from "@vueuse/core";
import type { FormInstance } from "element-plus";
import { useUserStoreHook } from "@/store/modules/user";
import { initRouter, getTopMenu } from "@/router/utils";
import { useDataThemeChange } from "@/layout/hooks/useDataThemeChange";
import { ref, reactive } from "vue";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";

import dayIcon from "@/assets/svg/day.svg?component";
import darkIcon from "@/assets/svg/dark.svg?component";
import Lock from "~icons/ri/lock-fill";
import User from "~icons/ri/user-3-fill";

defineOptions({
  name: "Login"
});

const router = useRouter();
const loading = ref(false);
const disabled = ref(false);
const ruleFormRef = ref<FormInstance>();

const { dataTheme, themeMode, dataThemeChange } = useDataThemeChange();
dataThemeChange(themeMode.value);

const ruleForm = reactive({
  username: "admin",
  password: "admin123"
});

const loginRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }]
};

const onLogin = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(valid => {
    if (valid) {
      loading.value = true;
      useUserStoreHook()
        .loginByUsername({
          username: ruleForm.username,
          password: ruleForm.password
        })
        .then(async () => {
          await initRouter();
          disabled.value = true;
          router.push(getTopMenu(true).path).then(() => {
            message("登录成功", { type: "success" });
          });
        })
        .catch(_err => {
          message("登录失败，请检查用户名和密码", { type: "error" });
        })
        .finally(() => {
          disabled.value = false;
          loading.value = false;
        });
    }
  });
};

const immediateDebounce: any = debounce(
  formRef => onLogin(formRef),
  1000,
  true
);

useEventListener(document, "keydown", ({ code }) => {
  if (
    ["Enter", "NumpadEnter"].includes(code) &&
    !disabled.value &&
    !loading.value
  )
    immediateDebounce(ruleFormRef.value);
});
</script>

<template>
  <div class="login-page">
    <!-- 右上角主题切换 -->
    <div class="theme-switch">
      <el-switch
        v-model="dataTheme"
        inline-prompt
        :active-icon="dayIcon"
        :inactive-icon="darkIcon"
        @change="dataThemeChange"
      />
    </div>

    <!-- 左侧装饰区 -->
    <div class="login-left">
      <div class="left-content">
        <div class="brand-icon">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none">
            <path d="M12 3L1 9L5 11.18V17.18L12 21L19 17.18V11.18L21 10.09V17H23V9L12 3ZM18.82 9L12 12.72L5.18 9L12 5.28L18.82 9ZM17 15.99L12 18.72L7 15.99V12.27L12 15L17 12.27V15.99Z" fill="currentColor"/>
          </svg>
        </div>
        <h1 class="brand-title">学生课堂管理系统</h1>
        <p class="brand-subtitle">后台管理平台</p>
        <div class="feature-list">
          <div class="feature-item">
            <span class="dot"></span>
            <span>课程与班级管理</span>
          </div>
          <div class="feature-item">
            <span class="dot"></span>
            <span>签到与考勤统计</span>
          </div>
          <div class="feature-item">
            <span class="dot"></span>
            <span>作业发布与批改</span>
          </div>
          <div class="feature-item">
            <span class="dot"></span>
            <span>学生成绩分析</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录区 -->
    <div class="login-right">
      <div class="login-box">
        <Motion>
          <h2 class="login-title">管理员登录</h2>
          <p class="login-desc">请使用管理员账号登录后台管理系统</p>
        </Motion>

        <el-form
          ref="ruleFormRef"
          :model="ruleForm"
          :rules="loginRules"
          size="large"
          class="login-form"
        >
          <Motion :delay="100">
            <el-form-item prop="username">
              <el-input
                v-model="ruleForm.username"
                clearable
                placeholder="请输入用户名"
                :prefix-icon="useRenderIcon(User)"
              />
            </el-form-item>
          </Motion>

          <Motion :delay="200">
            <el-form-item prop="password">
              <el-input
                v-model="ruleForm.password"
                clearable
                show-password
                placeholder="请输入密码"
                :prefix-icon="useRenderIcon(Lock)"
              />
            </el-form-item>
          </Motion>

          <Motion :delay="300">
            <el-form-item>
              <el-button
                class="login-btn"
                type="primary"
                size="default"
                :loading="loading"
                :disabled="disabled"
                @click="onLogin(ruleFormRef)"
              >
                登 录
              </el-button>
            </el-form-item>
          </Motion>
        </el-form>

        <Motion :delay="400">
          <div class="login-footer">
            <p>基于 Spring Boot 的学生课堂管理系统</p>
          </div>
        </Motion>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  overflow: hidden;
}

.theme-switch {
  position: fixed;
  top: 16px;
  right: 20px;
  z-index: 10;
}

/* ---- 左侧装饰 ---- */
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #1e3a5f 0%, #2563eb 60%, #3b82f6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  position: relative;
  overflow: hidden;
}
.login-left::before {
  content: '';
  position: absolute;
  width: 500px;
  height: 500px;
  border-radius: 50%;
  background: rgba(255,255,255,0.05);
  top: -120px;
  right: -120px;
}
.login-left::after {
  content: '';
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(255,255,255,0.04);
  bottom: -80px;
  left: -60px;
}

.left-content {
  position: relative;
  z-index: 2;
  padding: 40px;
  max-width: 400px;
}

.brand-icon {
  width: 80px;
  height: 80px;
  background: rgba(255,255,255,0.15);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  backdrop-filter: blur(10px);
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 2px;
  margin-bottom: 8px;
}

.brand-subtitle {
  font-size: 16px;
  opacity: 0.7;
  margin-bottom: 48px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  opacity: 0.85;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #60a5fa;
  flex-shrink: 0;
}

/* ---- 右侧登录 ---- */
.login-right {
  width: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--el-bg-color);
}

.login-box {
  width: 380px;
  padding: 20px;
}

.login-title {
  font-size: 26px;
  font-weight: 700;
  color: var(--el-text-color-primary);
  margin-bottom: 8px;
}

.login-desc {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-bottom: 36px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  padding: 4px 12px;
}

.login-btn {
  width: 100%;
  height: 44px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 8px;
  margin-top: 8px;
}

.login-footer {
  text-align: center;
  margin-top: 32px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

/* ---- 暗黑模式适配 ---- */
html.dark .login-left {
  background: linear-gradient(135deg, #0f172a 0%, #1e3a5f 60%, #1e40af 100%);
}

/* ---- 响应式 ---- */
@media (max-width: 900px) {
  .login-left {
    display: none;
  }
  .login-right {
    width: 100%;
  }
}
</style>
