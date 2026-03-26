<template>
  <div class="exam-grading">
    <div class="header">
      <h2>考试批改</h2>
      <el-button type="primary" @click="refreshRecords">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="filterCourse" placeholder="选择课程" clearable>
            <el-option 
              v-for="course in courses" 
              :key="course.courseId" 
              :label="course.courseTitle" 
              :value="course.courseId">
            </el-option>
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="filterStatus" placeholder="批改状态" clearable @change="filterRecords">
            <el-option label="全部状态" :value="null"></el-option>
            <el-option label="待批改" :value="1"></el-option>
            <el-option label="已批改" :value="2"></el-option>
          </el-select>
        </el-col>
      </el-row>
    </div>

    <!-- 考试记录列表 -->
    <div class="records-list" v-loading="loading">
      <el-table :data="filteredRecords" border style="width: 100%">
        <el-table-column label="学生姓名" width="120">
          <template #default="{ row }">
            {{ row.studentName || row.studentname }}
          </template>
        </el-table-column>
        <el-table-column label="考试名称" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.examTitle || row.examtitle }}
          </template>
        </el-table-column>
        
        <el-table-column label="提交时间" width="200" align="center">
          <template #default="{ row }">
            {{ (row.submitTime || row.submittime || '')?.toString().replace('T', ' ') }}
          </template>
        </el-table-column>
        
        <el-table-column label="主观题情况" width="120" align="center">
          <template #default="{ row }">
            <span v-if="(row.subjectiveCount || row.subjectivecount || 0) > 0">
              {{ (row.subjectiveCount || row.subjectivecount) - (row.ungradedCount || row.ungradedcount || 0) }}/{{ row.subjectiveCount || row.subjectivecount }}
            </span>
            <span v-else>无主观题</span>
          </template>
        </el-table-column>
        
        <el-table-column label="得分" width="120" align="center">
          <template #default="{ row }">
            <span v-if="(row.studentScore || row.studentscore) !== null && (row.studentScore || row.studentscore) !== undefined">
              {{ row.studentScore || row.studentscore }}/{{ row.totalScore || row.totalscore }}
            </span>
            <span v-else class="text-muted">未评分</span>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusColor(row.examStatus || row.examstatus, row.ungradedCount || row.ungradedcount)">
              {{ getStatusText(row.examStatus || row.examstatus, row.ungradedCount || row.ungradedcount) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button 
              v-if="(row.ungradedCount || row.ungradedcount || 0) > 0" 
              type="primary" 
              size="small" 
              @click="startGrading(row)"
            >
              批改主观题
            </el-button>
            <el-button 
              v-else-if="(row.subjectiveCount || row.subjectivecount || 0) > 0" 
              type="info" 
              size="small" 
              @click="viewGrading(row)"
            >
              查看批改
            </el-button>
            <span v-else class="text-muted">无需批改</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <div v-if="!loading && records.length === 0" class="empty-state">
        <el-empty description="暂无需要批改的考试记录"></el-empty>
      </div>
    </div>

    <!-- 批改对话框 -->
    <el-dialog 
      :title="isViewMode ? '主观题批改结果' : '主观题批改'" 
      v-model="gradingDialogVisible" 
      width="80%" 
      :close-on-click-modal="false"
    >
      <div v-if="currentRecord" class="grading-content">
        <!-- 基本信息 -->
        <div class="exam-info">
          <div class="exam-info-item">
            <span class="label">学生</span>
            <span class="value">{{ currentRecord.studentName }}</span>
          </div>
          <div class="exam-info-item">
            <span class="label">考试</span>
            <span class="value">{{ currentRecord.examTitle }}</span>
          </div>
          <div class="exam-info-item">
            <span class="label">提交时间</span>
            <span class="value">{{ formatDateTime(currentRecord.submitTime) }}</span>
          </div>
        </div>
        
        <!-- 题目列表 -->
        <div 
          v-if="currentRecord.subjectiveAnswers && currentRecord.subjectiveAnswers.length > 0" 
          class="subjective-questions"
        >
          <h4 class="section-title">需要批改的主观题</h4>
          <div 
            v-for="(answer, index) in currentRecord.subjectiveAnswers" 
            :key="answer.answerId" 
            class="question-card"
          >
            <div class="question-card-header">
              <div class="question-title">
                <span class="question-badge">第 {{ index + 1 }} 题</span>
                <span class="question-score-tag">{{ answer.questionScore }} 分</span>
              </div>
            </div>

            <div class="question-card-body">
              <div class="question-text">
                <span class="sub-label">题目：</span>
                <span>{{ answer.questionContent }}</span>
              </div>
              <div class="question-answers">
                <div class="answer-column">
                  <div class="question-text">
                    <span class="sub-label">学生答案：</span>
                  </div>
                  <div class="student-answer">{{ answer.studentAnswer }}</div>
                </div>
                <div class="answer-column reference-column">
                  <div class="question-text">
                    <span class="sub-label">参考答案：</span>
                  </div>
                  <div class="reference-answer">
                    {{ answer.referenceAnswer || '暂无参考答案' }}
                  </div>
                </div>
              </div>

              <!-- 更改分数入口（查看模式下显示） -->
              <div class="change-score-link" v-if="isViewMode && !answer.canEditScore">
                <el-button 
                  type="primary" 
                  text 
                  size="small" 
                  @click="enableEdit(answer)"
                >
                  更改分数
                </el-button>
              </div>
            </div>

            <div class="question-card-footer">
              <div class="score-box">
                <span class="sub-label">得分：</span>
                <el-input-number 
                  v-model="answer.teacherScore" 
                  :min="0" 
                  :max="answer.questionScore" 
                  :precision="1"
                  placeholder="得分"
                  size="small"
                  :disabled="isViewMode && !answer.canEditScore"
                />
                <span class="score-max"> / {{ answer.questionScore }} 分</span>
              </div>
            </div>
          </div>
        </div>
        
        <div v-else>
          <el-alert message="该考试没有需要批改的主观题" type="info" />
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="gradingDialogVisible = false">
            {{ isViewMode ? '关闭' : '取消' }}
          </el-button>
          <el-button 
            v-if="!isViewMode || (currentRecord && currentRecord.subjectiveAnswers && currentRecord.subjectiveAnswers.some(a => a.canEditScore))"
            type="primary" 
            @click="submitGrading"
            :disabled="!currentRecord || !currentRecord.subjectiveAnswers || currentRecord.subjectiveAnswers.length === 0"
          >
            提交批改
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getSessionStorage } from '../../utils/common'
import { getTeacherCoursesList } from '../../api/course'
import { getRecordsForGrading, gradeExamRecord } from '../../api/examRecord'
import { getStudentAnswers, gradeSubjectiveAnswers } from '../../api/studentAnswer'

export default {
  name: 'ExamGrading',
  components: {
    Refresh
  },
  setup() {
    // 响应式数据
    const courses = ref([])
    const records = ref([])
    const loading = ref(false)
    
    // 筛选条件
    const filterCourse = ref(null)
    const filterStatus = ref(null) // 默认显示所有状态
    
    // 批改对话框
    const gradingDialogVisible = ref(false)
    const currentRecord = ref(null)
    const isViewMode = ref(false)

    // 初始化：加载课程列表
    onMounted(() => {
      loadCourses()
    })

    // 过滤后的记录
    const filteredRecords = computed(() => {
      let filtered = records.value
      
      if (filterStatus.value !== null && filterStatus.value !== undefined) {
        filtered = filtered.filter(record => {
          const examStatus = record.examStatus || record.examstatus
          return examStatus === filterStatus.value
        })
      }
      
      console.log('过滤后的记录数量:', filtered.length)
      console.log('过滤后的记录:', filtered)
      
      return filtered
    })

    // 加载课程列表
    const loadCourses = async () => {
      try {
        const currentUser = getSessionStorage('curuser')
        console.log('当前用户信息:', currentUser)
        if (!currentUser || !currentUser.info) {
          ElMessage.error('请先登录')
          return
        }
        
        const teacherId = currentUser.info.teachId || currentUser.info.teacherId
        console.log('教师ID:', teacherId)
        const res = await getTeacherCoursesList(teacherId)
        console.log('课程列表响应:', res)
        
        if (res && res.flag) {
          courses.value = res.result || []
          console.log('课程列表:', courses.value)
          
          if (courses.value.length > 0) {
            filterCourse.value = courses.value[0].courseId
            console.log('选择的课程ID:', filterCourse.value)
            // 不手动调用 loadExamRecords，交给 watch(filterCourse) 统一处理
          }
        } else {
          ElMessage.error(res?.reason || '获取课程列表失败')
        }
      } catch (e) {
        console.error('获取课程列表失败', e)
        ElMessage.error('获取课程列表失败')
      }
    }

    // 加载考试记录
    const loadExamRecords = async () => {
      if (!filterCourse.value) {
        console.log('没有选择课程，跳过加载考试记录')
        return
      }
      
      loading.value = true
      try {
        console.log('开始获取考试记录，课程ID:', filterCourse.value)
        console.log('请求URL:', `/exam/GetRecordsForGrading?courseId=${filterCourse.value}`)
        
        const res = await getRecordsForGrading(filterCourse.value)
        console.log('考试记录响应:', res)
        
        if (res && res.flag) {
          records.value = res.result || []
          console.log('考试记录列表:', records.value)
          console.log('记录数量:', records.value.length)
          
          if (records.value.length === 0) {
            console.log('没有找到考试记录，可能的原因：')
            console.log('1. 数据库中没有该课程的考试记录')
            console.log('2. 所有记录的exam_status不是1或2')
            console.log('3. 课程ID不匹配')
            console.log('当前课程ID:', filterCourse.value)
          }
          
          // 添加详细的记录信息日志
          records.value.forEach((record, index) => {
            console.log(`记录${index + 1}:`, {
              recordId: record.record_id,
              studentName: record.student_name,
              examTitle: record.exam_title,
              examStatus: record.exam_status,
              submitTime: record.submit_time
            })
          })
        } else {
          console.error('获取考试记录失败，响应:', res)
          records.value = []
          ElMessage.error(res?.reason || '获取考试记录失败')
        }
      } catch (e) {
        console.error('获取考试记录失败', e)
        records.value = []
        ElMessage.error('获取考试记录失败')
      } finally {
        loading.value = false
      }
    }

    // 监听课程选择变化：进入页面第一次赋值 & 手动切换课程都会触发
    watch(filterCourse, (newVal, oldVal) => {
      if (newVal) {
        console.log('课程发生变化，旧值:', oldVal, '新值:', newVal)
        loadExamRecords()
      } else {
        console.log('课程被清空，不加载考试记录')
        records.value = []
      }
    })

    // 刷新记录（手动点击按钮）
    const refreshRecords = () => {
      loadExamRecords()
    }

    // 过滤记录
    const filterRecords = () => {
      // 过滤逻辑在computed中处理
    }

    // 打开批改弹窗
    // showAll: 是否查看所有主观题（包含已批改）
    // readonly: 是否只读查看（不能改分）
    const openGrading = async (record, showAll = false, readonly = false) => {
      try {
        isViewMode.value = readonly
        const recordId = record.recordId || record.recordid
        console.log(showAll ? '查看批改，记录ID:' : '开始批改，记录ID:', recordId)
        // 获取该考试记录的所有答案
        const res = await getStudentAnswers(recordId)
        console.log('获取答案响应:', res)
        
        if (res && res.flag) {
          const answers = res.result || []
          console.log('所有答案:', answers)
          
          // 筛选出主观题（题目类型 4填空，5简答）
          const subjectiveAnswers = answers.filter(answer => {
            const questionType = answer.questionType || answer.question_type
            const studentScore = answer.studentScore || answer.student_score
            console.log('题目类型:', questionType, '学生得分:', studentScore)
            // showAll=true 时不过滤已评分的，showAll=false 时只取未评分的
            if (questionType <= 3) return false
            if (showAll) return true
            return studentScore === null || studentScore === undefined
          }).map(answer => ({
            ...answer,
            answerId: answer.answerId || answer.answer_id,
            questionId: answer.questionId || answer.question_id,
            questionType: answer.questionType || answer.question_type,
            questionContent: answer.questionContent || answer.question_content,
            studentAnswer: answer.studentAnswer || answer.student_answer,
            referenceAnswer: answer.standardAnswer || answer.standard_answer || answer.referenceAnswer || answer.reference_answer || '',
            questionScore: answer.questionScore || answer.question_score,
            studentScore: answer.studentScore || answer.student_score,
            // 查看模式下显示已批改分数，批改模式下初始为 0
            teacherScore: showAll ? (answer.studentScore || answer.student_score || 0) : 0,
            // 仅当前题目点击“更改分数”后可编辑
            canEditScore: !showAll,
            teacherComment: ''
          }))
          
          console.log('主观题答案:', subjectiveAnswers)
          
          if (subjectiveAnswers.length === 0) {
            ElMessage.info(showAll ? '该考试没有主观题' : '该考试没有需要批改的主观题')
            return
          }
          
          currentRecord.value = {
            ...record,
            recordId: recordId,
            studentName: record.studentName || record.studentname,
            examTitle: record.examTitle || record.examtitle,
            submitTime: record.submitTime || record.submittime,
            subjectiveAnswers: subjectiveAnswers
          }
          gradingDialogVisible.value = true
        } else {
          ElMessage.error('获取答案失败：' + (res?.reason || '未知错误'))
        }
      } catch (e) {
        console.error('获取答案失败', e)
        ElMessage.error('获取答案失败')
      }
    }

    // 开始批改：只看未批改主观题
    const startGrading = (record) => {
      openGrading(record, false, false)
    }

    // 查看批改
    const viewGrading = (record) => {
      openGrading(record, true, true)
    }

    // 查看模式下，点击某一题的“更改分数”只允许该题可编辑
    const enableEdit = (answer) => {
      if (!answer) return
      answer.canEditScore = true
    }

    // 提交批改
    const submitGrading = async () => {
      if (!currentRecord.value || !currentRecord.value.subjectiveAnswers) return
      
      try {
        // 验证所有主观题都已评分
        const ungraded = currentRecord.value.subjectiveAnswers.filter(answer => 
          answer.teacherScore === null || answer.teacherScore === undefined || answer.teacherScore === ''
        )
        
        if (ungraded.length > 0) {
          ElMessage.warning('请为所有主观题评分')
          return
        }
        
        // 准备批改数据
        const gradingData = currentRecord.value.subjectiveAnswers.map(answer => ({
          answerId: answer.answerId,
          studentScore: answer.teacherScore,
          teacherComment: ''
        }))
        
        console.log('提交批改数据:', gradingData)
        
        // 调用批改接口
        const recordId = currentRecord.value.recordId
        const res = await gradeSubjectiveAnswers(recordId, gradingData)
        
        console.log('批改响应:', res)
        
        if (res && res.flag) {
          ElMessage.success('批改提交成功！')
          gradingDialogVisible.value = false
          currentRecord.value = null
          loadExamRecords()
        } else {
          ElMessage.error(res?.reason || '批改提交失败')
        }
      } catch (e) {
        console.error('批改提交失败', e)
        ElMessage.error('批改提交失败：' + e.message)
      }
    }

    // 工具方法
    const getStatusText = (status, ungradedCount) => {
      if (ungradedCount > 0) {
        return '待批改'
      } else if (status === 2) {
        return '已批改'
      } else {
        return '已提交'
      }
    }

    const getStatusColor = (status, ungradedCount) => {
      if (ungradedCount > 0) {
        return 'warning'
      } else if (status === 2) {
        return 'success'
      } else {
        return 'info'
      }
    }

    const formatDateTime = (dateStr) => {
      if (!dateStr) return ''
      // 直接使用后端返回的完整时间字符串，避免浏览器解析时丢失秒或被时区影响
      if (typeof dateStr === 'string') {
        // 常见格式：'2026-03-03 18:32:23' 或 '2026-03-03T18:32:23'
        return dateStr.replace('T', ' ').substring(0, 19)
      }
      // 兜底：如果是 Date 对象再做本地格式化
      const date = new Date(dateStr)
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const d = String(date.getDate()).padStart(2, '0')
      const hh = String(date.getHours()).padStart(2, '0')
      const mm = String(date.getMinutes()).padStart(2, '0')
      const ss = String(date.getSeconds()).padStart(2, '0')
      return `${y}-${m}-${d} ${hh}:${mm}:${ss}`
    }

    return {
      courses,
      records,
      loading,
      filterCourse,
      filterStatus,
      filteredRecords,
      gradingDialogVisible,
      currentRecord,
      isViewMode,
      enableEdit,
      refreshRecords,
      filterRecords,
      startGrading,
      viewGrading,
      submitGrading,
      getStatusText,
      getStatusColor,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.exam-grading {
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

.filter-section {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
}

.records-list {
  margin-bottom: 20px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.grading-content {
  padding: 20px 0;
}

.exam-info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 24px;
  padding: 12px 16px;
  margin-bottom: 16px;
  border-radius: 10px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
}

.exam-info-item .label {
  display: inline-block;
  min-width: 64px;
  color: #6b7280;
}

.exam-info-item .value {
  color: #111827;
  font-weight: 500;
}

.subjective-questions {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 6px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 10px;
}

.question-card {
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  padding: 12px 14px;
  margin-bottom: 12px;
  background: #ffffff;
}

.question-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.question-title {
  display: flex;
  gap: 8px;
  align-items: center;
}

.question-badge {
  padding: 2px 10px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 12px;
}

.question-score-tag {
  padding: 2px 8px;
  border-radius: 999px;
  background: #ecfdf5;
  color: #059669;
  font-size: 12px;
}

.question-card-body {
  margin: 4px 0 8px;
  font-size: 13px;
  color: #374151;
}

.question-text {
  margin-bottom: 4px;
}

.sub-label {
  color: #6b7280;
}

.student-answer {
  padding: 8px 10px;
  border-radius: 8px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  white-space: pre-wrap;
}

.change-score-link {
  margin-top: 6px;
}

.question-answers {
  display: flex;
  gap: 12px;
  margin-top: 6px;
}

.answer-column {
  flex: 1;
}

.reference-column .reference-answer {
  padding: 8px 10px;
  border-radius: 8px;
  background: #fefce8;
  border: 1px solid #facc15;
  white-space: pre-wrap;
  color: #43302b;
}

.question-card-footer {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  margin-top: 6px;
}

.score-box {
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.score-max {
  color: #6b7280;
  font-size: 12px;
}
</style>