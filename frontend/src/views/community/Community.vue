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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { createPost, getPostList, likePostApi, collectPostApi } from '@/api/community'

const postList = ref([])

const dialogVisible = ref(false)
const postForm = reactive({
  title: '',
  content: '',
  images: '',
  goodsLink: ''
})

const loadPostList = async () => {
  try {
    const res = await getPostList()
    if (res.code === 200) {
      postList.value = res.data || []
    } else {
      ElMessage.error(res.msg || '获取帖子列表失败')
    }
  } catch (error) {
    console.error('获取帖子列表失败:', error)
    ElMessage.error('获取帖子列表失败')
  }
}

onMounted(() => {
  loadPostList()
})

const handleCreatePost = () => {
  dialogVisible.value = true
}

const submitPost = async () => {
  try {
    const res = await createPost(postForm)
    if (res.code === 200) {
      dialogVisible.value = false
      ElMessage.success('帖子发布成功，等待审核')
      loadPostList()
    } else {
      ElMessage.error(res.msg || '发布失败')
    }
  } catch (error) {
    console.error('发布帖子失败:', error)
    ElMessage.error('发布帖子失败')
  }
}

const likePost = async (id) => {
  try {
    const res = await likePostApi(id)
    if (res.code === 200) {
      ElMessage.success('点赞成功')
      loadPostList()
    } else {
      ElMessage.error(res.msg || '点赞失败')
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('点赞失败')
  }
}

const collectPost = async (id) => {
  try {
    const res = await collectPostApi(id)
    if (res.code === 200) {
      ElMessage.success('收藏成功')
      loadPostList()
    } else {
      ElMessage.error(res.msg || '收藏失败')
    }
  } catch (error) {
    console.error('收藏失败:', error)
    ElMessage.error('收藏失败')
  }
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
