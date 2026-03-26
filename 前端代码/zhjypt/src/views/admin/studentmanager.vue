<template>
  <div class="manager">
    <el-row :gutter="20" style="margin-bottom:12px;">
      <el-col :span="4">
        <el-input v-model="query.stuAccount" placeholder="请输入账号查询" clearable></el-input>
      </el-col>
      <el-col :span="4">
        <el-input v-model="query.stuName" placeholder="请输入姓名查询" clearable></el-input>
      </el-col>
      <el-col :span="8">
        <el-button type="primary" @click="search">查询</el-button>
        <el-button type="success" @click="reset">重置</el-button>
      </el-col>
      <el-col :span="8" style="text-align:right">
        <el-button type="success" @click="onAdd">新增</el-button>
        <el-button type="danger" @click="batchDelete" :disabled="selectedRows.length===0">批量删除</el-button>
      </el-col>
    </el-row>

    <el-table :data="list" border style="width:100%" @selection-change="handleSelectionChange" v-loading="loading">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="stuAccount" label="账号" width="180"></el-table-column>
      <el-table-column prop="stuName" label="名称" width="180"></el-table-column>
      <el-table-column prop="stuPhone" label="电话" width="160"></el-table-column>
      <el-table-column prop="stuEmail" label="邮箱"></el-table-column>
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="onEdit(row)">编辑</el-button>
          <el-button type="warning" size="small" @click="onResetPassword(row)">重置密码</el-button>
          <el-button type="danger" size="small" @click="onDelete(row)">删除</el-button>
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
    <el-dialog :title="dialogTitle" v-model="dialogVisible">
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-form-item label="姓名" prop="stuName">
          <el-input v-model="form.stuName"></el-input>
        </el-form-item>
        <el-form-item label="账号" prop="stuAccount">
          <el-input v-model="form.stuAccount"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="stuPhone">
          <el-input v-model="form.stuPhone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="stuEmail">
          <el-input v-model="form.stuEmail"></el-input>
        </el-form-item>
        <el-form-item label="大学" prop="stuUniversity">
          <el-input v-model="form.stuUniversity"></el-input>
        </el-form-item>
        <el-form-item label="生日" prop="stuBirth">
          <el-date-picker v-model="form.stuBirth" type="date" placeholder="选择日期" style="width:100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="年级" prop="stuGrade">
          <el-input v-model="form.stuGrade"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="success" @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getstudentlistbypage, addstudent, updatestudent, deletestudent, resetstudentpassword } from '../../api/student'
export default {
  data() {
    return {
      query: { stuAccount: '', stuName: '' },
      list: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      selectedRows: [],

      dialogVisible: false,
      dialogTitle: '新增学生',
      form: {},
    }
  },
  created() {
    this.getList();
  },
  methods: {
    async getList() {
      this.loading = true
      try {
        // 规范参数：去除两端空白
        const params = {
          stuAccount: this.query.stuAccount ? this.query.stuAccount.trim() : '',
          stuName: this.query.stuName ? this.query.stuName.trim() : ''
        }
        console.debug('getStudentListByPage params', params, 'page', this.pageNum, 'size', this.pageSize)
        const res = await getstudentlistbypage(params, this.pageNum, this.pageSize)
        console.debug('getStudentListByPage response', res)
        if (res && res.flag) {
          this.list = res.result || []
          this.total = res.total || 0
        } else {
          this.list = []
          this.total = 0
        }
      } catch (e) {
        console.error('获取学生列表失败', e)
        this.list = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },
    search() {
      this.pageNum = 1
      this.getList()
    },
    reset() {
      this.query = { stuAccount: '', stuName: '' }
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
      this.dialogTitle = '新增学生'
      this.form = { stuPassword: '123456' }
      this.dialogVisible = true
    },
    onEdit(row) {
      this.dialogTitle = '编辑学生'
      this.form = Object.assign({}, row)
      this.dialogVisible = true
    },
    async submitForm() {
      try {
        // 将日期格式化为 yyyy-MM-dd
        const payload = Object.assign({}, this.form, { stuBirth: this.form.stuBirth ? this.form.stuBirth.toISOString().slice(0, 10) : null })
        let res
        if (payload.stuId) {
          // 编辑时不在此修改密码
          res = await updatestudent(payload)
        } else {
          res = await addstudent(payload)
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
        const ok = await this.$confirm('确定删除该条记录吗？', '确认', { type: 'warning' })
        // if confirmed
        const res = await deletestudent({ stuId: row.stuId })
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
    async batchDelete() {
      if (this.selectedRows.length === 0) return
      try {
        await this.$confirm(`确定删除选中的 ${this.selectedRows.length} 条记录吗？`, '确认', { type: 'warning' })
        const promises = this.selectedRows.map(r => deletestudent({ stuId: r.stuId }))
        const results = await Promise.all(promises)
        const failed = results.filter(r => !r || !r.flag)
        if (failed.length === 0) {
          this.$message.success('批量删除成功')
        } else {
          this.$message.warning(`${failed.length} 条删除失败`) 
        }
        this.getList()
      } catch (e) {
        // canceled
      }
    },
    async onResetPassword(row) {
      try {
        await this.$confirm(`确定将该学生密码重置为 123456 吗？`, '确认', { type: 'warning' })
        const res = await resetstudentpassword({ stuId: row.stuId })
        if (res && res.flag) {
          this.$message.success('密码已重置为 123456')
        } else {
          this.$message.error(res.reason || '重置失败')
        }
      } catch (e) {
        // cancel
      }
    }
  }
}
</script>

<style scoped>
.manager { padding: 12px }
</style>
