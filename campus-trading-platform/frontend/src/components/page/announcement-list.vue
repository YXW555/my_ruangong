<template>
  <div class="announcement-list page-card">
    <h2>平台公告</h2>
    <el-card v-for="item in paged" :key="item.id" class="mt-16">
      <div style="display:flex;justify-content:space-between;align-items:center;">
        <div>
          <strong v-if="item.isPinned === 1" style="color:#f56c6c">[置顶]</strong>
          <img v-if="getThumbnail(item)" :src="getThumbnail(item)" style="width:48px;height:48px;object-fit:cover;margin-left:8px;margin-right:8px;border-radius:4px;vertical-align:middle" />
          <span style="font-size:16px;margin-left:8px">{{ item.title }}</span>
        </div>
        <div style="color:#909399;font-size:12px">{{ formatTime(item.createTime) }}</div>
      </div>
      <div class="mt-8" v-html="item.content"></div>
      <div class="mt-8" v-if="isAdmin">
        <el-button size="small" type="warning" @click="edit(item)">编辑</el-button>
        <el-button size="small" type="danger" @click="remove(item.id)">删除</el-button>
        <el-button size="small" type="primary" @click="togglePin(item)">{{ item.isPinned===1 ? '取消置顶' : '置顶' }}</el-button>
      </div>
    </el-card>
    <div style="text-align:center;margin-top:16px">
      <el-pagination background layout="prev, pager, next" :page-size="pageSize" :total="announcements.length" @current-change="onPageChange"></el-pagination>
    </div>
  </div>
</template>

<script>
import { listAnnouncements } from '@/api';
export default {
  name: 'AnnouncementList',
  data() {
    return {
      announcements: [],
      pageSize: 5,
      currentPage: 1,
      isAdmin: false
    };
  },
  created() {
    this.load();
  },
  methods: {
    load() {
      this.$api.listAnnouncements().then(res => {
        if (res.status_code === 1 && res.data) {
          this.announcements = res.data;
          // detect admin from global state if available
          if (this.$sta && this.$sta.user && this.$sta.user.userRole) {
            this.isAdmin = this.$sta.user.userRole === 2 || this.$sta.user.userRole === 1;
          }
        }
      }).catch(() => {});
    },
    onPageChange(page) {
      this.currentPage = page;
    },
    
    edit(item) {
      // navigate to admin page with id for editing
      this.$router.push({ path: '/admin/announcement', query: { id: item.id }});
    },
    remove(id) {
      this.$confirm('确认删除该公告吗？', '删除确认', { type: 'warning' }).then(() => {
        this.$api.deleteAnnouncement({ id }).then(res => {
          if (res.status_code === 1) {
            this.$message.success('删除成功');
            this.load();
          } else {
            this.$message.error(res.msg || '删除失败');
          }
        }).catch(() => this.$message.error('删除失败'));
      }).catch(()=>{});
    },
    togglePin(item) {
      const newPinned = item.isPinned === 1 ? 0 : 1;
      this.$api.updateAnnouncement({ id: item.id, isPinned: newPinned }).then(res => {
        if (res.status_code === 1) {
          this.$message.success('操作成功');
          this.load();
        } else {
          this.$message.error(res.msg || '操作失败');
        }
      }).catch(()=>this.$message.error('操作失败'));
    },
    formatTime(t) {
      if (!t) return '';
      return t.toString().substring(0, 19).replace('T', ' ');
    }
    ,
    getThumbnail(item) {
      if (!item || !item.content) return null;
      const m = item.content.match(/<img\s+[^>]*src=["']([^"']+)["'][^>]*>/i);
      return m ? m[1] : null;
    }
  }
  ,
  computed: {
    paged() {
      const start = (this.currentPage - 1) * this.pageSize;
      return this.announcements.slice(start, start + this.pageSize);
    }
  }
};
</script>

<style scoped>
.announcement-list { padding: 20px; max-width: 900px; margin: 0 auto; }
.page-card h2 { font-size: 20px; margin-bottom: 12px; }
.mt-8 { margin-top: 8px; }
.mt-16 { margin-top: 16px; }
</style>

