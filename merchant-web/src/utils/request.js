import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { clearAuth } from '@/utils/auth'

const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

service.interceptors.request.use((config) => {
  const token = localStorage.getItem('merchant_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      if (!response.config?.silent) {
        ElMessage.error(res.msg || '请求失败')
      }
      const error = new Error(res.msg || 'Request failed')
      error.code = res.code
      error.response = response
      return Promise.reject(error)
    }
    return res.data
  },
  (error) => {
    if (error.response?.status === 401) {
      clearAuth()
      router.push('/login')
    }
    if (!error.config?.silent) {
      ElMessage.error(error.response?.data?.msg || error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export const get = (url, params, config = {}) => service.get(url, { params, ...config })
export const post = (url, data, config = {}) => service.post(url, data, config)
export const put = (url, data, config = {}) => service.put(url, data, config)
export const del = (url, params, config = {}) => service.delete(url, { params, ...config })

export default service
