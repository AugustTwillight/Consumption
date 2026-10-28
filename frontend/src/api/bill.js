import request from '@/utils/request'

export function createBill(data) {
  return request({
    url: '/bill/create',
    method: 'post',
    data
  })
}

export function getBillList(tag) {
  return request({
    url: '/bill/list',
    method: 'get',
    params: tag ? { tag } : {}
  })
}

export function deleteBillApi(id) {
  return request({
    url: `/bill/delete/${id}`,
    method: 'delete'
  })
}

export function getMonthlyReport(year, month) {
  return request({
    url: '/bill/monthly-report',
    method: 'get',
    params: { year, month }
  })
}
