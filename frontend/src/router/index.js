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
    name: 'Home',
    component: () => import('@/views/home/Home.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/home/Dashboard.vue')
      },
      {
        path: 'calc',
        name: 'Calc',
        component: () => import('@/views/calc/Calc.vue')
      },
      {
        path: 'monitor',
        name: 'Monitor',
        component: () => import('@/views/monitor/Monitor.vue')
      },
      {
        path: 'bill',
        name: 'Bill',
        component: () => import('@/views/bill/Bill.vue')
      },
      {
        path: 'community',
        name: 'Community',
        component: () => import('@/views/community/Community.vue')
      },
      {
        path: 'msg',
        name: 'Message',
        component: () => import('@/views/msg/Message.vue')
      },
      {
        path: 'sync',
        name: 'Sync',
        component: () => import('@/views/sync/Sync.vue')
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
  
  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
  } else if (to.path === '/login' && userStore.token) {
    next('/')
  } else {
    next()
  }
})

export default router
