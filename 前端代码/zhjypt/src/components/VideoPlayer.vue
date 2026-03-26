<template>
  <div class="video-player">
    <div class="video-container" ref="videoContainer" @mousemove="onMouseMove">
      <video 
        ref="videoElement"
        :src="videoUrl"
        :poster="poster"
        :controls="!showCustomControls"
        preload="metadata"
        @loadedmetadata="onLoadedMetadata"
        @timeupdate="onTimeUpdate"
        @ended="onVideoEnded"
        @play="onPlay"
        @pause="onPause"
        @error="onError"
        @loadstart="onLoadStart"
        @canplay="onCanPlay"
      >
        您的浏览器不支持视频播放
      </video>
      
      <!-- 视频加载错误提示 -->
      <div v-if="showVideoError" class="video-error">
        <div class="error-content">
          <el-icon class="error-icon"><Warning /></el-icon>
          <h4>视频无法播放</h4>
          <p>当前视频可能存在以下问题：</p>
          <ul>
            <li>视频地址无效或已失效</li>
            <li>网络连接问题</li>
            <li>视频格式不支持</li>
            <li>服务器限制访问</li>
          </ul>
          <div class="error-actions">
            <el-button @click="retryLoad" type="primary" size="small">重试加载</el-button>
            <el-button @click="openInNewTab" size="small">在新窗口打开</el-button>
          </div>
          <p class="video-url">视频地址：{{ videoUrl }}</p>
        </div>
      </div>
      
      <!-- 自定义控制栏（可选） -->
      <div class="custom-controls" v-if="showCustomControls" :class="{ visible: controlsVisible }">
        <div class="progress-bar">
          <div class="progress-bg" @click="seekTo">
            <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
          </div>
        </div>
        
        <div class="controls-row">
          <button @click="togglePlay" class="play-btn">
            <el-icon v-if="isPlaying"><VideoPause /></el-icon>
            <el-icon v-else><VideoPlay /></el-icon>
          </button>
          
          <span class="time-display">
            {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
          </span>
          
          <div class="volume-control">
            <el-icon @click="toggleMute"><Mute v-if="isMuted" /><Microphone v-else /></el-icon>
            <input 
              type="range" 
              min="0" 
              max="1" 
              step="0.1" 
              v-model="volume" 
              @input="setVolume"
              class="volume-slider"
            />
          </div>
          
          <div class="speed-control">
            <el-select v-model="playbackRate" @change="setPlaybackRate" size="small">
              <el-option label="0.5x" :value="0.5"></el-option>
              <el-option label="0.75x" :value="0.75"></el-option>
              <el-option label="1.0x" :value="1.0"></el-option>
              <el-option label="1.25x" :value="1.25"></el-option>
              <el-option label="1.5x" :value="1.5"></el-option>
              <el-option label="2.0x" :value="2.0"></el-option>
            </el-select>
          </div>
          
          <button @click="toggleFullscreen" class="fullscreen-btn">
            <el-icon><FullScreen /></el-icon>
          </button>
        </div>
      </div>
    </div>
    
    <!-- 视频信息 -->
    <div class="video-info" v-if="contentInfo">
      <h3>{{ contentInfo.contentTitle }}</h3>
      <p v-if="contentInfo.contentDesc" class="video-desc">{{ contentInfo.contentDesc }}</p>
      <div class="video-meta">
        <span>时长: {{ formatTime(contentInfo.duration) }}</span>
        <el-tag v-if="contentInfo.isFree === 1" type="success" size="small">免费试看</el-tag>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'VideoPlayer',
  props: {
    videoUrl: {
      type: String,
      required: true
    },
    poster: {
      type: String,
      default: ''
    },
    contentInfo: {
      type: Object,
      default: () => ({})
    },
    showCustomControls: {
      type: Boolean,
      default: false
    },
    autoPlay: {
      type: Boolean,
      default: false
    }
  },
  emits: ['progress-update', 'video-ended', 'play', 'pause'],
  data() {
    return {
      isPlaying: false,
      currentTime: 0,
      duration: 0,
      volume: 1,
      isMuted: false,
      playbackRate: 1.0,
      progressPercent: 0,
      showVideoError: false,
      controlsVisible: true,
      controlsTimer: null
    }
  },
  mounted() {
    if (this.autoPlay) {
      this.$nextTick(() => {
        this.play()
      })
    }
  },
  methods: {
    onLoadedMetadata() {
      this.duration = this.$refs.videoElement.duration
      this.volume = this.$refs.videoElement.volume
    },
    
    onLoadStart() {
      console.log('开始加载视频:', this.videoUrl)
      this.showVideoError = false
    },
    
    onCanPlay() {
      console.log('视频可以播放')
      this.showVideoError = false
    },
    
    onTimeUpdate() {
      this.currentTime = this.$refs.videoElement.currentTime
      this.progressPercent = (this.currentTime / this.duration) * 100
      
      // 发送进度更新事件
      this.$emit('progress-update', {
        currentTime: this.currentTime,
        duration: this.duration,
        progress: this.progressPercent
      })
    },
    
    onVideoEnded() {
      this.isPlaying = false
      this.$emit('video-ended')
    },
    
    onPlay() {
      this.isPlaying = true
      this.showControls()
      this.$emit('play')
    },
    
    onPause() {
      this.isPlaying = false
      this.showControls()
      this.$emit('pause')
    },
    
    onError(e) {
      console.error('视频播放错误:', e)
      console.error('视频URL:', this.videoUrl)
      
      // 显示更详细的错误信息
      const errorMessages = {
        1: '视频加载被中止',
        2: '网络错误，无法加载视频',
        3: '视频解码错误',
        4: '视频格式不支持或视频URL无效'
      }
      
      const errorCode = e.target?.error?.code || 4
      const errorMessage = errorMessages[errorCode] || '视频播放失败'
      
      this.$message.error(`${errorMessage}。请检查视频地址是否正确或尝试其他视频。`)
      
      // 尝试提供备用解决方案
      this.showVideoError = true
    },
    
    togglePlay() {
      if (this.isPlaying) {
        this.pause()
      } else {
        this.play()
      }
    },
    
    play() {
      this.$refs.videoElement.play().catch(e => {
        console.error('播放失败:', e)
        this.$message.error('播放失败')
      })
    },
    
    pause() {
      this.$refs.videoElement.pause()
    },
    
    seekTo(event) {
      const rect = event.target.getBoundingClientRect()
      const percent = (event.clientX - rect.left) / rect.width
      const seekTime = percent * this.duration
      this.$refs.videoElement.currentTime = seekTime
    },
    
    setVolume() {
      this.$refs.videoElement.volume = this.volume
      this.isMuted = this.volume === 0
    },
    
    toggleMute() {
      if (this.isMuted) {
        this.volume = 0.5
        this.$refs.videoElement.volume = 0.5
        this.isMuted = false
      } else {
        this.volume = 0
        this.$refs.videoElement.volume = 0
        this.isMuted = true
      }
    },
    
    setPlaybackRate() {
      this.$refs.videoElement.playbackRate = this.playbackRate
    },
    
    toggleFullscreen() {
      const container = this.$refs.videoContainer
      if (document.fullscreenElement) {
        document.exitFullscreen()
      } else {
        container.requestFullscreen().catch(e => {
          console.error('全屏失败:', e)
        })
      }
    },
    
    retryLoad() {
      this.showVideoError = false
      this.$refs.videoElement.load()
    },
    
    openInNewTab() {
      if (this.videoUrl) {
        window.open(this.videoUrl, '_blank')
      }
    },
    
    showControls() {
      this.controlsVisible = true
      if (this.controlsTimer) {
        clearTimeout(this.controlsTimer)
      }
      if (this.isPlaying) {
        this.controlsTimer = setTimeout(() => {
          this.controlsVisible = false
        }, 3000) // 3秒后自动隐藏
      }
    },
    
    onMouseMove() {
      if (this.showCustomControls) {
        this.showControls()
      }
    },
    
    formatTime(seconds) {
      if (!seconds || isNaN(seconds)) return '00:00'
      const mins = Math.floor(seconds / 60)
      const secs = Math.floor(seconds % 60)
      return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    },
    
    formatFileSize(bytes) {
      if (!bytes) return ''
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(1024))
      return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i]
    }
  }
}
</script>

<style scoped>
.video-player {
  width: 100%;
  background: white;
  overflow: hidden;
}

.video-container {
  position: relative;
  width: 100%;
  background: #000;
}

video {
  width: 100%;
  height: auto;
  display: block;
}

.custom-controls {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0,0,0,0.8));
  color: white;
  padding: 20px 15px 15px;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.custom-controls.visible {
  opacity: 1;
  pointer-events: auto;
}

.video-container:hover .custom-controls {
  opacity: 1;
  pointer-events: auto;
}

.progress-bar {
  margin-bottom: 10px;
}

.progress-bg {
  height: 6px;
  background: rgba(255,255,255,0.3);
  border-radius: 3px;
  cursor: pointer;
  position: relative;
}

.progress-bg:hover {
  height: 8px;
  margin-top: -1px;
}

.progress-fill {
  height: 100%;
  background: #409eff;
  border-radius: 3px;
  transition: width 0.1s;
}

.controls-row {
  display: flex;
  align-items: center;
  gap: 15px;
}

.play-btn, .fullscreen-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.play-btn:hover, .fullscreen-btn:hover {
  background: rgba(255,255,255,0.2);
}

.time-display {
  font-size: 14px;
  color: white;
}

.volume-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.volume-slider {
  width: 60px;
}

.speed-control {
  margin-left: auto;
}

.video-info {
  padding: 20px;
  background: white;
}

.video-info h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.video-desc {
  color: #666;
  margin: 0 0 15px 0;
  line-height: 1.6;
}

.video-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #888;
  font-size: 14px;
}

.video-error {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.error-content {
  text-align: center;
  max-width: 500px;
  padding: 30px;
}

.error-icon {
  font-size: 48px;
  color: #f56c6c;
  margin-bottom: 15px;
}

.error-content h4 {
  margin: 0 0 15px 0;
  color: white;
}

.error-content ul {
  text-align: left;
  margin: 15px 0;
  padding-left: 20px;
}

.error-content li {
  margin: 5px 0;
  color: #ccc;
}

.error-actions {
  margin: 20px 0;
}

.video-url {
  font-size: 12px;
  color: #999;
  word-break: break-all;
  margin-top: 15px;
}
</style>