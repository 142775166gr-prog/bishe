<template>
  <div class="my-courses">
    <div class="header">
      <h2>我的课程</h2>
      <div class="stats">
        <el-statistic title="已选课程" :value="total" />
      </div>
    </div>

    <!-- 课程卡片列表 -->
    <div class="course-grid" v-loading="loading">
      <div class="course-card" v-for="course in list" :key="course.id">
        <div class="course-cover">
          <img 
            :src="formatImageUrl(course.courseCover)" 
            :alt="course.courseTitle"
            referrerpolicy="no-referrer"
            @error="handleImageError" />
          <div class="progress-overlay">
            <el-progress 
              type="circle" 
              :percentage="Math.round(course.progress || 0)" 
              :width="60"
              :stroke-width="4">
            </el-progress>
          </div>
        </div>
        <div class="course-info">
          <h3 class="course-title">{{ course.courseTitle }}</h3>
          <p class="course-teacher">{{ course.teacherName }}</p>
          <p class="course-category">{{ course.categoryName }}</p>
          <div class="course-meta">
            <span class="enroll-time">选课时间：{{ formatDate(course.enrollTime) }}</span>
            <span v-if="course.lastStudyTime" class="last-study">
              最后学习：{{ formatDate(course.lastStudyTime) }}
            </span>
          </div>
          <div class="course-actions">
            <el-button type="primary" size="small" @click="startLearning(course)">开始学习</el-button>
            <el-button type="info" size="small" @click="viewCourse(course)">课程详情</el-button>
            <el-button type="danger" size="small" @click="withdrawCourse(course)">退课</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && list.length === 0" class="empty-state">
      <i class="el-icon-document" style="font-size: 64px; color: #ccc;"></i>
      <p>您还没有选择任何课程</p>
      <el-button type="primary" @click="goToCourseCenter">去选课</el-button>
    </div>

    <!-- 课程详情对话框 -->
    <el-dialog title="课程详情" v-model="detailDialogVisible" width="70%">
      <div v-if="selectedCourse" class="course-detail">
        <div class="detail-header">
          <div class="detail-cover">
            <img 
              :src="formatImageUrl(selectedCourse.courseCover)" 
              :alt="selectedCourse.courseTitle"
              referrerpolicy="no-referrer"
              @error="handleImageError" />
          </div>
          <div class="detail-info">
            <h2>{{ selectedCourse.courseTitle }}</h2>
            <p class="detail-teacher">授课教师：{{ selectedCourse.teacherName }}</p>
            <p class="detail-category">课程分类：{{ selectedCourse.categoryName }}</p>
            <div class="detail-progress">
              <span>学习进度：</span>
              <el-progress :percentage="Math.round(selectedCourse.progress || 0)" />
            </div>
            <div class="detail-meta">
              <p>选课时间：{{ formatDate(selectedCourse.enrollTime) }}</p>
              <p v-if="selectedCourse.lastStudyTime">最后学习：{{ formatDate(selectedCourse.lastStudyTime) }}</p>
              <el-tag :type="getDifficultyType(selectedCourse.difficultyLevel)">
                {{ getDifficultyText(selectedCourse.difficultyLevel) }}
              </el-tag>
            </div>
          </div>
        </div>
        <div class="detail-content">
          <h3>课程介绍</h3>
          <div class="course-description">{{ selectedCourse.courseDesc }}</div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="success" @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getStudentCourses, withdrawCourse } from '../../api/studentCourse'
import { getSessionStorage } from '../../utils/common'

export default {
  data() {
    return {
      list: [],
      total: 0,
      loading: false,

      detailDialogVisible: false,
      selectedCourse: null,

      currentStudent: null
    }
  },
  created() {
    this.initStudentInfo()
    this.getList()
  },
  
  activated() {
    // 当页面被激活时（从其他页面返回），重新加载数据
    console.log('我的课程页面被激活，重新加载数据...')
    this.initStudentInfo()
    this.getList()
  },
  methods: {
    initStudentInfo() {
      const currentUser = getSessionStorage('curuser')
      if (currentUser && currentUser.info) {
        this.currentStudent = currentUser.info
      }
    },

    async getList() {
      if (!this.currentStudent) {
        this.$message.error('请先登录')
        return
      }

      this.loading = true
      try {
        const res = await getStudentCourses(this.currentStudent.stuId)
        if (res && res.flag) {
          this.list = res.result || []
          this.total = this.list.length
        } else {
          this.list = []
          this.total = 0
        }
      } catch (e) {
        console.error('获取我的课程失败', e)
        this.list = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },

    startLearning(course) {
      this.$router.push({
        path: '/student/courselearn',
        query: { courseId: course.courseId }
      })
    },

    viewCourse(course) {
      this.selectedCourse = course
      this.detailDialogVisible = true
    },

    async withdrawCourse(course) {
      try {
        await this.$confirm('确定要退出这门课程吗？退课后学习进度将被保留。', '确认退课', { 
          type: 'warning' 
        })
        
        const res = await withdrawCourse(this.currentStudent.stuId, course.courseId)
        if (res && res.flag) {
          this.$message.success('退课成功')
          this.getList() // 刷新列表
        } else {
          this.$message.error(res.reason || '退课失败')
        }
      } catch (e) {
        // 用户取消操作
      }
    },

    goToCourseCenter() {
      this.$router.push('/student/courselist')
    },

    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    },

    getDifficultyType(level) {
      const types = { 1: 'success', 2: 'warning', 3: 'danger' }
      return types[level] || 'info'
    },

    getDifficultyText(level) {
      const texts = { 1: '初级', 2: '中级', 3: '高级' }
      return texts[level] || '未知'
    },

    // 生成占位图片的方法
    generatePlaceholderImage(text) {
      const svg = `
        <svg width="300" height="200" xmlns="http://www.w3.org/2000/svg">
          <rect width="300" height="200" fill="#f0f0f0"/>
          <text x="150" y="100" font-family="Arial, sans-serif" font-size="16" fill="#999999" text-anchor="middle" dy="0.3em">${text}</text>
        </svg>
      `
      return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svg)))
    },

    formatImageUrl(url) {
      const raw = (url === null || url === undefined) ? '' : String(url)
      let u = raw.trim().replace(/^['"]|['"]$/g, '')

      // 调试信息
      console.log('课程封面URL:', JSON.stringify(u), 'type:', typeof url)
      
      // 检查各种空值情况
      if (!u || u === 'null' || u === 'undefined') {
        return this.generatePlaceholderImage('暂无封面')
      }
      
      // 已是完整 URL
      if (u.startsWith('http://') || u.startsWith('https://')) {
        return u
      }

      // 协议相对 URL
      if (u.startsWith('//')) {
        return 'https:' + u
      }
      
      // 站内相对路径
      if (u.startsWith('/')) {
        return u
      }
      
      // 域名开头的链接，自动补 https://
      if (u.includes('.') && !u.startsWith('//')) {
        return 'https://' + u
      }
      
      // 其他情况返回占位图
      return this.generatePlaceholderImage('暂无封面')
    },

    handleImageError(event) {
      const img = event && event.target ? event.target : null
      const src = img ? (img.currentSrc || img.src || '') : ''
      console.error('图片加载失败:', src)

      // 对外链图片做一次轻量重试（加时间戳避免缓存/边缘节点偶发失败）
      if (img && src && (src.startsWith('http://') || src.startsWith('https://')) && !src.includes('__retry=')) {
        const joiner = src.includes('?') ? '&' : '?'
        img.src = `${src}${joiner}__retry=${Date.now()}`
        return
      }

      // 最终设置默认图片
      if (img) img.src = this.generatePlaceholderImage('图片加载失败')
    }
  }
}
</script>

<style scoped>
.my-courses {
  padding: 20px;
  background: linear-gradient(180deg, #f8fbff 0%, #f3f4f6 100%);
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px 16px;
  border-radius: 14px;
  background: linear-gradient(90deg, #e0f2fe 0%, #f0f9ff 55%, #f0fdf4 120%);
  border: 1px solid #e5e7eb;
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.06);
}

.header h2 {
  margin: 0;
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

.stats :deep(.el-statistic__content) {
  color: #0f172a;
}

.stats :deep(.el-statistic__head) {
  color: #64748b;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.course-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  background: white;
}

.course-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.course-cover {
  position: relative;
  height: 200px;
  overflow: hidden;
  background: #f5f5f5;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: white;
}

.progress-overlay {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(255,255,255,0.9);
  border-radius: 50%;
  padding: 5px;
}

.course-info {
  padding: 15px;
}

.course-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 8px 0;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-teacher {
  font-size: 14px;
  color: #666;
  margin: 0 0 5px 0;
}

.course-category {
  font-size: 13px;
  color: #999;
  margin: 0 0 10px 0;
}

.course-meta {
  font-size: 12px;
  color: #999;
  margin-bottom: 15px;
}

.course-meta span {
  display: block;
  margin-bottom: 3px;
}

.course-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.course-detail .detail-header {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

.detail-cover {
  width: 300px;
  height: 200px;
  flex-shrink: 0;
}

.detail-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.detail-info {
  flex: 1;
}

.detail-info h2 {
  margin: 0 0 15px 0;
  color: #333;
}

.detail-info p {
  margin: 8px 0;
  color: #666;
}

.detail-progress {
  margin: 15px 0;
}

.detail-progress span {
  display: inline-block;
  margin-bottom: 8px;
  font-weight: bold;
}

.detail-meta p {
  margin: 5px 0;
  font-size: 14px;
}

.course-description {
  white-space: pre-wrap;
  line-height: 1.6;
  color: #666;
}
</style>