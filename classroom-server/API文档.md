# 基于Spring Boot的学生课堂管理系统 - API接口文档

> 基础路径: `http://localhost:8089/api`
> 认证方式: JWT Bearer Token（登录后在请求头添加 `Authorization: Bearer <token>`）

---

## 1. 认证模块 `/auth`

### 1.1 用户登录

- **URL**: `POST /auth/login`
- **权限**: 公开
- **请求体**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```
- **成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "userId": 1,
    "username": "admin",
    "realName": "系统管理员",
    "avatar": null,
    "role": "ADMIN"
  }
}
```

### 1.2 用户注册

- **URL**: `POST /auth/register`
- **权限**: 公开
- **请求体**:
```json
{
  "username": "2022102310003",
  "password": "123456",
  "realName": "张三",
  "email": "zhangsan@example.com",
  "phone": "13800138000",
  "gender": 1,
  "role": "STUDENT"
}
```
- **成功响应**:
```json
{ "code": 200, "message": "操作成功", "data": null }
```

---

## 2. 用户管理模块 `/user`

### 2.1 获取当前用户信息

- **URL**: `GET /user/info`
- **权限**: 登录用户
- **响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1, "username": "admin", "realName": "系统管理员",
    "avatar": null, "email": null, "phone": null,
    "gender": 0, "role": "ADMIN", "status": 1
  }
}
```

### 2.2 修改个人信息

- **URL**: `PUT /user/profile`
- **权限**: 登录用户
- **请求体**:
```json
{
  "realName": "管理员",
  "email": "admin@school.edu.cn",
  "phone": "13900139000",
  "gender": 1,
  "avatar": "/upload/avatar.jpg"
}
```

### 2.3 用户分页列表（管理员）

- **URL**: `GET /user/page?page=1&size=10&keyword=张&role=STUDENT&status=1`
- **权限**: ADMIN
- **响应**:
```json
{
  "code": 200,
  "data": {
    "records": [{ "id": 3, "username": "2022102310001", "realName": "李明", "role": "STUDENT" }],
    "total": 2, "size": 10, "current": 1, "pages": 1
  }
}
```

### 2.4 启用/禁用用户（管理员）

- **URL**: `PUT /user/{id}/status?status=0`
- **权限**: ADMIN

### 2.5 重置密码（管理员）

- **URL**: `PUT /user/{id}/reset-password?newPassword=123456`
- **权限**: ADMIN

### 2.6 删除用户（管理员）

- **URL**: `DELETE /user/{id}`
- **权限**: ADMIN

---

## 3. 课程管理模块 `/course`

### 3.1 创建课程（教师）

- **URL**: `POST /course`
- **权限**: TEACHER
- **请求体**:
```json
{
  "courseName": "Java程序设计",
  "description": "面向对象程序设计基础课程",
  "semester": "2025-2026-1",
  "maxStudents": 100,
  "checkinWeight": 20.00,
  "assignmentWeight": 40.00,
  "examWeight": 40.00
}
```
- **响应**: 返回创建的课程对象（含自动生成的6位班课码 `courseCode`）

### 3.2 修改课程（教师）

- **URL**: `PUT /course`
- **权限**: TEACHER（仅课程创建者）

### 3.3 课程详情

- **URL**: `GET /course/{id}`
- **权限**: 登录用户

### 3.4 删除课程

- **URL**: `DELETE /course/{id}`
- **权限**: TEACHER / ADMIN

### 3.5 课程分页列表

- **URL**: `GET /course/page?page=1&size=10&keyword=Java&teacherId=2&status=1`
- **权限**: 登录用户

### 3.6 我的课程

- **URL**: `GET /course/my`
- **权限**: 登录用户
- **说明**: 教师返回自己创建的课程，学生返回已加入的课程

### 3.7 加入课程（学生）

- **URL**: `POST /course/join?courseCode=123456`
- **权限**: STUDENT

### 3.8 退出课程（学生）

- **URL**: `POST /course/{courseId}/quit`
- **权限**: STUDENT

### 3.9 课程成员列表

- **URL**: `GET /course/{courseId}/members?page=1&size=20`
- **权限**: 登录用户

### 3.10 移除成员（教师）

- **URL**: `DELETE /course/{courseId}/members/{studentId}`
- **权限**: TEACHER

---

## 4. 签到模块 `/checkin`

### 4.1 发布签到（教师）

- **URL**: `POST /checkin`
- **权限**: TEACHER
- **请求体**:
```json
{
  "courseId": 1,
  "title": "第一次签到",
  "checkinType": 1,
  "startTime": "2025-12-01 08:00:00",
  "endTime": "2025-12-01 08:15:00"
}
```
- **签到类型**: 1-普通签到 2-定位签到 3-手势签到

### 4.2 结束签到（教师）

- **URL**: `PUT /checkin/{id}/end`
- **权限**: TEACHER

### 4.3 签到活动分页列表

- **URL**: `GET /checkin/page?page=1&size=10&courseId=1&status=1`
- **权限**: 登录用户

### 4.4 学生签到

- **URL**: `POST /checkin/{checkinId}/do?latitude=34.7&longitude=113.6`
- **权限**: STUDENT

### 4.5 签到记录列表

- **URL**: `GET /checkin/{checkinId}/records?page=1&size=20&status=0`
- **权限**: 登录用户
- **签到状态**: 0-未签到 1-已签到 2-迟到 3-请假

### 4.6 签到统计

- **URL**: `GET /checkin/stats/{courseId}`
- **响应**:
```json
{
  "code": 200,
  "data": { "total": 100, "checked": 85, "late": 10, "absent": 5, "rate": "95.0" }
}
```

---

## 5. 作业模块 `/assignment`

### 5.1 布置作业（教师）

- **URL**: `POST /assignment`
- **权限**: TEACHER
- **请求体**:
```json
{
  "courseId": 1,
  "title": "第一次作业",
  "content": "完成课后练习1-5题",
  "attachmentUrl": "/upload/hw1.pdf",
  "deadline": "2025-12-15 23:59:59",
  "maxScore": 100.0,
  "allowLate": 0
}
```

### 5.2 修改作业（教师）

- **URL**: `PUT /assignment`
- **权限**: TEACHER

### 5.3 删除作业

- **URL**: `DELETE /assignment/{id}`
- **权限**: TEACHER / ADMIN

### 5.4 作业详情

- **URL**: `GET /assignment/{id}`
- **权限**: 登录用户

### 5.5 作业分页列表

- **URL**: `GET /assignment/page?page=1&size=10&courseId=1&status=1`
- **权限**: 登录用户

### 5.6 提交作业（学生）

- **URL**: `POST /assignment/{assignmentId}/submit`
- **权限**: STUDENT
- **请求体**:
```json
{
  "content": "答案内容...",
  "attachmentUrl": "/upload/submit1.docx"
}
```

### 5.7 批改作业（教师）

- **URL**: `PUT /assignment/submission/{id}/grade?score=85.0&feedback=完成度较好`
- **权限**: TEACHER

### 5.8 作业提交列表

- **URL**: `GET /assignment/submission/page?page=1&size=10&assignmentId=1&studentId=3&status=2`
- **权限**: 登录用户
- **提交状态**: 0-退回 1-已提交 2-已批改

---

## 6. 成绩模块 `/grade`

### 6.1 成绩分页列表

- **URL**: `GET /grade/page?page=1&size=10&courseId=1&studentId=3`
- **权限**: 登录用户

### 6.2 我的成绩（学生）

- **URL**: `GET /grade/my?page=1&size=10&courseId=1`
- **权限**: STUDENT

### 6.3 计算成绩（教师）

- **URL**: `POST /grade/{courseId}/calculate/{studentId}`
- **权限**: TEACHER
- **说明**: 自动计算签到成绩+作业成绩，按权重汇总

### 6.4 录入考试成绩（教师）

- **URL**: `PUT /grade/{courseId}/exam/{studentId}?examScore=88.5`
- **权限**: TEACHER

### 6.5 成绩统计

- **URL**: `GET /grade/stats/{courseId}`
- **权限**: TEACHER / ADMIN
- **响应**:
```json
{
  "code": 200,
  "data": { "count": 50, "avg": "76.3", "max": 98.0, "min": 42.0, "passRate": "86.0" }
}
```

---

## 7. 公告模块 `/announcement`

### 7.1 发布公告

- **URL**: `POST /announcement`
- **权限**: TEACHER / ADMIN
- **请求体**:
```json
{
  "title": "期末考试安排",
  "content": "考试时间为...",
  "type": 1,
  "courseId": 1,
  "isTop": 0,
  "status": 1
}
```
- **公告类型**: 1-课程公告 2-系统公告

### 7.2 修改公告

- **URL**: `PUT /announcement`
- **权限**: TEACHER / ADMIN

### 7.3 删除公告

- **URL**: `DELETE /announcement/{id}`
- **权限**: TEACHER / ADMIN

### 7.4 公告详情

- **URL**: `GET /announcement/{id}`
- **权限**: 登录用户

### 7.5 公告分页列表

- **URL**: `GET /announcement/page?page=1&size=10&courseId=1&type=1&status=1`
- **权限**: 登录用户

---

## 8. 资料模块 `/material`

### 8.1 上传资料（教师）

- **URL**: `POST /material`
- **权限**: TEACHER
- **请求体**:
```json
{
  "courseId": 1,
  "title": "第一章课件",
  "description": "面向对象基础",
  "fileUrl": "/upload/ch1.pptx",
  "fileName": "第一章课件.pptx",
  "fileSize": 2048000,
  "fileType": "application/vnd.openxmlformats-officedocument.presentationml.presentation"
}
```

### 8.2 删除资料

- **URL**: `DELETE /material/{id}`
- **权限**: TEACHER / ADMIN

### 8.3 资料分页列表

- **URL**: `GET /material/page?page=1&size=10&courseId=1`
- **权限**: 登录用户

### 8.4 下载资料（记录下载次数）

- **URL**: `PUT /material/{id}/download`
- **权限**: 登录用户

---

## 9. 留言模块 `/message`

### 9.1 发送留言

- **URL**: `POST /message`
- **权限**: 登录用户
- **请求体**:
```json
{
  "courseId": 1,
  "content": "老师，这道题不太理解",
  "parentId": null,
  "isAnonymous": 0
}
```

### 9.2 删除留言

- **URL**: `DELETE /message/{id}`
- **权限**: 登录用户

### 9.3 留言分页列表

- **URL**: `GET /message/page?page=1&size=10&courseId=1`
- **权限**: 登录用户

---

## 10. 审计日志模块 `/audit`

### 10.1 日志分页列表（管理员）

- **URL**: `GET /audit/page?page=1&size=10&userId=1&module=用户管理`
- **权限**: ADMIN
- **响应**:
```json
{
  "code": 200,
  "data": {
    "records": [{
      "id": 1, "userId": 1, "username": "admin",
      "module": "用户管理", "operation": "登录系统",
      "requestUrl": "/auth/login", "requestMethod": "POST",
      "ip": "127.0.0.1", "executionTime": 125
    }],
    "total": 50, "current": 1
  }
}
```

---

## 11. 文件上传 `/upload`

### 11.1 上传文件

- **URL**: `POST /upload`
- **权限**: 公开
- **请求**: `multipart/form-data`，字段名 `file`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "url": "/upload/abc123.pdf",
    "fileName": "课件.pdf",
    "fileSize": "2048000"
  }
}
```

---

## 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

| code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 参数校验错误 |
| 401 | 未登录/Token过期 |
| 403 | 无权限 |
| 500 | 服务器错误 |

---

## 测试账户

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 教师 | teacher001 | admin123 |
| 学生 | 2022102310001 | admin123 |
| 学生 | 2022102310002 | admin123 |
