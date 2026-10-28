import request from '@/utils/request'

export function pullSyncData(tableName, lastWaterTime) {
  return request({
    url: '/sync/pull',
    method: 'get',
    params: { tableName, lastWaterTime }
  })
}

export function pushSyncData(data) {
  return request({
    url: '/sync/push',
    method: 'post',
    data
  })
}
