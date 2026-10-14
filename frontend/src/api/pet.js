import request from "./index";

export const petApi = {
  list: (p) => request.get("/api/pets", { params: p }),
  detail: (id) => request.get(`/api/pets/${id}`),
  types: () => request.get("/api/pets/types"),
  hot: (size = 6) => request.get("/api/pets/hot", { params: { size } }),
};
