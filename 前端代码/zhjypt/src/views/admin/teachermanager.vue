<template>
  <div class="manager">
    <el-row :gutter="20" style="margin-bottom:12px;">
      <el-col :span="4">
        <el-input v-model="query.teachAccount" placeholder="请输入账号查询" clearable></el-input>
      </el-col>
      <el-col :span="4">
        <el-input v-model="query.teachName" placeholder="请输入姓名查询" clearable></el-input>
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
      <el-table-column prop="teachAccount" label="账号" width="150"></el-table-column>
      <el-table-column prop="teachName" label="姓名" width="120"></el-table-column>
      <el-table-column prop="teachPhone" label="电话" width="140"></el-table-column>
      <el-table-column prop="teachEmail" label="邮箱" min-width="180"></el-table-column>
      <el-table-column label="操作" width="260" align="center">
        <template #default="{ row }">
          <div class="admin-buttons">
            <el-button type="primary" size="small" @click="onEdit(row)">编辑</el-button>
            <el-button type="warning" size="small" @click="onResetPassword(row)">重置密码</el-button>
            <el-button type="danger" size="small" @click="onDelete(row)">删除</el-button>
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
    <el-dialog :title="dialogTitle" v-model="dialogVisible">
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-form-item label="姓名" prop="teachName">
          <el-input v-model="form.teachName"></el-input>
        </el-form-item>
        <el-form-item label="账号" prop="teachAccount">
          <el-input v-model="form.teachAccount"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="teachPhone">
          <el-input v-model="form.teachPhone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="teachEmail">
          <el-input v-model="form.teachEmail"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="teachAddress">
          <el-input v-model="form.teachAddress"></el-input>
        </el-form-item>
        <el-form-item label="生日" prop="teachBirth">
          <el-date-picker v-model="form.teachBirth" type="date" placeholder="选择日期" style="width:100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="年级" prop="teachGrade">
          <el-input v-model="form.teachGrade"></el-input>
        </el-form-item>
        <el-form-item label="班级" prop="teachClass">
          <el-input v-model="form.teachClass"></el-input>
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
import { getteacherlistbypage, addteacher, updateteacher, deleteteacher, resetteacherpassword } from '../../api/teacher'
export default {
  data() {
    return {
      query: { teachAccount: '', teachName: '' },
      list: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      selectedRows: [],

      dialogVisible: false,
      dialogTitle: '新增教师',
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
          teachAccount: this.query.teachAccount ? this.query.teachAccount.trim() : '',
          teachName: this.query.teachName ? this.query.teachName.trim() : ''
        }
        console.debug('getTeacherListByPage params', params, 'page', this.pageNum, 'size', this.pageSize)
        const res = await getteacherlistbypage(params, this.pageNum, this.pageSize)
        console.debug('getTeacherListByPage response', res)
        if (res && res.flag) {
          this.list = res.result || []
          this.total = res.total || 0
        } else {
          this.list = []
          this.total = 0
        }
      } catch (e) {
        console.error('获取教师列表失败', e)
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
      this.query = { teachAccount: '', teachName: '' }
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
      this.dialogTitle = '新增教师'
      this.form = { teachPassword: '123456' }
      this.dialogVisible = true
    },
    onEdit(row) {
      this.dialogTitle = '编辑教师'
      this.form = Object.assign({}, row)
      this.dialogVisible = true
    },
    async submitForm() {
      try {
        // 将日期格式化为 yyyy-MM-dd
        const payload = Object.assign({}, this.form, { teachBirth: this.form.teachBirth ? this.form.teachBirth.toISOString().slice(0, 10) : null })
        let res
        if (payload.teachId) {
          // 编辑时不在此修改密码
          res = await updateteacher(payload)
        } else {
          res = await addteacher(payload)
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
        const res = await deleteteacher({ teachId: row.teachId })
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
        const promises = this.selectedRows.map(r => deleteteacher({ teachId: r.teachId }))
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
        await this.$confirm(`确定将该教师密码重置为 123456 吗？`, '确认', { type: 'warning' })
        const res = await resetteacherpassword({ teachId: row.teachId })
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
.manager { 
  padding: 12px;
}

.admin-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.admin-buttons .el-button {
  margin: 0;
  min-width: 50px;
}
</style>
