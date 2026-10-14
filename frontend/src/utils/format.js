export const imageUrl = (url) => url || "/placeholder-pet.svg";

export function genderLabel(value) {
  if (value === "男" || value === "male") return "公";
  if (value === "女" || value === "female") return "母";
  return value || "未知";
}

export function statusLabel(s) {
  return (
    {
      available: "待领养",
      adopted: "已领养",
      offline: "已下架",
      pending: "待审核",
      approved: "已通过",
      rejected: "已拒绝",
      canceled: "已取消",
    }[s] ||
    s ||
    "-"
  );
}

export function statusType(s) {
  return (
    {
      available: "success",
      adopted: "info",
      offline: "warning",
      pending: "warning",
      approved: "success",
      rejected: "danger",
      canceled: "info",
    }[s] || "info"
  );
}

export const formatDate = (v) =>
  v ? String(v).replace("T", " ").slice(0, 16) : "-";

export function debounce(fn, delay = 300) {
  let t;
  return (...args) => {
    clearTimeout(t);
    t = setTimeout(() => fn(...args), delay);
  };
}
