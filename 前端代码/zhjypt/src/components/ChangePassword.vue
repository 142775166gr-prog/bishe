<template>
  <el-dialog title="修改密码" v-model="visible" width="400px" @close="handleClose">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="新密码" prop="newPassword">
        <el-input 
          v-model="form.newPassword" 
          type="password" 
          placeholder="请输入新密码"
          show-password>
        </el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input 
          v-model="form.confirmPassword" 
          type="password" 
          placeholder="请再次输入新密码"
          show-password>
        </el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button type="success" @click="handleClose">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="loading">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
export default {
  name: 'ChangePassword',
  props: {
    modelValue: {
      type: Boolean,
      default: false
    },
    userType: {
      type: String,
      required: true,
      validator: value => ['admin', 'teacher', 'student'].includes(value)
    },
    userId: {
      type: Number,
      required: true
    }
  },
  emits: ['update:modelValue', 'success'],
  data() {
    // 确认密码验证
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入新密码'))
      } else if (value !== this.form.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      form: {
        newPassword: '',
        confirmPassword: ''
      },
      rules: {
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  computed: {
    visible: {
      get() {
        return this.modelValue
      },
      set(value) {
        this.$emit('update:modelValue', value)
      }
    }
  },
  methods: {
    async submitForm() {
      try {
        await this.$refs.formRef.validate()
        
        this.loading = true
        let res
        
        // 根据用户类型调用不同的API
        switch (this.userType) {
          case 'admin':
            const { updateadmin } = await import('../api/admin.js')
            res = await updateadmin({
              admId: this.userId,
              admPassword: this.form.newPassword
            })
            break
          case 'teacher':
            const { updateteacher } = await import('../api/teacher.js')
            res = await updateteacher({
              teachId: this.userId,
              teachPassword: this.form.newPassword
            })
            break
          case 'student':
            const { updatestudent } = await import('../api/student.js')
            res = await updatestudent({
              stuId: this.userId,
              stuPassword: this.form.newPassword
            })
            break
        }
        
        if (res && res.flag) {
          this.$message.success('密码修改成功')
          this.$emit('success')
          this.handleClose()
        } else {
          this.$message.error(res.reason || '密码修改失败')
        }
      } catch (e) {
        console.error('密码修改失败', e)
        this.$message.error('密码修改失败')
      } finally {
        this.loading = false
      }
    },

    handleClose() {
      this.visible = false
      this.resetForm()
    },

    resetForm() {
      this.form = {
        newPassword: '',
        confirmPassword: ''
      }
      if (this.$refs.formRef) {
        this.$refs.formRef.resetFields()
      }
    }
  }
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>