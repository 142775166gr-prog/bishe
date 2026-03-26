<template>
  <div class="suggestion-list">
    <div class="header">
      <h2>教育建议</h2>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-bar">
      <el-radio-group v-model="filterType" @change="loadSuggestions">
        <el-radio-button :label="null">全部</el-radio-button>
        <el-radio-button :label="1">学习方法</el-radio-button>
        <el-radio-button :label="2">课程推荐</el-radio-button>
        <el-radio-button :label="3">学习计划</el-radio-button>
        <el-radio-button :label="4">其他</el-radio-button>
      </el-radio-group>

      <el-radio-group v-model="filterRead" @change="loadSuggestions" style="margin-left: 20px">
        <el-radio-button :label="null">全部</el-radio-button>
        <el-radio-button :label="0">未读</el-radio-button>
        <el-radio-button :label="1">已读</el-radio-button>
      </el-radio-group>

      <el-radio-group v-model="filterFavorite" @change="loadSuggestions" style="margin-left: 20px">
        <el-radio-button :label="null">全部</el-radio-button>
        <el-radio-button :label="1">收藏</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 建议列表 -->
    <div class="suggestion-items">
      <el-empty v-if="suggestions.length === 0" description="暂无教育建议" />
      
      <div v-for="item in suggestions" :key="item.suggestionId" class="suggestion-item" :class="{ unread: item.isRead === 0 }">
        <div class="item-header">
          <div class="title-row">
            <el-tag v-if="item.isRead === 0" type="danger" size="small">未读</el-tag>
            <el-tag :type="getTypeColor(item.suggestionType)" size="small">{{ getTypeName(item.suggestionType) }}</el-tag>
            <h3>{{ item.suggestionTitle }}</h3>
            <el-icon v-if="item.isFavorite === 1" class="favorite-icon" color="#f56c6c"><StarFilled /></el-icon>
          </div>
          <div class="meta-info">
            <span>来自：{{ item.teacherName }}</span>
            <span v-if="item.courseTitle">课程：{{ item.courseTitle }}</span>
            <span>{{ formatTime(item.createTime) }}</span>
          </div>
        </div>

        <div class="item-content">
          <p>{{ item.suggestionContent }}</p>
        </div>

        <div class="item-actions">
          <el-button v-if="item.isRead === 0" type="primary" size="small" @click="markAsRead(item)">
            标记已读
          </el-button>
          <el-button :type="item.isFavorite === 1 ? 'warning' : 'default'" size="small" @click="toggleFavorite(item)">
            <el-icon><Star /></el-icon>
            {{ item.isFavorite === 1 ? '取消收藏' : '收藏' }}
          </el-button>
          <el-button type="danger" size="small" @click="deleteSuggestion(item)">
            删除
          </el-button>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <el-pagination
      v-if="total > 0"
      :current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script>
import { getStudentSuggestions, markSuggestionAsRead, toggleSuggestionFavorite, deleteSuggestion as deleteSuggestionApi } from '../../api/suggestion.js'
import { getSessionStorage } from '../../utils/common.js'

export default {
  name: 'SuggestionList',
  data() {
    return {
      suggestions: [],
      filterType: null,
      filterRead: null,
      filterFavorite: null,
      pageNum: 1,
      pageSize: 10,
      total: 0
    }
  },
  computed: {
    currentUser() {
      const user = getSessionStorage('curuser')
      return user ? user.info : null
    }
  },
  mounted() {
    this.loadSuggestions()
  },
  methods: {
    handleSizeChange(size) {
      this.pageSize = size
      this.pageNum = 1
      this.loadSuggestions()
    },
    handleCurrentChange(page) {
      this.pageNum = page
      this.loadSuggestions()
    },
    async loadSuggestions() {
      try {
        const res = await getStudentSuggestions(
          this.currentUser.stuId,
          this.filterType,
          this.filterRead,
          this.filterFavorite,
          this.pageNum,
          this.pageSize
        )
        if (res.flag) {
          this.suggestions = res.result || []
          this.total = res.total || 0
        }
      } catch (e) {
        console.error('加载建议列表失败', e)
      }
    },

    async markAsRead(item) {
      try {
        const res = await markSuggestionAsRead(item.suggestionId, this.currentUser.stuId)
        if (res.flag) {
          this.$message.success('已标记为已读')
          item.isRead = 1
        }
      } catch (e) {
        console.error('标记失败', e)
      }
    },

    async toggleFavorite(item) {
      try {
        const res = await toggleSuggestionFavorite(item.suggestionId, this.currentUser.stuId)
        if (res.flag) {
          item.isFavorite = item.isFavorite === 1 ? 0 : 1
          this.$message.success(item.isFavorite === 1 ? '已收藏' : '已取消收藏')
        }
      } catch (e) {
        console.error('操作失败', e)
      }
    },

    async deleteSuggestion(item) {
      try {
        await this.$confirm('确定要删除此建议吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await deleteSuggestionApi(item.suggestionId, this.currentUser.stuId, 'student')
        if (res.flag) {
          this.$message.success('删除成功')
          this.loadSuggestions()
        }
      } catch (e) {
        if (e !== 'cancel') {
          console.error('删除失败', e)
        }
      }
    },

    getTypeName(type) {
      const names = { 1: '学习方法', 2: '课程推荐', 3: '学习计划', 4: '其他' }
      return names[type] || '其他'
    },

    getTypeColor(type) {
      const colors = { 1: 'success', 2: 'primary', 3: 'warning', 4: 'info' }
      return colors[type] || 'info'
    },

    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.suggestion-list {
  padding: 20px;
  background: linear-gradient(180deg, #f8fbff 0%, #f3f4f6 100%);
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px 16px;
  border-radius: 14px;
  background: linear-gradient(90deg, #e0f2fe 0%, #f0f9ff 55%, #f0fdf4 120%);
  border: 1px solid #e5e7eb;
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.06);
}

.header h2 {
  margin: 0;
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

.filter-bar {
  margin-bottom: 20px;
}

.suggestion-items {
  min-height: 400px;
}

.suggestion-item {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  transition: all 0.3s;
}

.suggestion-item.unread {
  border-left: 4px solid #409eff;
  background: #f0f9ff;
}

.item-header {
  margin-bottom: 15px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.title-row h3 {
  margin: 0;
  font-size: 18px;
  flex: 1;
}

.favorite-icon {
  font-size: 20px;
}

.meta-info {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 14px;
}

.item-content {
  margin-bottom: 15px;
}

.item-content p {
  color: #333;
  line-height: 1.8;
  margin: 0;
}

.item-actions {
  display: flex;
  gap: 10px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: center;
}
</style>
