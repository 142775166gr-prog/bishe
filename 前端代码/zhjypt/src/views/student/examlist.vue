<template>
  <div class="exam-list">
    <div class="header">
      <h2>{{ courseTitle }} - 考试练习</h2>
    </div>

    <!-- 考试列表 -->
    <div class="exam-grid" v-loading="loading">
      <div class="exam-card" v-for="exam in exams" :key="exam.examId">
        <div class="exam-header">
          <h3 class="exam-title">{{ exam.examTitle }}</h3>
          <el-tag :type="exam.examType === 1 ? 'success' : 'primary'">
            {{ exam.examType === 1 ? '练习' : '考试' }}
          </el-tag>
        </div>
        
        <div class="exam-info">
          <p class="exam-desc">{{ exam.examDesc }}</p>
          <div class="exam-meta">
            <div class="meta-item">
              <span>🏆</span>
              <span>总分：{{ exam.totalScore }}分</span>
            </div>
            <div class="meta-item">
              <span>✅</span>
              <span>及格：{{ exam.passScore }}分</span>
            </div>
            <div class="meta-item" v-if="exam.timeLimit">
              <span>⏰</span>
              <span>时长：{{ exam.timeLimit }}分钟</span>
            </div>
            <div class="meta-item" v-else>
              <span>⏰</span>
              <span>不限时</span>
            </div>
          </div>
        </div>
        
        <div class="exam-actions">
          <el-button type="primary" @click="startExam(exam)">
            {{ exam.examType === 1 ? '开始练习' : '开始考试' }}
          </el-button>
          <el-button type="info" @click="viewHistory(exam)">
            查看记录
          </el-button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && exams.length === 0" class="empty-state">
      <el-empty description="暂无考试或练习"></el-empty>
    </div>
  </div>
</template>

<script>
import { getAvailableExams, getExamById } from '../../api/exam'
import { getSessionStorage } from '../../utils/common'

export default {
  name: 'ExamList',
  data() {
    return {
      courseId: null,
      courseTitle: '',
      exams: [],
      loading: false,
      currentStudent: null
    }
  },
  created() {
    this.courseId = this.$route.query.courseId
    this.courseTitle = this.$route.query.courseTitle || '课程'
    
    if (!this.courseId) {
      this.$message.error('课程ID不能为空')
      this.$router.push('/student/courselist')
      return
    }
    
    this.initStudentInfo()
    this.loadExams()
  },
  methods: {
    initStudentInfo() {
      const currentUser = getSessionStorage('curuser')
      if (currentUser && currentUser.info) {
        this.currentStudent = currentUser.info
      }
    },

    async loadExams() {
      this.loading = true
      try {
        if (!this.currentStudent || !this.currentStudent.stuId) {
          this.$message.error('请先登录')
          return
        }
        
        const res = await getAvailableExams(this.courseId, this.currentStudent.stuId)
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

    startExam(exam) {
      this.$router.push({
        path: '/student/exam',
        query: {
          examId: exam.examId,
          examTitle: exam.examTitle
        }
      })
    },

    viewHistory(exam) {
      this.$router.push({
        path: '/student/examhistory',
        query: {
          examId: exam.examId,
          examTitle: exam.examTitle
        }
      })
    }
  }
}
</script>

<style scoped>
.exam-list {
  padding: 20px;
  background: linear-gradient(180deg, #f8fbff 0%, #f3f4f6 100%);
  min-height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
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

.exam-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.exam-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
  background: white;
  transition: all 0.3s;
}

.exam-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.exam-title {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.exam-info {
  margin-bottom: 20px;
}

.exam-desc {
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
}

.exam-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #888;
  font-size: 14px;
}

.exam-actions {
  display: flex;
  gap: 10px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}
</style>