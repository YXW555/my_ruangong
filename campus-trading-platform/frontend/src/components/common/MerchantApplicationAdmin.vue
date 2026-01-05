<template>
    <div class="merchant-application-admin-container">
        <el-card shadow="never">
            <div slot="header" class="clearfix">
                <span>商家认证审核</span>
                <el-button style="float: right; padding: 3px 0" type="text" @click="loadApplications">刷新</el-button>
            </div>

            <el-table
                :data="applications"
                v-loading="loading"
                style="width: 100%">
                <el-table-column
                    prop="user.nickname"
                    label="申请人"
                    width="180">
                </el-table-column>
                <el-table-column
                    prop="shopName"
                    label="店铺名称">
                </el-table-column>
                <el-table-column
                    prop="contactPhone"
                    label="联系电话"
                    width="180">
                </el-table-column>
                <el-table-column
                    prop="createTime"
                    label="申请时间"
                    width="180">
                    <template slot-scope="scope">
                        {{ scope.row.createTime | formatDate }}
                    </template>
                </el-table-column>
                <el-table-column
                    label="操作"
                    width="120">
                    <template slot-scope="scope">
                        <el-button type="primary" size="mini" @click="viewDetails(scope.row)">查看详情</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- 详情对话框 -->
        <el-dialog
            title="审核商家认证申请"
            :visible.sync="dialogVisible"
            width="60%"
            @close="currentApplication = null">
            <div v-if="currentApplication" class="detail-content">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="申请人">{{ currentApplication.user.nickname }}</el-descriptions-item>
                    <el-descriptions-item label="申请时间">{{ currentApplication.createTime | formatDate }}</el-descriptions-item>
                    <el-descriptions-item label="店铺名称">{{ currentApplication.shopName }}</el-descriptions-item>
                    <el-descriptions-item label="联系电话">{{ currentApplication.contactPhone }}</el-descriptions-item>
                    <el-descriptions-item label="联系地址" :span="2">{{ currentApplication.contactAddress }}</el-descriptions-item>
                    <el-descriptions-item label="申请理由" :span="2">{{ currentApplication.applicationReason || '无' }}</el-descriptions-item>
                </el-descriptions>

                <el-divider>认证材料</el-divider>

                <div class="image-gallery">
                    <div class="image-item">
                        <p>身份证正面</p>
                        <el-image :src="currentApplication.idCardFront" :preview-src-list="[currentApplication.idCardFront]"></el-image>
                    </div>
                    <div class="image-item">
                        <p>身份证反面</p>
                        <el-image :src="currentApplication.idCardBack" :preview-src-list="[currentApplication.idCardBack]"></el-image>
                    </div>
                    <div class="image-item" v-if="currentApplication.businessLicense">
                        <p>营业执照</p>
                        <el-image :src="currentApplication.businessLicense" :preview-src-list="[currentApplication.businessLicense]"></el-image>
                    </div>
                </div>

                <el-divider>审核操作</el-divider>
                <div class="review-actions">
                     <el-input
                        v-model="adminComment"
                        type="textarea"
                        :rows="3"
                        placeholder="如果拒绝，请填写拒绝理由..."
                        style="margin-bottom: 20px;">
                    </el-input>
                    <el-button type="success" icon="el-icon-check" @click="review(1)">批准申请</el-button>
                    <el-button type="danger" icon="el-icon-close" @click="review(2)">拒绝申请</el-button>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">关闭</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: "MerchantApplicationAdmin",
    data() {
        return {
            loading: false,
            applications: [],
            dialogVisible: false,
            currentApplication: null,
            adminComment: ''
        };
    },
    created() {
        this.loadApplications();
    },
    methods: {
        loadApplications() {
            this.loading = true;
            this.$api.adminGetAllApplications({ status: 0 }) // 0 表示待审核
                .then(res => {
                    if (res.status_code === 1) {
                        this.applications = res.data;
                    }
                    this.loading = false;
                })
                .catch(() => {
                    this.loading = false;
                    this.$message.error('数据加载失败');
                });
        },
        viewDetails(application) {
            this.currentApplication = application;
            this.adminComment = '';
            this.dialogVisible = true;
        },
        async review(status) {
            if (status === 2 && !this.adminComment) {
                this.$message.warning('拒绝申请必须填写理由');
                return;
            }

            try {
                const res = await this.$api.adminReviewApplication({
                    applicationId: this.currentApplication.id,
                    status: status,
                    adminComment: this.adminComment
                });

                if (res.status_code === 1) {
                    this.$message.success('操作成功');
                    this.dialogVisible = false;
                    // 刷新列表（忽略其内部错误）
                    try {
                        this.loadApplications();
                    } catch (e) {
                        console.error('刷新列表失败', e);
                    }
                    // 刷新父组件的角标（如果存在）
                    try {
                        if (this.$parent && typeof this.$parent.loadPendingCount === 'function') {
                            this.$parent.loadPendingCount();
                        }
                    } catch (e) {
                        console.error('刷新待审核数量失败', e);
                    }
                } else {
                    this.$message.error(res.msg || '操作失败');
                }
            } catch (e) {
                console.error('审核请求失败', e);
                this.$message.error('操作失败');
            }
        }
    },
    filters: {
        formatDate(time) {
            if (!time) return '';
            let date = new Date(time);
            return date.toLocaleString();
        }
    }
}
</script>

<style scoped>
.merchant-application-admin-container {
    padding: 20px;
}
.detail-content {
    max-height: 60vh;
    overflow-y: auto;
}
.image-gallery {
    display: flex;
    gap: 20px;
    margin-top: 20px;
}
.image-item {
    text-align: center;
}
.image-item p {
    margin-bottom: 10px;
    color: #606266;
}
.image-item .el-image {
    width: 200px;
    height: 120px;
    border: 1px solid #ebeef5;
    border-radius: 4px;
}
.review-actions {
    text-align: center;
    margin-top: 20px;
}
</style>
