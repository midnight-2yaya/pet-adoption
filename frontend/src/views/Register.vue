<template>
  <section class="register-page">
    <div class="login-card">
      <h2>注册账号</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        
        <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="3-30 位" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" show-password type="password" placeholder="至少 6 位" />
        </el-form-item>
                
        <el-form-item label="昵称">
            <el-input v-model="form.nickname" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" />
        </el-form-item>

        <el-button type="primary" :loading="loading" @click="submit">创建账号</el-button>
    </el-form>

    <RouterLink to="/login">已有账号？去登录</RouterLink>
    </div>
  </section>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "../stores/userStore";

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({
  username: "",
  password: "",
  nickname: "",
  phone: "",
  email: "",
});
const rules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 30, message: "用户名长度3-30位", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码至少6位", trigger: "blur" },
  ],
  phone: [
    {
      pattern: /^$|^1[3-9]\d{9}$/,
      message: "手机号格式不正确",
      trigger: "blur",
    },
  ],
};

async function submit() {
  await formRef.value.validate();
  loading.value = true;
  try {
    await userStore.register(form);
    ElMessage.success("注册成功，请登录");
    router.push("/login");
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.register-page {
  min-height: calc(100vh - 92px);
  display: grid;
  place-items: center;
}

.login-card {
  width: min(520px, calc(100% - 40px));
  background: #fff;
  border-radius: 24px;
  padding: 42px;
  box-shadow: var(--warm-shadow);
}

.login-card h2 {
  font-size: 40px;
  margin: 0 0 12px;
}

.login-card p {
  color: var(--warm-muted);
}
</style>
