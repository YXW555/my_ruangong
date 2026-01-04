<template>
    <div>
        <app-head></app-head>
        <app-body>
            <div class="auto-reply-container">
                <div class="page-header">
                    <h2>智能自动回复设置</h2>
                    <p class="page-desc">设置常见问题的自动回复模板，买家发送关键词时系统会自动回复</p>
                </div>

                <div class="template-list">
                    <div class="list-header">
                        <span>我的自动回复模板</span>
                        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">添加模板</el-button>
                    </div>

                    <div v-if="templateList.length === 0" class="empty-state">
                        <p>暂无自动回复模板</p>
                        <el-button type="primary" @click="handleAdd">立即添加</el-button>
                    </div>

                    <el-table v-else :data="templateList" style="width: 100%">
                        <el-table-column prop="keyword" label="关键词" width="150">
                            <template slot-scope="scope">
                                <el-tag>{{ scope.row.keyword }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="replyContent" label="回复内容" min-width="300">
                            <template slot-scope="scope">
                                <div class="reply-content">{{ scope.row.replyContent }}</div>
                            </template>
                        </el-table-column>
                        <el-table-column prop="isEnabled" label="状态" width="100" align="center">
                            <template slot-scope="scope">
                                <el-switch
                                    v-model="scope.row.isEnabled"
                                    :active-value="1"
                                    :inactive-value="0"
                                    @change="handleToggleStatus(scope.row)"
                                ></el-switch>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="180" align="center">
                            <template slot-scope="scope">
                                <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
                                <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>

                <!-- 添加/编辑对话框 -->
                <el-dialog
                    :title="dialogTitle"
                    :visible.sync="dialogVisible"
                    width="600px"
                    @close="handleDialogClose"
                >
                    <el-form :model="form" :rules="rules" ref="form" label-width="100px">
                        <el-form-item label="关键词" prop="keyword">
                            <el-input
                                v-model="form.keyword"
                                placeholder="例如：自提、价格、地址"
                                maxlength="64"
                                show-word-limit
                            ></el-input>
                            <div class="form-tip">买家消息中包含此关键词时，将自动回复</div>
                        </el-form-item>
                        <el-form-item label="回复内容" prop="replyContent">
                            <el-input
                                type="textarea"
                                :rows="4"
                                v-model="form.replyContent"
                                placeholder="例如：自提点：3 栋楼下快递柜"
                                maxlength="512"
                                show-word-limit
                            ></el-input>
                        </el-form-item>
                        <el-form-item label="启用状态">
                            <el-switch
                                v-model="form.isEnabled"
                                :active-value="1"
                                :inactive-value="0"
                            ></el-switch>
                        </el-form-item>
                    </el-form>
                    <div slot="footer" class="dialog-footer">
                        <el-button @click="dialogVisible = false">取消</el-button>
                        <el-button type="primary" @click="handleSubmit">确定</el-button>
                    </div>
                </el-dialog>
            </div>
            <app-foot></app-foot>
        </app-body>
    </div>
</template>

<script>
    import AppHead from '../common/AppHeader.vue';
    import AppBody from '../common/AppPageBody.vue';
    import AppFoot from '../common/AppFoot.vue';

    export default {
        name: "auto-reply-manage",
        components: {
            AppHead,
            AppBody,
            AppFoot
        },
        data() {
            return {
                templateList: [],
                dialogVisible: false,
                dialogTitle: '添加模板',
                form: {
                    id: null,
                    keyword: '',
                    replyContent: '',
                    isEnabled: 1
                },
                rules: {
                    keyword: [
                        { required: true, message: '请输入关键词', trigger: 'blur' },
                        { min: 1, max: 64, message: '关键词长度在 1 到 64 个字符', trigger: 'blur' }
                    ],
                    replyContent: [
                        { required: true, message: '请输入回复内容', trigger: 'blur' },
                        { min: 1, max: 512, message: '回复内容长度在 1 到 512 个字符', trigger: 'blur' }
                    ]
                }
            };
        },
        created() {
            this.loadTemplateList();
        },
        methods: {
            async loadTemplateList() {
                try {
                    const res = await this.$api.getAutoReplyTemplateList();
                    if (res.status_code === 1 && res.data) {
                        this.templateList = res.data;
                    }
                } catch (e) {
                    this.$message.error('加载模板列表失败');
                }
            },
            handleAdd() {
                this.dialogTitle = '添加模板';
                this.form = {
                    id: null,
                    keyword: '',
                    replyContent: '',
                    isEnabled: 1
                };
                this.dialogVisible = true;
            },
            handleEdit(row) {
                this.dialogTitle = '编辑模板';
                this.form = {
                    id: row.id,
                    keyword: row.keyword,
                    replyContent: row.replyContent,
                    isEnabled: row.isEnabled
                };
                this.dialogVisible = true;
            },
            handleDelete(row) {
                this.$confirm('确定要删除这个模板吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(async () => {
                    try {
                        const res = await this.$api.deleteAutoReplyTemplate(row.id);
                        if (res.status_code === 1) {
                            this.$message.success('删除成功');
                            this.loadTemplateList();
                        } else {
                            this.$message.error(res.msg || '删除失败');
                        }
                    } catch (e) {
                        this.$message.error('删除失败');
                    }
                }).catch(() => {});
            },
            async handleToggleStatus(row) {
                try {
                    const res = await this.$api.updateAutoReplyTemplate({
                        id: row.id,
                        isEnabled: row.isEnabled
                    });
                    if (res.status_code === 1) {
                        this.$message.success(row.isEnabled === 1 ? '已启用' : '已禁用');
                    } else {
                        this.$message.error(res.msg || '操作失败');
                        // 恢复原状态
                        row.isEnabled = row.isEnabled === 1 ? 0 : 1;
                    }
                } catch (e) {
                    this.$message.error('操作失败');
                    // 恢复原状态
                    row.isEnabled = row.isEnabled === 1 ? 0 : 1;
                }
            },
            handleSubmit() {
                this.$refs.form.validate(async (valid) => {
                    if (valid) {
                        try {
                            const res = await this.$api.createOrUpdateAutoReplyTemplate(this.form);
                            if (res.status_code === 1) {
                                this.$message.success(this.form.id ? '更新成功' : '添加成功');
                                this.dialogVisible = false;
                                this.loadTemplateList();
                            } else {
                                this.$message.error(res.msg || '操作失败');
                            }
                        } catch (e) {
                            this.$message.error('操作失败');
                        }
                    }
                });
            },
            handleDialogClose() {
                this.$refs.form.resetFields();
            }
        }
    }
</script>

<style scoped>
    .auto-reply-container {
        min-height: 85vh;
        padding: 20px;
        max-width: 1200px;
        margin: 0 auto;
    }

    .page-header {
        margin-bottom: 30px;
    }

    .page-header h2 {
        font-size: 24px;
        color: #303133;
        margin-bottom: 8px;
    }

    .page-desc {
        font-size: 14px;
        color: #909399;
    }

    .template-list {
        background: #fff;
        border-radius: 8px;
        padding: 20px;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    }

    .list-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
    }

    .empty-state {
        text-align: center;
        padding: 60px 20px;
        color: #909399;
    }

    .empty-state p {
        margin-bottom: 20px;
        font-size: 14px;
    }

    .reply-content {
        word-break: break-word;
        line-height: 1.6;
    }

    .form-tip {
        font-size: 12px;
        color: #909399;
        margin-top: 4px;
    }

    .dialog-footer {
        text-align: right;
    }
</style>

