<template>
  <el-upload action="/api/upload/image" list-type="picture-card" multiple :headers="headers"
    :file-list="fileList" :on-success="ok" :on-remove="remove">
    <el-icon>
        <Plus />
    </el-icon>
  </el-upload>
</template>

<script setup>
import { computed } from "vue";
import { Plus } from "@element-plus/icons-vue";
import { useUserStore } from "../../stores/userStore";
import { imageUrl } from "../../utils/format";

const props = defineProps({ modelValue: { type: Array, default: () => [] } });
const emit = defineEmits(["update:modelValue"]);
const u = useUserStore();

const headers = computed(() =>
  u.token ? { Authorization: `Bearer ${u.token}` } : {},
);

const fileList = computed(() =>
  props.modelValue.map((url, i) => ({
    name: `轮播图${i + 1}`,
    url: imageUrl(url),
  })),
);

function ok(res) {
  const url = res?.data?.data?.url || res?.data?.data?.path;
  if (url) emit("update:modelValue", [...props.modelValue, url]);
}

function remove(file) {
  emit(
    "update:modelValue",
    props.modelValue.filter(
      (url) => imageUrl(url) !== file.url && url !== file.url,
    ),
  );
}
</script>
