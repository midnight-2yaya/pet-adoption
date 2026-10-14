<template>
    <div ref="chartRef" class="chart-box"></div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from "vue";
import * as echarts from "echarts";

const props = defineProps({
  data: { type: Array, default: () => [] },
  mode: { type: String, default: "bar" },
});

const chartRef = ref(null);

let chart = null;

function draw() {
  if (!chartRef.value) return;
  if (!chart) chart = echarts.init(chartRef.value);
  const colors = [
    "#E8835A",
    "#F5A623",
    "#80B47F",
    "#D96A78",
    "#7BA7D7",
    "#A78BFA",
  ];
  const option =
    props.mode === "pie"
      ? {
          tooltip: {
            trigger: "item",
            formatter: "{b}<br/>数量：{c}<br/>占比：{d}%",
          },
          legend: { bottom: 8 },
          color: colors,
          series: [
            {
              type: "pie",
              radius: ["42%", "66%"],
              center: ["50%", "45%"],
              data: props.data.map((i) => ({ name: i.type, value: i.count })),
              animationDuration: 800,
            },
          ],
        }
      : {
          tooltip: { trigger: "axis" },
          grid: { left: 48, right: 24, top: 36, bottom: 44 },
          xAxis: { type: "category", data: props.data.map((i) => i.type) },
          yAxis: { type: "value", minInterval: 1 },
          color: ["#E8835A"],
          series: [
            {
              type: "bar",
              data: props.data.map((i) => i.count),
              barWidth: 42,
              animationDuration: 800,
            },
          ],
        };
  chart.setOption(option, true);
  chart.resize();
}

function resize() {
  chart?.resize();
}

onMounted(async () => {
  await nextTick();
  draw();
  window.addEventListener("resize", resize);
});

watch(
  () => [props.data, props.mode],
  () => nextTick(draw),
  { deep: true },
);

onBeforeUnmount(() => {
  window.removeEventListener("resize", resize);
  chart?.dispose();
  chart = null;
});
</script>

<style scoped>
.chart-box {
  width: 100%;
  height: 520px;
  min-height: 520px;
}
</style>
