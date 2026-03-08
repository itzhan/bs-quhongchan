import { http } from "@/utils/http";

type Result = {
  code: number;
  message: string;
  data: Array<any>;
};

export const getAsyncRoutes = () => {
  // 本项目使用前端静态路由，不需要后端返回动态路由
  return Promise.resolve({ code: 0, data: [] } as Result);
};
