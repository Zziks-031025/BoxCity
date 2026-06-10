export const USER_ROLE_ADMIN = 'admin'
export const USER_ROLE_MERCHANT = 'merchant'

const USER_ROLE_KEY = 'user_role'
const DISPLAY_NAME_KEY = 'display_name'

export function clearAuth() {
  localStorage.removeItem('merchant_token')
  localStorage.removeItem('merchant_shop_name')
  localStorage.removeItem('admin_token')
  localStorage.removeItem('admin_name')
  localStorage.removeItem(USER_ROLE_KEY)
  localStorage.removeItem(DISPLAY_NAME_KEY)
}

export function getUserRole() {
  const storedRole = localStorage.getItem(USER_ROLE_KEY)
  if (storedRole) {
    return storedRole
  }
  if (localStorage.getItem('admin_token')) {
    return USER_ROLE_ADMIN
  }
  if (localStorage.getItem('merchant_token')) {
    return USER_ROLE_MERCHANT
  }
  return ''
}

export function hasSession() {
  return Boolean(localStorage.getItem('merchant_token') || localStorage.getItem('admin_token'))
}

export function storeMerchantSession(data) {
  clearAuth()
  localStorage.setItem(USER_ROLE_KEY, USER_ROLE_MERCHANT)
  localStorage.setItem('merchant_token', data.token)
  localStorage.setItem('merchant_shop_name', data.shopName || '我的店铺')
  localStorage.setItem(DISPLAY_NAME_KEY, data.shopName || '我的店铺')
}

export function storeAdminSession(data, username = 'admin') {
  clearAuth()
  localStorage.setItem(USER_ROLE_KEY, USER_ROLE_ADMIN)
  localStorage.setItem('admin_token', data.token)
  localStorage.setItem('admin_name', data.username || username)
  localStorage.setItem(DISPLAY_NAME_KEY, data.username || username)
}

export function getDisplayName() {
  return (
    localStorage.getItem(DISPLAY_NAME_KEY) ||
    localStorage.getItem('merchant_shop_name') ||
    localStorage.getItem('admin_name') ||
    ''
  )
}

export function getRoleLabel() {
  return getUserRole() === USER_ROLE_ADMIN ? '平台管理' : '商家后台'
}

export function getRoleHomePath(role = getUserRole()) {
  return role === USER_ROLE_ADMIN ? '/admin/dashboard' : '/dashboard'
}
