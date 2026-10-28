<template>
  <div class="monitor-container">
    <el-card class="monitor-card">
      <template #header>
        <div class="card-header">
          <span>价格监控任务</span>
          <el-button type="primary" @click="handleAddTask">添加监控</el-button>
        </div>
      </template>

      <el-table :data="taskList" style="width: 100%">
        <el-table-column prop="goodsName" label="商品名称" />
        <el-table-column prop="platform" label="平台" width="100" />
        <el-table-column prop="currentPrice" label="当前价格" width="100" />
        <el-table-column prop="targetPrice" label="目标价格" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="scope">
            <el-button size="small" @click="viewPriceRecords(scope.row.id)">
              价格历史
            </el-button>
            <el-button size="small" @click="toggleStatus(scope.row)">
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="deleteTask(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="添加监控任务" width="500px">
      <el-form :model="taskForm" label-width="100px">
        <el-form-item label="商品名称">
          <el-input v-model="taskForm.goodsName" />
        </el-form-item>
        <el-form-item label="商品链接">
          <el-input v-model="taskForm.goodsLink" />
        </el-form-item>
        <el-form-item label="平台">
          <el-select v-model="taskForm.platform">
            <el-option label="淘宝" value="taobao" />
            <el-option label="京东" value="jd" />
            <el-option label="拼多多" value="pdd" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前价格">
          <el-input-number v-model="taskForm.currentPrice" :min="0" :step="0.01" />
        </el-form-item>
        <el-form-item label="目标价格">
          <el-input-number v-model="taskForm.targetPrice" :min="0" :step="0.01" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTask">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="priceRecordsDialogVisible" title="价格历史记录" width="600px">
      <el-table :data="priceRecordsList" style="width: 100%">
        <el-table-column prop="price" label="价格" width="120" />
        <el-table-column prop="recordTime" label="记录时间" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { saveMonitorTask, getMonitorTaskList, toggleMonitorTaskStatus, deleteMonitorTask, getMonitorPriceRecords } from '@/api/monitor'

const taskList = ref([])

const dialogVisible = ref(false)
const taskForm = reactive({
  goodsName: '',
  goodsLink: '',
  platform: 'taobao',
  currentPrice: 0,
  targetPrice: 0
})

const priceRecordsDialogVisible = ref(false)
const priceRecordsList = ref([])
const currentTaskId = ref(null)

const loadTaskList = async () => {
  try {
    const res = await getMonitorTaskList()
    if (res.code === 200) {
      taskList.value = res.data || []
    } else {
      ElMessage.error(res.msg || '获取监控任务列表失败')
    }
  } catch (error) {
    console.error('获取监控任务列表失败:', error)
    ElMessage.error('获取监控任务列表失败')
  }
}

onMounted(() => {
  loadTaskList()
})

const handleAddTask = () => {
  dialogVisible.value = true
}

const submitTask = async () => {
  try {
    const res = await saveMonitorTask(taskForm)
    if (res.code === 200) {
      dialogVisible.value = false
      ElMessage.success('监控任务添加成功')
      loadTaskList()
    } else {
      ElMessage.error(res.msg || '添加失败')
    }
  } catch (error) {
    console.error('添加监控任务失败:', error)
    ElMessage.error('添加监控任务失败')
  }
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  ElMessageBox.confirm(`确定要${newStatus === 1 ? '启用' : '禁用'}此监控任务吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await toggleMonitorTaskStatus(row.id, newStatus)
      if (res.code === 200) {
        row.status = newStatus
        ElMessage.success('操作成功')
      } else {
        ElMessage.error(res.msg || '操作失败')
      }
    } catch (error) {
      console.error('切换状态失败:', error)
      ElMessage.error('切换状态失败')
    }
  })
}

const deleteTask = async (id) => {
  ElMessageBox.confirm('确定要删除此监控任务吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteMonitorTask(id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadTaskList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除监控任务失败:', error)
      ElMessage.error('删除监控任务失败')
    }
  })
}

const viewPriceRecords = async (taskId) => {
  currentTaskId.value = taskId
  try {
    const res = await getMonitorPriceRecords(taskId)
    if (res.code === 200) {
      priceRecordsList.value = res.data || []
      priceRecordsDialogVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取价格历史记录失败')
    }
  } catch (error) {
    console.error('获取价格历史记录失败:', error)
    ElMessage.error('获取价格历史记录失败')
  }
}
</script>

<style scoped>
.monitor-container {
  padding: 20px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
