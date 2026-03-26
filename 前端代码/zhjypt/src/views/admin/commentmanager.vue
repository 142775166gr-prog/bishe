<template>
  <div class="comment-manager">
    <div class="page-header">
      <h2>评论管理</h2>
      <el-select v-model="selectedCourseId" placeholder="选择课程筛选" @change="loadComments" clearable>
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
      <el-empty description="请选择一个课程管理评论" />
    </div>
  </div>
</template>

<script>
import { getAllCourses } from '../../api/course'
import CourseComments from '../../components/CourseComments.vue'

export default {
  name: 'AdminCommentManager',
  components: {
    CourseComments
  },
  data() {
    return {
      courses: [],
      selectedCourseId: null
    }
  },
  created() {
    this.loadCourses()
  },
  methods: {
    async loadCourses() {
      try {
        const res = await getAllCourses()
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
.comment-manager {
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
