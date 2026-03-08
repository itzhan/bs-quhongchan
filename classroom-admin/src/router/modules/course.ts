import { course } from "@/router/enums";
const Layout = () => import("@/layout/index.vue");

export default {
  path: "/course",
  name: "Course",
  component: Layout,
  redirect: "/course/list",
  meta: {
    icon: "ri/book-2-line",
    title: "课程管理",
    rank: course
  },
  children: [
    {
      path: "/course/list",
      name: "CourseList",
      component: () => import("@/views/course/index.vue"),
      meta: {
        icon: "ri/book-open-line",
        title: "课程列表"
      }
    },
    {
      path: "/course/member",
      name: "CourseMember",
      component: () => import("@/views/course/member/index.vue"),
      meta: {
        icon: "ri/team-line",
        title: "课程成员"
      }
    }
  ]
} satisfies RouteConfigsTable;
