import { grade } from "@/router/enums";
const Layout = () => import("@/layout/index.vue");

export default {
  path: "/grade",
  name: "Grade",
  component: Layout,
  redirect: "/grade/list",
  meta: {
    icon: "ri/bar-chart-box-line",
    title: "成绩管理",
    rank: grade
  },
  children: [
    {
      path: "/grade/list",
      name: "GradeList",
      component: () => import("@/views/grade/index.vue"),
      meta: {
        icon: "ri/file-chart-line",
        title: "成绩列表"
      }
    }
  ]
} satisfies RouteConfigsTable;
