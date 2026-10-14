import axios from "axios";
import { ElMessage } from "element-plus";
import { useUserStore } from "../stores/userStore";

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "",
  timeout: 12000,
});

request.interceptors.request.use((config) => {
  const u = useUserStore();
  if (u.token) config.headers.Authorization = `Bearer ${u.token}`;
  return config;
});

request.interceptors.response.use(
  (res) => {
    const r = res.data;
    if (r && r.code !== 200) {
      ElMessage.error(r.message || "操作失败");
      return Promise.reject(new Error(r.message || "操作失败"));
    }
    return r ? r.data : res.data;
  },
  (err) => {
    const m =
      err.response?.data?.message ||
      err.message ||
      "网络连接失败，请确认后端已启动";
    ElMessage.error(m);
    return Promise.reject(new Error(m));
  },
);

export default request;
