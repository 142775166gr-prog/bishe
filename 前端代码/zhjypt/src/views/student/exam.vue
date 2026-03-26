<template>
  <div class="exam-page">
    <div class="exam-header">
      <div class="exam-info">
        <h2>{{ examInfo ? examInfo.examTitle : '考试' }}</h2>
        <div class="exam-meta">
          <el-tag :type="examInfo && examInfo.examType === 1 ? 'success' : 'primary'">
            {{ examInfo && examInfo.examType === 1 ? '练习' : '考试' }}
          </el-tag>
          <span>总分：{{ examInfo ? examInfo.totalScore : 0 }}分</span>
          <span v-if="examInfo && examInfo.timeLimit">时长：{{ examInfo.timeLimit }}分钟</span>
          <span v-else>不限时</span>
        </div>
      </div>
      <div class="exam-timer" v-if="examInfo && examInfo.timeLimit && examStarted">
        <span>⏰</span>
        <span>剩余时间：{{ formatTime(remainingTime) }}</span>
      </div>
    </div>

    <!-- 考试说明 -->
    <div v-if="!examStarted" class="exam-instructions">
      <el-card>
        <h3>考试说明</h3>
        <div class="instructions-content">
          <p v-if="examInfo && examInfo.examDesc">{{ examInfo.examDesc }}</p>
          <ul>
            <li>请仔细阅读每道题目，认真作答</li>
            <li v-if="examInfo && examInfo.timeLimit">本次考试限时{{ examInfo.timeLimit }}分钟，请合理安排时间</li>
            <li v-else>本次考试不限时，请仔细作答</li>
            <li style="color: #f56c6c; font-weight: bold;">考试开始后不能中途退出，否则将自动提交试卷</li>
            <li>考试过程中请勿刷新页面，否则可能丢失答题进度</li>
            <li>完成所有题目后，请点击"提交考试"按钮</li>
          </ul>
        </div>
        <div class="start-actions">
          <el-button type="success" @click="goBack">返回</el-button>
          <el-button type="primary" @click="startExam">开始考试</el-button>
        </div>
      </el-card>
    </div>

    <!-- 考试内容 -->
    <div v-else class="exam-content" v-loading="loading">
      <div class="question-progress">
        <span>第 {{ currentQuestionIndex + 1 }} 题 / 共 {{ questions.length }} 题</span>
        <span v-if="currentQuestion" class="question-score">{{ currentQuestion.questionScore }}分</span>
      </div>

      <div v-if="currentQuestion" class="question-container">
        <div class="question-header">
          <h3>{{ currentQuestion.questionContent }}</h3>
          <div class="question-meta">
            <el-tag :type="getQuestionTypeColor(currentQuestion.questionType)">
              {{ getQuestionTypeText(currentQuestion.questionType) }}
            </el-tag>
            <span>{{ currentQuestion.questionScore }}分</span>
          </div>
        </div>

        <div class="question-image" v-if="currentQuestion.questionImage">
          <img :src="currentQuestion.questionImage" alt="题目图片" />
        </div>

        <div class="answer-area">
          <!-- 单选题 -->
          <div v-if="currentQuestion.questionType === 1" class="single-choice">
            <el-radio-group v-model="currentAnswer">
              <div v-for="option in currentQuestion.options" :key="option.optionId" class="option-item">
                <el-radio :label="option.optionLabel">
                  {{ option.optionLabel }}. {{ option.optionContent }}
                </el-radio>
                <img v-if="option.optionImage" :src="option.optionImage" alt="选项图片" class="option-image" />
              </div>
            </el-radio-group>
          </div>

          <!-- 多选题 -->
          <div v-else-if="currentQuestion.questionType === 2" class="multiple-choice">
            <el-checkbox-group v-model="currentAnswer">
              <div v-for="option in currentQuestion.options" :key="option.optionId" class="option-item">
                <el-checkbox :label="option.optionLabel">
                  {{ option.optionLabel }}. {{ option.optionContent }}
                </el-checkbox>
                <img v-if="option.optionImage" :src="option.optionImage" alt="选项图片" class="option-image" />
              </div>
            </el-checkbox-group>
          </div>

          <!-- 判断题 -->
          <div v-else-if="currentQuestion.questionType === 3" class="true-false">
            <el-radio-group v-model="currentAnswer">
              <el-radio label="true">正确</el-radio>
              <el-radio label="false">错误</el-radio>
            </el-radio-group>
          </div>

          <!-- 填空题 -->
          <div v-else-if="currentQuestion.questionType === 4" class="fill-blank">
            <el-input 
              v-model="currentAnswer" 
              type="textarea" 
              :rows="3" 
              placeholder="请输入答案"
            ></el-input>
          </div>

          <!-- 简答题 -->
          <div v-else-if="currentQuestion.questionType === 5" class="essay">
            <el-input 
              v-model="currentAnswer" 
              type="textarea" 
              :rows="6" 
              placeholder="请输入答案"
            ></el-input>
          </div>
        </div>

        <div class="question-actions">
          <el-button type="success" @click="prevQuestion" :disabled="currentQuestionIndex === 0">上一题</el-button>
          <el-button v-if="currentQuestionIndex < questions.length - 1" type="primary" @click="nextQuestion">下一题</el-button>
          <el-button v-else type="warning" @click="submitExam">提交考试</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExamById } from '../../api/exam'
import { getQuestionsByExamId } from '../../api/question'
import { createExamRecord, submitExamRecord } from '../../api/examRecord'
import { batchSaveStudentAnswers, testStudentAnswerAPI, getStudentAnswers } from '../../api/studentAnswer'
import { getSessionStorage } from '../../utils/common'
import { getLatestExamRecord, checkExamSubmitted } from '../../api/examRecord'

export default {
  name: 'Exam',
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    // 响应式数据
    const examId = ref(null)
    const examInfo = ref(null)
    const questions = ref([])
    const currentQuestionIndex = ref(0)
    const answers = reactive({}) // 存储所有答案
    const loading = ref(false)
    const examStarted = ref(false)
    const examStartTime = ref(null)
    const remainingTime = ref(0)
    const timer = ref(null)
    const currentStudent = ref(null)
    const showResults = ref(false)
    const finalScore = ref(0)
    const currentRecordId = ref(null) // 当前考试记录ID

    // 计算属性
    const currentQuestion = computed(() => {
      return questions.value[currentQuestionIndex.value] || null
    })

    const currentAnswer = computed({
      get() {
        if (!currentQuestion.value) return null
        const questionId = currentQuestion.value.questionId
        return answers[questionId] || (currentQuestion.value.questionType === 2 ? [] : '')
      },
      set(value) {
        if (currentQuestion.value) {
          const questionId = currentQuestion.value.questionId
          answers[questionId] = value
          console.log('设置答案:', questionId, '=', value)
        }
      }
    })

    // 方法
    const initStudentInfo = () => {
      const currentUser = getSessionStorage('curuser')
      if (currentUser && currentUser.info) {
        currentStudent.value = currentUser.info
      }
    }

    const loadExamInfo = async () => {
      loading.value = true
      try {
        const res = await getExamById(examId.value)
        if (res && res.flag) {
          examInfo.value = res.result
          console.log('考试信息:', examInfo.value)
        } else {
          ElMessage.error(res?.reason || '获取考试信息失败')
          goBack()
        }
      } catch (e) {
        console.error('获取考试信息失败', e)
        ElMessage.error('获取考试信息失败')
        goBack()
      } finally {
        loading.value = false
      }
    }

    const loadQuestions = async () => {
      loading.value = true
      try {
        console.log('开始获取题目，examId:', examId.value)
        const res = await getQuestionsByExamId(examId.value)
        console.log('API响应:', res)
        
        if (res && res.flag) {
          let allQuestions = res.result || []
          
          // 根据考试类型过滤题目
          if (examInfo.value && examInfo.value.examType === 1) {
            // 练习：只包含客观题（单选、多选、判断）
            questions.value = allQuestions.filter(q => q.questionType <= 3)
            console.log('练习模式，过滤后的客观题:', questions.value.length)
          } else {
            // 考试：包含所有题型
            questions.value = allQuestions
            console.log('考试模式，包含所有题型:', questions.value.length)
          }
          
          console.log('题目列表:', questions.value)
          
          // 初始化答案对象
          questions.value.forEach(question => {
            console.log('初始化题目答案:', question.questionId, '类型:', question.questionType)
            if (question.questionType === 2) {
              answers[question.questionId] = []
            } else {
              answers[question.questionId] = ''
            }
          })
          console.log('初始化的答案对象:', answers)
        } else {
          console.error('获取题目失败，响应:', res)
          ElMessage.error(res?.reason || '获取题目失败')
        }
      } catch (e) {
        console.error('获取题目失败，错误:', e)
        ElMessage.error('获取题目失败')
      } finally {
        loading.value = false
      }
    }

    const startExam = async () => {
      // 考试模式下，仅在有“未完成记录”时提示是否继续；不再限制只能参加一次
      if (examInfo.value && examInfo.value.examType === 2) {
        try {
          const studentId = currentStudent.value.stuId || currentStudent.value.studentId
          const latestRes = await getLatestExamRecord(studentId, examId.value)
          if (latestRes && latestRes.flag && latestRes.result) {
            const latestRecord = latestRes.result
            if (latestRecord.examStatus === 0) {
              // 有进行中的记录，询问是否继续
              try {
                await ElMessageBox.confirm(
                  '检测到你有未完成的考试，是否继续上次的考试？',
                  '提示',
                  {
                    confirmButtonText: '继续考试',
                    cancelButtonText: '重新开始',
                    type: 'warning'
                  }
                )
                // 用户选择继续，恢复考试状态
                await resumeExam(latestRecord)
                return
              } catch {
                // 用户选择重新开始，继续创建新记录
                console.log('用户选择重新开始考试')
              }
            }
          }
        } catch (e) {
          console.error('检查考试记录失败', e)
        }
      }
      
      await loadQuestions()
      
      if (questions.value.length === 0) {
        ElMessage.error('该考试暂无题目')
        return
      }
      
      // 创建考试记录
      try {
        const studentId = currentStudent.value.stuId || currentStudent.value.studentId
        const totalScore = examInfo.value.totalScore
        
        const recordRes = await createExamRecord({
          studentId: studentId,
          examId: examId.value,
          totalScore: totalScore
        })
        
        if (recordRes && recordRes.flag) {
          currentRecordId.value = recordRes.result.recordId
          console.log('创建考试记录成功，recordId:', currentRecordId.value)
        } else {
          ElMessage.error('创建考试记录失败')
          return
        }
      } catch (e) {
        console.error('创建考试记录失败', e)
        ElMessage.error('创建考试记录失败')
        return
      }
      
      examStarted.value = true
      examStartTime.value = Date.now() // 记录开始时间戳
      
      // 如果有时间限制，启动计时器
      if (examInfo.value && examInfo.value.timeLimit) {
        remainingTime.value = examInfo.value.timeLimit * 60 // 转换为秒
        startTimer()
      }
    }

    // 恢复考试
    const resumeExam = async (record) => {
      try {
        currentRecordId.value = record.recordId
        console.log('恢复考试，recordId:', currentRecordId.value)
        
        // 加载题目
        await loadQuestions()
        
        if (questions.value.length === 0) {
          ElMessage.error('该考试暂无题目')
          return
        }
        
        // 加载已作答的答案
        const answersRes = await getStudentAnswers(currentRecordId.value)
        if (answersRes && answersRes.flag && answersRes.result) {
          const savedAnswers = answersRes.result
          console.log('已保存的答案:', savedAnswers)
          
          // 恢复答案到answers对象
          savedAnswers.forEach(answer => {
            const questionId = answer.questionId || answer.question_id
            let studentAnswer = answer.studentAnswer || answer.student_answer
            
            // 根据题型处理答案格式
            const question = questions.value.find(q => q.questionId === questionId)
            if (question) {
              if (question.questionType === 2) {
                // 多选题，转换为数组
                try {
                  studentAnswer = JSON.parse(studentAnswer)
                } catch {
                  studentAnswer = []
                }
              } else if (question.questionType === 3) {
                // 判断题，转换为字符串
                studentAnswer = String(studentAnswer)
              }
              answers[questionId] = studentAnswer
            }
          })
          
          console.log('恢复的答案:', answers)
        }
        
        // 计算剩余时间
        if (examInfo.value && examInfo.value.timeLimit) {
          const startTime = new Date(record.startTime).getTime()
          const now = Date.now()
          const usedSeconds = Math.floor((now - startTime) / 1000)
          const totalSeconds = examInfo.value.timeLimit * 60
          remainingTime.value = Math.max(0, totalSeconds - usedSeconds)
          
          console.log('已用时间:', usedSeconds, '秒，剩余时间:', remainingTime.value, '秒')
          
          if (remainingTime.value > 0) {
            startTimer()
          } else {
            ElMessage.warning('考试时间已到，请提交考试')
            timeUp()
            return
          }
        }
        
        examStarted.value = true
        examStartTime.value = new Date(record.startTime).getTime()
        
        ElMessage.success('已恢复上次考试进度')
      } catch (e) {
        console.error('恢复考试失败', e)
        ElMessage.error('恢复考试失败')
      }
    }

    const startTimer = () => {
      timer.value = setInterval(() => {
        remainingTime.value--
        if (remainingTime.value <= 0) {
          timeUp()
        }
      }, 1000)
    }

    const timeUp = () => {
      clearInterval(timer.value)
      ElMessage.warning('考试时间到，系统将自动提交')
      submitExam()
    }

    const prevQuestion = async () => {
      // 保存当前题目的答案
      await saveCurrentAnswer()
      
      if (currentQuestionIndex.value > 0) {
        currentQuestionIndex.value--
      }
    }

    const nextQuestion = async () => {
      // 保存当前题目的答案
      await saveCurrentAnswer()
      
      if (currentQuestionIndex.value < questions.value.length - 1) {
        currentQuestionIndex.value++
      }
    }

    // 保存当前题目的答案
    const saveCurrentAnswer = async () => {
      if (!currentQuestion.value || !currentRecordId.value) return
      
      const questionId = currentQuestion.value.questionId
      const answer = answers[questionId]
      
      // 如果没有答案，跳过
      if (answer === undefined || answer === null || answer === '') return
      
      try {
        // 格式化答案
        let formattedAnswer = answer
        if (currentQuestion.value.questionType === 2) {
          // 多选题，转换为JSON字符串
          formattedAnswer = JSON.stringify(answer)
        }
        
        // 调用保存接口
        const answerData = {
          recordId: currentRecordId.value,
          answers: [{
            questionId: questionId,
            studentAnswer: formattedAnswer,
            questionScore: currentQuestion.value.questionScore
          }]
        }
        
        await batchSaveStudentAnswers(answerData)
        console.log('自动保存答案成功:', questionId)
      } catch (e) {
        console.error('自动保存答案失败', e)
      }
    }

    const submitExam = async () => {
      try {
        // 检查未答题
        const unansweredQuestions = []
        questions.value.forEach((question, index) => {
          const answer = answers[question.questionId]
          if (!answer || (Array.isArray(answer) && answer.length === 0) || answer === '') {
            unansweredQuestions.push(index + 1)
          }
        })
        // 如果有未答题，提示用户
        if (unansweredQuestions.length > 0) {
          const message = `还有 ${unansweredQuestions.length} 道题未作答：第 ${unansweredQuestions.join('、')} 题。是否确定提交？`
          await ElMessageBox.confirm(message, '确认提交', { 
            type: 'warning',
            confirmButtonText: '确定提交',
            cancelButtonText: '继续答题'
          })
        } else {
          await ElMessageBox.confirm('所有题目已完成，确定提交考试吗？提交后无法修改答案。', '确认提交', { 
            type: 'info',
            confirmButtonText: '确定提交',
            cancelButtonText: '再检查一下'
          })
        }
        // 停止计时器
        if (timer.value) {
          clearInterval(timer.value)
        }
        // 计算用时
        const timeUsed = examInfo.value && examInfo.value.timeLimit 
          ? examInfo.value.timeLimit - Math.floor(remainingTime.value / 60)
          : Math.floor((Date.now() - examStartTime.value) / 60000)
        
        // 如果是练习，立即计算得分
        let studentScore = null
        if (examInfo.value && examInfo.value.examType === 1) {
          studentScore = calculateScore()
        }
        // 保存学生答案到数据库
        if (currentRecordId.value) {
          try {
            // 先测试API是否可达
            console.log('测试StudentAnswer API...')
            const testRes = await testStudentAnswerAPI()
            console.log('API测试结果:', testRes)
            
            if (!testRes || !testRes.flag) {
              console.error('StudentAnswer API不可达')
              ElMessage.error('系统错误：答案保存服务不可用')
              return
            }
            
            const answersToSave = []
            questions.value.forEach(question => {
              const answer = answers[question.questionId]
              let answerStr = ''
              
              if (Array.isArray(answer)) {
                // 多选题答案
                answerStr = JSON.stringify(answer)
              } else {
                // 单选、判断、填空、简答题答案
                answerStr = answer ? answer.toString() : ''
              }
              
              answersToSave.push({
                questionId: question.questionId,
                studentAnswer: answerStr,
                questionScore: question.questionScore
              })
            })
            
            console.log('准备保存的答案:', answersToSave)
            
            const saveAnswersRes = await batchSaveStudentAnswers({
              recordId: currentRecordId.value,
              answers: answersToSave
            })
            
            console.log('保存答案响应:', saveAnswersRes)
            
            if (!saveAnswersRes || !saveAnswersRes.flag) {
              console.error('保存答案失败:', saveAnswersRes)
              ElMessage.error('保存答案失败，请重试')
              return
            }
            
            console.log('答案保存成功')
          } catch (e) {
            console.error('保存答案失败', e)
            ElMessage.error('保存答案失败，请重试')
            return
          }
        }
        // 提交考试记录
        if (currentRecordId.value) {
          const submitRes = await submitExamRecord({
            recordId: currentRecordId.value,
            timeUsed: timeUsed,
            studentScore: studentScore
          })
          
          if (submitRes && submitRes.flag) {
            if (examInfo.value && examInfo.value.examType === 1) {
              ElMessage.success(`练习完成！得分：${studentScore}/${examInfo.value.totalScore}`)
              finalScore.value = studentScore
              showResults.value = true
            } else {
              ElMessage.success('考试提交成功！请等待老师批改。')
            }
          } else {
            ElMessage.error(submitRes?.reason || '提交失败')
            return
          }
        } else {
          ElMessage.error('考试记录ID丢失，提交失败')
          return
        }
        console.log('提交的答案:', answers)
        console.log('用时:', timeUsed, '分钟')
        // 跳转回考试列表
        setTimeout(() => {
          goBack(true) // 提交成功后直接返回，不需要确认
        }, 2000)
        
      } catch (e) {
        // 用户取消提交
        if (e !== 'cancel') {
          console.error('提交失败', e)
          ElMessage.error('提交失败，请重试')
        }
      }
    }

    // 计算练习得分（仅客观题）
    const calculateScore = () => {
      let totalScore = 0
      questions.value.forEach(question => {
        // 只计算客观题（单选、多选、判断）
        if (question.questionType <= 3) {
          const userAnswer = answers[question.questionId]
          const correctAnswer = JSON.parse(question.correctAnswer || '[]')
          
          let isCorrect = false
          if (question.questionType === 1) {
            // 单选题
            isCorrect = userAnswer === correctAnswer[0]
          } else if (question.questionType === 2) {
            // 多选题
            isCorrect = Array.isArray(userAnswer) && 
                       userAnswer.length === correctAnswer.length &&
                       userAnswer.every(ans => correctAnswer.includes(ans))
          } else if (question.questionType === 3) {
            // 判断题
            isCorrect = userAnswer === correctAnswer
          }
          
          if (isCorrect) {
            totalScore += question.questionScore
          }
        }
      })
      return totalScore
    }

    const goBack = async (skipConfirm = false) => {
      // 如果跳过确认或考试未开始，直接返回
      if (skipConfirm || !examStarted.value || !currentRecordId.value) {
        router.go(-1)
        return
      }
      
      // 如果考试已经开始，询问是否退出
      try {
        await ElMessageBox.confirm(
          '考试尚未提交，确定要退出吗？退出后可以继续作答。',
          '提示',
          {
            confirmButtonText: '确定退出',
            cancelButtonText: '继续考试',
            type: 'warning'
          }
        )
        // 用户确认退出，清除定时器
        if (timer.value) {
          clearInterval(timer.value)
        }
        router.go(-1)
      } catch {
        // 用户取消，不做任何操作
      }
    }

    const formatTime = (seconds) => {
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = seconds % 60
      return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
    }

    const getQuestionTypeText = (type) => {
      const types = { 1: '单选题', 2: '多选题', 3: '判断题', 4: '填空题', 5: '简答题' }
      return types[type] || '未知'
    }

    const getQuestionTypeColor = (type) => {
      const colors = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'info', 5: 'danger' }
      return colors[type] || 'info'
    }

    // 生命周期
    onMounted(() => {
      examId.value = route.query.examId
      if (!examId.value) {
        ElMessage.error('考试ID不能为空')
        router.push('/student/courselist')
        return
      }
      
      initStudentInfo()
      loadExamInfo()
    })

    onUnmounted(() => {
      if (timer.value) {
        clearInterval(timer.value)
      }
    })

    return {
      examId,
      examInfo,
      questions,
      currentQuestionIndex,
      answers,
      loading,
      examStarted,
      examStartTime,
      remainingTime,
      timer,
      currentStudent,
      currentQuestion,
      currentAnswer,
      currentRecordId,
      initStudentInfo,
      loadExamInfo,
      loadQuestions,
      startExam,
      startTimer,
      timeUp,
      prevQuestion,
      nextQuestion,
      submitExam,
      goBack,
      formatTime,
      getQuestionTypeText,
      getQuestionTypeColor
    }
  }
}
</script>

<style scoped>
.exam-page {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.exam-info h2 {
  margin: 0 0 10px 0;
  color: #333;
}

.exam-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #666;
}

.exam-timer {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #e6a23c;
  font-weight: bold;
}

.exam-instructions {
  margin-bottom: 20px;
}

.instructions-content ul {
  margin: 15px 0;
  padding-left: 20px;
}

.instructions-content li {
  margin: 8px 0;
  line-height: 1.6;
}

.start-actions {
  text-align: center;
  margin-top: 20px;
}

.exam-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.question-progress {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  gap: 15px;
}

.question-score {
  color: #409eff;
  font-weight: bold;
  font-size: 16px;
}

.question-container {
  padding: 30px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.question-header h3 {
  margin: 0;
  color: #333;
  flex: 1;
  line-height: 1.6;
}

.question-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: 20px;
}

.question-image {
  margin: 20px 0;
  text-align: center;
}

.question-image img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
}

.answer-area {
  margin: 30px 0;
}

.option-item {
  margin: 15px 0;
  padding: 10px;
  border-radius: 6px;
  transition: background-color 0.2s;
}

.option-item:hover {
  background: #f8f9fa;
}

.option-image {
  display: block;
  max-width: 200px;
  height: auto;
  margin: 10px 0 0 25px;
  border-radius: 4px;
}

.question-actions {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.question-actions .el-button {
  margin: 0 10px;
}
</style>