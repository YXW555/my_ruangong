<template>
    <div>
        <app-head></app-head>
        <app-body>
            <div class="order-container" v-loading="isLoading">
                <el-card class="order-card" shadow="hover" v-if="orderInfo">
                    <div slot="header" class="card-header">
                        <h2 class="order-title">
                            <i class="el-icon-shopping-bag-2"></i> 订单详情
                        </h2>
                </div>
                
                    <!-- 订单信息 -->
                    <div class="order-info-section">
                        <div class="info-row">
                            <span class="info-label">订单编号：</span>
                            <span class="info-value">{{ orderInfo.orderNumber }}</span>
                            </div>
                        <div class="info-row">
                            <span class="info-label">订单状态：</span>
                            <el-tag :type="getStatusType(orderInfo.orderStatus)" size="medium">
                                {{ getStatusText(orderInfo.orderStatus) }}
                        </el-tag>
                            </div>
                        <div class="info-row">
                            <span class="info-label">支付状态：</span>
                            <el-tag :type="orderInfo.paymentStatus === 1 ? 'success' : 'warning'" size="medium">
                                {{ orderInfo.paymentStatus === 1 ? '已支付' : '待支付' }}
                            </el-tag>
                        </div>
                        <!-- 资金状态（卖家视角，订单已完成时显示） -->
                        <div class="info-row" v-if="isSeller && orderInfo.orderStatus === 3 && orderInfo.paymentStatus === 1">
                            <span class="info-label">资金状态：</span>
                            <el-tag :type="getFundStatusType(orderInfo.fundStatus)" size="medium">
                                {{ getFundStatusText(orderInfo.fundStatus) }}
                            </el-tag>
                            <span v-if="orderInfo.fundStatus === 0 && orderInfo.finishTime" class="fund-hint">
                                （预计 {{ getFundReleaseDate(orderInfo.finishTime) }} 释放）
                            </span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">订单金额：</span>
                            <span class="info-value price">¥{{ orderInfo.orderPrice }}</span>
                        </div>
                        <div class="info-row" v-if="orderInfo.createTime">
                            <span class="info-label">创建时间：</span>
                            <span class="info-value">{{ formatTime(orderInfo.createTime) }}</span>
                        </div>
                        <div class="info-row" v-if="orderInfo.finishTime">
                            <span class="info-label">完成时间：</span>
                            <span class="info-value">{{ formatTime(orderInfo.finishTime) }}</span>
                    </div>
                </div>
                
                    <!-- 商品信息 -->
                    <el-divider></el-divider>
                    <div class="product-info-section" v-if="orderInfo.idleItem">
                        <h3 class="section-title">商品信息</h3>
                        <div class="product-card">
                        <el-image
                                v-if="productImage"
                                :src="productImage"
                                fit="cover"
                                class="product-image">
                        </el-image>
                            <div class="product-details">
                                <h4 class="product-name">{{ orderInfo.idleItem.idleName }}</h4>
                                <div class="product-price-info">
                                    <span class="product-price">¥{{ orderInfo.idleItem.idlePrice }}</span>
                            </div>
                        </div>
                    </div>
                    </div>

                    <!-- 卖家信息（买家视角） -->
                    <el-divider></el-divider>
                    <div class="seller-info-section" v-if="orderInfo.idleItem && isBuyer">
                        <h3 class="section-title">卖家信息</h3>
                        <div class="seller-card">
                            <el-avatar 
                                v-if="sellerAvatar"
                                :src="sellerAvatar" 
                                :size="50"
                                class="seller-avatar">
                            </el-avatar>
                            <el-avatar 
                                v-else
                                :size="50"
                                icon="el-icon-user-solid"
                                class="seller-avatar">
                            </el-avatar>
                            <div class="seller-details">
                                <div class="seller-name">
                                    <span class="seller-label">卖家：</span>
                                    <span class="seller-value">{{ sellerName || '未知卖家' }}</span>
                                </div>
                                <div class="seller-actions">
                        <el-button 
                                        type="primary" 
                                        size="small"
                                        icon="el-icon-chat-line-round"
                                        @click="goToChat">
                                        联系卖家
                        </el-button>
                    </div>
                            </div>
                        </div>
                        </div>

                    <!-- 买家信息（卖家视角） -->
                    <el-divider></el-divider>
                    <div class="buyer-info-section" v-if="orderInfo.user && isSeller">
                        <h3 class="section-title">买家信息</h3>
                        <div class="buyer-card">
                            <el-avatar 
                                v-if="buyerAvatar"
                                :src="buyerAvatar" 
                                :size="50"
                                class="buyer-avatar">
                            </el-avatar>
                            <el-avatar 
                                v-else
                                :size="50"
                                icon="el-icon-user-solid"
                                class="buyer-avatar">
                            </el-avatar>
                            <div class="buyer-details">
                                <div class="buyer-name">
                                    <span class="buyer-label">买家：</span>
                                    <span class="buyer-value">{{ buyerName || '未知买家' }}</span>
                                </div>
                                <div class="buyer-actions">
                                <el-button
                                        type="primary"
                                        size="small"
                                        icon="el-icon-chat-line-round"
                                        @click="goToChatWithBuyer">
                                        联系买家
                                </el-button>
                    </div>
                    </div>
                                    </div>
                                    </div>

                    <!-- 交易地点导航（待发货/待收货时显示） -->
                    <el-divider></el-divider>
                    <div class="location-navigation-section" v-if="showLocationNavigation">
                        <h3 class="section-title">
                            <i class="el-icon-location-outline"></i> 交易地点导航
                        </h3>
                        <div class="location-info-card">
                            <div class="location-info-item full-width">
                                <span class="location-label">卖家地址：</span>
                                <span class="location-value" v-if="sellerLocation">{{ sellerLocation.formattedAddress || sellerLocation.address }}</span>
                                <span class="location-value text-warning" v-else>{{ orderInfo.idleItem.idlePlace || '未填写' }}</span>
                    </div>
                            <div class="location-info-item full-width">
                                <span class="location-label">买家地址：</span>
                                <span class="location-value" v-if="buyerLocation">{{ buyerLocation.formattedAddress || buyerLocation.address }}</span>
                                <span class="location-value text-warning" v-else-if="buyerAddressText">{{ buyerAddressText }}</span>
                                <span class="location-value text-warning" v-else>未填写或解析失败</span>
                                    </div>
                            <div class="location-info-item" v-if="distanceText">
                                <span class="location-label">距离：</span>
                                <span class="location-value">{{ distanceText }}</span>
                                    </div>
                            <div class="location-info-item" v-if="distanceKm">
                                <span class="location-label">建议：</span>
                                <el-tag :type="distanceKm <= 3 ? 'success' : 'warning'" size="small">
                                    {{ distanceKm <= 3 ? '适合面交' : '建议快递' }}
                                </el-tag>
                                </div>
                                    </div>
                        <el-alert
                            v-if="!sellerLocation || !buyerLocation"
                            title="地址解析提示"
                            type="info"
                            :closable="false"
                            show-icon
                            style="margin-bottom: 15px;">
                            <div slot="default">
                                <p style="margin: 0; line-height: 1.6; font-size: 13px;">
                                    <strong>地址格式建议：</strong><br/>
                                    • 完整格式：城市+详细地址，例如："北京市清华大学"、"上海市复旦大学"<br/>
                                    • 学校名称：可以直接填写学校名称，例如："清华大学"、"复旦大学"<br/>
                                    • 详细地址：包含城市、区县、街道等，例如："北京市海淀区清华大学"<br/>
                                    <span style="color: #909399;">如果地址解析失败，请检查地址是否包含城市信息</span>
                                </p>
                                    </div>
                        </el-alert>
                        <div class="map-container" ref="mapContainer" id="order-map-container"></div>
                        <div class="map-actions">
                            <!-- 买家视角：只显示导航到卖家 -->
                            <el-button 
                                v-if="isBuyer"
                                type="primary" 
                                size="small"
                                icon="el-icon-location"
                                @click="navigateToSeller"
                                :disabled="!sellerLocation && !(orderInfo.idleItem && orderInfo.idleItem.idlePlace)">
                                导航到卖家位置
                            </el-button>
                            <!-- 卖家视角：只显示导航到买家 -->
                            <el-button 
                                v-if="isSeller"
                                type="success" 
                                size="small"
                                icon="el-icon-location"
                                @click="navigateToBuyer"
                                :disabled="!buyerLocation && !buyerAddressText">
                                导航到买家位置
                            </el-button>
                            <el-button 
                                type="info" 
                                size="small"
                                icon="el-icon-refresh"
                                @click="refreshMap">
                                刷新地图
                            </el-button>
                                </div>
                    </div>

                    <!-- 售后申请信息（卖家视角） -->
                    <el-divider></el-divider>
                    <div class="after-sale-section" v-if="isSeller && afterSaleInfo">
                        <h3 class="section-title">售后申请</h3>
                        <el-alert
                            :title="getAfterSaleStatusText(afterSaleInfo.applicationStatus)"
                            :type="getAfterSaleStatusType(afterSaleInfo.applicationStatus)"
                            :closable="false"
                            show-icon>
                        </el-alert>
                        <div class="after-sale-details" style="margin-top: 15px;">
                            <el-descriptions :column="2" border size="small">
                                <el-descriptions-item label="申请类型">
                                    <el-tag size="small">{{ getApplicationTypeText(afterSaleInfo.applicationType) }}</el-tag>
                                </el-descriptions-item>
                                <el-descriptions-item label="退款金额">
                                    <span style="color: #f56c6c; font-weight: bold;">¥{{ afterSaleInfo.refundAmount }}</span>
                                </el-descriptions-item>
                                <el-descriptions-item label="问题描述" :span="2">{{ afterSaleInfo.problemDescription }}</el-descriptions-item>
                                <el-descriptions-item label="申请时间" :span="2">{{ formatTime(afterSaleInfo.createTime) }}</el-descriptions-item>
                            </el-descriptions>
                            
                            <!-- 证据图片 -->
                            <div v-if="afterSaleInfo.evidenceImages" style="margin-top: 15px;">
                                <p style="margin-bottom: 10px;"><strong>买家证据图片：</strong></p>
                                <div class="evidence-images">
                                    <el-image
                                        v-for="(img, index) in getEvidenceImages(afterSaleInfo.evidenceImages)"
                                        :key="index"
                                        :src="img"
                                        :preview-src-list="getEvidenceImages(afterSaleInfo.evidenceImages)"
                                        fit="cover"
                                        class="evidence-image">
                                    </el-image>
                    </div>
                                </div>
                    </div>
                    </div>

                    <!-- 操作按钮 -->
                    <el-divider></el-divider>
                    <div class="action-buttons">
                        <!-- 买家操作 -->
                        <template v-if="isBuyer">
                        <el-button 
                                v-if="orderInfo.orderStatus === 0 && orderInfo.paymentStatus === 0"
                            type="primary" 
                                size="large"
                            icon="el-icon-wallet" 
                                @click="goToPay">
                                立即支付
                            </el-button>
                        <el-button 
                                v-if="orderInfo.orderStatus === 2"
                            type="success" 
                                size="large"
                            icon="el-icon-check" 
                                @click="confirmReceive">
                                确认收货
                            </el-button>
                            <el-button 
                                v-if="orderInfo.orderStatus === 3"
                                type="warning" 
                                size="large"
                                icon="el-icon-warning-outline" 
                                @click="openAfterSaleDialog">
                                申请售后
                            </el-button>
                        </template>
                        
                        <!-- 卖家操作 -->
                        <template v-if="isSeller">
                            <el-button 
                                v-if="afterSaleInfo && (afterSaleInfo.applicationStatus === 0 || afterSaleInfo.applicationStatus === 1 || afterSaleInfo.applicationStatus === 2)"
                                type="warning" 
                                size="large"
                                icon="el-icon-s-check" 
                                @click="openSellerReviewDialog">
                                审核售后申请
                            </el-button>
                            <el-button 
                                v-if="orderInfo.orderStatus === 1 && orderInfo.paymentStatus === 1"
                                type="warning" 
                                size="large"
                                icon="el-icon-truck" 
                                @click="confirmShip">
                                确认发货
                            </el-button>
                        </template>
                        
                        <el-button 
                            type="default" 
                            size="large"
                            icon="el-icon-back"
                            @click="goBack">
                            返回
                            </el-button>
                        </div>
                </el-card>

                <!-- 订单不存在 -->
                <el-card v-else class="order-card" shadow="hover">
                    <div class="empty-state">
                        <i class="el-icon-warning-outline"></i>
                        <p>订单不存在或已删除</p>
                        <el-button type="primary" @click="goBack">返回</el-button>
                    </div>
                </el-card>
            </div>
            <app-foot></app-foot>
        </app-body>

        <!-- 申请售后弹窗 -->
                <el-dialog
            title="申请售后"
            :visible.sync="afterSaleDialogVisible"
                    width="600px"
            @close="resetAfterSaleForm">
            <el-form :model="afterSaleForm" :rules="afterSaleRules" ref="afterSaleForm" label-width="100px">
                <el-form-item label="申请类型" prop="applicationType">
                    <el-select v-model="afterSaleForm.applicationType" placeholder="请选择申请类型" style="width: 100%">
                        <el-option label="质量问题" :value="1"></el-option>
                        <el-option label="描述不符" :value="2"></el-option>
                        <el-option label="未收到货" :value="3"></el-option>
                        <el-option label="其他" :value="4"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="问题描述" prop="problemDescription">
                    <el-input
                        type="textarea"
                        :rows="4"
                        v-model="afterSaleForm.problemDescription"
                        placeholder="请详细描述遇到的问题..."
                        maxlength="500"
                        show-word-limit>
                    </el-input>
                </el-form-item>
                <el-form-item label="证据图片">
                    <el-upload
                        class="evidence-uploader"
                        :action="'/api/file'"
                        :show-file-list="true"
                        :file-list="evidenceFileList"
                        :on-success="handleEvidenceSuccess"
                        :on-remove="handleEvidenceRemove"
                        :before-upload="beforeEvidenceUpload"
                        list-type="picture"
                        accept="image/*"
                        multiple>
                        <el-button size="small" type="primary" icon="el-icon-upload">上传图片</el-button>
                        <div slot="tip" class="el-upload__tip">支持上传多张图片，每张不超过5MB</div>
                    </el-upload>
                </el-form-item>
                <el-form-item label="退款金额">
                    <el-input-number
                        v-model="afterSaleForm.refundAmount"
                        :precision="2"
                        :min="0"
                        :max="orderInfo ? orderInfo.orderPrice : 0"
                        style="width: 100%">
                    </el-input-number>
                    <div style="color: #909399; font-size: 12px; margin-top: 5px;">
                        最多可申请退款：¥{{ orderInfo ? orderInfo.orderPrice : 0 }}
                        </div>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="afterSaleDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitAfterSale" :loading="afterSaleSubmitting">提 交</el-button>
            </span>
        </el-dialog>

        <!-- 卖家审核售后申请弹窗 -->
        <el-dialog
            title="审核售后申请"
            :visible.sync="sellerReviewDialogVisible"
            width="600px"
            @close="resetSellerReviewForm">
            <el-form :model="sellerReviewForm" :rules="sellerReviewRules" ref="sellerReviewForm" label-width="100px">
                <el-form-item label="申请类型">
                    <el-input :value="getApplicationTypeText(afterSaleInfo && afterSaleInfo.applicationType)" disabled></el-input>
                </el-form-item>
                <el-form-item label="退款金额">
                    <el-input :value="'¥' + (afterSaleInfo && afterSaleInfo.refundAmount)" disabled></el-input>
                </el-form-item>
                <el-form-item label="问题描述">
                    <el-input type="textarea" :value="afterSaleInfo && afterSaleInfo.problemDescription" disabled :rows="3"></el-input>
                </el-form-item>
                <el-form-item label="审核结果" prop="reviewResult">
                    <el-radio-group v-model="sellerReviewForm.reviewResult">
                        <el-radio :label="1">同意申请</el-radio>
                        <el-radio :label="2">拒绝申请</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="拒绝原因" prop="rejectReason" v-if="sellerReviewForm.reviewResult === 2">
                            <el-input
                                type="textarea"
                        :rows="4"
                        placeholder="请填写拒绝原因"
                        v-model="sellerReviewForm.rejectReason"
                                maxlength="200"
                                show-word-limit>
                            </el-input>
                </el-form-item>
                <el-form-item label="卖家举证图片">
                    <el-upload
                        :action="'/api/file'"
                        list-type="picture-card"
                        :on-success="handleSellerEvidenceUploadSuccess"
                        :on-remove="handleSellerEvidenceUploadRemove"
                        :before-upload="beforeAfterSaleUpload"
                        :file-list="sellerEvidenceUploadList"
                        :limit="3"
                        accept="image/*">
                        <i class="el-icon-plus"></i>
                    </el-upload>
                    <div slot="tip" class="el-upload__tip">最多上传3张图片，每张不超过5MB</div>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="sellerReviewDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitSellerReview" :loading="sellerReviewSubmitting">提 交</el-button>
            </span>
                </el-dialog>

        <!-- 空状态提示 -->
        <el-card v-if="!isLoading && !orderInfo" class="order-card" shadow="hover">
            <el-empty description="订单不存在或加载失败">
                <el-button type="primary" @click="$router.push('/index')">返回首页</el-button>
            </el-empty>
        </el-card>
    </div>
</template>

<script>
    import AppHead from '../common/AppHeader.vue';
    import AppBody from '../common/AppPageBody.vue';
    import AppFoot from '../common/AppFoot.vue';
    import { 
        initMap, 
        geocodeAddress, 
        addMarker, 
        calculateDistance, 
        formatDistance, 
        fitMapView, 
        openNavigation 
    } from '../../utils/mapUtil';

    export default {
        name: "order",
        components: {
            AppHead,
            AppBody,
            AppFoot
        },
        data() {
            return {
                isLoading: false,
                orderInfo: null,
                productImage: '',
                sellerName: '',
                sellerAvatar: '',
                buyerName: '',
                buyerAvatar: '',
                currentUserId: null,
                isBuyer: false,
                isSeller: false,
                // 申请售后相关
                afterSaleDialogVisible: false,
                afterSaleSubmitting: false,
                afterSaleForm: {
                    applicationType: null,
                    problemDescription: '',
                    refundAmount: 0,
                    evidenceImages: []
                },
                evidenceFileList: [],
                afterSaleRules: {
                    applicationType: [
                        { required: true, message: '请选择申请类型', trigger: 'change' }
                    ],
                    problemDescription: [
                        { required: true, message: '请填写问题描述', trigger: 'blur' },
                        { min: 10, max: 500, message: '问题描述长度在10到500个字符', trigger: 'blur' }
                    ]
                },
                afterSaleInfo: null, // 售后申请信息
                // 卖家审核售后相关
                sellerReviewDialogVisible: false,
                sellerReviewSubmitting: false,
                sellerReviewForm: {
                    reviewResult: 1,
                    rejectReason: '',
                    sellerEvidenceImages: []
                },
                sellerReviewRules: {
                    reviewResult: [
                        { required: true, message: '请选择审核结果', trigger: 'change' }
                    ],
                    rejectReason: [
                        { required: true, message: '拒绝申请时必须填写拒绝原因', trigger: 'blur' }
                    ]
                },
                sellerEvidenceUploadList: [],
                // 地图相关
                map: null,
                sellerLocation: null, // 卖家位置 {lng, lat, address}
                buyerLocation: null, // 买家位置 {lng, lat, address}
                buyerAddressText: '', // 买家地址原始文本（即使解析失败也显示）
                distanceKm: null, // 距离（公里）
                distanceText: '', // 距离文本
                geocoder: null // 地址解析器
            };
        },
        computed: {
            // 是否显示交易地点导航（待发货或待收货时显示）
            showLocationNavigation() {
                if (!this.orderInfo) return false;
                // 订单状态：1-待发货，2-待收货
                // 且已支付
                return (this.orderInfo.orderStatus === 1 || this.orderInfo.orderStatus === 2) 
                    && this.orderInfo.paymentStatus === 1
                    && this.orderInfo.idleItem 
                    && this.orderInfo.idleItem.idlePlace;
            }
        },
        created() {
            this.currentUserId = this.getCookie('shUserId');
            this.loadOrderInfo();
        },
        mounted() {
            // 等待DOM更新后再初始化地图
            this.$nextTick(() => {
                if (this.showLocationNavigation) {
                    this.initMap();
                }
            });
        },
        watch: {
            showLocationNavigation(newVal) {
                if (newVal) {
                    this.$nextTick(() => {
                        this.initMap();
                    });
                        } else {
                    this.destroyMap();
                }
            }
        },
        methods: {
            loadOrderInfo() {
                const orderId = this.$route.query.id;
                if (!orderId) {
                    this.$message.error('订单ID不存在');
                    this.$router.push('/index');
                    return;
                }

                this.isLoading = true;
                this.$api.getOrder({ id: orderId }).then(res => {
                    if (res.status_code === 1 && res.data) {
                        this.orderInfo = res.data;
                        
                        // 判断当前用户是买家还是卖家
                        if (this.currentUserId) {
                            const currentUserIdNum = parseInt(this.currentUserId);
                            this.isBuyer = this.orderInfo.userId === currentUserIdNum;
                            this.isSeller = this.orderInfo.idleItem && 
                                          this.orderInfo.idleItem.userId === currentUserIdNum;
                        }
                        
                        // 处理商品图片
                        if (this.orderInfo.idleItem && this.orderInfo.idleItem.pictureList) {
                            try {
                                const pictureList = JSON.parse(this.orderInfo.idleItem.pictureList);
                                if (pictureList && pictureList.length > 0) {
                                    this.productImage = pictureList[0];
                                }
                            } catch (e) {
                                console.error('解析商品图片失败:', e);
                            }
                        }
                        
                        // 处理卖家信息
                        if (this.orderInfo.idleItem) {
                            if (this.orderInfo.idleItem.user) {
                                this.sellerName = this.orderInfo.idleItem.user.nickname || '未知卖家';
                                this.sellerAvatar = this.orderInfo.idleItem.user.avatar || '';
                            } else if (this.orderInfo.idleItem.userId) {
                                // 如果没有user对象，但有userId，尝试获取用户信息
                                this.loadSellerInfo(this.orderInfo.idleItem.userId);
                            }
                        }
                        
                        // 加载售后申请信息
                        this.loadAfterSaleInfo();
                        
                        // 处理买家信息
                        if (this.orderInfo.user) {
                            this.buyerName = this.orderInfo.user.nickname || '未知买家';
                            this.buyerAvatar = this.orderInfo.user.avatar || '';
                        }
                        
                        // 加载买家地址（无论是否显示地图都需要加载，用于显示）
                        this.loadBuyerAddressForDisplay();
                        
                        // 如果显示地图，初始化地图
                        if (this.showLocationNavigation) {
                            this.$nextTick(() => {
                                this.initMap();
                            });
                        }
                    } else {
                        this.$message.error(res.msg || '订单不存在');
                        this.orderInfo = null;
                    }
                }).catch(e => {
                    console.error('获取订单信息失败:', e);
                    this.$message.error('获取订单信息失败');
                    this.orderInfo = null;
                }).finally(() => {
                    this.isLoading = false;
                });
            },
            goToPay() {
                if (!this.orderInfo || !this.orderInfo.id) {
                    this.$message.error('订单信息不存在');
                    return;
                }

                // 确认支付
                this.$confirm('确认支付 ¥' + this.orderInfo.orderPrice + ' 吗？', '确认支付', {
                    confirmButtonText: '确认支付',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    // 调用模拟支付接口
                    this.$api.simulatePay({
                        orderId: this.orderInfo.id
                    }).then(res => {
                        if (res.status_code === 1) {
                            this.$message.success('支付成功！');
                            // 跳转到支付成功页面
                            this.$router.push({
                                path: '/alipay/pay',
                                query: {
                                    orderNumber: this.orderInfo.orderNumber,
                                    price: this.orderInfo.orderPrice,
                                    orderId: this.orderInfo.id
                                }
                            });
                        } else {
                            this.$message.error(res.msg || '支付失败，请稍后重试');
                        }
                    }).catch(e => {
                        console.error('支付失败:', e);
                        this.$message.error('支付失败，请稍后重试');
                    });
                }).catch(() => {
                    // 用户取消支付
                });
            },
            goBack() {
                this.$router.go(-1);
            },
            loadSellerInfo(userId) {
                // 如果后端没有返回user信息，可以尝试通过API获取
                // 这里暂时不实现，因为通常后端应该返回完整的user信息
                console.log('卖家ID:', userId);
            },
            getCookie(cname) {
                const name = cname + "=";
                const ca = document.cookie.split(';');
                for (let i = 0; i < ca.length; i++) {
                    let c = ca[i].trim();
                    if (c.indexOf(name) === 0) return c.substring(name.length, c.length);
                }
                return "";
            },
            goToChat() {
                if (!this.orderInfo || !this.orderInfo.idleItem) {
                    this.$message.error('商品信息不存在');
                    return;
                }
                const sellerId = this.orderInfo.idleItem.userId;
                const idleId = this.orderInfo.idleItem.id;
                if (!sellerId) {
                    this.$message.error('卖家信息不存在');
                    return;
                }
                // 跳转到私信页面
                this.$router.push({
                    path: '/chat',
                    query: {
                        targetUserId: sellerId,
                        idleId: idleId
                    }
                });
            },
            goToChatWithBuyer() {
                if (!this.orderInfo || !this.orderInfo.userId) {
                    this.$message.error('买家信息不存在');
                    return;
                }
                const buyerId = this.orderInfo.userId;
                const idleId = this.orderInfo.idleItem ? this.orderInfo.idleItem.id : null;
                // 跳转到私信页面
                this.$router.push({
                    path: '/chat',
                    query: {
                        targetUserId: buyerId,
                        idleId: idleId
                    }
                });
            },
            confirmShip() {
                this.$confirm('确认已发货吗？', '确认发货', {
                    confirmButtonText: '确认',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$api.updateOrder({
                        id: this.orderInfo.id,
                        orderStatus: 2 // 待收货
                    }).then(res => {
                    if (res.status_code === 1) {
                            this.$message.success('发货成功！');
                            this.loadOrderInfo(); // 刷新订单信息
                        } else {
                            this.$message.error(res.msg || '发货失败');
                        }
                    }).catch(e => {
                        console.error('发货失败:', e);
                        this.$message.error('发货失败，请稍后重试');
                    });
                }).catch(() => {});
            },
            confirmReceive() {
                this.$confirm('确认已收到商品吗？', '确认收货', {
                    confirmButtonText: '确认',
                            cancelButtonText: '取消',
                            type: 'warning'
                        }).then(() => {
                            this.$api.updateOrder({
                        id: this.orderInfo.id,
                        orderStatus: 3 // 已完成
                            }).then(res => {
                                if (res.status_code === 1) {
                            this.$message.success('确认收货成功！');
                            this.loadOrderInfo(); // 刷新订单信息
                        } else {
                            this.$message.error(res.msg || '确认收货失败');
                        }
                    }).catch(e => {
                        console.error('确认收货失败:', e);
                        this.$message.error('确认收货失败，请稍后重试');
                    });
                }).catch(() => {});
            },
            getStatusType(status) {
                const types = {
                    0: 'warning',  // 待付款
                    1: 'info',     // 待发货
                    2: 'primary',  // 待收货
                    3: 'success',  // 已完成
                    4: 'danger'    // 已取消
                };
                return types[status] || 'info';
            },
            getStatusText(status) {
                const texts = {
                    0: '待付款',
                    1: '待发货',
                    2: '待收货',
                    3: '已完成',
                    4: '已取消'
                };
                return texts[status] || '未知状态';
            },
            formatTime(time) {
                if (!time) return '';
                return time.toString().substring(0, 19).replace('T', ' ');
            },
            openAfterSaleDialog() {
                // 检查是否已有售后申请
                if (this.orderInfo && this.orderInfo.id) {
                    this.$api.getAfterSaleByOrderId({ orderId: this.orderInfo.id }).then(res => {
                        if (res.status_code === 1 && res.data) {
                            this.$message.warning('该订单已有售后申请，请勿重复申请');
                        } else {
                            // 初始化表单
                            this.afterSaleForm.refundAmount = this.orderInfo ? this.orderInfo.orderPrice : 0;
                            this.afterSaleDialogVisible = true;
                        }
                    }).catch(e => {
                        console.error('检查售后申请失败:', e);
                        this.afterSaleForm.refundAmount = this.orderInfo ? this.orderInfo.orderPrice : 0;
                        this.afterSaleDialogVisible = true;
                    });
                }
            },
            resetAfterSaleForm() {
                if (this.$refs.afterSaleForm) {
                    this.$refs.afterSaleForm.resetFields();
                }
                this.afterSaleForm = {
                    applicationType: null,
                    problemDescription: '',
                    refundAmount: 0,
                    evidenceImages: []
                };
                this.evidenceFileList = [];
            },
            beforeAfterSaleUpload(file) {
                const isImage = file.type.startsWith('image/');
                const isLt5M = file.size / 1024 / 1024 < 5;

                if (!isImage) {
                    this.$message.error('只能上传图片文件!');
                    return false;
                }
                if (!isLt5M) {
                    this.$message.error('图片大小不能超过 5MB!');
                    return false;
                }
                return true;
            },
            beforeEvidenceUpload(file) {
                const isImage = file.type.startsWith('image/');
                const isLt5M = file.size / 1024 / 1024 < 5;

                if (!isImage) {
                    this.$message.error('只能上传图片文件!');
                    return false;
                }
                if (!isLt5M) {
                    this.$message.error('图片大小不能超过5MB!');
                    return false;
                }
                return true;
            },
            handleEvidenceSuccess(res, file) {
                let imageUrl = '';
                if (res.status_code === 1 && res.data) {
                    imageUrl = res.data;
                } else if (typeof res === 'string') {
                    try {
                        const parsed = JSON.parse(res);
                        if (parsed.status_code === 1 && parsed.data) {
                            imageUrl = parsed.data;
                        }
                    } catch (e) {
                        console.error('解析上传响应失败:', e);
                    }
                }
                
                if (imageUrl) {
                    this.afterSaleForm.evidenceImages.push(imageUrl);
                    this.$message.success('图片上传成功');
                } else {
                    this.$message.error('图片上传失败');
                }
            },
            handleEvidenceRemove(file, fileList) {
                // 从证据图片列表中移除
                if (file.response && file.response.data) {
                    const index = this.afterSaleForm.evidenceImages.indexOf(file.response.data);
                    if (index > -1) {
                        this.afterSaleForm.evidenceImages.splice(index, 1);
                    }
                } else if (file.url) {
                    const index = this.afterSaleForm.evidenceImages.indexOf(file.url);
                    if (index > -1) {
                        this.afterSaleForm.evidenceImages.splice(index, 1);
                    }
                }
            },
            submitAfterSale() {
                this.$refs.afterSaleForm.validate((valid) => {
                    if (!valid) {
                        return false;
                    }

                    if (this.afterSaleForm.evidenceImages.length === 0) {
                        this.$message.warning('请至少上传一张证据图片');
                        return;
                    }

                    this.afterSaleSubmitting = true;
                    const formData = {
                        orderId: this.orderInfo.id,
                        applicationType: this.afterSaleForm.applicationType,
                        problemDescription: this.afterSaleForm.problemDescription,
                        evidenceImages: JSON.stringify(this.afterSaleForm.evidenceImages),
                        refundAmount: this.afterSaleForm.refundAmount
                    };

                    this.$api.applyAfterSale(formData).then(res => {
                                if (res.status_code === 1) {
                            this.$message.success('售后申请提交成功，等待卖家审核');
                            this.afterSaleDialogVisible = false;
                            this.loadOrderInfo(); // 刷新订单信息
                        } else {
                            this.$message.error(res.msg || '申请失败，请稍后重试');
                        }
                    }).catch(e => {
                        console.error('提交售后申请失败:', e);
                        this.$message.error('申请失败，请稍后重试');
                    }).finally(() => {
                        this.afterSaleSubmitting = false;
                    });
                });
            },
            loadAfterSaleInfo() {
                if (!this.orderInfo || !this.orderInfo.id) return;
                this.$api.getAfterSaleByOrderId({ orderId: this.orderInfo.id }).then(res => {
                    if (res.status_code === 1 && res.data) {
                        this.afterSaleInfo = res.data;
                } else {
                        this.afterSaleInfo = null;
                    }
                }).catch(e => {
                    console.error('加载售后申请信息失败:', e);
                    this.afterSaleInfo = null;
                });
            },
            openSellerReviewDialog() {
                if (!this.afterSaleInfo) {
                    this.$message.warning('售后申请信息不存在');
                    return;
                }
                this.sellerReviewForm.reviewResult = 1;
                this.sellerReviewForm.rejectReason = '';
                this.sellerReviewForm.sellerEvidenceImages = [];
                this.sellerEvidenceUploadList = [];
                this.sellerReviewDialogVisible = true;
            },
            resetSellerReviewForm() {
                if (this.$refs.sellerReviewForm) {
                    this.$refs.sellerReviewForm.resetFields();
                }
                this.sellerReviewForm = {
                    reviewResult: 1,
                    rejectReason: '',
                    sellerEvidenceImages: []
                };
                this.sellerEvidenceUploadList = [];
            },
            handleSellerEvidenceUploadSuccess(response, file, fileList) {
                if (response.status_code === 1 && response.data) {
                    this.sellerReviewForm.sellerEvidenceImages.push(response.data);
                    this.$message.success('图片上传成功');
                    } else {
                    this.$message.error('图片上传失败');
                }
            },
            handleSellerEvidenceUploadRemove(file, fileList) {
                if (file.response && file.response.data) {
                    const index = this.sellerReviewForm.sellerEvidenceImages.indexOf(file.response.data);
                    if (index > -1) {
                        this.sellerReviewForm.sellerEvidenceImages.splice(index, 1);
                    }
                } else if (file.url) {
                    const index = this.sellerReviewForm.sellerEvidenceImages.indexOf(file.url);
                    if (index > -1) {
                        this.sellerReviewForm.sellerEvidenceImages.splice(index, 1);
                    }
                }
            },
            submitSellerReview() {
                this.$refs.sellerReviewForm.validate((valid) => {
                    if (!valid) {
                        return false;
                    }
                    
                    if (this.sellerReviewForm.reviewResult === 2 && !this.sellerReviewForm.rejectReason) {
                        this.$message.warning('拒绝申请时必须填写拒绝原因');
                    return;
                }
                    
                    this.sellerReviewSubmitting = true;
                    const formData = {
                        id: this.afterSaleInfo.id,
                        reviewResult: this.sellerReviewForm.reviewResult,
                        rejectReason: this.sellerReviewForm.rejectReason || null,
                        sellerEvidenceImages: this.sellerReviewForm.sellerEvidenceImages.length > 0 
                            ? JSON.stringify(this.sellerReviewForm.sellerEvidenceImages) 
                            : null
                    };
                    
                    this.$api.reviewAfterSale(formData).then(res => {
                    if (res.status_code === 1) {
                            this.$message.success('审核成功');
                            this.sellerReviewDialogVisible = false;
                            this.loadAfterSaleInfo(); // 刷新售后申请信息
                            this.loadOrderInfo(); // 刷新订单信息
                    } else {
                            this.$message.error(res.msg || '审核失败，请稍后重试');
                    }
                }).catch(e => {
                        console.error('审核售后申请失败:', e);
                        this.$message.error('审核失败，请稍后重试');
                    }).finally(() => {
                        this.sellerReviewSubmitting = false;
                    });
                });
            },
            getAfterSaleStatusText(status) {
                const statuses = {
                    0: '待卖家审核',
                    1: '卖家已同意',
                    2: '卖家已拒绝',
                    3: '待管理员审核（卖家同意）',
                    4: '待管理员审核（卖家拒绝）',
                    5: '管理员已同意退款',
                    6: '管理员已驳回'
                };
                return statuses[status] || '未知状态';
            },
            getAfterSaleStatusType(status) {
                if (status === 0 || status === 3 || status === 4) return 'warning';
                if (status === 1 || status === 5) return 'success';
                if (status === 2 || status === 6) return 'error';
                return 'info';
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
            getEvidenceImages(imagesStr) {
                if (!imagesStr) return [];
                try {
                    return JSON.parse(imagesStr);
                } catch (e) {
                    return [];
                }
            },
            getFundStatusText(status) {
                if (status === null || status === undefined) {
                    return '未知';
                }
                const statuses = {
                    0: '平台担保中',
                    1: '已释放给卖家'
                };
                return statuses[status] || '未知';
            },
            getFundStatusType(status) {
                if (status === null || status === undefined) {
                    return 'info';
                }
                if (status === 0) {
                    return 'warning'; // 平台担保中
                } else if (status === 1) {
                    return 'success'; // 已释放
                }
                return 'info';
            },
            getFundReleaseDate(finishTime) {
                if (!finishTime) return '';
                try {
                    const finish = new Date(finishTime);
                    const releaseDate = new Date(finish);
                    releaseDate.setDate(releaseDate.getDate() + 7); // 加7天
                    return releaseDate.toLocaleDateString('zh-CN', {
                        year: 'numeric',
                        month: '2-digit',
                        day: '2-digit'
                    });
                } catch (e) {
                    return '';
                }
            },
            // ========== 地图相关方法 ==========
            // 初始化地图
            async initMap() {
                if (!this.$refs.mapContainer) {
                    console.warn('地图容器不存在，等待DOM更新');
                    return;
                }
                
                // 确保容器有高度
                const container = document.getElementById('order-map-container');
                if (!container) {
                    console.error('找不到地图容器元素');
                    return;
                }
                
                try {
                    // 销毁旧地图
                    if (this.map) {
                        this.map.destroy();
                        this.map = null;
                    }
                    
                    console.log('开始初始化地图，容器ID: order-map-container');
                    
                    // 动态加载地图API并创建地图实例
                    this.map = await initMap('order-map-container', {
                        zoom: 13
                    });
                    
                    console.log('地图实例创建成功:', this.map);
                    
                    // 等待地图加载完成
                    this.map.on('complete', () => {
                        console.log('地图加载完成，开始解析地址');
                        // 解析地址并显示标记
                        this.geocodeAddresses();
                    });
                    
                    // 如果地图已经加载完成，直接解析地址
                    if (this.map.getStatus() === 'complete') {
                        console.log('地图已加载完成，直接解析地址');
                        this.geocodeAddresses();
                    }
                } catch (e) {
                    console.error('地图初始化失败:', e);
                    this.$message.error('地图初始化失败: ' + e.message);
                }
            },
            // 解析地址
            async geocodeAddresses() {
                if (!this.map) {
                    console.error('地图实例不存在，无法解析地址');
                    return;
                }
                
                console.log('开始解析地址，地图实例:', this.map);
                
                // 解析卖家地址
                const sellerAddress = this.orderInfo.idleItem.idlePlace;
                if (sellerAddress) {
                    try {
                        console.log('开始解析卖家地址:', sellerAddress);
                        const sellerLoc = await geocodeAddress(sellerAddress);
                        console.log('卖家地址解析成功，位置:', sellerLoc);
                        this.sellerLocation = sellerLoc;
                        
                        // 添加标记
                        const sellerMarker = addMarker(this.map, sellerLoc, '卖家位置', 'seller');
                        if (sellerMarker) {
                            console.log('卖家标记已添加:', sellerMarker);
                            // 立即调整到卖家位置（如果只有卖家地址）
                            // 不等待，直接设置，确保地图显示正确位置
                            this.map.setCenter([sellerLoc.lng, sellerLoc.lat]);
                            this.map.setZoom(15);
                            console.log('地图已调整到卖家位置:', [sellerLoc.lng, sellerLoc.lat]);
                } else {
                            console.error('卖家标记添加失败');
                        }
                    } catch (e) {
                        console.error('卖家地址解析失败:', e);
                        this.$message.warning('卖家地址解析失败: ' + e.message);
                    }
                } else {
                    console.warn('卖家未填写发货地址');
                }
                
                // 解析买家地址
                const buyerAddress = await this.loadBuyerAddress();
                if (buyerAddress) {
                    try {
                        console.log('开始解析买家地址:', buyerAddress);
                        const buyerLoc = await geocodeAddress(buyerAddress);
                        console.log('买家地址解析成功，位置:', buyerLoc);
                        this.buyerLocation = buyerLoc;
                        
                        // 添加标记
                        const buyerMarker = addMarker(this.map, buyerLoc, '买家位置', 'buyer');
                        if (buyerMarker) {
                            console.log('买家标记已添加:', buyerMarker);
                            
                            // 更新地图视野（包含两个位置）
                            // 立即更新，不延迟
                            this.updateMapView();
                            
                            // 计算距离
                            this.calculateDistance();
                            console.log('距离计算完成');
                        } else {
                            console.error('买家标记添加失败');
                        }
                    } catch (e) {
                        console.error('买家地址解析失败:', e);
                        this.$message.warning('买家地址解析失败，但已显示原始地址');
                    }
                } else {
                    console.warn('买家地址为空');
                }
            },
            // 加载买家地址（用于地图解析）
            async loadBuyerAddress() {
                try {
                    console.log('开始加载买家地址，订单ID:', this.orderInfo.id);
                    const res = await this.$api.getOrderAddress({ orderId: this.orderInfo.id });
                    console.log('获取订单地址API响应:', res);
                    if (res.status_code === 1 && res.data) {
                        if (res.data.detailAddress) {
                            console.log('找到买家地址:', res.data.detailAddress);
                            // 保存原始地址文本，即使解析失败也显示
                            this.buyerAddressText = res.data.detailAddress;
                            return res.data.detailAddress;
                    } else {
                            console.warn('订单地址数据存在，但detailAddress为空:', res.data);
                        }
                    } else {
                        console.warn('订单地址API返回失败或数据为空:', res);
                    }
                    return null;
                } catch (e) {
                    console.error('加载买家地址异常:', e);
                    return null;
                }
            },
            // 加载买家地址（仅用于显示，不用于地图解析）
            async loadBuyerAddressForDisplay() {
                try {
                    console.log('开始加载买家地址用于显示，订单ID:', this.orderInfo.id);
                    const res = await this.$api.getOrderAddress({ orderId: this.orderInfo.id });
                    console.log('获取订单地址API响应（显示用）:', res);
                    if (res.status_code === 1 && res.data && res.data.detailAddress) {
                        console.log('找到买家地址用于显示:', res.data.detailAddress);
                        this.buyerAddressText = res.data.detailAddress;
                    } else {
                        console.warn('未找到买家地址:', res);
                        this.buyerAddressText = '';
                    }
                } catch (e) {
                    console.error('加载买家地址异常（显示用）:', e);
                    this.buyerAddressText = '';
                }
            },
            // 更新地图视野
            updateMapView() {
                if (!this.map) {
                    console.warn('updateMapView: 地图实例不存在');
                    return;
                }
                
                const locations = [];
                if (this.sellerLocation) {
                    locations.push(this.sellerLocation);
                    console.log('添加卖家位置到视野:', this.sellerLocation);
                }
                if (this.buyerLocation) {
                    locations.push(this.buyerLocation);
                    console.log('添加买家位置到视野:', this.buyerLocation);
                }
                
                if (locations.length > 0) {
                    console.log('调整地图视野，包含', locations.length, '个位置');
                    console.log('位置详情:', locations.map(loc => ({ lng: loc.lng, lat: loc.lat, address: loc.address })));
                    try {
                        fitMapView(this.map, locations);
                        // 延迟检查地图视野是否已更新
                        setTimeout(() => {
                            try {
                                const center = this.map.getCenter();
                                const zoom = this.map.getZoom();
                                console.log('地图当前中心点:', center.toString(), '缩放级别:', zoom);
                            } catch (e) {
                                console.error('获取地图中心点失败:', e);
                            }
                        }, 300);
                    } catch (e) {
                        console.error('调整地图视野异常:', e);
                        // 如果失败，至少设置到第一个位置
                        if (locations.length > 0 && locations[0].lng && locations[0].lat) {
                            this.map.setCenter([locations[0].lng, locations[0].lat]);
                            this.map.setZoom(15);
                            console.log('使用备用方法设置地图中心:', [locations[0].lng, locations[0].lat]);
                        }
                    }
                    } else {
                    console.warn('updateMapView: 没有可用的位置');
                }
            },
            // 计算距离
            calculateDistance() {
                if (!this.sellerLocation || !this.buyerLocation) return;
                
                try {
                    const distance = calculateDistance(this.sellerLocation, this.buyerLocation);
                    if (distance !== null) {
                        this.distanceKm = (distance / 1000).toFixed(2);
                        this.distanceText = formatDistance(distance);
                    }
                } catch (e) {
                    console.error('距离计算失败:', e);
                }
            },
            // 导航到卖家位置
            navigateToSeller() {
                // 如果同时有卖家和买家解析后的位置，打开高德路线页面，展示两点与距离
                const sellerAddress = this.orderInfo.idleItem && this.orderInfo.idleItem.idlePlace;
                const buyerAddressText = this.buyerAddressText;

                const tryOpenRoute = async () => {
                    try {
                        let start = this.buyerLocation;
                        let end = this.sellerLocation;

                        // 如果买家位置尚未解析但有文本，尝试用后端代理解析
                        if (!start && buyerAddressText) {
                            const res = await this.$api.geocodeProxy({ address: buyerAddressText });
                            if (res && res.status_code === 1 && res.data) {
                                start = { lng: res.data.lng, lat: res.data.lat, address: res.data.formattedAddress || buyerAddressText };
                                this.buyerLocation = start;
                            }
                        }

                        // 如果卖家位置尚未解析但有文本，尝试用后端代理解析
                        if (!end && sellerAddress) {
                            const res2 = await this.$api.geocodeProxy({ address: sellerAddress });
                            if (res2 && res2.status_code === 1 && res2.data) {
                                end = { lng: res2.data.lng, lat: res2.data.lat, address: res2.data.formattedAddress || sellerAddress };
                                this.sellerLocation = end;
                            }
                        }

                        if (start && end) {
                            // 两点均可用，打开路线页面（高德会显示距离和路线）
                            openRouteInAmap(start, end, 'car');
                            return true;
                        }
                    } catch (e) {
                        console.error('尝试使用route打开高德地图失败:', e);
                    }
                    return false;
                };

                (async () => {
                    const ok = await tryOpenRoute();
                    if (ok) return;

                    // 回退：如果有解析后的卖家位置，直接导航到卖家
                    if (this.sellerLocation) {
                        try {
                            openNavigation(this.sellerLocation, 'car');
                            return;
                        } catch (e) {
                            console.error('使用解析位置导航失败:', e);
                        }
                    }

                    // 最后回退到使用原始地址打开高德导航
                    if (sellerAddress) {
                        const url = `https://uri.amap.com/navigation?to=${encodeURIComponent(sellerAddress)}&mode=car&policy=1&src=mypage&callnative=1`;
                        window.open(url, '_blank');
                        this.$message.info('正在使用原始地址进行导航');
                    } else {
                        this.$message.warning('卖家地址未填写');
                    }
                })();
            },
            // 导航到买家位置
            navigateToBuyer() {
                // 当卖家发起导航到买家时，优先尝试打开高德路线页面，展示两点与距离
                const sellerAddress = this.orderInfo.idleItem && this.orderInfo.idleItem.idlePlace;
                const buyerAddressText = this.buyerAddressText;

                const tryOpenRoute = async () => {
                    try {
                        let start = this.sellerLocation;
                        let end = this.buyerLocation;

                        if (!start && sellerAddress) {
                            const res = await this.$api.geocodeProxy({ address: sellerAddress });
                            if (res && res.status_code === 1 && res.data) {
                                start = { lng: res.data.lng, lat: res.data.lat, address: res.data.formattedAddress || sellerAddress };
                                this.sellerLocation = start;
                            }
                        }

                        if (!end && buyerAddressText) {
                            const res2 = await this.$api.geocodeProxy({ address: buyerAddressText });
                            if (res2 && res2.status_code === 1 && res2.data) {
                                end = { lng: res2.data.lng, lat: res2.data.lat, address: res2.data.formattedAddress || buyerAddressText };
                                this.buyerLocation = end;
                            }
                        }

                        if (start && end) {
                            openRouteInAmap(start, end, 'car');
                            return true;
                        }
                    } catch (e) {
                        console.error('尝试使用route打开高德地图失败:', e);
                    }
                    return false;
                };

                (async () => {
                    const ok = await tryOpenRoute();
                    if (ok) return;

                    // 回退：如果解析后有买家位置，直接导航到买家
                    if (this.buyerLocation) {
                        try {
                            openNavigation(this.buyerLocation, 'car');
                            return;
                        } catch (e) {
                            console.error('使用解析位置导航失败:', e);
                        }
                    }

                    if (this.buyerAddressText) {
                        const url = `https://uri.amap.com/navigation?to=${encodeURIComponent(this.buyerAddressText)}&mode=car&policy=1&src=mypage&callnative=1`;
                        window.open(url, '_blank');
                        this.$message.info('正在使用原始地址进行导航');
                    } else {
                        this.$message.warning('买家地址未填写');
                    }
                })();
            },
            // 刷新地图
            refreshMap() {
                this.sellerLocation = null;
                this.buyerLocation = null;
                this.buyerAddressText = '';
                this.distanceKm = null;
                this.distanceText = '';
                
                if (this.map) {
                    this.map.clearMap();
                }
                
                this.geocodeAddresses();
                this.$message.success('地图已刷新');
            },
            // 销毁地图
            destroyMap() {
                if (this.map) {
                    this.map.destroy();
                    this.map = null;
                }
                this.sellerLocation = null;
                this.buyerLocation = null;
                this.buyerAddressText = '';
                this.distanceKm = null;
                this.distanceText = '';
            }
        },
        beforeDestroy() {
            this.destroyMap();
        }
    }
</script>

<style scoped>
    .order-container {
        min-height: 85vh;
        padding: 20px 0;
    }

    .order-card {
        max-width: 900px;
        margin: 0 auto;
        border-radius: 12px;
    }

    .card-header {
        padding: 20px;
        border-bottom: 1px solid #f0f2f5;
    }

    .order-title {
        font-size: 24px;
        font-weight: 600;
        color: #303133;
        margin: 0;
        display: flex;
        align-items: center;
    }
    
    .order-title i {
        margin-right: 10px;
        color: #409EFF;
        font-size: 28px;
    }

    .order-info-section {
        padding: 20px;
    }

    .info-row {
        display: flex;
        align-items: center;
        margin-bottom: 20px;
        font-size: 16px;
    }

    .info-label {
        width: 120px;
        color: #606266;
        font-weight: 500;
    }

    .info-value {
        color: #303133;
        flex: 1;
    }

    .info-value.price {
        font-size: 24px;
        font-weight: 700;
        color: #f56c6c;
    }

    .section-title {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 15px;
    }

    .product-info-section {
        padding: 20px;
    }

    .product-card {
        display: flex;
        padding: 20px;
        background: #f8f9fa;
        border-radius: 8px;
        gap: 20px;
    }
    
    .product-image {
        width: 120px;
        height: 120px;
        border-radius: 8px;
        flex-shrink: 0;
    }

    .product-details {
        flex: 1;
    }
    
    .product-name {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
        margin: 0 0 15px 0;
    }
    
    .product-price-info {
        margin-top: 10px;
    }
    
    .product-price {
        font-size: 22px;
        font-weight: 700;
        color: #f56c6c;
    }

    .seller-info-section {
        padding: 20px;
    }

    .seller-card {
        display: flex;
        padding: 20px;
        background: #f8f9fa;
        border-radius: 8px;
        gap: 20px;
        align-items: center;
    }

    .seller-avatar {
        flex-shrink: 0;
    }

    .seller-details {
        flex: 1;
    }

    .seller-name {
        font-size: 16px;
        margin-bottom: 15px;
    }
    
    .seller-label {
        color: #606266;
        font-weight: 500;
    }

    .seller-value {
        color: #303133;
        font-weight: 600;
        font-size: 18px;
    }
    
    .seller-actions {
        display: flex;
        gap: 10px;
    }

    .buyer-info-section {
        padding: 20px;
    }

    .buyer-card {
        display: flex;
        padding: 20px;
        background: #f8f9fa;
        border-radius: 8px;
        gap: 20px;
        align-items: center;
    }
    
    .buyer-avatar {
        flex-shrink: 0;
    }

    .buyer-details {
        flex: 1;
    }

    .buyer-name {
        font-size: 16px;
        margin-bottom: 15px;
    }

    .buyer-label {
        color: #606266;
        font-weight: 500;
    }

    .buyer-value {
        color: #303133;
        font-weight: 600;
        font-size: 18px;
    }

    .buyer-actions {
        display: flex;
        gap: 10px;
    }
    
    .action-buttons {
        padding: 20px;
        display: flex;
        justify-content: center;
        gap: 15px;
    }

    .empty-state {
        text-align: center;
        padding: 60px 20px;
        color: #909399;
    }

    .empty-state i {
        font-size: 64px;
        margin-bottom: 20px;
        color: #c0c4cc;
    }

    .empty-state p {
            font-size: 16px;
        margin-bottom: 30px;
    }

    .fund-hint {
        margin-left: 10px;
        color: #909399;
        font-size: 12px;
    }
    
    /* 地图相关样式 */
    .location-navigation-section {
        margin-top: 20px;
        padding: 20px;
    }
    
    .location-info-card {
        display: flex;
        gap: 20px;
        margin-bottom: 15px;
        padding: 15px;
        background: #f5f7fa;
        border-radius: 8px;
        flex-wrap: wrap;
    }
    
    .location-info-item.full-width {
        width: 100%;
        margin-bottom: 8px;
    }
    
    .location-info-item {
        display: flex;
        align-items: center;
        gap: 8px;
    }
    
    .location-label {
        color: #606266;
            font-size: 14px;
        }
        
    .location-value {
        color: #303133;
        font-weight: 600;
            font-size: 16px;
        }
        
    .location-value.text-warning {
        color: #E6A23C;
        font-weight: normal;
    }
    
    .map-container {
        width: 100%;
        height: 400px;
        border-radius: 8px;
        overflow: hidden;
        margin-bottom: 15px;
        border: 1px solid #e4e7ed;
    }
    
    .map-actions {
        display: flex;
            justify-content: center;
        gap: 10px;
        flex-wrap: wrap;
    }

    @media (max-width: 768px) {
        .order-container {
            padding: 10px;
        }
        
        .product-card {
            flex-direction: column;
        }

        .product-image {
            width: 100%;
            height: 200px;
        }
    }
</style>
