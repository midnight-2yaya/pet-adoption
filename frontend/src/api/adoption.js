import request from "./index";

export const adoptionApi = {
  apply: (d) => request.post("/api/adoptions", d),
  my: (p) => request.get("/api/adoptions/my", { params: p }),
  cancel: (id) => request.delete(`/api/adoptions/${id}`),
  statistics: (scope = "all") =>
    request.get("/api/adoptions/statistics", { params: { scope } }),
  summary: () => request.get("/api/adoptions/summary"),
};
