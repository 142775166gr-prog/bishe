<template>
  <div class="teacher-home">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h1>您好，{{ teacherName }} 老师！</h1>
        <p>今天是 {{ currentDate }}，愿您教学愉快</p>
      </div>
      <div class="welcome-icon">
        <i class="el-icon-reading"></i>
      </div>
    </div>

    <!-- 教学数据统计 -->
    <div class="stats-grid">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon course-icon">
            <i class="el-icon-reading"></i>
          </div>
          <div class="stat-info">
            <h3>{{ stats.courseCount }}</h3>
            <p>我的课程</p>
          </div>
        </div>
      </el-card>

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
          <div class="stat-icon published-icon">
            <i class="el-icon-check"></i>
          </div>
          <div class="stat-info">
            <h3>{{ stats.publishedCount }}</h3>
            <p>已发布课程</p>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon draft-icon">
            <i class="el-icon-edit-outline"></i>
          </div>
          <div class="stat-info">
            <h3>{{ stats.draftCount }}</h3>
            <p>草稿课程</p>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover" @click="goToConsultations">
        <div class="stat-content">
          <div class="stat-icon consultation-icon">
            <i class="el-icon-chat-dot-round"></i>
          </div>
          <div class="stat-info">
            <h3>{{ stats.pendingConsultations }}</h3>
            <p>待回复咨询</p>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover" @click="goToGrading">
        <div class="stat-content">
          <div class="stat-icon grading-icon">
            <i class="el-icon-edit"></i>
          </div>
          <div class="stat-info">
            <h3>{{ stats.pendingGrading }}</h3>
            <p>待批改考试</p>
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
          <div class="action-item" @click="createCourse">
            <i class="el-icon-plus"></i>
            <span>创建课程</span>
          </div>
          <div class="action-item" @click="goToCourseManager">
            <i class="el-icon-reading"></i>
            <span>我的课程</span>
          </div>
          <div class="action-item" @click="goToAnnouncements">
            <i class="el-icon-bell"></i>
            <span>系统公告</span>
          </div>
          <div class="action-item" @click="changePassword">
            <i class="el-icon-key"></i>
            <span>修改密码</span>
          </div>
        </div>
      </el-card>

      <!-- 我的课程 -->
      <el-card class="my-courses" shadow="hover">
        <template #header>
          <h3><i class="el-icon-reading"></i> 我的课程</h3>
        </template>
        <div v-if="myCourses.length > 0">
          <div 
            v-for="course in myCourses" 
            :key="course.courseId"
            class="course-item"
            @click="editCourse(course)">
            <div class="course-info">
              <div class="course-title">{{ course.courseTitle }}</div>
              <div class="course-meta">
                <el-tag :type="getStatusType(course.courseStatus)" size="small">
                  {{ getStatusText(course.courseStatus) }}
                </el-tag>
                <span class="course-students">{{ course.studentCount }}人学习</span>
              </div>
            </div>
            <div class="course-actions">
              <el-button 
                v-if="course.courseStatus === 0" 
                type="success" 
                size="small" 
                @click.stop="publishCourse(course)">
                发布
              </el-button>
              <el-button 
                v-if="course.courseStatus === 1" 
                type="warning" 
                size="small" 
                @click.stop="unpublishCourse(course)">
                下架
              </el-button>
            </div>
          </div>
        </div>
        <div v-else class="no-data">
          <i class="el-icon-document"></i>
          <p>还没有创建课程</p>
          <el-button type="primary" @click="createCourse">创建第一门课程</el-button>
        </div>
      </el-card>
    </div>

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

    <!-- 修改密码对话框 -->
    <ChangePassword 
      v-model="passwordDialogVisible" 
      user-type="teacher" 
      :user-id="teacherId"
      @success="handlePasswordChangeSuccess" />
  </div>
</template>

<script>
import { getSessionStorage } from '../../utils/common'
import { getAnnouncementPage } from '../../api/announcement'
import { getTeacherCoursesList, publishCourse, unpublishCourse } from '../../api/course'
import { getTeacherUnrepliedCount } from '../../api/consultation'
import { getPendingGradingCount } from '../../api/examRecord'
import ChangePassword from '../../components/ChangePassword.vue'

export default {
  name: 'TeacherHome',
  components: {
    ChangePassword
  },
  data() {
    return {
      teacherName: '',
      teacherId: null,
      currentDate: '',
      passwordDialogVisible: false,
      stats: {
        courseCount: 0,
        studentCount: 0,
        publishedCount: 0,
        draftCount: 0,
        pendingConsultations: 0,
        pendingGrading: 0
      },
      myCourses: [],
      recentAnnouncements: []
    }
  },
  created() {
    console.log('TeacherHome created')
    this.initData()
    this.loadMyCourses()
    this.loadRecentAnnouncements()
  },
  methods: {
    initData() {
      const user = getSessionStorage('curuser')
      if (user && user.info) {
        this.teacherName = user.info.teachName || '老师'
        this.teacherId = user.info.teachId
      }
      
      this.currentDate = new Date().toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
      })
    },

    async loadMyCourses() {
      if (!this.teacherId) return
      
      try {
        const res = await getTeacherCoursesList(this.teacherId)
        if (res && res.flag) {
          this.myCourses = (res.result || []).slice(0, 5) // 只显示前5门课程
          this.calculateStats()
        }
      } catch (e) {
        console.error('加载课程失败', e)
        this.myCourses = []
        this.calculateStats()
      }
    },

    calculateStats() {
      this.stats.courseCount = this.myCourses.length
      this.stats.publishedCount = this.myCourses.filter(c => c.courseStatus === 1).length
      this.stats.draftCount = this.myCourses.filter(c => c.courseStatus === 0).length
      this.stats.studentCount = this.myCourses.reduce((sum, course) => sum + (course.studentCount || 0), 0)
      
      // 加载待处理事项
      this.loadPendingTasks()
    },

    async loadPendingTasks() {
      if (!this.teacherId) return
      
      try {
        // 获取待回复咨询数量
        const consultationRes = await getTeacherUnrepliedCount(this.teacherId)
        if (consultationRes && consultationRes.flag) {
          this.stats.pendingConsultations = consultationRes.result || 0
        }
      } catch (e) {
        console.log('咨询统计暂不可用')
        this.stats.pendingConsultations = 0
      }

      try {
        // 获取待批改考试数量
        const gradingRes = await getPendingGradingCount(this.teacherId)
        if (gradingRes && gradingRes.flag) {
          this.stats.pendingGrading = gradingRes.result || 0
        }
      } catch (e) {
        console.log('批改统计暂不可用')
        this.stats.pendingGrading = 0
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

    formatDate(dateStr) {
      if (!dateStr) return ''
      return new Date(dateStr).toLocaleDateString('zh-CN')
    },

    getStatusType(status) {
      const types = { 0: 'info', 1: 'success', 2: 'warning' }
      return types[status] || 'info'
    },

    getStatusText(status) {
      const texts = { 0: '草稿', 1: '已发布', 2: '已下架' }
      return texts[status] || '未知'
    },

    createCourse() {
      this.$router.push('/teacher/coursemanager')
      // 可以在路由跳转后触发新增课程对话框
    },

    editCourse(course) {
      this.$router.push('/teacher/coursemanager')
    },

    async publishCourse(course) {
      try {
        const res = await publishCourse(course.courseId, this.teacherId)
        if (res && res.flag) {
          this.$message.success('课程发布成功')
          this.loadMyCourses()
        } else {
          this.$message.error(res.reason || '发布失败')
        }
      } catch (e) {
        this.$message.error('发布失败')
      }
    },

    async unpublishCourse(course) {
      try {
        const res = await unpublishCourse(course.courseId, this.teacherId)
        if (res && res.flag) {
          this.$message.success('课程下架成功')
          this.loadMyCourses()
        } else {
          this.$message.error(res.reason || '下架失败')
        }
      } catch (e) {
        this.$message.error('下架失败')
      }
    },

    goToCourseManager() {
      this.$router.push('/teacher/coursemanager')
    },

    goToAnnouncements() {
      this.$router.push('/common/announcementlist')
    },

    goToConsultations() {
      this.$router.push('/teacher/consultationmanager')
    },

    goToGrading() {
      this.$router.push('/teacher/examgrading')
    },

    changePassword() {
      this.passwordDialogVisible = true
    },

    handlePasswordChangeSuccess() {
      this.$message.success('密码修改成功，请重新登录')
      setTimeout(() => {
        this.$router.push('/login')
      }, 1000)
    },

    viewAnnouncement(announcement) {
      this.$router.push('/common/announcementlist')
    }
  }
}
</script>

<style scoped>
.teacher-home {
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
  cursor: pointer;
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

.course-icon { background: linear-gradient(135deg, #667eea, #764ba2); }
.student-icon { background: linear-gradient(135deg, #f093fb, #f5576c); }
.published-icon { background: linear-gradient(135deg, #43e97b, #38f9d7); }
.draft-icon { background: linear-gradient(135deg, #fa709a, #fee140); }
.consultation-icon { background: linear-gradient(135deg, #4facfe, #00f2fe); }
.grading-icon { background: linear-gradient(135deg, #a8edea, #fed6e3); }

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

.course-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.3s;
}

.course-item:hover {
  background: #f8f9fa;
}

.course-item:last-child {
  border-bottom: none;
}

.course-info {
  flex: 1;
}

.course-title {
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 5px;
}

.course-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.course-students {
  color: #7f8c8d;
  font-size: 12px;
}

.course-actions {
  display: flex;
  gap: 10px;
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