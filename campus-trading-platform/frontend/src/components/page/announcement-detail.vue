<template>
  <div class="announcement-detail page-card">
    <el-card class="mt-16" v-if="announcement">
      <div style="display:flex;justify-content:space-between;align-items:flex-start;">
        <div style="display:flex;align-items:center;">
          <img v-if="thumbnail" :src="thumbnail" style="width:56px;height:56px;object-fit:cover;border-radius:6px;margin-right:12px" />
          <div>
            <div style="font-size:18px;font-weight:700">{{ announcement.title }}</div>
            <div style="color:#909399;font-size:12px;margin-top:4px;">
              {{ formatTime(announcement.createTime) }} · {{ roleLabel(announcement.creatorRole) }}
            </div>
          </div>
        </div>
      </div>
      <div class="mt-12" v-html="announcement.content"></div>
    </el-card>
    <div v-else class="mt-40" style="text-align:center;color:#909399">正在加载公告...</div>
  </div>
</template>

<script>
export default {
  name: 'AnnouncementDetail',
  data() {
    return {
      announcement: null
    };
  },
  created() {
    const id = this.$route.query.id;
    if (id) {
      this.load(id);
    }
  },
  methods: {
    load(id) {
      this.$api.getAnnouncement({ id }).then(res => {
        if (res.status_code === 1 && res.data) {
          this.announcement = res.data;
        } else {
          this.$message.error(res.msg || '加载公告失败');
        }
      }).catch(() => {
        this.$message.error('网络错误，加载公告失败');
      });
    },
    formatTime(t) {
      if (!t) return '';
      return t.toString().substring(0, 19).replace('T', ' ');
    },
    getFirstImageFromContent(html) {
      if (!html) return null;
      const m = html.match(/<img\s+[^>]*src=["']([^"']+)["'][^>]*>/i);
      return m ? m[1] : null;
    },
    roleLabel(role) {
      if (role === 2) return '管理员';
      if (role === 1) return '商家';
      return '公告发布者';
    }
  },
  computed: {
    thumbnail() {
      if (!this.announcement) return null;
      return this.getFirstImageFromContent(this.announcement.content);
    }
  }
};
</script>

<style scoped>
.announcement-detail {
  max-width: 980px;
  margin: 24px auto;
}
</style>

