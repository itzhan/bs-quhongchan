import { auditlog } from "@/router/enums";
const Layout = () => import("@/layout/index.vue");

export default {
  path: "/auditlog",
  name: "AuditLog",
  component: Layout,
  redirect: "/auditlog/list",
  meta: {
    icon: "ri/shield-check-line",
    title: "审计日志",
    rank: auditlog,
    roles: ["ADMIN"]
  },
  children: [
    {
      path: "/auditlog/list",
      name: "AuditLogList",
      component: () => import("@/views/auditlog/index.vue"),
      meta: {
        icon: "ri/file-shield-2-line",
        title: "操作日志",
        roles: ["ADMIN"]
      }
    }
  ]
} satisfies RouteConfigsTable;
