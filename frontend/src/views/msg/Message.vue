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
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMessageList, markAsRead, markAllAsRead } from '@/api/message'

const messageList = ref([])

const loadMessageList = async () => {
  try {
    const res = await getMessageList()
    if (res.code === 200) {
      messageList.value = res.data || []
    } else {
      ElMessage.error(res.msg || '获取消息列表失败')
    }
  } catch (error) {
    console.error('获取消息列表失败:', error)
    ElMessage.error('获取消息列表失败')
  }
}

onMounted(() => {
  loadMessageList()
})

const markAsReadApi = async (id) => {
  try {
    const res = await markAsRead(id)
    if (res.code === 200) {
      const message = messageList.value.find(m => m.id === id)
      if (message && message.isRead === 0) {
        message.isRead = 1
      }
      ElMessage.success('标记已读成功')
    } else {
      ElMessage.error(res.msg || '标记已读失败')
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('标记已读失败')
  }
}

const markAllAsReadApi = async () => {
  try {
    const res = await markAllAsRead()
    if (res.code === 200) {
      messageList.value.forEach(msg => {
        msg.isRead = 1
      })
      ElMessage.success('全部标记已读成功')
    } else {
      ElMessage.error(res.msg || '全部标记已读失败')
    }
  } catch (error) {
    console.error('全部标记已读失败:', error)
    ElMessage.error('全部标记已读失败')
  }
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
