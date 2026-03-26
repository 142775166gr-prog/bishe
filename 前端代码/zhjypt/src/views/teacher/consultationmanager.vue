<template>
  <div class="consultation-manager">
    <div class="header">
      <h2>学生咨询管理</h2>
      <el-badge :value="unrepliedCount" :hidden="unrepliedCount === 0" class="badge">
        <el-button type="primary" @click="filterStatus = 0; loadConsultations()">
          待回复咨询
        </el-button>
      </el-badge>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-bar">
      <el-radio-group v-model="filterStatus" @change="loadConsultations">
        <el-radio-button :label="null">全部</el-radio-button>
        <el-radio-button :label="0">待回复</el-radio-button>
        <el-radio-button :label="1">已回复</el-radio-button>
        <el-radio-button :label="2">已关闭</el-radio-button>
      </el-radio-group>

      <el-select v-model="filterCourse" placeholder="筛选课程" clearable @change="loadConsultations" style="width: 200px; margin-left: 20px">
        <el-option v-for="course in myCourses" :key="course.courseId" :label="course.courseTitle" :value="course.courseId" />
      </el-select>
    </div>

    <!-- 咨询列表 -->
    <div class="consultation-items">
      <el-empty v-if="consultations.length === 0" description="暂无咨询记录" />
      
      <div v-for="item in consultations" :key="item.consultationId" class="consultation-item" :class="{ priority: item.priority === 1 }">
        <div class="item-header">
          <div class="title-row">
            <el-tag v-if="item.priority === 1" type="danger" size="small">紧急</el-tag>
            <el-tag v-if="item.status === 0" type="warning" size="small">待回复</el-tag>
            <el-tag v-else-if="item.status === 1" type="success" size="small">已回复</el-tag>
            <el-tag v-else type="info" size="small">已关闭</el-tag>
            <h3>{{ item.questionTitle }}</h3>
          </div>
          <div class="meta-info">
            <span>学生：{{ item.studentName }}</span>
            <span v-if="item.courseTitle">课程：{{ item.courseTitle }}</span>
            <span>{{ formatTime(item.createTime) }}</span>
          </div>
        </div>

        <div class="item-content">
          <p class="question">{{ item.questionContent }}</p>
          <div v-if="item.replyContent" class="reply">
            <div class="reply-header">
              <el-icon><ChatDotRound /></el-icon>
              <span>我的回复：</span>
              <span class="reply-time">{{ formatTime(item.replyTime) }}</span>
            </div>
            <p class="reply-content">{{ item.replyContent }}</p>
          </div>
        </div>

        <div class="item-actions">
          <el-button v-if="item.status === 0" type="primary" size="small" @click="showReplyDialog(item)">
            回复
          </el-button>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <el-pagination
      v-if="total > 0"
      :current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="(size) => { pageSize = size; pageNum = 1; loadConsultations() }"
      @current-change="(page) => { pageNum = page; loadConsultations() }"
    />

    <!-- 回复对话框 -->
    <el-dialog v-model="showReply" title="回复学生咨询" width="600px">
      <div class="reply-dialog">
        <div class="question-info">
          <h4>学生问题：</h4>
          <p>{{ currentItem?.questionContent }}</p>
        </div>
        <el-form :model="replyForm" :rules="replyRules" ref="replyFormRef" label-width="100px">
          <el-form-item label="回复内容" prop="replyContent">
            <el-input v-model="replyForm.replyContent" type="textarea" :rows="8" placeholder="请输入回复内容" maxlength="2000" show-word-limit />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="showReply = false">取消</el-button>
        <el-button type="primary" @click="submitReply" :loading="replying">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getTeacherConsultations, replyConsultation, getTeacherUnrepliedCount } from '../../api/consultation.js'
import { getTeacherCoursesList } from '../../api/course.js'
import { getSessionStorage } from '../../utils/common.js'

export default {
  name: 'ConsultationManager',
  data() {
    return {
      consultations: [],
      filterStatus: null,
      filterCourse: null,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      myCourses: [],
      unrepliedCount: 0,
      showReply: false,
      currentItem: null,
      replyForm: {
        replyContent: ''
      },
      replying: false,
      replyRules: {
        replyContent: [{ required: true, message: '请输入回复内容', trigger: 'blur' }]
      }
    }
  },
  computed: {
    currentUser() {
      const user = getSessionStorage('curuser')
      return user ? user.info : null
    }
  },
  mounted() {
    this.loadConsultations()
    this.loadUnrepliedCount()
    this.loadMyCourses()
  },
  methods: {
    async loadConsultations() {
      try {
        const res = await getTeacherConsultations(this.currentUser.teachId, this.filterStatus, this.filterCourse, this.pageNum, this.pageSize)
        if (res.flag) {
          this.consultations = res.result || []
          this.total = res.total || 0
        }
      } catch (e) {
        console.error('加载咨询列表失败', e)
      }
    },

    async loadUnrepliedCount() {
      try {
        const res = await getTeacherUnrepliedCount(this.currentUser.teachId)
        if (res.flag) {
          this.unrepliedCount = res.result || 0
        }
      } catch (e) {
        console.error('加载未回复数量失败', e)
      }
    },

    async loadMyCourses() {
      if (!this.currentUser?.teachId) {
        this.myCourses = []
        return
      }

      try {
        const res = await getTeacherCoursesList(this.currentUser.teachId)
        if (res && res.flag) {
          this.myCourses = res.result || []
        } else {
          this.myCourses = []
        }
      } catch (e) {
        console.error('加载教师课程列表失败', e)
        this.myCourses = []
      }
    },

    showReplyDialog(item) {
      this.currentItem = item
      this.replyForm.replyContent = ''
      this.showReply = true
    },

    async submitReply() {
      this.$refs.replyFormRef.validate(async (valid) => {
        if (!valid) return

        this.replying = true
        try {
          const res = await replyConsultation({
            consultationId: this.currentItem.consultationId,
            replyContent: this.replyForm.replyContent,
            teacherId: this.currentUser.teachId
          })
          if (res.flag) {
            this.$message.success('回复成功')
            this.showReply = false
            this.loadConsultations()
            this.loadUnrepliedCount()
          } else {
            this.$message.error(res.reason || '回复失败')
          }
        } catch (e) {
          console.error('回复失败', e)
          this.$message.error('回复失败')
        } finally {
          this.replying = false
        }
      })
    },

    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.consultation-manager {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px 16px;
  border-radius: 14px;
  background: linear-gradient(90deg, #3b82f6 0%, #22c55e 100%);
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.12);
  color: #fff;
}

.header h2 {
  margin: 0;
  color: #fff;
  font-size: 18px;
  font-weight: 700;
}

.badge {
  margin-left: 20px;
}

.filter-bar {
  margin-bottom: 20px;
}

.consultation-items {
  min-height: 400px;
}

.consultation-item {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
}

.consultation-item.priority {
  border-left: 4px solid #f56c6c;
}

.item-header {
  margin-bottom: 15px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.title-row h3 {
  margin: 0;
  font-size: 18px;
}

.meta-info {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 14px;
}

.item-content {
  margin-bottom: 15px;
}

.question {
  color: #333;
  line-height: 1.6;
  margin-bottom: 15px;
}

.reply {
  background: #e8f4ff;
  border-left: 3px solid #409eff;
  padding: 15px;
  border-radius: 4px;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #409eff;
  font-weight: 500;
  margin-bottom: 10px;
}

.reply-time {
  color: #999;
  font-size: 12px;
  margin-left: auto;
}

.reply-content {
  color: #333;
  line-height: 1.6;
  margin: 0;
}

.item-actions {
  display: flex;
  gap: 10px;
}

.reply-dialog .question-info {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.reply-dialog .question-info h4 {
  margin: 0 0 10px 0;
  color: #666;
}

.reply-dialog .question-info p {
  margin: 0;
  color: #333;
  line-height: 1.6;
}

.el-pagination {
  margin-top: 20px;
  justify-content: center;
}
</style>
