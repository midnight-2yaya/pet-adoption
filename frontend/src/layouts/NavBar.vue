<template>
  <header class="nav">
    <RouterLink to="/" class="brand">
      <span class="logo">🐾</span>
      <b>暖宠领养</b>
      </RouterLink>
    <nav>
      <RouterLink to="/">首页</RouterLink>
      <RouterLink to="/pets">宠物列表</RouterLink>
      <RouterLink v-if="userStore.isLogin" to="/my-adoptions">领养记录</RouterLink>
      <RouterLink to="/statistics">统计分析</RouterLink>
      <RouterLink v-if="userStore.isAdmin" to="/admin">后台管理</RouterLink>
    </nav>
    <div class="right">
      <template v-if="userStore.isLogin">
        <RouterLink class="user" to="/profile">
          <el-avatar :src="imageUrl(userStore.user?.avatar)" :size="32" />
          <span class="username">{{ userStore.user?.nickname || userStore.user?.username }}</span>
        </RouterLink>
        <button class="logout-btn" @click="userStore.logout()">退出</button>
      </template>
      <template v-else>
        <RouterLink to="/login">登录</RouterLink>
        <RouterLink class="register" to="/register">注册</RouterLink>
      </template>
    </div>
  </header>
</template>

<script setup>
import { useUserStore } from "../stores/userStore";
import { imageUrl } from "../utils/format";

const userStore = useUserStore();
</script>

<style scoped>
.nav {
  height: 92px;
  display: flex;
  align-items: center;
  gap: 42px;
  padding: 0 56px;
  background: rgba(255, 248, 243, 0.92);
  border-bottom: 1px solid rgba(232, 131, 90, 0.14);
  position: sticky;
  top: 0;
  z-index: 20;
  backdrop-filter: blur(12px);
  justify-content: space-between; 
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  flex-shrink: 0; 
}

.logo {
  width: 44px;
  height: 44px;
  display: grid;
  place-items: center;
  border-radius: 17px;
  background: #fff1e8;
  font-size: 22px;
}

nav {
  flex: 1;
  display: flex;
  justify-content: center;
  gap: 32px;
  font-weight: 600;
  letter-spacing: 0.5px;
  font-size: 20px;
}

nav a {
  padding: 8px 0;
  position: relative;
}

nav a.router-link-active {
  color: var(--warm-primary);
}

nav a.router-link-active:after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  bottom: 4px;
  height: 4px;
  border-radius: 4px;
  background: var(--warm-primary);
}

.right {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  flex-shrink: 0;
}

.right button {
  border: none;
  background: transparent;
  color: var(--warm-primary);
  cursor: pointer;
  font-size: 18px;
}

.register {
  border: 1px solid #f1cdb9;
  border-radius: 999px;
  padding: 10px 18px;
  color: var(--warm-primary);
}

.right .register {
  background: var(--warm-primary); 
  color: #fff;
  border: none;
  padding: 8px 20px;
}

.right .register:hover {
  opacity: 0.9;
}

.right > a:not(.register) {
  font-weight: 600;
}

.user {
  display: flex;
  align-items: center;
  gap: 10px;
  background: transparent;
  border-radius: 999px;
  padding: 0;
  box-shadow: none;
}

.username {
  max-width: 80px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis; 
}

.logout-btn {
  margin-left: 6px;
}

@media (max-width: 900px) {
  .nav {
    padding: 0 12px;
    gap: 8px;
    height: 70px;
  }
  .brand {
    font-size: 18px;
  }
  .logo {
    width: 36px;
    height: 36px;
    font-size: 16px;
  }
  nav {
    font-size: 14px;
    gap: 10px;
    justify-content: flex-end; 
  }
  .right button {
    font-size: 14px;
  }
  .brand b {
    display: none;
  }
  .right span {
    display: none;
  }
}
</style>
