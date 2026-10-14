import request from "./index";

export const adminApi = {
  pets: (p) => request.get("/api/admin/pets", { params: p }),
  createPet: (d) => request.post("/api/admin/pets", d),
  updatePet: (id, d) => request.put(`/api/admin/pets/${id}`, d),
  deletePet: (id) => request.delete(`/api/admin/pets/${id}`),
  batchOffline: (ids) => request.post("/api/admin/pets/batch-offline", { ids }),
  adoptions: (p) => request.get("/api/admin/adoptions", { params: p }),
  review: (id, d) => request.put(`/api/admin/adoptions/${id}/status`, d),
  batchReview: (d) => request.post("/api/admin/adoptions/batch-review", d),
  users: (p) => request.get("/api/admin/users", { params: p }),
  updateUser: (id, d) => request.put(`/api/admin/users/${id}`, d),
  deleteUser: (id) => request.delete(`/api/admin/users/${id}`),
  batchDisable: (ids) =>
    request.post("/api/admin/users/batch-disable", { ids }),
};
