<template>
  <div class="file-upload">
    <div class="upload-label">
      <span class="label-text">{{ getUploadLabel() }}</span>
    </div>
    
    <el-upload
      class="upload-demo"
      :action="uploadUrl"
      :on-success="handleSuccess"
      :on-error="handleError"
      :before-upload="beforeUpload"
      :show-file-list="false"
      :disabled="uploading"
    >
      <el-button type="primary" :loading="uploading">
        {{ uploading ? '上传中...' : '选择文件' }}
      </el-button>
    </el-upload>
    
    <div v-if="fileUrl" class="upload-result">
      <el-input v-model="fileUrl" readonly>
        <template #append>
          <el-button type="success" @click="copyUrl">复制</el-button>
        </template>
      </el-input>
    </div>
    
    <div class="upload-tips">
      <p>支持的文件类型：{{ acceptTypes }}</p>
      <p>文件大小限制：{{ maxSizeMB }}MB</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FileUpload',
  props: {
    fileType: {
      type: String,
      default: 'all' // video, audio, document, image, all
    },
    maxSize: {
      type: Number,
      default: 100 // MB
    }
  },
  emits: ['success', 'error'],
  data() {
    return {
      uploading: false,
      fileUrl: '',
      uploadUrl: this.getUploadUrl() // 根据文件类型动态设置上传URL
    }
  },
  computed: {
    acceptTypes() {
      const types = {
        video: 'mp4, avi, mov, wmv',
        audio: 'mp3, wav, aac, flac',
        document: 'pdf, doc, docx, ppt, pptx',
        image: 'jpg, jpeg, png, gif, bmp',
        all: '视频、音频、文档、图片'
      }
      return types[this.fileType] || types.all
    },
    
    maxSizeMB() {
      return this.maxSize
    },
    
    acceptExtensions() {
      const extensions = {
        video: ['.mp4', '.avi', '.mov', '.wmv'],
        audio: ['.mp3', '.wav', '.aac', '.flac'],
        document: ['.pdf', '.doc', '.docx', '.ppt', '.pptx'],
        image: ['.jpg', '.jpeg', '.png', '.gif', '.bmp'],
        all: ['.mp4', '.avi', '.mov', '.wmv', '.mp3', '.wav', '.aac', '.flac', '.pdf', '.doc', '.docx', '.ppt', '.pptx', '.jpg', '.jpeg', '.png', '.gif', '.bmp']
      }
      return extensions[this.fileType] || extensions.all
    }
  },
  methods: {
    getUploadLabel() {
      const labels = {
        video: '本地视频上传',
        audio: '本地音频上传',
        document: '本地文档上传',
        image: '本地图片上传',
        all: '本地文件上传'
      }
      return labels[this.fileType] || labels.all
    },

    getUploadUrl() {
      const baseUrl = 'http://localhost:9999/upload'
      switch (this.fileType) {
        case 'video':
          return `${baseUrl}/video`
        case 'audio':
          return `${baseUrl}/video` // 音频也用video接口
        case 'document':
          return `${baseUrl}/document`
        case 'image':
          return `${baseUrl}/image`
        default:
          return `${baseUrl}/video`
      }
    },

    beforeUpload(file) {
      // 检查后端服务是否可用
      this.checkBackendService()
      
      // 检查文件类型
      const fileName = file.name.toLowerCase()
      const isValidType = this.acceptExtensions.some(ext => fileName.endsWith(ext))
      
      if (!isValidType) {
        this.$message.error(`不支持的文件类型，请选择 ${this.acceptTypes} 格式的文件`)
        return false
      }
      
      // 检查文件大小
      const isValidSize = file.size / 1024 / 1024 < this.maxSize
      if (!isValidSize) {
        this.$message.error(`文件大小不能超过 ${this.maxSize}MB`)
        return false
      }
      
      this.uploading = true
      this.$message.info('开始上传文件...')
      return true
    },
    
    // 检查后端服务
    async checkBackendService() {
      try {
        const response = await fetch('http://localhost:9999/upload/video', {
          method: 'OPTIONS'
        })
        console.log('后端服务检查:', response.status)
      } catch (e) {
        console.warn('后端服务可能未启动:', e.message)
        this.$message.warning('提示：请确保后端服务已启动（端口9999）')
      }
    },
    
    handleSuccess(response, file) {
      this.uploading = false
      
      // 处理后端返回的数据
      if (response && response.flag) {
        // 构建完整的文件URL
        this.fileUrl = `http://localhost:9999${response.result.url}`
        this.$message.success('上传成功')
        this.$emit('success', {
          url: this.fileUrl,
          fileName: file.name,
          fileSize: response.result.fileSize || file.size,
          duration: response.result.duration || null, // 后端返回的真实时长
          originalName: response.result.originalName || file.name
        })
      } else {
        this.$message.error(response.reason || '上传失败')
        this.$emit('error', response.reason || '上传失败')
      }
    },
    
    handleError(error) {
      this.uploading = false
      console.error('上传失败', error)
      
      // 详细的错误处理
      let errorMessage = '上传失败，请重试'
      
      if (error.message) {
        if (error.message.includes('ERR_CONNECTION_RESET')) {
          errorMessage = '连接被重置，请检查后端服务是否启动'
        } else if (error.message.includes('ERR_CONNECTION_REFUSED')) {
          errorMessage = '连接被拒绝，请检查后端服务地址和端口'
        } else if (error.message.includes('timeout')) {
          errorMessage = '上传超时，请检查网络连接或文件大小'
        } else {
          errorMessage = `上传失败：${error.message}`
        }
      }
      
      this.$message.error(errorMessage)
      this.$emit('error', errorMessage)
    },
    
    copyUrl() {
      if (this.fileUrl) {
        navigator.clipboard.writeText(this.fileUrl).then(() => {
          this.$message.success('URL已复制到剪贴板')
        }).catch(() => {
          // 降级方案
          const textArea = document.createElement('textarea')
          textArea.value = this.fileUrl
          document.body.appendChild(textArea)
          textArea.select()
          document.execCommand('copy')
          document.body.removeChild(textArea)
          this.$message.success('URL已复制到剪贴板')
        })
      }
    },
    
    reset() {
      this.fileUrl = ''
      this.uploading = false
    }
  }
}
</script>

<style scoped>
.file-upload {
  width: 100%;
}

.upload-label {
  margin-bottom: 10px;
}

.label-text {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.upload-result {
  margin-top: 15px;
}

.upload-tips {
  margin-top: 10px;
  color: #666;
  font-size: 12px;
}

.upload-tips p {
  margin: 5px 0;
}
</style>