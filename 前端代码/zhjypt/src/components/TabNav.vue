<template>
  <!-- 标签页整体容器 -->
  <div class="container_tab">
    <!-- 标签页导航列表容器 -->
    <ul class="tab_nav_box">
      <!-- 循环渲染标签页：item是标签页对象，index是索引 -->
      <li
        v-for="(item, index) in $store.getters.tabs"
        :key="item.title"
        :class="{ active: $route.path === item.path }"
      >
        <!-- 标签页路由链接：点击跳转对应路径 -->
        <router-link :to="item.path">{{item.title}}</router-link>
        <!-- 关闭图标容器 -->
        <el-icon >
          <!-- 关闭按钮：点击触发关闭指定索引标签页的方法 -->
          <CloseBold @click="onCloseTabIndex(index)" />
        </el-icon>
      </li>
    </ul>
  </div>
</template>
  
<script>
export default {
  // 组件名称
  name: "TabNav",

  methods: {
    // 关闭指定索引的标签页
    onCloseTabIndex(index) {
      // 获取当前标签总数
      const tabsLength = this.$store.getters.tabs.length;
      
      // 如果只剩一个标签，关闭后跳转到首页
      if (tabsLength === 1) {
        this.$store.commit("deleteTabByIndex", index);
        // 根据角色跳转到对应首页
        const curuser = sessionStorage.getItem('curuser');
        const curadmin = sessionStorage.getItem('curadmin');
        if (curadmin) {
          this.$router.push('/admin/adminhome');
        } else if (curuser) {
          const user = JSON.parse(curuser);
          if (user.role === 'teacher') {
            this.$router.push('/teacher/teacherhome');
          } else if (user.role === 'student') {
            this.$router.push('/student/studenthome');
          }
        }
        return;
      }
      
      // 获取当前激活的标签路径
      const currentPath = this.$route.path;
      const closingTab = this.$store.getters.tabs[index];
      
      // 删除标签
      this.$store.commit("deleteTabByIndex", index);
      
      // 如果关闭的是当前激活的标签，跳转到最后一个标签
      if (closingTab && closingTab.path === currentPath) {
        const tabs = this.$store.getters.tabs;
        if (tabs.length > 0) {
          const lastTab = tabs[tabs.length - 1];
          this.$router.push(lastTab.path);
        }
      }
    }
  },
  watch: {
    // 监听浏览器直接输入路由，将此路由添加到tabs
    "$route.path": {
      handler: function(newVal, oldVal) {
        // 此处恒为true，暂时阻塞后续逻辑（可根据业务需求修改判断条件）
        if (true) {
          return;
        }
        //判断当前tab中是否已经存在该路由
        let index = this.$store.getters.tabs.findIndex(function(value, key) {
          return value.path === newVal;
        });
        // 若标签页已存在 或 路由是登录页，则不添加新标签
        if (index != -1 || newVal == "/login") {
          return;
        }
        // 通过路由路径获取对应的菜单名称
        let menuName = this.$store.getters.getMenuNameByUrl(newVal);
        //手动构建tab并提交mutation添加
        this.$store.commit("addTab", {
          title: menuName,
          path: newVal
        });
      },
      // 组件初始化时立即执行一次监听回调
      immediate: true
    }
  }
};
</script>
  
<style scoped>
/* 标签页容器样式 */
.container_tab {
  padding: 6px 12px 4px 16px;
  border-bottom: 1px solid #e5e7eb;
  /* 清爽教育风：浅蓝白背景 */
  background: linear-gradient(90deg, #f8fbff 0%, #f0f9ff 55%, #f0fdf4 120%);
}

/* 标签页导航列表样式 */
.tab_nav_box {
  display: flex;
  align-items: center;
  padding: 2px 0;
  gap: 6px;
}

/* 单个标签页样式 */
.tab_nav_box li {
  height: 28px;
  line-height: 28px;
  display: flex;
  align-items: center;
  padding: 0 2px;
  border-radius: 999px;
  background: transparent;
}
/* 标签页悬浮效果 */
.tab_nav_box li:hover {
  background: rgba(14, 165, 233, 0.10);
}

/* 标签页链接样式 */
.tab_nav_box li a {
  padding: 0 10px;
  display: inline-block;
  color: #334155;
  font-size: 13px;
}

/* 从第二个标签开始调整链接右边距 */
.tab_nav_box li:nth-child(n + 2) a {
  padding-right: 15px;
}

/* 关闭图标悬浮效果 */
.tab_nav_box li i:hover {
  color: #6b7280;
}

/* 标签页激活状态样式 */
.tab_nav_box li.active {
  background-color: #ffffff;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.08);
  border: 1px solid rgba(14, 165, 233, 0.18);
}

/* 激活状态标签页链接样式 */
.tab_nav_box li.active a {
  color: #0f172a;
  font-weight: 500;
}
</style>