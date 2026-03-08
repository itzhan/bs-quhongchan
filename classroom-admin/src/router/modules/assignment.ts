import { assignment } from "@/router/enums";
const Layout = () => import("@/layout/index.vue");

export default {
  path: "/assignment",
  name: "Assignment",
  component: Layout,
  redirect: "/assignment/list",
  meta: {
    icon: "ri/task-line",
    title: "作业管理",
    rank: assignment
  },
  children: [
    {
      path: "/assignment/list",
      name: "AssignmentList",
      component: () => import("@/views/assignment/index.vue"),
      meta: {
        icon: "ri/draft-line",
        title: "作业列表"
      }
    },
    {
      path: "/assignment/submission",
      name: "AssignmentSubmission",
      component: () => import("@/views/assignment/submission/index.vue"),
      meta: {
        icon: "ri/upload-2-line",
        title: "提交记录"
      }
    }
  ]
} satisfies RouteConfigsTable;
