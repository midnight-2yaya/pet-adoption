<template>
  <section class="stats page">
    <div class="top">
      <div>
        <span>ADOPTION INSIGHTS</span>
        <h1>统计分析</h1>
      </div>
      <el-radio-group v-model="scope" @change="fetchStats">
        <el-radio-button label="all">全部申请</el-radio-button>
        <el-radio-button label="approved">仅已通过</el-radio-button>
      </el-radio-group>
    </div>
    <div class="cards">
      <div>
        <b>{{ data.total || 0 }}</b>
        <span>累计申请</span>
      </div>
      <div>
        <b>{{ data.pending || 0 }}</b>
        <span>待审核</span>
      </div>
      <div>
        <b>{{ data.approved || 0 }}</b>
        <span>已通过</span>
      </div>
      <div>
        <b>{{ data.rejected || 0 }}</b>
        <span>未通过</span>
      </div>
      <div>
        <b>{{ data.approvalRate || 0 }}%</b>
        <span>通过率</span>
      </div>
    </div>
    <div class="chart-grid">
      <section class="chart-card">
        <div class="chart-title">
          <span>TYPE SHARE</span>
          <h2>各类型申请占比</h2>
        </div>
        <StatChart :data="data.typeStats || []" mode="pie" />
      </section>
      <section class="chart-card">
        <div class="chart-title">
          <span>TYPE COUNT</span>
          <h2>各类型申请数量</h2>
        </div>
        <StatChart :data="data.typeStats || []" mode="bar" />
      </section>
      <section class="chart-card full">
        <div class="chart-title">
          <span>TIMELINE</span>
          <h2>近 14 天申请趋势</h2>
        </div>
        <div ref="timelineRef" class="chart-box"></div>
      </section>
    </div>
  </section>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from "vue";
import * as echarts from "echarts";
import StatChart from "../components/StatChart.vue";
import { adoptionApi } from "../api/adoption";

const scope = ref("all");
const data = ref({ typeStats: [] });

const timelineRef = ref(null);
let timelineChart = null;

function drawTimeline() {
  if (!timelineRef.value) return;
  if (!timelineChart) timelineChart = echarts.init(timelineRef.value);
  const list = data.value.timeline || [];
  timelineChart.setOption(
      {
        tooltip: { trigger: "axis" },
        legend: { data: ["新增申请", "通过", "拒绝"], top: 0 },
        grid: { left: 48, right: 24, top: 40, bottom: 40 },
        xAxis: {
          type: "category",
          data: list.map((i) => i.date),
          axisLabel: { rotate: 30 },
        },
        yAxis: { type: "value", minInterval: 1 },
        series: [
          {
            name: "新增申请",
            type: "line",
            smooth: true,
            data: list.map((i) => i.applied),
            itemStyle: { color: "#E8835A" },
            areaStyle: { opacity: 0.1 },
          },
          {
            name: "通过",
            type: "line",
            smooth: true,
            data: list.map((i) => i.approved),
            itemStyle: { color: "#80B47F" },
          },
          {
            name: "拒绝",
            type: "line",
            smooth: true,
            data: list.map((i) => i.rejected),
            itemStyle: { color: "#D96A78" },
          },
        ],
      },
      true
  );
  timelineChart.resize();
}

function resize() {
  timelineChart?.resize();
}

async function fetchStats() {
  data.value = await adoptionApi.statistics(scope.value);
}

watch(data, () => nextTick(drawTimeline), { deep: true });

onMounted(async () => {
  await fetchStats();
  window.addEventListener("resize", resize);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", resize);
  timelineChart?.dispose();
  timelineChart = null;
});
</script>

<style scoped>
.top {
  display: flex;
  justify-content: space-between;
  align-items: end;
  margin-bottom: 34px;
}

.top span,
.chart-title span {
  color: #cc5e39;
  font-weight: 900;
  letter-spacing: 6px;
}

.top h1 {
  font-size: 62px;
  margin: 12px 0 8px;
}

.top p {
  font-size: 20px;
  color: #665a54;
}

.cards {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
  margin-bottom: 34px;
}

.cards div {
  background: #fff;
  border-radius: 18px;
  padding: 28px;
  box-shadow: var(--warm-shadow);
}

.cards b {
  display: block;
  font-size: 46px;
  color: #cd6742;
}

.cards span {
  font-size: 18px;
  color: #665a54;
}

.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 28px;
}

.chart-card {
  background: #fff;
  border-radius: 20px;
  padding: 30px;
  box-shadow: var(--warm-shadow);
  min-height: 650px;
}

.chart-card.full {
  grid-column: 1 / -1;
  min-height: auto;
}

.chart-box {
  width: 100%;
  height: 420px;
}

.chart-title {
  display: flex;
  align-items: start;
  justify-content: space-between;
  gap: 20px;
}

.chart-title h2 {
  font-size: 32px;
  margin: 12px 0;
}

.chart-title p {
  color: #9a8a80;
  text-align: right;
}

@media (max-width: 1100px) {
  .cards {
    grid-template-columns: repeat(2, 1fr);
  }
  .chart-grid {
    grid-template-columns: 1fr;
  }
  .top {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>