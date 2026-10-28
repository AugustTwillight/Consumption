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
        <el-table-column label="操作" width="200">
          <template #default="scope">
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
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const taskList = ref([
  { id: 1, goodsName: '商品A', platform: 'taobao', currentPrice: 150.00, targetPrice: 120.00, status: 1 }
])

const dialogVisible = ref(false)
const taskForm = reactive({
  goodsName: '',
  goodsLink: '',
  platform: 'taobao',
  currentPrice: 0,
  targetPrice: 0
})

const handleAddTask = () => {
  dialogVisible.value = true
}

const submitTask = () => {
  dialogVisible.value = false
  ElMessage.success('监控任务添加成功')
}

const toggleStatus = (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  ElMessageBox.confirm(`确定要${newStatus === 1 ? '启用' : '禁用'}此监控任务吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    row.status = newStatus
    ElMessage.success('操作成功')
  })
}

const deleteTask = (id) => {
  ElMessageBox.confirm('确定要删除此监控任务吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = taskList.value.findIndex(t => t.id === id)
    if (index > -1) {
      taskList.value.splice(index, 1)
    }
    ElMessage.success('删除成功')
  })
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
