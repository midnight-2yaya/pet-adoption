<template>
  <div class="panel soft-card">
    <div class="bar">
      <el-input v-model="query.keyword" placeholder="搜索申请人或宠物"/>
      <el-select v-model="query.status" placeholder="状态" clearable>
        <el-option label="待审核" value="pending" />
        <el-option label="已通过" value="approved" />
        <el-option label="已拒绝" value="rejected" />
      </el-select>
      <el-button type="success" :disabled="!selected.length" @click="batch('approved')">批量通过</el-button>
      <el-button type="danger" :disabled="!selected.length" @click="batch('rejected')">批量拒绝</el-button>
    </div>
    <el-table :data="rows" @selection-change="selected = $event" v-loading="loading">
        <el-table-column type="selection" width="48" />
        <el-table-column prop="applicantName" label="申请人"/>
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="petName" label="宠物"/>
        <el-table-column label="状态">
            <template #default="{ row }">
                <StatusBadge :status="row.status" />
            </template>
        </el-table-column>
        <el-table-column prop="reason" label="理由" show-overflow-tooltip/>
        <el-table-column label="操作" >
            <template #default="{ row }">
                <el-button v-if="row.status === 'pending'" link type="success" @click="review(row, 'approved')">通过</el-button>
                <el-button v-if="row.status === 'pending'" link type="danger" @click="review(row, 'rejected')">拒绝</el-button>
            </template>
        </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import StatusBadge from "../StatusBadge.vue";
import { adminApi } from "../../api/admin";
import { debounce } from "../../utils/format";

const rows = ref([])
const selected = ref([])
const loading = ref(false)
const query = reactive({ page: 1, pageSize: 50, keyword: "", status: "" });

async function fetchRows() {
  loading.value = true;
  try {
    const p = await adminApi.adoptions(query);
    rows.value = p.content || [];
  } finally {
    loading.value = false;
  }
}

async function review(row, status) {
  await ElMessageBox.confirm("确认审核此申请？", "提示");
  await adminApi.review(row.id, {
    status,
    reviewComment: status === "approved" ? "审核通过" : "暂不符合领养条件",
  });
  ElMessage.success("已处理");
  fetchRows();
}

async function batch(status) {
  await adminApi.batchReview({
    ids: selected.value.map((i) => i.id),
    status,
    reviewComment: status === "approved" ? "审核通过" : "暂不符合领养条件",
  });
  ElMessage.success("已批量处理");
  fetchRows();
}

watch(() => [query.keyword, query.status], debounce(fetchRows, 300));

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
