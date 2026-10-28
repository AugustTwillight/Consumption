import request from '@/utils/request'

export function calculateCalcPlan(data) {
  return request({
    url: '/calc-plan/calculate',
    method: 'post',
    data
  })
}

export function saveCalcPlan(data) {
  return request({
    url: '/calc-plan/save',
    method: 'post',
    data
  })
}

export function getCalcPlanList() {
  return request({
    url: '/calc-plan/list',
    method: 'get'
  })
}

export function getCalcPlanDetail(id) {
  return request({
    url: `/calc-plan/detail/${id}`,
    method: 'get'
  })
}

export function deleteCalcPlan(id) {
  return request({
    url: `/calc-plan/delete/${id}`,
    method: 'delete'
  })
}
