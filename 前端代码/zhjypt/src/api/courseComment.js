import { get, postJson, del } from '../http/request.js'

// 获取课程评论列表
export function getCommentList(courseId) {
  return get(`/courseComment/list/${courseId}`)
}

// 发表评论
export function addComment(data) {
  return postJson('/courseComment/add', data)
}

// 回复评论
export function replyComment(data) {
  return postJson('/courseComment/reply', data)
}

// 删除自己的评论
export function deleteComment(commentId, userId, userType) {
  return del('/courseComment/delete', { commentId, userId, userType })
}

// 管理员删除评论
export function adminDeleteComment(commentId) {
  return del(`/courseComment/adminDelete/${commentId}`)
}

// 获取课程评论数量
export function getCommentCount(courseId) {
  return get(`/courseComment/count/${courseId}`)
}

// 获取用户未读回复数量
export function getUnreadReplyCount(userId, userType) {
  return get('/courseComment/unreadCount', { userId, userType })
}

// 获取用户未读回复列表
export function getUnreadReplies(userId, userType) {
  return get('/courseComment/unreadList', { userId, userType })
}

// 标记回复为已读
export function markAsRead(commentId, userId, userType) {
  return get('/courseComment/markRead', { commentId, userId, userType })
}

// 标记所有回复为已读
export function markAllAsRead(userId, userType) {
  return get('/courseComment/markAllRead', { userId, userType })
}

// 获取所有评论列表（管理员用）
export function getCourseCommentList(params) {
  return get('/courseComment/adminList', params)
}
