<template>
  <div class="manager">
    <div class="header">
      <h2>系统公告</h2>
      <div>
        <el-button type="success" @click="onAdd">发布公告</el-button>
        <el-button type="danger" @click="batchDelete" :disabled="selectedRows.length===0">批量删除</el-button>
      </div>
    </div>

    <el-row :gutter="20" style="margin-bottom:12px;">
      <el-col :span="6">
        <el-input v-model="query.title" placeholder="请输入标题查询" clearable></el-input>
      </el-col>
      <el-col :span="8">
        <el-button type="primary" @click="search">查询</el-button>
        <el-button type="success" @click="reset">重置</el-button>
      </el-col>
    </el-row>

    <el-table :data="list" border style="width:100%" @selection-change="handleSelectionChange" v-loading="loading">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="title" label="标题" width="200"></el-table-column>
      <el-table-column prop="content" label="内容" show-overflow-tooltip>
        <template #default="{ row }">
          <span>{{ row.content.length > 50 ? row.content.substring(0, 50) + '...' : row.content }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="publisherName" label="发布人" width="120"></el-table-column>
      <el-table-column prop="publishTime" label="发布时间" width="180"></el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="onEdit(row)">编辑</el-button>
          <el-button type="info" size="small" @click="onView(row)">查看</el-button>
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
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="60%">
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-form-item label="标题" prop="title" :rules="[{ required: true, message: '请输入标题', trigger: 'blur' }]">
          <el-input v-model="form.title" placeholder="请输入公告标题"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content" :rules="[{ required: true, message: '请输入内容', trigger: 'blur' }]">
          <el-input 
            v-model="form.content" 
            type="textarea" 
            :rows="8" 
            placeholder="请输入公告内容">
          </el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
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
    <el-dialog title="公告详情" v-model="viewDialogVisible" width="60%">
      <div v-if="viewData">
        <h3>{{ viewData.title }}</h3>
        <p style="color: #666; margin: 10px 0;">
          发布人：{{ viewData.publisherName }} | 发布时间：{{ viewData.publishTime }}
        </p>
        <div style="white-space: pre-wrap; line-height: 1.6;">{{ viewData.content }}</div>
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
import { getAnnouncementPage, publishAnnouncement, updateAnnouncement, deleteAnnouncement } from '../../api/announcement'
import { getSessionStorage } from '../../utils/common'

export default {
  data() {
    return {
      query: { title: '' },
      list: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      selectedRows: [],

      dialogVisible: false,
      dialogTitle: '发布公告',
      form: {
        status: 1
      },

      viewDialogVisible: false,
      viewData: null
    }
  },
  created() {
    this.getList();
  },
  methods: {
    async getList() {
      this.loading = true
      try {
        const params = {
          title: this.query.title ? this.query.title.trim() : ''
        }
        const res = await getAnnouncementPage(params, this.pageNum, this.pageSize)
        if (res && res.flag) {
          this.list = res.result || []
          this.total = res.total || 0
        } else {
          this.list = []
          this.total = 0
        }
      } catch (e) {
        console.error('获取公告列表失败', e)
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
      this.query = { title: '' }
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
      this.dialogTitle = '发布公告'
      this.form = { status: 1 }
      this.dialogVisible = true
    },
    onEdit(row) {
      this.dialogTitle = '编辑公告'
      this.form = Object.assign({}, row)
      this.dialogVisible = true
    },
    onView(row) {
      this.viewData = row
      this.viewDialogVisible = true
    },
    async submitForm() {
      try {
        await this.$refs.formRef.validate()
        
        // 获取当前管理员信息
        const currentAdmin = getSessionStorage('curadmin')
        if (!currentAdmin) {
          this.$message.error('请先登录')
          return
        }

        const payload = Object.assign({}, this.form, {
          publisherId: currentAdmin.admId,
          publisherName: currentAdmin.admName
        })

        let res
        if (payload.announcementId) {
          res = await updateAnnouncement(payload)
        } else {
          res = await publishAnnouncement(payload)
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
        await this.$confirm('确定删除该公告吗？', '确认', { type: 'warning' })
        const res = await deleteAnnouncement(row.announcementId)
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
        await this.$confirm(`确定删除选中的 ${this.selectedRows.length} 条公告吗？`, '确认', { type: 'warning' })
        const promises = this.selectedRows.map(r => deleteAnnouncement(r.announcementId))
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
    }
  }
}
</script>

<style scoped>
.manager { 
  padding: 20px;
}

.manager > .header {
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

.manager > .header h2 {
  margin: 0;
  color: #fff;
  font-size: 18px;
  font-weight: 700;
}
</style>