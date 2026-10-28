<template>
  <div class="dashboard-container">
    <!-- 数据卡片 -->
    <el-row :gutter="20" class="dashboard-cards">
      <el-col :span="6">
        <el-card class="card-box" shadow="hover">
          <div class="card-icon primary">
            <el-icon><Wallet /></el-icon>
          </div>
          <div class="card-info">
            <div class="card-title">本月消费</div>
            <div class="card-value">¥{{ monthlyExpense || '0.00' }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="card-box" shadow="hover">
          <div class="card-icon success">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="card-info">
            <div class="card-title">监控任务</div>
            <div class="card-value">{{ monitorTasks || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="card-box" shadow="hover">
          <div class="card-icon warning">
            <el-icon><Star /></el-icon>
          </div>
          <div class="card-info">
            <div class="card-title">当前积分</div>
            <div class="card-value">{{ userPoints || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="card-box" shadow="hover">
          <div class="card-icon info">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="card-info">
            <div class="card-title">社区帖子</div>
            <div class="card-value">{{ communityPosts || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <el-card class="quick-access-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>快捷入口</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="6" v-for="item in quickAccessItems" :key="item.path">
          <div class="quick-access-item" @click="handleQuickAccess(item.path)">
            <div class="access-icon" :style="{ backgroundColor: item.color }">
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
            <div class="access-title">{{ item.title }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Wallet, TrendCharts, Star, ChatDotRound } from '@element-plus/icons-vue'

const router = useRouter()

const monthlyExpense = ref('0.00')
const monitorTasks = ref(0)
const userPoints = ref(0)
const communityPosts = ref(0)

const quickAccessItems = [
  { title: 'AI凑单计算器', path: '/calc-plan', icon: 'Calculator', color: '#409EFF' },
  { title: '价格监控', path: '/monitor', icon: 'TrendCharts', color: '#67C23A' },
  { title: '消费账单', path: '/bill', icon: 'Wallet', color: '#E6A23C' },
  { title: '积分中心', path: '/point', icon: 'Star', color: '#F56C6C' }
]

const handleQuickAccess = (path) => {
  router.push(path)
}

onMounted(() => {
  // 模拟数据加载
  monthlyExpense.value = '2,458.00'
  monitorTasks.value = 3
  userPoints.value = 1250
  communityPosts.value = 156
})
</script>

<style scoped>
.dashboard-container {
  padding: 0;
}

.dashboard-cards {
  margin-bottom: 20px;
}

.card-box {
  display: flex;
  align-items: center;
  padding: 20px;
  transition: all 0.3s;
}

.card-box:hover {
  transform: translateY(-5px);
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  margin-right: 15px;
}

.card-icon.primary { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.card-icon.success { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
.card-icon.warning { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
.card-icon.info { background: linear-gradient(135deg, #30cfd0 0%, #330867 100%); }

.card-info {
  flex: 1;
}

.card-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.quick-access-card {
  margin-top: 20px;
}

.card-header {
  font-weight: 500;
  font-size: 16px;
}

.quick-access-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 8px;
}

.quick-access-item:hover {
  background-color: #f5f7fa;
}

.access-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  margin-bottom: 10px;
}

.access-title {
  font-size: 14px;
  color: #606266;
}
</style>
