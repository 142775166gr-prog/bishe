import { createStore } from 'vuex'
export default createStore({
    state: {
        tabs: [],
        // 运行时生效的菜单（登录时会被替换为对应角色的菜单）
        menus: [],
        // 所有角色的菜单统一在这里配置，动态路由将根据该结构注入
        adminmenus: [
            // 管理员菜单（原有）
            {
                menusIndex: "1",
                icon: "User",
                title: '管理员',
                children: [
                    { title: '首页', path: '/admin/adminhome' },
                    { title: '学生管理', path: '/admin/studentmanager' },
                    { title: '教师管理', path: '/admin/teachermanager' },
                    { title: '课程管理', path: '/admin/coursemanager' },
                    { title: '公告管理', path: '/admin/announcementmanager' }
                ]
            },
        ],
        teachermenus: [
            // 老师菜单
            {
                menusIndex: "2",
                icon: "User",
                title: '老师',
                children: [
                    { title: '首页', path: '/teacher/teacherhome' },
                    { title: '我的课程', path: '/teacher/coursemanager' },
                    { title: '考试批改', path: '/teacher/examgrading' },
                    { title: '学生咨询', path: '/teacher/consultationmanager' },
                    { title: '教育建议', path: '/teacher/suggestionmanager' },
                    { title: '系统公告', path: '/common/announcementlist' }
                ]
            },
        ],
        studentmenus: [
            // 学生菜单
            {
                menusIndex: "3",
                icon: "User",
                title: '学生',
                children: [
                    { title: '首页', path: '/student/studenthome' },
                    { title: '课程中心', path: '/student/courselist' },
                    { title: '我的课程', path: '/student/mycourses' },
                    { title: '在线咨询', path: '/student/consultationlist' },
                    { title: '教育建议', path: '/student/suggestionlist' },
                    { title: '系统公告', path: '/common/announcementlist' }
                ]
            }
        ]
    },
    getters: {
        tabs(state) {
            return state.tabs;
        },
        menus(state) {
            return state.menus;
        }
    },
    mutations: {
        addMenus(state, param) {
            state.menus = param
        },
        deleteTabByIndex(state, index) {
            state.tabs.splice(index, 1);
        },
        clearTab(state, param) {
            state.tabs = param
        },
        addTab(state, payload) {
            let path = payload.path;
            if (path) {
                let result = state.tabs.filter((item) => {
                    return item.path == path;
                });
                if (result.length == 0) {
                    if (state.tabs.length == 10) {
                        state.tabs.splice(1, 1)
                    }
                    state.tabs.push(payload);
                }

            }
        },
    },
    actions: {
    },
    modules: {
    }
})