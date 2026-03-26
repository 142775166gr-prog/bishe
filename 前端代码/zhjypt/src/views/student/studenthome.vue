<template>
  <div class="student-home">
    <!-- 顶部欢迎与概览 -->
    <div class="home-header">
      <div class="welcome-block">
        <div class="welcome-main">
          <p class="welcome-badge">学生端 · 学习工作台</p>
          <h1>欢迎回来，{{ studentName || '同学' }}</h1>
          <p class="welcome-sub">
            今天是 {{ currentDate }}，坚持每一天的小进步，都会在未来变成大收获。
          </p>
          <div class="welcome-actions">
            <el-button type="primary" round @click="goToCourseCenter">
              继续学习
            </el-button>
          </div>
        </div>
        <div class="welcome-side">
          <div class="today-stat">
            <span class="label">已选课程</span>
            <span class="value">{{ stats.enrolledCount }}</span>
          </div>
          <div class="today-stat">
            <span class="label">平均进度</span>
            <span class="value">{{ stats.averageProgress }}%</span>
        </div>
        </div>
      </div>

    </div>

    <!-- 中部主区域：左侧学习进度 + 右侧待办/快捷入口 -->
    <div class="home-main">
      <div class="main-left">
        <!-- 学习进度与课程 -->
        <el-card shadow="hover" class="panel">
          <template #header>
            <div class="panel-header">
              <h3><i class="el-icon-data-line"></i> 我的学习进度</h3>
              <el-button type="text" size="small" @click="goToMyCourses">查看全部课程</el-button>
            </div>
          </template>

          <div v-if="learningProgress.length > 0">
            <div
              v-for="course in learningProgress"
              :key="course.id"
              class="course-progress-row"
            >
              <div class="course-progress-main">
                <div class="course-name">{{ course.courseTitle }}</div>
                <div class="course-meta-line">
                  <span>教师：{{ course.teacherName || '未知教师' }}</span>
                  <span v-if="course.enrollTime">选课时间：{{ formatDate(course.enrollTime) }}</span>
                </div>
                <div class="progress-line">
                  <el-progress
                    :percentage="Math.round(course.progress || 0)"
                    :stroke-width="6"
                    :show-text="false"
                  />
                  <span class="percent-text">{{ Math.round(course.progress || 0) }}%</span>
                </div>
              </div>
              <div class="course-progress-actions">
                <el-button
                  type="primary"
                  size="small"
                  @click="continueLearning(course)"
                >
                  {{ course.progress > 0 ? '继续学习' : '开始学习' }}
                </el-button>
              </div>
            </div>
          </div>
          <div v-else class="panel-empty">
            <i class="el-icon-document"></i>
            <p>你还没有选择任何课程</p>
            <el-button type="primary" @click="goToCourseCenter">去课程中心选课</el-button>
          </div>
        </el-card>

        <!-- 推荐课程 -->
        <el-card shadow="hover" class="panel">
          <template #header>
            <div class="panel-header">
              <h3><i class="el-icon-star-on"></i> 推荐课程</h3>
            </div>
          </template>
          <div v-if="recommendedCourses.length > 0" class="course-grid">
            <div
              v-for="course in recommendedCourses"
              :key="course.courseId"
              class="course-card"
              @click="viewCourse(course)"
            >
              <div class="course-cover">
                <img
                  :src="formatImageUrl(course.courseCover)"
                  :alt="course.courseTitle"
                  referrerpolicy="no-referrer"
                  @error="handleImageError"
                />
              </div>
              <div class="course-info">
                <h4>{{ course.courseTitle }}</h4>
                <p class="course-teacher">{{ course.teacherName }}</p>
                <div class="course-meta">
                  <span class="course-students">{{ course.studentCount }}人学习</span>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="panel-empty small">
            <i class="el-icon-document"></i>
            <p>暂无推荐课程</p>
          </div>
        </el-card>
      </div>

      <div class="main-right">
        <!-- 待办事项 / 快捷入口 -->
        <el-card shadow="hover" class="panel">
          <template #header>
            <div class="panel-header">
              <h3><i class="el-icon-s-operation"></i> 学习快捷入口</h3>
            </div>
          </template>
          <div class="quick-grid">
            <div class="quick-item" @click="goToCourseCenter">
              <div class="quick-icon primary"><i class="el-icon-search"></i></div>
              <div class="quick-text">
                <p class="title">课程中心</p>
                <p class="desc">浏览全部课程并选课</p>
              </div>
            </div>
            <div class="quick-item" @click="goToMyCourses">
              <div class="quick-icon success"><i class="el-icon-reading"></i></div>
              <div class="quick-text">
                <p class="title">我的课程</p>
                <p class="desc">查看已选课程与进度</p>
              </div>
            </div>
            <div class="quick-item" @click="goToAnnouncements">
              <div class="quick-icon info"><i class="el-icon-bell"></i></div>
              <div class="quick-text">
                <p class="title">系统公告</p>
                <p class="desc">查看最新系统通知</p>
              </div>
            </div>
            <div class="quick-item" @click="goToSuggestions">
              <div class="quick-icon suggest"><i class="el-icon-message"></i></div>
              <div class="quick-text">
                <p class="title">教育建议</p>
                <p class="desc">查看老师给你的学习建议</p>
              </div>
            </div>
            <div class="quick-item" @click="changePassword">
              <div class="quick-icon muted"><i class="el-icon-key"></i></div>
              <div class="quick-text">
                <p class="title">修改密码</p>
                <p class="desc">提升账号安全性</p>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 最新公告 -->
        <el-card shadow="hover" class="panel">
          <template #header>
            <div class="panel-header">
              <h3><i class="el-icon-bell"></i> 最新公告</h3>
              <el-button type="text" size="small" @click="goToAnnouncements">全部公告</el-button>
            </div>
          </template>
          <div v-if="recentAnnouncements.length > 0">
            <div
              v-for="announcement in recentAnnouncements"
              :key="announcement.announcementId"
              class="announcement-item"
              @click="viewAnnouncement(announcement)"
            >
              <div class="announcement-title">{{ announcement.title }}</div>
              <div class="announcement-time">{{ formatDate(announcement.publishTime) }}</div>
            </div>
          </div>
          <div v-else class="panel-empty small">
            <i class="el-icon-document"></i>
            <p>暂无公告</p>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 修改密码对话框 -->
    <ChangePassword
      v-model="passwordDialogVisible"
      user-type="student"
      :user-id="studentId"
      @success="handlePasswordChangeSuccess"
    />
  </div>
</template>

<script>
import { getSessionStorage } from '../../utils/common'
import { getAnnouncementPage } from '../../api/announcement'
import { getPublishedCoursePage } from '../../api/course'
import { getStudentCourses } from '../../api/studentCourse'
import { getStudentUnreadSuggestionCount } from '../../api/suggestion'
import { getPendingExamCount } from '../../api/examRecord'
import ChangePassword from '../../components/ChangePassword.vue'

export default {
  name: 'StudentHome',
  components: {
    ChangePassword
  },
  data() {
    return {
      studentName: '',
      studentId: null,
      currentDate: '',
      passwordDialogVisible: false,
      stats: {
        enrolledCount: 0,
        averageProgress: 0,
        completedCount: 0,
        studyDays: 0,
        unreadSuggestions: 0,
        pendingExams: 0
      },
      // 全量课程数据（用于统计）
      allCourses: [],
      // 首页只展示一部分课程进度
      learningProgress: [],
      recommendedCourses: [],
      recentAnnouncements: []
    }
  },
  created() {
    console.log('StudentHome created')
    this.initData()
    this.loadLearningProgress()
    this.loadRecommendedCourses()
    this.loadRecentAnnouncements()
  },
  methods: {
    initData() {
      const user = getSessionStorage('curuser')
      if (user && user.info) {
        this.studentName = user.info.stuName || '同学'
        this.studentId = user.info.stuId
      }
      
      this.currentDate = new Date().toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
      })
    },

    async loadLearningProgress() {
      if (!this.studentId) return
      
      try {
        const res = await getStudentCourses(this.studentId)
        if (res && res.flag) {
          const courses = res.result || []
          // 保存全量课程
          this.allCourses = courses
          // 按进度从高到低排序，取前 2 门
          const sorted = [...courses].sort((a, b) => (b.progress || 0) - (a.progress || 0))
          this.learningProgress = sorted.slice(0, 2)
          this.calculateStats()
        }
      } catch (e) {
        console.error('加载学习进度失败', e)
        this.allCourses = []
        this.learningProgress = []
        this.calculateStats()
      }
    },

    calculateStats() {
      // 统计使用全量课程，避免与“我的课程”数量不一致
      this.stats.enrolledCount = this.allCourses.length
      
      if (this.allCourses.length > 0) {
        const totalProgress = this.allCourses.reduce((sum, course) => sum + (course.progress || 0), 0)
        this.stats.averageProgress = Math.round(totalProgress / this.allCourses.length)
        this.stats.completedCount = this.allCourses.filter(c => (c.progress || 0) >= 100).length
        
        // 计算学习天数（简单模拟：最早选课日期到今天）
        const enrollDates = this.allCourses
          .map(c => c.enrollTime ? new Date(c.enrollTime) : null)
          .filter(d => d && !isNaN(d.getTime()))
        if (enrollDates.length > 0) {
          const earliestDate = new Date(Math.min(...enrollDates.map(d => d.getTime())))
          const daysDiff = Math.ceil((new Date() - earliestDate) / (1000 * 60 * 60 * 24))
          this.stats.studyDays = daysDiff > 0 ? daysDiff : 0
        } else {
          this.stats.studyDays = 0
        }
      } else {
        this.stats.averageProgress = 0
        this.stats.completedCount = 0
        this.stats.studyDays = 0
      }
      
      // 加载待处理事项
      this.loadPendingTasks()
    },

    async loadPendingTasks() {
      if (!this.studentId) return
      
      try {
        // 获取未读建议数量
        const suggestionRes = await getStudentUnreadSuggestionCount(this.studentId)
        if (suggestionRes && suggestionRes.flag) {
          this.stats.unreadSuggestions = suggestionRes.result || 0
        }
      } catch (e) {
        console.log('建议统计暂不可用')
        this.stats.unreadSuggestions = 0
      }

      try {
        // 获取待完成考试数量
        const examRes = await getPendingExamCount(this.studentId)
        if (examRes && examRes.flag) {
          this.stats.pendingExams = examRes.result || 0
        }
      } catch (e) {
        console.log('考试统计暂不可用')
        this.stats.pendingExams = 0
      }
    },

    async loadRecommendedCourses() {
      try {
        const res = await getPublishedCoursePage({}, 1, 6)
        if (res && res.flag) {
          this.recommendedCourses = res.result || []
        }
      } catch (e) {
        console.error('加载推荐课程失败', e)
        this.recommendedCourses = []
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

    generatePlaceholderImage(text) {
      const svg = `
        <svg width="300" height="200" xmlns="http://www.w3.org/2000/svg">
          <rect width="300" height="200" fill="#f3f4f6"/>
          <text x="150" y="100" font-family="Arial, sans-serif" font-size="16" fill="#9ca3af" text-anchor="middle" dy="0.3em">${text}</text>
        </svg>
      `
      return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svg)))
    },

    formatImageUrl(url) {
      const raw = (url === null || url === undefined) ? '' : String(url)
      let u = raw.trim().replace(/^['"]|['"]$/g, '')

      if (!u || u === 'null' || u === 'undefined') {
        return this.generatePlaceholderImage('暂无封面')
      }

      if (u.startsWith('http://') || u.startsWith('https://')) {
        return u
      }

      if (u.startsWith('//')) {
        return 'https:' + u
      }

      if (u.startsWith('/')) {
        return u
      }

      if (u.includes('.') && !u.startsWith('//')) {
        return 'https://' + u
      }

      return this.generatePlaceholderImage('暂无封面')
    },

    handleImageError(event) {
      const img = event && event.target ? event.target : null
      const src = img ? (img.currentSrc || img.src || '') : ''

      // 外链图片做一次轻量重试（加时间戳避免缓存/边缘节点偶发失败）
      if (img && src && (src.startsWith('http://') || src.startsWith('https://')) && !src.includes('__retry=')) {
        const joiner = src.includes('?') ? '&' : '?'
        img.src = `${src}${joiner}__retry=${Date.now()}`
        return
      }

      if (img) img.src = this.generatePlaceholderImage('图片加载失败')
    },

    continueLearning(course) {
      // 从首页快捷入口跳转到课程学习页面，携带课程ID
      if (!course || !course.courseId) {
        this.$message.error('无法识别课程信息')
        return
      }
      this.$router.push({
        path: '/student/courselearn',
        query: { courseId: course.courseId }
      })
    },

    viewCourse(course) {
      this.$router.push('/student/courselist')
    },

    goToCourseCenter() {
      this.$router.push('/student/courselist')
    },

    goToMyCourses() {
      this.$router.push('/student/mycourses')
    },

    goToAnnouncements() {
      this.$router.push('/common/announcementlist')
    },

    goToSuggestions() {
      this.$router.push('/student/suggestionlist')
    },

    goToExams() {
      this.$router.push('/student/examlist')
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
.student-home {
  padding: 20px;
  /* 清爽教育风：更明亮的浅蓝背景 */
  background: linear-gradient(180deg, #f8fbff 0%, #f1f8ff 42%, #f3f4f6 100%);
  min-height: 100vh;
}

.home-header {
  margin-bottom: 20px;
}

.welcome-block {
  display: flex;
  justify-content: space-between;
  align-items: stretch;
  padding: 22px 24px;
  border-radius: 16px;
  /* 清爽教育风：天蓝 + 青绿，更轻、更亲和 */
  background: linear-gradient(135deg, #2563eb 0%, #38bdf8 45%, #22c55e 120%);
  color: #f8fafc;
  border: 1px solid rgba(255, 255, 255, 0.25);
  box-shadow: 0 16px 40px rgba(37, 99, 235, 0.18);
}

.welcome-main {
  max-width: 60%;
}

.welcome-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.16);
  border: 1px solid rgba(255, 255, 255, 0.28);
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  margin-bottom: 10px;
}

.welcome-main h1 {
  margin: 0 0 6px;
  font-size: 26px;
  font-weight: 700;
}

.welcome-sub {
  margin: 0 0 16px;
  font-size: 14px;
  color: rgba(248, 250, 252, 0.86);
}

.welcome-actions {
  display: flex;
  gap: 10px;
}

.welcome-side {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
  min-width: 200px;
}

.today-stat {
  padding: 8px 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.24);
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}

.today-stat .label {
  color: rgba(248, 250, 252, 0.82);
}

.today-stat .value {
  font-weight: 600;
  color: #ffffff;
}

.today-stat .value.highlight {
  color: #fef08a;
}

.home-main {
  margin-top: 16px;
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
}

.panel {
  border-radius: 14px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
  overflow: hidden;
}

.panel :deep(.el-card__header) {
  background: #ffffff;
  border-bottom: 1px solid #f1f5f9;
  padding: 14px 16px;
}

.panel :deep(.el-card__body) {
  padding: 14px 16px 16px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.panel-header h3 i {
  margin-right: 6px;
  color: #0ea5e9;
}

.course-progress-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f3f4f6;
}

.progress-line :deep(.el-progress-bar__outer) {
  background-color: #e5e7eb;
}

.progress-line :deep(.el-progress-bar__inner) {
  /* 清爽教育风：蓝到青绿 */
  background: linear-gradient(90deg, #2563eb 0%, #38bdf8 55%, #22c55e 120%);
}

.course-progress-row:last-child {
  border-bottom: none;
}

.course-progress-main {
  flex: 1;
  margin-right: 12px;
}

.course-name {
  font-size: 14px;
  font-weight: 500;
  color: #111827;
  margin-bottom: 4px;
}

.course-meta-line {
  font-size: 12px;
  color: #6b7280;
  display: flex;
  gap: 14px;
  margin-bottom: 6px;
}

.progress-line {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-line .el-progress {
  flex: 1;
}

.percent-text {
  font-size: 12px;
  color: #6b7280;
  min-width: 40px;
  text-align: right;
}

.course-progress-actions {
  min-width: 96px;
  text-align: right;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.course-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
}

.course-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.course-cover {
  height: 120px;
  overflow: hidden;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-info {
  padding: 12px;
}

.course-info h4 {
  margin: 0 0 5px 0;
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-teacher {
  margin: 0 0 8px 0;
  font-size: 12px;
  color: #7f8c8d;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
}

.course-price {
  font-weight: bold;
  color: #E6A23C;
}

.course-students {
  color: #999;
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

.panel-empty {
  text-align: center;
  padding: 36px 0;
  color: #6b7280;
}

.panel-empty.small {
  padding: 24px 0;
}

.panel-empty i {
  font-size: 40px;
  margin-bottom: 6px;
  display: block;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.course-card {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  overflow: hidden;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
}

.course-card:hover {
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.1);
  transform: translateY(-2px);
}

.course-cover {
  height: 110px;
  overflow: hidden;
  background: #f3f4f6;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-info {
  padding: 10px 12px 12px;
}

.course-info h4 {
  margin: 0 0 4px;
  font-size: 14px;
  font-weight: 500;
  color: #111827;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-teacher {
  margin: 0 0 6px;
  font-size: 12px;
  color: #6b7280;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
}

.course-price {
  font-weight: 600;
  color: #f59e0b;
}

.course-students {
  color: #9ca3af;
}

.quick-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}

.quick-item {
  display: flex;
  align-items: center;
  padding: 10px 10px;
  border-radius: 10px;
  background: #f9fafb;
  cursor: pointer;
  transition: all 0.2s ease;
}

.quick-item:hover {
  background: #eef2ff;
  transform: translateY(-1px);
}

.quick-item:hover .quick-text .title {
  color: #0f172a;
}

.quick-icon {
  width: 34px;
  height: 34px;
  border-radius: 999px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 10px;
  font-size: 18px;
}

.quick-icon.primary { background: #6366f1; }
.quick-icon.success { background: #22c55e; }
.quick-icon.warning { background: #f59e0b; }
.quick-icon.info { background: #0ea5e9; }
.quick-icon.suggest { background: #ec4899; }
.quick-icon.muted { background: #6b7280; }

.quick-text .title {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
  color: #111827;
}

.quick-text .desc {
  margin: 2px 0 0;
  font-size: 12px;
  color: #6b7280;
}

.announcement-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f3f4f6;
  cursor: pointer;
  transition: background 0.2s ease;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-item:hover {
  background: #f9fafb;
}

.announcement-title {
  font-size: 13px;
  color: #111827;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.announcement-time {
  font-size: 12px;
  color: #9ca3af;
  margin-left: 8px;
  white-space: nowrap;
}

@media (max-width: 900px) {
  .home-main {
    grid-template-columns: 1fr;
  }

  .welcome-block {
    flex-direction: column;
  }

  .welcome-main {
    max-width: 100%;
    margin-bottom: 12px;
  }

  .welcome-side {
    flex-direction: row;
    flex-wrap: wrap;
    gap: 8px;
  }
}
</style>