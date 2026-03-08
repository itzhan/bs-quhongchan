import { checkin } from "@/router/enums";
const Layout = () => import("@/layout/index.vue");

export default {
  path: "/checkin",
  name: "Checkin",
  component: Layout,
  redirect: "/checkin/list",
  meta: {
    icon: "ri/map-pin-user-line",
    title: "签到管理",
    rank: checkin
  },
  children: [
    {
      path: "/checkin/list",
      name: "CheckinList",
      component: () => import("@/views/checkin/index.vue"),
      meta: {
        icon: "ri/checkbox-circle-line",
        title: "签到列表"
      }
    },
    {
      path: "/checkin/record",
      name: "CheckinRecord",
      component: () => import("@/views/checkin/record/index.vue"),
      meta: {
        icon: "ri/file-list-3-line",
        title: "签到记录"
      }
    }
  ]
} satisfies RouteConfigsTable;
