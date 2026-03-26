<template>
  <div class="course-students">
    <div class="header">
      <h2>课程学生学习情况</h2>
      <el-button @click="$router.back()">
        <el-icon><Back /></el-icon>
        返回
      </el-button>
    </div>

    <!-- 课程信息 -->
    <el-card class="course-info" v-if="courseInfo">
      <h3>{{ courseInfo.courseTitle }}</h3>
      <div class="info-row">
        <span>选课人数：{{ students.length }}人</span>
        <span>平均进度：{{ averageProgress }}%</span>
        <span>完成人数：{{ completedCount }}人</span>
      </div>
    </el-card>

    <!-- 筛选和搜索 -->
    <div class="filter-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索学生姓名或学号"
        style="width: 300px"
        clearable
        @input="filterStudents"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-select v-model="filterProgress" placeholder="学习进度筛选" clearable @change="filterStudents" style="width: 200px">
        <el-option label="全部" :value="null" />
        <el-option label="未开始（0%）" value="0" />
        <el-option label="学习中（1-99%）" value="1-99" />
        <el-option label="已完成（100%）" value="100" />
      </el-select>

      <el-button type="primary" @click="exportData">
        <el-icon><Download /></el-icon>
        导出数据
      </el-button>
    </div>

    <!-- 学生列表 -->
    <el-table :data="filteredStudents" border stripe style="width: 100%">
      <el-table-column type="index" label="序号" width="60" />
      
      <el-table-column prop="stuNum" label="学号" width="120" />
      
      <el-table-column prop="stuName" label="姓名" width="100" />
      
      <el-table-column label="学习进度" width="200">
        <template #default="{ row }">
          <el-progress :percentage="Math.round(row.progress || 0)" :color="getProgressColor(row.progress)" />
        </template>
      </el-table-column>
      
      <el-table-column label="章节学习" width="120">
        <template #default="{ row }">
          <span>{{ row.completedChapters || 0 }} / {{ row.totalChapters || 0 }}</span>
        </template>
      </el-table-column>
      
      <el-table-column label="考试情况" width="150">
        <template #default="{ row }">
          <div v-if="row.examCount > 0">
            <div>已完成：{{ row.completedExams || 0 }} / {{ row.examCount || 0 }}</div>
            <div v-if="row.avgScore !== null" style="color: #67c23a">
              平均分：{{ row.avgScore }}
            </div>
          </div>
          <span v-else style="color: #999">暂无考试</span>
        </template>
      </el-table-column>
      
      <el-table-column prop="enrollTime" label="选课时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.enrollTime) }}
        </template>
      </el-table-column>
      
      <el-table-column prop="lastStudyTime" label="最后学习" width="180">
        <template #default="{ row }">
          {{ row.lastStudyTime ? formatTime(row.lastStudyTime) : '未开始' }}
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="viewDetail(row)">
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 学生学习详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="学生学习详情" width="900px">
      <div v-if="currentStudent" class="student-detail">
        <!-- 基本信息 -->
        <el-descriptions :column="2" border>
          <el-descriptions-item label="学号">{{ currentStudent.stuNum }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ currentStudent.stuName }}</el-descriptions-item>
          <el-descriptions-item label="选课时间">{{ formatTime(currentStudent.enrollTime) }}</el-descriptions-item>
          <el-descriptions-item label="最后学习">{{ currentStudent.lastStudyTime ? formatTime(currentStudent.lastStudyTime) : '未开始' }}</el-descriptions-item>
          <el-descriptions-item label="学习进度">
            <el-progress :percentage="Math.round(currentStudent.progress || 0)" :color="getProgressColor(currentStudent.progress)" />
          </el-descriptions-item>
          <el-descriptions-item label="章节学习">{{ currentStudent.completedChapters || 0 }} / {{ currentStudent.totalChapters || 0 }}</el-descriptions-item>
        </el-descriptions>

        <!-- 章节学习详情 -->
        <h3 style="margin-top: 20px">章节学习详情</h3>
        <el-table :data="chapterProgress" border stripe max-height="300">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="chapterTitle" label="章节名称" />
          <el-table-column label="内容完成" width="120">
            <template #default="{ row }">
              {{ row.completedContents || 0 }} / {{ row.totalContents || 0 }}
            </template>
          </el-table-column>
          <el-table-column label="完成度" width="150">
            <template #default="{ row }">
              <el-progress :percentage="Math.round(row.progress || 0)" :show-text="true" />
            </template>
          </el-table-column>
          <el-table-column prop="lastStudyTime" label="最后学习" width="180">
            <template #default="{ row }">
              {{ row.lastStudyTime ? formatTime(row.lastStudyTime) : '未学习' }}
            </template>
          </el-table-column>
        </el-table>

        <!-- 考试情况 -->
        <h3 style="margin-top: 20px">考试情况</h3>
        <el-table :data="examRecords" border stripe max-height="300">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="examTitle" label="考试名称" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.examStatus === 0" type="info">未开始</el-tag>
              <el-tag v-else-if="row.examStatus === 1" type="warning">已提交</el-tag>
              <el-tag v-else-if="row.examStatus === 2" type="success">已评分</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="studentScore" label="得分" width="100">
            <template #default="{ row }">
              {{ row.studentScore !== null ? row.studentScore : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="totalScore" label="总分" width="100" />
          <el-table-column label="通过状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.passStatus === 1" type="success">通过</el-tag>
              <el-tag v-else-if="row.passStatus === 0" type="danger">未通过</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="submitTime" label="提交时间" width="180">
            <template #default="{ row }">
              {{ row.submitTime ? formatTime(row.submitTime) : '-' }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getCourseStudents, getStudentChapterProgress } from '../../api/studentCourse.js'
import { getCourseById } from '../../api/course.js'
import { getSessionStorage } from '../../utils/common.js'

export default {
  name: 'CourseStudents',
  data() {
    return {
      courseId: null,
      courseInfo: null,
      students: [],
      filteredStudents: [],
      searchKeyword: '',
      filterProgress: null,
      showDetailDialog: false,
      currentStudent: null,
      chapterProgress: [],
      examRecords: []
    }
  },
  computed: {
    currentUser() {
      const user = getSessionStorage('curuser')
      return user ? user.info : null
    },
    averageProgress() {
      if (this.students.length === 0) return 0
      const total = this.students.reduce((sum, s) => sum + (s.progress || 0), 0)
      return Math.round(total / this.students.length)
    },
    completedCount() {
      return this.students.filter(s => (s.progress || 0) >= 100).length
    }
  },
  mounted() {
    this.courseId = this.$route.query.courseId
    if (this.courseId) {
      this.loadCourseInfo()
      this.loadStudents()
    } else {
      // 没有课程ID参数，静默返回
      this.$router.back()
    }
  },
  methods: {
    async loadCourseInfo() {
      try {
        const res = await getCourseById(this.courseId)
        if (res.flag) {
          this.courseInfo = res.result
        }
      } catch (e) {
        console.error('加载课程信息失败', e)
      }
    },

    async loadStudents() {
      try {
        const res = await getCourseStudents(this.courseId)
        if (res.flag) {
          this.students = res.result || []
          this.filteredStudents = this.students
        }
      } catch (e) {
        console.error('加载学生列表失败', e)
        this.$message.error('加载学生列表失败')
      }
    },

    filterStudents() {
      let result = this.students

      // 关键词搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        result = result.filter(s => 
          s.stuName.toLowerCase().includes(keyword) || 
          s.stuNum.toLowerCase().includes(keyword)
        )
      }

      // 进度筛选
      if (this.filterProgress !== null) {
        if (this.filterProgress === '0') {
          result = result.filter(s => (s.progress || 0) === 0)
        } else if (this.filterProgress === '1-99') {
          result = result.filter(s => (s.progress || 0) > 0 && (s.progress || 0) < 100)
        } else if (this.filterProgress === '100') {
          result = result.filter(s => (s.progress || 0) >= 100)
        }
      }

      this.filteredStudents = result
    },

    async viewDetail(student) {
      this.currentStudent = student
      this.showDetailDialog = true
      
      // 加载详细数据
      await this.loadChapterProgress(student.stuId)
      await this.loadExamRecords(student.stuId)
    },

    async loadChapterProgress(studentId) {
      try {
        const res = await getStudentChapterProgress(studentId, this.courseId)
        if (res.flag) {
          this.chapterProgress = res.result || []
        }
      } catch (e) {
        console.error('加载章节进度失败', e)
        this.chapterProgress = []
      }
    },

    async loadExamRecords(studentId) {
      try {
        // 调用获取学生考试记录的API
        const { getStudentExamRecords } = await import('../../api/examRecord.js')
        const res = await getStudentExamRecords(studentId, this.courseId)
        if (res.flag) {
          this.examRecords = res.result || []
        }
      } catch (e) {
        console.error('加载考试记录失败', e)
        this.examRecords = []
      }
    },

    getProgressColor(progress) {
      if (progress >= 100) return '#67c23a'
      if (progress >= 60) return '#409eff'
      if (progress >= 30) return '#e6a23c'
      return '#f56c6c'
    },

    exportData() {
      // 导出Excel功能
      this.$message.info('导出功能开发中...')
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
.course-students {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
}

.course-info {
  margin-bottom: 20px;
}

.course-info h3 {
  margin: 0 0 15px 0;
  font-size: 20px;
}

.info-row {
  display: flex;
  gap: 30px;
  color: #666;
}

.info-row span {
  font-size: 14px;
}

.filter-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  align-items: center;
}

.student-detail h3 {
  margin: 20px 0 10px 0;
  font-size: 16px;
  color: #333;
}

.el-descriptions {
  margin-bottom: 20px;
}
</style>
