<template>
  <div class="sync-container">
    <el-card class="sync-card">
      <template #header>
        <span>同步管理</span>
      </template>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="sync-operation-card">
            <template #header>
              <span>数据拉取 (Pull)</span>
            </template>
            <el-form label-width="120px">
              <el-form-item label="表名">
                <el-select v-model="pullTable" placeholder="选择表名">
                  <el-option label="凑单方案" value="calc_plan" />
                  <el-option label="监控任务" value="monitor_task" />
                  <el-option label="消费账单" value="consume_bill" />
                </el-select>
              </el-form-item>
              <el-form-item label="上次水位时间">
                <el-date-picker
                  v-model="lastWaterTime"
                  type="datetime"
                  placeholder="选择时间"
                  value-format="YYYY-MM-DD HH:mm:ss"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handlePull">执行拉取</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>

        <el-col :span="12">
          <el-card class="sync-operation-card">
            <template #header>
              <span>数据推送 (Push)</span>
            </template>
            <el-form label-width="120px">
              <el-form-item label="表名">
                <el-select v-model="pushTable" placeholder="选择表名">
                  <el-option label="凑单方案" value="calc_plan" />
                  <el-option label="监控任务" value="monitor_task" />
                  <el-option label="消费账单" value="consume_bill" />
                </el-select>
              </el-form-item>
              <el-form-item label="本地版本号">
                <el-input-number v-model="localVersion" :min="1" />
              </el-form-item>
              <el-form-item>
                <el-button type="success" @click="handlePush">执行推送</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>

      <el-card class="sync-log-card" style="margin-top: 20px;">
        <template #header>
          <span>同步操作日志</span>
        </template>
        <el-table :data="syncLogList" style="width: 100%">
          <el-table-column prop="syncType" label="同步类型" width="100" />
          <el-table-column prop="tableName" label="表名" width="150" />
          <el-table-column label="冲突状态" width="120">
            <template #default="scope">
              <el-tag :type="scope.row.conflictFlag === 1 ? 'danger' : 'success'">
                {{ scope.row.conflictFlag === 1 ? '有冲突' : '无冲突' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="操作时间" width="180" />
        </el-table>
      </el-card>
    </el-card>

    <el-dialog v-model="conflictDialogVisible" title="数据冲突" width="600px">
      <div class="conflict-content">
        <p>检测到数据冲突，云端版本为 {{ conflictData.cloudDataVersion }}，本地版本为 {{ conflictData.localVersion }}。</p>
        <p>请选择合并策略：</p>
      </div>
      <template #footer>
        <el-button @click="conflictDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConflictResolve('local')">以本地覆盖云端</el-button>
        <el-button type="success" @click="handleConflictResolve('cloud')">以云端覆盖本地</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { pullSyncData, pushSyncData } from '@/api/sync'

const pullTable = ref('calc_plan')
const lastWaterTime = ref('2026-01-01 00:00:00')
const pushTable = ref('calc_plan')
const localVersion = ref(1)

const syncLogList = ref([
  { syncType: 'pull', tableName: 'calc_plan', conflictFlag: 0, createTime: '2026-01-01 10:00:00' },
  { syncType: 'push', tableName: 'monitor_task', conflictFlag: 0, createTime: '2026-01-01 09:00:00' }
])

const conflictDialogVisible = ref(false)
const conflictData = reactive({
  cloudDataVersion: null,
  localVersion: null
})

const handlePull = async () => {
  try {
    const res = await pullSyncData(pullTable.value, lastWaterTime.value)
    if (res.code === 200) {
      ElMessage.success('拉取成功')
      // 实际应更新本地数据和水位时间
    } else {
      ElMessage.error(res.msg || '拉取失败')
    }
  } catch (error) {
    console.error('拉取数据失败:', error)
    ElMessage.error('拉取数据失败')
  }
}

const handlePush = async () => {
  try {
    const res = await pushSyncData({
      tableName: pushTable.value,
      localVersion: localVersion.value,
      clientData: {}
    })
    if (res.code === 200) {
      if (res.data.conflict) {
        conflictData.cloudDataVersion = res.data.cloudDataVersion
        conflictData.localVersion = localVersion.value
        conflictDialogVisible.value = true
      } else {
        ElMessage.success('推送成功')
        localVersion.value = res.data.newDataVersion || localVersion.value + 1
      }
    } else {
      ElMessage.error(res.msg || '推送失败')
    }
  } catch (error) {
    console.error('推送数据失败:', error)
    ElMessage.error('推送数据失败')
  }
}

const handleConflictResolve = (strategy) => {
  conflictDialogVisible.value = false
  if (strategy === 'local') {
    ElMessage.success('已以本地数据覆盖云端')
    // 实际应执行本地数据推送到云端的逻辑
  } else {
    ElMessage.success('已以云端数据覆盖本地')
    // 实际应执行云端数据拉取到本地的逻辑
  }
}
</script>

<style scoped>
.sync-container {
  padding: 20px 0;
}

.sync-operation-card {
  margin-bottom: 20px;
}

.sync-log-card {
  margin-top: 20px;
}

.conflict-content {
  padding: 20px 0;
  line-height: 1.6;
  color: #666;
}
</style>
