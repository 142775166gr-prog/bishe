<template>
  <div class="exam-history">
    <div class="header">
      <h2>{{ examTitle }} - 考试详情</h2>
      <el-button type="success" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>

    <!-- 考试信息卡片 -->
    <el-card class="exam-info-card" v-if="examInfo">
      <div class="exam-summary">
        <div class="summary-item">
          <div class="summary-label">考试类型</div>
          <div class="summary-value">
            <el-tag :type="examInfo.examType === 1 ? 'success' : 'primary'">
              {{ examInfo.examType === 1 ? '练习' : '考试' }}
            </el-tag>
          </div>
        </div>
        <div class="summary-item">
          <div class="summary-label">总分</div>
          <div class="summary-value">{{ examInfo.totalScore }}分</div>
        </div>
        <div class="summary-item">
          <div class="summary-label">及格分</div>
          <div class="summary-value">{{ examInfo.passScore }}分</div>
        </div>
        <div class="summary-item" v-if="examInfo.timeLimit">
          <div class="summary-label">时间限制</div>
          <div class="summary-value">{{ examInfo.timeLimit }}分钟</div>
        </div>
        <div class="summary-item">
          <div class="summary-label">参考次数</div>
          <div class="summary-value">{{ records.length }}次</div>
        </div>
      </div>
    </el-card>

    <!-- 考试记录列表 -->
    <el-card class="records-card">
      <template #header>
        <div class="card-header">
          <span>考试记录</span>
          <el-button type="primary" size="small" @click="refreshRecords">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <div v-loading="loading">
        <el-table :data="records" border style="width: 100%">
          <el-table-column prop="attemptNumber" label="第几次" width="80" align="center">
            <template #default="{ row }">
              第{{ row.attemptNumber }}次
            </template>
          </el-table-column>
          
          <el-table-column prop="startTime" label="开始时间" width="180" align="center">
            <template #default="{ row }">
              {{ formatDateTime(row.startTime) }}
            </template>
          </el-table-column>
          
          <el-table-column prop="submitTime" label="提交时间" width="180" align="center">
            <template #default="{ row }">
              <span v-if="row.submitTime">{{ formatDateTime(row.submitTime) }}</span>
              <span v-else class="text-warning">未提交</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="timeUsed" label="用时" width="100" align="center">
            <template #default="{ row }">
              <span v-if="row.timeUsed">{{ row.timeUsed }}分钟</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="studentScore" label="得分" width="120" align="center">
            <template #default="{ row }">
              <span v-if="row.studentScore !== null" :class="getScoreClass(row)">
                {{ row.studentScore }}/{{ row.totalScore }}
              </span>
              <span v-else class="text-muted">未评分</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="passStatus" label="结果" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.passStatus === 1" type="success" size="small">通过</el-tag>
              <el-tag v-else-if="row.passStatus === 0" type="danger" size="small">未通过</el-tag>
              <span v-else class="text-muted">未评分</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="examStatus" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusColor(row.examStatus)" size="small">
                {{ getStatusText(row.examStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" align="center">
            <template #default="{ row }">
              <el-button 
                v-if="row.examStatus === 0" 
                type="primary" 
                size="small" 
                @click="continueExam(row)"
              >
                继续考试
              </el-button>
              <el-button 
                v-if="row.examStatus >= 1" 
                type="info" 
                size="small" 
                @click="viewDetails(row)"
              >
                查看详情
              </el-button>
              <el-button 
                v-if="row.examStatus === 2 && examInfo && examInfo.examType === 1" 
                type="success" 
                size="small" 
                @click="retakeExam"
              >
                重新练习
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 空状态 -->
        <div v-if="!loading && records.length === 0" class="empty-state">
          <el-empty description="暂无考试记录">
            <el-button type="primary" @click="startNewExam">开始考试</el-button>
          </el-empty>
        </div>
      </div>
    </el-card>

    <!-- 统计信息 -->
    <el-card v-if="records.length > 0" class="statistics-card">
      <template #header>
        <span>统计信息</span>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ statistics.totalAttempts }}</div>
            <div class="stat-label">总考试次数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ statistics.passedAttempts }}</div>
            <div class="stat-label">通过次数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ statistics.passRate }}%</div>
            <div class="stat-label">通过率</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ statistics.bestScore }}</div>
            <div class="stat-label">最高分</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog 
      :title="examInfo && examInfo.examType === 2 ? '考试详情' : '练习详情'" 
      v-model="detailDialogVisible" 
      width="80%" 
      :close-on-click-modal="false"
    >
      <div v-if="selectedRecord" class="exam-detail">
        <!-- 基本信息 -->
        <el-descriptions :column="2" border class="detail-info">
          <el-descriptions-item label="考试次数">第{{ selectedRecord.attemptNumber }}次</el-descriptions-item>
          <el-descriptions-item label="考试状态">
            <el-tag :type="getStatusColor(selectedRecord.examStatus)">
              {{ getStatusText(selectedRecord.examStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ formatDateTime(selectedRecord.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">
            {{ selectedRecord.submitTime ? formatDateTime(selectedRecord.submitTime) : '未提交' }}
          </el-descriptions-item>
          <el-descriptions-item label="用时">
            {{ selectedRecord.timeUsed ? selectedRecord.timeUsed + '分钟' : '未完成' }}
          </el-descriptions-item>
          <el-descriptions-item label="总分">{{ selectedRecord.totalScore }}分</el-descriptions-item>
          <el-descriptions-item label="得分">
            <span v-if="selectedRecord.studentScore !== null" :class="getScoreClass(selectedRecord)">
              {{ selectedRecord.studentScore }}分
            </span>
            <span v-else>未评分</span>
          </el-descriptions-item>
          <el-descriptions-item label="通过状态">
            <el-tag v-if="selectedRecord.passStatus === 1" type="success">通过</el-tag>
            <el-tag v-else-if="selectedRecord.passStatus === 0" type="danger">未通过</el-tag>
            <span v-else>未评分</span>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 题目详情 -->
        <div v-if="selectedRecord.examStatus === 2" class="questions-detail">
          <h3 style="margin-top: 20px; margin-bottom: 15px;">答题详情</h3>
          
          <div v-if="detailAnswers.length > 0" class="answer-list">
            <div v-for="(answer, index) in detailAnswers" :key="answer.answerId || answer.answer_id || index" class="answer-item">
              <div class="question-header">
                <span class="question-number">第{{ index + 1 }}题</span>
                <el-tag :type="getQuestionTypeColor(answer.questionType || answer.question_type)" size="small">
                  {{ getQuestionTypeText(answer.questionType || answer.question_type) }}
                </el-tag>
                <span class="question-score">
                  得分：
                  <span :class="(answer.studentScore || answer.student_score || 0) > 0 ? 'text-success' : 'text-danger'">
                    {{ answer.studentScore !== null && answer.studentScore !== undefined ? answer.studentScore : (answer.student_score !== null && answer.student_score !== undefined ? answer.student_score : 0) }}
                  </span>
                  /{{ answer.questionScore || answer.question_score }}分
                </span>
              </div>
              
              <div class="question-content">
                <p><strong>题目：</strong>{{ answer.questionContent || answer.question_content }}</p>
                <p>
                  <strong>你的答案：</strong>
                  <span :class="(answer.studentAnswer || answer.student_answer) ? '' : 'text-muted'">
                    {{ formatAnswer(answer.studentAnswer || answer.student_answer, answer.questionType || answer.question_type) }}
                  </span>
                </p>
                <p>
                  <strong>{{ (answer.questionType || answer.question_type) <= 3 ? '正确答案' : '参考答案' }}：</strong>
                  {{ (answer.correctAnswer || answer.correct_answer) ? formatAnswer(answer.correctAnswer || answer.correct_answer, answer.questionType || answer.question_type) : '略' }}
                </p>
                <p v-if="answer.teacherComment || answer.teacher_comment" class="teacher-comment">
                  <strong>教师评语：</strong>{{ answer.teacherComment || answer.teacher_comment }}
                </p>
              </div>
            </div>
          </div>
          
          <div v-else-if="loadingAnswers" style="text-align: center; padding: 40px;">
            <el-icon class="is-loading"><Loading /></el-icon>
            <p>加载中...</p>
          </div>
          
          <el-empty v-else description="暂无答题记录"></el-empty>
        </div>
        
        <!-- 未批改提示 -->
        <div v-else-if="selectedRecord.examStatus === 1" class="waiting-grading">
          <el-alert
            title="等待教师批改"
            type="warning"
            description="你的考试已提交，请等待教师批改后查看详细结果"
            :closable="false"
            show-icon
          />
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button 
            v-if="selectedRecord && selectedRecord.examStatus === 0" 
            type="primary" 
            @click="continueExamFromDetail"
          >
            继续考试
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Refresh, Loading } from '@element-plus/icons-vue'
import { getExamById } from '../../api/exam'
import { getStudentExamRecords, getLatestExamRecord } from '../../api/examRecord'
import { getStudentAnswers } from '../../api/studentAnswer'
import { getSessionStorage } from '../../utils/common'

export default {
  name: 'ExamHistory',
  components: {
    ArrowLeft,
    Refresh,
    Loading
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    // 响应式数据
    const examId = ref(null)
    const examTitle = ref('')
    const examInfo = ref(null)
    const records = ref([])
    const loading = ref(false)
    const currentStudent = ref(null)
    
    // 详情对话框
    const detailDialogVisible = ref(false)
    const selectedRecord = ref(null)
    const detailAnswers = ref([])
    const loadingAnswers = ref(false)

    // 初始化
    onMounted(() => {
      examId.value = route.query.examId
      examTitle.value = route.query.examTitle || '考试'
      
      if (!examId.value) {
        ElMessage.error('考试ID不能为空')
        router.go(-1)
        return
      }
      
      initStudentInfo()
      loadExamInfo()
      loadExamRecords()
    })

    // 初始化学生信息
    const initStudentInfo = () => {
      const userInfo = getSessionStorage('curuser')
      if (userInfo && userInfo.info) {
        currentStudent.value = userInfo.info
      } else {
        ElMessage.error('请先登录')
        router.push('/login')
      }
    }

    // 加载考试信息
    const loadExamInfo = async () => {
      try {
        const res = await getExamById(examId.value)
        if (res && res.flag) {
          examInfo.value = res.result
        } else {
          ElMessage.error(res?.reason || '获取考试信息失败')
        }
      } catch (e) {
        console.error('获取考试信息失败', e)
        ElMessage.error('获取考试信息失败')
      }
    }

    // 加载考试记录
    const loadExamRecords = async () => {
      if (!currentStudent.value) return
      
      loading.value = true
      try {
        const studentId = currentStudent.value.stuId || currentStudent.value.studentId
        
        // 根据考试类型决定获取方式
        if (examInfo.value && examInfo.value.examType === 2) {
          // 考试类型：只获取最新一条记录
          const res = await getLatestExamRecord(studentId, examId.value)
          if (res && res.flag && res.result) {
            records.value = [res.result] // 包装成数组
          } else {
            records.value = []
          }
        } else {
          // 练习类型：获取所有记录
          const res = await getStudentExamRecords(studentId, examId.value)
          if (res && res.flag) {
            records.value = res.result || []
          } else {
            records.value = []
            ElMessage.error(res?.reason || '获取考试记录失败')
          }
        }
      } catch (e) {
        console.error('获取考试记录失败，错误:', e)
        records.value = []
        ElMessage.error('获取考试记录失败')
      } finally {
        loading.value = false
      }
    }

    // 统计信息
    const statistics = computed(() => {
      const total = records.value.length
      const passed = records.value.filter(r => r.passStatus === 1).length
      const scores = records.value.filter(r => r.studentScore !== null).map(r => r.studentScore)
      const bestScore = scores.length > 0 ? Math.max(...scores) : 0
      
      return {
        totalAttempts: total,
        passedAttempts: passed,
        passRate: total > 0 ? Math.round((passed / total) * 100) : 0,
        bestScore: bestScore
      }
    })

    // 刷新记录
    const refreshRecords = () => {
      loadExamRecords()
    }

    // 返回上一页
    const goBack = () => {
      router.go(-1)
    }

    // 继续考试
    const continueExam = (record) => {
      router.push({
        path: '/student/exam',
        query: {
          examId: examId.value,
          recordId: record.recordId
        }
      })
    }

    // 查看详情
    const viewDetails = async (record) => {
      selectedRecord.value = record
      detailDialogVisible.value = true
      detailAnswers.value = []
      
      // 如果是已批改状态，加载答题详情
      if (record.examStatus === 2) {
        await loadAnswerDetails(record.recordId)
      }
    }

    // 加载答题详情
    const loadAnswerDetails = async (recordId) => {
      loadingAnswers.value = true
      try {
        const res = await getStudentAnswers(recordId)
        if (res && res.flag) {
          detailAnswers.value = res.result || []
          console.log('答题详情:', detailAnswers.value)
        } else {
          ElMessage.error('获取答题详情失败')
        }
      } catch (e) {
        console.error('获取答题详情失败', e)
        ElMessage.error('获取答题详情失败')
      } finally {
        loadingAnswers.value = false
      }
    }

    // 重新考试
    const retakeExam = () => {
      router.push({
        path: '/student/exam',
        query: {
          examId: examId.value
        }
      })
    }

    // 开始新考试
    const startNewExam = () => {
      retakeExam()
    }

    // 从详情对话框继续考试
    const continueExamFromDetail = () => {
      detailDialogVisible.value = false
      continueExam(selectedRecord.value)
    }

    // 工具方法
    const getStatusText = (status) => {
      const statusMap = { 0: '进行中', 1: '已提交', 2: '已评分' }
      return statusMap[status] || '未知'
    }

    const getStatusColor = (status) => {
      const colorMap = { 0: 'warning', 1: 'primary', 2: 'success' }
      return colorMap[status] || 'info'
    }

    const getScoreClass = (record) => {
      if (record.studentScore === null) return ''
      const percentage = (record.studentScore / record.totalScore) * 100
      if (percentage >= 90) return 'score-excellent'
      if (percentage >= 80) return 'score-good'
      if (percentage >= 60) return 'score-pass'
      return 'score-fail'
    }

    const formatDateTime = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    }

    // 格式化答案
    const formatAnswer = (answer, questionType) => {
      if (!answer || answer === '' || answer === null || answer === undefined) {
        return '未作答'
      }
      
      try {
        if (questionType === 2) {
          // 多选题
          const arr = JSON.parse(answer)
          return Array.isArray(arr) ? arr.join(', ') : answer
        } else if (questionType === 3) {
          // 判断题
          return answer === 'true' || answer === true ? '正确' : '错误'
        }
        return answer
      } catch {
        return answer
      }
    }

    // 获取题型文本
    const getQuestionTypeText = (type) => {
      const types = { 1: '单选题', 2: '多选题', 3: '判断题', 4: '填空题', 5: '简答题' }
      return types[type] || '未知'
    }

    // 获取题型颜色
    const getQuestionTypeColor = (type) => {
      const colors = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'info', 5: 'danger' }
      return colors[type] || ''
    }

    return {
      examTitle,
      examInfo,
      records,
      loading,
      statistics,
      detailDialogVisible,
      selectedRecord,
      detailAnswers,
      loadingAnswers,
      refreshRecords,
      goBack,
      continueExam,
      viewDetails,
      retakeExam,
      startNewExam,
      continueExamFromDetail,
      getStatusText,
      getStatusColor,
      getScoreClass,
      formatDateTime,
      formatAnswer,
      getQuestionTypeText,
      getQuestionTypeColor
    }
  }
}
</script>

<style scoped>
.exam-history {
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

.exam-info-card {
  margin-bottom: 20px;
}

.exam-summary {
  display: flex;
  gap: 30px;
  flex-wrap: wrap;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 100px;
}

.summary-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.summary-value {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.records-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.statistics-card .stat-item {
  text-align: center;
  padding: 15px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.score-excellent {
  color: #67c23a;
  font-weight: bold;
}

.score-good {
  color: #409eff;
  font-weight: bold;
}

.score-pass {
  color: #e6a23c;
}

.score-fail {
  color: #f56c6c;
}

.text-warning {
  color: #e6a23c;
}

.text-muted {
  color: #999;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.exam-detail {
  padding: 20px 0;
}
</style>


.text-success {
  color: #67c23a;
  font-weight: bold;
}

.text-danger {
  color: #f56c6c;
  font-weight: bold;
}

.detail-info {
  margin-bottom: 20px;
}

.questions-detail {
  margin-top: 20px;
}

.answer-list {
  max-height: 500px;
  overflow-y: auto;
}

.answer-item {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 15px;
  background: #fafafa;
}

.question-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.question-number {
  font-weight: bold;
  font-size: 16px;
}

.question-score {
  margin-left: auto;
  font-weight: bold;
}

.question-content p {
  margin: 8px 0;
  line-height: 1.6;
}

.teacher-comment {
  color: #e6a23c;
  background: #fdf6ec;
  padding: 10px;
  border-radius: 4px;
  margin-top: 10px;
}

.waiting-grading {
  padding: 40px 20px;
  text-align: center;
}
