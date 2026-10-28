<template>
  <el-container class="home-container">
    <el-aside width="200px" class="sidebar">
      <div class="logo">精明消费AI平台</div>
      <el-menu
        :default-active="activeMenu"
        router
        class="el-menu-vertical"
      >
        <el-menu-item index="/">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/calc">
          <el-icon><Calculator /></el-icon>
          <span>凑单计算器</span>
        </el-menu-item>
        <el-menu-item index="/monitor">
          <el-icon><TrendCharts /></el-icon>
          <span>价格监控</span>
        </el-menu-item>
        <el-menu-item index="/bill">
          <el-icon><Wallet /></el-icon>
          <span>消费账单</span>
        </el-menu-item>
        <el-menu-item index="/community">
          <el-icon><ChatDotRound /></el-icon>
          <span>社区分享</span>
        </el-menu-item>
        <el-menu-item index="/msg">
          <el-icon><Message /></el-icon>
          <span>消息中心</span>
          <el-badge v-if="unreadCount > 0" :value="unreadCount" class="message-badge" />
        </el-menu-item>
        <el-menu-item index="/sync">
          <el-icon><Refresh /></el-icon>
          <span>同步管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              <span>{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { logout } from '@/api/auth'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => {
  const titles = {
    '/': '首页',
    '/calc': '凑单计算器',
    '/monitor': '价格监控',
    '/bill': '消费账单',
    '/community': '社区分享',
    '/msg': '消息中心',
    '/sync': '同步管理'
  }
  return titles[route.path] || '精明消费AI平台'
})

const unreadCount = computed(() => {
  // 简化处理，实际应从消息接口获取
  return 0
})

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await logout()
      } catch (error) {
        console.error('登出失败:', error)
      } finally {
        userStore.logout()
        router.push('/login')
      }
    })
  }
}
</script>

<style scoped>
.home-container {
  height: 100vh;
}

.sidebar {
  background: #304156;
  color: white;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
  background: #2b3a4b;
  color: white;
}

.el-menu-vertical {
  border: none;
  background: #304156;
}

.el-menu-vertical .el-menu-item {
  color: #bfcbd9;
}

.el-menu-vertical .el-menu-item:hover,
.el-menu-vertical .el-menu-item.is-active {
  background: #263445;
  color: #409eff;
}

.header {
  background: white;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.page-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #666;
}

.user-info span {
  margin-left: 8px;
}

.main-content {
  background: #f5f7fa;
  padding: 20px;
}

.message-badge {
  margin-left: 8px;
}
</style>
