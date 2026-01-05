<template>
  <div class="admin-announcement page-card">
    <h2>发布公告</h2>
    <el-form :model="form" label-width="80px">
      <el-form-item label="标题">
        <el-input v-model="form.title" maxlength="128"></el-input>
      </el-form-item>
      <el-form-item label="内容">
        <quill-editor ref="quillEditor" v-model="form.content" :options="editorOption" style="height:300px;"></quill-editor>
      </el-form-item>
      <el-form-item label="置顶">
        <el-switch v-model="form.isPinned"></el-switch>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit" :loading="submitting">发布</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { quillEditor } from 'vue-quill-editor';
import 'quill/dist/quill.core.css';
import 'quill/dist/quill.snow.css';
import 'quill/dist/quill.bubble.css';

export default {
  name: 'AdminAnnouncement',
  data() {
    return {
      form: {
        title: '',
        content: '',
        isPinned: 0
      },
      submitting: false
    };
  },
  components: {
    quillEditor
  },
  computed: {
    editorOption() {
      return {
        placeholder: '请输入公告内容（支持图片上传）',
        modules: {
          toolbar: {
            container: [
              ['bold', 'italic', 'underline'],
              [{ 'header': [1, 2, 3, false] }],
              [{ 'list': 'ordered'}, { 'list': 'bullet' }],
              ['link', 'image']
            ],
            handlers: {
              image: this.imageHandler
            }
          }
        }
      };
    }
  },
  methods: {
    async imageHandler() {
      // create file input
      const input = document.createElement('input');
      input.setAttribute('type', 'file');
      input.setAttribute('accept', 'image/*');
      input.click();
      input.onchange = async () => {
        const file = input.files[0];
        if (!file) return;
        const formData = new FormData();
        formData.append('file', file);
        try {
          const res = await fetch('/api/file', {
            method: 'POST',
            body: formData
          });
          const text = await res.text();
          let parsed = {};
          try { parsed = JSON.parse(text); } catch { parsed = { status_code: 1, data: text }; }
          const url = parsed && parsed.status_code === 1 ? parsed.data : text;
          const quill = this.$refs.quillEditor && this.$refs.quillEditor.quill ? this.$refs.quillEditor.quill : null;
          if (quill) {
            const range = quill.getSelection();
            quill.insertEmbed(range ? range.index : 0, 'image', url);
          } else {
            // fallback append as plain img tag
            this.form.content += `<img src="${url}" />`;
          }
        } catch (e) {
          this.$message.error('图片上传失败');
        }
      };
    },
    submit() {
      if (!this.form.title || !this.form.content) {
        this.$message.error('请填写标题和内容');
        return;
      }
      this.submitting = true;
      const payload = {
        title: this.form.title,
        content: this.form.content,
        isPinned: this.form.isPinned ? 1 : 0
      };
      const apiCall = this.editId ? this.$api.updateAnnouncement({ id: this.editId, ...payload }) : this.$api.addAnnouncement(payload);
      apiCall.then(res => {
        if (res.status_code === 1) {
          this.$message.success('发布成功');
          this.form.title = '';
          this.form.content = '';
          this.form.isPinned = 0;
          this.$router.push('/announcements');
        } else {
          this.$message.error(res.msg || '发布失败');
        }
      }).catch(() => {
        this.$message.error('网络错误');
      }).finally(() => {
        this.submitting = false;
      });
    }
  },
  mounted() {
    // if editing (id passed), load existing announcement
    const id = this.$route.query.id;
    if (id) {
      this.editId = id;
      this.$api.getAnnouncement({ id }).then(res => {
        if (res.status_code === 1 && res.data) {
          this.form.title = res.data.title;
          this.form.content = res.data.content;
          this.form.isPinned = res.data.isPinned === 1 ? 1 : 0;
        }
      });
    }
  }
};
</script>

<style scoped>
.admin-announcement {
  max-width: 980px;
  margin: 24px auto;
  background: #ffffff;
  border-radius: 8px;
  padding: 28px;
  box-shadow: 0 6px 18px rgba(24,39,75,0.08);
}
.admin-announcement h2{
  font-size: 24px;
  font-weight: 700;
  color: #2d3a4b;
  margin-bottom: 18px;
}
.admin-announcement .el-form {
  background: transparent;
}
.admin-announcement .el-form-item {
  margin-bottom: 18px;
}
.admin-announcement .el-input__inner {
  background: #fafbfd;
  border-radius: 6px;
  border: 1px solid #e6eef8;
}
.admin-announcement .quill-editor {
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #e6eef8;
  background: #fafbfd;
}
.admin-announcement .el-switch {
  vertical-align: middle;
}
.admin-announcement .el-button--primary {
  background: linear-gradient(90deg,#409eff,#60aef6);
  border-color: transparent;
  box-shadow: 0 6px 12px rgba(64,158,255,0.12);
}
@media (max-width: 768px) {
  .admin-announcement {
    padding: 16px;
    margin: 12px;
  }
  .admin-announcement h2 {
    font-size: 20px;
  }
}
</style>

