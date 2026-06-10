import { get, post, put, del } from '@/utils/request'

export const merchantLogin = (data, config) => post('/merchant/auth/login', data, config)
export const merchantApply = (data) => post('/merchant/auth/apply', data)

export const getDashboard = () => get('/merchant/dashboard')

export const getShopInfo = () => get('/merchant/shop/info')
export const updateShopInfo = (data) => put('/merchant/shop/info', data)

export const getMerchantGoodsList = (params) => get('/merchant/goods/list', params)
export const getMerchantGoodsDetail = (id) => get(`/merchant/goods/${id}`)
export const publishGoods = (data) => post('/merchant/goods', data)
export const updateGoods = (id, data) => put(`/merchant/goods/${id}`, data)
export const deleteGoods = (id) => del(`/merchant/goods/${id}`)
export const updateGoodsStatus = (id, status) => put(`/merchant/goods/${id}/status`, { status })
export const updateGoodsStock = (id, stock) => put(`/merchant/goods/${id}/stock`, { stock })

export const getOrderList = (params) => get('/merchant/order/list', params)
export const getOrderDetail = (orderNo) => get(`/merchant/order/${orderNo}`)
export const shipOrder = (orderNo, data) => post(`/merchant/order/${orderNo}/ship`, data)

export const getAftersaleList = (params) => get('/merchant/refund/list', params)
export const getAftersaleDetail = (id) => get(`/merchant/refund/${id}`)
export const handleAftersale = (id, data) => put(`/merchant/refund/${id}/handle`, data)
export const confirmAftersaleReturn = (id) => put(`/merchant/refund/${id}/confirm`)

export const getAddressList = () => get('/merchant/address/list')
export const createAddress = (data) => post('/merchant/address', data)
export const updateAddress = (id, data) => put(`/merchant/address/${id}`, data)
export const deleteAddress = (id) => del(`/merchant/address/${id}`)
export const setDefaultAddress = (id) => put(`/merchant/address/${id}/default`)

export const getFreightList = () => get('/merchant/freight/list')
export const getFreightDetail = (id) => get(`/merchant/freight/${id}`)
export const addFreight = (data) => post('/merchant/freight', data)
export const updateFreight = (id, data) => put(`/merchant/freight/${id}`, data)
export const deleteFreight = (id) => del(`/merchant/freight/${id}`)
export const setDefaultFreight = (id) => put(`/merchant/freight/${id}/default`)

export const getCategoryTree = () => get('/common/category/tree')
export const getCityTree = () => get('/common/city/tree')
