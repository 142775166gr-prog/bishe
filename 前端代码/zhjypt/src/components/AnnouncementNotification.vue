<template>
  <div>
    <!-- 公告弹窗 -->
    <el-dialog 
      title="系统公告" 
      :visible.sync="dialogVisible" 
      width="60%"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false">
      
      <div v-if="currentAnnouncement">
        <h3 style="margin-bottom: 10px;">{{ currentAnnouncement.title }}</h3>
        <p style="color: #666; margin-bottom: 20px; font-size: 12px;">
          发布人：{{ currentAnnouncement.publisherName }} | 
          发布时间：{{ currentAnnouncement.publishTime }}
        </p>
        <div style="white-space: pre-wrap; line-height: 1.6; max-height: 400px; overflow-y: auto;">
          {{ currentAnnouncement.content }}
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <span style="color: #666; font-size: 12px; float: left; margin-top: 8px;">
          还有 {{ remainingCount }} 条未读公告
        </span>
        <el-button type="success" @click="skipAll">跳过全部</el-button>
        <el-button type="primary" @click="confirmRead">确认已读</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getUnreadAnnouncements, markAnnouncementAsRead } from '../api/announcement'
import { getSessionStorage } from '../utils/common'

export default {
  name: 'AnnouncementNotification',
  data() {
    return {
      dialogVisible: false,
      announcements: [],
      currentIndex: 0,
      currentAnnouncement: null,
      userInfo: null,
      userType: ''
    }
  },
  computed: {
    remainingCount() {
      return this.announcements.length - this.currentIndex - 1
    }
  },
  mounted() {
    this.checkUnreadAnnouncements()
  },
  methods: {
    async checkUnreadAnnouncements() {
      try {
        // 获取用户信息
        const currentAdmin = getSessionStorage('curadmin')
        const currentUser = getSessionStorage('curuser')
        
        if (currentAdmin) {
          this.userInfo = currentAdmin
          this.userType = 'admin'
        } else if (currentUser) {
          this.userInfo = currentUser.info
          this.userType = currentUser.role
        } else {
          return // 未登录
        }

        // 获取未读公告
        const res = await getUnreadAnnouncements(this.getUserId(), this.userType)
        if (res && res.flag && res.result && res.result.length > 0) {
          this.announcements = res.result
          this.currentIndex = 0
          this.showCurrentAnnouncement()
        }
      } catch (e) {
        console.error('获取未读公告失败', e)
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

    showCurrentAnnouncement() {
      if (this.currentIndex < this.announcements.length) {
        this.currentAnnouncement = this.announcements[this.currentIndex]
        this.dialogVisible = true
      } else {
        this.dialogVisible = false
      }
    },

    async confirmRead() {
      try {
        // 标记当前公告为已读
        const res = await markAnnouncementAsRead(
          this.currentAnnouncement.announcementId,
          this.getUserId(),
          this.userType
        )
        
        if (res && res.flag) {
          this.currentIndex++
          if (this.currentIndex < this.announcements.length) {
            // 显示下一条公告
            this.showCurrentAnnouncement()
          } else {
            // 所有公告都已读完
            this.dialogVisible = false
            this.$message.success('所有公告已阅读完毕')
          }
        } else {
          this.$message.error('标记已读失败')
        }
      } catch (e) {
        console.error('标记已读失败', e)
        this.$message.error('标记已读失败')
      }
    },

    async skipAll() {
      try {
        await this.$confirm('确定跳过所有未读公告吗？', '确认', { type: 'warning' })
        
        // 标记所有公告为已读
        const promises = this.announcements.map(announcement => 
          markAnnouncementAsRead(
            announcement.announcementId,
            this.getUserId(),
            this.userType
          )
        )
        
        await Promise.all(promises)
        this.dialogVisible = false
        this.$message.success('已跳过所有公告')
      } catch (e) {
        // 用户取消或出错
        if (e !== 'cancel') {
          console.error('跳过公告失败', e)
          this.$message.error('操作失败')
        }
      }
    }
  }
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>