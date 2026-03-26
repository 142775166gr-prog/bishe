<template>
  <div class="suggestion-manager">
    <div class="header">
      <h2>教育建议管理</h2>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        发送建议
      </el-button>
    </div>

    <!-- 已发送的建议列表 -->
    <div class="suggestion-items">
      <el-empty v-if="suggestions.length === 0" description="暂无发送记录" />
      
      <div v-for="item in suggestions" :key="item.suggestionId" class="suggestion-item">
        <div class="item-header">
          <div class="title-row">
            <el-tag :type="getTypeColor(item.suggestionType)" size="small">{{ getTypeName(item.suggestionType) }}</el-tag>
            <h3>{{ item.suggestionTitle }}</h3>
            <el-tag v-if="item.isRead === 1" type="success" size="small">已读</el-tag>
            <el-tag v-else type="info" size="small">未读</el-tag>
          </div>
          <div class="meta-info">
            <span>发送给：{{ item.studentName }}</span>
            <span v-if="item.courseTitle">课程：{{ item.courseTitle }}</span>
            <span>{{ formatTime(item.createTime) }}</span>
          </div>
        </div>

        <div class="item-content">
          <p>{{ item.suggestionContent }}</p>
        </div>

        <div class="item-actions">
          <el-button type="danger" size="small" @click="deleteSuggestion(item)">
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

    <!-- 创建建议对话框 -->
    <el-dialog v-model="showCreateDialog" title="发送教育建议" width="700px">
      <el-form :model="newSuggestion" :rules="rules" ref="suggestionForm" label-width="100px">
        <el-form-item label="选择学生" prop="studentIds">
          <div style="display: flex; gap: 8px; align-items: center; width: 100%;">
            <el-select
              v-model="newSuggestion.studentIds"
              multiple
              filterable
              collapse-tags
              collapse-tags-tooltip
              placeholder="请选择学生（可多选）"
              style="flex: 1"
            >
              <el-option
                v-for="student in students"
                :key="student.stuId"
                :label="student.stuName"
                :value="student.stuId"
              />
            </el-select>
            <el-checkbox v-model="selectAllStudents" @change="handleSelectAllStudents">
              全选
            </el-checkbox>
          </div>
        </el-form-item>

        <el-form-item label="相关课程">
          <el-select v-model="newSuggestion.courseId" placeholder="请选择课程（可选）" clearable @change="onCourseChange" style="width: 100%">
            <el-option v-for="course in myCourses" :key="course.courseId" :label="course.courseTitle" :value="course.courseId" />
          </el-select>
        </el-form-item>

        <el-form-item label="建议类型" prop="suggestionType">
          <el-radio-group v-model="newSuggestion.suggestionType">
            <el-radio :label="1">学习方法</el-radio>
            <el-radio :label="2">课程推荐</el-radio>
            <el-radio :label="3">学习计划</el-radio>
            <el-radio :label="4">其他</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="建议标题" prop="suggestionTitle">
          <el-input v-model="newSuggestion.suggestionTitle" placeholder="请输入建议标题" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="建议内容" prop="suggestionContent">
          <el-input v-model="newSuggestion.suggestionContent" type="textarea" :rows="8" placeholder="请输入建议内容" maxlength="2000" show-word-limit />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitSuggestion" :loading="submitting">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getTeacherSuggestions, createSuggestion, deleteSuggestion as deleteSuggestionApi } from '../../api/suggestion.js'
import { getstudentlist } from '../../api/student.js'
import { getSessionStorage } from '../../utils/common.js'

export default {
  name: 'SuggestionManager',
  data() {
    return {
      suggestions: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      showCreateDialog: false,
      newSuggestion: {
        studentIds: [],
        courseId: null,
        courseTitle: '',
        suggestionType: 1,
        suggestionTitle: '',
        suggestionContent: ''
      },
      students: [],
      myCourses: [],
      submitting: false,
      selectAllStudents: false,
      rules: {
        studentIds: [{ required: true, message: '请选择学生', trigger: 'change' }],
        suggestionType: [{ required: true, message: '请选择建议类型', trigger: 'change' }],
        suggestionTitle: [{ required: true, message: '请输入建议标题', trigger: 'blur' }],
        suggestionContent: [{ required: true, message: '请输入建议内容', trigger: 'blur' }]
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
    this.loadSuggestions()
    this.loadStudents()
    this.loadMyCourses()
  },
  methods: {
    async loadSuggestions() {
      try {
        const res = await getTeacherSuggestions(this.currentUser.teachId, this.pageNum, this.pageSize)
        if (res.flag) {
          this.suggestions = res.result || []
          this.total = res.total || 0
        }
      } catch (e) {
        console.error('加载建议列表失败', e)
      }
    },

    handleSizeChange(size) {
      this.pageSize = size
      this.loadSuggestions()
    },

    handleCurrentChange(page) {
      this.pageNum = page
      this.loadSuggestions()
    },

    async loadStudents() {
      try {
        const res = await getstudentlist({})
        if (res.flag) {
          this.students = res.result || []
        }
      } catch (e) {
        console.error('加载学生列表失败', e)
      }
    },

    async loadMyCourses() {
      // 调用获取教师课程列表的API
      // 暂时留空，根据实际API调整
    },

    handleSelectAllStudents(checked) {
      if (checked) {
        this.newSuggestion.studentIds = this.students.map(s => s.stuId)
      } else {
        this.newSuggestion.studentIds = []
      }
    },

    onCourseChange(courseId) {
      if (courseId) {
        const course = this.myCourses.find(c => c.courseId === courseId)
        if (course) {
          this.newSuggestion.courseTitle = course.courseTitle
        }
      } else {
        this.newSuggestion.courseTitle = ''
      }
    },

    async submitSuggestion() {
      this.$refs.suggestionForm.validate(async (valid) => {
        if (!valid) return

        if (!this.newSuggestion.studentIds || this.newSuggestion.studentIds.length === 0) {
          this.$message.error('请选择至少一名学生')
          return
        }

        this.submitting = true
        try {
          const selectedStudents = this.students.filter(s =>
            this.newSuggestion.studentIds.includes(s.stuId)
          )

          const results = await Promise.all(
            selectedStudents.map(student => {
              const params = {
                teacherId: this.currentUser.teachId,
                teacherName: this.currentUser.teachName,
                studentId: student.stuId,
                studentName: student.stuName,
                courseId: this.newSuggestion.courseId,
                courseTitle: this.newSuggestion.courseTitle,
                suggestionType: this.newSuggestion.suggestionType,
                suggestionTitle: this.newSuggestion.suggestionTitle,
                suggestionContent: this.newSuggestion.suggestionContent
              }
              return createSuggestion(params)
            })
          )

          const successCount = results.filter(r => r && r.flag).length
          if (successCount > 0) {
            this.$message.success(`成功发送给 ${successCount} 名学生`)
            this.showCreateDialog = false
            this.resetForm()
            this.loadSuggestions()
          } else {
            this.$message.error('建议发送失败')
          }
        } catch (e) {
          console.error('发送建议失败', e)
          this.$message.error('建议发送失败')
        } finally {
          this.submitting = false
        }
      })
    },

    resetForm() {
      this.newSuggestion = {
        studentIds: [],
        courseId: null,
        courseTitle: '',
        suggestionType: 1,
        suggestionTitle: '',
        suggestionContent: ''
      }
      this.selectAllStudents = false
      this.$refs.suggestionForm?.resetFields()
    },

    async deleteSuggestion(item) {
      try {
        await this.$confirm('确定要删除此建议吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await deleteSuggestionApi(item.suggestionId, this.currentUser.teachId, 'teacher')
        if (res.flag) {
          this.$message.success('删除成功')
          this.loadSuggestions()
        }
      } catch (e) {
        if (e !== 'cancel') {
          console.error('删除失败', e)
        }
      }
    },

    getTypeName(type) {
      const names = { 1: '学习方法', 2: '课程推荐', 3: '学习计划', 4: '其他' }
      return names[type] || '其他'
    },

    getTypeColor(type) {
      const colors = { 1: 'success', 2: 'primary', 3: 'warning', 4: 'info' }
      return colors[type] || 'info'
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
.suggestion-manager {
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

.suggestion-items {
  min-height: 400px;
}

.suggestion-item {
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
  flex: 1;
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

.item-content p {
  color: #333;
  line-height: 1.8;
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
