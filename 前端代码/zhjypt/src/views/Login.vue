<template>
  <div class="login-page">
    <div class="login-wrapper">
      <!-- 左侧品牌与简介区域 -->
      <div class="login-panel intro-panel">
        <div class="intro-logo">
          <span class="logo-dot"></span>
          <span class="logo-text">ZH智慧教育</span>
        </div>
        <h1 class="intro-title">智慧教育平台</h1>
        <p class="intro-subtitle">面向管理员、教师与学生的一体化教学与学习平台</p>
        <ul class="intro-list">
          <li>统一课程管理与教学资源</li>
          <li>在线考试与智能批改</li>
          <li>学习进度追踪与可视化分析</li>
        </ul>
      </div>

      <!-- 右侧登录卡片区域 -->
      <div class="login-panel form-panel">
        <el-card shadow="hover" class="login-card">
          <div class="login-card-header">
            <h2>欢迎登录</h2>
            <p class="login-card-desc">请选择身份并输入账号密码进入系统</p>
          </div>

          <!-- 使用 Element Plus 表单容器，仅做布局不启用校验 -->
          <el-form
            :model="{ username, password, role }"
            label-position="top"
            class="login-form"
          >
            <el-form-item label="登录身份">
              <el-radio-group v-model="role" class="role-group">
                <el-radio-button label="student">学生</el-radio-button>
                <el-radio-button label="teacher">老师</el-radio-button>
                <el-radio-button label="admin">管理员</el-radio-button>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="账号">
              <el-input
                v-model="username"
                placeholder="请输入账号"
                clearable
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="密码">
              <el-input
                v-model="password"
                type="password"
                placeholder="请输入密码"
                show-password
                clearable
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item class="login-actions">
              <el-button
                type="primary"
                class="login-btn"
                size="large"
                round
                @click="handleLogin"
              >
                登录
              </el-button>
            </el-form-item>

            <!-- 学生注册入口 -->
            <div class="register-entry">
              <span>还没有学生账号？</span>
              <el-button type="primary" link @click="handleRegister">
                立即注册
              </el-button>
            </div>
          </el-form>
        </el-card>
      </div>
    </div>

    <!-- 学生注册对话框 -->
    <el-dialog 
      title="学生注册" 
      v-model="showRegister"
      width="560px" 
      :before-close="onRegisterDialogClose"
      top="60px"
    >
      <div class="register-header">
        <h3>创建学生账号</h3>
        <p>请填写真实的联系方式，方便老师与管理员与您联系</p>
      </div>

      <el-form
        :model="reg"
        :rules="registerRules"
        ref="registerForm"
        label-width="80px"
        class="register-form"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" prop="stuName">
              <el-input v-model="reg.stuName" placeholder="请输入姓名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账号" prop="stuAccount">
              <el-input v-model="reg.stuAccount" placeholder="建议使用学号"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="密码" prop="stuPassword">
              <el-input
                type="password"
                v-model="reg.stuPassword"
                placeholder="不少于 6 位"
                show-password
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级" prop="stuGrade">
              <el-input v-model="reg.stuGrade" placeholder="如：高一 / 大一"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="电话" prop="stuPhone">
              <el-input v-model="reg.stuPhone" placeholder="用于接收通知"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="stuEmail">
              <el-input v-model="reg.stuEmail" placeholder="用于找回密码"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="大学" prop="stuUniversity">
          <el-input
            v-model="reg.stuUniversity"
            placeholder="就读大学"
          ></el-input>
        </el-form-item>

        <el-form-item label="生日" prop="stuBirth">
          <el-date-picker
            v-model="reg.stuBirth"
            type="date"
            placeholder="选择生日"
            style="width:100%"
          ></el-date-picker>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="onRegisterCancel">取消</el-button>
        <el-button type="primary" :loading="registering" @click="registerStudent">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
// 登录页：统一管理员 / 老师 / 学生登录入口 + 学生自助注册
import { adminlogin } from "../api/admin";
import { studentlogin, addstudent } from "../api/student";
import { teacherlogin } from "../api/teacher";
import { setSessionStorage, removeSessionStorage } from "../utils/common.js";
import store from "../state";
import { initRouter } from "../router";
import { User, Lock } from '@element-plus/icons-vue'


export default {
  data() {
    return {
      username: "",
      password: "",
      // 默认身份改为学生
      role: "student",
      // 注册面板相关
      showRegister: false,
      reg: {
        stuName: '',
        stuAccount: '',
        stuPassword: '',
        stuPhone: '',
        stuEmail: '',
        stuUniversity: '',
        stuBirth: null,
        stuGrade: ''
      },
      registering: false,
      registerRules: {
        stuName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        stuAccount: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        stuPassword: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        stuPhone: [{ pattern: /^\d{11}$/, message: '请输入11位数字手机号', trigger: 'blur' }],
        stuEmail: [{ type: 'email', message: '请输入正确邮箱地址', trigger: 'blur' }]
      }
    };
  },
  methods: {
    // 打开学生注册对话框
    handleRegister() {
      console.log(this.showRegister);
      this.showRegister = true;
      console.log(this.showRegister);
    },
    // 切换注册面板显示
    toggleRegister() {
      this.showRegister = !this.showRegister;
    },

    // 兼容表单校验（返回 Promise）
    validateRegisterForm(form) {
      return new Promise((resolve, reject) => {
        try {
          const r = form.validate();
          if (r && typeof r.then === 'function') {
            r.then(() => resolve()).catch(err => reject(err));
          } else {
            // 旧回调式 API
            form.validate((valid, fields) => {
              if (valid) resolve(); else reject(fields || 'invalid');
            });
          }
        } catch (e) {
          reject(e);
        }
      });
    },

    // 学生注册（使用 Element Plus 表单验证并格式化日期）
    async registerStudent() {
      if (this.registering) return;
      const form = this.$refs.registerForm;
      if (!form) {
        console.warn('registerStudent: register form ref not found');
        alert('表单未就绪，请刷新页面再试');
        return;
      }

      // 兼容不同 Element Plus 版本的校验 API
      try {
        await this.validateRegisterForm(form);
      } catch (validationError) {
        // 校验未通过，提示用户
        console.debug('registerStudent: 验证未通过', validationError);
        this.$message ? this.$message.warning('请检查表单输入') : alert('请检查表单输入');
        return;
      }

      this.registering = true;
      try {
        const params = Object.assign({}, this.reg, {
          stuBirth: this.reg.stuBirth ? this.reg.stuBirth.toISOString().slice(0, 10) : null
        });
        console.debug('registerStudent: sending params', params);
        const res = await addstudent(params);
        console.debug('registerStudent: response', res);
        if (res && res.flag) {
          if (this.$message) this.$message.success('注册成功，已为您跳转到登录，请使用新账号登录'); else alert('注册成功');
          // 预填登录框并切换回登录面板
          this.username = this.reg.stuAccount;
          this.password = this.reg.stuPassword;
          this.role = 'student';
          this.showRegister = false;
          // 重置表单
          form.resetFields();
        } else {
          if (this.$message) this.$message.error((res && res.reason) || '注册失败'); else alert((res && res.reason) || '注册失败');
        }
      } catch (e) {
        console.error('注册失败', e);
        if (this.$message) this.$message.error('注册失败，请稍后重试'); else alert('注册失败，请稍后重试');
      } finally {
        this.registering = false;
      }
    },


    async handleLogin() {
      if (!this.username || !this.password) {
        alert("请输入用户名和密码");
        return;
      }

      // 在每次登录前清除上一次登录的状态（session + Vuex）
      try {
        removeSessionStorage('curuser');
        removeSessionStorage('curadmin');
        removeSessionStorage('menus');
        // 清空运行时菜单与标签
        this.$store.commit('addMenus', []);
        this.$store.commit('clearTab', []);
        // 尝试移除 layout 路由，避免残留子路由
        const hasLayout = this.$router.getRoutes().find(r => r.name === 'layout');
        if (hasLayout) {
          try { this.$router.removeRoute('layout'); } catch (e) { console.warn('login: removeRoute failed', e); }
        }
      } catch (e) {
        console.warn('login: 清理上一次登录状态失败', e);
      }

      try {
        let params = {};
        let loginFn = null;
        if (this.role === "admin") {
          params = { admAccount: this.username, admPassword: this.password };
          loginFn = adminlogin;
        } else if (this.role === "teacher") {
          params = { teachAccount: this.username, teachPassword: this.password };
          loginFn = teacherlogin;
        } else {
          params = { stuAccount: this.username, stuPassword: this.password };
          loginFn = studentlogin;
        }

        const response = await loginFn(params);

        if (response && response.flag) {
          // 保存用户信息
          // response.result 为用户对象（服务端登录时会注入 token 字段）
          setSessionStorage('curuser', {
            role: this.role,
            info: response.result,
            token: response.result && response.result.token ? response.result.token : null
          });
          if (this.role === 'admin') setSessionStorage('curadmin', response.result);

          // 根据角色把预定义菜单加载到运行时 menus
          let targetPath = '/';
          if (this.role === 'admin') {
            this.$store.commit('addMenus', this.$store.state.adminmenus);
            setSessionStorage('menus', this.$store.state.adminmenus);
            targetPath = '/admin/adminhome';
          } else if (this.role === 'teacher') {
            this.$store.commit('addMenus', this.$store.state.teachermenus);
            setSessionStorage('menus', this.$store.state.teachermenus);
            targetPath = '/teacher/teacherhome';
          } else {
            this.$store.commit('addMenus', this.$store.state.studentmenus);
            setSessionStorage('menus', this.$store.state.studentmenus);
            targetPath = '/student/studenthome';
          }

          // 注册动态路由
          initRouter();

          // 先进入 layout 确保子路由容器存在，然后导航到目标页（不再弹出“登录成功”对话框）
          this.$router.push('/layout').then(() => {
            this.$router.push(targetPath).catch(() => {});
          }).catch(() => {
            this.$router.push(targetPath).catch(() => {});
          });
        } else {
          alert((response && response.reason) || "登录失败，请检查用户名或密码");
        }
      } catch (error) {
        console.error("登录接口调用失败", error);
        alert("登录失败，请稍后再试");
      }
    },    onRegisterCancel() {
      this.showRegister = false;
    },
    onRegisterDialogClose(done) {
      // 清理表单并关闭
      this.$refs.registerForm && this.$refs.registerForm.resetFields();
      this.showRegister = false;
      done();
    }  },
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: radial-gradient(circle at top left, #4b6cb7 0, #182848 35%, #0f172a 100%);
}

.login-wrapper {
  width: 100%;
  max-width: 980px;
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 32px;
  align-items: stretch;
}

.login-panel {
  background: rgba(15, 23, 42, 0.4);
  border-radius: 18px;
  padding: 32px;
  color: #f9fafb;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(18px);
}

.intro-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.intro-logo {
  display: inline-flex;
  align-items: center;
  padding: 6px 14px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.7);
  border: 1px solid rgba(148, 163, 184, 0.4);
  margin-bottom: 18px;
  font-size: 13px;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}

.logo-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, #22c55e, #a3e635);
  margin-right: 8px;
}

.logo-text {
  color: #e5e7eb;
}

.intro-title {
  font-size: 30px;
  font-weight: 700;
  margin: 0 0 12px;
}

.intro-subtitle {
  margin: 0 0 24px;
  font-size: 14px;
  color: #cbd5f5;
}

.intro-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.intro-list li {
  position: relative;
  padding-left: 20px;
  margin-bottom: 10px;
  font-size: 14px;
  color: #e5e7eb;
}

.intro-list li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #60a5fa;
}

.form-panel {
  padding: 0;
  background: transparent;
  box-shadow: none;
}

.login-card {
  border-radius: 18px;
  overflow: hidden;
}

.login-card-header {
  margin-bottom: 16px;
}

.login-card-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
}

.login-card-desc {
  margin: 6px 0 0;
  font-size: 13px;
  color: #6b7280;
}

.login-form {
  margin-top: 8px;
}

.role-group {
  width: 100%;
  display: flex;
  justify-content: space-between;
}

.role-group :deep(.el-radio-button__inner) {
  border-radius: 12px;
}

.login-actions {
  margin-top: 12px;
}

.login-btn {
  width: 100%;
}

.register-entry {
  margin-top: 4px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  font-size: 13px;
  color: #9ca3af;
}

.register-entry span {
  margin-right: 4px;
}

.register-header {
  margin-bottom: 12px;
}

.register-header h3 {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 600;
}

.register-header p {
  margin: 0;
  font-size: 12px;
  color: #6b7280;
}

.register-form {
  margin-top: 8px;
}

@media (max-width: 900px) {
  .login-wrapper {
    max-width: 480px;
    grid-template-columns: 1fr;
  }

  .intro-panel {
    display: none;
  }
}
</style>