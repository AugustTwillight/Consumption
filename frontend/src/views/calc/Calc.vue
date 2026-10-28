<template>
  <div class="calc-container">
    <el-card class="calc-card">
      <template #header>
        <div class="card-header">
          <span>AI凑单计算器</span>
          <el-button type="primary" @click="handleCalculate">执行计算</el-button>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="section-card">
            <template #header>
              <span>商品列表</span>
            </template>
            <el-button type="success" size="small" @click="addGoods" style="margin-bottom: 10px;">
              添加商品
            </el-button>
            <el-table :data="goodsList" style="width: 100%">
              <el-table-column prop="goodsName" label="商品名称" />
              <el-table-column prop="platform" label="平台" width="80" />
              <el-table-column prop="unitPrice" label="单价" width="100" />
              <el-table-column prop="quantity" label="数量" width="80" />
              <el-table-column label="操作" width="100">
                <template #default="scope">
                  <el-button size="small" type="danger" @click="removeGoods(scope.$index)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>

        <el-col :span="12">
          <el-card class="section-card">
            <template #header>
              <span>优惠规则</span>
            </template>
            <el-button type="success" size="small" @click="addRule" style="margin-bottom: 10px;">
              添加规则
            </el-button>
            <el-table :data="ruleList" style="width: 100%">
              <el-table-column prop="ruleType" label="规则类型" width="120" />
              <el-table-column prop="ruleCondition" label="门槛" width="100" />
              <el-table-column prop="ruleDiscount" label="优惠" width="100" />
              <el-table-column label="操作" width="100">
                <template #default="scope">
                  <el-button size="small" type="danger" @click="removeRule(scope.$index)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>

      <el-card class="result-card" v-if="calcResult">
        <template #header>
          <span>计算结果</span>
        </template>
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="result-item">
              <div class="label">商品总金额</div>
              <div class="value">¥{{ calcResult.totalAmount }}</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="result-item">
              <div class="label">总优惠金额</div>
              <div class="value discount">¥{{ calcResult.totalDiscount }}</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="result-item">
              <div class="label">实付总金额</div>
              <div class="value final">¥{{ calcResult.finalAmount }}</div>
            </div>
          </el-col>
        </el-row>
        <div v-if="calcResult.riskTags && calcResult.riskTags.length > 0" class="risk-tags">
          <el-tag v-for="tag in calcResult.riskTags" :key="tag" type="warning" style="margin-right: 10px;">
            {{ tag }}
          </el-tag>
        </div>
        <el-button type="primary" style="margin-top: 20px;" @click="savePlan">保存方案</el-button>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const goodsList = ref([
  { goodsName: '商品A', platform: 'taobao', unitPrice: 99.00, quantity: 1 }
])

const ruleList = ref([
  { ruleType: 'full_reduce', ruleCondition: 200.00, ruleDiscount: 30.00, isStackable: 1 }
])

const calcResult = ref(null)

const addGoods = () => {
  goodsList.value.push({
    goodsName: '新商品',
    platform: 'taobao',
    unitPrice: 0.00,
    quantity: 1
  })
}

const removeGoods = (index) => {
  goodsList.value.splice(index, 1)
}

const addRule = () => {
  ruleList.value.push({
    ruleType: 'full_reduce',
    ruleCondition: 0.00,
    ruleDiscount: 0.00,
    isStackable: 1
  })
}

const removeRule = (index) => {
  ruleList.value.splice(index, 1)
}

const handleCalculate = () => {
  // 简化处理，实际应调用后端API
  calcResult.value = {
    totalAmount: 99.00,
    totalDiscount: 0.00,
    finalAmount: 99.00,
    riskTags: []
  }
  ElMessage.success('计算完成')
}

const savePlan = () => {
  ElMessage.success('方案保存成功')
}
</script>

<style scoped>
.calc-container {
  padding: 20px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-card {
  margin-bottom: 20px;
}

.result-card {
  margin-top: 20px;
  background: #f0f9ff;
}

.result-item {
  text-align: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
}

.label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.value.discount {
  color: #67c23a;
}

.value.final {
  color: #f56c6c;
}

.risk-tags {
  margin-top: 20px;
}
</style>
