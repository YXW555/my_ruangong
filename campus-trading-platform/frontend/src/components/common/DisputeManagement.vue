<template>
    <div class="dispute-management-container">
        <el-card shadow="never">
            <div slot="header" class="clearfix">
                <span>交易纠纷管理</span>
                <el-button style="float: right; padding: 3px 0" type="text" @click="loadDisputes">刷新</el-button>
            </div>

            <!-- 筛选条件 -->
            <div class="filter-container">
                <el-radio-group v-model="filterStatus" @change="handleStatusChange" size="small">
                    <el-radio-button :label="null">全部</el-radio-button>
                    <el-radio-button :label="0">待处理</el-radio-button>
                    <el-radio-button :label="2">已解决</el-radio-button>
                </el-radio-group>
            </div>

            <!-- 纠纷列表 -->
            <el-table
                :data="disputes"
                v-loading="loading"
                style="width: 100%"
                :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: 'bold' }"
                border>
                <el-table-column
                    prop="id"
                    label="纠纷ID"
                    width="80">
                </el-table-column>
                <el-table-column
                    prop="order.orderNumber"
                    label="订单号"
                    width="180"
                    show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                    prop="order.idleItem.idleName"
                    label="商品名称"
                    show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                    prop="applicant.nickname"
                    label="申请人"
                    width="120">
                </el-table-column>
                <el-table-column
                    label="纠纷类型"
                    width="120">
                    <template slot-scope="scope">
                        <el-tag size="small">{{ getDisputeTypeText(scope.row.disputeType) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column
                    prop="disputeReason"
                    label="纠纷原因"
                    show-overflow-tooltip
                    min-width="200">
                </el-table-column>
                <el-table-column
                    label="状态"
                    width="100">
                    <template slot-scope="scope">
                        <el-tag :type="getStatusType(scope.row.disputeStatus)" size="small">
                            {{ getStatusText(scope.row.disputeStatus) }}
                        </el-tag>
                    </template>
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
                    width="120"
                    align="center">
                    <template slot-scope="scope">
                        <el-button
                            v-if="scope.row.disputeStatus === 0"
                            type="primary"
                            size="mini"
                            @click="viewDetails(scope.row)">
                            处理
                        </el-button>
                        <el-button
                            v-else
                            type="info"
                            size="mini"
                            @click="viewDetails(scope.row)">
                            查看
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination-container">
                <el-pagination
                    @current-change="handlePageChange"
                    :current-page="currentPage"
                    :page-size="pageSize"
                    layout="total, prev, pager, next, jumper"
                    :total="total">
                </el-pagination>
            </div>
        </el-card>

        <!-- 处理纠纷对话框 -->
        <el-dialog
            :title="currentDispute && currentDispute.disputeStatus === 0 ? '处理交易纠纷' : '查看纠纷详情'"
            :visible.sync="dialogVisible"
            width="70%"
            @close="currentDispute = null">
            <div v-if="currentDispute" class="dispute-detail">
                <!-- 订单信息 -->
                <el-card shadow="never" class="detail-section">
                    <div slot="header">
                        <span>订单信息</span>
                    </div>
                    <el-descriptions :column="2" border>
                        <el-descriptions-item label="订单号">{{ currentDispute.order.orderNumber }}</el-descriptions-item>
                        <el-descriptions-item label="订单金额">¥{{ currentDispute.order.orderPrice }}</el-descriptions-item>
                        <el-descriptions-item label="商品名称">{{ currentDispute.order.idleItem.idleName }}</el-descriptions-item>
                        <el-descriptions-item label="订单状态">
                            <el-tag size="small">{{ getOrderStatusText(currentDispute.order.orderStatus) }}</el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item label="支付状态">
                            <el-tag :type="currentDispute.order.paymentStatus ? 'success' : 'warning'" size="small">
                                {{ currentDispute.order.paymentStatus ? '已支付' : '未支付' }}
                            </el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item label="创建时间">{{ currentDispute.order.createTime | formatDate }}</el-descriptions-item>
                    </el-descriptions>
                </el-card>

                <!-- 纠纷信息 -->
                <el-card shadow="never" class="detail-section">
                    <div slot="header">
                        <span>纠纷信息</span>
                    </div>
                    <el-descriptions :column="2" border>
                        <el-descriptions-item label="申请人">{{ currentDispute.applicant.nickname }}</el-descriptions-item>
                        <el-descriptions-item label="申请时间">{{ currentDispute.createTime | formatDate }}</el-descriptions-item>
                        <el-descriptions-item label="纠纷类型">
                            <el-tag size="small">{{ getDisputeTypeText(currentDispute.disputeType) }}</el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item label="纠纷状态">
                            <el-tag :type="getStatusType(currentDispute.disputeStatus)" size="small">
                                {{ getStatusText(currentDispute.disputeStatus) }}
                            </el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item label="纠纷原因" :span="2">{{ currentDispute.disputeReason }}</el-descriptions-item>
                    </el-descriptions>

                    <!-- 证据图片 -->
                    <div v-if="currentDispute.evidenceImages" class="evidence-images">
                        <el-divider>证据图片</el-divider>
                        <div class="image-list">
                            <el-image
                                v-for="(img, index) in getEvidenceImages(currentDispute.evidenceImages)"
                                :key="index"
                                :src="img"
                                :preview-src-list="getEvidenceImages(currentDispute.evidenceImages)"
                                fit="cover"
                                class="evidence-image">
                            </el-image>
                        </div>
                    </div>
                </el-card>

                <!-- 处理结果（已处理时显示） -->
                <el-card v-if="currentDispute.disputeStatus === 2" shadow="never" class="detail-section">
                    <div slot="header">
                        <span>处理结果</span>
                    </div>
                    <el-descriptions :column="2" border>
                        <el-descriptions-item label="处理结果">
                            <el-tag type="success" size="small">{{ getHandleResultText(currentDispute.handleResult) }}</el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item label="处理时间">{{ currentDispute.handleTime | formatDate }}</el-descriptions-item>
                        <el-descriptions-item label="处理说明" :span="2">{{ currentDispute.handleDescription || '无' }}</el-descriptions-item>
                    </el-descriptions>
                </el-card>

                <!-- 处理操作（待处理时显示） -->
                <el-card v-if="currentDispute.disputeStatus === 0" shadow="never" class="detail-section">
                    <div slot="header">
                        <span>处理操作</span>
                    </div>
                    <el-form :model="handleForm" label-width="120px">
                        <el-form-item label="处理结果" required>
                            <el-radio-group v-model="handleForm.handleResult">
                                <el-radio :label="1">退款给买家</el-radio>
                                <el-radio :label="2">驳回申请</el-radio>
                                <el-radio :label="3">要求退货退款</el-radio>
                                <el-radio :label="4">警告用户</el-radio>
                                <el-radio :label="5">其他</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item label="处理说明">
                            <el-input
                                type="textarea"
                                :rows="4"
                                v-model="handleForm.handleDescription"
                                placeholder="请输入处理说明">
                            </el-input>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="submitHandle">提交处理</el-button>
                            <el-button @click="dialogVisible = false">取消</el-button>
                        </el-form-item>
                    </el-form>
                </el-card>
            </div>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: 'DisputeManagement',
    data() {
        return {
            disputes: [],
            loading: false,
            currentPage: 1,
            pageSize: 8,
            total: 0,
            filterStatus: null, // null=全部, 0=待处理, 2=已解决
            dialogVisible: false,
            currentDispute: null,
            handleForm: {
                handleResult: 1,
                handleDescription: ''
            }
        }
    },
    created() {
        this.loadDisputes();
    },
    filters: {
        formatDate(dateStr) {
            if (!dateStr) return '';
            const date = new Date(dateStr);
            return date.toLocaleString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
        }
    },
    methods: {
        loadDisputes() {
            this.loading = true;
            this.$api.adminGetDisputeList({
                status: this.filterStatus,
                page: this.currentPage,
                nums: this.pageSize
            }).then(res => {
                if (res.status_code === 1) {
                    this.disputes = res.data.list || [];
                    this.total = res.data.count || 0;
                } else {
                    this.$message.error('加载纠纷列表失败');
                }
            }).catch(err => {
                console.error('加载纠纷列表失败', err);
                this.$message.error('加载纠纷列表失败');
            }).finally(() => {
                this.loading = false;
            });
        },
        handleStatusChange() {
            this.currentPage = 1;
            this.loadDisputes();
        },
        handlePageChange(page) {
            this.currentPage = page;
            this.loadDisputes();
        },
        viewDetails(dispute) {
            this.currentDispute = dispute;
            this.handleForm.handleResult = 1;
            this.handleForm.handleDescription = '';
            this.dialogVisible = true;
        },
        submitHandle() {
            if (!this.handleForm.handleResult) {
                this.$message.warning('请选择处理结果');
                return;
            }
            
            this.$confirm('确认提交处理结果？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$api.adminHandleDispute({
                    id: this.currentDispute.id,
                    handleResult: this.handleForm.handleResult,
                    handleDescription: this.handleForm.handleDescription
                }).then(res => {
                    if (res.status_code === 1) {
                        this.$message.success('处理成功');
                        this.dialogVisible = false;
                        this.loadDisputes();
                    } else {
                        this.$message.error(res.msg || '处理失败');
                    }
                }).catch(err => {
                    console.error('处理纠纷失败', err);
                    this.$message.error('处理失败');
                });
            }).catch(() => {});
        },
        getDisputeTypeText(type) {
            const types = {
                1: '商品描述不符',
                2: '商品质量问题',
                3: '未收到货',
                4: '退款问题',
                5: '其他'
            };
            return types[type] || '未知';
        },
        getStatusText(status) {
            const statuses = {
                0: '待处理',
                1: '处理中',
                2: '已解决',
                3: '已关闭'
            };
            return statuses[status] || '未知';
        },
        getStatusType(status) {
            const types = {
                0: 'warning',
                1: 'info',
                2: 'success',
                3: 'info'
            };
            return types[status] || '';
        },
        getHandleResultText(result) {
            const results = {
                1: '退款给买家',
                2: '驳回申请',
                3: '要求退货退款',
                4: '警告用户',
                5: '其他'
            };
            return results[result] || '未知';
        },
        getOrderStatusText(status) {
            const statuses = {
                0: '待确认',
                1: '已确认',
                2: '已发货',
                3: '已完成',
                4: '已取消'
            };
            return statuses[status] || '未知';
        },
        getEvidenceImages(imagesStr) {
            if (!imagesStr) return [];
            try {
                return JSON.parse(imagesStr);
            } catch (e) {
                return [];
            }
        }
    }
}
</script>

<style scoped>
.dispute-management-container {
    padding: 20px;
}

.filter-container {
    margin-bottom: 20px;
}

.pagination-container {
    margin-top: 20px;
    text-align: right;
}

.detail-section {
    margin-bottom: 20px;
}

.evidence-images {
    margin-top: 20px;
}

.image-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
}

.evidence-image {
    width: 150px;
    height: 150px;
    border-radius: 4px;
    cursor: pointer;
}
</style>

