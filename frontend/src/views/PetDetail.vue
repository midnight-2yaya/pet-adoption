<template>
  <section class="page">
    <el-skeleton v-if="loading" :rows="8" animated />
    <template v-else-if="pet">
      <div class="detail soft-card">
        <div class="media">
          <el-carousel height="520px" :autoplay="true" :interval="3000" arrow="hover" indicator-position="outside">
            <el-carousel-item v-for="(img, i) in images" :key="i">
            <img :src="imageUrl(img)" :alt="pet.name" />
            </el-carousel-item>
         </el-carousel>
        </div>
        <div class="info">
          <div>
            <el-tag>{{ pet.type }}</el-tag>
            <el-tag style="margin-left: 8px">{{genderLabel(pet.gender)}}</el-tag>
          </div>
          <h1>{{ pet.name }}</h1>
          <p>{{ pet.brief }}</p>
          <div class="attrs">
            <div>
              <span>年龄</span>
              <b>{{ pet.age || "-" }}{{ pet.ageUnit || "岁" }}</b>
            </div>
            <div>
              <span>体型</span>
              <b>{{ pet.bodySize || "-" }}</b>
            </div>
            <div>
              <span>身高</span>
              <b>{{ pet.height || "-" }} cm</b>
            </div>
            <div>
              <span>体重</span>
              <b>{{ pet.weight || "-" }} kg</b>
            </div>
            <div>
              <span>绝育</span>
              <b>{{ pet.sterilized ? "已绝育" : "未绝育" }}</b>
            </div>
            <div>
              <span>浏览</span>
              <b>{{ pet.viewCount || 0 }} 次</b>
            </div>
          </div>
          <el-button type="primary" size="large" :disabled="pet.status !== 'available' || hasApplied" @click="openApply">
            {{pet.status !== "available"? "已被领养": hasApplied? "已申请": "我想领养它"}}
            </el-button>
        </div>
      </div>
      <div class="text soft-card">
        <h2>它的情况</h2>
        <p>{{ pet.description }}</p>
        <h2>生活习惯</h2>
        <p>{{ pet.habit || "暂无补充" }}</p>
        <h2>健康情况</h2>
        <p>{{ pet.health || "暂无补充" }}</p>
        <h2>领养要求</h2>
        <p>{{ pet.requirements }}</p>
      </div>
      </template>

      <el-dialog v-model="show" title="提交领养申请" width="520px">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="92px">
            <el-form-item label="申请人" prop="applicantName">
                <el-input v-model="form.applicantName" />
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
                <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item label="居住情况">
                <el-input v-model="form.housing" placeholder="如自有住房、整租、宿舍" />
            </el-form-item>
            <el-form-item label="养宠经验">
                 <el-input v-model="form.experience" placeholder="简单说说是否养过宠物" />
            </el-form-item>
            <el-form-item label="申请理由" prop="reason">
                <el-input v-model="form.reason" type="textarea" :rows="4" />
            </el-form-item>
            <el-form-item prop="agreement">
                <el-checkbox v-model="form.agreement">我承诺不随意弃养，并愿意接受必要回访</el-checkbox>
            </el-form-item>
        </el-form>
                            
        <template #footer>
            <el-button @click="show = false">取消</el-button>
            <el-button type="primary" :loading="submitting" @click="submit">提交申请</el-button>
        </template>
      </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { petApi } from "../api/pet";
import { adoptionApi } from "../api/adoption";
import { useUserStore } from "../stores/userStore";
import { genderLabel, imageUrl } from "../utils/format";

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const pet = ref(null)
const hasApplied = ref(false)
const show = ref(false)
const formRef = ref(null)
const form = reactive({
  petId: null,
  applicantName: "",
  phone: "",
  housing: "",
  experience: "",
  reason: "",
  agreement: false,
});
const rules = {
  applicantName: [{ required: true, message: "请输入申请人", trigger: "blur" }],
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    { pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确", trigger: "blur" },
  ],
  reason: [{ required: true, message: "请填写申请理由", trigger: "blur" }],
  agreement: [
    {
      validator: (_, v, cb) => (v ? cb() : cb(new Error("请先勾选领养承诺"))),
      trigger: "change",
    },
  ],
};

const images = computed(() => {
  const arr = Array.isArray(pet.value?.detailImages)
    ? pet.value.detailImages.filter(Boolean)
    : [];
  return arr.length ? arr : [pet.value?.mainImage || "/placeholder-pet.svg"];
});

async function load() {
  loading.value = true;
  try {
    pet.value = await petApi.detail(route.params.id);
    form.petId = pet.value.id;
    if (userStore.isLogin) {
      const page = await adoptionApi
        .my({ page: 1, pageSize: 100 })
        .catch(() => ({ content: [] }));
      hasApplied.value = (page.content || []).some(
        (a) =>
          a.petId === pet.value.id &&
          ["pending", "approved"].includes(a.status),
      );
    }
  } finally {
    loading.value = false;
  }
}

function openApply() {
  if (!userStore.isLogin) {
    ElMessage.warning("请先登录");
    router.push({ path: "/login", query: { redirect: route.fullPath } });
    return;
  }
  form.applicantName = userStore.user?.nickname || "";
  form.phone = userStore.user?.phone || "";
  show.value = true;
}

async function submit() {
  await formRef.value.validate();
  submitting.value = true;
  try {
    await adoptionApi.apply(form);
    ElMessage.success("申请已提交");
    show.value = false;
    hasApplied.value = true;
  } finally {
    submitting.value = false;
  }
}

onMounted(load);
</script>

<style scoped>
.detail {
  display: grid;
  grid-template-columns: 1.08fr 0.92fr;
  gap: 42px;
  padding: 36px;
  overflow: hidden;
}

.media {
  min-width: 0;
}

.media :deep(.el-carousel) {
  height: 560px;
  overflow: hidden;
  border-radius: 22px;
}

.media :deep(.el-carousel__container) {
  height: 520px !important;
}

.media img {
  width: 100%;
  height: 520px;
  object-fit: cover;
  border-radius: 22px;
}

.info h1 {
  font-size: 56px;
  margin: 28px 0 12px;
}

.info p {
  font-size: 20px;
  color: var(--warm-muted);
}

.attrs {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin: 28px 0;
}

.attrs div {
  background: #fff8f3;
  border-radius: 16px;
  padding: 20px;
}

.attrs span {
  display: block;
  color: var(--warm-muted);
  font-size: 16px;
}

.attrs b {
  font-size: 24px;
}
.text {
  margin-top: 26px;
  padding: 34px;
}

.text p {
  font-size: 19px;
  color: #665a54;
}

@media (max-width: 980px) {
  .detail {
    grid-template-columns: 1fr;
  }
}
</style>
