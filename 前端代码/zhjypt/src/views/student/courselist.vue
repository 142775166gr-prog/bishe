<template>
  <div class="course-list">
    <div class="header">
      <h2>课程中心</h2>
      <div class="search-bar">
        <el-input 
          v-model="query.courseTitle" 
          placeholder="搜索课程..." 
          style="width: 300px; margin-right: 10px;"
          clearable>
          <template #prefix>
            <i class="el-icon-search"></i>
          </template>
        </el-input>
        <el-select v-model="query.categoryId" placeholder="选择分类" clearable style="width: 150px; margin-right: 10px;">
          <el-option label="全部分类" :value="null"></el-option>
          <el-option 
            v-for="category in categories" 
            :key="category.categoryId" 
            :label="category.categoryName" 
            :value="category.categoryId">
          </el-option>
        </el-select>
        <el-button type="primary" @click="search">搜索</el-button>
      </div>
    </div>

    <!-- 分类标签 -->
    <div class="category-tabs" style="margin-bottom: 20px;">
      <el-tag 
        :type="selectedCategory === null ? 'primary' : ''" 
        @click="selectCategory(null)"
        style="margin-right: 10px; cursor: pointer;">
        全部课程
      </el-tag>
      <el-tag 
        v-for="category in categories" 
        :key="category.categoryId"
        :type="selectedCategory === category.categoryId ? 'primary' : ''" 
        @click="selectCategory(category.categoryId)"
        style="margin-right: 10px; cursor: pointer;">
        {{ category.categoryName }}
      </el-tag>
    </div>

    <!-- 课程卡片列表 -->
    <div class="course-grid" v-loading="loading">
      <div class="course-card" v-for="course in list" :key="course.courseId" @click="viewCourse(course)">
        <div class="course-cover">
          <img 
            :src="formatImageUrl(course.courseCover)" 
            :alt="course.courseTitle" 
            referrerpolicy="no-referrer"
            @error="handleImageError"
            @load="handleImageLoad" />
          <div class="course-overlay">
            <el-button type="primary" size="small">查看详情</el-button>
          </div>
        </div>
        <div class="course-info">
          <h3 class="course-title">{{ course.courseTitle }}</h3>
          <p class="course-teacher">{{ course.teacherName }}</p>
          <p class="course-desc">{{ course.courseDesc }}</p>
          <div class="course-meta">
            <span class="course-students">{{ course.studentCount }}人学习</span>
            <el-tag size="small" :type="getDifficultyType(course.difficultyLevel)">
              {{ getDifficultyText(course.difficultyLevel) }}
            </el-tag>
          </div>
          <div class="course-actions" style="margin-top: 10px;">
            <el-button v-if="course.isEnrolled" type="success" size="small" @click.stop="goToLearning(course)">
              <i class="el-icon-video-play"></i>
              已选课
            </el-button>
            <el-button v-else type="primary" size="small" @click.stop="quickEnroll(course)">
              <i class="el-icon-plus"></i>
              选课
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && list.length === 0" class="empty-state">
      <i class="el-icon-document" style="font-size: 64px; color: #ccc;"></i>
      <p>暂无课程</p>
    </div>

    <!-- 分页 -->
    <div style="margin-top: 30px; text-align: center">
      <el-pagination
        background
        :current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="sizes, prev, pager, next, jumper, ->, total"
        :page-sizes="[6,9,12,18]"
      />
    </div>

    <!-- 课程详情对话框 -->
    <el-dialog title="课程详情" v-model="detailDialogVisible" width="70%">
      <div v-if="selectedCourse" class="course-detail">
        <div class="detail-header">
          <div class="detail-cover">
            <img 
              :src="formatImageUrl(selectedCourse.courseCover)" 
              :alt="selectedCourse.courseTitle"
              @error="handleImageError" />
          </div>
          <div class="detail-info">
            <h2>{{ selectedCourse.courseTitle }}</h2>
            <p class="detail-teacher">授课教师：{{ selectedCourse.teacherName }}</p>
            <p class="detail-category">课程分类：{{ selectedCourse.categoryName }}</p>
          <div class="detail-meta">
            <span class="detail-students">{{ selectedCourse.studentCount }}人已学习</span>
            <el-tag :type="getDifficultyType(selectedCourse.difficultyLevel)">
              {{ getDifficultyText(selectedCourse.difficultyLevel) }}
            </el-tag>
          </div>
            <div class="detail-actions" style="margin-top: 20px;">
              <el-button v-if="!selectedCourse.isEnrolled" type="primary" size="large" @click="enrollCourse" :loading="enrolling">
                <i class="el-icon-plus"></i>
                加入学习
              </el-button>
              <el-button v-else type="success" size="large" @click="startLearning">
                <i class="el-icon-video-play"></i>
                开始学习
              </el-button>
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
import { getPublishedCoursePage, getCourseCategoriesList } from '../../api/course'
import { enrollCourse, checkEnrolled } from '../../api/studentCourse'
import { getSessionStorage } from '../../utils/common'

export default {
  data() {
    return {
      query: { courseTitle: '', categoryId: null },
      list: [],
      total: 0,
      pageNum: 1,
      pageSize: 8,
      loading: false,

      categories: [],
      selectedCategory: null,

      detailDialogVisible: false,
      selectedCourse: null,
      enrolling: false,

      currentStudent: null
    }
  },
  created() {
    this.initStudentInfo()
    this.getList()
    this.loadCategories()
  },
  
  activated() {
    // 当页面被激活时（从其他页面返回），重新加载数据
    console.log('课程列表页面被激活，重新加载数据...')
    this.initStudentInfo()
    this.getList()
    this.loadCategories()
  },
  methods: {
    initStudentInfo() {
      const currentUser = getSessionStorage('curuser')
      console.log('当前用户信息:', currentUser)
      if (currentUser && currentUser.info) {
        this.currentStudent = currentUser.info
        console.log('学生信息:', this.currentStudent)
        console.log('学生ID:', this.currentStudent.stuId)
      }
    },

    async getList() {
      this.loading = true
      try {
        const params = {
          courseTitle: this.query.courseTitle ? this.query.courseTitle.trim() : '',
          categoryId: this.selectedCategory || this.query.categoryId
        }
        const res = await getPublishedCoursePage(params, this.pageNum, this.pageSize)
        if (res && res.flag) {
          this.list = res.result || []
          this.total = res.total || 0
          
          // 检查每个课程的选课状态
          if (this.currentStudent && this.list.length > 0) {
            await this.checkEnrollmentStatus()
          }
        } else {
          this.list = []
          this.total = 0
        }
      } catch (e) {
        console.error('获取课程列表失败', e)
        this.list = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },

    // 检查课程列表的选课状态
    async checkEnrollmentStatus() {
      if (!this.currentStudent) return
      
      try {
        for (let course of this.list) {
          const res = await checkEnrolled(this.currentStudent.stuId, course.courseId)
          if (res && res.flag) {
            course.isEnrolled = res.result
          } else {
            course.isEnrolled = false
          }
        }
      } catch (e) {
        console.error('检查选课状态失败', e)
      }
    },

    async loadCategories() {
      try {
        const res = await getCourseCategoriesList()
        if (res && res.flag) {
          this.categories = res.result || []
        }
      } catch (e) {
        console.error('获取分类列表失败', e)
      }
    },

    search() {
      this.pageNum = 1
      this.selectedCategory = this.query.categoryId
      this.getList()
    },

    selectCategory(categoryId) {
      this.selectedCategory = categoryId
      this.query.categoryId = categoryId
      this.pageNum = 1
      this.getList()
    },

    handleSizeChange(size) {
      this.pageSize = size
      this.getList()
    },

    handleCurrentChange(page) {
      this.pageNum = page
      this.getList()
    },

    async viewCourse(course) {
      this.selectedCourse = course
      
      // 检查是否已选课
      if (this.currentStudent) {
        try {
          const res = await checkEnrolled(this.currentStudent.stuId, course.courseId)
          if (res && res.flag) {
            this.selectedCourse.isEnrolled = res.result
          }
        } catch (e) {
          console.error('检查选课状态失败', e)
          this.selectedCourse.isEnrolled = false
        }
      } else {
        this.selectedCourse.isEnrolled = false
      }
      
      this.detailDialogVisible = true
    },

    async enrollCourse() {
      if (!this.currentStudent) {
        this.$message.error('请先登录')
        return
      }

      this.enrolling = true
      try {
        const res = await enrollCourse(this.currentStudent.stuId, this.selectedCourse.courseId)
        if (res && res.flag) {
          this.$message.success('选课成功！')
          this.selectedCourse.isEnrolled = true
          // 更新课程列表中的学生数量
          const courseInList = this.list.find(c => c.courseId === this.selectedCourse.courseId)
          if (courseInList) {
            courseInList.studentCount = (courseInList.studentCount || 0) + 1
          }
        } else {
          this.$message.error(res.reason || '选课失败')
        }
      } catch (e) {
        console.error('选课失败', e)
        this.$message.error('选课失败')
      } finally {
        this.enrolling = false
      }
    },

    startLearning() {
      if (this.selectedCourse) {
        this.$router.push({
          path: '/student/courselearn',
          query: { courseId: this.selectedCourse.courseId }
        })
      }
    },

    // 快速选课
    async quickEnroll(course) {
      if (!this.currentStudent) {
        this.$message.error('请先登录')
        return
      }

      try {
        console.log('快速选课参数:', {
          studentId: this.currentStudent.stuId,
          courseId: course.courseId
        })
        
        const res = await enrollCourse(this.currentStudent.stuId, course.courseId)
        console.log('选课结果:', res)
        
        if (res && res.flag) {
          this.$message.success('选课成功！')
          course.isEnrolled = true
          course.studentCount = (course.studentCount || 0) + 1
        } else {
          this.$message.error(res?.reason || '选课失败')
        }
      } catch (e) {
        console.error('选课失败', e)
        this.$message.error('选课失败：' + (e.message || '网络错误'))
      }
    },

    // 跳转到学习页面
    goToLearning(course) {
      this.$router.push({
        path: '/student/courselearn',
        query: { courseId: course.courseId }
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
      // 随机颜色数组，使用较柔和的颜色
      const colors = [
        { bg: '#4CAF50', text: '#FFFFFF' }, // 绿色
        { bg: '#2196F3', text: '#FFFFFF' }, // 蓝色
        { bg: '#FF9800', text: '#FFFFFF' }, // 橙色
        { bg: '#9C27B0', text: '#FFFFFF' }, // 紫色
        { bg: '#607D8B', text: '#FFFFFF' }, // 蓝灰色
        { bg: '#795548', text: '#FFFFFF' }  // 棕色
      ]
      
      // 根据文本内容选择颜色（保持一致性）
      const colorIndex = text.length % colors.length
      const color = colors[colorIndex]
      
      const svg = `
        <svg width="300" height="200" xmlns="http://www.w3.org/2000/svg">
          <defs>
            <linearGradient id="grad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:${color.bg};stop-opacity:1" />
              <stop offset="100%" style="stop-color:${color.bg};stop-opacity:0.8" />
            </linearGradient>
          </defs>
          <rect width="300" height="200" fill="url(#grad)"/>
          <circle cx="150" cy="80" r="25" fill="${color.text}" opacity="0.3"/>
          <text x="150" y="130" font-family="Arial, sans-serif" font-size="16" font-weight="bold" fill="${color.text}" text-anchor="middle" dy="0.3em">${text}</text>
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
    },

    handleImageLoad(event) {
      console.log('图片加载成功:', event.target.src)
    }
  }
}
</script>

<style scoped>
.course-list {
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

.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}

.category-tabs {
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.course-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
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

.course-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.course-card:hover .course-overlay {
  opacity: 1;
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
  margin: 0 0 8px 0;
}

.course-desc {
  font-size: 13px;
  color: #999;
  margin: 0 0 12px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  height: 2.8em;
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

.detail-meta {
  display: flex;
  gap: 20px;
  align-items: center;
  margin-top: 15px;
}

.detail-price {
  font-size: 18px;
  font-weight: bold;
  color: #E6A23C;
}

.detail-students {
  color: #999;
}

.course-description {
  white-space: pre-wrap;
  line-height: 1.6;
  color: #666;
}
</style>