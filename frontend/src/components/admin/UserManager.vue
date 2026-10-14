<template>
  <div class="panel soft-card">
    <div class="bar">
      <el-input v-model="query.keyword" placeholder="搜索用户" />
      <el-select v-model="query.role" placeholder="角色" clearable>
        <el-option label="普通用户" value="USER" />
        <el-option label="管理员" value="ADMIN" />
        </el-select>
        <el-button type="warning" :disabled="!selected.length" @click="batchDisable">批量禁用</el-button>
    </div>
    <el-table :data="rows" @selection-change="selected = $event" v-loading="loading">
        <el-table-column type="selection" width="48" />
        <el-table-column label="头像" width="80">
        <template #default="{ row }">
          <el-avatar :src="imageUrl(row.avatar)" />
        </template>
        </el-table-column>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称"/>
      <el-table-column prop="phone" label="手机号" />
      <el-table-column label="角色">
        <template #default="{ row }">{{ row.role === "ADMIN" ? "管理员" : "普通用户" }}</template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
            <el-button link type="primary" @click="open(row)">编辑</el-button>
          <el-button link type="danger" @click="remove(row)">删除</el-button>
        </template>
        </el-table-column>
    </el-table>
    <el-dialog v-model="show" title="编辑用户" width="560px">
        <el-form :model="form" label-width="90px">
            <el-form-item label="头像">
                <ImageUploadSingle v-model="form.avatar" />
            </el-form-item>
        <el-form-item label="用户名">
            <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
            <el-input v-model="form.nickname" />
            </el-form-item>
        <el-form-item label="手机号">
            <el-input v-model="form.phone" />
            </el-form-item>
        <el-form-item label="邮箱">
            <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色">
            <el-select v-model="form.role">
                <el-option label="普通用户" value="USER" />
                <el-option label="管理员" value="ADMIN" />
            </el-select>
        </el-form-item>
        <el-form-item label="禁用">
            <el-switch v-model="form.disabled" />
        </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="show = false">取消</el-button>
            <el-button type="primary" @click="save">保存</el-button>
            </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import ImageUploadSingle from "../base/ImageUploadSingle.vue";
import { adminApi } from "../../api/admin";
import { debounce, imageUrl } from "../../utils/format";

const rows = ref([])
const selected = ref([])
const loading = ref(false)
const show = ref(false)
const query = reactive({ page: 1, pageSize: 50, keyword: "", role: "" })
const form = reactive({})

async function fetchRows() {
  loading.value = true;
  try {
    const p = await adminApi.users(query);
    rows.value = p.content || [];
  } finally {
    loading.value = false;
  }
}

function open(row) {
  Object.assign(form, JSON.parse(JSON.stringify(row)));
  show.value = true;
}

async function save() {
  await adminApi.updateUser(form.id, form);
  ElMessage.success("已保存");
  show.value = false;
  fetchRows();
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除${row.username}吗？`, "提示");
  await adminApi.deleteUser(row.id);
  ElMessage.success("已删除");
  fetchRows();
}

async function batchDisable() {
  await adminApi.batchDisable(selected.value.map((i) => i.id));
  ElMessage.success("已禁用");
  fetchRows();
}

watch(() => [query.keyword, query.role], debounce(fetchRows, 300));

onMounted(fetchRows);
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
  width: 260px;
}

.bar .el-select {
  width: 150px;
}
</style>
