import request from '@/utils/request'

export function saveMonitorTask(data) {
  return request({
    url: '/monitor/task',
    method: 'post',
    data
  })
}

export function getMonitorTaskList() {
  return request({
    url: '/monitor/task-list',
    method: 'get'
  })
}

export function updateMonitorPrice(data) {
  return request({
    url: '/monitor/update-price',
    method: 'post',
    data
  })
}

export function deleteMonitorTask(id) {
  return request({
    url: `/monitor/task-delete/${id}`,
    method: 'delete'
  })
}

export function toggleMonitorTaskStatus(id, status) {
  return request({
    url: `/monitor/task-toggle/${id}/${status}`,
    method: 'put'
  })
}

export function getMonitorPriceRecords(id) {
  return request({
    url: `/monitor/price-records/${id}`,
    method: 'get'
  })
}
