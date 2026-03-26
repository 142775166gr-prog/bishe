<template>
  <!-- 侧边栏容器：Element Plus 侧边栏组件，固定宽度200px -->
  <el-aside id="asideNav" width="220px">
    <!-- 简洁 Logo 区域 -->
    <div class="logo-name">
      <p>学习导航</p>
    </div>
    <!-- 侧边栏菜单：Element Plus 菜单组件 -->
    <el-menu
      ref="menu"
      active-text-color="#0ea5e9"
      background-color="#ffffff"
      :default-active="$route.path" 
      :default-openeds="defaultOpeneds"
      :router="true"               
      unique-opened                
      text-color="#0f172a"         
      @close="handleSubMenuClose"
      >
      <!-- 循环渲染一级菜单：遍历Vuex中获取的菜单列表 -->
      <el-sub-menu
        v-for="menu in $store.getters.menus"
        :key="menu.menusIndex"
        :index="menu.menusIndex"
      >
          <!-- 折叠菜单的标题模板 -->
          <template #title>
            <!-- 图标容器 -->
            <el-icon>
              <!-- 动态渲染图标组件：根据菜单配置的图标名称渲染 -->
              <component :is="menu.icon" />
            </el-icon>
            <!-- 一级菜单标题文字 -->
            <span>{{menu.title}}</span>
          </template>
          <!-- 循环渲染二级菜单：遍历当前一级菜单的子菜单列表 -->
          <el-menu-item
            v-for="child in menu.children"
            :key="child.path"
            :index="child.path"
          >
            <el-icon class="child-icon">
              <component :is="getChildIcon(child.title)" />
            </el-icon>
            <span>{{child.title}}</span>
          </el-menu-item>
        </el-sub-menu>
    </el-menu>
  </el-aside>
</template>
  
<script>
export default {
  // 组件名称
  name: "Aside",
  methods: {
    // 阻止一级菜单被折叠，保持始终展开
    handleSubMenuClose(index) {
      this.$nextTick(() => {
        this.$refs.menu && this.$refs.menu.open(index);
      });
    },
    // 根据子菜单标题返回合适的图标名称（Element Plus 图标组件名）
    getChildIcon(title) {
      switch (title) {
        case '首页':
          return 'House';
        case '课程中心':
          return 'Collection';
        case '我的课程':
          return 'Reading';
        case '在线咨询':
          return 'ChatLineRound';
        case '教育建议':
          return 'EditPen';
        case '系统公告':
          return 'Bell';
        default:
          return 'Menu';
      }
    }
  },
  computed: {
    // 默认展开所有根级菜单
    defaultOpeneds() {
      return (this.$store.getters.menus || []).map(m => m.menusIndex);
    }
  },
  // 监听属性配置
  watch: {
    // 监听当前路由路径的变化
    "$route.path": {
      // 路由变化后的回调函数：newVal新路由，oldVal旧路由
      handler: function(newVal, oldVal) {
        // 恒为true，暂时阻塞后续逻辑（可根据业务需求修改判断条件）
        if (true) {
          return;
        }
        // 判断当前标签页列表中是否已存在该路由
        let index = this.$store.getters.tabs.findIndex(function(value, key) {
          return value.path === newVal;
        });
        // 若标签页已存在 或 路由是登录页，则不执行添加操作
        if (index != -1 || newVal == "/login") {
          return;
        }
        // 通过路由路径获取对应的菜单名称
        let menuName = this.$store.getters.getMenuNameByUrl(newVal);
        // 提交Vuex mutation，添加新的标签页
        this.$store.commit("addTab", {
          title: menuName,
          path: newVal
        });
      },
      // 组件初始化时立即执行一次该监听回调
      immediate: true
    }
  }
};
</script>
  
<style scoped>
/* 侧边栏容器样式：弹性布局，子元素垂直排列 */
#asideNav {
  display: flex;
  flex-direction: column;
  background: #ffffff;
  color: #0f172a;
  border-right: 1px solid #e5e7eb;
}

/* 侧边栏logo区域样式（预留） */
#asideNav .logo-name {
  width: 100%;
  height: 64px;
  background: linear-gradient(90deg, #e0f2fe 0%, #ecfeff 55%, #dcfce7 120%);
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #e5e7eb;
}
/* logo图片样式（预留） */
#asideNav .logo-name .logo-png {
  width: 165px;
  height: 165px;
  margin: -30px 0 0 10px;
}
/* logo下方文字样式（预留） */
#asideNav .logo-name p {
  font-weight: 600;
  font-size: 16px;
  margin: 0;
  color: #0f172a;
}

/* 菜单组件样式：占满侧边栏剩余高度，移除右侧默认边框 */
#asideNav .el-menu {
  /* font-weight: 550; */
  flex: 1;
  border-right: none;
  border-top: none;
}

/* 清爽教育风：菜单可读性与层级 */
#asideNav :deep(.el-sub-menu__title),
#asideNav :deep(.el-menu-item) {
  height: 44px;
  line-height: 44px;
  font-size: 14px;
  padding-left: 18px;
}

#asideNav :deep(.el-sub-menu__title) {
  font-weight: 600;
}

#asideNav .child-icon {
  margin-right: 8px;
  color: #0284c7;
}

#asideNav :deep(.el-sub-menu__title:hover),
#asideNav :deep(.el-menu-item:hover) {
  background: #f0f9ff;
  color: #0f172a;
}

#asideNav :deep(.el-menu-item.is-active) {
  background: #e0f2fe;
  color: #0284c7;
  font-weight: 600;
  position: relative;
}

#asideNav :deep(.el-menu-item.is-active::before) {
  content: "";
  position: absolute;
  left: 0;
  top: 8px;
  bottom: 8px;
  width: 3px;
  border-radius: 999px;
  background-color: #0ea5e9;
}

#asideNav :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
  color: #0284c7;
  font-weight: 600;
}

/* 去掉一级菜单右侧的折叠箭头图标 */
#asideNav :deep(.el-sub-menu__icon-arrow) {
  display: none;
}
</style>