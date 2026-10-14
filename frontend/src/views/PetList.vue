<template>
  <section class="page">
    <div class="list-head soft-card">
      <div>
        <h1>宠物列表</h1>
      </div>
      <el-input v-model="query.keyword" placeholder="搜索名字或简介" clearable/>
    </div>
    <div class="filters soft-card">
    <el-select v-model="query.type" placeholder="类型" clearable>
        <el-option v-for="t in types" :key="t" :label="t" :value="t" />
    </el-select>
    <el-select v-model="query.gender" placeholder="性别" clearable>
        <el-option label="公" value="公" />
        <el-option label="母" value="母" />
    </el-select>
    <el-select v-model="query.bodySize" placeholder="体型" clearable>
        <el-option label="小型" value="小型" />
        <el-option label="中型" value="中型" />
        <el-option label="大型" value="大型" />
    </el-select>
    <el-select v-model="query.sort">
        <el-option label="最新发布" value="latest" />
        <el-option label="申请热度" value="hot"/>
    </el-select>
    </div>
    <el-skeleton v-if="loading" :rows="6" animated />
    <div v-else class="grid">
      <PetCard v-for="p in pets" :key="p.id" :pet="p" />
    </div>
    <el-empty v-if="!loading && !pets.length" description="未找到相关宠物" />
    <div class="pager">
      <el-pagination background layout="prev, pager, next" :page-size="query.pageSize" :current-page="query.page" :total="total" @current-change=" (p) => { query.page = p; fetchPets(); } "/>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from "vue";
import PetCard from "../components/PetCard.vue";
import { petApi } from "../api/pet";
import { debounce } from "../utils/format";

const loading = ref(false)
const pets = ref([])
const total = ref(0)
const types = ref([])
const query = reactive({
  page: 1,
  pageSize: 8,
  keyword: "",
  type: "",
  gender: "",
  bodySize: "",
  sort: "latest",
  status: "available",
});

async function fetchPets() {
  loading.value = true;
  try {
    const page = await petApi.list(query);
    pets.value = page.content || [];
    total.value = page.totalElements || 0;
  } finally {
    loading.value = false;
  }
}
watch(
  () => [query.keyword, query.type, query.gender, query.bodySize, query.sort],
  debounce(() => {
    query.page = 1;
    fetchPets();
  }, 300),
);

onMounted(async () => {
  types.value = await petApi.types().catch(() => []);
  fetchPets();
});
</script>

<style scoped>
.list-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  margin-bottom: 18px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.04);
}

.list-head h1 {
  font-size: 40px;
  margin: 0;
  color: #3a2a22;
}

.list-head p {
  color: var(--warm-muted);
  margin-top: 6px;
}

.list-head .el-input {
  max-width: 360px;
}

.filters {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  padding: 16px 20px;
  margin-bottom: 26px;
  background: #faf7f5; 
  border-radius: 16px;
  border: 1px solid #f0e9e5;
}

.filters .el-select {
  width: 180px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 26px;
  grid-auto-flow: dense; 
}

.pager {
  display: flex;
  justify-content: center;
  padding: 28px 0 10px;
}

@media (max-width: 1200px) {
  .grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 650px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .list-head {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
