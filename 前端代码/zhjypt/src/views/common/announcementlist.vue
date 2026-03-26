<template>
  <div class="announcement-list">
    <div class="header">
      <h2>系统公告</h2>
      <div class="header-actions">
        <el-button type="primary" size="small" @click="checkUnread" :loading="checkingUnread">
          <i class="el-icon-bell"></i>
          刷新未读状态
        </el-button>
      </div>
    </div>

    <el-table :data="list" border style="width:100%" v-loading="loading">
      <el-table-column prop="title" label="标题" width="250">
        <template #default="{ row }">
          <span :style="{ fontWeight: isUnread(row) ? 'bold' : 'normal', color: isUnread(row) ? '#E6A23C' : '#333' }">
            {{ row.title }}
            <el-tag v-if="isUnread(row)" type="warning" size="small" style="margin-left: 8px;">未读</el-tag>
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="内容摘要" show-overflow-tooltip>
        <template #default="{ row }">
          <span>{{ row.content && row.content.length > 80 ? row.content.substring(0, 80) + '...' : row.content }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="publisherName" label="发布人" width="120"></el-table-column>
      <el-table-column prop="publishTime" label="发布时间" width="180"></el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="onView(row)">查看详情</el-button>
          <el-button v-if="isUnread(row)" type="success" size="small" @click="markAsRead(row)">标记已读</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top:12px; text-align:right">
      <el-pagination
        background
        :current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="sizes, prev, pager, next, jumper, ->, total"
        :page-sizes="[5,10,20,50]"
      />
    </div>

    <!-- 查看对话框 -->
    <el-dialog title="公告详情" v-model="viewDialogVisible" width="60%">
      <div v-if="viewData">
        <h3>{{ viewData.title }}</h3>
        <p style="color: #666; margin: 10px 0;">
          发布人：{{ viewData.publisherName }} | 发布时间：{{ viewData.publishTime }}
        </p>
        <div style="white-space: pre-wrap; line-height: 1.6;">{{ viewData.content }}</div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getAnnouncementPage, getUnreadAnnouncements, markAnnouncementAsRead } from '../../api/announcement'
import { getSessionStorage } from '../../utils/common'

export default {
  data() {
    return {
      list: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      checkingUnread: false,

      viewDialogVisible: false,
      viewData: null,

      userInfo: null,
      userType: '',
      unreadAnnouncementIds: []
    }
  },
  created() {
    this.initUserInfo()
    this.getList()
    this.loadUnreadAnnouncements()
  },
  methods: {
    initUserInfo() {
      const currentAdmin = getSessionStorage('curadmin')
      const currentUser = getSessionStorage('curuser')
      
      if (currentAdmin) {
        this.userInfo = currentAdmin
        this.userType = 'admin'
      } else if (currentUser) {
        this.userInfo = currentUser.info
        this.userType = currentUser.role
      }
    },

    getUserId() {
      if (this.userType === 'admin') {
        return this.userInfo.admId
      } else if (this.userType === 'teacher') {
        return this.userInfo.teachId
      } else if (this.userType === 'student') {
        return this.userInfo.stuId
      }
      return null
    },

    async getList() {
      this.loading = true
      try {
        const res = await getAnnouncementPage({}, this.pageNum, this.pageSize)
        if (res && res.flag) {
          this.list = res.result || []
          this.total = res.total || 0
        } else {
          this.list = []
          this.total = 0
        }
      } catch (e) {
        console.error('获取公告列表失败', e)
        this.list = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },

    handleSizeChange(size) {
      this.pageSize = size
      this.getList()
    },

    handleCurrentChange(page) {
      this.pageNum = page
      this.getList()
    },

    onView(row) {
      console.log('查看公告详情:', row)
      this.viewData = row
      this.viewDialogVisible = true
    },

    async checkUnread() {
      if (!this.userInfo) {
        this.$message.warning('请先登录')
        return
      }

      this.checkingUnread = true
      try {
        const res = await getUnreadAnnouncements(this.getUserId(), this.userType)
        if (res && res.flag) {
          const unreadAnnouncements = res.result || []
          this.unreadAnnouncementIds = unreadAnnouncements.map(item => item.announcementId)
          
          const unreadCount = unreadAnnouncements.length
          if (unreadCount > 0) {
            this.$message.info(`您有 ${unreadCount} 条未读公告，已在列表中标出`)
          } else {
            this.$message.success('暂无未读公告')
          }
        }
      } catch (e) {
        console.error('检查未读公告失败', e)
        this.$message.error('检查失败')
      } finally {
        this.checkingUnread = false
      }
    },

    async loadUnreadAnnouncements() {
      if (!this.userInfo) return
      
      try {
        const res = await getUnreadAnnouncements(this.getUserId(), this.userType)
        if (res && res.flag) {
          const unreadAnnouncements = res.result || []
          this.unreadAnnouncementIds = unreadAnnouncements.map(item => item.announcementId)
        }
      } catch (e) {
        console.error('加载未读公告失败', e)
      }
    },

    isUnread(announcement) {
      return this.unreadAnnouncementIds.includes(announcement.announcementId)
    },

    async markAsRead(announcement) {
      try {
        const res = await markAnnouncementAsRead(
          announcement.announcementId,
          this.getUserId(),
          this.userType
        )
        
        if (res && res.flag) {
          this.unreadAnnouncementIds = this.unreadAnnouncementIds.filter(
            id => id !== announcement.announcementId
          )
          this.$message.success('已标记为已读')
        } else {
          this.$message.error('标记失败')
        }
      } catch (e) {
        console.error('标记已读失败', e)
        this.$message.error('标记失败')
      }
    }
  }
}
</script>

<style scoped>
.announcement-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 10px 16px;
  border-radius: 12px;
  /* 清爽教育风：柔和浅色横幅 */
  background: linear-gradient(90deg, #e0f2fe 0%, #f0f9ff 55%, #fdf2ff 110%);
  border: 1px solid #e5e7eb;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.06);
}

.header h2 {
  margin: 0;
  color: #0f172a;
  font-size: 18px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>