<template>
  <div class="chapter-manager">
    <!-- 课程信息头部 -->
    <div class="course-header" v-if="courseInfo">
      <h2>{{ courseInfo.courseTitle }}</h2>
      <p class="course-desc">{{ courseInfo.courseDesc }}</p>
      <div class="course-meta">
        <el-tag :type="getStatusType(courseInfo.courseStatus)">
          {{ getStatusText(courseInfo.courseStatus) }}
        </el-tag>
        <span class="meta-item">分类：{{ courseInfo.categoryName }}</span>
        <span class="meta-item">创建时间：{{ courseInfo.createTime }}</span>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="onAddChapter">
        <el-icon><Plus /></el-icon>
        添加章节
      </el-button>
      <el-button type="success" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回课程列表
      </el-button>
    </div>

    <!-- 章节列表 -->
    <div class="chapter-list" v-loading="loading">
      <div v-if="chapters.length === 0" class="empty-state">
        <el-empty description="暂无章节，点击上方按钮添加第一个章节"></el-empty>
      </div>
      
      <div v-else>
        <div 
          v-for="(chapter, index) in chapters" 
          :key="chapter.chapterId"
          class="chapter-item"
        >
          <div class="chapter-header">
            <div class="chapter-info">
              <h3 class="chapter-title">
                第{{ index + 1 }}章：{{ chapter.chapterTitle }}
              </h3>
              <p class="chapter-desc" v-if="chapter.chapterDesc">
                {{ chapter.chapterDesc }}
              </p>
              <div class="chapter-meta">
                <el-tag 
                  :type="chapter.chapterStatus === 1 ? 'success' : 'info'" 
                  size="small"
                >
                  {{ chapter.chapterStatus === 1 ? '已发布' : '草稿' }}
                </el-tag>
                <span class="meta-text">创建时间：{{ chapter.createTime }}</span>
              </div>
            </div>
            
            <div class="chapter-actions">
              <el-button type="primary" size="small" @click="onEditChapter(chapter)">
                编辑
              </el-button>
              <el-button 
                v-if="chapter.chapterStatus === 0" 
                type="success" 
                size="small" 
                @click="onPublishChapter(chapter)"
              >
                发布
              </el-button>
              <el-button 
                v-else 
                type="warning" 
                size="small" 
                @click="onUnpublishChapter(chapter)"
              >
                取消发布
              </el-button>
              <el-button type="info" size="small" @click="onManageContent(chapter)">
                管理内容
              </el-button>
              <el-button type="danger" size="small" @click="onDeleteChapter(chapter)">
                删除
              </el-button>
            </div>
          </div>

          <!-- 章节内容预览 -->
          <div class="content-preview" v-if="chapter.contents && chapter.contents.length > 0">
            <div class="content-list">
              <div 
                v-for="content in chapter.contents" 
                :key="content.contentId"
                class="content-item"
              >
                <el-icon class="content-icon">
                  <VideoPlay v-if="content.contentType === 1" />
                  <Document v-else-if="content.contentType === 2" />
                  <Headphone v-else-if="content.contentType === 3" />
                  <Picture v-else />
                </el-icon>
                <span class="content-title">{{ content.contentTitle }}</span>
                <el-tag 
                  v-if="content.isFree === 1" 
                  type="success" 
                  size="small"
                >
                  试看
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 章节编辑对话框 -->
    <el-dialog 
      :title="chapterDialogTitle" 
      v-model="chapterDialogVisible" 
      width="50%"
    >
      <el-form :model="chapterForm" ref="chapterFormRef" label-width="100px">
        <el-form-item 
          label="章节标题" 
          prop="chapterTitle" 
          :rules="[{ required: true, message: '请输入章节标题', trigger: 'blur' }]"
        >
          <el-input v-model="chapterForm.chapterTitle" placeholder="请输入章节标题"></el-input>
        </el-form-item>
        <el-form-item label="章节描述" prop="chapterDesc">
          <el-input 
            v-model="chapterForm.chapterDesc" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入章节描述（可选）"
          ></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number 
            v-model="chapterForm.sortOrder" 
            :min="1" 
            :max="999"
            placeholder="章节排序"
          ></el-input-number>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="success" @click="chapterDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitChapterForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 内容管理对话框 -->
    <el-dialog 
      title="章节内容管理" 
      v-model="contentDialogVisible" 
      width="80%"
      :before-close="handleContentDialogClose"
    >
      <ChapterContentManager 
        v-if="contentDialogVisible && currentChapter"
        :chapter="currentChapter"
        @refresh="loadChapterContents"
      />
    </el-dialog>
  </div>
</template>

<script>
import { 
  getChaptersByCourseId, 
  createChapter, 
  updateChapter, 
  deleteChapter,
  publishChapter,
  unpublishChapter
} from '../../api/courseChapter'
import { getCourseById } from '../../api/course'
import { getContentsByChapterId } from '../../api/chapterContent'
import ChapterContentManager from '../../components/ChapterContentManager.vue'
import { getSessionStorage } from '../../utils/common'

export default {
  name: 'ChapterManager',
  components: {
    ChapterContentManager
  },
  data() {
    return {
      courseId: null,
      courseInfo: null,
      chapters: [],
      loading: false,
      
      // 章节对话框
      chapterDialogVisible: false,
      chapterDialogTitle: '添加章节',
      chapterForm: {
        chapterTitle: '',
        chapterDesc: '',
        sortOrder: 1
      },
      
      // 内容管理对话框
      contentDialogVisible: false,
      currentChapter: null,
      
      currentTeacher: null
    }
  },
  created() {
    this.courseId = this.$route.params.courseId || this.$route.query.courseId
    if (!this.courseId) {
      this.$message.error('课程ID不能为空')
      this.goBack()
      return
    }
    
    this.initTeacherInfo()
    this.loadCourseInfo()
    this.loadChapters()
  },
  methods: {
    initTeacherInfo() {
      const currentUser = getSessionStorage('curuser')
      if (currentUser && currentUser.info) {
        this.currentTeacher = currentUser.info
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

    async loadChapters() {
      this.loading = true
      try {
        const res = await getChaptersByCourseId(this.courseId)
        if (res && res.flag) {
          this.chapters = res.result || []
          // 为每个章节加载内容预览
          for (let chapter of this.chapters) {
            await this.loadChapterContents(chapter)
          }
        } else {
          this.chapters = []
        }
      } catch (e) {
        console.error('获取章节列表失败', e)
        this.chapters = []
      } finally {
        this.loading = false
      }
    },

    async loadChapterContents(chapter) {
      try {
        const res = await getContentsByChapterId(chapter.chapterId)
        if (res && res.flag) {
          chapter.contents = res.result || []
        } else {
          chapter.contents = []
        }
      } catch (e) {
        console.error('获取章节内容失败', e)
        chapter.contents = []
      }
    },

    onAddChapter() {
      this.chapterDialogTitle = '添加章节'
      this.chapterForm = {
        chapterTitle: '',
        chapterDesc: '',
        sortOrder: this.chapters.length + 1
      }
      this.chapterDialogVisible = true
    },

    onEditChapter(chapter) {
      this.chapterDialogTitle = '编辑章节'
      this.chapterForm = {
        chapterId: chapter.chapterId,
        chapterTitle: chapter.chapterTitle,
        chapterDesc: chapter.chapterDesc,
        sortOrder: chapter.sortOrder
      }
      this.chapterDialogVisible = true
    },

    async submitChapterForm() {
      try {
        await this.$refs.chapterFormRef.validate()
        
        const params = {
          ...this.chapterForm,
          courseId: this.courseId
        }
        
        let res
        if (this.chapterForm.chapterId) {
          res = await updateChapter(params)
        } else {
          res = await createChapter(params)
        }
        
        if (res && res.flag) {
          this.$message.success(res.reason || '操作成功')
          this.chapterDialogVisible = false
          this.loadChapters()
        } else {
          this.$message.error(res.reason || '操作失败')
        }
      } catch (e) {
        console.error('保存章节失败', e)
        this.$message.error('保存失败')
      }
    },

    async onPublishChapter(chapter) {
      try {
        await this.$confirm('确定发布该章节吗？', '确认', { type: 'warning' })
        const res = await publishChapter(chapter.chapterId)
        if (res && res.flag) {
          this.$message.success('发布成功')
          this.loadChapters()
        } else {
          this.$message.error(res.reason || '发布失败')
        }
      } catch (e) {
        // cancel noop
      }
    },

    async onUnpublishChapter(chapter) {
      try {
        await this.$confirm('确定取消发布该章节吗？', '确认', { type: 'warning' })
        const res = await unpublishChapter(chapter.chapterId)
        if (res && res.flag) {
          this.$message.success('取消发布成功')
          this.loadChapters()
        } else {
          this.$message.error(res.reason || '取消发布失败')
        }
      } catch (e) {
        // cancel noop
      }
    },

    async onDeleteChapter(chapter) {
      try {
        await this.$confirm('确定删除该章节吗？删除后无法恢复。', '确认', { type: 'warning' })
        console.log('删除章节:', chapter.chapterId, this.courseId)
        const res = await deleteChapter(chapter.chapterId, this.courseId)
        console.log('删除章节响应:', res)
        if (res && res.flag) {
          this.$message.success('删除成功')
          this.loadChapters()
        } else {
          this.$message.error(res.reason || '删除失败')
        }
      } catch (e) {
        if (e !== 'cancel') {
          console.error('删除章节失败', e)
          this.$message.error('删除失败')
        }
      }
    },

    onManageContent(chapter) {
      this.currentChapter = chapter
      this.contentDialogVisible = true
    },

    handleContentDialogClose() {
      this.contentDialogVisible = false
      this.currentChapter = null
      // 刷新章节内容
      this.loadChapters()
    },

    goBack() {
      this.$router.push('/teacher/coursemanager')
    },

    getStatusType(status) {
      const types = { 0: 'info', 1: 'success', 2: 'warning' }
      return types[status] || 'info'
    },

    getStatusText(status) {
      const texts = { 0: '草稿', 1: '已发布', 2: '已下架' }
      return texts[status] || '未知'
    },

    formatDuration(seconds) {
      if (!seconds) return ''
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = seconds % 60
      return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
    }
  }
}
</script>

<style scoped>
.chapter-manager {
  padding: 20px;
}

.course-header {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.course-header h2 {
  margin: 0 0 10px 0;
  color: #333;
}

.course-desc {
  color: #666;
  margin: 0 0 15px 0;
  line-height: 1.6;
}

.course-meta {
  display: flex;
  align-items: center;
  gap: 15px;
}

.meta-item {
  color: #888;
  font-size: 14px;
}

.action-bar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.chapter-list {
  min-height: 400px;
}

.chapter-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 20px;
  overflow: hidden;
}

.chapter-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  background: #fff;
}

.chapter-info {
  flex: 1;
}

.chapter-title {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 18px;
}

.chapter-desc {
  color: #666;
  margin: 0 0 10px 0;
  line-height: 1.6;
}

.chapter-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.meta-text {
  color: #888;
  font-size: 12px;
}

.chapter-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.content-preview {
  background: #f8f9fa;
  border-top: 1px solid #e4e7ed;
  padding: 15px 20px;
}

.content-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.content-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background: #fff;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.content-icon {
  color: #409eff;
}

.content-title {
  flex: 1;
  color: #333;
}

.content-duration {
  color: #888;
  font-size: 12px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}
</style>