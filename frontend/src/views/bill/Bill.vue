<template>
  <div class="bill-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="bill-card">
          <template #header>
            <div class="card-header">
              <span>消费账单</span>
              <el-button type="primary" @click="handleAddBill">新增账单</el-button>
            </div>
          </template>

          <el-table :data="billList" style="width: 100%">
            <el-table-column prop="billName" label="账单名称" />
            <el-table-column prop="actualAmount" label="实际支付" width="120" />
            <el-table-column prop="expectedCycle" label="使用周期(天)" width="120" />
            <el-table-column prop="tags" label="标签" width="150" />
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button size="small" type="danger" @click="deleteBill(scope.row.id)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="report-card">
          <template #header>
            <span>月度复盘报表</span>
          </template>
          <el-form label-width="100px">
            <el-form-item label="年月">
              <el-date-picker
                v-model="reportMonth"
                type="month"
                placeholder="选择年月"
                value-format="YYYY-MM"
              />
            </el-form-item>
          </el-form>
          
          <div class="report-result">
            <div class="report-item">
              <div class="label">本月消费总额</div>
              <div class="value">¥{{ reportData.totalAmount || '0.00' }}</div>
            </div>
            <div class="report-item">
              <div class="label">账单数量</div>
              <div class="value">{{ reportData.billCount || 0 }}</div>
            </div>
            <div class="report-item">
              <div class="label">平均每日摊薄成本</div>
              <div class="value">¥{{ reportData.avgDailyCost || '0.00' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" title="新增账单" width="500px">
      <el-form :model="billForm" label-width="100px">
        <el-form-item label="账单名称">
          <el-input v-model="billForm.billName" />
        </el-form-item>
        <el-form-item label="实际支付金额">
          <el-input-number v-model="billForm.actualAmount" :min="0" :step="0.01" />
        </el-form-item>
        <el-form-item label="预期使用周期">
          <el-input-number v-model="billForm.expectedCycle" :min="0" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="billForm.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="billForm.remarks" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBill">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const billList = ref([
  { id: 1, billName: '双十一购物账单', actualAmount: 250.00, expectedCycle: 30, tags: '家电,促销', createTime: '2026-01-01 10:00:00' }
])

const reportMonth = ref('2026-01')
const reportData = ref({
  totalAmount: 2500.00,
  billCount: 5,
  avgDailyCost: 8.33
})

const dialogVisible = ref(false)
const billForm = reactive({
  billName: '',
  actualAmount: 0,
  expectedCycle: 0,
  tags: '',
  remarks: ''
})

const handleAddBill = () => {
  dialogVisible.value = true
}

const submitBill = () => {
  dialogVisible.value = false
  ElMessage.success('账单添加成功')
}

const deleteBill = (id) => {
  ElMessageBox.confirm('确定要删除此账单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = billList.value.findIndex(b => b.id === id)
    if (index > -1) {
      billList.value.splice(index, 1)
    }
    ElMessage.success('删除成功')
  })
}
</script>

<style scoped>
.bill-container {
  padding: 20px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.report-card {
  height: 100%;
}

.report-result {
  margin-top: 20px;
}

.report-item {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 15px;
}

.report-item .label {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.report-item .value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}
</style>
