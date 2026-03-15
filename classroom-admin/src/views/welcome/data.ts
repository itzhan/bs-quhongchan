import GroupLine from "~icons/ri/group-line";
import UserLine from "~icons/ri/user-line";
import BookLine from "~icons/ri/book-open-line";
import CheckLine from "~icons/ri/checkbox-circle-line";

/** 顶部四卡片配置（值会被 API 数据覆盖） */
export const cardConfigs = [
  {
    icon: GroupLine,
    bgColor: "#effaff",
    color: "#41b6ff",
    duration: 2200,
    name: "学生总数",
    key: "totalStudents"
  },
  {
    icon: UserLine,
    bgColor: "#fff5f4",
    color: "#e85f33",
    duration: 1600,
    name: "教师总数",
    key: "totalTeachers"
  },
  {
    icon: BookLine,
    bgColor: "#eff8f4",
    color: "#26ce83",
    duration: 1500,
    name: "课程总数",
    key: "totalCourses"
  },
  {
    icon: CheckLine,
    bgColor: "#f6f4fe",
    color: "#7846e5",
    duration: 1800,
    name: "签到活动",
    key: "totalCheckins"
  }
];

export { default as dayjs } from "dayjs";
