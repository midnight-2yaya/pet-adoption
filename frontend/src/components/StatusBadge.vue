<template>
  <el-tag :type="statusType(status)" effect="light">{{statusLabel(status)}}</el-tag>
</template>
<script setup>
import { statusLabel, statusType } from "../utils/format";

defineProps({ status: String });
</script>
