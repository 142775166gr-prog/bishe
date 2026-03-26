<template>
  <div class="content-manager">
    <!-- 章节信息 -->
    <div class="chapter-info">
      <h3>{{ chapter.chapterTitle }}</h3>
      <p v-if="chapter.chapterDesc" class="chapter-desc">{{ chapter.chapterDesc }}</p>
    </div>

    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="onAddContent">
        <el-icon><Plus /></el-icon>
        添加内容
      </el-button>
    </div>

    <!-- 内容列表 -->
    <div class="content-list" v-loading="loading">
      <div v-if="contents.length === 0" class="empty-state">
        <el-empty description="暂无内容，点击上方按钮添加内容"></el-empty>
      </div>
      
      <el-table v-else :data="contents" border style="width: 100%">
        <el-table-column prop="sortOrder" label="序号" width="80" align="center">
          <template #default="{ row, $index }">
            {{ $index + 1 }}
          </template>
        </el-table-column>
        
        <el-table-column prop="contentTitle" label="内容标题" min-width="200">
          <template #default="{ row }">
            <div class="content-title-cell">
              <el-icon class="content-type-icon">
                <VideoPlay v-if="row.contentType === 1" />
                <Document v-else-if="row.contentType === 2" />
                <Headphone v-else-if="row.contentType === 3" />
                <Picture v-else />
              </el-icon>
              <span>{{ row.contentTitle }}</span>
              <el-tag v-if="row.isFree === 1" type="success" size="small">试看</el-tag>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="contentType" label="类型" width="100" align="center">
          <template #default="{ row }">
            {{ getContentTypeText(row.contentType) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="progressWeight" label="权重" width="80" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.progressWeight || 1 }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="contentStatus" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.contentStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.contentStatus === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="300" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="onEditContent(row)">
              编辑
            </el-button>
            <el-button 
              v-if="row.contentStatus === 0" 
              type="success" 
              size="small" 
              @click="onPublishContent(row)"
            >
              发布
            </el-button>
            <el-button 
              v-else 
              type="warning" 
              size="small" 
              @click="onUnpublishContent(row)"
            >
              取消发布
            </el-button>
            <el-button type="danger" size="small" @click="onDeleteContent(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 内容编辑对话框 -->
    <el-dialog 
      :title="contentDialogTitle" 
      v-model="contentDialogVisible" 
      width="60%"
    >
      <el-form :model="contentForm" ref="contentFormRef" label-width="120px">
        <el-form-item 
          label="内容标题" 
          prop="contentTitle" 
          :rules="[{ required: true, message: '请输入内容标题', trigger: 'blur' }]"
        >
          <el-input v-model="contentForm.contentTitle" placeholder="请输入内容标题"></el-input>
        </el-form-item>
        
        <el-form-item 
          label="内容类型" 
          prop="contentType" 
          :rules="[{ required: true, message: '请选择内容类型', trigger: 'change' }]"
        >
          <el-radio-group v-model="contentForm.contentType" @change="onContentTypeChange">
            <el-radio :label="1">视频</el-radio>
            <el-radio :label="2">文档</el-radio>
            <el-radio :label="3">音频</el-radio>
            <el-radio :label="4">图片</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item 
          label="内容URL" 
          prop="contentUrl" 
          :rules="[{ required: true, message: '请输入内容URL或上传文件', trigger: 'blur' }]"
        >
          <div class="url-input-group">
            <el-input 
              v-model="contentForm.contentUrl" 
              placeholder="请输入内容文件的URL地址，或使用下方上传功能"
              style="margin-bottom: 10px;"
              @blur="onUrlBlur">
              <template #append>
                <el-button type="success" @click="analyzeUrl" :loading="analyzingUrl">
                  {{ analyzingUrl ? '分析中...' : '分析URL' }}
                </el-button>
              </template>
            </el-input>
            <FileUpload 
              :file-type="getFileTypeForUpload(contentForm.contentType)"
              @success="onFileUploadSuccess"
              @error="onFileUploadError"
            />
            <div class="upload-hint">
              <el-icon><InfoFilled /></el-icon>
              <span>输入URL后，点击"分析URL"按钮或失去焦点时会自动分析文件信息</span>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="内容描述" prop="contentDesc">
          <el-input 
            v-model="contentForm.contentDesc" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入内容描述（可选）"
          ></el-input>
        </el-form-item>
        
        <el-row>
          <el-col :span="8">
            <el-form-item label="进度权重" prop="progressWeight">
              <el-input-number 
                v-model="contentForm.progressWeight" 
                :min="1" 
                :max="100"
                placeholder="进度权重"
                style="width: 100%;"
              ></el-input-number>
              <div class="form-hint">权重越高，在总进度中占比越大</div>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number 
                v-model="contentForm.sortOrder" 
                :min="1" 
                :max="999"
                style="width: 100%;"
              ></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button type="success" @click="contentDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitContentForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { 
  getContentsByChapterId, 
  createContent, 
  updateContent, 
  deleteContent,
  publishContent,
  unpublishContent,
  getFileSize
} from '../api/chapterContent'
import FileUpload from './FileUpload.vue'

export default {
  name: 'ChapterContentManager',
  components: {
    FileUpload
  },
  props: {
    chapter: {
      type: Object,
      required: true
    }
  },
  emits: ['refresh'],
  data() {
    return {
      contents: [],
      loading: false,
      analyzingUrl: false,
      
      // 内容对话框
      contentDialogVisible: false,
      contentDialogTitle: '添加内容',
      contentForm: {
        contentTitle: '',
        contentType: 1,
        contentUrl: '',
        contentDesc: '',
        duration: null,
        fileSize: null,
        progressWeight: 1,
        isFree: 0,
        sortOrder: 1
      },
      fileSizeMB: null
    }
  },
  created() {
    this.loadContents()
  },
  methods: {
    async loadContents() {
      this.loading = true
      try {
        const res = await getContentsByChapterId(this.chapter.chapterId)
        if (res && res.flag) {
          this.contents = res.result || []
        } else {
          this.contents = []
        }
      } catch (e) {
        console.error('获取内容列表失败', e)
        this.contents = []
      } finally {
        this.loading = false
      }
    },

    onAddContent() {
      this.contentDialogTitle = '添加内容'
      this.contentForm = {
        contentTitle: '',
        contentType: 1,
        contentUrl: '',
        contentDesc: '',
        duration: null,
        fileSize: null,
        progressWeight: this.getDefaultWeight(1), // 视频默认权重为5
        isFree: 0,
        sortOrder: this.contents.length + 1
      }
      this.fileSizeMB = null
      this.contentDialogVisible = true
    },

    onEditContent(content) {
      this.contentDialogTitle = '编辑内容'
      this.contentForm = {
        contentId: content.contentId,
        contentTitle: content.contentTitle,
        contentType: content.contentType,
        contentUrl: content.contentUrl,
        contentDesc: content.contentDesc,
        duration: content.duration,
        fileSize: content.fileSize,
        progressWeight: content.progressWeight || this.getDefaultWeight(content.contentType),
        isFree: content.isFree,
        sortOrder: content.sortOrder
      }
      this.fileSizeMB = content.fileSize ? (content.fileSize / 1024 / 1024) : null
      this.contentDialogVisible = true
    },

    async submitContentForm() {
      try {
        await this.$refs.contentFormRef.validate()
        
        const params = {
          ...this.contentForm,
          chapterId: this.chapter.chapterId
        }
        
        console.log('提交的内容参数:', params) // 调试日志
        
        let res
        if (this.contentForm.contentId) {
          res = await updateContent(params)
        } else {
          res = await createContent(params)
        }
        
        if (res && res.flag) {
          this.$message.success(res.reason || '操作成功')
          this.contentDialogVisible = false
          this.loadContents()
          this.$emit('refresh')
        } else {
          this.$message.error(res.reason || '操作失败')
        }
      } catch (e) {
        console.error('保存内容失败', e)
        this.$message.error('保存失败')
      }
    },

    async onPublishContent(content) {
      try {
        await this.$confirm('确定发布该内容吗？', '确认', { type: 'warning' })
        const res = await publishContent(content.contentId)
        if (res && res.flag) {
          this.$message.success('发布成功')
          this.loadContents()
          this.$emit('refresh')
        } else {
          this.$message.error(res.reason || '发布失败')
        }
      } catch (e) {
        // cancel noop
      }
    },

    async onUnpublishContent(content) {
      try {
        await this.$confirm('确定取消发布该内容吗？', '确认', { type: 'warning' })
        const res = await unpublishContent(content.contentId)
        if (res && res.flag) {
          this.$message.success('取消发布成功')
          this.loadContents()
          this.$emit('refresh')
        } else {
          this.$message.error(res.reason || '取消发布失败')
        }
      } catch (e) {
        // cancel noop
      }
    },

    async onDeleteContent(content) {
      try {
        await this.$confirm('确定删除该内容吗？删除后无法恢复。', '确认', { type: 'warning' })
        const res = await deleteContent(content.contentId, this.chapter.chapterId)
        if (res && res.flag) {
          this.$message.success('删除成功')
          this.loadContents()
          this.$emit('refresh')
        } else {
          this.$message.error(res.reason || '删除失败')
        }
      } catch (e) {
        // cancel noop
      }
    },

    onFileSizeChange(value) {
      this.contentForm.fileSize = value ? Math.round(value * 1024 * 1024) : null
    },

    onFileUploadSuccess(fileInfo) {
      this.contentForm.contentUrl = fileInfo.url
      
      // 自动填入文件大小
      if (fileInfo.fileSize) {
        this.contentForm.fileSize = fileInfo.fileSize
        this.fileSizeMB = Math.round(fileInfo.fileSize / 1024 / 1024 * 100) / 100
      }
      
      // 自动填入视频时长
      if (fileInfo.duration && (this.contentForm.contentType === 1 || this.contentForm.contentType === 3)) {
        this.contentForm.duration = fileInfo.duration
      }
      
      // 如果没有填写标题，使用原始文件名
      if (!this.contentForm.contentTitle && fileInfo.originalName) {
        const nameWithoutExt = fileInfo.originalName.substring(0, fileInfo.originalName.lastIndexOf('.'))
        this.contentForm.contentTitle = nameWithoutExt
      }
      
      this.$message.success('文件上传成功，相关信息已自动填入')
    },

    onFileUploadError(error) {
      this.$message.error('文件上传失败：' + error)
    },

    getFileTypeForUpload(contentType) {
      const typeMap = { 1: 'video', 2: 'document', 3: 'audio', 4: 'image' }
      return typeMap[contentType] || 'video'
    },

    getContentTypeText(type) {
      const types = { 1: '视频', 2: '文档', 3: '音频', 4: '图片' }
      return types[type] || '未知'
    },

    formatDuration(seconds) {
      if (!seconds) return ''
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = seconds % 60
      return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
    },

    async analyzeUrl() {
      if (!this.contentForm.contentUrl) {
        this.$message.warning('请先输入URL')
        return
      }

      this.analyzingUrl = true
      console.log('开始分析URL:', this.contentForm.contentUrl)
      
      try {
        // 构建完整的URL
        let fullUrl = this.contentForm.contentUrl
        if (this.contentForm.contentType === 1 && !fullUrl.includes('.')) {
          if (fullUrl.includes('w3schools.com/html/mov_bbb')) {
            fullUrl = 'https://www.w3schools.com/html/mov_bbb.mp4'
            console.log('修正URL为:', fullUrl)
          }
        }

        // 并行获取文件大小和时长
        const promises = []
        
        // 获取文件大小（通过后端）
        promises.push(this.getFileSizeFromBackend(fullUrl))
        
        // 对于视频和音频，获取时长（前端）
        if (this.contentForm.contentType === 1 || this.contentForm.contentType === 3) {
          promises.push(this.getMediaDuration(fullUrl, this.contentForm.contentType))
        } else {
          promises.push(Promise.resolve(null))
        }

        const [fileSize, duration] = await Promise.all(promises)
        
        // 设置文件大小
        if (fileSize && fileSize > 0) {
          this.contentForm.fileSize = fileSize
          this.fileSizeMB = Math.round(fileSize / 1024 / 1024 * 100) / 100
          console.log('设置文件大小:', this.fileSizeMB, 'MB')
        }
        
        // 设置时长
        if (duration && duration > 0) {
          this.contentForm.duration = Math.round(duration)
          console.log('设置真实时长为:', this.contentForm.duration)
        }
        
        // 设置文件名
        if (!this.contentForm.contentTitle) {
          const fileName = this.extractFileNameFromUrl(fullUrl)
          if (fileName) {
            this.contentForm.contentTitle = fileName
          }
        }
        
        // 显示结果
        let message = 'URL分析完成'
        if (fileSize) message += `，文件大小：${this.fileSizeMB}MB`
        if (duration) message += `，时长：${this.formatDuration(this.contentForm.duration)}`
        
        this.$message.success(message)
        
      } catch (error) {
        console.error('URL分析失败', error)
        this.$message.error('URL分析失败：' + (error.message || '未知错误'))
      } finally {
        this.analyzingUrl = false
      }
    },

    // 通过后端获取文件大小
    async getFileSizeFromBackend(url) {
      try {
        console.log('通过后端获取文件大小:', url)
        const result = await getFileSize(url)
        
        if (result && result.flag && result.result && result.result.fileSize) {
          const size = result.result.fileSize
          console.log('文件大小:', size, '字节')
          return size
        }
        
        console.log('无法获取文件大小')
        return null
      } catch (error) {
        console.log('获取文件大小失败:', error)
        return null
      }
    },

    // 获取媒体文件真实时长（修复版本）
    getMediaDuration(url, contentType) {
      return new Promise((resolve) => {
        console.log('创建媒体元素，URL:', url, '类型:', contentType)
        
        const element = contentType === 1 ? document.createElement('video') : document.createElement('audio')
        element.preload = 'metadata'
        element.muted = true
        element.style.display = 'none'
        
        // 添加到DOM中（某些浏览器需要）
        document.body.appendChild(element)
        
        let resolved = false
        
        const cleanup = () => {
          if (element.parentNode) {
            document.body.removeChild(element)
          }
          element.src = ''
          element.load() // 停止加载
        }
        
        const timeout = setTimeout(() => {
          if (!resolved) {
            console.log('获取时长超时')
            resolved = true
            cleanup()
            resolve(null)
          }
        }, 10000) // 10秒超时
        
        element.onloadedmetadata = function() {
          if (!resolved) {
            console.log('元数据加载完成，时长:', element.duration)
            resolved = true
            clearTimeout(timeout)
            const duration = element.duration
            cleanup()
            resolve(duration && !isNaN(duration) && isFinite(duration) ? duration : null)
          }
        }
        
        element.onerror = function(e) {
          if (!resolved) {
            console.log('媒体加载错误:', e)
            resolved = true
            clearTimeout(timeout)
            cleanup()
            resolve(null)
          }
        }
        
        element.onabort = function() {
          if (!resolved) {
            console.log('媒体加载中止')
            resolved = true
            clearTimeout(timeout)
            cleanup()
            resolve(null)
          }
        }
        
        try {
          element.src = url
          console.log('设置媒体源:', url)
        } catch (e) {
          console.log('设置媒体源失败:', e)
          if (!resolved) {
            resolved = true
            clearTimeout(timeout)
            cleanup()
            resolve(null)
          }
        }
      })
    },

    // 从URL提取文件名
    extractFileNameFromUrl(url) {
      try {
        const urlObj = new URL(url)
        let pathname = urlObj.pathname
        let fileName = pathname.substring(pathname.lastIndexOf('/') + 1)
        
        // 移除查询参数
        const queryIndex = fileName.indexOf('?')
        if (queryIndex > 0) {
          fileName = fileName.substring(0, queryIndex)
        }
        
        // 移除文件扩展名作为标题
        const dotIndex = fileName.lastIndexOf('.')
        if (dotIndex > 0) {
          fileName = fileName.substring(0, dotIndex)
        }
        
        return fileName || '未知文件'
      } catch (e) {
        return '未知文件'
      }
    },

    onUrlBlur() {
      // 移除自动分析，避免循环请求
      // 用户需要手动点击"分析URL"按钮
    },

    getDefaultWeight(contentType) {
      const defaultWeights = {
        1: 5, // 视频
        2: 3, // 文档
        3: 4, // 音频
        4: 1  // 图片
      }
      return defaultWeights[contentType] || 1
    },

    onContentTypeChange(newType) {
      // 根据内容类型自动设置默认权重
      this.contentForm.progressWeight = this.getDefaultWeight(newType)
    },

    isValidUrl(string) {
      try {
        new URL(string)
        return true
      } catch (_) {
        return false
      }
    }
  }
}
</script>

<style scoped>
.content-manager {
  padding: 0;
}

.chapter-info {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.chapter-info h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.chapter-desc {
  color: #666;
  margin: 0;
  line-height: 1.6;
}

.action-bar {
  margin-bottom: 20px;
}

.content-list {
  min-height: 300px;
}

.content-title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.content-type-icon {
  color: #409eff;
}

.url-input-group {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 15px;
  background: #fafafa;
}

.upload-hint {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.upload-hint .el-icon {
  color: #409eff;
}

.form-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}
</style>