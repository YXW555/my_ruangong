<template>
    <div class="dispute-management-container">
        <el-card shadow="never">
            <div slot="header" class="clearfix">
                <span>交易纠纷与售后管理</span>
                <el-button style="float: right; padding: 3px 0" type="text" @click="loadAllData">刷新</el-button>
            </div>

            <!-- 筛选条件 -->
            <div class="filter-container">
                <el-radio-group v-model="filterStatus" @change="handleStatusChange" size="small">
                    <el-radio-button :label="null">全部</el-radio-button>
                    <el-radio-button :label="0">待处理</el-radio-button>
                    <el-radio-button :label="2">已解决</el-radio-button>
                </el-radio-group>
                <el-radio-group v-model="filterType" @change="handleTypeChange" size="small" style="margin-left: 20px;">
                    <el-radio-button :label="null">全部类型</el-radio-button>
                    <el-radio-button :label="'dispute'">交易纠纷</el-radio-button>
                    <el-radio-button :label="'afterSale'">售后申请</el-radio-button>
                </el-radio-group>
            </div>

            <!-- 空状态提示 -->
            <div v-if="!loading && allItems.length === 0" style="text-align: center; padding: 40px;">
                <el-empty description="暂无数据">
                    <el-button type="primary" @click="loadAllData">刷新</el-button>
                </el-empty>
            </div>

            <!-- 纠纷和售后列表 -->
            <el-table
                v-else
                :data="allItems"
                v-loading="loading"
                style="width: 100%"
                :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: 'bold' }"
                border>
                <el-table-column
                    label="类型"
                    width="100">
                    <template slot-scope="scope">
                        <el-tag :type="scope.row.type === 'afterSale' ? 'warning' : 'primary'" size="small">
                            {{ scope.row.type === 'afterSale' ? '售后申请' : '交易纠纷' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column
                    prop="id"
                    label="ID"
                    width="80">
                </el-table-column>
                <el-table-column
                    label="订单号"
                    width="180"
                    show-overflow-tooltip>
                    <template slot-scope="scope">
                        {{ scope.row.order && scope.row.order.orderNumber }}
                    </template>
                </el-table-column>
                <el-table-column
                    label="商品名称"
                    show-overflow-tooltip>
                    <template slot-scope="scope">
                        {{ scope.row.order && scope.row.order.idleItem && scope.row.order.idleItem.idleName }}
                    </template>
                </el-table-column>
                <el-table-column
                    label="申请人"
                    width="120">
                    <template slot-scope="scope">
                        {{ scope.row.type === 'afterSale' ? (scope.row.buyer && scope.row.buyer.nickname) : (scope.row.applicant && scope.row.applicant.nickname) }}
                    </template>
                </el-table-column>
                <el-table-column
                    label="申请类型/纠纷类型"
                    width="140">
                    <template slot-scope="scope">
                        <el-tag size="small" v-if="scope.row.type === 'afterSale'">
                            {{ getApplicationTypeText(scope.row.applicationType) }}
                        </el-tag>
                        <el-tag size="small" v-else>
                            {{ getDisputeTypeText(scope.row.disputeType) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column
                    label="原因/描述"
                    show-overflow-tooltip
                    min-width="200">
                    <template slot-scope="scope">
                        {{ scope.row.type === 'afterSale' ? scope.row.problemDescription : scope.row.disputeReason }}
                    </template>
                </el-table-column>
                <el-table-column
                    label="状态"
                    width="140">
                    <template slot-scope="scope">
                        <el-tag :type="getItemStatusType(scope.row)" size="small">
                            {{ getItemStatusText(scope.row) }}
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
                            v-if="canHandle(scope.row)"
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

        <!-- 处理对话框 -->
        <el-dialog
            :title="getDialogTitle()"
            :visible.sync="dialogVisible"
            width="70%"
            @close="currentItem = null">
            <div v-if="currentItem" class="item-detail">
                <!-- 售后申请详情 -->
                <template v-if="currentItem.type === 'afterSale'">
                    <!-- 订单信息 -->
                    <el-card shadow="never" class="detail-section">
                        <div slot="header">
                            <span>订单信息</span>
                        </div>
                        <el-descriptions :column="2" border>
                            <el-descriptions-item label="订单号">{{ currentItem.order && currentItem.order.orderNumber }}</el-descriptions-item>
                            <el-descriptions-item label="订单金额">¥{{ currentItem.order && currentItem.order.orderPrice }}</el-descriptions-item>
                            <el-descriptions-item label="商品名称">{{ currentItem.order && currentItem.order.idleItem && currentItem.order.idleItem.idleName }}</el-descriptions-item>
                            <el-descriptions-item label="订单状态">
                                <el-tag size="small">{{ getOrderStatusText(currentItem.order && currentItem.order.orderStatus) }}</el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item label="支付状态">
                                <el-tag :type="currentItem.order && currentItem.order.paymentStatus ? 'success' : 'warning'" size="small">
                                    {{ currentItem.order && currentItem.order.paymentStatus ? '已支付' : '未支付' }}
                                </el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item label="创建时间">{{ currentItem.order && currentItem.order.createTime | formatDate }}</el-descriptions-item>
                        </el-descriptions>
                    </el-card>

                    <!-- 售后申请信息 -->
                    <el-card shadow="never" class="detail-section">
                        <div slot="header">
                            <span>售后申请信息</span>
                        </div>
                        <el-descriptions :column="2" border>
                            <el-descriptions-item label="买家">{{ currentItem.buyer && currentItem.buyer.nickname }}</el-descriptions-item>
                            <el-descriptions-item label="卖家">{{ currentItem.seller && currentItem.seller.nickname }}</el-descriptions-item>
                            <el-descriptions-item label="申请类型">
                                <el-tag size="small">{{ getApplicationTypeText(currentItem.applicationType) }}</el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item label="申请状态">
                                <el-tag :type="getItemStatusType(currentItem)" size="small">
                                    {{ getItemStatusText(currentItem) }}
                                </el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item label="退款金额">
                                <span style="color: #f56c6c; font-weight: bold;">¥{{ currentItem.refundAmount }}</span>
                            </el-descriptions-item>
                            <el-descriptions-item label="申请时间">{{ currentItem.createTime | formatDate }}</el-descriptions-item>
                            <el-descriptions-item label="问题描述" :span="2">{{ currentItem.problemDescription }}</el-descriptions-item>
                        </el-descriptions>

                        <!-- 买家证据图片 -->
                        <div v-if="currentItem.evidenceImages" class="evidence-images">
                            <el-divider>买家证据图片</el-divider>
                            <div class="image-list">
                                <el-image
                                    v-for="(img, index) in getEvidenceImages(currentItem.evidenceImages)"
                                    :key="index"
                                    :src="img"
                                    :preview-src-list="getEvidenceImages(currentItem.evidenceImages)"
                                    fit="cover"
                                    class="evidence-image">
                                </el-image>
                            </div>
                        </div>

                        <!-- 卖家审核信息 -->
                        <div v-if="currentItem.sellerReviewTime" style="margin-top: 20px;">
                            <el-divider>卖家审核信息</el-divider>
                            <p><strong>审核结果：</strong>
                                <el-tag :type="currentItem.sellerReviewResult === 1 ? 'success' : 'danger'" size="small">
                                    {{ currentItem.sellerReviewResult === 1 ? '同意' : '拒绝' }}
                                </el-tag>
                            </p>
                            <p v-if="currentItem.sellerRejectReason"><strong>拒绝原因：</strong>{{ currentItem.sellerRejectReason }}</p>
                            <p><strong>审核时间：</strong>{{ currentItem.sellerReviewTime | formatDate }}</p>
                            <div v-if="currentItem.sellerEvidenceImages" class="evidence-images">
                                <p><strong>卖家举证图片：</strong></p>
                                <div class="image-list">
                                    <el-image
                                        v-for="(img, index) in getEvidenceImages(currentItem.sellerEvidenceImages)"
                                        :key="index"
                                        :src="img"
                                        :preview-src-list="getEvidenceImages(currentItem.sellerEvidenceImages)"
                                        fit="cover"
                                        class="evidence-image">
                                    </el-image>
                                </div>
                            </div>
                        </div>
                    </el-card>

                    <!-- 处理操作（待处理时显示） -->
                    <el-card v-if="canHandle(currentItem)" shadow="never" class="detail-section">
                        <div slot="header">
                            <span>处理操作</span>
                        </div>
                        <el-form :model="handleForm" label-width="120px">
                            <el-form-item label="处理结果" required>
                                <el-radio-group v-model="handleForm.handleResult">
                                    <el-radio :label="1">同意退款</el-radio>
                                    <el-radio :label="2">驳回申请</el-radio>
                                </el-radio-group>
                            </el-form-item>
                            <el-form-item label="处理说明" v-if="handleForm.handleResult === 2" required>
                                <el-input
                                    type="textarea"
                                    :rows="4"
                                    v-model="handleForm.handleDescription"
                                    placeholder="请填写驳回原因">
                                </el-input>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="submitAfterSaleHandle">提交处理</el-button>
                                <el-button @click="dialogVisible = false">取消</el-button>
                            </el-form-item>
                        </el-form>
                    </el-card>

                    <!-- 处理结果（已处理时显示） -->
                    <el-card v-if="!canHandle(currentItem) && (currentItem.applicationStatus === 5 || currentItem.applicationStatus === 6)" shadow="never" class="detail-section">
                        <div slot="header">
                            <span>处理结果</span>
                        </div>
                        <el-descriptions :column="2" border>
                            <el-descriptions-item label="处理结果">
                                <el-tag :type="currentItem.applicationStatus === 5 ? 'success' : 'danger'" size="small">
                                    {{ currentItem.applicationStatus === 5 ? '已同意退款' : '已驳回' }}
                                </el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item label="处理时间">{{ currentItem.updateTime | formatDate }}</el-descriptions-item>
                            <el-descriptions-item label="处理说明" :span="2">{{ currentItem.sellerRejectReason || '无' }}</el-descriptions-item>
                        </el-descriptions>
                    </el-card>
                </template>

                <!-- 交易纠纷详情 -->
                <template v-else>
                <!-- 订单信息 -->
                <el-card shadow="never" class="detail-section">
                    <div slot="header">
                        <span>订单信息</span>
                    </div>
                    <el-descriptions :column="2" border>
                        <el-descriptions-item label="订单号">{{ currentItem.order && currentItem.order.orderNumber }}</el-descriptions-item>
                        <el-descriptions-item label="订单金额">¥{{ currentItem.order && currentItem.order.orderPrice }}</el-descriptions-item>
                        <el-descriptions-item label="商品名称">{{ currentItem.order && currentItem.order.idleItem && currentItem.order.idleItem.idleName }}</el-descriptions-item>
                        <el-descriptions-item label="订单状态">
                            <el-tag size="small">{{ getOrderStatusText(currentItem.order && currentItem.order.orderStatus) }}</el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item label="支付状态">
                            <el-tag :type="currentItem.order && currentItem.order.paymentStatus ? 'success' : 'warning'" size="small">
                                {{ currentItem.order && currentItem.order.paymentStatus ? '已支付' : '未支付' }}
                            </el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item label="创建时间">{{ currentItem.order && currentItem.order.createTime | formatDate }}</el-descriptions-item>
                    </el-descriptions>
                </el-card>

                <!-- 纠纷信息 -->
                <el-card shadow="never" class="detail-section">
                    <div slot="header">
                        <span>纠纷信息</span>
                    </div>
                    <el-descriptions :column="2" border>
                        <el-descriptions-item label="申请人">{{ currentItem.applicant && currentItem.applicant.nickname }}</el-descriptions-item>
                        <el-descriptions-item label="申请时间">{{ currentItem.createTime | formatDate }}</el-descriptions-item>
                        <el-descriptions-item label="纠纷类型">
                            <el-tag size="small">{{ getDisputeTypeText(currentItem.disputeType) }}</el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item label="纠纷状态">
                            <el-tag :type="getStatusType(currentItem.disputeStatus)" size="small">
                                {{ getStatusText(currentItem.disputeStatus) }}
                            </el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item label="纠纷原因" :span="2">{{ currentItem.disputeReason }}</el-descriptions-item>
                    </el-descriptions>

                    <!-- 证据图片 -->
                    <div v-if="currentItem.evidenceImages" class="evidence-images">
                        <el-divider>证据图片</el-divider>
                        <div class="image-list">
                            <el-image
                                v-for="(img, index) in getEvidenceImages(currentItem.evidenceImages)"
                                :key="index"
                                :src="img"
                                :preview-src-list="getEvidenceImages(currentItem.evidenceImages)"
                                fit="cover"
                                class="evidence-image">
                            </el-image>
                        </div>
                    </div>
                </el-card>

                <!-- 处理结果（已处理时显示） -->
                <el-card v-if="currentItem.disputeStatus === 2" shadow="never" class="detail-section">
                    <div slot="header">
                        <span>处理结果</span>
                    </div>
                    <el-descriptions :column="2" border>
                        <el-descriptions-item label="处理结果">
                            <el-tag type="success" size="small">{{ getHandleResultText(currentItem.handleResult) }}</el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item label="处理时间">{{ currentItem.handleTime | formatDate }}</el-descriptions-item>
                        <el-descriptions-item label="处理说明" :span="2">{{ currentItem.handleDescription || '无' }}</el-descriptions-item>
                    </el-descriptions>
                </el-card>

                <!-- 处理操作（待处理时显示） -->
                <el-card v-if="currentItem.disputeStatus === 0" shadow="never" class="detail-section">
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
                </template>
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
            afterSales: [],
            allItems: [], // 合并后的列表
            loading: false,
            currentPage: 1,
            pageSize: 8,
            total: 0,
            filterStatus: null, // null=全部, 0=待处理, 2=已解决
            filterType: null, // null=全部, 'dispute'=交易纠纷, 'afterSale'=售后申请
            dialogVisible: false,
            currentItem: null, // 当前查看的项目（可能是纠纷或售后）
            handleForm: {
                handleResult: 1,
                handleDescription: ''
            }
        }
    },
    created() {
        this.loadAllData();
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
        loadAllData() {
            this.loading = true;
            // 同时加载交易纠纷和售后申请
            Promise.all([
                this.loadDisputes(),
                this.loadAfterSales()
            ]).then(() => {
                this.mergeItems();
            }).finally(() => {
                this.loading = false;
            });
        },
        loadDisputes() {
            return this.$api.adminGetDisputeList({
                status: this.filterStatus,
                page: this.currentPage,
                nums: this.pageSize
            }).then(res => {
                console.log('加载纠纷列表响应:', res);
                if (res.status_code === 1) {
                    this.disputes = (res.data.list || []).map(item => {
                        item.type = 'dispute';
                        return item;
                    });
                    console.log('纠纷列表:', this.disputes);
                } else {
                    console.warn('加载纠纷列表失败:', res.msg);
                    this.disputes = [];
                }
            }).catch(err => {
                console.error('加载纠纷列表失败', err);
                this.disputes = [];
            });
        },
        loadAfterSales() {
            return this.$api.getAdminPendingAfterSales({}).then(res => {
                console.log('加载售后申请响应:', res);
                if (res.status_code === 1) {
                    this.afterSales = (res.data || []).map(item => {
                        item.type = 'afterSale';
                        return item;
                    });
                    console.log('售后申请列表:', this.afterSales);
                } else {
                    console.warn('加载售后申请失败:', res.msg);
                    this.afterSales = [];
                }
            }).catch(err => {
                console.error('加载售后申请失败', err);
                this.afterSales = [];
            });
        },
        mergeItems() {
            let items = [];
            
            console.log('合并数据 - 纠纷数量:', this.disputes.length, '售后数量:', this.afterSales.length);
            console.log('当前筛选条件 - 类型:', this.filterType, '状态:', this.filterStatus);
            
            // 添加交易纠纷
            if (!this.filterType || this.filterType === 'dispute') {
                items = items.concat(this.disputes);
            }
            
            // 添加售后申请
            if (!this.filterType || this.filterType === 'afterSale') {
                items = items.concat(this.afterSales);
            }
            
            console.log('合并后数量（筛选前）:', items.length);
            
            // 按状态筛选
            if (this.filterStatus !== null) {
                if (this.filterStatus === 0) {
                    // 待处理：纠纷状态为0，售后状态为3或4
                    items = items.filter(item => {
                        if (item.type === 'dispute') {
                            return item.disputeStatus === 0;
                        } else {
                            return item.applicationStatus === 3 || item.applicationStatus === 4;
                        }
                    });
                } else if (this.filterStatus === 2) {
                    // 已解决：纠纷状态为2，售后状态为5或6
                    items = items.filter(item => {
                        if (item.type === 'dispute') {
                            return item.disputeStatus === 2;
                        } else {
                            return item.applicationStatus === 5 || item.applicationStatus === 6;
                        }
                    });
                }
            }
            
            console.log('筛选后数量:', items.length);
            
            // 按时间排序
            items.sort((a, b) => {
                if (!a.createTime || !b.createTime) return 0;
                return new Date(b.createTime) - new Date(a.createTime);
            });
            
            this.allItems = items;
            this.total = items.length;
            
            console.log('最终列表数量:', this.allItems.length);
        },
        handleStatusChange() {
            this.currentPage = 1;
            this.mergeItems();
        },
        handleTypeChange() {
            this.currentPage = 1;
            this.mergeItems();
        },
        handlePageChange(page) {
            this.currentPage = page;
            this.loadAllData();
        },
        viewDetails(item) {
            // 如果是售后申请，需要获取完整详情（包含订单、买家、卖家信息）
            if (item.type === 'afterSale') {
                this.loading = true;
                this.$api.getAdminAfterSaleDetail({ id: item.id }).then(res => {
                    if (res.status_code === 1 && res.data) {
                        res.data.type = 'afterSale';
                        this.currentItem = res.data;
                        this.handleForm.handleResult = 1;
                        this.handleForm.handleDescription = '';
                        this.dialogVisible = true;
                    } else {
                        this.$message.error('获取售后申请详情失败');
                    }
                }).catch(err => {
                    console.error('获取售后申请详情失败', err);
                    this.$message.error('获取售后申请详情失败');
                }).finally(() => {
                    this.loading = false;
                });
            } else {
                // 交易纠纷直接使用列表数据
                this.currentItem = item;
                this.handleForm.handleResult = 1;
                this.handleForm.handleDescription = '';
                this.dialogVisible = true;
            }
        },
        canHandle(item) {
            if (item.type === 'dispute') {
                return item.disputeStatus === 0;
            } else {
                return item.applicationStatus === 3 || item.applicationStatus === 4;
            }
        },
        getItemStatusText(item) {
            if (item.type === 'afterSale') {
                const statuses = {
                    0: '待卖家审核',
                    1: '卖家已同意',
                    2: '卖家已拒绝',
                    3: '待管理员审核（卖家同意）',
                    4: '待管理员审核（卖家拒绝）',
                    5: '管理员已同意退款',
                    6: '管理员已驳回'
                };
                return statuses[item.applicationStatus] || '未知状态';
            } else {
                return this.getStatusText(item.disputeStatus);
            }
        },
        getItemStatusType(item) {
            if (item.type === 'afterSale') {
                if (item.applicationStatus === 3 || item.applicationStatus === 4) return 'warning';
                if (item.applicationStatus === 5) return 'success';
                if (item.applicationStatus === 6) return 'danger';
                return 'info';
            } else {
                return this.getStatusType(item.disputeStatus);
            }
        },
        getApplicationTypeText(type) {
            const types = {
                1: '质量问题',
                2: '描述不符',
                3: '未收到货',
                4: '其他'
            };
            return types[type] || '未知';
        },
        getDialogTitle() {
            if (!this.currentItem) return '详情';
            if (this.currentItem.type === 'afterSale') {
                return this.canHandle(this.currentItem) ? '处理售后申请' : '查看售后申请详情';
            } else {
                return this.currentItem.disputeStatus === 0 ? '处理交易纠纷' : '查看纠纷详情';
            }
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
                    id: this.currentItem.id,
                    handleResult: this.handleForm.handleResult,
                    handleDescription: this.handleForm.handleDescription
                }).then(res => {
                    if (res.status_code === 1) {
                        this.$message.success('处理成功');
                        this.dialogVisible = false;
                        this.loadAllData();
                    } else {
                        this.$message.error(res.msg || '处理失败');
                    }
                }).catch(err => {
                    console.error('处理纠纷失败', err);
                    this.$message.error('处理失败');
                });
            }).catch(() => {});
        },
        submitAfterSaleHandle() {
            if (!this.handleForm.handleResult) {
                this.$message.warning('请选择处理结果');
                return;
            }
            
            if (this.handleForm.handleResult === 2 && !this.handleForm.handleDescription) {
                this.$message.warning('驳回申请时必须填写驳回原因');
                return;
            }
            
            this.$confirm('确认提交处理结果？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$api.adminReviewAfterSale({
                    id: this.currentItem.id,
                    reviewResult: this.handleForm.handleResult,
                    reason: this.handleForm.handleDescription
                }).then(res => {
                    if (res.status_code === 1) {
                        this.$message.success('处理成功');
                        this.dialogVisible = false;
                        this.loadAllData();
                    } else {
                        this.$message.error(res.msg || '处理失败');
                    }
                }).catch(err => {
                    console.error('处理售后申请失败', err);
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

