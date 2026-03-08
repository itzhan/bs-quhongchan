import { message } from "@/router/enums";
const Layout = () => import("@/layout/index.vue");

export default {
  path: "/message",
  name: "Message",
  component: Layout,
  redirect: "/message/list",
  meta: {
    icon: "ri/message-3-line",
    title: "消息管理",
    rank: message
  },
  children: [
    {
      path: "/message/list",
      name: "MessageList",
      component: () => import("@/views/message/index.vue"),
      meta: {
        icon: "ri/chat-1-line",
        title: "消息列表"
      }
    }
  ]
} satisfies RouteConfigsTable;
