import { material } from "@/router/enums";
const Layout = () => import("@/layout/index.vue");

export default {
  path: "/material",
  name: "Material",
  component: Layout,
  redirect: "/material/list",
  meta: {
    icon: "ri/folder-3-line",
    title: "资料管理",
    rank: material
  },
  children: [
    {
      path: "/material/list",
      name: "MaterialList",
      component: () => import("@/views/material/index.vue"),
      meta: {
        icon: "ri/file-download-line",
        title: "资料列表"
      }
    }
  ]
} satisfies RouteConfigsTable;
