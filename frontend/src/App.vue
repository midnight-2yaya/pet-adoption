<template>
  <NavBar />
  <RouterView v-slot="{ Component, route }">
    <Transition name="page-slide" mode="out-in">
      <main :key="route.fullPath" class="route-main">
        <component :is="Component" />
      </main>
    </Transition>
  </RouterView>
</template>

<script setup>
import NavBar from "./layouts/NavBar.vue";
</script>

<style scoped>
.route-main {
  min-height: calc(100vh - 92px);
}
</style>
