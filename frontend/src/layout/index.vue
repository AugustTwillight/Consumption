<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="layout-aside">
      <div class="logo-container">
        <div class="logo-icon">🧠</div>
        <div class="logo-text">精明消费AI</div>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <span>首页概览</span>
        </el-menu-item>
        <el-menu-item index="/calc-plan">
          <el-icon><Calculator /></el-icon>
          <span>AI凑单计算器</span>
        </el-menu-item>
        <el-menu-item index="/monitor">
          <el-icon><TrendCharts /></el-icon>
          <span>价格监控</span>
        </el-menu-item>
        <el-menu-item index="/bill">
          <el-icon><Wallet /></el-icon>
          <span>消费账单</span>
        </el-menu-item>
        <el-menu-item index="/point">
          <el-icon><Star /></el-icon>
          <span>积分中心</span>
        </el-menu-item>
        <el-menu-item index="/community">
          <el-icon><ChatDotRound /></el-icon>
          <span>社区分享</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="layout-header">
        <div class="header-left">
          <h3 class="page-title">{{ pageTitle }}</h3>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              <span class="username">{{ userStore.userInfo?.username || '用户' }}</span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  House,
  Calculator,
  TrendCharts,
  Wallet,
  Star,
  ChatDotRound,
  User,
  ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const pageTitle = computed(() => {
  const titleMap = {
    '/dashboard': '首页概览',
    '/calc-plan': 'AI凑单计算器',
    '/monitor': '价格监控',
    '/bill': '消费账单',
    '/point': '积分中心',
    '/community': '社区分享'
  }
  return titleMap[route.path] || '精明消费AI平台'
})

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
      ElMessage.success('已退出登录')
    })
  } else if (command === 'profile') {
    ElMessage.info('个人中心功能开发中...')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout-aside {
  background-color: #304156;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  transition: width 0.3s;
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
  font-size: 28px;
  margin-right: 10px;
}

.logo-text {
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  letter-spacing: 1px;
}

.el-menu-vertical {
  border-right: none;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 220px;
}

.layout-header {
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.page-title {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  margin: 0 8px;
  color: #606266;
}

.layout-main {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}

/* 路由过渡动画 */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>
