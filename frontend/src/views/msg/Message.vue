<template>
  <div class="message-container">
    <el-card class="message-card">
      <template #header>
        <div class="card-header">
          <span>消息中心</span>
          <el-button size="small" @click="markAllAsRead">全部已读</el-button>
        </div>
      </template>

      <el-table :data="messageList" style="width: 100%">
        <el-table-column type="expand">
          <template #default="scope">
            <div class="message-content">
              <p>{{ scope.row.content }}</p>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.isRead === 0 ? 'danger' : 'info'" size="small">
              {{ scope.row.isRead === 0 ? '未读' : '已读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="messageType" label="消息类型" width="150" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button 
              size="small" 
              :type="scope.row.isRead === 0 ? 'primary' : 'default'" 
              @click="markAsRead(scope.row.id)"
            >
              {{ scope.row.isRead === 0 ? '标记已读' : '已读' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const messageList = ref([
  { id: 1, messageType: 'price_alert', title: '价格监控告警', content: '商品【商品A】价格已降至¥119，低于您的目标价¥120！', isRead: 0, createTime: '2026-01-01 10:00:00' },
  { id: 2, messageType: 'comment_notice', title: '评论通知', content: '您的帖子【双十一捡漏攻略】收到了一条新评论', isRead: 1, createTime: '2025-12-31 15:00:00' }
])

const markAsRead = (id) => {
  const message = messageList.value.find(m => m.id === id)
  if (message && message.isRead === 0) {
    message.isRead = 1
    ElMessage.success('标记已读成功')
  }
}

const markAllAsRead = () => {
  messageList.value.forEach(msg => {
    msg.isRead = 1
  })
  ElMessage.success('全部标记已读成功')
}
</script>

<style scoped>
.message-container {
  padding: 20px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message-content {
  padding: 20px;
  background: #f5f7fa;
  line-height: 1.6;
}
</style>
