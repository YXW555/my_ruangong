<template>
    <div>
        <app-head></app-head>
        <app-body>
            <div class="membership-container">
                <el-card class="membership-header-card" shadow="hover">
                    <div class="header-content">
                        <h1 class="page-title">开通会员，享受更多特权</h1>
                        <p class="page-subtitle">提升您的交易体验，让闲置物品更快找到新主人</p>
                    </div>
                </el-card>

                <!-- 会员状态卡片 -->
                <el-card class="status-card" shadow="hover" v-if="membershipStatus">
                    <div class="status-content">
                        <div class="status-info">
                            <el-tag :type="getStatusTagType()" size="large">{{ getStatusText() }}</el-tag>
                            <div class="status-details" v-if="membershipStatus.isValid">
                                <p>到期时间：{{ formatDate(membershipStatus.expireTime) }}</p>
                                <p>本月已发布：{{ membershipStatus.publishedCount }} / {{ getPublishLimit() }}</p>
                                <p>当前置顶：{{ membershipStatus.activePinCount }} / {{ getPinLimit() }}</p>
                            </div>
                        </div>
                    </div>
                </el-card>

                <!-- 会员套餐选择 -->
                <div class="membership-plans">
                    <el-row :gutter="20">
                        <!-- 基础会员 -->
                        <el-col :xs="24" :sm="12">
                            <el-card class="plan-card" :class="{'recommended': false}" shadow="hover">
                                <div class="plan-header">
                                    <h2>基础会员</h2>
                                    <div class="plan-price">
                                        <span class="price-main">¥9.9</span>
                                        <span class="price-unit">/月</span>
                                    </div>
                                    <div class="plan-year-price">年付仅需 ¥99，省 ¥19.8</div>
                                </div>
                                <ul class="plan-features">
                                    <li><i class="el-icon-check"></i> 商品置顶：每月 5 个</li>
                                    <li><i class="el-icon-check"></i> 发布数量：提升至 20 个/月</li>
                                    <li><i class="el-icon-check"></i> 图片数量：6 张/商品</li>
                                    <li><i class="el-icon-check"></i> 优先展示：搜索结果靠前</li>
                                    <li><i class="el-icon-check"></i> 会员标识：商品显示会员徽章</li>
                                </ul>
                                <el-button type="primary" size="medium" @click="purchaseMembership(1)" 
                                          :disabled="membershipStatus && membershipStatus.membershipType === 1">
                                    {{ membershipStatus && membershipStatus.membershipType === 1 ? '已是基础会员' : '立即开通' }}
                                </el-button>
                            </el-card>
                        </el-col>

                        <!-- 高级会员 -->
                        <el-col :xs="24" :sm="12">
                            <el-card class="plan-card recommended" shadow="hover">
                                <div class="recommended-badge">推荐</div>
                                <div class="plan-header">
                                    <h2>高级会员</h2>
                                    <div class="plan-price">
                                        <span class="price-main">¥19.9</span>
                                        <span class="price-unit">/月</span>
                                    </div>
                                    <div class="plan-year-price">年付仅需 ¥199，省 ¥39.8</div>
                                </div>
                                <ul class="plan-features">
                                    <li><i class="el-icon-check"></i> 无限发布：不限制商品发布数量</li>
                                    <li><i class="el-icon-check"></i> 高级置顶：每月 15 个</li>
                                    <li><i class="el-icon-check"></i> 批量管理工具</li>
                                    <li><i class="el-icon-check"></i> 数据分析面板</li>
                                    <li><i class="el-icon-check"></i> 专属推广链接</li>
                                    <li><i class="el-icon-check"></i> 交易手续费减免 30%</li>
                                </ul>
                                <el-button type="success" size="medium" @click="purchaseMembership(2)"
                                          :disabled="membershipStatus && membershipStatus.membershipType === 2">
                                    {{ membershipStatus && membershipStatus.membershipType === 2 ? '已是高级会员' : '立即开通' }}
                                </el-button>
                            </el-card>
                        </el-col>
                    </el-row>
                </div>

                <!-- 购买时长选择对话框 -->
                <el-dialog
                    title="选择购买时长"
                    :visible.sync="durationDialogVisible"
                    width="400px"
                    @close="selectedDuration = 1">
                    <div class="duration-options">
                        <el-radio-group v-model="selectedDuration">
                            <el-radio :label="1">1个月</el-radio>
                            <el-radio :label="3">3个月</el-radio>
                            <el-radio :label="6">6个月</el-radio>
                            <el-radio :label="12">12个月（最优惠）</el-radio>
                        </el-radio-group>
                        <div class="duration-price">
                            <p>应付金额：<span class="price">¥{{ calculatePrice() }}</span></p>
                        </div>
                    </div>
                    <span slot="footer" class="dialog-footer">
                        <el-button @click="durationDialogVisible = false">取消</el-button>
                        <el-button type="primary" @click="confirmPurchase">确认购买</el-button>
                    </span>
                </el-dialog>

                <!-- 订单历史 -->
                <el-card class="orders-card" shadow="hover" v-if="orders.length > 0">
                    <div slot="header">
                        <span>我的会员订单</span>
                    </div>
                    <el-table :data="orders" style="width: 100%">
                        <el-table-column prop="orderNumber" label="订单号" width="200"></el-table-column>
                        <el-table-column prop="membershipType" label="会员类型" width="120">
                            <template slot-scope="scope">
                                <el-tag :type="scope.row.membershipType === 2 ? 'success' : 'primary'">
                                    {{ scope.row.membershipType === 2 ? '高级会员' : '基础会员' }}
                                </el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="durationMonths" label="时长" width="100">
                            <template slot-scope="scope">{{ scope.row.durationMonths }}个月</template>
                        </el-table-column>
                        <el-table-column prop="amount" label="金额" width="120">
                            <template slot-scope="scope">¥{{ scope.row.amount }}</template>
                        </el-table-column>
                        <el-table-column prop="paymentStatus" label="状态" width="100">
                            <template slot-scope="scope">
                                <el-tag :type="scope.row.paymentStatus === 1 ? 'success' : 'warning'">
                                    {{ scope.row.paymentStatus === 1 ? '已支付' : '待支付' }}
                                </el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="createTime" label="创建时间" width="180">
                            <template slot-scope="scope">{{ formatDate(scope.row.createTime) }}</template>
                        </el-table-column>
                    </el-table>
                </el-card>
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
    name: "membership",
    components: {
        AppHead,
        AppBody,
        AppFoot
    },
    data() {
        return {
            membershipStatus: null,
            orders: [],
            durationDialogVisible: false,
            selectedMembershipType: 1,
            selectedDuration: 1
        };
    },
    created() {
        this.loadMembershipStatus();
        this.loadOrders();
    },
    methods: {
        loadMembershipStatus() {
            this.$api.getMembershipStatus().then(res => {
                if (res.status_code === 1) {
                    this.membershipStatus = res.data;
                }
            }).catch(() => {
            });
        },
        loadOrders() {
            this.$api.getMembershipOrders().then(res => {
                if (res.status_code === 1) {
                    this.orders = res.data;
                }
            }).catch(() => {
            });
        },
        purchaseMembership(type) {
            this.selectedMembershipType = type;
            this.selectedDuration = 1;
            this.durationDialogVisible = true;
        },
        calculatePrice() {
            if (this.selectedMembershipType === 1) {
                return this.selectedDuration >= 12 ? '99.00' : (9.9 * this.selectedDuration).toFixed(2);
            } else {
                return this.selectedDuration >= 12 ? '199.00' : (19.9 * this.selectedDuration).toFixed(2);
            }
        },
        confirmPurchase() {
            this.$api.createMembershipOrder({
                membershipType: this.selectedMembershipType,
                durationMonths: this.selectedDuration
            }).then(res => {
                if (res.status_code === 1) {
                    this.durationDialogVisible = false;
                    // 跳转到支付页面
                    this.$router.push({
                        path: '/alipay/pay',
                        query: {
                            orderNumber: res.data.orderNumber,
                            amount: res.data.amount,
                            type: 'membership'
                        }
                    });
                } else {
                    this.$message.error(res.msg || '创建订单失败');
                }
            }).catch(() => {
                this.$message.error('网络错误，请稍后重试');
            });
        },
        getStatusText() {
            if (!this.membershipStatus || !this.membershipStatus.isValid) {
                return '普通用户';
            }
            return this.membershipStatus.membershipType === 2 ? '高级会员' : '基础会员';
        },
        getStatusTagType() {
            if (!this.membershipStatus || !this.membershipStatus.isValid) {
                return 'info';
            }
            return this.membershipStatus.membershipType === 2 ? 'success' : 'primary';
        },
        getPublishLimit() {
            if (!this.membershipStatus) return 10;
            if (this.membershipStatus.membershipType === 2) return '∞';
            return this.membershipStatus.membershipType === 1 ? 20 : 10;
        },
        getPinLimit() {
            if (!this.membershipStatus) return 0;
            if (this.membershipStatus.membershipType === 0) return 0;
            return this.membershipStatus.membershipType === 2 ? 15 : 5;
        },
        formatDate(date) {
            if (!date) return '';
            return new Date(date).toLocaleString('zh-CN');
        }
    }
}
</script>

<style scoped>
.membership-container {
    padding: 20px;
    max-width: 1200px;
    margin: 0 auto;
}

.membership-header-card {
    margin-bottom: 30px;
    text-align: center;
}

.header-content {
    padding: 30px 0;
}

.page-title {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 10px;
}

.page-subtitle {
    font-size: 16px;
    color: #909399;
}

.status-card {
    margin-bottom: 30px;
}

.status-content {
    padding: 20px;
}

.status-info {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.status-details p {
    margin: 5px 0;
    color: #606266;
}

.membership-plans {
    margin-bottom: 30px;
}

.plan-card {
    position: relative;
    height: 100%;
    transition: all 0.3s;
}

.plan-card:hover {
    transform: translateY(-5px);
}

.plan-card.recommended {
    border: 2px solid #67c23a;
}

.recommended-badge {
    position: absolute;
    top: -10px;
    right: 20px;
    background: #67c23a;
    color: white;
    padding: 5px 15px;
    border-radius: 15px;
    font-size: 12px;
    font-weight: 600;
}

.plan-header {
    text-align: center;
    padding: 20px 0;
    border-bottom: 1px solid #ebeef5;
    margin-bottom: 20px;
}

.plan-header h2 {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 15px;
}

.plan-price {
    margin: 15px 0;
}

.price-main {
    font-size: 36px;
    font-weight: 700;
    color: #409EFF;
}

.price-unit {
    font-size: 16px;
    color: #909399;
    margin-left: 5px;
}

.plan-year-price {
    font-size: 14px;
    color: #67c23a;
    margin-top: 10px;
}

.plan-features {
    list-style: none;
    padding: 0;
    margin: 20px 0;
    min-height: 200px;
}

.plan-features li {
    padding: 12px 0;
    color: #606266;
    font-size: 15px;
}

.plan-features li i {
    color: #67c23a;
    margin-right: 10px;
    font-weight: 600;
}

.plan-card .el-button {
    width: 100%;
    margin-top: 20px;
    padding: 12px 0;
    font-size: 16px;
}

.duration-options {
    padding: 20px 0;
}

.duration-options .el-radio-group {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.duration-price {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;
    text-align: center;
}

.duration-price .price {
    font-size: 24px;
    font-weight: 700;
    color: #f56c6c;
}

.orders-card {
    margin-top: 30px;
}
</style>

