import { announcement } from "@/router/enums";
const Layout = () => import("@/layout/index.vue");

export default {
  path: "/announcement",
  name: "Announcement",
  component: Layout,
  redirect: "/announcement/list",
  meta: {
    icon: "ri/notification-3-line",
    title: "公告管理",
    rank: announcement
  },
  children: [
    {
      path: "/announcement/list",
      name: "AnnouncementList",
      component: () => import("@/views/announcement/index.vue"),
      meta: {
        icon: "ri/megaphone-line",
        title: "公告列表"
      }
    }
  ]
} satisfies RouteConfigsTable;
