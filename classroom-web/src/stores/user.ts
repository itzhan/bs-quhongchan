import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const realName = ref(localStorage.getItem('realName') || '')
  const role = ref(localStorage.getItem('role') || '')
  const avatar = ref(localStorage.getItem('avatar') || '')
  const userId = ref(localStorage.getItem('userId') || '')

  function setUser(data: any) {
    token.value = data.token
    username.value = data.username
    realName.value = data.realName
    role.value = data.role
    avatar.value = data.avatar || ''
    userId.value = String(data.userId)
    localStorage.setItem('token', data.token)
    localStorage.setItem('username', data.username)
    localStorage.setItem('realName', data.realName)
    localStorage.setItem('role', data.role)
    localStorage.setItem('avatar', data.avatar || '')
    localStorage.setItem('userId', String(data.userId))
  }

  function logout() {
    token.value = ''
    username.value = ''
    realName.value = ''
    role.value = ''
    avatar.value = ''
    userId.value = ''
    localStorage.clear()
  }

  const isLoggedIn = () => !!token.value
  const isTeacher = () => role.value === 'TEACHER'
  const isStudent = () => role.value === 'STUDENT'
  const isAdmin = () => role.value === 'ADMIN'

  return { token, username, realName, role, avatar, userId, setUser, logout, isLoggedIn, isTeacher, isStudent, isAdmin }
})
