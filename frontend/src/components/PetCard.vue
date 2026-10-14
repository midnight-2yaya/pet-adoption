<template>
  <article class="pet-card" @click="$router.push(`/pets/${pet.id}`)">
    <div class="thumb">
      <img :src="imageUrl(pet.mainImage)" :alt="pet.name" />
      <span>{{pet.type}}</span>
    </div>
    <div class="body">
      <h3>{{ pet.name }}</h3>
      <p>{{ pet.brief }}</p>
      <div class="tags">
        <em>{{ genderLabel(pet.gender) }}</em>
        <em>{{ pet.bodySize || "未知体型" }}</em>
        <em>{{ pet.sterilized ? "已绝育" : "未绝育" }}</em>
      </div>
      <footer>
        <span>浏览 {{ pet.viewCount || 0 }}</span>
        <span>申请 {{ pet.applicationCount || 0 }}</span>
        <b>查看详情 →</b>
      </footer>
    </div>
  </article>
</template>

<script setup>
import { imageUrl, genderLabel } from "../utils/format";

defineProps({ pet: { type: Object, required: true } });
</script>

<style scoped>
.pet-card {
  background: #fff;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: var(--warm-shadow);
  cursor: pointer;
  transition: 0.25s;
}

.pet-card:hover {
  transform: translateY(-4px);
}

.thumb {
  height: 230px;
  background: #ffe1cf;
  position: relative;
  overflow: hidden;
}

.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumb span {
  position: absolute;
  right: 18px;
  top: 18px;
  background: rgba(47, 42, 37, 0.65);
  color: #fff;
  border-radius: 999px;
  padding: 7px 13px;
  font-weight: 800;
}

.body {
  padding: 22px 26px 24px;
}

.body h3 {
  font-size: 30px;
  margin: 0 0 8px;
}

.body p {
  color: var(--warm-muted);
  min-height: 54px;
}

.tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin: 18px 0;
}

.tags em {
  font-style: normal;
  background: #fff1e8;
  border-radius: 999px;
  padding: 8px 14px;
  color: #7d665a;
}

footer {
  display: flex;
  gap: 18px;
  border-top: 1px solid #f1dfd2;
  padding-top: 18px;
  color: #9a8a80;
}

footer b {
  margin-left: auto;
  color: var(--warm-primary);
}
</style>
