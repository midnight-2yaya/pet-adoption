<template>
  <section class="home">
    <div class="hero page">
      <div class="hero-text">
        <div class="hero-tag">WARM PET ADOPTION</div>
        <h1>认真匹配，<br />让陪伴更长久。</h1>
        <p class="hero-subtitle">在这里，每一次领养都不是冲动，而是深思熟虑后的双向奔赴。</p>
        <div class="actions">
          <RouterLink class="warm-btn" to="/pets">去看宠物</RouterLink>
          <RouterLink class="ghost" to="/statistics">查看领养数据</RouterLink>
        </div>
      </div>
      <div class="hero-carousel">
        <el-carousel height="560px" :autoplay="true" :interval="3000" arrow="always">
            <el-carousel-item v-for="pet in carouselPets" :key="pet.id || pet.name">
              <RouterLink v-if="pet.id" class="slide" :to="`/pets/${pet.id}`">
                  <img :src="imageUrl(pet.mainImage)" :alt="pet.name" />
              <div class="shade"></div>
              <div class="slide-info">
                <span>{{ pet.type }} · 待领养</span>
                <h3>{{ pet.name }}</h3>
                <p>{{ pet.brief }}</p>
              </div>
              </RouterLink>
            <div v-else class="slide">
              <img src="/placeholder-pet.svg" />
              <div class="shade"></div>
              <div class="slide-info">
                <span>今日小明星</span>
                <h3>正在等你</h3>
                <p>慢慢看，不着急。</p>
              </div>
            </div>
            </el-carousel-item>
        </el-carousel>
      </div>
    </div>
    <section class="numbers page">
      <div class="numbers-title">
        <h2>真实的数字，温暖的选择</h2>
      </div>
      <div class="number-grid">
        <div class="num-card">
          <b>{{ summary.total || 0 }}</b>
          <span>累计领养申请</span>
        </div>
        <div class="num-card">
          <b>{{ summary.approved || 0 }}</b>
          <span>已通过申请</span>
        </div>
        <div class="num-card">
          <b>{{ summary.pending || 0 }}</b>
          <span>等待审核</span>
        </div>
        <div class="num-card">
          <b>{{ summary.approvalRate || 0 }}%</b>
          <span>申请通过率</span>
        </div>
      </div>
    </section>
    <section class="popular page">
      <div class="section-head">
        <span>POPULAR PETS</span>
        <h2>最近关注较多的宠物</h2>
        <RouterLink to="/pets">查看全部 →</RouterLink>
      </div>
      <div class="pet-row">
        <PetCard v-for="pet in hotPets" :key="pet.id" :pet="pet" />
      </div>
    </section>
    <section class="process page">
      <span>EASY STEPS</span>
      <h2>三步完成线上领养流程</h2>
      <div class="process-grid">
        <div>
          <b>01</b>
          <h3>筛选与了解</h3>
          <p>按类型、性别、体型等条件筛选，在详情页查看健康情况和领养要求。</p>
        </div>
        <div>
          <b>02</b>
          <h3>提交申请</h3>
          <p>登录后填写联系方式、住房情况、养宠经验和申请理由。</p>
        </div>
        <div>
          <b>03</b>
          <h3>审核与跟踪</h3>
          <p>在领养记录中查看进度。审核通过后，同一宠物其他申请自动拒绝。</p>
        </div>
      </div>
    </section>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import PetCard from "../components/PetCard.vue";
import { petApi } from "../api/pet";
import { adoptionApi } from "../api/adoption";
import { imageUrl } from "../utils/format";

const hotPets = ref([])
const summary = ref({})

const carouselPets = computed(() =>
  hotPets.value.length ? hotPets.value.slice(0, 4) : [{ name: "暖宠" }],
)

onMounted(async () => {
  const [p, s] = await Promise.allSettled([
    petApi.hot(4),
    adoptionApi.summary(),
  ]);

  if (p.status === "fulfilled") hotPets.value = p.value || [];
  if (s.status === "fulfilled") summary.value = s.value || {};
});
</script>

<style scoped>
.home {
  background: linear-gradient(180deg, #fff8f3 0, #fffdfb 55%, #fff8f3 100%);
}

.hero {
  display: grid;
  grid-template-columns: 0.9fr 1.05fr;
  gap: 70px;
  align-items: center;
  padding-top: 86px;
}

.hero-text span,.popular span,.process > span {
  color: #cc5e39;
  font-weight: 900;
  letter-spacing: 7px;
}

.hero-text h1 {
  font-size: 72px; 
  line-height: 1.15;
  margin: 0 0 22px 0;
  font-weight: 900;
  letter-spacing: -2px;
  color: #2d1e18; 
  max-width: 580px; 
}

.hero-text p {
  font-size: 22px;
  color: #766963;
  max-width: 640px;
}

.hero-tag {
  color: #cc5e39;
  font-weight: 700;
  letter-spacing: 4px;
  font-size: 15px;
  margin-bottom: 18px; 
  display: inline-block;
}

.hero-subtitle {
  font-size: 20px;
  color: #7b6a63;
  max-width: 420px;
  margin: 0 0 40px 0; 
  line-height: 1.6;
}

.actions {
  display: flex;
  gap: 16px;
}

.ghost {
  padding: 14px 26px;
  border-radius: 14px;
  background: #fff1e8;
  color: var(--warm-primary);
  font-weight: 800;
}

.hero-carousel {
  min-width: 0;
}

.hero-carousel :deep(.el-carousel) {
  border-radius: 24px;
  overflow: hidden;
  box-shadow: var(--warm-shadow);
}

.slide {
  height: 560px;
  display: block;
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  background: #fde0cf;
}

.slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.shade {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    180deg,
    rgba(0, 0, 0, 0) 40%,
    rgba(0, 0, 0, 0.66) 100%
  );
}

.slide-info {
  position: absolute;
  left: 72px;
  bottom: 58px;
  color: #fff;
  background: rgba(0, 0, 0, 0.3); 
  backdrop-filter: blur(6px); 
  padding: 16px 28px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.slide-info span {
  font-size: 20px;
}

.slide-info h3 {
  font-size: 54px;
  margin: 18px 0 4px;
}

.slide-info p {
  font-size: 22px;
  margin: 0;
}

.numbers {
  padding-top: 80px;
}

.numbers-title {
  display: grid;
  grid-template-columns: 1.3fr 0.8fr;
  gap: 60px;
  align-items: end;
}

.numbers-title h2 {
  font-size: 58px;
  line-height: 1.15;
  margin: 0;
}

.numbers-title p {
  font-size: 20px;
  color: #766963;
}

.number-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 26px;
  margin-top: 34px;
}

.num-card {
  background: #fff;
  border-radius: 18px;
  padding: 36px;
  box-shadow: var(--warm-shadow);
}

.num-card:hover {
  transform: translateY(-6px); 
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.06);
}

.num-card b {
  display: block;
  font-size: 54px;
  color: #cd6742;
}

.num-card span {
  font-size: 20px;
  color: #665a54;
}

.section-head {
  display: flex;
  align-items: end;
  gap: 24px;
}

.section-head h2 {
  font-size: 54px;
  margin: 12px 0 0;
}

.section-head a {
  margin-left: auto;
  color: var(--warm-primary);
  font-size: 20px;
  font-weight: 800;
}

.pet-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 26px;
  margin-top: 34px;
  align-items: stretch;
}

.process {
  padding-top: 100px;
}

.process h2 {
  font-size: 58px;
  margin: 18px 0 40px;
}

.process-grid {
  display: flex;
  justify-content: space-between;
  gap: 0;
  position: relative;
}

.process-grid div {
  background: transparent; 
  box-shadow: none;
  border-top: none;
  padding: 0 40px;
  flex: 1;
  position: relative;
}


.process-grid b {
  color: var(--warm-primary);
  font-size: 24px;
}

.process-grid h3 {
  font-size: 32px;
}

.process-grid p {
  font-size: 20px;
  color: #665a54;
}

@media (max-width: 1100px) {
  .hero,.numbers-title {
    grid-template-columns: 1fr;
  }
  .pet-row,.number-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .hero-text h1 {
    font-size: 58px;
  }
  .process-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 620px) {
  .pet-row,.number-grid {
    grid-template-columns: 1fr;
  }
}
</style>
