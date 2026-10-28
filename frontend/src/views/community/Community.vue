<template>
  <div class="community-container">
    <el-card class="community-card">
      <template #header>
        <div class="card-header">
          <span>社区分享</span>
          <el-button type="primary" @click="handleCreatePost">发布帖子</el-button>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="16" v-for="post in postList" :key="post.id" class="post-item">
          <el-card shadow="hover">
            <div class="post-header">
              <h3>{{ post.title }}</h3>
              <span class="post-meta">作者: 用户{{ post.userId }} | {{ post.createTime }}</span>
            </div>
            <div class="post-content">
              <p>{{ post.content }}</p>
            </div>
            <div class="post-actions">
              <el-button size="small" @click="likePost(post.id)">
                <el-icon><Star /></el-icon>
                点赞 ({{ post.likeCount }})
              </el-button>
              <el-button size="small" @click="collectPost(post.id)">
                <el-icon><Collection /></el-icon>
                收藏 ({{ post.collectCount }})
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog v-model="dialogVisible" title="发布帖子" width="600px">
      <el-form :model="postForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="postForm.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="postForm.content" type="textarea" :rows="5" />
        </el-form-item>
        <el-form-item label="图片链接">
          <el-input v-model="postForm.images" placeholder="多个链接用逗号分隔" />
        </el-form-item>
        <el-form-item label="商品链接">
          <el-input v-model="postForm.goodsLink" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPost">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const postList = ref([
  { id: 1, title: '双十一捡漏攻略', content: '分享一些双十一凑单的好方法...', userId: 1, likeCount: 15, collectCount: 8, createTime: '2026-01-01 10:00:00' }
])

const dialogVisible = ref(false)
const postForm = reactive({
  title: '',
  content: '',
  images: '',
  goodsLink: ''
})

const handleCreatePost = () => {
  dialogVisible.value = true
}

const submitPost = () => {
  dialogVisible.value = false
  ElMessage.success('帖子发布成功，等待审核')
}

const likePost = (id) => {
  ElMessage.success('点赞成功')
}

const collectPost = (id) => {
  ElMessage.success('收藏成功')
}
</script>

<style scoped>
.community-container {
  padding: 20px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-item {
  margin-bottom: 20px;
}

.post-header {
  margin-bottom: 15px;
}

.post-header h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.post-meta {
  font-size: 12px;
  color: #999;
}

.post-content {
  margin-bottom: 15px;
  color: #666;
  line-height: 1.6;
}

.post-actions {
  display: flex;
  gap: 10px;
}
</style>
