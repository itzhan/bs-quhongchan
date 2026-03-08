import axios from 'axios'
import { useUserStore } from '@/stores/user'

const http = axios.create({
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

http.interceptors.request.use(config => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  return config
})

http.interceptors.response.use(
  res => res.data,
  err => {
    if (err.response?.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      window.location.hash = '#/login'
    }
    return Promise.reject(err)
  }
)

export default http
