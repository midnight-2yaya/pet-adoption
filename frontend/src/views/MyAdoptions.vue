<template>
  <section class="page">
    <div class="list-head soft-card">
      <h1>我的领养</h1>
      <el-select v-model="query.status" placeholder="筛选状态" clearable>
        <el-option label="待审核" value="pending" />
        <el-option label="已通过" value="approved" />
        <el-option label="已拒绝" value="rejected"/>
      </el-select>
    </div>
    <div v-if="!loading && rows.length === 0" style="text-align: center; margin-bottom: 16px; color: #888; font-size: 14px;">
      还没有领养记录，去 <router-link to="/pets" style="color: #e07a5f; font-weight: bold; text-decoration: none;">宠物列表</router-link> 看看有缘分的小宠物吧！
    </div>
    <el-table 
      class="soft-card" 
      :data="rows" 
      v-loading="loading"
      :header-cell-style="{ background: 'transparent', borderBottom: 'none' }"
      style="--el-table-border-color: transparent; --el-table-row-hover-background: #fcfaf9;"
      empty-text="🐾 暂时没有领养记录，快去宠物列表看看吧"
    >
        <el-table-column label="宠物" width="200">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; gap: 12px;">
              <!-- 这里会用后端存进去的图片，如果没有图片，会自动展示在 utils 里写的 placeholder-pet.svg -->
              <el-image 
                style="width: 40px; height: 40px; border-radius: 8px; flex-shrink: 0; background-color: #f5f5f5;"
                :src="imageUrl(row.petImage)" 
                fit="cover"
              />
              <span style="font-weight: 500;">{{ row.petName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="petType" label="类型" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag  :type="row.status === 'approved' ? 'success' : row.status === 'pending' ? 'warning' : 'danger'" effect="light" round>
              <span style="display: inline-block; width: 6px; height: 6px; border-radius: 50%; margin-right: 6px; background-color: currentColor;"></span>
              {{ 
                row.status === 'approved' ? '已通过' : 
                row.status === 'pending' ? '待审核' : 
                row.status === 'rejected' ? '已拒绝' : row.status 
              }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间">
            <template #default="{ row }">
                {{formatDate(row.applyTime)}}
            </template>
        </el-table-column>
        <el-table-column prop="reviewComment" label="审核意见" show-overflow-tooltip/>
        <el-table-column label="操作">
            <template #default="{ row }">
                <el-button v-if="row.status === 'pending'" link type="danger" @click="cancel(row)">取消申请</el-button>
                <RouterLink v-else :to="`/pets/${row.petId}`">查看宠物</RouterLink>
            </template>
        </el-table-column>
    </el-table>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { adoptionApi } from "../api/adoption";
import { formatDate, imageUrl } from "../utils/format";
const rows = ref([])
const loading = ref(false)
const query = reactive({ page: 1, pageSize: 50, status: "" })

async function fetchRows() {
  loading.value = true;
  try {
    const p = await adoptionApi.my(query);
    rows.value = p.content || [];
  } finally {
    loading.value = false;
  }
}

async function cancel(row) {
  await ElMessageBox.confirm("确定取消这条申请吗？", "提示");
  await adoptionApi.cancel(row.id);
  ElMessage.success("已取消");
  fetchRows();
}

watch(() => query.status, fetchRows)

onMounted(fetchRows);
</script>

<style scoped>
.list-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  margin-bottom: 20px;
  border-bottom: 1px solid #f0f0f0; 
}

.list-head h1 {
  margin: 0;
  font-size: 32px;
}

.list-head .el-select {
  width: 150px; 
}
</style>
