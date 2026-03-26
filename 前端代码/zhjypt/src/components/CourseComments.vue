<template>
  <div class="course-comments">
    <div class="comments-header">
      <h3>课程评论 ({{ total }})</h3>
    </div>

    <!-- 发表评论区域 -->
    <div class="comment-input-area" v-if="currentUser">
      <el-input
        v-model="newComment"
        type="textarea"
        :rows="3"
        placeholder="发表你的评论..."
        maxlength="500"
        show-word-limit
      />
      <div class="input-actions">
        <el-button type="primary" @click="submitComment" :disabled="!newComment.trim()">
          发表评论
        </el-button>
      </div>
    </div>
    <div v-else class="login-tip">
      <el-alert title="请登录后发表评论" type="info" :closable="false" />
    </div>

    <!-- 评论列表 -->
    <div class="comments-list" v-loading="loading">
      <div v-if="comments.length === 0" class="no-comments">
        <el-empty description="暂无评论，快来发表第一条评论吧！" />
      </div>

      <div v-for="comment in comments" :key="comment.commentId" class="comment-item">
        <!-- 主评论 -->
        <div class="comment-main">
          <div class="comment-avatar">
            <el-avatar :size="40" :src="getAvatarUrl(comment.userAvatar)">{{ comment.userName?.charAt(0) }}</el-avatar>
          </div>
          <div class="comment-content">
            <div class="comment-header">
              <span class="user-name">{{ comment.userName }}</span>
              <el-tag size="small" :type="comment.userType === 'teacher' ? 'warning' : 'info'">
                {{ comment.userType === 'teacher' ? '教师' : '学生' }}
              </el-tag>
              <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <div class="comment-text">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button type="primary" link size="small" @click="showReplyInput(comment)">
                回复
              </el-button>
              <el-button 
                v-if="canDelete(comment)" 
                type="danger" 
                link 
                size="small" 
                @click="handleDelete(comment)"
              >
                删除
              </el-button>
            </div>

            <!-- 回复输入框 -->
            <div v-if="replyingTo === comment.commentId" class="reply-input">
              <el-input
                v-model="replyContent"
                type="textarea"
                :rows="2"
                :placeholder="`回复 ${comment.userName}...`"
                maxlength="500"
              />
              <div class="reply-actions">
                <el-button size="small" @click="cancelReply">取消</el-button>
                <el-button type="primary" size="small" @click="submitReply(comment)" :disabled="!replyContent.trim()">
                  回复
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 回复列表 -->
        <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
          <div v-for="reply in comment.replies" :key="reply.commentId" class="reply-item">
            <div class="comment-avatar">
              <el-avatar :size="32" :src="getAvatarUrl(reply.userAvatar)">{{ reply.userName?.charAt(0) }}</el-avatar>
            </div>
            <div class="comment-content">
              <div class="comment-header">
                <span class="user-name">{{ reply.userName }}</span>
                <el-tag size="small" :type="reply.userType === 'teacher' ? 'warning' : 'info'">
                  {{ reply.userType === 'teacher' ? '教师' : '学生' }}
                </el-tag>
                <span v-if="reply.replyToName" class="reply-to">
                  回复 <span class="reply-to-name">{{ reply.replyToName }}</span>
                </span>
                <span class="comment-time">{{ formatTime(reply.createTime) }}</span>
              </div>
              <div class="comment-text">{{ reply.content }}</div>
              <div class="comment-actions">
                <el-button type="primary" link size="small" @click="showReplyInput(reply, comment)">
                  回复
                </el-button>
                <el-button 
                  v-if="canDelete(reply)" 
                  type="danger" 
                  link 
                  size="small" 
                  @click="handleDelete(reply)"
                >
                  删除
                </el-button>
              </div>
              
              <!-- 子评论的回复输入框 -->
              <div v-if="replyingTo === reply.commentId" class="reply-input">
                <el-input
                  v-model="replyContent"
                  type="textarea"
                  :rows="2"
                  :placeholder="`回复 ${reply.userName}...`"
                  maxlength="500"
                />
                <div class="reply-actions">
                  <el-button size="small" @click="cancelReply">取消</el-button>
                  <el-button type="primary" size="small" @click="submitReply(reply, comment)" :disabled="!replyContent.trim()">
                    回复
                  </el-button>
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
import { getCommentList, addComment, replyComment, deleteComment, adminDeleteComment } from '../api/courseComment'
import { getSessionStorage } from '../utils/common'

export default {
  name: 'CourseComments',
  props: {
    courseId: {
      type: [Number, String],
      required: true
    }
  },
  data() {
    return {
      comments: [],
      total: 0,
      loading: false,
      newComment: '',
      replyContent: '',
      replyingTo: null,
      replyingToParent: null,
      replyingToComment: null,
      currentUser: null,
      userType: ''
    }
  },
  created() {
    this.initUser()
    this.loadComments()
  },
  watch: {
    courseId() {
      this.loadComments()
    }
  },
  methods: {
    initUser() {
      const curuser = getSessionStorage('curuser')
      if (curuser && curuser.info) {
        this.currentUser = curuser.info
        // 登录时存储的是role，需要转换为userType
        const role = curuser.role
        if (role === 'student') {
          this.userType = 'student'
        } else if (role === 'teacher') {
          this.userType = 'teacher'
        } else if (role === 'admin') {
          this.userType = 'admin'
        }
      }
    },

    async loadComments() {
      this.loading = true
      try {
        const res = await getCommentList(this.courseId)
        if (res && res.flag) {
          this.comments = res.result || []
          this.total = res.total || 0
        }
      } catch (e) {
        console.error('加载评论失败', e)
      } finally {
        this.loading = false
      }
    },

    async submitComment() {
      if (!this.newComment.trim()) return
      
      const userId = this.getUserId()
      if (!userId) {
        this.$message.warning('请先登录')
        return
      }

      try {
        const data = {
          courseId: this.courseId,
          userId: userId,
          userType: this.userType,
          userName: this.currentUser.stuName || this.currentUser.teaName || '用户',
          content: this.newComment.trim()
        }
        const res = await addComment(data)
        if (res && res.flag) {
          this.$message.success('评论成功')
          this.newComment = ''
          this.loadComments()
        } else {
          this.$message.error(res?.message || '评论失败')
        }
      } catch (e) {
        this.$message.error('评论失败')
      }
    },

    showReplyInput(comment, parentComment = null) {
      // 记录要显示输入框的评论ID
      this.replyingTo = comment.commentId
      // 记录父评论（用于提交时设置parentId）
      this.replyingToParent = parentComment || comment
      // 记录被回复的评论（用于显示"回复xxx"）
      this.replyingToComment = comment
      this.replyContent = ''
    },

    cancelReply() {
      this.replyingTo = null
      this.replyingToParent = null
      this.replyingToComment = null
      this.replyContent = ''
    },

    async submitReply(targetComment, parentComment = null) {
      if (!this.replyContent.trim()) return
      
      const userId = this.getUserId()
      if (!userId) {
        this.$message.warning('请先登录')
        return
      }

      try {
        // parentId 是顶级评论的ID
        const topParentId = parentComment ? parentComment.commentId : targetComment.commentId
        const data = {
          courseId: this.courseId,
          parentId: topParentId,
          replyToId: targetComment.commentId,
          userId: userId,
          userType: this.userType,
          userName: this.currentUser.stuName || this.currentUser.teaName || '用户',
          content: this.replyContent.trim(),
          replyToName: targetComment.userName
        }
        const res = await replyComment(data)
        if (res && res.flag) {
          this.$message.success('回复成功')
          this.cancelReply()
          this.loadComments()
        } else {
          this.$message.error(res?.message || '回复失败')
        }
      } catch (e) {
        this.$message.error('回复失败')
      }
    },

    getUserId() {
      if (!this.currentUser) return null
      return this.currentUser.stuId || this.currentUser.teaId || this.currentUser.adminId
    },

    canDelete(comment) {
      if (!this.currentUser) return false
      // 管理员可以删除任何评论
      if (this.userType === 'admin') return true
      // 用户只能删除自己的评论
      const userId = this.getUserId()
      return comment.userId === userId && comment.userType === this.userType
    },

    async handleDelete(comment) {
      try {
        await this.$confirm('确定要删除这条评论吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        let res
        if (this.userType === 'admin') {
          res = await adminDeleteComment(comment.commentId)
        } else {
          res = await deleteComment(comment.commentId, this.getUserId(), this.userType)
        }
        
        if (res && res.flag) {
          this.$message.success('删除成功')
          this.loadComments()
        } else {
          this.$message.error(res?.message || '删除失败')
        }
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },

    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      
      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
      if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
      if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
      
      return date.toLocaleDateString()
    },

    getAvatarUrl(avatar) {
      if (!avatar) return ''
      if (avatar.startsWith('http')) return avatar
      return 'http://localhost:9999' + avatar
    }
  }
}
</script>


<style scoped>
.course-comments {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}

.comments-header h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.comment-input-area {
  margin-bottom: 20px;
}

.input-actions {
  margin-top: 10px;
  text-align: right;
}

.login-tip {
  margin-bottom: 20px;
}

.comments-list {
  min-height: 100px;
}

.comment-item {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-main {
  display: flex;
  gap: 12px;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.user-name {
  font-weight: 600;
  color: #333;
}

.reply-to {
  color: #999;
  font-size: 13px;
}

.reply-to-name {
  color: #409eff;
}

.comment-time {
  color: #999;
  font-size: 12px;
  margin-left: auto;
}

.comment-text {
  color: #666;
  line-height: 1.6;
  word-break: break-word;
}

.comment-actions {
  margin-top: 8px;
}

.reply-input {
  margin-top: 12px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 6px;
}

.reply-actions {
  margin-top: 8px;
  text-align: right;
}

.replies-list {
  margin-left: 52px;
  margin-top: 15px;
  padding-left: 15px;
  border-left: 2px solid #e8e8e8;
}

.reply-item {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px dashed #f0f0f0;
}

.reply-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.no-comments {
  padding: 40px 0;
}
</style>
