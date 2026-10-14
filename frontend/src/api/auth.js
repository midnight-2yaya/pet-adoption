import request from "./index";

export const authApi = {
  login: (d) => request.post("/api/auth/login", d),
  register: (d) => request.post("/api/auth/register", d),
  info: () => request.get("/api/user/info"),
  updateProfile: (d) => request.put("/api/user/info", d),
};
