import request from '@/utils/request'

export function addPoint(data) {
  return request({
    url: '/point/add',
    method: 'post',
    data
  })
}

export function getPointLogList() {
  return request({
    url: '/point/log-list',
    method: 'get'
  })
}
