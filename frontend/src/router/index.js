import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '首页概览' }
      },
      {
        path: 'calc-plan',
        name: 'CalcPlan',
        component: () => import('@/views/calc-plan/CalcPlan.vue'),
        meta: { title: 'AI凑单计算器' }
      },
      {
        path: 'monitor',
        name: 'Monitor',
        component: () => import('@/views/monitor/Monitor.vue'),
        meta: { title: '价格监控' }
      },
      {
        path: 'bill',
        name: 'Bill',
        component: () => import('@/views/bill/Bill.vue'),
        meta: { title: '消费账单' }
      },
      {
        path: 'community',
        name: 'Community',
        component: () => import('@/views/community/Community.vue'),
        meta: { title: '社区分享' }
      },
      {
        path: 'point',
        name: 'Point',
        component: () => import('@/views/point/Point.vue'),
        meta: { title: '积分中心' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth !== false && !userStore.token) {
    next('/login')
  } else if (to.path === '/login' && userStore.token) {
    next('/')
  } else {
    next()
  }
})

export default router
