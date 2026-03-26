<template>
  <div class="consultation-list">
    <div class="header">
      <h2>我的咨询</h2>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        发起咨询
      </el-button>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-bar">
      <el-radio-group v-model="filterStatus" @change="loadConsultations">
        <el-radio-button :label="null">全部</el-radio-button>
        <el-radio-button :label="0">待回复</el-radio-button>
        <el-radio-button :label="1">已回复</el-radio-button>
        <el-radio-button :label="2">已关闭</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 咨询列表 -->
    <div class="consultation-items">
      <el-empty v-if="consultations.length === 0" description="暂无咨询记录" />
      
      <div v-for="item in consultations" :key="item.consultationId" class="consultation-item">
        <div class="item-header">
          <div class="title-row">
            <el-tag v-if="item.priority === 1" type="danger" size="small">紧急</el-tag>
            <el-tag v-if="item.status === 0" type="warning" size="small">待回复</el-tag>
            <el-tag v-else-if="item.status === 1 && item.isRead === 0" type="success" size="small">新回复</el-tag>
            <el-tag v-else-if="item.status === 1" type="info" size="small">已回复</el-tag>
            <el-tag v-else type="info" size="small">已关闭</el-tag>
            <h3>{{ item.questionTitle }}</h3>
          </div>
          <div class="meta-info">
            <span>咨询教师：{{ item.teacherName }}</span>
            <span v-if="item.courseTitle">课程：{{ item.courseTitle }}</span>
            <span>{{ formatTime(item.createTime) }}</span>
          </div>
        </div>

        <div class="item-content">
          <p class="question">{{ item.questionContent }}</p>
          <div v-if="item.replyContent" class="reply">
            <div class="reply-header">
              <el-icon><ChatDotRound /></el-icon>
              <span>教师回复：</span>
              <span class="reply-time">{{ formatTime(item.replyTime) }}</span>
            </div>
            <p class="reply-content">{{ item.replyContent }}</p>
          </div>
        </div>

        <div class="item-actions">
          <el-button v-if="item.status === 1 && item.isRead === 0" type="primary" size="small" @click="markAsRead(item)">
            标记已读
          </el-button>
          <el-button v-if="item.status === 0 || item.status === 1" type="warning" size="small" @click="closeConsultation(item)">
            关闭咨询
          </el-button>
          <el-button type="danger" size="small" @click="deleteConsultation(item)">
            删除
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
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <!-- 创建咨询对话框 -->
    <el-dialog v-model="showCreateDialog" title="发起咨询" width="600px">
      <el-form :model="newConsultation" :rules="rules" ref="consultationForm" label-width="100px">
        <el-form-item label="咨询教师" prop="teacherId">
          <el-select v-model="newConsultation.teacherId" placeholder="请选择教师" @change="onTeacherChange" style="width: 100%">
            <el-option v-for="teacher in teachers" :key="teacher.teachId" :label="teacher.teachName" :value="teacher.teachId" />
          </el-select>
        </el-form-item>

        <el-form-item label="相关课程">
          <el-select v-model="newConsultation.courseId" placeholder="请选择课程（可选）" clearable @change="onCourseChange" style="width: 100%">
            <el-option v-for="course in myCourses" :key="course.courseId" :label="course.courseTitle" :value="course.courseId" />
          </el-select>
        </el-form-item>

        <el-form-item label="问题标题" prop="questionTitle">
          <el-input v-model="newConsultation.questionTitle" placeholder="请输入问题标题" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="问题描述" prop="questionContent">
          <el-input v-model="newConsultation.questionContent" type="textarea" :rows="6" placeholder="请详细描述您的问题" maxlength="1000" show-word-limit />
        </el-form-item>

        <el-form-item label="优先级">
          <el-radio-group v-model="newConsultation.priority">
            <el-radio :label="0">普通</el-radio>
            <el-radio :label="1">紧急</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitConsultation" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getStudentConsultations, createConsultation, markConsultationAsRead, closeConsultation as closeConsultationApi, deleteConsultation as deleteConsultationApi } from '../../api/consultation.js'
import { getteacherlist } from '../../api/teacher.js'
import { getSessionStorage } from '../../utils/common.js'

export default {
  name: 'ConsultationList',
  data() {
    return {
      consultations: [],
      filterStatus: null,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      showCreateDialog: false,
      newConsultation: {
        teacherId: null,
        teacherName: '',
        courseId: null,
        courseTitle: '',
        questionTitle: '',
        questionContent: '',
        priority: 0
      },
      teachers: [],
      myCourses: [],
      submitting: false,
      rules: {
        teacherId: [{ required: true, message: '请选择咨询教师', trigger: 'change' }],
        questionTitle: [{ required: true, message: '请输入问题标题', trigger: 'blur' }],
        questionContent: [{ required: true, message: '请输入问题描述', trigger: 'blur' }]
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
    this.loadTeachers()
  },
  methods: {
    handleSizeChange(size) {
      this.pageSize = size
      this.pageNum = 1
      this.loadConsultations()
    },
    handleCurrentChange(page) {
      this.pageNum = page
      this.loadConsultations()
    },
    async loadConsultations() {
      try {
        const res = await getStudentConsultations(this.currentUser.stuId, this.filterStatus, this.pageNum, this.pageSize)
        if (res.flag) {
          this.consultations = res.result || []
          this.total = res.total || 0
        }
      } catch (e) {
        console.error('加载咨询列表失败', e)
      }
    },

    async loadTeachers() {
      try {
        const res = await getteacherlist({})
        if (res.flag) {
          this.teachers = res.result || []
        }
      } catch (e) {
        console.error('加载教师列表失败', e)
      }
    },

    onTeacherChange(teacherId) {
      const teacher = this.teachers.find(t => t.teachId === teacherId)
      if (teacher) {
        this.newConsultation.teacherName = teacher.teachName
        // 加载该教师的课程
        this.loadTeacherCourses(teacherId)
      }
      // 清空之前选择的课程
      this.newConsultation.courseId = null
      this.newConsultation.courseTitle = ''
    },

    async loadTeacherCourses(teacherId) {
      try {
        // 调用获取教师课程列表的API
        const { getTeacherCoursesList } = await import('../../api/course.js')
        const res = await getTeacherCoursesList(teacherId)
        if (res.flag) {
          // 只显示已发布的课程
          this.myCourses = (res.result || []).filter(c => c.courseStatus === 1)
        } else {
          this.myCourses = []
        }
      } catch (e) {
        console.error('加载教师课程失败', e)
        this.myCourses = []
      }
    },

    onCourseChange(courseId) {
      if (courseId) {
        const course = this.myCourses.find(c => c.courseId === courseId)
        if (course) {
          this.newConsultation.courseTitle = course.courseTitle
        }
      } else {
        this.newConsultation.courseTitle = ''
      }
    },

    async submitConsultation() {
      this.$refs.consultationForm.validate(async (valid) => {
        if (!valid) return

        this.submitting = true
        try {
          const params = {
            studentId: this.currentUser.stuId,
            studentName: this.currentUser.stuName,
            ...this.newConsultation
          }
          const res = await createConsultation(params)
          if (res.flag) {
            this.$message.success('咨询提交成功')
            this.showCreateDialog = false
            this.resetForm()
            this.loadConsultations()
          } else {
            this.$message.error(res.reason || '咨询提交失败')
          }
        } catch (e) {
          console.error('提交咨询失败', e)
          this.$message.error('咨询提交失败')
        } finally {
          this.submitting = false
        }
      })
    },

    resetForm() {
      this.newConsultation = {
        teacherId: null,
        teacherName: '',
        courseId: null,
        courseTitle: '',
        questionTitle: '',
        questionContent: '',
        priority: 0
      }
      this.$refs.consultationForm?.resetFields()
    },

    async markAsRead(item) {
      try {
        const res = await markConsultationAsRead(item.consultationId, this.currentUser.stuId)
        if (res.flag) {
          this.$message.success('已标记为已读')
          item.isRead = 1
        }
      } catch (e) {
        console.error('标记失败', e)
      }
    },

    async closeConsultation(item) {
      try {
        await this.$confirm('确定要关闭此咨询吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await closeConsultationApi(item.consultationId)
        if (res.flag) {
          this.$message.success('咨询已关闭')
          this.loadConsultations()
        }
      } catch (e) {
        if (e !== 'cancel') {
          console.error('关闭失败', e)
        }
      }
    },

    async deleteConsultation(item) {
      try {
        await this.$confirm('确定要删除此咨询吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await deleteConsultationApi(item.consultationId, this.currentUser.stuId, 'student')
        if (res.flag) {
          this.$message.success('删除成功')
          this.loadConsultations()
        }
      } catch (e) {
        if (e !== 'cancel') {
          console.error('删除失败', e)
        }
      }
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
.consultation-list {
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
  background: #f5f7fa;
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

.el-pagination {
  margin-top: 20px;
  justify-content: center;
}
</style>
