<template>
  <el-upload class="single-upload" :class="{ 'is-full': fileList.length }"
    action="/api/upload/image" list-type="picture-card" :headers="headers"
    :limit="1" :file-list="fileList" :on-success="ok" :on-remove="remove"
    :on-exceed="exceed">
    <el-icon>
        <Plus />
    </el-icon>
  </el-upload>
</template>

<script setup>
import { computed } from "vue";
import { Plus } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useUserStore } from "../../stores/userStore";
import { imageUrl } from "../../utils/format";

const props = defineProps({ modelValue: { type: String, default: "" } });
const emit = defineEmits(["update:modelValue"]);
const u = useUserStore();

const headers = computed(() =>
  u.token ? { Authorization: `Bearer ${u.token}` } : {},
);

const fileList = computed(() =>
  props.modelValue ? [{ name: "图片", url: imageUrl(props.modelValue) }] : [],
);

function ok(res) {
  const url = res?.data?.data?.url || res?.data?.data?.path;
  if (url) emit("update:modelValue", url);
}

function remove() {
  emit("update:modelValue", "");
}

function exceed() {
  ElMessage.warning("主图只保留一张，重传前请先删除当前图片");
}
</script>

<style scoped>
.single-upload.is-full :deep(.el-upload--picture-card) {
  display: none;
}

.single-upload :deep(.el-upload-list__item),.single-upload :deep(.el-upload--picture-card) {
  border-radius: 16px;
}
</style>
