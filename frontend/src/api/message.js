import request from '@/utils/request'

export function getMessageList(isRead) {
  return request({
    url: '/message/list',
    method: 'get',
    params: isRead !== undefined ? { isRead } : {}
  })
}

export function markAsRead(id) {
  return request({
    url: `/message/read/${id}`,
    method: 'put'
  })
}

export function markAllAsRead() {
  return request({
    url: '/message/read-all',
    method: 'put'
  })
}
