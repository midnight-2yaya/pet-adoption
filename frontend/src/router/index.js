import { createRouter, createWebHistory } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "../stores/userStore";

const routes = [
  {
    path: "/",
    component: () => import("../views/Home.vue"),
  },
  {
    path: "/pets",
    component: () => import("../views/PetList.vue"),
  },
  {
    path: "/pets/:id",
    component: () => import("../views/PetDetail.vue"),
  },
  {
    path: "/my-adoptions",
    component: () => import("../views/MyAdoptions.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/statistics",
    component: () => import("../views/Statistics.vue"),
  },
  {
    path: "/profile",
    component: () => import("../views/Profile.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/admin",
    component: () => import("../views/Admin.vue"),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: "/login",
    component: () => import("../views/Login.vue"),
  },
  {
    path: "/register",
    component: () => import("../views/Register.vue"),
  },
  {
    path: "/:pathMatch(.*)*",
    component: () => import("../views/NotFound.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
});

router.beforeEach((to) => {
  const u = useUserStore();
  if (to.meta.requiresAuth && !u.isLogin) {
    ElMessage.warning("请先登录");
    return { path: "/login", query: { redirect: to.fullPath } };
  }
  if (to.meta.requiresAdmin && !u.isAdmin) {
    ElMessage.warning("没有管理员权限");
    return "/";
  }
  return true;
});

export default router;
