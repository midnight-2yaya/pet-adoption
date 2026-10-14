<template>
  <section class="login-page">
    <div class="login-left">
      <h1>欢迎回来，<br />继续寻找<br />温暖的相遇。</h1>
       <p class="login-subtitle">登录你的账号，查看领养进度，随时关注可爱的它。</p>
    </div>
    <div class="login-right">
      <div class="login-card">
        <h2>进入暖宠领养</h2>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
          </el-form-item>
          <el-button class="login-btn" type="primary" :loading="loading" @click="submit">登录</el-button>
        </el-form>
        <div class="go-register">
          还没有账号？ <RouterLink to="/register">立即注册</RouterLink>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "../stores/userStore";

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: "", password: "" });
const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

async function submit() {
  await formRef.value.validate();
  loading.value = true;
  try {
    await userStore.login(form);
    ElMessage.success("登录成功");
    router.push(route.query.redirect || "/");
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-page {
  min-height: calc(100vh - 92px);
  display: grid;
  grid-template-columns: 1.22fr 0.9fr;
  background: linear-gradient(100deg, #fff8f3 0%, #fffdfb 54%, #fff8f3 100%);
}

.login-left {
  padding: 110px 72px 60px 140px;
}

.login-left h1 {
  font-size: 82px;
  line-height: 1.15;
  margin: 0 0 30px;
  font-weight: 900;
  letter-spacing: -4px;
  margin-bottom: 20px;
}

.login-left p {
  font-size: 22px;
  color: var(--warm-muted);
  max-width: 720px;
}

.login-subtitle {
  font-size: 20px;
  color: #8c7a70; 
  line-height: 1.6;
  max-width: 400px;
}

.login-right {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 90px;
}

.login-card {
  width: min(560px, calc(100% - 48px));
  background: #fff;
  border-radius: 0 0 24px 24px;
  padding: 48px 48px 60px; 
  position: relative;
  box-shadow: var(--warm-shadow); 
}

.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 6px;
  background: linear-gradient(90deg, #f5b698, #e07a5f);
  border-radius: 24px 24px 0 0;
}

.login-card :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #ebe5e0 inset; 
  background-color: #faf8f7; 
  transition: all 0.3s;
  padding: 6px 11px; 
}

.login-card :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #e07a5f inset; 
  background-color: #fff;
}

.login-card h2 {
  font-size: 42px;
  margin: 0 0 18px;
}

.login-card p {
  font-size: 20px;
  color: var(--warm-muted);
  margin-bottom: 30px;
}

.login-card .el-form-item {
  margin-bottom: 28px; 
}

.login-btn {
  width: 100%;
  height: 54px;
  font-size: 18px;
  border-radius: 12px; 
  transition: all 0.2s ease;
}

.login-btn:not(.is-loading):hover {
  transform: translateY(-2px); 
  box-shadow: 0 6px 16px rgba(224, 122, 95, 0.35); 
}

.go-register {
  text-align: center;
  margin-top: 24px;
  font-size: 18px;
}
.go-register a {
  color: var(--warm-primary);
  font-weight: 800;
}

@media (max-width: 1000px) {
  .login-page {
    grid-template-columns: 1fr;
  }
  .login-left {
    padding: 48px;
  }
  .login-left h1 {
    font-size: 50px;
  }
  .login-right {
    padding: 0 0 50px;
  }
}
</style>
