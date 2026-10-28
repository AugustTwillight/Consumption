<template>
  <div class="point-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="point-card">
          <template #header>
            <span>我的积分与等级</span>
          </template>
          <div class="point-info">
            <div class="balance">
              <div class="label">积分余额</div>
              <div class="value">{{ userPoints }}</div>
            </div>
            <div class="level">
              <div class="label">当前等级</div>
              <div class="value level-tag">Lv.{{ userLevel }}</div>
            </div>
          </div>
          <div class="level-rules">
            <h4>等级权益规则：</h4>
            <ul>
              <li>Lv.1：初始等级，积分 < 1000</li>
              <li>Lv.2：进阶用户，积分 >= 1000</li>
              <li>Lv.3：资深用户，积分 >= 5000</li>
            </ul>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card class="point-log-card">
          <template #header>
            <span>积分流水记录</span>
          </template>
          <el-table :data="pointLogList" style="width: 100%">
            <el-table-column prop="createTime" label="时间" width="180" />
            <el-table-column prop="pointType" label="类型" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.pointType === 'earn' ? 'success' : 'info'">
                  {{ scope.row.pointType === 'earn' ? '获得' : '消耗' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="pointChange" label="变动积分" width="120">
              <template #default="scope">
                <span :style="{ color: scope.row.pointChange > 0 ? '#67c23a' : '#f56c6c' }">
                  {{ scope.row.pointChange > 0 ? '+' : '' }}{{ scope.row.pointChange }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="sourceType" label="来源" width="120" />
            <el-table-column prop="remark" label="备注" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getPointLogList } from '@/api/point'

const userStore = useUserStore()
const userPoints = ref(userStore.userInfo?.pointBalance || 0)
const userLevel = ref(userStore.userInfo?.level || 1)

const pointLogList = ref([])

const loadPointLogs = async () => {
  try {
    const res = await getPointLogList()
    if (res.code === 200) {
      pointLogList.value = res.data || []
    } else {
      ElMessage.error(res.msg || '获取积分流水失败')
    }
  } catch (error) {
    console.error('获取积分流水失败:', error)
    ElMessage.error('获取积分流水失败')
  }
}

onMounted(() => {
  loadPointLogs()
})
</script>

<style scoped>
.point-container { padding: 20px 0; }
.point-card, .point-log-card { margin-bottom: 20px; }
.point-info { display: flex; justify-content: space-around; text-align: center; margin-bottom: 20px; }
.balance .label, .level .label { font-size: 14px; color: #666; margin-bottom: 10px; }
.balance .value { font-size: 32px; font-weight: bold; color: #e6a23c; }
.level .value { font-size: 24px; font-weight: bold; color: #409eff; }
.level-tag { background: #ecf5ff; padding: 5px 15px; border-radius: 20px; }
.level-rules h4 { margin-bottom: 10px; color: #333; }
.level-rules ul { padding-left: 20px; color: #666; line-height: 1.8; }
</style>
