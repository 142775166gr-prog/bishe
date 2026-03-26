<template>
  <div class="manager">
    <el-row :gutter="20" style="margin-bottom:12px;">
      <el-col :span="4">
        <el-input v-model="query.courseTitle" placeholder="请输入课程标题查询" clearable></el-input>
      </el-col>
      <el-col :span="3">
        <el-select v-model="query.categoryId" placeholder="选择分类" clearable>
          <el-option label="全部分类" :value="null"></el-option>
          <el-option 
            v-for="category in categories" 
            :key="category.categoryId" 
            :label="category.categoryName" 
            :value="category.categoryId">
          </el-option>
        </el-select>
      </el-col>
      <el-col :span="3">
        <el-select v-model="query.courseStatus" placeholder="选择状态" clearable>
          <el-option label="全部状态" :value="null"></el-option>
          <el-option label="草稿" :value="0"></el-option>
          <el-option label="已发布" :value="1"></el-option>
          <el-option label="已下架" :value="2"></el-option>
        </el-select>
      </el-col>
      <el-col :span="6">
        <el-button type="success" @click="search">查询</el-button>
        <el-button type="success" @click="reset">重置</el-button>
      </el-col>
      <el-col :span="8" style="text-align:right">
        <el-button type="success" @click="onAdd">新增课程</el-button>
        <el-button type="danger" @click="batchDelete" :disabled="selectedRows.length===0">批量删除</el-button>
      </el-col>
    </el-row>

    <el-table :data="list" border style="width:100%" @selection-change="handleSelectionChange" v-loading="loading">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="courseTitle" label="课程标题" min-width="150" show-overflow-tooltip></el-table-column>
      <el-table-column prop="teacherName" label="授课教师" width="100"></el-table-column>
      <el-table-column prop="categoryName" label="分类" width="100"></el-table-column>
      <el-table-column prop="courseStatus" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.courseStatus)" size="small">
            {{ getStatusText(row.courseStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="studentCount" label="学生数" width="80" align="center"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="150" align="center"></el-table-column>
      <el-table-column label="操作" width="200" align="center">
        <template #default="{ row }">
          <div class="admin-course-buttons">
            <div class="admin-button-row">
              <el-button type="primary" size="small" @click="onEdit(row)">编辑</el-button>
              <el-button v-if="row.courseStatus === 0" type="success" size="small" @click="onPublish(row)">发布</el-button>
              <el-button v-if="row.courseStatus === 1" type="warning" size="small" @click="onUnpublish(row)">下架</el-button>
              <el-button v-if="row.courseStatus === 2" type="success" size="small" @click="onPublish(row)">上架</el-button>
            </div>
            <div class="admin-button-row">
              <el-button type="info" size="small" @click="onView(row)">查看</el-button>
              <el-button type="danger" size="small" @click="onDelete(row)">删除</el-button>
            </div>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top:12px; text-align:right">
      <el-pagination
        background
        :current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="sizes, prev, pager, next, jumper, ->, total"
        :page-sizes="[5,10,20,50]"
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
        <el-form-item label="授课教师" prop="teacherId" :rules="[{ required: true, message: '请选择授课教师', trigger: 'change' }]">
          <el-select v-model="form.teacherId" placeholder="请选择授课教师" style="width: 100%;" @change="onTeacherChange">
            <el-option 
              v-for="teacher in teachers" 
              :key="teacher.teachId" 
              :label="teacher.teachName" 
              :value="teacher.teachId">
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
          授课教师：{{ viewData.teacherName }} | 创建时间：{{ viewData.createTime }}
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
import { getCoursePage, createCourse, updateCourse, deleteCourse, publishCourse, unpublishCourse, getCourseCategoriesList } from '../../api/course'
import { getteacherlist } from '../../api/teacher'
import { getSessionStorage } from '../../utils/common'

export default {
  data() {
    return {
      query: { courseTitle: '', categoryId: null, courseStatus: null },
      list: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      selectedRows: [],

      dialogVisible: false,
      dialogTitle: '新增课程',
      form: {
        isFree: 1,
        difficultyLevel: 1,
        coursePrice: 0
      },

      viewDialogVisible: false,
      viewData: null,

      categories: [],
      teachers: []
    }
  },
  created() {
    this.getList()
    this.loadCategories()
    this.loadTeachers()
  },
  methods: {
    async getList() {
      this.loading = true
      try {
        const params = {
          courseTitle: this.query.courseTitle ? this.query.courseTitle.trim() : '',
          categoryId: this.query.categoryId,
          courseStatus: this.query.courseStatus
        }
        const res = await getCoursePage(params, this.pageNum, this.pageSize)
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

    async loadTeachers() {
      try {
        const res = await getteacherlist({})
        if (res && res.flag) {
          this.teachers = res.result || []
        }
      } catch (e) {
        console.error('获取教师列表失败', e)
      }
    },

    search() {
      this.pageNum = 1
      this.getList()
    },

    reset() {
      this.query = { courseTitle: '', categoryId: null, courseStatus: null }
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

    handleSelectionChange(rows) {
      this.selectedRows = rows
    },

    onAdd() {
      this.dialogTitle = '新增课程'
      this.form = {
        isFree: 1,
        difficultyLevel: 1,
        coursePrice: 0
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

    onTeacherChange(teacherId) {
      const teacher = this.teachers.find(t => t.teachId === teacherId)
      if (teacher) {
        this.form.teacherName = teacher.teachName
      }
    },

    async submitForm() {
      try {
        await this.$refs.formRef.validate()
        
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

    async onDelete(row) {
      try {
        await this.$confirm('确定删除该课程吗？', '确认', { type: 'warning' })
        const res = await deleteCourse(row.courseId, row.teacherId)
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

    async onPublish(row) {
      try {
        const actionText = row.courseStatus === 0 ? '发布' : '上架'
        await this.$confirm(`确定${actionText}该课程吗？${actionText}后学生即可看到此课程。`, '确认', { type: 'warning' })
        const res = await publishCourse(row.courseId, row.teacherId)
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
        const res = await unpublishCourse(row.courseId, row.teacherId)
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

    async batchDelete() {
      if (this.selectedRows.length === 0) return
      try {
        await this.$confirm(`确定删除选中的 ${this.selectedRows.length} 门课程吗？`, '确认', { type: 'warning' })
        const promises = this.selectedRows.map(r => deleteCourse(r.courseId, r.teacherId))
        const results = await Promise.all(promises)
        const failed = results.filter(r => !r || !r.flag)
        if (failed.length === 0) {
          this.$message.success('批量删除成功')
        } else {
          this.$message.warning(`${failed.length} 门课程删除失败`) 
        }
        this.getList()
      } catch (e) {
        // canceled
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
  padding: 12px;
}

.admin-course-buttons {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.admin-button-row {
  display: flex;
  justify-content: center;
  gap: 4px;
}

.admin-button-row .el-button {
  margin: 0;
  font-size: 12px;
  padding: 4px 8px;
  min-width: 70px;
}
</style>