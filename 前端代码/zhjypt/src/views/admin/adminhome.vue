<template>
  <div class="admin-home">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h1>欢迎回来，{{ adminName }}！</h1>
        <p>今天是 {{ currentDate }}，祝您工作愉快</p>
      </div>
      <div class="welcome-icon">
        <i class="el-icon-user-solid"></i>
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <div class="stats-grid">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon student-icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-info">
            <h3>{{ stats.studentCount }}</h3>
            <p>学生总数</p>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon teacher-icon">
            <i class="el-icon-user-solid"></i>
          </div>
          <div class="stat-info">
            <h3>{{ stats.teacherCount }}</h3>
            <p>教师总数</p>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon course-icon">
            <i class="el-icon-reading"></i>
          </div>
          <div class="stat-info">
            <h3>{{ stats.courseCount }}</h3>
            <p>课程总数</p>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon announcement-icon">
            <i class="el-icon-bell"></i>
          </div>
          <div class="stat-info">
            <h3>{{ stats.announcementCount }}</h3>
            <p>公告总数</p>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon comment-icon">
            <i class="el-icon-chat-dot-round"></i>
          </div>
          <div class="stat-info">
            <h3>{{ stats.commentCount }}</h3>
            <p>评论总数</p>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon exam-icon">
            <i class="el-icon-edit"></i>
          </div>
          <div class="stat-info">
            <h3>{{ stats.examCount }}</h3>
            <p>考试总数</p>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 主要功能区域 -->
    <div class="main-content">
      <!-- 快捷操作 -->
      <el-card class="quick-actions" shadow="hover">
        <template #header>
          <h3><i class="el-icon-lightning"></i> 快捷操作</h3>
        </template>
        <div class="action-grid">
          <div class="action-item" @click="goToStudentManager">
            <i class="el-icon-user"></i>
            <span>学生管理</span>
          </div>
          <div class="action-item" @click="goToTeacherManager">
            <i class="el-icon-user-solid"></i>
            <span>教师管理</span>
          </div>
          <div class="action-item" @click="goToCourseManager">
            <i class="el-icon-reading"></i>
            <span>课程管理</span>
          </div>
          <div class="action-item" @click="goToAnnouncementManager">
            <i class="el-icon-bell"></i>
            <span>公告管理</span>
          </div>
        </div>
      </el-card>

      <!-- 最新公告 -->
      <el-card class="recent-announcements" shadow="hover">
        <template #header>
          <h3><i class="el-icon-bell"></i> 最新公告</h3>
        </template>
        <div v-if="recentAnnouncements.length > 0">
          <div 
            v-for="announcement in recentAnnouncements" 
            :key="announcement.announcementId"
            class="announcement-item"
            @click="viewAnnouncement(announcement)">
            <div class="announcement-title">{{ announcement.title }}</div>
            <div class="announcement-time">{{ formatDate(announcement.publishTime) }}</div>
          </div>
        </div>
        <div v-else class="no-data">
          <i class="el-icon-document"></i>
          <p>暂无公告</p>
        </div>
      </el-card>
    </div>

    <!-- 系统信息 -->
    <el-card class="system-info" shadow="hover">
      <template #header>
        <h3><i class="el-icon-info"></i> 系统信息</h3>
      </template>
      <div class="info-grid">
        <div class="info-item">
          <span class="info-label">系统版本：</span>
          <span class="info-value">v1.0.0</span>
        </div>
        <div class="info-item">
          <span class="info-label">最后登录：</span>
          <span class="info-value">{{ lastLoginTime }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">在线时长：</span>
          <span class="info-value">{{ onlineTime }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">系统状态：</span>
          <span class="info-value status-normal">正常运行</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getSessionStorage } from '../../utils/common'
import { getAnnouncementPage } from '../../api/announcement'
import { getadminlist } from '../../api/admin'
import { getteacherlist } from '../../api/teacher'
import { getstudentlist } from '../../api/student'
import { getCoursePage } from '../../api/course'
import { getCourseCommentList } from '../../api/courseComment'
import { getExamPage } from '../../api/exam'

export default {
  name: 'AdminHome',
  data() {
    return {
      adminName: '',
      currentDate: '',
      lastLoginTime: '',
      onlineTime: '0小时0分钟',
      loginStartTime: null,
      stats: {
        studentCount: 0,
        teacherCount: 0,
        courseCount: 0,
        announcementCount: 0,
        commentCount: 0,
        examCount: 0
      },
      recentAnnouncements: []
    }
  },
  created() {
    console.log('AdminHome created')
    this.initData()
    this.loadStats()
    this.loadRecentAnnouncements()
    this.startOnlineTimer()
  },
  beforeUnmount() {
    if (this.onlineTimer) {
      clearInterval(this.onlineTimer)
    }
  },
  methods: {
    initData() {
      const admin = getSessionStorage('curadmin')
      if (admin) {
        this.adminName = admin.admName || '管理员'
      }
      
      this.currentDate = new Date().toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
      })
      
      this.lastLoginTime = new Date().toLocaleString('zh-CN')
      this.loginStartTime = new Date()
    },

    async loadStats() {
      try {
        // 获取学生数量
        const studentRes = await getstudentlist({})
        if (studentRes && studentRes.flag) {
          this.stats.studentCount = studentRes.result?.length || 0
        }

        // 获取教师数量
        const teacherRes = await getteacherlist({})
        if (teacherRes && teacherRes.flag) {
          this.stats.teacherCount = teacherRes.result?.length || 0
        }

        // 获取课程数量
        const courseRes = await getCoursePage({}, 1, 1000)
        if (courseRes && courseRes.flag) {
          this.stats.courseCount = courseRes.total || 0
        }

        // 获取公告数量
        const announcementRes = await getAnnouncementPage({}, 1, 1000)
        if (announcementRes && announcementRes.flag) {
          this.stats.announcementCount = announcementRes.total || 0
        }

        // 获取评论数量
        try {
          const commentRes = await getCourseCommentList({})
          if (commentRes && commentRes.flag) {
            this.stats.commentCount = commentRes.result?.length || 0
          }
        } catch (e) {
          console.log('评论统计暂不可用')
        }

        // 获取考试数量
        try {
          const examRes = await getExamPage({}, 1, 1000)
          if (examRes && examRes.flag) {
            this.stats.examCount = examRes.total || 0
          }
        } catch (e) {
          console.log('考试统计暂不可用')
        }
      } catch (e) {
        console.error('加载统计数据失败', e)
        // 设置默认值，避免页面崩溃
        this.stats = {
          studentCount: 0,
          teacherCount: 0,
          courseCount: 0,
          announcementCount: 0,
          commentCount: 0,
          examCount: 0
        }
      }
    },

    async loadRecentAnnouncements() {
      try {
        const res = await getAnnouncementPage({}, 1, 5)
        if (res && res.flag) {
          this.recentAnnouncements = res.result || []
        }
      } catch (e) {
        console.error('加载公告失败', e)
        this.recentAnnouncements = []
      }
    },

    startOnlineTimer() {
      this.onlineTimer = setInterval(() => {
        if (this.loginStartTime) {
          const now = new Date()
          const diff = now - this.loginStartTime
          const hours = Math.floor(diff / (1000 * 60 * 60))
          const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
          this.onlineTime = `${hours}小时${minutes}分钟`
        }
      }, 60000) // 每分钟更新一次
    },

    formatDate(dateStr) {
      if (!dateStr) return ''
      return new Date(dateStr).toLocaleDateString('zh-CN')
    },

    viewAnnouncement(announcement) {
      // 可以打开公告详情对话框或跳转到公告页面
      this.$router.push('/admin/announcementmanager')
    },

    goToStudentManager() {
      this.$router.push('/admin/studentmanager')
    },

    goToTeacherManager() {
      this.$router.push('/admin/teachermanager')
    },

    goToCourseManager() {
      this.$router.push('/admin/coursemanager')
    },

    goToAnnouncementManager() {
      this.$router.push('/admin/announcementmanager')
    }
  }
}
</script>

<style scoped>
.admin-home {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 12px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-content h1 {
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 600;
}

.welcome-content p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.welcome-icon {
  font-size: 48px;
  opacity: 0.3;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  margin-right: 15px;
}

.student-icon { background: linear-gradient(135deg, #667eea, #764ba2); }
.teacher-icon { background: linear-gradient(135deg, #f093fb, #f5576c); }
.course-icon { background: linear-gradient(135deg, #4facfe, #00f2fe); }
.announcement-icon { background: linear-gradient(135deg, #43e97b, #38f9d7); }
.comment-icon { background: linear-gradient(135deg, #fa709a, #fee140); }
.exam-icon { background: linear-gradient(135deg, #a8edea, #fed6e3); }

.stat-info h3 {
  margin: 0 0 5px 0;
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
}

.stat-info p {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

.main-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.quick-actions .action-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
}

.action-item:hover {
  border-color: #667eea;
  background: #f8f9ff;
  transform: translateY(-2px);
}

.action-item i {
  font-size: 32px;
  color: #667eea;
  margin-bottom: 10px;
}

.action-item span {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
}

.announcement-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.3s;
}

.announcement-item:hover {
  background: #f8f9fa;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-title {
  font-weight: 500;
  color: #2c3e50;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.announcement-time {
  color: #7f8c8d;
  font-size: 12px;
  margin-left: 10px;
}

.no-data {
  text-align: center;
  padding: 40px 0;
  color: #7f8c8d;
}

.no-data i {
  font-size: 48px;
  margin-bottom: 10px;
  display: block;
}

.system-info {
  border-radius: 12px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.info-label {
  color: #7f8c8d;
  font-size: 14px;
}

.info-value {
  color: #2c3e50;
  font-weight: 500;
}

.status-normal {
  color: #27ae60;
}

.el-card {
  border-radius: 12px;
}

.el-card__header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.el-card__header h3 i {
  margin-right: 8px;
  color: #667eea;
}

@media (max-width: 768px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .welcome-banner {
    flex-direction: column;
    text-align: center;
  }
  
  .welcome-icon {
    margin-top: 20px;
  }
}
</style>