<template>
  <div class="profile">
    <h2>管理员信息</h2>
    
    <!-- 头像区域 -->
    <div class="avatar-section">
      <el-avatar :size="100" :src="avatarDisplay">
        {{ form.admName?.charAt(0) }}
      </el-avatar>
      <div class="avatar-actions">
        <el-upload
          class="avatar-uploader"
          action="http://localhost:9999/upload/image"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
        >
          <el-button size="small" type="primary">上传头像</el-button>
        </el-upload>
        <el-button size="small" @click="showUrlDialog = true">使用网络图片</el-button>
      </div>
    </div>

    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="姓名" prop="admName">
        <el-input v-model="form.admName"></el-input>
      </el-form-item>
      <el-form-item label="账户" prop="admAccount">
        <el-input v-model="form.admAccount" disabled></el-input>
      </el-form-item>
      <el-form-item label="电话" prop="admPhone">
        <el-input v-model="form.admPhone"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="admEmail">
        <el-input v-model="form.admEmail"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">保存</el-button>
      </el-form-item>
    </el-form>

    <!-- 网络图片URL对话框 -->
    <el-dialog v-model="showUrlDialog" title="使用网络图片" width="400px">
      <el-input v-model="avatarUrl" placeholder="请输入图片URL"></el-input>
      <div class="url-preview" v-if="avatarUrl">
        <p>预览：</p>
        <el-avatar :size="80" :src="avatarUrl" />
      </div>
      <template #footer>
        <el-button @click="showUrlDialog = false">取消</el-button>
        <el-button type="primary" @click="useUrlAvatar">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getSessionStorage, setSessionStorage } from '../../utils/common.js'

export default {
  name: 'AdminProfile',
  data() {
    const cur = getSessionStorage('curadmin') || {};
    return {
      form: Object.assign({}, cur),
      rules: {
        admName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
      },
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      showUrlDialog: false,
      avatarUrl: '',
      baseUrl: 'http://localhost:9999'
    }
  },
  computed: {
    avatarDisplay() {
      if (!this.form.avatar) return this.defaultAvatar
      if (this.form.avatar.startsWith('http')) return this.form.avatar
      return this.baseUrl + this.form.avatar
    }
  },
  methods: {
    handleAvatarSuccess(response) {
      if (response && response.flag) {
        // 返回的是对象，包含url字段
        this.form.avatar = response.result.url
        this.$message.success('头像上传成功')
      } else {
        this.$message.error('上传失败')
      }
    },
    beforeAvatarUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB!')
        return false
      }
      return true
    },
    useUrlAvatar() {
      if (this.avatarUrl) {
        this.form.avatar = this.avatarUrl
        this.showUrlDialog = false
        this.avatarUrl = ''
        this.$message.success('头像设置成功，请点击保存')
      }
    },
    async submit() {
      try {
        const { updateadmin } = await import('../../api/admin.js');
        const res = await updateadmin(this.form);
        if (res && res.flag) {
          this.$message.success('保存成功');
          setSessionStorage('curadmin', Object.assign({}, this.form));
          // 刷新页面以更新顶部头像
          location.reload();
        } else {
          this.$message.error(res ? res.reason : '保存失败');
        }
      } catch (e) {
        console.error(e);
        this.$message.error('保存失败');
      }
    }
  }
}
</script>

<style scoped>
.profile { padding: 20px; max-width: 600px; }
.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}
.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.url-preview {
  margin-top: 15px;
  text-align: center;
}
.url-preview p {
  margin-bottom: 10px;
  color: #666;
}
</style>
