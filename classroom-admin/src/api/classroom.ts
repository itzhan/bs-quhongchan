import { http } from "@/utils/http";

/** 通用分页结果类型 */
type PageResult = {
  code: number;
  message: string;
  data: {
    records: Array<any>;
    total: number;
    size: number;
    current: number;
  };
};

type Result = {
  code: number;
  message: string;
  data?: any;
};

// ========== 用户管理 ==========
export const getUserPage = (params?: object) =>
  http.request<PageResult>("get", "/api/user/page", { params });

export const updateUserStatus = (id: number, status: number) =>
  http.request<Result>("put", `/api/user/${id}/status?status=${status}`);

export const resetUserPassword = (id: number) =>
  http.request<Result>("put", `/api/user/${id}/reset-password`);

// ========== 课程管理 ==========
export const getCoursePage = (params?: object) =>
  http.request<PageResult>("get", "/api/course/page", { params });

export const createCourse = (data?: object) =>
  http.request<Result>("post", "/api/course", { data });

export const updateCourse = (id: number, data?: object) =>
  http.request<Result>("put", `/api/course/${id}`, { data });

export const deleteCourse = (id: number) =>
  http.request<Result>("delete", `/api/course/${id}`);

export const getCourseDetail = (id: number) =>
  http.request<Result>("get", `/api/course/${id}`);

// ========== 课程成员 ==========
export const getCourseMemberPage = (params?: object) =>
  http.request<PageResult>("get", "/api/course-member/page", { params });

export const getStudentCourses = () =>
  http.request<Result>("get", "/api/course/my-courses");

export const joinCourse = (courseCode: string) =>
  http.request<Result>("post", `/api/course/join?courseCode=${courseCode}`);

export const quitCourse = (courseId: number) =>
  http.request<Result>("delete", `/api/course/quit/${courseId}`);

export const removeCourseMember = (id: number) =>
  http.request<Result>("delete", `/api/course-member/${id}`);

// ========== 签到管理 ==========
export const getCheckinPage = (params?: object) =>
  http.request<PageResult>("get", "/api/checkin/page", { params });

export const createCheckin = (data?: object) =>
  http.request<Result>("post", "/api/checkin", { data });

export const endCheckin = (id: number) =>
  http.request<Result>("put", `/api/checkin/${id}/end`);

// ========== 签到记录 ==========
export const getCheckinRecordPage = (params?: object) =>
  http.request<PageResult>("get", "/api/checkin-record/page", { params });

export const doCheckin = (checkinId: number) =>
  http.request<Result>("post", `/api/checkin-record/checkin/${checkinId}`);

export const getCheckinStats = (courseId: number) =>
  http.request<Result>("get", `/api/checkin-record/stats/${courseId}`);

// ========== 作业管理 ==========
export const getAssignmentPage = (params?: object) =>
  http.request<PageResult>("get", "/api/assignment/page", { params });

export const createAssignment = (data?: object) =>
  http.request<Result>("post", "/api/assignment", { data });

export const updateAssignment = (id: number, data?: object) =>
  http.request<Result>("put", `/api/assignment/${id}`, { data });

export const deleteAssignment = (id: number) =>
  http.request<Result>("delete", `/api/assignment/${id}`);

// ========== 作业提交 ==========
export const getSubmissionPage = (params?: object) =>
  http.request<PageResult>("get", "/api/submission/page", { params });

export const submitAssignment = (data?: object) =>
  http.request<Result>("post", "/api/submission", { data });

export const gradeSubmission = (id: number, data?: object) =>
  http.request<Result>("put", `/api/submission/${id}/grade`, { data });

// ========== 成绩管理 ==========
export const getGradePage = (params?: object) =>
  http.request<PageResult>("get", "/api/grade/page", { params });

export const calculateGrade = (data?: object) =>
  http.request<Result>("post", "/api/grade/calculate", { data });

export const getGradeStats = (courseId: number) =>
  http.request<Result>("get", `/api/grade/stats/${courseId}`);

// ========== 公告管理 ==========
export const getAnnouncementPage = (params?: object) =>
  http.request<PageResult>("get", "/api/announcement/page", { params });

export const createAnnouncement = (data?: object) =>
  http.request<Result>("post", "/api/announcement", { data });

export const updateAnnouncement = (id: number, data?: object) =>
  http.request<Result>("put", `/api/announcement/${id}`, { data });

export const deleteAnnouncement = (id: number) =>
  http.request<Result>("delete", `/api/announcement/${id}`);

// ========== 资料管理 ==========
export const getMaterialPage = (params?: object) =>
  http.request<PageResult>("get", "/api/material/page", { params });

export const createMaterial = (data?: object) =>
  http.request<Result>("post", "/api/material", { data });

export const deleteMaterial = (id: number) =>
  http.request<Result>("delete", `/api/material/${id}`);

// ========== 消息管理 ==========
export const getMessagePage = (params?: object) =>
  http.request<PageResult>("get", "/api/message/page", { params });

export const sendMessage = (data?: object) =>
  http.request<Result>("post", "/api/message", { data });

export const markMessageRead = (id: number) =>
  http.request<Result>("put", `/api/message/${id}/read`);

export const getUnreadCount = () =>
  http.request<Result>("get", "/api/message/unread-count");

// ========== 审计日志 ==========
export const getAuditLogPage = (params?: object) =>
  http.request<PageResult>("get", "/api/audit-log/page", { params });

// ========== 文件上传 ==========
export const uploadFile = (data: FormData) =>
  http.request<Result>("post", "/api/file/upload", { data }, {
    headers: { "Content-Type": "multipart/form-data" }
  });

// ========== 仪表盘统计 ==========
export const getDashboardStats = () =>
  http.request<Result>("get", "/api/dashboard/stats");
