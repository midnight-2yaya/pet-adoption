<template>
  <section class="page">
    <h1 class="title">后台管理</h1>
    <el-tabs v-model="active">
        <el-tab-pane label="宠物管理" name="pets">
            <PetManager />
        </el-tab-pane>
        <el-tab-pane label="领养审核" name="adoptions">
            <AdoptionManager />
        </el-tab-pane>
        <el-tab-pane label="用户账号" name="users">
            <UserManager />
        </el-tab-pane>
    </el-tabs>
  </section>
</template>

<script setup>
import { ref } from "vue";
import PetManager from "../components/admin/PetManager.vue";
import AdoptionManager from "../components/admin/AdoptionManager.vue";
import UserManager from "../components/admin/UserManager.vue";

const active = ref("pets");
</script>

<style scoped>
.title {
  font-size: 52px;
  margin: 0 0 24px;
}
</style>
