<template>
  <div class="panel soft-card">
    <div class="bar">
      <el-input v-model="query.keyword" placeholder="搜索宠物" />
      <el-select v-model="query.status" placeholder="状态" clearable>
        <el-option label="待领养" value="available" />
        <el-option label="已领养" value="adopted" />
          <el-option label="已下架" value="offline" />
      </el-select>
        <el-button type="primary" @click="openCreate">新增宠物</el-button>
        <el-button type="warning" :disabled="!selected.length" @click="batchOffline">批量下架</el-button>
    </div>
    <el-table :data="rows"  @selection-change="selected = $event"  v-loading="loading" empty-text="🫣 暂无宠物数据，点击「新增宠物」开始记录吧！">
        <el-table-column type="selection" width="48" />
        <el-table-column label="主图" width="90">
            <template #default="{ row }">
                <el-image class="cover" :src="imageUrl(row.mainImage)" fit="cover" />
            </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="type" label="类型" />
        <el-table-column label="性别">
            <template #default="{ row }">{{genderLabel(row.gender)}}</template>
        </el-table-column>
      <el-table-column label="状态">
        <template #default="{ row }">
            <StatusBadge :status="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="show" :title="form.id ? '编辑宠物' : '新增宠物'" width="860px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="grid">
          <el-form-item label="名称" prop="name">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="类型" prop="type">
            <el-select v-model="form.type" filterable allow-create>
                <el-option v-for="t in types" :key="t" :label="t" :value="t" />
            </el-select>
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="form.gender">
                <el-option label="公" value="公" />
                <el-option label="母" value="母" />
            </el-select>
          </el-form-item>
          <el-form-item label="年龄">
            <div style="display: flex; gap: 8px;">
              <el-input-number v-model="form.age" :min="0" style="width: 100px;" />
              <el-select v-model="form.ageUnit" style="width: 80px;">
                <el-option label="岁" value="岁" />
                <el-option label="月" value="月" />
              </el-select>
            </div>
          </el-form-item>
          <el-form-item label="身高(cm)">
            <el-input-number v-model="form.height" :min="0" :step="0.5" :precision="1" />
          </el-form-item>
          <el-form-item label="体重(kg)">
            <el-input-number v-model="form.weight" :min="0" :step="0.5" :precision="1" />
          </el-form-item>
          <el-form-item label="体型">
            <el-select v-model="form.bodySize">
                <el-option label="小型" value="小型" />
                <el-option label="中型" value="中型" />
                <el-option label="大型" value="大型" />
            </el-select>
          </el-form-item>
          <el-form-item label="绝育">
            <el-switch v-model="form.sterilized" />
          </el-form-item>
        </div>
        <el-form-item label="简介">
          <el-input v-model="form.brief" placeholder="一句话介绍这只小可爱，例如：性格温顺亲人..." />
        </el-form-item>
        <el-form-item label="详细介绍">
          <el-input v-model="form.description" type="textarea" :rows="2" resize="none" placeholder="详细描述宠物的性格、喜好..." />
        </el-form-item>
        <el-form-item label="生活习惯">
            <el-input v-model="form.habit" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="健康情况">
          <el-input v-model="form.health" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="领养要求">
            <el-input v-model="form.requirements" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="主图">
          <ImageUploadSingle v-model="form.mainImage" />
        </el-form-item>
        <el-form-item label="轮播图">
          <ImageUploadMulti v-model="form.detailImages" />
        </el-form-item>
        <el-form-item label="状态">
            <el-select v-model="form.status">
                <el-option label="待领养" value="available" />
                <el-option label="已领养" value="adopted" />
                <el-option label="已下架" value="offline" />
            </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div style="display: flex; justify-content: flex-end; gap: 12px;">
          <el-button @click="show = false">取消</el-button>
          <el-button type="primary" color="#e07a5f" :loading="saving" @click="save" style="padding: 0 30px; box-shadow: 0 4px 10px rgba(224, 122, 95, 0.3);">
            保存提交
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import StatusBadge from "../StatusBadge.vue";
import ImageUploadSingle from "../base/ImageUploadSingle.vue";
import ImageUploadMulti from "../base/ImageUploadMulti.vue";
import { adminApi } from "../../api/admin";
import { petApi } from "../../api/pet";
import { debounce, genderLabel, imageUrl } from "../../utils/format";

const rows = ref([])
const loading = ref(false)
const saving = ref(false)
const selected = ref([])
const types = ref([])
const show = ref(false)
const formRef = ref(null)

const query = reactive({ page: 1, pageSize: 50, keyword: "", status: "" });

function def() {
  return {
    id: null,
    name: "",
    type: "",
    gender: "公",
    age: 1,
    ageUnit: "岁",
    height: null,
    weight: null,
    bodySize: "小型",
    sterilized: false,
    brief: "",
    description: "",
    habit: "",
    health: "",
    requirements: "",
    mainImage: "",
    detailImages: [],
    status: "available",
  };
}

const form = reactive(def());

const rules = {
  name: [{ required: true, message: "请输入名称", trigger: "blur" }],
  type: [{ required: true, message: "请输入类型", trigger: "change" }],
};

async function fetchRows() {
  loading.value = true;
  try {
    const p = await adminApi.pets(query);
    rows.value = p.content || [];
  } finally {
    loading.value = false;
  }
}

function openCreate() {
  Object.assign(form, def());
  show.value = true;
}

function openEdit(row) {
  Object.assign(form, def(), JSON.parse(JSON.stringify(row)));
  if (!Array.isArray(form.detailImages)) form.detailImages = [];
  show.value = true;
}

async function save() {
  await formRef.value.validate();
  saving.value = true;
  try {
    form.id
      ? await adminApi.updatePet(form.id, form)
      : await adminApi.createPet(form);
    ElMessage.success("已保存");
    show.value = false;
    types.value = await petApi.types().catch(() => types.value);
    fetchRows();
  } finally {
    saving.value = false;
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除${row.name}吗？`, "提示");
  await adminApi.deletePet(row.id);
  ElMessage.success("已删除");
  fetchRows();
}

async function batchOffline() {
  await adminApi.batchOffline(selected.value.map((i) => i.id));
  ElMessage.success("已下架");
  fetchRows();
}

watch(() => [query.keyword, query.status], debounce(fetchRows, 300));
onMounted(async () => {
  types.value = await petApi.types().catch(() => []);
  fetchRows();
});

</script>

<style scoped>
.panel {
  padding: 20px;
}

.bar {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.bar .el-input {
  width: 240px;
}

.bar .el-select {
  width: 150px;
}

.cover {
  width: 56px;
  height: 56px;
  border-radius: 12px;
}

.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 18px;
}

:deep(.el-table__inner-wrapper::before) {
  height: 0; 
}

:deep(.el-table tr) {
  height: 64px; 
}

@media (max-width: 700px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
