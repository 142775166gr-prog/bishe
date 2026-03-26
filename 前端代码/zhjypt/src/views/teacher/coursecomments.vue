<template>
  <div class="course-comments-manager">
    <div class="page-header">
      <h2>课程评论管理</h2>
      <el-select v-model="selectedCourseId" placeholder="选择课程" @change="loadComments" clearable>
        <el-option
          v-for="course in courses"
          :key="course.courseId"
          :label="course.courseTitle"
          :value="course.courseId"
        />
      </el-select>
    </div>

    <div v-if="selectedCourseId">
      <CourseComments :courseId="selectedCourseId" />
    </div>
    <div v-else class="no-course">
      <el-empty description="请选择一个课程查看评论" />
    </div>
  </div>
</template>

<script>
import { getCoursesByTeacherId } from '../../api/course'
import CourseComments from '../../components/CourseComments.vue'
import { getSessionStorage } from '../../utils/common'

export default {
  name: 'TeacherCourseComments',
  components: {
    CourseComments
  },
  data() {
    return {
      courses: [],
      selectedCourseId: null,
      currentTeacher: null
    }
  },
  created() {
    this.initTeacher()
    this.loadCourses()
  },
  methods: {
    initTeacher() {
      const curuser = getSessionStorage('curuser')
      if (curuser && curuser.info) {
        this.currentTeacher = curuser.info
      }
    },
    async loadCourses() {
      if (!this.currentTeacher) return
      try {
        const res = await getCoursesByTeacherId(this.currentTeacher.teaId)
        if (res && res.flag) {
          this.courses = res.result || []
        }
      } catch (e) {
        console.error('加载课程失败', e)
      }
    },
    loadComments() {
      // 组件会自动加载
    }
  }
}
</script>

<style scoped>
.course-comments-manager {
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h2 {
  margin: 0;
}
.no-course {
  padding: 60px 0;
}
</style>
