<template>
  <div class="pagination-container">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  total: {
    type: Number,
    required: true
  },
  currentPage: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 10
  }
})

const emit = defineEmits(['update:currentPage', 'update:pageSize', 'size-change', 'current-change'])

const currentPage = computed({
  get() {
    return props.currentPage
  },
  set(val) {
    emit('update:currentPage', val)
  }
})

const pageSize = computed({
  get() {
    return props.pageSize
  },
  set(val) {
    emit('update:pageSize', val)
  }
})

const handleSizeChange = (val) => {
  emit('size-change', val)
}

const handleCurrentChange = (val) => {
  emit('current-change', val)
}
</script>

<style scoped>
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
