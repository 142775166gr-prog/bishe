<template>
  <div class="course-learn">
    <div class="learn-container">
      <!-- 左侧视频播放区域 -->
      <div class="video-section">
        <div v-if="currentContent && currentContent.contentType === 1" class="video-wrapper">
          <!-- 视频完成状态提示 -->
          <div v-if="isContentCompleted(currentContent.contentId)" class="video-completed-banner">
            <el-icon><CircleCheck /></el-icon>
            <span>您已完成学习该视频</span>
          </div>
          
          <VideoPlayer 
            :video-url="currentContent.contentUrl"
            :content-info="currentContent"
            :show-custom-controls="true"
            @progress-update="onProgressUpdate"
            @video-ended="onVideoEnded"
          />
        </div>
        
        <!-- 非视频内容显示 -->
        <div v-else-if="currentContent" class="content-wrapper">
          <div class="content-header">
            <h3>{{ currentContent.contentTitle }}</h3>
            <div class="content-tags">
              <el-tag :type="getContentTypeColor(currentContent.contentType)">
                {{ getContentTypeText(currentContent.contentType) }}
              </el-tag>
              <el-tag v-if="isContentCompleted(currentContent.contentId)" type="success">
                <el-icon><CircleCheck /></el-icon>
                已完成
              </el-tag>
            </div>
          </div>
          
          <div class="content-body">
            <div v-if="currentContent.contentType === 2" class="document-content">
              <el-button type="primary" @click="openDocument">
                <el-icon><Document /></el-icon>
                打开文档
              </el-button>
              <p>{{ currentContent.contentDesc }}</p>
            </div>
            
            <div v-else-if="currentContent.contentType === 3" class="audio-content">
              <audio :src="currentContent.contentUrl" controls style="width: 100%;">
                您的浏览器不支持音频播放
              </audio>
              <p>{{ currentContent.contentDesc }}</p>
            </div>
            
            <div v-else-if="currentContent.contentType === 4" class="image-content">
              <img 
                :src="formatImageUrl(currentContent.contentUrl)" 
                :alt="currentContent.contentTitle" 
                @error="handleImageError"
                @load="handleImageLoad"
                @click="viewLargeImage" />
              <p>{{ currentContent.contentDesc }}</p>
            </div>
          </div>
        </div>
        
        <!-- 没有选择内容时的提示 -->
        <div v-else class="no-content">
          <el-empty description="请从右侧选择要学习的内容"></el-empty>
        </div>

        <!-- 课程评论区 -->
        <div class="comments-section">
          <CourseComments :courseId="courseId" />
        </div>
      </div>
      
      <!-- 右侧课程目录 -->
      <div class="catalog-section">
        <div class="course-header">
          <h2>{{ courseInfo ? courseInfo.courseTitle : '课程学习' }}</h2>
          <div class="course-actions">
            <el-button type="success" size="small" @click="goToExams">
              <el-icon><Edit /></el-icon>
              考试练习
            </el-button>
          </div>
          <div class="course-progress">
            <el-progress :percentage="courseProgress" :show-text="false"></el-progress>
            <span class="progress-text">学习进度 {{ courseProgress }}%</span>
          </div>
        </div>
        
        <div class="chapter-list" v-loading="loading">
          <div 
            v-for="(chapter, chapterIndex) in chapters" 
            :key="chapter.chapterId"
            class="chapter-item"
          >
            <div class="chapter-header" @click="toggleChapter(chapter)">
              <div class="chapter-info">
                <h4>第{{ chapterIndex + 1 }}章 {{ chapter.chapterTitle }}</h4>
                <span class="content-count">{{ chapter.contents ? chapter.contents.length : 0 }}个内容</span>
              </div>
              <el-icon class="expand-icon" :class="{ expanded: chapter.expanded }">
                <ArrowDown />
              </el-icon>
            </div>
            
            <div class="content-list" v-show="chapter.expanded">
              <div 
                v-for="(content, contentIndex) in chapter.contents" 
                :key="content.contentId"
                class="content-item"
                :class="{ 
                  active: currentContent && currentContent.contentId === content.contentId,
                  completed: isContentCompleted(content.contentId)
                }"
                @click="selectContent(content)"
              >
                <div class="content-icon">
                  <el-icon v-if="content.contentType === 1"><VideoPlay /></el-icon>
                  <el-icon v-else-if="content.contentType === 2"><Document /></el-icon>
                  <el-icon v-else-if="content.contentType === 3"><Microphone /></el-icon>
                  <el-icon v-else><Picture /></el-icon>
                </div>
                
                <div class="content-info">
                  <span class="content-title">{{ contentIndex + 1 }}. {{ content.contentTitle }}</span>
                  <div class="content-meta">
                    <el-tag v-if="content.isFree === 1" type="success" size="small">试看</el-tag>
                  </div>
                </div>
                
                <div class="content-status">
                  <el-icon v-if="isContentCompleted(content.contentId)" class="completed-icon">
                    <CircleCheck />
                  </el-icon>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getCourseById } from '../../api/course'
import { getChaptersByCourseId } from '../../api/courseChapter'
import { getContentsByChapterId } from '../../api/chapterContent'
import { 
  getCourseProgress, 
  updateProgress, 
  markCompleted, 
  checkCompleted,
  getContentProgress 
} from '../../api/studentContentProgress'
import VideoPlayer from '../../components/VideoPlayer.vue'
import CourseComments from '../../components/CourseComments.vue'
import { getSessionStorage } from '../../utils/common'

export default {
  name: 'CourseLearn',
  components: {
    VideoPlayer,
    CourseComments
  },
  data() {
    return {
      courseId: null,
      courseInfo: null,
      chapters: [],
      currentContent: null,
      loading: false,
      courseProgress: 0,
      completedContents: [], // 已完成的内容ID列表
      currentStudent: null
    }
  },
  created() {
    this.courseId = this.$route.params.courseId || this.$route.query.courseId
    if (!this.courseId) {
      this.$message.error('课程ID不能为空')
      this.$router.push('/student/courselist')
      return
    }
    
    this.initStudentInfo()
    this.loadCourseInfo()
    this.loadChaptersAndProgress() // 合并加载章节和进度
  },
  
  watch: {
    // 监听路由变化，重新加载数据
    '$route'(to, from) {
      if (to.path !== from.path || to.params.courseId !== from.params.courseId) {
        console.log('路由变化，重新加载课程数据...')
        this.courseId = to.params.courseId || to.query.courseId
        if (this.courseId) {
          this.currentContent = null // 清空当前选择的内容
          this.initStudentInfo()
          this.loadCourseInfo()
          this.loadChaptersAndProgress()
        }
      }
    }
  },
  methods: {
    initStudentInfo() {
      const currentUser = getSessionStorage('curuser')
      if (currentUser && currentUser.info) {
        this.currentStudent = currentUser.info
      }
    },

    async loadCourseInfo() {
      try {
        const res = await getCourseById(this.courseId)
        if (res && res.flag) {
          this.courseInfo = res.result
        }
      } catch (e) {
        console.error('获取课程信息失败', e)
      }
    },

    async loadChaptersAndProgress() {
      this.loading = true
      try {
        // 1. 先加载章节和内容
        const res = await getChaptersByCourseId(this.courseId)
        if (res && res.flag) {
          this.chapters = res.result || []
          // 为每个章节加载内容
          for (let chapter of this.chapters) {
            chapter.expanded = false // 默认折叠
            await this.loadChapterContents(chapter)
          }
          // 默认展开第一个章节
          if (this.chapters.length > 0) {
            this.chapters[0].expanded = true
          }
        } else {
          this.chapters = []
        }

        // 2. 加载学习进度
        await this.loadLearningProgress()
        
      } catch (e) {
        console.error('获取章节列表失败', e)
        this.chapters = []
      } finally {
        this.loading = false
      }
    },

    async loadChapters() {
      // 保留原方法以备其他地方调用
      return this.loadChaptersAndProgress()
    },

    async loadChapterContents(chapter) {
      try {
        const res = await getContentsByChapterId(chapter.chapterId)
        if (res && res.flag) {
          // 兼容旧数据：contentStatus 可能为 0/1 或字符串，甚至为空
          // 学生端需要能看到数据库已有内容，否则会出现“0个内容”的空页面
          chapter.contents = (res.result || []).filter(content => {
            const s = content ? content.contentStatus : null
            if (s === null || s === undefined || s === '') return true
            return Number(s) === 1 || Number(s) === 0
          })
          console.log(`章节 "${chapter.chapterTitle}" 加载了 ${chapter.contents.length} 个内容`)
          
          // 打印权重信息用于调试
          chapter.contents.forEach(content => {
            console.log(`内容 "${content.contentTitle}" 权重: ${content.progressWeight}`)
          })
        } else {
          chapter.contents = []
        }
      } catch (e) {
        console.error('获取章节内容失败', e)
        chapter.contents = []
      }
    },

    async loadLearningProgress() {
      if (!this.currentStudent) return
      
      try {
        console.log('开始扫描学习进度...')
        
        // 获取课程总进度（基于权重计算）
        const res = await getCourseProgress(this.currentStudent.stuId, this.courseId)
        if (res && res.flag && res.result) {
          this.courseProgress = Math.round(res.result.progress || 0)
          console.log('课程进度（加权）:', this.courseProgress + '%')
          
          // 显示进度统计信息
          if (res.result.total_weight) {
            console.log('总权重:', res.result.total_weight)
            console.log('已完成权重:', res.result.completed_weight || 0)
            console.log('总内容数:', res.result.total_contents)
            console.log('已完成内容数:', res.result.completed_contents || 0)
          }
        }
        
        // 重新扫描每个内容的完成状态
        this.completedContents = []
        let totalContents = 0
        let completedCount = 0
        
        for (let chapter of this.chapters) {
          if (chapter.contents) {
            for (let content of chapter.contents) {
              totalContents++
              try {
                const completedRes = await checkCompleted(this.currentStudent.stuId, content.contentId)
                if (completedRes && completedRes.flag && completedRes.result) {
                  this.completedContents.push(content.contentId)
                  completedCount++
                  console.log(`✓ 内容 "${content.contentTitle}" 已完成`)
                } else {
                  console.log(`○ 内容 "${content.contentTitle}" 未完成`)
                }
              } catch (e) {
                console.error(`检查内容 ${content.contentId} 完成状态失败:`, e)
              }
            }
          }
        }
        
        console.log(`进度扫描完成: ${completedCount}/${totalContents} 个内容已完成`)
        console.log('已完成的内容ID列表:', this.completedContents)
        
        // 强制更新视图
        this.$forceUpdate()
        
      } catch (e) {
        console.error('获取学习进度失败', e)
      }
    },

    async saveLearningProgress(contentId, watchProgress) {
      if (!this.currentStudent || !this.currentContent) return
      
      try {
        await updateProgress(
          this.currentStudent.stuId,
          this.courseId,
          this.currentContent.chapterId,
          contentId,
          watchProgress
        )
        
        // 重新获取课程总进度（基于权重计算）
        const res = await getCourseProgress(this.currentStudent.stuId, this.courseId)
        if (res && res.flag && res.result) {
          this.courseProgress = Math.round(res.result.progress || 0)
          console.log('更新后的课程进度:', this.courseProgress + '%')
        }
      } catch (e) {
        console.error('保存学习进度失败', e)
      }
    },

    toggleChapter(chapter) {
      chapter.expanded = !chapter.expanded
    },

    async selectContent(content) {
      // 检查是否有权限观看
      if (content.isFree !== 1) {
        // 这里应该检查学生是否已购买课程
        // 暂时允许所有内容播放
      }
      
      this.currentContent = content
      
      // 获取该内容的学习进度（静默获取，不显示提示）
      if (this.currentStudent) {
        try {
          const progressRes = await getContentProgress(this.currentStudent.stuId, content.contentId)
          if (progressRes && progressRes.flag && progressRes.result) {
            const progress = progressRes.result
            console.log(`内容 "${content.contentTitle}" 的学习进度:`, {
              watchProgress: progress.watchProgress,
              isCompleted: progress.isCompleted,
              completedTime: progress.completedTime
            })
            
            // 将进度信息附加到当前内容对象上，供视频播放器使用
            this.currentContent.learningProgress = progress
          }
        } catch (e) {
          console.error('获取内容学习进度失败', e)
        }
      }
    },

    async onProgressUpdate(progress) {
      if (!this.currentContent || !this.currentStudent) return
      
      // 只有当进度大于0时才保存（避免频繁的0%更新）
      if (progress.progress > 0) {
        // 保存视频播放进度（后端会确保进度只能增加）
        await this.saveLearningProgress(this.currentContent.contentId, Math.round(progress.progress))
        
        // 当视频播放进度超过80%时，标记为已完成
        if (progress.progress > 80 && !this.isContentCompleted(this.currentContent.contentId)) {
          await this.markContentCompleted(this.currentContent.contentId)
        }
      }
    },

    async onVideoEnded() {
      if (this.currentContent) {
        await this.markContentCompleted(this.currentContent.contentId)
        // 自动播放下一个内容
        this.playNextContent()
      }
    },

    async markContentCompleted(contentId) {
      if (!this.currentStudent || this.isContentCompleted(contentId)) return
      
      try {
        const res = await markCompleted(
          this.currentStudent.stuId,
          this.courseId,
          this.currentContent.chapterId,
          contentId
        )
        
        if (res && res.flag) {
          // 重新扫描所有进度状态
          await this.loadLearningProgress()
          
          // 只在真正完成时显示一次提示
          this.$message.success('内容学习完成！')
        }
      } catch (e) {
        console.error('标记完成失败', e)
      }
    },

    isContentCompleted(contentId) {
      return this.completedContents.includes(contentId)
    },

    playNextContent() {
      // 找到下一个内容并自动播放
      let found = false
      let nextContent = null
      
      for (let chapter of this.chapters) {
        if (chapter.contents) {
          for (let content of chapter.contents) {
            if (found) {
              nextContent = content
              break
            }
            if (content.contentId === this.currentContent.contentId) {
              found = true
            }
          }
          if (nextContent) break
        }
      }
      
      if (nextContent) {
        this.selectContent(nextContent)
        this.$message.success('自动播放下一个内容')
      } else {
        this.$message.success('恭喜！您已完成本课程的学习')
      }
    },

    async openDocument() {
      if (this.currentContent && this.currentContent.contentUrl) {
        window.open(this.currentContent.contentUrl, '_blank')
        
        // 点击文档就标记为已完成（如果还未完成的话）
        if (!this.isContentCompleted(this.currentContent.contentId)) {
          await this.markContentCompleted(this.currentContent.contentId)
        }
      }
    },

    getContentTypeText(type) {
      const types = { 1: '视频', 2: '文档', 3: '音频', 4: '图片' }
      return types[type] || '未知'
    },

    getContentTypeColor(type) {
      const colors = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'info' }
      return colors[type] || 'info'
    },

    goToExams() {
      this.$router.push({
        path: '/student/examlist',
        query: {
          courseId: this.courseId,
          courseTitle: this.courseInfo ? this.courseInfo.courseTitle : '课程'
        }
      })
    },

    formatDuration(seconds) {
      if (!seconds) return ''
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = seconds % 60
      return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
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
      // 调试信息
      console.log('图片URL:', JSON.stringify(url), 'type:', typeof url)
      
      // 检查各种空值情况
      if (!url || url.trim() === '' || url === 'null' || url === 'undefined') {
        console.log('使用默认图片')
        return this.generatePlaceholderImage('暂无图片')
      }
      
      // 如果已经是完整的URL，直接返回
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url
      }
      
      // 如果是相对路径，直接返回
      if (url.startsWith('/')) {
        return url
      }
      
      // 如果是域名开头的链接，自动添加https://
      if (url.includes('.') && !url.startsWith('//')) {
        return 'https://' + url
      }
      
      // 其他情况也返回默认图片
      console.log('其他情况，使用默认图片')
      return this.generatePlaceholderImage('暂无图片')
    },

    handleImageError(event) {
      console.error('图片加载失败:', event.target.src)
      // 设置默认图片
      event.target.src = this.generatePlaceholderImage('图片加载失败')
    },

    handleImageLoad(event) {
      console.log('图片加载成功:', event.target.src)
    },

    viewLargeImage() {
      if (this.currentContent && this.currentContent.contentUrl) {
        // 在新窗口中打开大图
        window.open(this.formatImageUrl(this.currentContent.contentUrl), '_blank')
        
        // 点击图片也标记为已完成（如果还未完成的话）
        if (!this.isContentCompleted(this.currentContent.contentId)) {
          this.markContentCompleted(this.currentContent.contentId)
        }
      }
    }
  }
}
</script>

<style scoped>
.course-learn {
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(180deg, #f8fbff 0%, #f3f4f6 100%);
}

.learn-container {
  display: flex;
  height: 100%;
}

.video-section {
  flex: 1;
  padding: 20px 24px;
  overflow-y: auto;
}

.video-wrapper {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  background: #000;
  min-height: 400px;
}

.video-wrapper :deep(video) {
  width: 100%;
  height: auto;
  display: block;
  min-height: 400px;
  object-fit: contain;
}

.video-completed-banner {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(82, 196, 26, 0.9);
  color: white;
  padding: 8px 15px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  z-index: 10;
  backdrop-filter: blur(5px);
}

.content-wrapper {
  width: 100%;
  max-width: 800px;
  background: #ffffff;
  border-radius: 14px;
  padding: 24px 24px 20px;
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.06);
  border: 1px solid #e5e7eb;
  margin: 16px auto 0;
}

.content-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.content-header h3 {
  margin: 0;
  color: #333;
}

.content-tags {
  display: flex;
  align-items: center;
  gap: 10px;
}

.content-body {
  text-align: center;
}

.document-content, .audio-content {
  padding: 20px;
}

.image-content img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.2s;
}

.image-content img:hover {
  transform: scale(1.02);
}

.no-content {
  max-width: 800px;
  margin: 16px auto 0;
  padding: 32px 24px;
  border-radius: 14px;
  background: linear-gradient(135deg, #e0f2fe 0%, #f0f9ff 55%, #f0fdf4 120%);
  border: 1px solid #e5e7eb;
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.06);
}

.no-content :deep(.el-empty__description p) {
  color: #0f172a;
}

.comments-section {
  margin: 24px auto 0;
  max-width: 900px;
  background: #ffffff;
  border-radius: 14px;
  padding: 18px 20px 16px;
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.06);
  border: 1px solid #e5e7eb;
}

.catalog-section {
  width: 400px;
  background: #f9fafb;
  border-left: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
}

.course-header {
  padding: 16px 16px 14px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  gap: 10px;
  background: #ffffff;
}

.course-header h2 {
  margin: 0;
  color: #0f172a;
  font-size: 16px;
  font-weight: 600;
}

.course-actions {
  display: flex;
  justify-content: flex-end;
}

.course-progress {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 4px;
}

.progress-text {
  font-size: 14px;
  color: #666;
}

.chapter-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 10px 10px;
}

.chapter-item {
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.04);
  border: 1px solid #e5e7eb;
  margin-bottom: 10px;
  overflow: hidden;
}

.chapter-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  cursor: pointer;
  background: #f8fafc;
}

.chapter-header:hover {
  background: #e0f2fe;
}

.chapter-info h4 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 16px;
}

.content-count {
  color: #888;
  font-size: 12px;
}

.expand-icon {
  transition: transform 0.3s;
}

.expand-icon.expanded {
  transform: rotate(180deg);
}

.content-list {
  background: #ffffff;
}

.content-item {
  display: flex;
  align-items: center;
  padding: 10px 14px 10px 32px;
  cursor: pointer;
  border-bottom: 1px solid #f8f8f8;
}

.content-item:hover {
  background: #f8f9fa;
}

.content-item.active {
  background: #e0f2fe;
  border-left: 3px solid #0ea5e9;
}

.content-item.completed {
  background: #ecfdf3;
}

.content-icon {
  margin-right: 10px;
  color: #666;
}

.content-info {
  flex: 1;
}

.content-title {
  display: block;
  color: #333;
  font-size: 14px;
  margin-bottom: 5px;
}

.content-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.content-status {
  margin-left: 10px;
}

.completed-icon {
  color: #52c41a;
}
</style>