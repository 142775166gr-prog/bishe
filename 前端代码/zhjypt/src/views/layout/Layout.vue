<template>
  <!-- 布局整体容器 -->
  <div class="container">
    <!-- 页面头部区域（包含标题图片和管理员名称） -->
    <div class="header">
      <!-- 左侧系统标题 -->
      <div class="header-left">
        <div class="logo-dot"></div>
        <router-link to="/layout" class="logo-text">智慧教育平台</router-link>
      </div>

      <!-- 头部右侧：消息提醒与头像下拉 -->
      <div class="header-right">
        <!-- 未读消息提醒（仅学生和教师显示） -->
        <el-badge v-if="role !== 'admin'" :value="unreadCount" :hidden="unreadCount === 0" class="message-badge">
          <el-popover
            placement="bottom"
            :width="320"
            trigger="click"
            @show="loadUnreadReplies"
          >
            <template #reference>
              <el-icon class="message-icon" :size="22"><Bell /></el-icon>
            </template>
            <div class="unread-panel">
              <div class="unread-header">
                <span>未读回复</span>
                <el-button v-if="unreadCount > 0" type="primary" link size="small" @click="handleMarkAllRead">全部已读</el-button>
              </div>
              <div v-if="unreadReplies.length === 0" class="unread-empty">暂无未读回复</div>
              <div v-else class="unread-list">
                <div v-for="reply in unreadReplies" :key="reply.commentId" class="unread-item" @click="handleReplyClick(reply)">
                  <div class="unread-user">{{ reply.userName }} 回复了你</div>
                  <div class="unread-content">{{ reply.content }}</div>
                  <div class="unread-time">{{ formatTime(reply.createTime) }}</div>
                </div>
              </div>
            </div>
          </el-popover>
        </el-badge>

        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link">
            <el-avatar :size="36" :src="avatarUrl" class="header-avatar">
              {{ name?.charAt(0) }}
            </el-avatar>
            <span class="username">{{ name }}</span>
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="password">修改密码</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 
      Element Plus 布局容器：
      style="height: 100%;"：设置布局容器高度为100%，填充剩余区域
    -->
    <el-container style="height: 100%;">
      <!--左边菜单：引入Aside侧边栏组件-->
      <Aside></Aside>

      <!--右边内容容器：direction="vertical" 设置垂直排列子元素-->
      <el-container direction="vertical">
        <!--Tab选项：引入TabNav标签页导航组件-->
        <TabNav></TabNav>

        <!-- 主内容区域：用于渲染路由匹配的组件 -->
        <el-main class="elmain">
          <!-- 路由视图容器：显示当前路由对应的页面组件 -->
          <router-view />

          <!-- 修改密码对话框 -->
          <ChangePassword 
            v-model="pwdDialogVisible" 
            :user-type="role" 
            :user-id="getUserId()"
            @success="handlePasswordChangeSuccess" />

        </el-main>
      </el-container>
    </el-container>

    <!-- 公告通知组件 - 暂时注释掉 -->
    <!-- <AnnouncementNotification /> -->
  </div>
</template>
    
<script>
// 导入侧边栏组件
import Aside from "../../components/Aside.vue";
// 导入标签页导航组件
import TabNav from "../../components/TabNav.vue";
// 导入公告通知组件
import AnnouncementNotification from "../../components/AnnouncementNotification.vue";
// 导入修改密码组件
import ChangePassword from "../../components/ChangePassword.vue";
// 导入工具方法：获取sessionStorage中存储的管理员信息与移除函数
import { getSessionStorage, removeSessionStorage, setSessionStorage } from "../../utils/common.js";
// 导入评论相关API
import { getUnreadReplyCount, getUnreadReplies, markAsRead, markAllAsRead } from "../../api/courseComment.js";
// 导入图标
import { Bell } from '@element-plus/icons-vue';

// 导出Vue组件选项对象
export default {
  // 组件名称
  name: "Layout",
  // 注册子组件：在当前布局中使用Aside和TabNav组件
  components: {
    Aside,
    TabNav,
    ChangePassword,
    Bell,
    // AnnouncementNotification // 暂时注释掉
  },
  // 组件创建生命周期钩子（实例创建完成后调用）
  created() {
    //发送请求获取所有表的扩展信息？？（原始注释：预留业务逻辑位置）
    // 加载未读回复数量
    if (this.role !== 'admin') {
      this.loadUnreadCount();
      // 每30秒刷新一次未读数量
      this.unreadTimer = setInterval(() => {
        this.loadUnreadCount();
      }, 30000);
    }
  },
  beforeUnmount() {
    // 清除定时器
    if (this.unreadTimer) {
      clearInterval(this.unreadTimer);
    }
  },
  // 组件方法集
  data() {
    const curAdmin = getSessionStorage('curadmin');
    const curUser = getSessionStorage('curuser');
    let user = {};
    let role = '';
    let avatar = '';
    if (curAdmin) {
      user = curAdmin;
      role = 'admin';
      avatar = curAdmin.avatar || '';
    } else if (curUser) {
      user = curUser.info || {};
      role = curUser.role || '';
      avatar = user.avatar || '';
    }
    // 处理头像URL
    const baseUrl = 'http://localhost:9999';
    const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
    let avatarUrl = defaultAvatar;
    if (avatar) {
      avatarUrl = avatar.startsWith('http') ? avatar : baseUrl + avatar;
    }
    return {
      name: user.admName || user.stuName || user.teachName || '',
      avatarUrl: avatarUrl,
      pwdDialogVisible: false,
      role: role,
      rawUser: user,
      unreadCount: 0,
      unreadReplies: [],
      unreadTimer: null
    }
  },
  methods: {
    handleCommand(command) {
      if (command === 'profile') {
        this.gotoProfile();
      } else if (command === 'password') {
        this.pwdDialogVisible = true;
      } else if (command === 'logout') {
        this.handleLogout();
      }
    },

    getUserId() {
      if (this.role === 'admin') {
        return this.rawUser.admId;
      } else if (this.role === 'teacher') {
        return this.rawUser.teachId;
      } else if (this.role === 'student') {
        return this.rawUser.stuId;
      }
      return null;
    },

    handlePasswordChangeSuccess() {
      this.$message.success('密码修改成功，请重新登录');
      // 延迟1秒后自动退出登录
      setTimeout(() => {
        this.handleLogout();
      }, 1000);
    },

    gotoProfile() {
      if (this.role === 'admin') {
        this.$router.push('/admin/profile');
      } else if (this.role === 'teacher') {
        this.$router.push('/teacher/profile');
      } else if (this.role === 'student') {
        this.$router.push('/student/profile');
      }
    },
    handleLogout() {
      // 清理 sessionStorage
      removeSessionStorage('curuser');
      removeSessionStorage('curadmin');
      removeSessionStorage('menus');

      // 清理 Vuex 状态：menus 与 tabs
      this.$store.commit('addMenus', []);
      this.$store.commit('clearTab', []);

      // 尝试移除 layout 路由，避免遗留动态子路由
      try {
        const hasLayout = this.$router.getRoutes().find(r => r.name === 'layout');
        if (hasLayout) {
          this.$router.removeRoute('layout');
        }
      } catch (e) {
        console.warn('logout: removeRoute failed', e);
      }

      // 导航回登录页
      this.$router.push('/login');
    },

    // 加载未读回复数量
    async loadUnreadCount() {
      if (this.role === 'admin') return;
      try {
        const userId = this.getUserId();
        const res = await getUnreadReplyCount(userId, this.role);
        if (res.flag) {
          this.unreadCount = res.result || 0;
        }
      } catch (e) {
        console.error('获取未读数量失败', e);
      }
    },

    // 加载未读回复列表
    async loadUnreadReplies() {
      if (this.role === 'admin') return;
      try {
        const userId = this.getUserId();
        const res = await getUnreadReplies(userId, this.role);
        if (res.flag) {
          this.unreadReplies = res.result || [];
        }
      } catch (e) {
        console.error('获取未读列表失败', e);
      }
    },

    // 点击回复项
    async handleReplyClick(reply) {
      try {
        const userId = this.getUserId();
        await markAsRead(reply.commentId, userId, this.role);
        this.unreadCount = Math.max(0, this.unreadCount - 1);
        this.unreadReplies = this.unreadReplies.filter(r => r.commentId !== reply.commentId);
        // 跳转到课程学习页面
        if (this.role === 'student') {
          this.$router.push(`/student/courselearn?courseId=${reply.courseId}`);
        } else if (this.role === 'teacher') {
          this.$router.push(`/teacher/coursedetail?courseId=${reply.courseId}`);
        }
      } catch (e) {
        console.error('标记已读失败', e);
      }
    },

    // 全部标记已读
    async handleMarkAllRead() {
      try {
        const userId = this.getUserId();
        const res = await markAllAsRead(userId, this.role);
        if (res.flag) {
          this.unreadCount = 0;
          this.unreadReplies = [];
          this.$message.success('已全部标记为已读');
        }
      } catch (e) {
        console.error('标记全部已读失败', e);
      }
    },

    // 格式化时间
    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      const now = new Date();
      const diff = now - date;
      if (diff < 60000) return '刚刚';
      if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';
      if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前';
      if (diff < 604800000) return Math.floor(diff / 86400000) + '天前';
      return date.toLocaleDateString();
    }
  }
};
</script>
    
<style>
/* 全局样式重置：清除所有元素的默认外边距和内边距 */
* {
  margin: 0px;
  padding: 0px;
  box-sizing: border-box;
}

/* 页面整体字体样式：设置默认字体大小和颜色 */
body {
  font-size: 14px;
  color: #333333;
  overflow-x: hidden;
}

/* 列表样式重置：移除列表默认项目符号 */
li {
  list-style: none;
}

/* 链接样式重置：移除链接默认下划线 */
a {
  text-decoration: none;
}

/* 全屏高度设置：让html、body、布局容器等元素高度铺满整个视口 */
html,
body,
#layout,
.el-container,
#asideNav,
ul.el-menu {
  height: 100%;
  overflow-x: hidden;
}

/* Element Plus 容器样式调整 */
.container .el-container {
  flex: 1;
  overflow: hidden;
}

/* 布局容器样式：设置高度和宽度 */
.container {
  height: 100vh;
  width: 100%;
  max-width: 100vw;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
}

/* 头部区域样式：高度、宽度和背景渐变 */
.container .header {
  height: 60px;
  width: 100%;
  /* 清爽教育风：浅蓝渐变，整体更明亮 */
  background: linear-gradient(90deg, #1d4ed8 0%, #38bdf8 55%, #22c55e 120%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  box-sizing: border-box;
  flex-shrink: 0;
  box-shadow: 0 10px 26px rgba(37, 99, 235, 0.14);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-dot {
  width: 18px;
  height: 18px;
  border-radius: 6px;
  background: radial-gradient(circle at 30% 20%, #ffffff, rgba(255, 255, 255, 0.45));
  box-shadow: 0 0 14px rgba(255, 255, 255, 0.55);
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.95);
}

/* 头部右侧用户信息样式 */
.header-right {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.header-right .el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.header-right .username {
  font-weight: 500;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.95);
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-left: 8px;
}

/* 头像样式 */
.header-avatar {
  width: 36px !important;
  height: 36px !important;
  min-width: 36px !important;
  min-height: 36px !important;
  border-radius: 50% !important;
  flex-shrink: 0;
  border: 2px solid rgba(255, 255, 255, 0.65);
  overflow: hidden;
}

.header-avatar img {
  width: 100% !important;
  height: 100% !important;
  object-fit: cover !important;
  border-radius: 50% !important;
}

/* 头部标题样式：居中对齐、垂直居中、设置字体大小 */
.container .header .title {
  text-align: center;
  margin-top: auto;
  font-size: 1.5vw;
  line-height: 3vw; /* 行高等于高度，实现文字垂直居中 */
}

/* 头部图片样式：设置高度（仅用于标题图片，不影响头像） */
.container .header .title img{
  height: 3vw;
  position: absolute;
}

/* 主内容区域样式：清除默认内边距 */
.container .elmain{
  padding: 0;
}

/* 退出按钮样式 */
.logout-btn{
  position: absolute;
  right: 10px;
  top: 6px;
  background: transparent;
  border: 1px solid #fff;
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  cursor: pointer;
}
.logout-btn:hover{
  background: rgba(255,255,255,0.1);
}

/* 消息提醒样式 */
.message-badge {
  margin-right: 20px;
}

.message-icon {
  color: rgba(255, 255, 255, 0.95);
  cursor: pointer;
  font-size: 22px;
  transition: transform 0.2s;
}

.message-icon:hover {
  transform: scale(1.1);
}

/* 未读消息面板样式 */
.unread-panel {
  max-height: 400px;
  overflow-y: auto;
}

.unread-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  margin-bottom: 10px;
  font-weight: bold;
}

.unread-empty {
  text-align: center;
  color: #999;
  padding: 20px 0;
}

.unread-list {
  max-height: 350px;
  overflow-y: auto;
}

.unread-item {
  padding: 10px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.unread-item:hover {
  background-color: #f5f7fa;
}

.unread-item:last-child {
  border-bottom: none;
}

.unread-user {
  font-weight: 500;
  color: #409eff;
  margin-bottom: 4px;
}

.unread-content {
  color: #333;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 280px;
}

.unread-time {
  color: #999;
  font-size: 12px;
  margin-top: 4px;
}
</style>