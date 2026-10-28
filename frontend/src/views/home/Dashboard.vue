<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #409eff;">
            <el-icon><Calculator /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">0</div>
            <div class="stat-label">凑单方案数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #67c23a;">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">0</div>
            <div class="stat-label">监控任务数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #e6a23c;">
            <el-icon><Wallet /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥0.00</div>
            <div class="stat-label">本月消费</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #f56c6c;">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">0</div>
            <div class="stat-label">社区帖子</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="welcome-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>欢迎使用精明消费AI平台</span>
        </div>
      </template>
      <div class="welcome-content">
        <p>精明消费AI平台帮助您理性消费，自动计算满减/津贴/券最优凑单方案，长期追踪商品价格，记录消费，复盘性价比。</p>
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="8">
            <el-card shadow="hover" class="feature-card">
              <el-icon size="40" color="#409eff"><Calculator /></el-icon>
              <h3>AI凑单计算</h3>
              <p>录入商品和优惠规则，AI自动求解最优下单组合</p>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="feature-card">
              <el-icon size="40" color="#67c23a"><TrendCharts /></el-icon>
              <h3>价格监控</h3>
              <p>追踪商品价格变化，低于目标价时及时通知</p>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="feature-card">
              <el-icon size="40" color="#e6a23c"><Wallet /></el-icon>
              <h3>消费复盘</h3>
              <p>记录消费账单，月度复盘报表统计支出</p>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.dashboard {
  padding: 20px 0;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.welcome-card {
  margin-top: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
}

.welcome-content {
  color: #666;
  line-height: 1.6;
}

.feature-card {
  text-align: center;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-card h3 {
  margin: 15px 0 10px;
  color: #333;
}

.feature-card p {
  font-size: 14px;
  color: #666;
}
</style>
