import { del, get, post, put } from '@/utils/request'

export const adminLogin = (data) => post('/admin/auth/login', data)

export const getDashboard = () => get('/admin/dashboard')

export const getUserList = (params) => get('/admin/user/list', params)
export const getUserDetail = (id) => get(`/admin/user/${id}`)
export const updateUserStatus = (id, status) => put(`/admin/user/${id}/status`, { status })

export const getReportList = (params) => get('/admin/report/list', params)
export const handleReport = (id, handleResult) => put(`/admin/report/${id}/handle`, { handleResult })

export const getMerchantAuditList = (params) => get('/admin/merchant/audit/list', params)
export const getMerchantAuditDetail = (id) => get(`/admin/merchant/audit/${id}`)
export const auditMerchant = (id, data) => put(`/admin/merchant/audit/${id}`, data)
export const getMerchantList = (params) => get('/admin/merchant/list', params)
export const updateMerchantStatus = (id, data) => put(`/admin/merchant/${id}/status`, data)

export const getGoodsAuditList = (params) => get('/admin/goods/audit/list', params)
export const getGoodsAuditDetail = (id) => get(`/admin/goods/audit/${id}`)
export const auditGoods = (id, data) => put(`/admin/goods/audit/${id}`, data)

export const getOrderList = (params) => get('/admin/order/list', params)
export const getDisputeList = (params) => get('/admin/order/dispute/list', params)
export const handleDispute = (id, data) => put(`/admin/order/dispute/${id}/handle`, data)
export const getAbnormalOrders = (params) => get('/admin/order/abnormal/list', params)

export const getCategoryTree = () => get('/admin/category/tree')
export const createCategory = (data) => post('/admin/category', data)
export const updateCategory = (id, data) => put(`/admin/category/${id}`, data)
export const deleteCategory = (id) => del(`/admin/category/${id}`)

export const getCityList = (params) => get('/admin/system/city/list', params)
export const createCity = (data) => post('/admin/system/city', data)
export const updateCity = (id, data) => put(`/admin/system/city/${id}`, data)
export const deleteCity = (id) => del(`/admin/system/city/${id}`)

export const getPaymentConfig = () => get('/admin/system/payment')
export const updatePaymentConfig = (data) => put('/admin/system/payment', data)
export const getFreightConfig = () => get('/admin/system/freight')
export const updateFreightConfig = (data) => put('/admin/system/freight', data)

export const getBannerList = (params) => get('/admin/system/banner/list', params)
export const createBanner = (data) => post('/admin/system/banner', data)
export const updateBanner = (id, data) => put(`/admin/system/banner/${id}`, data)
export const deleteBanner = (id) => del(`/admin/system/banner/${id}`)

