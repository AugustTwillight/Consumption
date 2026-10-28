<template>
  <div class="calc-plan-container">
    <el-row :gutter="20">
      <!-- 左侧：商品与规则配置 -->
      <el-col :span="12">
        <el-card class="config-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>🛒 商品与规则配置</span>
              <div class="header-actions">
                <el-button type="success" size="small" @click="showGoodsDialog = true">
                  <el-icon><Plus /></el-icon> 添加商品
                </el-button>
                <el-button type="success" size="small" @click="showRuleDialog = true" style="margin-left: 10px;">
                  <el-icon><Plus /></el-icon> 添加规则
                </el-button>
              </div>
            </div>
          </template>

          <el-tabs v-model="activeTab">
            <el-tab-pane label="商品列表" name="goods">
              <el-table :data="goodsList" style="width: 100%" empty-text="暂无商品数据">
                <el-table-column prop="goodsName" label="商品名称" show-overflow-tooltip />
                <el-table-column prop="platform" label="平台" width="80">
                  <template #default="scope">
                    <el-tag size="small">{{ scope.row.platform || '未知' }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="unitPrice" label="单价" width="100">
                  <template #default="scope">
                    ¥{{ scope.row.unitPrice }}
                  </template>
                </el-table-column>
                <el-table-column prop="quantity" label="数量" width="80" />
                <el-table-column label="操作" width="100">
                  <template #default="scope">
                    <el-button size="small" type="danger" @click="removeGoods(scope.$index)">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="优惠规则" name="rules">
              <el-table :data="ruleList" style="width: 100%" empty-text="暂无规则数据">
                <el-table-column prop="ruleType" label="规则类型" width="120" />
                <el-table-column prop="ruleCondition" label="满减门槛" width="120">
                  <template #default="scope">
                    满 ¥{{ scope.row.ruleCondition }}
                  </template>
                </el-table-column>
                <el-table-column prop="ruleDiscount" label="优惠金额" width="120">
                  <template #default="scope">
                    减 ¥{{ scope.row.ruleDiscount }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                  <template #default="scope">
                    <el-button size="small" type="danger" @click="removeRule(scope.$index)">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>

      <!-- 右侧：计算结果 -->
      <el-col :span="12">
        <el-card class="result-card" shadow="hover" v-if="calcResult">
          <template #header>
            <div class="card-header">
              <span>📊 计算结果</span>
              <el-button type="primary" @click="handleCalculate">
                <el-icon><Refresh /></el-icon> 重新计算
              </el-button>
            </div>
          </template>

          <el-row :gutter="20" class="result-stats">
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-label">商品总金额</div>
                <div class="stat-value">¥{{ calcResult.totalAmount }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item discount">
                <div class="stat-label">总优惠金额</div>
                <div class="stat-value">-¥{{ calcResult.totalDiscount }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item final">
                <div class="stat-label">实付总金额</div>
                <div class="stat-value">¥{{ calcResult.finalAmount }}</div>
              </div>
            </el-col>
          </el-row>

          <div v-if="calcResult.riskTags && calcResult.riskTags.length > 0" class="risk-tags">
            <el-tag v-for="tag in calcResult.riskTags" :key="tag" type="warning" effect="dark" style="margin-right: 8px; margin-bottom: 8px;">
              ⚠️ {{ tag }}
            </el-tag>
          </div>

          <div class="scenario-list" v-if="calcResult.scenarios && calcResult.scenarios.length > 0">
            <div class="scenario-title">推荐方案：</div>
            <el-collapse v-model="activeScenario">
              <el-collapse-item title="最优凑单方案" name="1">
                <div class="scenario-detail">
                  <div v-for="(allocation, index) in calcResult.scenarios[0].goodsAllocations" :key="index" class="allocation-item">
                    <span class="goods-name">{{ allocation.goodsName }}</span>
                    <span class="goods-price">单价: ¥{{ allocation.unitPrice }}</span>
                    <span class="goods-actual">实付: ¥{{ allocation.actualPaid }}</span>
                  </div>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>

          <el-button type="success" size="large" style="margin-top: 20px; width: 100%;" @click="savePlan">
            <el-icon><Check /></el-icon> 保存方案
          </el-button>
        </el-card>

        <el-card class="empty-result-card" shadow="hover" v-else>
          <div class="empty-state">
            <el-icon class="empty-icon"><DataAnalysis /></el-icon>
            <div class="empty-text">暂无计算结果</div>
            <div class="empty-desc">请在左侧添加商品和规则，然后点击"执行计算"</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加商品对话框 -->
    <el-dialog v-model="showGoodsDialog" title="添加商品" width="500px">
      <el-form :model="goodsForm" label-width="80px">
        <el-form-item label="商品名称">
          <el-input v-model="goodsForm.goodsName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="平台">
          <el-select v-model="goodsForm.platform" placeholder="请选择平台">
            <el-option label="淘宝" value="淘宝" />
            <el-option label="京东" value="京东" />
            <el-option label="拼多多" value="拼多多" />
            <el-option label="天猫" value="天猫" />
          </el-select>
        </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="goodsForm.unitPrice" :min="0" :step="0.01" style="width: 100%" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="goodsForm.quantity" :min="1" :step="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showGoodsDialog = false">取消</el-button>
        <el-button type="primary" @click="addGoods">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加规则对话框 -->
    <el-dialog v-model="showRuleDialog" title="添加优惠规则" width="500px">
      <el-form :model="ruleForm" label-width="100px">
        <el-form-item label="规则类型">
          <el-select v-model="ruleForm.ruleType" placeholder="请选择规则类型">
            <el-option label="跨店满减" value="cross_store_discount" />
            <el-option label="店铺优惠券" value="shop_coupon" />
            <el-option label="平台补贴" value="platform_subsidy" />
          </el-select>
        </el-form-item>
        <el-form-item label="满减门槛">
          <el-input-number v-model="ruleForm.ruleCondition" :min="0" :step="0.01" style="width: 100%" />
        </el-form-item>
        <el-form-item label="优惠金额">
          <el-input-number v-model="ruleForm.ruleDiscount" :min="0" :step="0.01" style="width: 100%" />
        </el-form-item>
        <el-form-item label="是否可叠加">
          <el-switch v-model="ruleForm.isStackable" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRuleDialog = false">取消</el-button>
        <el-button type="primary" @click="addRule">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { calculateCalcPlan, saveCalcPlan } from '@/api/calcPlan'
import { Plus, Delete, Refresh, Check, DataAnalysis } from '@element-plus/icons-vue'

const activeTab = ref('goods')
const calcResult = ref(null)
const activeScenario = ref('1')

const goodsList = ref([
  { goodsName: '示例商品1', platform: '淘宝', unitPrice: 99.00, quantity: 1 }
])

const ruleList = ref([
  { ruleType: 'cross_store_discount', ruleCondition: 300, ruleDiscount: 50, isStackable: 1 }
])

const showGoodsDialog = ref(false)
const showRuleDialog = ref(false)

const goodsForm = reactive({
  goodsName: '',
  platform: '淘宝',
  unitPrice: 0,
  quantity: 1
})

const ruleForm = reactive({
  ruleType: 'cross_store_discount',
  ruleCondition: 300,
  ruleDiscount: 50,
  isStackable: 1
})

const addGoods = () => {
  if (!goodsForm.goodsName) {
    ElMessage.warning('请输入商品名称')
    return
  }
  goodsList.value.push({ ...goodsForm })
  goodsForm.goodsName = ''
  goodsForm.unitPrice = 0
  goodsForm.quantity = 1
  showGoodsDialog.value = false
}

const removeGoods = (index) => {
  goodsList.value.splice(index, 1)
}

const addRule = () => {
  ruleList.value.push({ ...ruleForm })
  showRuleDialog.value = false
}

const removeRule = (index) => {
  ruleList.value.splice(index, 1)
}

const handleCalculate = async () => {
  if (goodsList.value.length === 0) {
    ElMessage.warning('请先添加商品')
    return
  }

  try {
    const res = await calculateCalcPlan({
      planName: '临时计算方案',
      goodsList: goodsList.value,
      ruleList: ruleList.value
    })

    if (res.code === 200) {
      calcResult.value = res.data
      ElMessage.success('计算完成')
    } else {
      ElMessage.error(res.msg || '计算失败')
    }
  } catch (error) {
    console.error('计算失败:', error)
    ElMessage.error('计算失败，请检查商品和规则数据')
  }
}

const savePlan = async () => {
  if (!calcResult.value) {
    ElMessage.warning('请先执行计算')
    return
  }

  try {
    const res = await saveCalcPlan({
      planName: 'AI凑单方案_' + new Date().getTime(),
      goodsList: goodsList.value,
      ruleList: ruleList.value,
      totalAmount: calcResult.value.totalAmount,
      totalDiscount: calcResult.value.totalDiscount,
      finalAmount: calcResult.value.finalAmount,
      riskTags: calcResult.value.riskTags ? calcResult.value.riskTags.join(',') : ''
    })

    if (res.code === 200) {
      ElMessage.success('方案保存成功')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存方案失败:', error)
    ElMessage.error('保存方案失败')
  }
}
</script>

<style scoped>
.calc-plan-container {
  padding: 0;
}

.config-card, .result-card, .empty-result-card {
  height: 100%;
  min-height: 600px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
  font-size: 16px;
}

.header-actions {
  display: flex;
  align-items: center;
}

.result-stats {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
}

.stat-item.discount .stat-value {
  color: #67C23A;
}

.stat-item.final .stat-value {
  color: #F56C6C;
}

.risk-tags {
  margin: 15px 0;
  padding: 10px;
  background-color: #fdf6ec;
  border-radius: 4px;
}

.scenario-list {
  margin: 20px 0;
}

.scenario-title {
  font-weight: 500;
  margin-bottom: 10px;
  color: #303133;
}

.allocation-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
  font-size: 14px;
}

.allocation-item:last-child {
  border-bottom: none;
}

.goods-name {
  flex: 1;
  color: #606266;
}

.goods-price {
  color: #909399;
  margin-right: 20px;
}

.goods-actual {
  color: #F56C6C;
  font-weight: 500;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #909399;
}

.empty-icon {
  font-size: 64px;
  color: #dcdfe6;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
}

.empty-desc {
  font-size: 14px;
  color: #c0c4cc;
}
</style>
