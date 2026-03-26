<template>
  <div class="question-manager">
    <div class="header">
      <h2>{{ examTitle ? `${examTitle} - 题目管理` : `${courseTitle} - 题目管理` }}</h2>
      <el-button type="primary" @click="onAddQuestion">
        <el-icon><Plus /></el-icon>
        创建题目
      </el-button>
    </div>

    <!-- 题目列表 -->
    <div class="question-list" v-loading="loading">
      <el-table :data="questions" border style="width: 100%">
        <el-table-column prop="questionContent" label="题目内容" min-width="300" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="question-content-cell">
              <span>{{ row.questionContent }}</span>
              <el-tag :type="getQuestionTypeColor(row.questionType)" size="small">
                {{ getQuestionTypeText(row.questionType) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="difficultyLevel" label="难度" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getDifficultyColor(row.difficultyLevel)" size="small">
              {{ getDifficultyText(row.difficultyLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="questionScore" label="分值" width="80" align="center" />
        
        <el-table-column prop="createTime" label="创建时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="onEditQuestion(row)">
              编辑
            </el-button>
            <el-button type="info" size="small" @click="onViewQuestion(row)">
              预览
            </el-button>
            <el-button type="danger" size="small" @click="onDeleteQuestion(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && questions.length === 0" class="empty-state">
      <el-empty description="暂无题目，点击上方按钮创建题目"></el-empty>
    </div>

    <!-- 题目编辑对话框 -->
    <el-dialog 
      :title="questionDialogTitle" 
      v-model="questionDialogVisible" 
      width="70%"
      :close-on-click-modal="false"
    >
      <el-form :model="questionForm" ref="questionFormRef" label-width="120px">
        <el-form-item 
          label="题目类型" 
          prop="questionType" 
          :rules="[{ required: true, message: '请选择题目类型', trigger: 'change' }]"
        >
          <el-radio-group v-model="questionForm.questionType" @change="onQuestionTypeChange">
            <el-radio :label="1">单选题</el-radio>
            <el-radio :label="2">多选题</el-radio>
            <el-radio :label="3">判断题</el-radio>
            <el-radio :label="4">填空题</el-radio>
            <el-radio :label="5">简答题</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item 
          label="题目内容" 
          prop="questionContent" 
          :rules="[{ required: true, message: '请输入题目内容', trigger: 'blur' }]"
        >
          <el-input 
            v-model="questionForm.questionContent" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入题目内容">
          </el-input>
        </el-form-item>
        
        <el-form-item label="题目图片" prop="questionImage">
          <el-input v-model="questionForm.questionImage" placeholder="请输入题目图片URL（可选）"></el-input>
        </el-form-item>
        
        <!-- 选择题选项 -->
        <div v-if="questionForm.questionType === 1 || questionForm.questionType === 2" class="options-section">
          <el-form-item label="题目选项">
            <div class="options-container">
              <div v-for="(option, index) in questionForm.options" :key="index" class="option-item">
                <div class="option-header">
                  <span class="option-label">选项 {{ option.optionLabel }}</span>
                  <el-button 
                    v-if="questionForm.options.length > 2" 
                    type="danger" 
                    size="small" 
                    @click="removeOption(index)"
                  >
                    删除
                  </el-button>
                </div>
                <div class="option-content">
                  <el-input 
                    v-model="option.optionContent" 
                    placeholder="请输入选项内容"
                    style="margin-bottom: 10px;"
                  ></el-input>
                  <el-input 
                    v-model="option.optionImage" 
                    placeholder="选项图片URL（可选）"
                    style="margin-bottom: 10px;"
                  ></el-input>
                  <el-checkbox 
                    v-if="questionForm.questionType === 2" 
                    v-model="option.isCorrect"
                    @change="onMultipleChoiceChange"
                  >
                    正确答案
                  </el-checkbox>
                  <el-radio-group 
                    v-else 
                    v-model="singleCorrectAnswer" 
                    @change="onSingleChoiceChange"
                  >
                    <el-radio :label="option.optionLabel">正确答案</el-radio>
                  </el-radio-group>
                </div>
              </div>
              <el-button 
                v-if="questionForm.options.length < 6" 
                type="success" 
                @click="addOption"
              >
                添加选项
              </el-button>
            </div>
          </el-form-item>
        </div>
        
        <!-- 判断题答案 -->
        <el-form-item v-if="questionForm.questionType === 3" label="正确答案">
          <el-radio-group v-model="questionForm.correctAnswer">
            <el-radio label="true">正确</el-radio>
            <el-radio label="false">错误</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <!-- 填空题/简答题答案 -->
        <el-form-item 
          v-if="questionForm.questionType === 4 || questionForm.questionType === 5" 
          label="参考答案"
        >
          <el-input 
            v-model="questionForm.correctAnswer" 
            type="textarea" 
            :rows="3" 
            :placeholder="questionForm.questionType === 4 ? '请输入标准答案，多个答案用逗号分隔' : '请输入参考答案'"
          ></el-input>
        </el-form-item>
        
        <el-row>
          <el-col :span="8">
            <el-form-item label="难度等级" prop="difficultyLevel">
              <el-radio-group v-model="questionForm.difficultyLevel">
                <el-radio :label="1">简单</el-radio>
                <el-radio :label="2">中等</el-radio>
                <el-radio :label="3">困难</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="题目分值" prop="questionScore">
              <el-input-number 
                v-model="questionForm.questionScore" 
                :min="1" 
                :max="100"
                style="width: 100%;"
              ></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="题目解析" prop="questionAnalysis">
          <el-input 
            v-model="questionForm.questionAnalysis" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入题目解析（可选）">
          </el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button type="success" @click="questionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitQuestionForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 题目预览对话框 -->
    <el-dialog title="题目预览" v-model="previewDialogVisible" width="60%">
      <div v-if="previewQuestion" class="question-preview">
        <div class="preview-header">
          <h3>{{ previewQuestion.questionContent }}</h3>
          <div class="preview-meta">
            <el-tag :type="getQuestionTypeColor(previewQuestion.questionType)">
              {{ getQuestionTypeText(previewQuestion.questionType) }}
            </el-tag>
            <el-tag :type="getDifficultyColor(previewQuestion.difficultyLevel)">
              {{ getDifficultyText(previewQuestion.difficultyLevel) }}
            </el-tag>
            <span>{{ previewQuestion.questionScore }}分</span>
          </div>
        </div>
        
        <div v-if="previewQuestion.questionImage" class="preview-image">
          <img :src="previewQuestion.questionImage" alt="题目图片" />
        </div>
        
        <!-- 选择题才显示选项；判断 / 填空 / 简答不展示这一块 -->
        <div
          class="preview-options"
          v-if="previewQuestion.questionType !== 3 && previewQuestion.options && previewQuestion.options.length > 0"
        >
          <div v-for="option in previewQuestion.options" :key="option.optionId" class="preview-option">
            <span class="option-label" :class="{ correct: option.isCorrect }">
              {{ option.optionLabel }}.
            </span>
            <span class="option-content">{{ option.optionContent }}</span>
            <img v-if="option.optionImage" :src="option.optionImage" alt="选项图片" class="option-image" />
          </div>
        </div>
        
        <div v-if="previewQuestion.correctAnswer" class="preview-answer">
          <strong>正确答案：</strong>{{ previewQuestion.correctAnswer }}
        </div>
        
        <div v-if="previewQuestion.questionAnalysis" class="preview-analysis">
          <strong>题目解析：</strong>{{ previewQuestion.questionAnalysis }}
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button type="success" @click="previewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getQuestionsByCourseId, getQuestionsByExamId, createQuestion, updateQuestion, deleteQuestion } from '../../api/question'

export default {
  name: 'QuestionManager',
  components: {
    Plus
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    // 响应式数据
    const courseId = ref(null)
    const courseTitle = ref('')
    const examId = ref(null)
    const examTitle = ref('')
    const questions = ref([])
    const loading = ref(false)
    
    // 题目对话框
    const questionDialogVisible = ref(false)
    const questionDialogTitle = ref('创建题目')
    const questionFormRef = ref(null)
    const questionForm = reactive({
      questionType: 1,
      questionContent: '',
      questionImage: '',
      correctAnswer: '',
      questionAnalysis: '',
      difficultyLevel: 1,
      questionScore: 5,
      options: []
    })
    const singleCorrectAnswer = ref('') // 单选题正确答案
    
    // 预览对话框
    const previewDialogVisible = ref(false)
    const previewQuestion = ref(null)

    // 初始化
    onMounted(() => {
      // 从路由参数获取课程和考试信息
      courseId.value = route.query.courseId
      courseTitle.value = route.query.courseTitle || '课程'
      examId.value = route.query.examId
      examTitle.value = route.query.examTitle || ''
      
      if (!courseId.value) {
        ElMessage.error('课程ID不能为空')
        router.push('/teacher/coursemanager')
        return
      }
      
      loadQuestions()
    })

    // 加载题目列表
    const loadQuestions = async () => {
      loading.value = true
      try {
        let res
        if (examId.value) {
          // 如果有考试ID，获取该考试的题目
          res = await getQuestionsByExamId(examId.value)
        } else {
          // 否则获取课程的所有题目
          res = await getQuestionsByCourseId(courseId.value)
        }
        
        if (res && res.flag) {
          questions.value = res.result || []
        } else {
          questions.value = []
          ElMessage.error(res?.reason || '获取题目列表失败')
        }
      } catch (e) {
        console.error('获取题目列表失败', e)
        questions.value = []
        ElMessage.error('获取题目列表失败')
      } finally {
        loading.value = false
      }
    }

    // 添加题目
    const onAddQuestion = () => {
      questionDialogTitle.value = '创建题目'
      Object.assign(questionForm, {
        questionType: 1,
        questionContent: '',
        questionImage: '',
        correctAnswer: '',
        questionAnalysis: '',
        difficultyLevel: 1,
        questionScore: 5,
        options: [
          { optionLabel: 'A', optionContent: '', optionImage: '', isCorrect: false, sortOrder: 1 },
          { optionLabel: 'B', optionContent: '', optionImage: '', isCorrect: false, sortOrder: 2 },
          { optionLabel: 'C', optionContent: '', optionImage: '', isCorrect: false, sortOrder: 3 },
          { optionLabel: 'D', optionContent: '', optionImage: '', isCorrect: false, sortOrder: 4 }
        ]
      })
      singleCorrectAnswer.value = ''
      questionDialogVisible.value = true
    }

    // 编辑题目
    const onEditQuestion = (question) => {
      questionDialogTitle.value = '编辑题目'
      Object.assign(questionForm, {
        questionId: question.questionId,
        questionType: question.questionType,
        questionContent: question.questionContent,
        questionImage: question.questionImage || '',
        correctAnswer: question.correctAnswer || '',
        questionAnalysis: question.questionAnalysis || '',
        difficultyLevel: question.difficultyLevel,
        questionScore: question.questionScore,
        options: question.options || []
      })
      
      // 如果是选择题但没有选项，初始化选项
      if ((question.questionType === 1 || question.questionType === 2) && (!question.options || question.options.length === 0)) {
        questionForm.options = [
          { optionLabel: 'A', optionContent: '', optionImage: '', isCorrect: false, sortOrder: 1 },
          { optionLabel: 'B', optionContent: '', optionImage: '', isCorrect: false, sortOrder: 2 },
          { optionLabel: 'C', optionContent: '', optionImage: '', isCorrect: false, sortOrder: 3 },
          { optionLabel: 'D', optionContent: '', optionImage: '', isCorrect: false, sortOrder: 4 }
        ]
      }
      
      // 设置单选题的正确答案
      if (question.questionType === 1 && question.options) {
        const correctOption = question.options.find(opt => opt.isCorrect)
        singleCorrectAnswer.value = correctOption ? correctOption.optionLabel : ''
      }
      
      questionDialogVisible.value = true
    }

    // 预览题目
    const onViewQuestion = (question) => {
      previewQuestion.value = question
      previewDialogVisible.value = true
    }

    // 删除题目
    const onDeleteQuestion = async (question) => {
      try {
        await ElMessageBox.confirm('确定删除该题目吗？删除后无法恢复。', '确认', { type: 'warning' })
        const res = await deleteQuestion(question.questionId)
        if (res && res.flag) {
          ElMessage.success(res.reason || '删除成功')
          loadQuestions()
        } else {
          ElMessage.error(res?.reason || '删除失败')
        }
      } catch (e) {
        if (e !== 'cancel') {
          console.error('删除失败', e)
          ElMessage.error('删除失败')
        }
      }
    }

    // 题目类型变化
    const onQuestionTypeChange = (type) => {
      // 切换题目类型时重置相关数据
      if (type === 1 || type === 2) {
        // 选择题，初始化选项
        if (!questionForm.options || questionForm.options.length === 0) {
          questionForm.options = [
            { optionLabel: 'A', optionContent: '', optionImage: '', isCorrect: false, sortOrder: 1 },
            { optionLabel: 'B', optionContent: '', optionImage: '', isCorrect: false, sortOrder: 2 },
            { optionLabel: 'C', optionContent: '', optionImage: '', isCorrect: false, sortOrder: 3 },
            { optionLabel: 'D', optionContent: '', optionImage: '', isCorrect: false, sortOrder: 4 }
          ]
        }
      } else {
        // 非选择题，清空选项
        questionForm.options = []
      }
      
      if (type === 3) {
        // 判断题，设置默认答案
        questionForm.correctAnswer = 'true'
      } else {
        questionForm.correctAnswer = ''
      }
      
      singleCorrectAnswer.value = ''
    }

    // 添加选项
    const addOption = () => {
      const labels = ['A', 'B', 'C', 'D', 'E', 'F']
      const nextLabel = labels[questionForm.options.length]
      if (nextLabel) {
        questionForm.options.push({
          optionLabel: nextLabel,
          optionContent: '',
          optionImage: '',
          isCorrect: false,
          sortOrder: questionForm.options.length + 1
        })
      }
    }

    // 删除选项
    const removeOption = (index) => {
      questionForm.options.splice(index, 1)
      // 重新分配标签
      const labels = ['A', 'B', 'C', 'D', 'E', 'F']
      questionForm.options.forEach((option, idx) => {
        option.optionLabel = labels[idx]
        option.sortOrder = idx + 1
      })
    }

    // 单选题答案变化
    const onSingleChoiceChange = (value) => {
      // 单选题：只能选择一个正确答案
      questionForm.options.forEach(option => {
        option.isCorrect = option.optionLabel === value
      })
      updateCorrectAnswer()
    }

    // 多选题答案变化
    const onMultipleChoiceChange = () => {
      // 多选题：可以选择多个正确答案
      updateCorrectAnswer()
    }

    // 更新正确答案
    const updateCorrectAnswer = () => {
      const correctOptions = questionForm.options
        .filter(option => option.isCorrect)
        .map(option => option.optionLabel)
      questionForm.correctAnswer = JSON.stringify(correctOptions)
    }

    // 提交表单
    const submitQuestionForm = async () => {
      try {
        await questionFormRef.value.validate()
        
        // 验证选择题必须有正确答案
        if (questionForm.questionType === 1 || questionForm.questionType === 2) {
          const hasCorrect = questionForm.options.some(option => option.isCorrect)
          if (!hasCorrect) {
            ElMessage.error('请至少选择一个正确答案')
            return
          }
          
          // 验证选项内容不能为空
          const emptyOption = questionForm.options.find(option => !option.optionContent.trim())
          if (emptyOption) {
            ElMessage.error('选项内容不能为空')
            return
          }
        }
        
        const params = {
          courseId: courseId.value,
          questionType: questionForm.questionType,
          questionContent: questionForm.questionContent,
          questionImage: questionForm.questionImage,
          correctAnswer: questionForm.correctAnswer,
          questionAnalysis: questionForm.questionAnalysis,
          difficultyLevel: questionForm.difficultyLevel,
          questionScore: questionForm.questionScore
        }
        
        // 如果有选项，序列化为JSON
        let optionsJson = ''
        if (questionForm.options && questionForm.options.length > 0) {
          optionsJson = JSON.stringify(questionForm.options)
        }
        params.optionsJson = optionsJson
        
        let res
        if (questionForm.questionId) {
          // 更新题目
          params.questionId = questionForm.questionId
          res = await updateQuestion(params)
        } else {
          // 创建题目
          res = await createQuestion(params)
        }
        
        if (res && res.flag) {
          ElMessage.success(res.reason || '保存成功')
          questionDialogVisible.value = false
          loadQuestions()
        } else {
          ElMessage.error(res?.reason || '保存失败')
        }
      } catch (e) {
        console.error('保存题目失败', e)
        ElMessage.error('保存失败')
      }
    }

    // 工具方法
    const getQuestionTypeText = (type) => {
      const types = { 1: '单选题', 2: '多选题', 3: '判断题', 4: '填空题', 5: '简答题' }
      return types[type] || '未知'
    }

    const getQuestionTypeColor = (type) => {
      const colors = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'info', 5: 'danger' }
      return colors[type] || 'info'
    }

    const getDifficultyText = (level) => {
      const texts = { 1: '简单', 2: '中等', 3: '困难' }
      return texts[level] || '未知'
    }

    const getDifficultyColor = (level) => {
      const colors = { 1: 'success', 2: 'warning', 3: 'danger' }
      return colors[level] || 'info'
    }

    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    }

    return {
      courseTitle,
      examTitle,
      questions,
      loading,
      questionDialogVisible,
      questionDialogTitle,
      questionFormRef,
      questionForm,
      singleCorrectAnswer,
      previewDialogVisible,
      previewQuestion,
      loadQuestions,
      onAddQuestion,
      onEditQuestion,
      onViewQuestion,
      onDeleteQuestion,
      onQuestionTypeChange,
      addOption,
      removeOption,
      onSingleChoiceChange,
      onMultipleChoiceChange,
      submitQuestionForm,
      getQuestionTypeText,
      getQuestionTypeColor,
      getDifficultyText,
      getDifficultyColor,
      formatDate
    }
  }
}
</script>

<style scoped>
.question-manager {
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

.question-content-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.question-list {
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.options-section {
  border: 1px solid #eee;
  border-radius: 6px;
  padding: 15px;
  margin: 15px 0;
}

.options-container {
  max-height: 400px;
  overflow-y: auto;
}

.option-item {
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  padding: 15px;
  margin-bottom: 15px;
  background: #fafafa;
}

.option-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.option-label {
  font-weight: bold;
  color: #333;
}

.option-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.question-preview {
  padding: 20px;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.preview-header h3 {
  margin: 0;
  flex: 1;
  line-height: 1.6;
}

.preview-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: 20px;
}

.preview-image {
  text-align: center;
  margin: 20px 0;
}

.preview-image img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
}

.preview-options {
  margin: 20px 0;
}

.preview-option {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 10px 0;
  padding: 10px;
  border-radius: 6px;
  background: #f8f9fa;
}

.preview-option .option-label {
  font-weight: bold;
  min-width: 30px;
}

.preview-option .option-label.correct {
  color: #67c23a;
}

.preview-option .option-image {
  max-width: 100px;
  height: auto;
  border-radius: 4px;
}

.preview-answer, .preview-analysis {
  margin: 15px 0;
  padding: 10px;
  background: #f0f9ff;
  border-radius: 6px;
  line-height: 1.6;
}
</style>