import request from '@/utils/request'

export function createPost(data) {
  return request({
    url: '/community/post',
    method: 'post',
    data
  })
}

export function getPostList() {
  return request({
    url: '/community/post-list',
    method: 'get'
  })
}

export function getPostDetail(id) {
  return request({
    url: `/community/post-detail/${id}`,
    method: 'get'
  })
}

export function likePostApi(id) {
  return request({
    url: `/community/post-like/${id}`,
    method: 'post'
  })
}

export function collectPostApi(id) {
  return request({
    url: `/community/post-collect/${id}`,
    method: 'post'
  })
}

export function createComment(data) {
  return request({
    url: '/community/comment',
    method: 'post',
    data
  })
}

export function getCommentList(postId) {
  return request({
    url: `/community/comment-list/${postId}`,
    method: 'get'
  })
}

export function auditPost(id, status) {
  return request({
    url: `/community/post-audit/${id}/${status}`,
    method: 'put'
  })
}

export function deletePostApi(id) {
  return request({
    url: `/community/post-delete/${id}`,
    method: 'delete'
  })
}
