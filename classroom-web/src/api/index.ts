import http from '@/utils/http'

// ==================== 认证 ====================
export const login = (data: any) => http.post('/api/auth/login', data)
export const register = (data: any) => http.post('/api/auth/register', data)

// ==================== 课程（通用） ====================
export const getCoursePage = (params: any) => http.get('/api/course/page', { params })
export const getCourseDetail = (id: number) => http.get(`/api/course/${id}`)
export const getMyCourses = () => http.get('/api/course/my')

// ==================== 课程（学生） ====================
export const joinCourse = (courseCode: string) => http.post(`/api/course/join?courseCode=${courseCode}`)
export const quitCourse = (courseId: number) => http.post(`/api/course/${courseId}/quit`)

// ==================== 课程（教师） ====================
export const createCourse = (data: any) => http.post('/api/course', data)
export const updateCourse = (data: any) => http.put('/api/course', data)
export const deleteCourse = (id: number) => http.delete(`/api/course/${id}`)
export const getCourseMembers = (courseId: number, params?: any) => http.get(`/api/course/${courseId}/members`, { params })
export const removeMember = (courseId: number, studentId: number) => http.delete(`/api/course/${courseId}/members/${studentId}`)

// ==================== 签到（通用） ====================
export const getCheckinPage = (params: any) => http.get('/api/checkin/page', { params })

// ==================== 签到（学生） ====================
export const doCheckin = (checkinId: number) => http.post(`/api/checkin/${checkinId}/do`)

// ==================== 签到（教师） ====================
export const createCheckin = (data: any) => http.post('/api/checkin', data)
export const endCheckin = (id: number) => http.put(`/api/checkin/${id}/end`)
export const getCheckinRecords = (checkinId: number, params?: any) => http.get(`/api/checkin/${checkinId}/records`, { params })
export const getCheckinStats = (courseId: number) => http.get(`/api/checkin/stats/${courseId}`)

// ==================== 作业（通用） ====================
export const getAssignmentPage = (params: any) => http.get('/api/assignment/page', { params })
export const getAssignmentById = (id: number) => http.get(`/api/assignment/${id}`)

// ==================== 作业（学生） ====================
export const submitAssignment = (assignmentId: number, data: any) => http.post(`/api/assignment/${assignmentId}/submit`, data)

// ==================== 作业（教师） ====================
export const createAssignment = (data: any) => http.post('/api/assignment', data)
export const updateAssignment = (data: any) => http.put('/api/assignment', data)
export const deleteAssignment = (id: number) => http.delete(`/api/assignment/${id}`)
export const getSubmissionPage = (params: any) => http.get('/api/assignment/submission/page', { params })
export const gradeSubmission = (id: number, score: number, feedback?: string) =>
  http.put(`/api/assignment/submission/${id}/grade?score=${score}${feedback ? '&feedback=' + encodeURIComponent(feedback) : ''}`)

// ==================== 成绩（通用） ====================
export const getGradePage = (params: any) => http.get('/api/grade/page', { params })

// ==================== 成绩（学生） ====================
export const getMyGrades = (params: any) => http.get('/api/grade/my', { params })

// ==================== 成绩（教师） ====================
export const calculateGrade = (courseId: number, studentId: number) => http.post(`/api/grade/${courseId}/calculate/${studentId}`)
export const calculateAllGrades = (courseId: number) => http.post(`/api/grade/${courseId}/calculate-all`)
export const updateExamScore = (courseId: number, studentId: number, examScore: number) =>
  http.put(`/api/grade/${courseId}/exam/${studentId}?examScore=${examScore}`)
export const getGradeStats = (courseId: number) => http.get(`/api/grade/stats/${courseId}`)

// ==================== 公告（教师 CRUD） ====================
export const getAnnouncementPage = (params: any) => http.get('/api/announcement/page', { params })
export const createAnnouncement = (data: any) => http.post('/api/announcement', data)
export const updateAnnouncement = (data: any) => http.put('/api/announcement', data)
export const deleteAnnouncement = (id: number) => http.delete(`/api/announcement/${id}`)

// ==================== 资料（教师 CRUD） ====================
export const getMaterialPage = (params: any) => http.get('/api/material/page', { params })
export const createMaterial = (data: any) => http.post('/api/material', data)
export const deleteMaterial = (id: number) => http.delete(`/api/material/${id}`)
export const downloadMaterial = (id: number) => http.put(`/api/material/${id}/download`)

// ==================== 消息 ====================
export const getMessagePage = (params: any) => http.get('/api/message/page', { params })
export const markMessageRead = (id: number) => http.put(`/api/message/${id}/read`)
export const getUnreadCount = () => http.get('/api/message/unread-count')

// ==================== 个人 ====================
export const getProfile = () => http.get('/api/user/info')
export const updateProfile = (data: any) => http.put('/api/user/profile', data)

// ==================== 文件上传 ====================
export const uploadFile = (data: FormData) =>
  http.post('/api/file/upload', data, { headers: { 'Content-Type': 'multipart/form-data' } })
