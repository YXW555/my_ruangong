<template>
    <div>
        <app-head></app-head>
        <app-body>
            <div class="merchant-manage-container">
                <el-card class="manage-card" shadow="hover">
                    <div slot="header" class="card-header">
                        <span><i class="el-icon-s-shop"></i> 店铺管理</span>
                    </div>

                    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
                        <!-- 数据统计 -->
                        <el-tab-pane label="数据统计" name="statistics">
                            <div class="statistics-container" v-if="statistics">
                                <el-row :gutter="20">
                                    <el-col :xs="24" :sm="12" :md="6">
                                        <el-card class="stat-card">
                                            <div class="stat-item">
                                                <div class="stat-value">{{ statistics.totalItems || 0 }}</div>
                                                <div class="stat-label">商品总数</div>
                                            </div>
                                        </el-card>
                                    </el-col>
                                    <el-col :xs="24" :sm="12" :md="6">
                                        <el-card class="stat-card">
                                            <div class="stat-item">
                                                <div class="stat-value">{{ statistics.onSaleItems || 0 }}</div>
                                                <div class="stat-label">在售商品</div>
                                            </div>
                                        </el-card>
                                    </el-col>
                                    <el-col :xs="24" :sm="12" :md="6">
                                        <el-card class="stat-card">
                                            <div class="stat-item">
                                                <div class="stat-value">{{ statistics.totalStock || 0 }}</div>
                                                <div class="stat-label">总库存</div>
                                            </div>
                                        </el-card>
                                    </el-col>
                                    <el-col :xs="24" :sm="12" :md="6">
                                        <el-card class="stat-card">
                                            <div class="stat-item">
                                                <div class="stat-value">{{ statistics.totalOrders || 0 }}</div>
                                                <div class="stat-label">订单总数</div>
                                            </div>
                                        </el-card>
                                    </el-col>
                                </el-row>
                                <el-row :gutter="20" style="margin-top: 20px;">
                                    <el-col :xs="24" :sm="12" :md="8">
                                        <el-card class="stat-card revenue">
                                            <div class="stat-item">
                                                <div class="stat-value">¥{{ (statistics.totalRevenue || 0).toFixed(2) }}</div>
                                                <div class="stat-label">已完成订单金额</div>
                                            </div>
                                        </el-card>
                                    </el-col>
                                    <el-col :xs="24" :sm="12" :md="8">
                                        <el-card class="stat-card pending">
                                            <div class="stat-item">
                                                <div class="stat-value">¥{{ (statistics.totalPendingRevenue || 0).toFixed(2) }}</div>
                                                <div class="stat-label">待完成订单金额</div>
                                            </div>
                                        </el-card>
                                    </el-col>
                                    <el-col :xs="24" :sm="12" :md="8">
                                        <el-card class="stat-card completed">
                                            <div class="stat-item">
                                                <div class="stat-value">{{ statistics.completedOrders || 0 }}</div>
                                                <div class="stat-label">已完成订单</div>
                                            </div>
                                        </el-card>
                                    </el-col>
                                </el-row>
                            </div>
                        </el-tab-pane>

                        <!-- 库存管理 -->
                        <el-tab-pane label="库存管理" name="stock">
                            <div class="stock-manage-container">
                                <el-table
                                    :data="shopItems"
                                    stripe
                                    border
                                    style="width: 100%">
                                    <el-table-column prop="idleName" label="商品名称" width="200"></el-table-column>
                                    <el-table-column prop="idlePrice" label="价格" width="100">
                                        <template slot-scope="scope">
                                            ¥{{ scope.row.idlePrice }}
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="stock" label="库存" width="150">
                                        <template slot-scope="scope">
                                            <el-input-number
                                                v-model="scope.row.stock"
                                                :min="0"
                                                :max="9999"
                                                size="small"
                                                @change="updateStock(scope.row)">
                                            </el-input-number>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="idleStatus" label="状态" width="100">
                                        <template slot-scope="scope">
                                            <el-tag :type="scope.row.idleStatus === 1 ? 'success' : 'info'">
                                                {{ scope.row.idleStatus === 1 ? '在售' : '已下架' }}
                                            </el-tag>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="releaseTime" label="发布时间" width="180">
                                        <template slot-scope="scope">
                                            {{ scope.row.releaseTime ? scope.row.releaseTime.substring(0, 16) : '' }}
                                        </template>
                                    </el-table-column>
                                    <el-table-column label="操作" width="150">
                                        <template slot-scope="scope">
                                            <el-button
                                                size="mini"
                                                :type="scope.row.idleStatus === 1 ? 'warning' : 'success'"
                                                @click="toggleStatus(scope.row)">
                                                {{ scope.row.idleStatus === 1 ? '下架' : '上架' }}
                                            </el-button>
                                        </template>
                                    </el-table-column>
                                </el-table>
                            </div>
                        </el-tab-pane>

                        <!-- 订单管理 -->
                        <el-tab-pane label="订单管理" name="orders">
                            <div class="orders-manage-container">
                                <el-table
                                    :data="shopOrders"
                                    stripe
                                    border
                                    style="width: 100%">
                                    <el-table-column prop="orderNumber" label="订单编号" width="180"></el-table-column>
                                    <el-table-column prop="idleItem.idleName" label="商品名称" width="200"></el-table-column>
                                    <el-table-column prop="orderPrice" label="订单金额" width="100">
                                        <template slot-scope="scope">
                                            ¥{{ scope.row.orderPrice }}
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="orderStatus" label="订单状态" width="120">
                                        <template slot-scope="scope">
                                            <el-tag :type="getOrderStatusType(scope.row.orderStatus)">
                                                {{ getOrderStatusText(scope.row.orderStatus) }}
                                            </el-tag>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="createTime" label="创建时间" width="180">
                                        <template slot-scope="scope">
                                            {{ scope.row.createTime ? scope.row.createTime.substring(0, 16) : '' }}
                                        </template>
                                    </el-table-column>
                                    <el-table-column label="操作" width="150">
                                        <template slot-scope="scope">
                                            <el-button
                                                v-if="scope.row.orderStatus === 1"
                                                size="mini"
                                                type="primary"
                                                @click="shipOrder(scope.row)">
                                                发货
                                            </el-button>
                                            <el-button
                                                size="mini"
                                                type="info"
                                                @click="viewOrder(scope.row)">
                                                查看
                                            </el-button>
                                        </template>
                                    </el-table-column>
                                </el-table>
                            </div>
                        </el-tab-pane>

                        <!-- 批量发布 -->
                        <el-tab-pane label="批量发布" name="batch">
                            <div class="batch-publish-container">
                                <el-form :model="batchForm" label-width="100px" class="batch-form">
                                    <el-form-item label="商品列表">
                                        <el-button type="primary" icon="el-icon-plus" @click="addBatchItem">添加商品</el-button>
                                    </el-form-item>
                                    <div v-for="(item, index) in batchForm.items" :key="index" class="batch-item-card">
                                        <el-card shadow="hover">
                                            <div slot="header" class="batch-item-header">
                                                <span>商品 {{ index + 1 }}</span>
                                                <el-button type="danger" size="mini" icon="el-icon-delete" @click="removeBatchItem(index)">删除</el-button>
                                            </div>
                                            <el-form :model="item" label-width="100px">
                                                <el-row :gutter="20">
                                                    <el-col :span="12">
                                                        <el-form-item label="商品名称" required>
                                                            <el-input v-model="item.idleName" placeholder="请输入商品名称"></el-input>
                                                        </el-form-item>
                                                    </el-col>
                                                    <el-col :span="12">
                                                        <el-form-item label="价格" required>
                                                            <el-input-number v-model="item.idlePrice" :min="0" :precision="2" style="width: 100%"></el-input-number>
                                                        </el-form-item>
                                                    </el-col>
                                                </el-row>
                                                <el-row :gutter="20">
                                                    <el-col :span="12">
                                                        <el-form-item label="分类" required>
                                                            <el-select v-model="item.idleLabel" placeholder="请选择分类" style="width: 100%">
                                                                <el-option label="数码科技" :value="1"></el-option>
                                                                <el-option label="生活用品" :value="2"></el-option>
                                                                <el-option label="运动相关" :value="3"></el-option>
                                                                <el-option label="图书笔记" :value="4"></el-option>
                                                            </el-select>
                                                        </el-form-item>
                                                    </el-col>
                                                    <el-col :span="12">
                                                        <el-form-item label="库存" required>
                                                            <el-input-number v-model="item.stock" :min="1" style="width: 100%"></el-input-number>
                                                        </el-form-item>
                                                    </el-col>
                                                </el-row>
                                                <el-form-item label="发货地区">
                                                    <el-input v-model="item.idlePlace" placeholder="请输入发货地区"></el-input>
                                                </el-form-item>
                                                <el-form-item label="商品详情">
                                                    <el-input
                                                        v-model="item.idleDetails"
                                                        type="textarea"
                                                        :rows="3"
                                                        placeholder="请输入商品详情"></el-input>
                                                </el-form-item>
                                                <el-form-item label="商品图片">
                                                    <el-upload
                                                        action="/file/"
                                                        :on-success="(res) => handleBatchImageSuccess(res, index)"
                                                        :file-list="item.pictureList || []"
                                                        list-type="picture-card"
                                                        :limit="5"
                                                        accept="image/*">
                                                        <i class="el-icon-plus"></i>
                                                    </el-upload>
                                                </el-form-item>
                                            </el-form>
                                        </el-card>
                                    </div>
                                    <el-form-item>
                                        <el-button type="primary" icon="el-icon-upload" @click="batchPublish" :loading="publishing" :disabled="batchForm.items.length === 0">
                                            批量发布
                                        </el-button>
                                        <el-button @click="resetBatchForm">重置</el-button>
                                    </el-form-item>
                                </el-form>
                            </div>
                        </el-tab-pane>
                    </el-tabs>
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
        name: "merchant-manage",
        components: {
            AppHead,
            AppBody,
            AppFoot
        },
        data() {
            return {
                activeTab: 'statistics',
                statistics: null,
                shopItems: [],
                shopOrders: [],
                batchForm: {
                    items: []
                },
                publishing: false
            };
        },
        created() {
            this.loadStatistics();
        },
        methods: {
            handleTabClick(tab) {
                if (tab.name === 'statistics') {
                    this.loadStatistics();
                } else if (tab.name === 'stock') {
                    this.loadShopItems();
                } else if (tab.name === 'orders') {
                    this.loadShopOrders();
                }
            },
            loadStatistics() {
                this.$api.getShopStatistics().then(res => {
                    if (res.status_code === 1 && res.data) {
                        this.statistics = res.data;
                    }
                }).catch(() => {
                });
            },
            loadShopItems() {
                this.$api.getShopItems().then(res => {
                    if (res.status_code === 1 && res.data) {
                        this.shopItems = res.data;
                        // 处理图片列表
                        for (let item of this.shopItems) {
                            if (item.pictureList) {
                                try {
                                    item.pictureList = JSON.parse(item.pictureList);
                                } catch (e) {
                                    item.pictureList = [];
                                }
                            }
                        }
                    }
                }).catch(() => {
                });
            },
            loadShopOrders() {
                this.$api.getShopOrders().then(res => {
                    if (res.status_code === 1 && res.data) {
                        this.shopOrders = res.data;
                    }
                }).catch(() => {
                });
            },
            updateStock(item) {
                this.$api.updateStock({
                    idleId: item.id,
                    stock: item.stock
                }).then(res => {
                    if (res.status_code === 1) {
                        this.$message.success('库存更新成功');
                        this.loadStatistics();
                    } else {
                        this.$message.error('库存更新失败');
                    }
                }).catch(() => {
                    this.$message.error('库存更新失败');
                });
            },
            toggleStatus(item) {
                const newStatus = item.idleStatus === 1 ? 2 : 1;
                this.$api.updateIdleItem({
                    id: item.id,
                    idleStatus: newStatus
                }).then(res => {
                    if (res.status_code === 1) {
                        this.$message.success('操作成功');
                        item.idleStatus = newStatus;
                        this.loadStatistics();
                    } else {
                        this.$message.error('操作失败');
                    }
                }).catch(() => {
                    this.$message.error('操作失败');
                });
            },
            shipOrder(order) {
                this.$api.updateOrder({
                    id: order.id,
                    orderStatus: 2
                }).then(res => {
                    if (res.status_code === 1) {
                        this.$message.success('发货成功');
                        order.orderStatus = 2;
                        this.loadStatistics();
                    } else {
                        this.$message.error('发货失败');
                    }
                }).catch(() => {
                    this.$message.error('发货失败');
                });
            },
            viewOrder(order) {
                this.$router.push({
                    path: '/order',
                    query: { id: order.id }
                });
            },
            getOrderStatusType(status) {
                const types = ['warning', 'primary', 'info', 'success', 'danger'];
                return types[status] || 'info';
            },
            getOrderStatusText(status) {
                const texts = ['待付款', '待发货', '待收货', '已完成', '已取消'];
                return texts[status] || '未知';
            },
            addBatchItem() {
                this.batchForm.items.push({
                    idleName: '',
                    idleDetails: '',
                    idlePrice: 0,
                    idlePlace: '',
                    idleLabel: null,
                    stock: 1,
                    pictureList: []
                });
            },
            removeBatchItem(index) {
                this.batchForm.items.splice(index, 1);
            },
            handleBatchImageSuccess(res, index) {
                if (res.status_code === 1) {
                    if (!this.batchForm.items[index].pictureList) {
                        this.batchForm.items[index].pictureList = [];
                    }
                    this.batchForm.items[index].pictureList.push(res.data);
                }
            },
            batchPublish() {
                if (this.batchForm.items.length === 0) {
                    this.$message.warning('请至少添加一个商品');
                    return;
                }

                // 验证必填字段
                for (let i = 0; i < this.batchForm.items.length; i++) {
                    const item = this.batchForm.items[i];
                    if (!item.idleName || !item.idlePrice || !item.idleLabel) {
                        this.$message.error(`商品 ${i + 1} 的必填信息不完整`);
                        return;
                    }
                }

                // 转换图片列表为JSON字符串
                const itemsToSubmit = this.batchForm.items.map(item => ({
                    ...item,
                    pictureList: JSON.stringify(item.pictureList || [])
                }));

                this.publishing = true;
                this.$api.batchAddItems(itemsToSubmit).then(res => {
                    if (res.status_code === 1) {
                        this.$message.success('批量发布成功！');
                        this.resetBatchForm();
                        this.loadStatistics();
                    } else {
                        this.$message.error('批量发布失败：' + (res.msg || '未知错误'));
                    }
                }).catch(() => {
                    this.$message.error('批量发布失败！');
                }).finally(() => {
                    this.publishing = false;
                });
            },
            resetBatchForm() {
                this.batchForm.items = [];
            }
        }
    }
</script>

<style scoped>
    .merchant-manage-container {
        min-height: 85vh;
        padding: 20px;
    }

    .manage-card {
        max-width: 1200px;
        margin: 0 auto;
    }

    .card-header {
        font-size: 18px;
        font-weight: 600;
    }

    .statistics-container {
        padding: 20px 0;
    }

    .stat-card {
        text-align: center;
        margin-bottom: 20px;
    }

    .stat-item {
        padding: 10px;
    }

    .stat-value {
        font-size: 32px;
        font-weight: 700;
        color: #409EFF;
        margin-bottom: 10px;
    }

    .stat-card.revenue .stat-value {
        color: #67C23A;
    }

    .stat-card.pending .stat-value {
        color: #E6A23C;
    }

    .stat-card.completed .stat-value {
        color: #909399;
    }

    .stat-label {
        font-size: 14px;
        color: #909399;
    }

    .stock-manage-container,
    .orders-manage-container,
    .batch-publish-container {
        padding: 20px 0;
    }

    .batch-item-card {
        margin-bottom: 20px;
    }

    .batch-item-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
</style>

