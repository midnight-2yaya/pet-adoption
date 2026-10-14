<template>
  <section class="profile page">
    <div class="profile-card soft-card">
      <div class="avatar-side">
        <ImageUploadSingle v-model="form.avatar" />
      </div>
      <el-form ref="formRef" class="profile-form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="用户名">
            <el-input :model-value="userStore.user?.username" disabled />
        </el-form-item>
            
        <el-form-item label="昵称">
            <el-input v-model="form.nickname" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
                 
        <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" />
        </el-form-item>
        
        <el-button type="primary" size="large" :loading="saving" @click="save">保存修改</el-button>
        
      </el-form>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import ImageUploadSingle from "../components/base/ImageUploadSingle.vue";
import { useUserStore } from "../stores/userStore";
const userStore = useUserStore()
const formRef = ref(null)
const saving = ref(false)
const form = reactive({ nickname: "", phone: "", email: "", avatar: "" });
const rules = {
  phone: [
    {
      pattern: /^$|^1[3-9]\d{9}$/,
      message: "手机号格式不正确",
      trigger: "blur",
    },
  ],
  email: [{ type: "email", message: "邮箱格式不正确", trigger: "blur" }],
};

onMounted(() => {
  const u = userStore.user || {};
  form.nickname = u.nickname || "";
  form.phone = u.phone || "";
  form.email = u.email || "";
  form.avatar = u.avatar || "";
});

async function save() {
  await formRef.value.validate();
  saving.value = true;
  try {
    await userStore.updateProfile(form);
    ElMessage.success("个人资料已更新");
  } finally {
    saving.value = false;
  }
}
</script>
<style scoped>
.profile {
  display: grid;
  place-items: center;
  min-height: calc(100vh - 92px);
}

.profile-card {
  width: min(1050px, 100%);
  padding: 54px 70px;
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 70px;
}

.avatar-side {
  display: grid;
  justify-items: center;
  align-content: center;
  text-align: center;
  color: var(--warm-muted);
  font-size: 22px;
}

.avatar-side :deep(.el-upload-list__item),.avatar-side :deep(.el-upload--picture-card) {
  width: 200px;
  height: 200px;
  border-radius: 50%;
}

.profile-form {
  align-self: center;
}

.profile-form :deep(.el-form-item) {
  margin-bottom: 28px;
}

.profile-form :deep(.el-input__wrapper) {
  height: 54px;
  font-size: 18px;
}

@media (max-width: 780px) {
  .profile-card {
    grid-template-columns: 1fr;
    padding: 34px;
  }
}
</style>
