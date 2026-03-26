<template>
  <div class="manager">
    <!-- 顶部标题区域 -->
    <div class="header">
      <div class="header-left">
        <h2>课程管理</h2>
        <p class="header-subtitle">管理你创建的课程，查看发布状态与学生学习情况</p>
      </div>
      <div class="header-right">
        <el-button type="success" @click="onAdd">
          创建课程
        </el-button>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <el-row :gutter="16" align="middle">
        <el-col :span="6">
          <el-input 
            v-model="query.courseTitle" 
            placeholder="请输入课程标题查询" 
            clearable
          />
        </el-col>
        <el-col :span="4">
          <el-select v-model="query.courseStatus" placeholder="选择状态" clearable>
            <el-option label="全部状态" :value="null"></el-option>
            <el-option label="草稿" :value="0"></el-option>
            <el-option label="已发布" :value="1"></el-option>
            <el-option label="已下架" :value="2"></el-option>
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 卡片列表区域 -->
    <div class="course-cards" v-loading="loading">
      <el-row :gutter="16">
        <el-col 
          v-for="row in list" 
          :key="row.courseId" 
          :span="12"
        >
          <el-card class="course-card" shadow="hover">
            <div class="course-card-header">
              <div class="course-title-block">
                <div class="course-title">{{ row.courseTitle }}</div>
                <div class="course-meta">
                  <span class="course-category">{{ row.categoryName }}</span>
                  <span class="course-difficulty">{{ getDifficultyText(row.difficultyLevel) }}</span>
                </div>
              </div>
              <div class="course-status-block">
                <el-tag :type="getStatusType(row.courseStatus)" effect="light">
                  {{ getStatusText(row.courseStatus) }}
                </el-tag>
                <div class="course-students">
                  学生数：<span>{{ row.studentCount || 0 }}</span>
                </div>
              </div>
            </div>

            <div class="course-card-body">
              <div class="course-time">
                创建时间：{{ row.createTime }}
              </div>
            </div>

            <div class="course-card-footer">
              <div class="teacher-buttons">
                <div class="button-row">
                  <el-button type="primary" size="small" @click="onEdit(row)">编辑</el-button>
                  <el-button v-if="row.courseStatus === 0" type="success" size="small" @click="onPublish(row)">发布</el-button>
                  <el-button v-if="row.courseStatus === 1" type="warning" size="small" @click="onUnpublish(row)">下架</el-button>
                  <el-button v-if="row.courseStatus === 2" type="success" size="small" @click="onPublish(row)">上架</el-button>
                  <el-button type="info" size="small" @click="onView(row)">查看</el-button>
                </div>
                <div class="button-row">
                  <el-button type="info" size="small" @click="onManageChapters(row)">章节管理</el-button>
                  <el-button type="primary" size="small" @click="onViewStudents(row)">查看学生</el-button>
                  <el-button type="success" size="small" @click="onManageExams(row)">考试管理</el-button>
                  <el-button type="danger" size="small" @click="onDelete(row)">删除</el-button>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty 
        v-if="!loading && (!list || list.length === 0)" 
        description="暂时还没有创建课程"
        class="course-empty"
      />
    </div>

    <div class="pagination-wrapper">
      <el-pagination
        background
        :current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="sizes, prev, pager, next, jumper, ->, total"
        :page-sizes="[1,10,20,50]"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="60%">
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-form-item label="课程标题" prop="courseTitle" :rules="[{ required: true, message: '请输入课程标题', trigger: 'blur' }]">
          <el-input v-model="form.courseTitle" placeholder="请输入课程标题"></el-input>
        </el-form-item>
        <el-form-item label="课程描述" prop="courseDesc" :rules="[{ required: true, message: '请输入课程描述', trigger: 'blur' }]">
          <el-input 
            v-model="form.courseDesc" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入课程描述">
          </el-input>
        </el-form-item>
        <el-form-item label="课程分类" prop="categoryId" :rules="[{ required: true, message: '请选择课程分类', trigger: 'change' }]">
          <el-select v-model="form.categoryId" placeholder="请选择课程分类" style="width: 100%;">
            <el-option 
              v-for="category in categories" 
              :key="category.categoryId" 
              :label="category.categoryName" 
              :value="category.categoryId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程封面" prop="courseCover">
          <el-input v-model="form.courseCover" placeholder="请输入课程封面图片URL"></el-input>
        </el-form-item>
        <el-form-item label="难度等级" prop="difficultyLevel">
          <el-radio-group v-model="form.difficultyLevel">
            <el-radio :label="1">初级</el-radio>
            <el-radio :label="2">中级</el-radio>
            <el-radio :label="3">高级</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="success" @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看对话框 -->
    <el-dialog title="课程详情" v-model="viewDialogVisible" width="60%">
      <div v-if="viewData">
        <h3>{{ viewData.courseTitle }}</h3>
        <p style="color: #666; margin: 10px 0;">
          创建时间：{{ viewData.createTime }} | 更新时间：{{ viewData.updateTime }}
        </p>
        <p><strong>课程分类：</strong>{{ viewData.categoryName }}</p>
        <p><strong>课程状态：</strong>{{ getStatusText(viewData.courseStatus) }}</p>
        <p><strong>难度等级：</strong>{{ getDifficultyText(viewData.difficultyLevel) }}</p>
        <p><strong>学生数量：</strong>{{ viewData.studentCount }}</p>
        <div style="margin-top: 20px;">
          <strong>课程描述：</strong>
          <div style="white-space: pre-wrap; line-height: 1.6; margin-top: 10px;">{{ viewData.courseDesc }}</div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="success" @click="viewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getTeacherCoursePage, createCourse, updateCourse, deleteCourse, publishCourse, unpublishCourse, getCourseCategoriesList } from '../../api/course'
import { getSessionStorage } from '../../utils/common'

export default {
  data() {
    return {
      query: { courseTitle: '', courseStatus: null },
      list: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,

      dialogVisible: false,
      dialogTitle: '创建课程',
      form: {
        isFree: 1,
        difficultyLevel: 1,
        coursePrice: 0
      },

      viewDialogVisible: false,
      viewData: null,

      categories: [],
      currentTeacher: null
    }
  },
  created() {
    this.initTeacherInfo()
    this.getList()
    this.loadCategories()
  },
  methods: {
    initTeacherInfo() {
      const currentUser = getSessionStorage('curuser')
      if (currentUser && currentUser.info) {
        this.currentTeacher = currentUser.info
      }
    },

    async getList() {
      if (!this.currentTeacher) {
        this.$message.error('请先登录')
        return
      }

      this.loading = true
      try {
        const params = {
          courseTitle: this.query.courseTitle ? this.query.courseTitle.trim() : '',
          courseStatus: this.query.courseStatus
        }
        const res = await getTeacherCoursePage(this.currentTeacher.teachId, params, this.pageNum, this.pageSize)
        if (res && res.flag) {
          this.list = res.result || []
          this.total = res.total || 0
        } else {
          this.list = []
          this.total = 0
        }
      } catch (e) {
        console.error('获取课程列表失败', e)
        this.list = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },

    async loadCategories() {
      try {
        const res = await getCourseCategoriesList()
        if (res && res.flag) {
          this.categories = res.result || []
        }
      } catch (e) {
        console.error('获取分类列表失败', e)
      }
    },

    search() {
      this.pageNum = 1
      this.getList()
    },

    reset() {
      this.query = { courseTitle: '', courseStatus: null }
      this.search()
    },

    handleSizeChange(size) {
      this.pageSize = size
      this.getList()
    },

    handleCurrentChange(page) {
      this.pageNum = page
      this.getList()
    },

    onAdd() {
      this.dialogTitle = '创建课程'
      this.form = {
        isFree: 1,
        difficultyLevel: 1,
        coursePrice: 0,
        teacherId: this.currentTeacher.teachId,
        teacherName: this.currentTeacher.teachName
      }
      this.dialogVisible = true
    },

    onEdit(row) {
      this.dialogTitle = '编辑课程'
      this.form = Object.assign({}, row)
      this.dialogVisible = true
    },

    onView(row) {
      this.viewData = row
      this.viewDialogVisible = true
    },

    onManageChapters(row) {
      // 跳转到章节管理页面
      this.$router.push({
        path: '/teacher/chaptermanager',
        query: { courseId: row.courseId }
      })
    },

    onViewStudents(row) {
      // 跳转到课程学生学习情况页面
      this.$router.push({
        path: '/teacher/coursestudents',
        query: { courseId: row.courseId }
      })
    },

    onManageExams(row) {
      // 跳转到考试管理页面
      this.$router.push({
        path: '/teacher/exammanager',
        query: { 
          courseId: row.courseId,
          courseTitle: row.courseTitle
        }
      })
    },

    async submitForm() {
      try {
        await this.$refs.formRef.validate()
        
        // 调试日志
        console.log('提交的表单数据:', this.form)
        console.log('课程封面URL:', this.form.courseCover)
        
        // 确保教师信息
        this.form.teacherId = this.currentTeacher.teachId
        this.form.teacherName = this.currentTeacher.teachName
        
        let res
        if (this.form.courseId) {
          res = await updateCourse(this.form)
        } else {
          res = await createCourse(this.form)
        }
        
        if (res && res.flag) {
          this.$message.success(res.reason || '操作成功')
          this.dialogVisible = false
          this.getList()
        } else {
          this.$message.error(res.reason || '操作失败')
        }
      } catch (e) {
        console.error('保存失败', e)
        this.$message.error('保存失败')
      }
    },

    async onPublish(row) {
      try {
        const actionText = row.courseStatus === 0 ? '发布' : '上架'
        await this.$confirm(`确定${actionText}该课程吗？${actionText}后学生即可看到此课程。`, '确认', { type: 'warning' })
        const res = await publishCourse(row.courseId, this.currentTeacher.teachId)
        if (res && res.flag) {
          this.$message.success(`${actionText}成功`)
          this.getList()
        } else {
          this.$message.error(res.reason || `${actionText}失败`)
        }
      } catch (e) {
        // cancel noop
      }
    },

    async onUnpublish(row) {
      try {
        await this.$confirm('确定下架该课程吗？下架后学生将无法看到此课程。', '确认', { type: 'warning' })
        const res = await unpublishCourse(row.courseId, this.currentTeacher.teachId)
        if (res && res.flag) {
          this.$message.success('下架成功')
          this.getList()
        } else {
          this.$message.error(res.reason || '下架失败')
        }
      } catch (e) {
        // cancel noop
      }
    },

    async onDelete(row) {
      try {
        await this.$confirm('确定删除该课程吗？删除后无法恢复。', '确认', { type: 'warning' })
        const res = await deleteCourse(row.courseId, this.currentTeacher.teachId)
        if (res && res.flag) {
          this.$message.success(res.reason || '删除成功')
          this.getList()
        } else {
          this.$message.error(res.reason || '删除失败')
        }
      } catch (e) {
        // cancel noop
      }
    },

    getStatusType(status) {
      const types = { 0: 'info', 1: 'success', 2: 'warning' }
      return types[status] || 'info'
    },

    getStatusText(status) {
      const texts = { 0: '草稿', 1: '已发布', 2: '已下架' }
      return texts[status] || '未知'
    },

    getDifficultyText(level) {
      const texts = { 1: '初级', 2: '中级', 3: '高级' }
      return texts[level] || '未知'
    }
  }
}
</script>

<style scoped>
.manager { 
  padding: 20px;
  background: linear-gradient(180deg, #f8fbff 0%, #f3f4f6 100%);
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 14px;
  border-radius: 14px;
  background: linear-gradient(90deg, #3b82f6 0%, #22c55e 100%);
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.12);
  color: #fff;
}

.header-left h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
}

.header-subtitle {
  margin: 4px 0 0;
  font-size: 13px;
  opacity: 0.9;
}

.header-right .el-button {
  box-shadow: 0 8px 18px rgba(16, 185, 129, 0.35);
}

.filter-section {
  margin-bottom: 14px;
  padding: 12px 16px;
  border-radius: 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
}

.pagination-wrapper {
  margin-top: 14px; 
  text-align:right
}

.course-cards {
  min-height: 180px;
}

.course-card {
  margin-bottom: 14px;
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.course-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 35px rgba(15, 23, 42, 0.10);
}

.course-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.course-title-block {
  flex: 1;
  min-width: 0;
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 4px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

.course-meta {
  display: flex;
  gap: 8px;
  font-size: 12px;
  color: #6b7280;
}

.course-category {
  padding: 2px 8px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
}

.course-difficulty {
  padding: 2px 8px;
  border-radius: 999px;
  background: #ecfdf5;
  color: #059669;
}

.course-status-block {
  text-align: right;
  min-width: 90px;
}

.course-students {
  margin-top: 6px;
  font-size: 12px;
  color: #4b5563;
}

.course-students span {
  font-weight: 600;
  color: #111827;
}

.course-card-body {
  margin-top: 4px;
  margin-bottom: 10px;
  font-size: 12px;
  color: #6b7280;
}

.course-time {
  display: flex;
  align-items: center;
  gap: 6px;
}

.course-card-footer {
  border-top: 1px dashed #e5e7eb;
  padding-top: 8px;
  margin-top: 4px;
}

.course-empty {
  margin-top: 40px;
}

.teacher-buttons {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.button-row {
  display: flex;
  justify-content: center;
  gap: 4px;
  flex-wrap: wrap;
}

.button-row .el-button {
  margin: 0;
  font-size: 12px;
  padding: 4px 10px;
  min-width: 70px;
}
</style>