import { get } from '../http/request.js'

// 分页查询公告列表
export function getAnnouncementPage(params, pageNum, pageSize) {
  return get('/announcement/page', {
    ...params,
    pageNum,
    pageSize
  })
}

// 发布公告
export function publishAnnouncement(params) {
  return get('/announcement/publish', params)
}

// 更新公告
export function updateAnnouncement(params) {
  return get('/announcement/update', params)
}

// 删除公告
export function deleteAnnouncement(id) {
  return get(`/announcement/delete/${id}`)
}

// 获取用户未读公告
export function getUnreadAnnouncements(userId, userType) {
  return get('/announcement/unread', {
    userId,
    userType
  })
}

// 标记公告为已读
export function markAnnouncementAsRead(announcementId, userId, userType) {
  return get('/announcement/markRead', {
    announcementId,
    userId,
    userType
  })
}

// 获取公告详情
export function getAnnouncementDetail(id) {
  return get(`/announcement/detail/${id}`)
}