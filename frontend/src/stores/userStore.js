import { defineStore } from "pinia";
import { authApi } from "../api/auth";

export const useUserStore = defineStore("user", {
  state: () => ({
    token: localStorage.getItem("token") || "",
    user: JSON.parse(localStorage.getItem("user") || "null"),
  }),
  getters: {
    isLogin: (s) => Boolean(s.token),
    isAdmin: (s) => s.user?.role === "ADMIN",
  },
  actions: {
    async login(f) {
      const d = await authApi.login(f);
      this.token = d.token;
      this.user = d.user;
      localStorage.setItem("token", this.token);
      localStorage.setItem("user", JSON.stringify(this.user));
      return d;
    },
    async register(f) {
      return authApi.register(f);
    },
    async fetchUser() {
      if (!this.token) return null;
      this.user = await authApi.info();
      localStorage.setItem("user", JSON.stringify(this.user));
      return this.user;
    },
    async updateProfile(f) {
      this.user = await authApi.updateProfile(f);
      localStorage.setItem("user", JSON.stringify(this.user));
      return this.user;
    },
    logout() {
      this.token = "";
      this.user = null;
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      location.href = "/login";
    },
  },
});
