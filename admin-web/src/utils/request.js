import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

service.interceptors.request.use((config) => {
  const token = localStorage.getItem('admin_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || 'Request failed'))
    }
    return res.data
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_name')
      router.push('/login')
    }
    ElMessage.error(error.response?.data?.msg || error.message || '网络错误')
    return Promise.reject(error)
  }
)

export const get = (url, params) => service.get(url, { params })
export const post = (url, data) => service.post(url, data)
export const put = (url, data) => service.put(url, data)
export const del = (url, params) => service.delete(url, { params })

export default service

