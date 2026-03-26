import { createWebHashHistory, createRouter } from 'vue-router'
import store from '../state'
import { getSessionStorage } from "../utils/common.js";

//配置路由规则：配置组件的访问路径
const routes = [
  {
    path: "/",
    redirect: "/login"
  },
  {
    path: '/:catchAll(.*)',
    component: () => import('../views/error/404.vue'),
    name: 'NotFound'
  },
  {
    path: "/login",
    component: () => import('../views/Login.vue'),
  },
]

//创建路由实例
let router = createRouter({
  routes: routes, //注册路由规则
  history: createWebHashHistory()  //路由方式 createWebHistory() 或 createWebHashHistory()
})

/**
 * 初始路由信息
 */
export function initRouter() {
  const appendRoute = {
    path: '/layout',
    name: 'layout',
    component: () => import('../views/layout/Layout.vue'),
    children: []
  };

  const menus = store.getters.menus || [];
  if (!menus || menus.length === 0) {
    console.warn('initRouter: 菜单为空，跳过动态路由注册');
    return;
  }

  // 获取可用组件映射
  const modules = import.meta.glob('../views/**/*.vue');

  for (const menu of menus) {
    (menu.children || []).forEach((temp) => {
      const item = {};
      // 计算组件名（将路径尾部首字母大写）
      let vueName = temp.path;
      const indexN = temp.path.lastIndexOf('/') + 1;
      vueName = vueName.substr(0, indexN) + vueName.substring(indexN).charAt(0).toUpperCase() + vueName.substring(indexN + 1);

      // 尝试多种解析策略以提高容错
      let comp = modules[`../views${vueName}.vue`] || modules[`../views${temp.path}.vue`] || modules[`../views${temp.path.substring(1)}.vue`];

      // 回退到运行时 import
      if (!comp) {
        comp = () => import(`../views${temp.path}.vue`);
      }

      item.component = comp;
      item.path = temp.path;
      item.name = temp.title;
      appendRoute.children.push(item);

      console.debug('initRouter: prepare child', temp.path);
    });
  }

  // 移除已存在的 layout 路由，避免重复注册
  const exists = router.getRoutes().find(r => r.name === 'layout');
  if (exists) {
    try {
      router.removeRoute('layout');
      console.debug('initRouter: removed existing layout route');
    } catch (e) {
      console.warn('initRouter: removeRoute failed', e);
    }
  }

  // 添加固定的个人信息和修改密码路由（不在侧边菜单中显示）
  const staticChildren = [
    { path: '/admin/profile', name: 'AdminProfile', component: modules['../views/admin/profile.vue'] || (() => import('../views/admin/profile.vue')) },
    { path: '/admin/commentmanager', name: 'AdminCommentManager', component: modules['../views/admin/commentmanager.vue'] || (() => import('../views/admin/commentmanager.vue')) },
    { path: '/teacher/profile', name: 'TeacherProfile', component: modules['../views/teacher/profile.vue'] || (() => import('../views/teacher/profile.vue')) },
    { path: '/student/profile', name: 'StudentProfile', component: modules['../views/student/profile.vue'] || (() => import('../views/student/profile.vue')) },
    { path: '/teacher/chaptermanager', name: 'ChapterManager', component: modules['../views/teacher/chaptermanager.vue'] || (() => import('../views/teacher/chaptermanager.vue')) },
    { path: '/teacher/coursestudents', name: 'CourseStudents', component: modules['../views/teacher/coursestudents.vue'] || (() => import('../views/teacher/coursestudents.vue')) },
    { path: '/teacher/exammanager', name: 'ExamManager', component: modules['../views/teacher/exammanager.vue'] || (() => import('../views/teacher/exammanager.vue')) },
    { path: '/teacher/questionmanager', name: 'QuestionManager', component: modules['../views/teacher/questionmanager.vue'] || (() => import('../views/teacher/questionmanager.vue')) },
    { path: '/teacher/examgrading', name: 'ExamGrading', component: modules['../views/teacher/examgrading.vue'] || (() => import('../views/teacher/examgrading.vue')) },
    { path: '/teacher/coursecomments', name: 'TeacherCourseComments', component: modules['../views/teacher/coursecomments.vue'] || (() => import('../views/teacher/coursecomments.vue')) },
    { path: '/student/courselearn', name: 'CourseLearn', component: modules['../views/student/courselearn.vue'] || (() => import('../views/student/courselearn.vue')) },
    { path: '/student/examlist', name: 'ExamList', component: modules['../views/student/examlist.vue'] || (() => import('../views/student/examlist.vue')) },
    { path: '/student/exam', name: 'Exam', component: modules['../views/student/exam.vue'] || (() => import('../views/student/exam.vue')) },
    { path: '/student/examhistory', name: 'ExamHistory', component: modules['../views/student/examhistory.vue'] || (() => import('../views/student/examhistory.vue')) }
  ];

  appendRoute.children = appendRoute.children.concat(staticChildren);

  if (appendRoute.children.length > 0) {
    router.addRoute(appendRoute);
    console.info(`initRouter: 注册了 ${appendRoute.children.length} 个子路由`);
  } else {
    console.warn('initRouter: 没有发现任何子路由，未注册 layout 子路由');
  }
}

//路由守卫
router.beforeEach((to, from, next) => {
  let rules = ['/', '/login']
  //indexOf():在数组中查找指定元素索引位置，如果找不到返回-1
  if (rules.indexOf(to.path) > 0) {
    next()
  } else {
    //判断是否登录（支持管理员或其他角色 curuser）
    let curnode = getSessionStorage('curadmin') || getSessionStorage('curuser')
    if (curnode != null) {
      // 如果 session 存在 menus，则优先恢复（支持刷新后保持角色菜单）
      const savedMenus = getSessionStorage('menus');
      if (savedMenus) {
        store.commit('addMenus', savedMenus);
      }

      let currentPath = to.fullPath;
      let menuArray = store.getters.menus;
      //第一次访问登录页面时，并没有菜单列表，此时也动态加载了路由并让length变成了3，导致登录后不会再执行初始化加载路由,加上其他额外条件
      if (router.getRoutes().length == 3 && menuArray != null && menuArray.length > 0 && menuArray != 'undefined') {

        //需要动态加载路由信息
        initRouter();
        next({ path: to.path });
        return;
      }
      let menus = store.getters.menus;
      let currentMenu = null;

      let firstLevelSize = menus.length;
      let secondLevelSize = 0;

      for (let i = 0; i < firstLevelSize && currentMenu == null; i++) {
        secondLevelSize = menus[i].children.length;
        for (let j = 0; j < secondLevelSize; j++) {
          //说明找到了
          if (menus[i].children[j].path == currentPath) {
            currentMenu = menus[i].children[j];
            break;
          }
        }
      }

      if (currentMenu) {
        //调用vuex判断是否需要添加到state的tabs中
        store.commit("addTab", currentMenu);
      }
      //放行
      next();
    } else {
      router.push('/login')
    }
  }
});


export default router