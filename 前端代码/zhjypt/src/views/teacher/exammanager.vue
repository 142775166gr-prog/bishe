<template>
  <div class="exam-manager">
    <div class="header">
      <h2>{{ courseTitle }} - 考试管理</h2>
      <el-button type="primary" @click="onAddExam">
        <el-icon><Plus /></el-icon>
        创建考试
      </el-button>
    </div>

    <!-- 考试列表 -->
    <div class="exam-list" v-loading="loading">
      <el-table :data="exams" border style="width: 100%">
        <el-table-column prop="examTitle" label="考试标题" min-width="200">
          <template #default="{ row }">
            <div class="exam-title-cell">
              <span>{{ row.examTitle }}</span>
              <el-tag :type="row.examType === 1 ? 'success' : 'primary'" size="small">
                {{ row.examType === 1 ? '练习' : '考试' }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="totalScore" label="总分" width="80" align="center" />
        <el-table-column prop="passScore" label="及格分" width="80" align="center" />
        <el-table-column prop="timeLimit" label="时长(分钟)" width="100" align="center">
          <template #default="{ row }">
            {{ row.timeLimit || '不限时' }}
          </template>
        </el-table-column>
        
        <el-table-column prop="examStatus" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.examStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.examStatus === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="创建时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="300" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="manageQuestions(row)">
              题目管理
            </el-button>
            <el-button type="info" size="small" @click="onEditExam(row)">
              编辑
            </el-button>
            <el-button 
              v-if="row.examStatus === 0" 
              type="success" 
              size="small" 
              @click="publishExam(row)"
            >
              发布
            </el-button>
            <el-button 
              v-else 
              type="warning" 
              size="small" 
              @click="unpublishExam(row)"
            >
              取消发布
            </el-button>
            <el-button type="danger" size="small" @click="deleteExam(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && exams.length === 0" class="empty-state">
      <el-empty description="暂无考试，点击上方按钮创建考试"></el-empty>
    </div>

    <!-- 考试编辑对话框 -->
    <el-dialog 
      :title="examDialogTitle" 
      v-model="examDialogVisible" 
      width="60%"
    >
      <el-form :model="examForm" ref="examFormRef" label-width="120px">
        <el-form-item 
          label="考试标题" 
          prop="examTitle" 
          :rules="[{ required: true, message: '请输入考试标题', trigger: 'blur' }]"
        >
          <el-input v-model="examForm.examTitle" placeholder="请输入考试标题"></el-input>
        </el-form-item>
        
        <el-form-item label="考试描述" prop="examDesc">
          <el-input 
            v-model="examForm.examDesc" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入考试描述（可选）"
          ></el-input>
        </el-form-item>
        
        <el-row>
          <el-col :span="12">
            <el-form-item 
              label="考试类型" 
              prop="examType" 
              :rules="[{ required: true, message: '请选择考试类型', trigger: 'change' }]"
            >
              <el-radio-group v-model="examForm.examType">
                <el-radio :label="1">练习</el-radio>
                <el-radio :label="2">考试</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总分" prop="totalScore">
              <el-input-number 
                v-model="examForm.totalScore" 
                :min="1" 
                :max="1000"
                style="width: 100%;"
              ></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="及格分数" prop="passScore">
              <el-input-number 
                v-model="examForm.passScore" 
                :min="1" 
                :max="examForm.totalScore"
                style="width: 100%;"
              ></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="考试时长(分钟)" prop="timeLimit">
              <el-input-number 
                v-model="examForm.timeLimit" 
                :min="1" 
                placeholder="不填表示不限时"
                style="width: 100%;"
              ></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="显示结果" prop="showResult">
              <el-radio-group v-model="examForm.showResult">
                <el-radio :label="0">否</el-radio>
                <el-radio :label="1">是</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示答案" prop="showAnswer">
              <el-radio-group v-model="examForm.showAnswer">
                <el-radio :label="0">否</el-radio>
                <el-radio :label="1">是</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button type="success" @click="examDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitExamForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getExamsByCourseId, createExam, updateExam, deleteExam, publishExam, unpublishExam } from '../../api/exam'

export default {
  name: 'ExamManager',
  data() {
    return {
      courseId: null,
      courseTitle: '',
      exams: [],
      loading: false,
      
      // 考试对话框
      examDialogVisible: false,
      examDialogTitle: '创建考试',
      examForm: {
        examTitle: '',
        examDesc: '',
        examType: 1,
        totalScore: 100,
        passScore: 60,
        timeLimit: null,
        showResult: 1,
        showAnswer: 1
      }
    }
  },
  created() {
    // 从路由参数获取课程信息
    this.courseId = this.$route.query.courseId
    this.courseTitle = this.$route.query.courseTitle || '课程'
    
    if (!this.courseId) {
      this.$message.error('课程ID不能为空')
      this.$router.push('/teacher/coursemanager')
      return
    }
    
    this.loadExams()
  },
  methods: {
    async loadExams() {
      this.loading = true
      try {
        const res = await getExamsByCourseId(this.courseId)
        if (res && res.flag) {
          this.exams = res.result || []
        } else {
          this.exams = []
          this.$message.error(res?.reason || '获取考试列表失败')
        }
      } catch (e) {
        console.error('获取考试列表失败', e)
        this.exams = []
        this.$message.error('获取考试列表失败')
      } finally {
        this.loading = false
      }
    },

    onAddExam() {
      this.examDialogTitle = '创建考试'
      this.examForm = {
        examTitle: '',
        examDesc: '',
        examType: 1,
        totalScore: 100,
        passScore: 60,
        timeLimit: null,
        showResult: 1,
        showAnswer: 1
      }
      this.examDialogVisible = true
    },

    onEditExam(exam) {
      this.examDialogTitle = '编辑考试'
      this.examForm = {
        examId: exam.examId,
        examTitle: exam.examTitle,
        examDesc: exam.examDesc,
        examType: exam.examType,
        totalScore: exam.totalScore,
        passScore: exam.passScore,
        timeLimit: exam.timeLimit,
        showResult: exam.showResult,
        showAnswer: exam.showAnswer
      }
      this.examDialogVisible = true
    },

    async submitExamForm() {
      try {
        await this.$refs.examFormRef.validate()
        
        const params = {
          ...this.examForm,
          courseId: this.courseId
        }
        
        let res
        if (params.examId) {
          // 更新考试
          res = await updateExam(params)
        } else {
          // 创建考试
          res = await createExam(params)
        }
        
        if (res && res.flag) {
          this.$message.success(res.reason || '保存成功')
          this.examDialogVisible = false
          this.loadExams()
        } else {
          this.$message.error(res?.reason || '保存失败')
        }
      } catch (e) {
        console.error('保存考试失败', e)
        this.$message.error('保存失败')
      }
    },

    manageQuestions(exam) {
      // 跳转到题目管理页面，传递考试ID和考试标题
      this.$router.push({
        path: '/teacher/questionmanager',
        query: { 
          examId: exam.examId,
          examTitle: exam.examTitle,
          courseId: this.courseId,
          courseTitle: this.courseTitle
        }
      })
    },

    async publishExam(exam) {
      try {
        const res = await publishExam(exam.examId)
        if (res && res.flag) {
          this.$message.success(res.reason || '发布成功')
          this.loadExams()
        } else {
          this.$message.error(res?.reason || '发布失败')
        }
      } catch (e) {
        console.error('发布失败', e)
        this.$message.error('发布失败')
      }
    },

    async unpublishExam(exam) {
      try {
        const res = await unpublishExam(exam.examId)
        if (res && res.flag) {
          this.$message.success(res.reason || '取消发布成功')
          this.loadExams()
        } else {
          this.$message.error(res?.reason || '取消发布失败')
        }
      } catch (e) {
        console.error('取消发布失败', e)
        this.$message.error('取消发布失败')
      }
    },

    async deleteExam(exam) {
      try {
        await this.$confirm('确定删除该考试吗？删除后无法恢复。', '确认', { type: 'warning' })
        const res = await deleteExam(exam.examId)
        if (res && res.flag) {
          this.$message.success(res.reason || '删除成功')
          this.loadExams()
        } else {
          this.$message.error(res?.reason || '删除失败')
        }
      } catch (e) {
        if (e !== 'cancel') {
          console.error('删除失败', e)
          this.$message.error('删除失败')
        }
      }
    },

    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    }
  }
}
</script>

<style scoped>
.exam-manager {
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

.exam-title-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.exam-list {
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}
</style>