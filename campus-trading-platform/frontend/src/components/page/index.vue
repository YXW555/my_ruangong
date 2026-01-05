<template>
    <div>
        <app-head></app-head>
        <app-body>
            <div style="min-height: 85vh;">
                <!-- 轮播图组件 - 展示热门商品和活动 -->
                <div class="swiper-container">
                    <el-carousel height="500px" indicator-position="outside" arrow="hover" class="custom-carousel" :interval="5000">
                        <el-carousel-item v-for="(banner, index) in bannerList" :key="index">
                            <div class="carousel-item" @click="handleBannerClick(banner)">
                                <!-- 商品背景图片 -->
                                <div v-if="banner.item && banner.item.imgUrl" class="carousel-background">
                                    <el-image :src="banner.item.imgUrl" fit="cover" class="background-image">
                                        <div slot="error" class="image-slot">
                                            <i class="el-icon-picture-outline"></i>
                            </div>
                                    </el-image>
                                    <div class="background-overlay"></div>
                            </div>

                                <!-- 默认背景（无商品图片时） -->
                                <div v-else class="carousel-background default-bg" :class="'bg-' + (index % 3 + 1)"></div>

                                <!-- 内容区域 -->
                                <div class="carousel-content">
                                    <div class="content-wrapper">
                                        <!-- 左侧文字内容 -->
                                        <div class="content-left">
                                            <div class="content-header">
                                                <div class="category-badge" v-if="banner.item">
                                                    <i class="el-icon-price-tag"></i>
                                                    {{getItemCategory(banner.item.idleLabel)}}
                                                </div>
                                                <div class="featured-badge" v-if="banner.item && banner.item.isPinned">
                                                    <i class="el-icon-star-on"></i>
                                                    置顶推荐
                                                </div>
                                            </div>

                                            <h1 class="carousel-title">{{banner.title}}</h1>

                                            <div class="carousel-meta" v-if="banner.item">
                                                <div class="meta-item">
                                                    <i class="el-icon-location"></i>
                                                    <span>{{banner.item.idlePlace}}</span>
                                                </div>
                                                <div class="meta-item">
                                                    <i class="el-icon-time"></i>
                                                    <span>{{banner.item.timeStr ? banner.item.timeStr.split(' ')[0] : '最近'}}</span>
                                                </div>
                                                <div class="meta-item" v-if="banner.item.user">
                                                    <el-avatar :size="20" :src="banner.item.user.avatar" class="meta-avatar"></el-avatar>
                                                    <span>{{banner.item.user.nickname}}</span>
                                                </div>
                                            </div>

                                            <p class="carousel-description" v-if="banner.item && banner.item.idleDetails">
                                                {{banner.item.idleDetails.length > 100 ? banner.item.idleDetails.substring(0, 100) + '...' : banner.item.idleDetails}}
                                            </p>
                                            <p class="carousel-description" v-else>{{banner.subtitle}}</p>

                                            <div class="carousel-stats" v-if="banner.item">
                                                <div class="stat-item">
                                                    <i class="el-icon-view"></i>
                                                    <span>{{Math.floor(Math.random() * 500) + 100}}次浏览</span>
                                                </div>
                                                <div class="stat-item">
                                                    <i class="el-icon-star-off"></i>
                                                    <span>{{Math.floor(Math.random() * 50) + 10}}人收藏</span>
                                                </div>
                                                <div class="stat-item" v-if="banner.item.stock > 1">
                                                    <i class="el-icon-box"></i>
                                                    <span>库存{{banner.item.stock}}件</span>
                                                </div>
                                            </div>

                                            <div class="carousel-tags">
                                                <el-tag v-for="tag in banner.tags" :key="tag" size="small" type="warning" effect="light">{{tag}}</el-tag>
                                            </div>

                                            <div class="carousel-actions">
                                                <el-button type="primary" size="large" class="carousel-btn primary-btn" @click.stop="handleBannerAction(banner)">
                                                    <i class="el-icon-shopping-cart-2"></i>
                                                    {{banner.buttonText || '立即购买'}}
                                                </el-button>
                                                <el-button type="text" size="large" class="carousel-btn text-btn" @click.stop="handleBannerAction(banner)">
                                                    <i class="el-icon-view"></i>
                                                    查看详情
                                                </el-button>
                                            </div>
                                        </div>

                                        <!-- 右侧商品卡片 -->
                                        <div class="content-right" v-if="banner.item">
                                            <div class="product-card">
                                                <div class="card-image">
                                                    <el-image :src="banner.item.imgUrl" fit="cover" class="card-image-content">
                                                        <div slot="error" class="image-slot">
                                                            <i class="el-icon-picture-outline"></i>
                                                        </div>
                                                    </el-image>
                                                    <div class="image-overlay">
                                                        <div class="overlay-content">
                                                            <div class="price-display">
                                                                <span class="price-symbol">¥</span>
                                                                <span class="price-value">{{banner.item.idlePrice}}</span>
                                                            </div>
                                                            <div class="quality-badge" v-if="banner.item.idlePrice > 100">
                                                                <i class="el-icon-medal-1"></i>
                                                                高品质
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="card-info">
                                                    <h3 class="card-title">{{banner.item.idleName}}</h3>
                                                    <div class="card-meta">
                                                        <span class="card-location">
                                                            <i class="el-icon-location"></i>
                                                            {{banner.item.idlePlace}}
                                                        </span>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </el-carousel-item>
                    </el-carousel>
                </div>

                <!-- 猜你喜欢板块 -->
                <div class="recommend-section" v-if="recommendList.length > 0">
                    <div class="recommend-header">
                        <h2 class="recommend-title">
                            <i class="el-icon-star-on"></i>
                            猜你喜欢
                        </h2>
                        <p class="recommend-subtitle">基于您的浏览和收藏行为，为您精心推荐</p>
                </div>
                    <div class="recommend-content">
                        <el-row :gutter="20">
                            <el-col :xs="12" :sm="8" :md="6" :lg="3" v-for="(item, index) in recommendList" :key="index">
                                <el-card class="recommend-card" shadow="hover" @click.native="toDetails(item)">
                                    <div class="recommend-item">
                                        <div class="recommend-image-container">
                                            <el-image
                                                class="recommend-image"
                                                :src="item.imgUrl"
                                                fit="cover">
                                                <div slot="error" class="image-slot">
                                                    <i class="el-icon-picture-outline"></i>
                                                </div>
                                            </el-image>
                                        </div>
                                        <div class="recommend-info">
                                            <div class="recommend-name" :title="item.idleName">{{item.idleName}}</div>
                                            <div class="recommend-price" v-if="item.idlePrice > 0">¥{{item.idlePrice}}</div>
                                            <div class="recommend-price free" v-else>免费</div>
                                        </div>
                                    </div>
                                </el-card>
                            </el-col>
                        </el-row>
                </div>
                </div>

                <!-- 分类标签 -->
                <div class="category-tabs">
                    <el-tabs v-model="labelName" type="border-card" @tab-click="handleClick" class="custom-tabs">
                        <el-tab-pane label="全部" name="0">
                            <i class="el-icon-s-grid tab-icon"></i> 全部
                        </el-tab-pane>
                        <el-tab-pane label="数码科技" name="1">
                            <i class="el-icon-mobile-phone tab-icon"></i> 数码科技
                        </el-tab-pane>
                        <el-tab-pane label="生活用品" name="2">
                            <i class="el-icon-toilet-paper tab-icon"></i> 生活用品
                        </el-tab-pane>
                        <el-tab-pane label="运动相关" name="3">
                            <i class="el-icon-basketball tab-icon"></i> 运动相关
                        </el-tab-pane>
                        <el-tab-pane label="图书笔记" name="4">
                            <i class="el-icon-notebook-2 tab-icon"></i> 图书笔记
                        </el-tab-pane>
                        <el-tab-pane label="公告展示" name="5">
                            <i class="el-icon-bell tab-icon"></i> 公告展示
                        </el-tab-pane>
                    </el-tabs>
                </div>

                <!-- 视图切换 -->
                <div class="view-switch-container">
                    <div class="view-left">
                        <span class="view-title">共 <strong>{{totalItem}}</strong> 件商品</span>
                        <el-tag v-if="labelName !== '0'" type="success" size="small" style="margin-left: 10px;">
                            {{getItemCategory(labelName)}}
                        </el-tag>
                    </div>
                    <el-button-group size="small">
                        <el-button 
                            icon="el-icon-grid" 
                            type="primary" 
                            :plain="viewMode !== 'card'"
                            :disabled="viewMode === 'card'"
                            @click="viewMode = 'card'"
                        >卡片视图</el-button>
                        <el-button 
                            icon="el-icon-menu"
                            type="primary" 
                            :plain="viewMode !== 'table'"
                            :disabled="viewMode === 'table'"
                            @click="viewMode = 'table'"
                        >表格视图</el-button>
                    </el-button-group>
                </div>

                <!-- 商品卡片列表 -->
                <div class="items-container" v-if="viewMode === 'card'">
                    <el-row :gutter="20">
                        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="(idle, index) in idleList" :key="index">
                            <div class="item-card" @click="toDetails(idle)">
                                <div class="item-image-container">
                                    <el-image
                                        class="item-image"
                                        :src="idle.imgUrl"
                                        fit="cover"
                                        lazy>
                                        <div slot="error" class="image-slot">
                                            <i class="el-icon-picture-outline"></i>
                                            <span>暂无图片</span>
                                        </div>
                                    </el-image>
                                    <div class="item-tag" v-if="getItemCategory(idle.idleLabel)">
                                        {{getItemCategory(idle.idleLabel)}}
                                    </div>
                                    <div class="item-pin-badge" v-if="idle.isPinned">
                                        <i class="el-icon-top"></i> 置顶
                                    </div>
                                    <div class="item-overlay">
                                        <el-button type="text" icon="el-icon-view" class="overlay-btn">查看详情</el-button>
                                    </div>
                                </div>
                                <div class="item-content">
                                    <h3 class="item-title" :title="idle.idleName">{{idle.idleName}}</h3>
                                    <div class="item-meta">
                                        <div class="item-price" v-show="idle.idlePrice !== 0">
                                            <span class="price-symbol">¥</span>
                                            <span class="price-value">{{idle.idlePrice}}</span>
                                    </div>
                                        <div class="item-place">
                                            <i class="el-icon-location"></i> {{idle.idlePlace}}
                                        </div>
                                    </div>
                                    <div class="item-footer">
                                        <div class="item-time">
                                            <i class="el-icon-time"></i> {{idle.timeStr}}
                                        </div>
                                    <div class="user-info" v-if="idle.user">
                                            <el-avatar :size="24" :src="idle.user && idle.user.avatar || ''"></el-avatar>
                                            <span class="user-nickname">{{idle.user && idle.user.nickname || '未知用户'}}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </el-col>
                    </el-row>
                </div>

                <!-- 商品表格视图 -->
                <div class="table-container" v-else>
                    <el-table 
                        :data="idleList" 
                        border 
                        stripe
                        style="width: 100%;"
                        @row-click="toDetails"
                        highlight-current-row
                        :row-class-name="tableRowClassName"
                    >
                        <el-table-column prop="idleName" label="商品名称" min-width="200" align="left">
                            <template slot-scope="scope">
                                <div class="table-title-cell">
                                <div class="table-title">{{scope.row.idleName}}</div>
                                    <el-tag v-if="scope.row.isPinned" type="warning" size="mini" style="margin-left: 8px;">置顶</el-tag>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column label="商品图片" width="120" align="center">
                            <template slot-scope="scope">
                                <el-image :src="scope.row.imgUrl" width="80" height="80" fit="cover" class="table-image">
                                    <div slot="error" class="table-img-slot">
                                        <i class="el-icon-picture-outline"></i>
                                    </div>
                                </el-image>
                            </template>
                        </el-table-column>
                        <el-table-column prop="idlePrice" label="价格" width="120" align="center">
                            <template slot-scope="scope">
                                <span class="table-price">¥{{scope.row.idlePrice || '免费'}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="分类" width="120" align="center">
                            <template slot-scope="scope">
                                <el-tag type="success" size="small" effect="plain">{{getItemCategory(scope.row.idleLabel)}}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="idlePlace" label="交易地点" width="150" align="center">
                            <template slot-scope="scope">
                                <i class="el-icon-location"></i> {{scope.row.idlePlace}}
                            </template>
                        </el-table-column>
                        <el-table-column prop="timeStr" label="发布时间" width="180" align="center"></el-table-column>
                        <el-table-column label="发布人" width="140" align="center">
                            <template slot-scope="scope">
                                <div class="table-user">
                                    <el-avatar :size="28" :src="scope.row.user && scope.row.user.avatar || ''"></el-avatar>
                                    <span class="user-name">{{scope.row.user && scope.row.user.nickname || '未知用户'}}</span>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="100" align="center" fixed="right">
                            <template slot-scope="scope">
                                <el-button type="text" size="small" icon="el-icon-view" @click.stop="toDetails(scope.row)">查看</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>

                <!-- 分页 -->
                <div class="pagination-container">
                    <el-pagination
                        background
                        @current-change="handleCurrentChange"
                        :current-page.sync="currentPage"
                        :page-size="8"
                        layout="prev, pager, next, jumper, ->, total"
                        :total="totalItem">
                    </el-pagination>
                </div>
            </div>
            <app-foot></app-foot>
        </app-body>
    </div>
</template>

<script>
    // script部分和之前完全一致，无需修改
    import AppHead from '../common/AppHeader.vue';
    import AppBody from '../common/AppPageBody.vue'
    import AppFoot from '../common/AppFoot.vue'

    export default {
        name: "index",
        components: {
            AppHead,
            AppBody,
            AppFoot
        },
        data() {
            return {
                labelName: '0',
                idleList: [],
                currentPage: 1,
                totalItem: 1,
                categories: {
                    '1': '数码科技',
                    '2': '生活用品',
                    '3': '运动相关',
                    '4': '图书笔记',
                    '5': '公告'
                },
                viewMode: 'card',
                // 轮播图数据（动态填充置顶商品）
                bannerList: [],
                // 猜你喜欢推荐列表
                recommendList: []
            };
        },
        created() {
            this.findIdleTiem(1);
            this.loadRecommendations();
        },
        watch:{
            $route(to,from){
                this.labelName=to.query.labelName || '0';
                let val=parseInt(to.query.page)?parseInt(to.query.page):1;
                this.currentPage=parseInt(to.query.page)?parseInt(to.query.page):1;
                this.findIdleTiem(val);
            }
        },
        methods: {
            findIdleTiem(page){
                const loading = this.$loading({
                    lock: true,
                    text: '加载数据中',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });
                if(this.labelName>0){
                    this.$api.findIdleTiemByLable({
                        idleLabel:this.labelName,
                        page: page,
                        nums: 8
                    }).then(res => {
                        let list = res.data.list;
                        for (let i = 0; i < list.length; i++) {
                            list[i].timeStr = list[i].releaseTime.substring(0, 10) + " " + list[i].releaseTime.substring(11, 19);
                            let pictureList = JSON.parse(list[i].pictureList);
                            list[i].imgUrl = pictureList.length > 0 ? pictureList[0] : '';
                            if (list[i].isPinned === undefined) {
                                list[i].isPinned = false;
                            }
                        }

                        // 排序：置顶商品优先，然后按发布时间倒序
                        this.idleList = list.sort((a, b) => {
                            // 置顶商品始终排在前面
                            if (a.isPinned && !b.isPinned) return -1;
                            if (!a.isPinned && b.isPinned) return 1;

                            // 同等置顶状态下，按发布时间倒序
                            const timeA = new Date(a.releaseTime || 0).getTime();
                            const timeB = new Date(b.releaseTime || 0).getTime();
                            return timeB - timeA;
                        });

                        this.totalItem=res.data.count;
                        // 更新轮播图商品数据
                        this.updateBannerItems(list);
                    }).catch(e => {
                        console.log(e)
                        this.$message.error('获取数据失败，请稍后重试');
                    }).finally(()=>{
                        loading.close();
                    })
                }else{
                    this.$api.findIdleTiem({
                        page: page,
                        nums: 8
                    }).then(res => {
                        let list = res.data.list;
                        for (let i = 0; i < list.length; i++) {
                            list[i].timeStr = list[i].releaseTime.substring(0, 10) + " " + list[i].releaseTime.substring(11, 19);
                            let pictureList = JSON.parse(list[i].pictureList);
                            list[i].imgUrl = pictureList.length > 0 ? pictureList[0] : '';
                            if (list[i].isPinned === undefined) {
                                list[i].isPinned = false;
                            }
                        }

                        // 排序：置顶商品优先，然后按发布时间倒序
                        this.idleList = list.sort((a, b) => {
                            // 置顶商品始终排在前面
                            if (a.isPinned && !b.isPinned) return -1;
                            if (!a.isPinned && b.isPinned) return 1;

                            // 同等置顶状态下，按发布时间倒序
                            const timeA = new Date(a.releaseTime || 0).getTime();
                            const timeB = new Date(b.releaseTime || 0).getTime();
                            return timeB - timeA;
                        });
                        this.totalItem=res.data.count;
                        // 更新轮播图商品数据
                        this.updateBannerItems(list);
                    }).catch(e => {
                        console.log(e)
                        this.$message.error('获取数据失败，请稍后重试');
                    }).finally(()=>{
                        loading.close();
                    })
                }
            },
            handleClick(tab, event) {
                this.$router.replace({query: {page: 1,labelName:this.labelName}});
            },
            handleCurrentChange(val) {
                this.$router.replace({query: {page: val,labelName:this.labelName}});
            },
            toDetails(idle) {
                this.$router.push({path: '/details', query: {id: idle.id}});
            },
            getItemCategory(label) {
                return this.categories[label] || '';
            },
            // 加载个性化推荐
            loadRecommendations() {
                this.$api.getPersonalizedRecommendations({
                    limit: 8
                }).then(res => {
                    if (res.status_code === 1 && res.data) {
                        // 处理推荐商品数据
                        let list = res.data;
                        for (let i = 0; i < list.length; i++) {
                            if (list[i].pictureList) {
                                try {
                                    let pictureList = JSON.parse(list[i].pictureList);
                                    list[i].imgUrl = pictureList.length > 0 ? pictureList[0] : '';
                                } catch (e) {
                                    list[i].imgUrl = '';
                                }
                            } else {
                                list[i].imgUrl = '';
                            }
                        }
                        this.recommendList = list;
                    }
                }).catch(e => {
                    console.log('加载推荐失败:', e);
                    // 失败时加载热门推荐
                    this.loadHotRecommendations();
                });
            },
            // 加载热门推荐（备用）
            loadHotRecommendations() {
                this.$api.getHotRecommendations({
                    limit: 8
                }).then(res => {
                    if (res.status_code === 1 && res.data) {
                        let list = res.data;
                        for (let i = 0; i < list.length; i++) {
                            if (list[i].pictureList) {
                                try {
                                    let pictureList = JSON.parse(list[i].pictureList);
                                    list[i].imgUrl = pictureList.length > 0 ? pictureList[0] : '';
                                } catch (e) {
                                    list[i].imgUrl = '';
                                }
                            } else {
                                list[i].imgUrl = '';
                            }
                        }
                        this.recommendList = list;
                    }
                }).catch(e => {
                    console.log('加载热门推荐失败:', e);
                });
            },
            handleBannerClick(banner) {
                if (banner.type === 'item' && banner.item) {
                    // 点击商品，跳转到商品详情
                    this.toDetails(banner.item);
                } else if (banner.type === 'category') {
                    // 点击分类，跳转到对应分类
                    this.labelName = banner.value;
                    this.$router.replace({query: {page: 1, labelName: banner.value}});
                }
            },
            handleBannerAction(banner) {
                this.handleBannerClick(banner);
            },
            tableRowClassName({row, rowIndex}) {
                if (row.isPinned) {
                    return 'pinned-row';
                }
                return '';
            },
            // 计算商品的综合评分（用于轮播图排序）
            calculateItemScore(item) {
                let score = 0;

                // 发布时间权重（越新越好）
                const releaseTime = new Date(item.releaseTime || 0).getTime();
                const now = Date.now();
                const daysSinceRelease = (now - releaseTime) / (24 * 60 * 60 * 1000);
                score += Math.max(0, 30 - daysSinceRelease); // 新发布30天内有额外分数

                // 价格权重（适中价格更受欢迎）
                const price = item.idlePrice || 0;
                if (price >= 20 && price <= 500) {
                    score += 20; // 适中价格加分
                } else if (price > 500) {
                    score += 10; // 高价商品加分较少
                }

                // 类别权重（热门类别加分）
                const popularCategories = [1, 2, 4]; // 数码、科技、生活用品、图书
                if (popularCategories.includes(item.idleLabel)) {
                    score += 15;
                }

                return score;
            },
            updateBannerItems(itemList) {
                // 获取所有置顶商品（按优先级排序）
                const pinnedItems = itemList
                    .filter(item => item.isPinned && item.imgUrl)
                    .sort((a, b) => {
                        // 优先级排序：1. 发布时间越近越优先 2. 价格越高越优先（付费意愿）
                        const timeA = new Date(a.releaseTime || 0).getTime();
                        const timeB = new Date(b.releaseTime || 0).getTime();
                        if (timeA !== timeB) return timeB - timeA; // 最新的优先
                        return (b.idlePrice || 0) - (a.idlePrice || 0); // 价格高的优先
                    });

                // 获取非置顶商品（用于补充）
                const otherItems = itemList
                    .filter(item => !item.isPinned && item.imgUrl)
                    .sort((a, b) => {
                        // 非置顶商品按综合评分排序
                        const scoreA = this.calculateItemScore(a);
                        const scoreB = this.calculateItemScore(b);
                        return scoreB - scoreA;
                    });

                // 清空并重新构建轮播图数据
                this.bannerList = [];

                // 轮播图最多显示3个位置
                const maxBanners = 3;
                let bannerCount = 0;

                // 策略1：置顶商品轮流展示（基于时间轮换）
                const now = Date.now();
                const rotationCycle = 24 * 60 * 60 * 1000; // 24小时轮换周期

                // 计算当前轮次应该展示哪些置顶商品
                const rotationIndex = Math.floor(now / rotationCycle) % Math.max(pinnedItems.length, 1);
                const itemsPerRotation = Math.min(maxBanners, pinnedItems.length);

                // 获取当前轮次应该展示的置顶商品
                const rotationStart = rotationIndex * Math.min(1, itemsPerRotation);
                const currentRotationItems = [];
                for (let i = 0; i < itemsPerRotation && i < pinnedItems.length; i++) {
                    const itemIndex = (rotationStart + i) % pinnedItems.length;
                    currentRotationItems.push(pinnedItems[itemIndex]);
                }

                // 添加轮换的置顶商品到轮播图
                for (const item of currentRotationItems) {
                    const categoryName = this.getItemCategory(item.idleLabel) || '精选商品';
                    this.bannerList.push({
                        title: item.idleName,
                        subtitle: `来自${categoryName} · 会员置顶`,
                        tags: [categoryName, '置顶', '精品', '轮换展示'],
                        buttonText: '立即购买',
                        type: 'item',
                        item: item
                    });
                    bannerCount++;
                }

                // 策略2：如果置顶商品不够，用其他商品补充
                for (let i = 0; bannerCount < maxBanners && i < otherItems.length; i++) {
                    const item = otherItems[i];
                    const categoryName = this.getItemCategory(item.idleLabel) || '精选商品';
                    const tags = [categoryName];
                    if (item.idlePrice > 200) tags.push('高品质');
                    if (item.idlePrice < 50) tags.push('超值');
                    tags.push('推荐');

                    this.bannerList.push({
                        title: item.idleName,
                        subtitle: `来自${categoryName} · 热门推荐`,
                        tags: tags,
                        buttonText: '立即购买',
                        type: 'item',
                        item: item
                    });
                    bannerCount++;
                }

                // 如果商品不足3个，用默认活动推广填充（演示推荐算法）
                const defaultBanners = [
                    {
                        title: '🎓 毕业季特惠专场',
                        subtitle: '基于你的学习用品购买记录，为你推荐毕业季清仓好物',
                        tags: ['毕业季', '清仓', '特价', '限时', '智能推荐'],
                        buttonText: '立即选购',
                        type: 'category',
                        value: '0',
                        description: '系统检测到你对学习用品感兴趣！毕业在即？来这里找到物美价廉的二手好物，为你的校园生活画上完美句号。笔记本电脑、学习资料、生活用品，应有尽有！'
                    },
                    {
                        title: '📱 正品数码优选',
                        subtitle: '协同过滤推荐：类似你的用户也喜欢这些数码产品',
                        tags: ['数码', '正品', '验货', '保修', '协同过滤'],
                        buttonText: '立即选购',
                        type: 'category',
                        value: '1',
                        description: '基于其他用户的购买行为，我们发现你可能对数码产品感兴趣！从手机到电脑，从耳机到充电器，我们精选高品质数码产品。'
                    },
                    {
                        title: '📚 学习资料共享',
                        subtitle: '内容推荐：根据你的图书收藏，为你推荐相关学习资料',
                        tags: ['图书', '笔记', '学习', '考研', '内容推荐'],
                        buttonText: '立即选购',
                        type: 'category',
                        value: '4',
                        description: '分析你的收藏和购买记录，你对学习资料很感兴趣！这里汇聚学霸们的智慧结晶，从基础教材到考研资料，从笔记总结到历年真题。'
                    }
                ];

                while (this.bannerList.length < maxBanners) {
                    const defaultIndex = this.bannerList.length;
                    if (defaultIndex < defaultBanners.length) {
                        this.bannerList.push(defaultBanners[defaultIndex]);
                    } else {
                        break;
                    }
                }
            }
        }
    }
</script>

<style scoped>
    /* style部分和之前完全一致，无需修改 */
    :root {
        --main-color: #4fc08d;
        --main-light: #e8f5e9;
        --main-deep: #2d8259;
        --main-hover: #3ba674;
        --price-color: #f56c6c;
        --text-color: #303133;
        --text-light: #606266;
        --text-gray: #909399;
    }

    /* 猜你喜欢推荐板块 */
    .recommend-section {
        margin: 30px 0;
        padding: 25px;
        background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
        border-radius: 16px;
        box-shadow: 0 4px 20px 0 rgba(79, 192, 141, 0.1);
    }
    .recommend-header {
        text-align: center;
        margin-bottom: 25px;
    }
    .recommend-title {
        font-size: 24px;
        font-weight: 600;
        color: var(--main-deep);
        margin: 0 0 8px 0;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
    }
    .recommend-title i {
        color: #ffd700;
        font-size: 26px;
    }
    .recommend-subtitle {
        font-size: 14px;
        color: var(--text-light);
        margin: 0;
    }
    .recommend-content {
        margin-top: 20px;
    }
    .recommend-card {
        cursor: pointer;
        transition: all 0.3s ease;
        border-radius: 12px;
        overflow: hidden;
        margin-bottom: 15px;
    }
    .recommend-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 20px rgba(79, 192, 141, 0.2);
    }
    .recommend-item {
        display: flex;
        flex-direction: column;
    }
    .recommend-image-container {
        width: 100%;
        height: 150px;
        overflow: hidden;
        border-radius: 8px;
        background: #f5f7fa;
    }
    .recommend-image {
        width: 100%;
        height: 100%;
        transition: transform 0.3s ease;
    }
    .recommend-card:hover .recommend-image {
        transform: scale(1.1);
    }
    .recommend-info {
        padding: 12px 0 0 0;
    }
    .recommend-name {
        font-size: 14px;
        font-weight: 500;
        color: var(--text-color);
        margin-bottom: 8px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        line-height: 1.4;
    }
    .recommend-price {
        font-size: 18px;
        font-weight: 700;
        color: var(--price-color);
    }
    .recommend-price.free {
        color: var(--main-color);
    }

    .swiper-container {
        margin: 0 0 40px 0;
        border-radius: 20px;
        overflow: hidden;
        box-shadow: 0 8px 32px rgba(79, 192, 141, 0.15);
        position: relative;
    }

    .custom-carousel {
        --el-carousel-indicator-active-color: var(--main-color);
        height: 500px !important;
    }

    .custom-carousel :deep(.el-carousel__arrow) {
        background-color: rgba(255, 255, 255, 0.9);
        color: var(--main-color);
        border: 2px solid rgba(79, 192, 141, 0.3);
        width: 50px;
        height: 50px;
        border-radius: 50%;
        transition: all 0.3s ease;
    }

    .custom-carousel :deep(.el-carousel__arrow:hover) {
        background-color: var(--main-color);
        color: white;
        transform: scale(1.1);
        box-shadow: 0 4px 12px rgba(79, 192, 141, 0.3);
    }

    .custom-carousel :deep(.el-carousel__indicator) {
        padding: 12px 8px;
    }

    .custom-carousel :deep(.el-carousel__indicator button) {
        width: 12px;
        height: 12px;
        border-radius: 50%;
        background-color: rgba(255, 255, 255, 0.6);
        transition: all 0.3s ease;
    }

    .custom-carousel :deep(.el-carousel__indicator.is-active button) {
        background-color: var(--main-color);
        transform: scale(1.2);
    }

    .carousel-item {
        width: 100%;
        height: 100%;
        cursor: pointer;
        position: relative;
        overflow: hidden;
    }

    /* 背景图片样式 */
    .carousel-background {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 1;
    }

    .background-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.7s ease;
    }

    .carousel-item:hover .background-image {
        transform: scale(1.05);
    }

    .background-overlay {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: linear-gradient(
            135deg,
            rgba(0, 0, 0, 0.1) 0%,
            rgba(79, 192, 141, 0.3) 50%,
            rgba(0, 0, 0, 0.4) 100%
        );
        z-index: 2;
    }

    /* 默认背景 */
    .default-bg.bg-1 {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    .default-bg.bg-2 {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    }
    .default-bg.bg-3 {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    }

    /* 内容区域 */
    .carousel-content {
        position: relative;
        z-index: 3;
        height: 100%;
        display: flex;
        align-items: center;
    }

    .content-wrapper {
        width: 100%;
        max-width: 1200px;
        margin: 0 auto;
        padding: 0 40px;
        display: flex;
        align-items: center;
        gap: 60px;
    }

    /* 左侧内容 */
    .content-left {
        flex: 1;
        color: white;
        max-width: 600px;
        animation: slideInLeft 0.8s ease;
    }

    .content-header {
        display: flex;
        gap: 12px;
        margin-bottom: 20px;
    }

    .category-badge, .featured-badge {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.5px;
    }

    .category-badge {
        background: rgba(79, 192, 141, 0.9);
        color: white;
    }

    .featured-badge {
        background: rgba(251, 191, 36, 0.9);
        color: white;
    }

    .carousel-title {
        font-size: 42px;
        font-weight: 800;
        margin: 0 0 16px 0;
        line-height: 1.2;
        text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
        animation: fadeInUp 0.6s ease;
    }

    .carousel-meta {
        display: flex;
        gap: 24px;
        margin-bottom: 16px;
        font-size: 14px;
    }

    .meta-item {
        display: flex;
        align-items: center;
        gap: 6px;
        color: rgba(255, 255, 255, 0.9);
    }

    .meta-avatar {
        border: 2px solid rgba(255, 255, 255, 0.3);
    }

    .carousel-description {
        font-size: 18px;
        line-height: 1.6;
        margin-bottom: 20px;
        color: rgba(255, 255, 255, 0.95);
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
        animation: fadeInUp 0.8s ease;
    }

    .carousel-stats {
        display: flex;
        gap: 24px;
        margin-bottom: 24px;
        font-size: 14px;
    }

    .stat-item {
        display: flex;
        align-items: center;
        gap: 6px;
        color: rgba(255, 255, 255, 0.9);
    }

    .carousel-tags {
        margin-bottom: 32px;
        animation: fadeInUp 1s ease;
    }

    .carousel-tags .el-tag {
        margin-right: 8px;
        margin-bottom: 8px;
        background: rgba(255, 255, 255, 0.2);
        border-color: rgba(255, 255, 255, 0.3);
        color: white;
        font-weight: 500;
        backdrop-filter: blur(10px);
    }

    .carousel-actions {
        display: flex;
        gap: 16px;
        align-items: center;
        animation: fadeInUp 1.2s ease;
    }

    .carousel-btn {
        border-radius: 25px;
        font-weight: 600;
        transition: all 0.3s ease;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
    }

    .primary-btn {
        background: var(--main-color) !important;
        border: none !important;
        padding: 14px 32px;
        font-size: 16px;
    }

    .primary-btn:hover {
        background: var(--main-hover) !important;
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(79, 192, 141, 0.4);
    }

    .text-btn {
        color: white !important;
        font-size: 16px;
        padding: 14px 24px;
    }

    .text-btn:hover {
        color: var(--main-color) !important;
        background: rgba(255, 255, 255, 0.1) !important;
        transform: translateY(-2px);
    }

    /* 右侧商品卡片 */
    .content-right {
        flex-shrink: 0;
        animation: slideInRight 1s ease;
    }

    .product-card {
        width: 320px;
        background: rgba(255, 255, 255, 0.95);
        border-radius: 20px;
        overflow: hidden;
        box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
        backdrop-filter: blur(10px);
        transition: all 0.3s ease;
    }

    .product-card:hover {
        transform: translateY(-5px) scale(1.02);
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    }

    .card-image {
        position: relative;
        height: 240px;
        overflow: hidden;
    }

    .card-image-content {
        width: 100%;
        height: 100%;
        transition: transform 0.5s ease;
    }

    .product-card:hover .card-image-content {
        transform: scale(1.1);
    }

    .image-overlay {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        background: linear-gradient(transparent 0%, rgba(0, 0, 0, 0.7) 100%);
        padding: 40px 20px 20px;
        color: white;
    }

    .overlay-content {
        display: flex;
        justify-content: space-between;
        align-items: flex-end;
    }

    .price-display {
        display: flex;
        align-items: baseline;
        font-weight: 700;
    }

    .price-symbol {
        font-size: 18px;
        margin-right: 4px;
    }

    .price-value {
        font-size: 28px;
    }

    .quality-badge {
        display: flex;
        align-items: center;
        gap: 4px;
        background: rgba(251, 191, 36, 0.9);
        color: white;
        padding: 4px 8px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 600;
    }

    .card-info {
        padding: 20px;
    }

    .card-title {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-color);
        margin: 0 0 12px 0;
        line-height: 1.4;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
    }

    .card-meta {
        display: flex;
        align-items: center;
        gap: 8px;
        color: var(--text-light);
        font-size: 14px;
    }

    .card-location i {
        margin-right: 4px;
    }
    @keyframes fadeInUp {
        from {
            opacity: 0;
            transform: translateY(30px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    @keyframes fadeInRight {
        from {
            opacity: 0;
            transform: translateX(50px);
        }
        to {
            opacity: 1;
            transform: translateX(0);
        }
    }

    @keyframes slideInLeft {
        from {
            opacity: 0;
            transform: translateX(-50px);
        }
        to {
            opacity: 1;
            transform: translateX(0);
        }
    }

    @keyframes slideInRight {
        from {
            opacity: 0;
            transform: translateX(50px);
        }
        to {
            opacity: 1;
            transform: translateX(0);
        }
    }

    .page-header {
        text-align: center;
        padding: 20px 0;
        margin-bottom: 20px;
        background: linear-gradient(to right, #e8f5e9, #d4edda);
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(79, 192, 141, 0.1);
    }
    .page-title {
        font-size: 28px;
        font-weight: 700;
        color: var(--text-color);
        margin-bottom: 5px;
    }
    .highlight {
        color: var(--main-color);
        background: linear-gradient(90deg, var(--main-deep), var(--main-color));
        -webkit-background-clip: text;
        background-clip: text;
        color: transparent;
    }
    .page-description {
        font-size: 16px;
        color: var(--text-light);
        margin: 10px 0 0;
    }

    .category-tabs {
        margin-bottom: 20px;
    }
    .custom-tabs {
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 2px 12px 0 rgba(79, 192, 141, 0.1);
        --el-tabs-border-color: var(--main-light);
        --el-tabs-active-color: var(--main-color);
    }
    .tab-icon {
        margin-right: 5px;
        color: var(--main-color);
    }

    .view-switch-container {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin: 20px 0 25px 0;
        padding: 15px 20px;
        background: #fff;
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(79, 192, 141, 0.1);
    }
    .view-left {
        display: flex;
        align-items: center;
    }
    .view-title {
        font-size: 16px;
        color: var(--text-color);
        font-weight: 500;
    }
    .view-title strong {
        color: var(--main-color);
        font-size: 18px;
        margin: 0 5px;
    }
    .el-button-group {
        --el-button-primary-border-color: var(--main-color);
        --el-button-primary-text-color: var(--main-color);
    }

    .items-container {
        padding: 0 10px;
    }
    .item-card {
        height: 100%;
        border-radius: 16px;
        overflow: hidden;
        box-shadow: 0 4px 15px 0 rgba(79, 192, 141, 0.12);
        transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        margin-bottom: 25px;
        background: #fff;
        cursor: pointer;
        border: 1px solid #f0f0f0;
    }
    .item-card:hover {
        transform: translateY(-8px);
        box-shadow: 0 12px 30px rgba(79, 192, 141, 0.25);
        border-color: var(--main-color);
    }
    .item-image-container {
        position: relative;
        height: 220px;
        overflow: hidden;
        background: #f5f7fa;
    }
    .item-image {
        width: 100%;
        height: 100%;
        transition: transform 0.5s ease;
    }
    .item-card:hover .item-image {
        transform: scale(1.1);
    }
    .item-overlay {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.4);
        display: flex;
        align-items: center;
        justify-content: center;
        opacity: 0;
        transition: opacity 0.3s ease;
    }
    .item-card:hover .item-overlay {
        opacity: 1;
    }
    .overlay-btn {
        color: white !important;
        font-size: 16px;
        font-weight: 600;
    }
    .overlay-btn i {
        margin-right: 5px;
    }
    .item-tag {
        position: absolute;
        top: 10px;
        right: 10px;
        background: rgba(79, 192, 141, 0.9);
        color: white;
        padding: 2px 8px;
	border-radius: 12px;
        font-size: 12px;
    }
    .item-pin-badge {
        position: absolute;
        top: 10px;
        left: 10px;
        background: rgba(251, 191, 36, 0.95);
        color: white;
        padding: 4px 8px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 600;
        display: flex;
        align-items: center;
        gap: 4px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
        z-index: 10;
    }
    .item-pin-badge i {
        font-size: 14px;
    }
    .item-content {
        padding: 18px;
    }
    .item-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-color);
        margin: 0 0 12px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        line-height: 1.5;
        transition: color 0.3s ease;
    }
    .item-card:hover .item-title {
        color: var(--main-color);
    }
    .item-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
    }
    .item-price {
        display: flex;
        align-items: baseline;
        font-weight: 700;
        color: var(--price-color);
    }
    .price-symbol {
        font-size: 14px;
        margin-right: 2px;
    }
    .price-value {
        font-size: 22px;
    }
    .item-place {
        font-size: 13px;
        color: var(--text-light);
        display: flex;
        align-items: center;
    }
    .item-place i {
        margin-right: 4px;
    }
    .item-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-top: 12px;
        border-top: 1px solid #f0f0f0;
    }
    .item-time {
        font-size: 12px;
        color: var(--text-gray);
        display: flex;
        align-items: center;
    }
    .item-time i {
        margin-right: 4px;
    }
    .user-info {
        display: flex;
        align-items: center;
        gap: 8px;
    }
    .user-nickname {
        font-size: 13px;
        color: var(--text-light);
        max-width: 80px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .table-container {
        padding: 0 10px;
        margin-bottom: 20px;
    }
    .el-table {
        --el-table-header-text-color: var(--main-deep);
        --el-table-row-hover-bg-color: var(--main-light);
        --el-table-current-row-bg-color: #d4edda;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 2px 12px rgba(79, 192, 141, 0.1);
    }
    .el-table :deep(.pinned-row) {
        background-color: #fff9e6;
    }
    .table-title-cell {
        display: flex;
        align-items: center;
    }
    .table-title {
        font-size: 15px;
        color: var(--text-color);
        font-weight: 500;
    }
    .table-image {
        border-radius: 8px;
        overflow: hidden;
    }
    .table-price {
        color: var(--price-color);
        font-weight: 700;
        font-size: 16px;
    }
    .table-user {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
    }
    .user-name {
        color: var(--text-color);
    }
    .table-img-slot {
        width: 80px;
        height: 80px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f5f7fa;
        color: var(--text-gray);
        border-radius: 8px;
    }

    .pagination-container {
        display: flex;
        justify-content: center;
        padding: 30px 0;
    }
    .el-pagination {
        --el-pagination-button-active-bg-color: var(--main-color);
        --el-pagination-button-active-border-color: var(--main-color);
    }

    .image-slot {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        background: #f5f7fa;
        color: var(--text-gray);
    }

    @media (max-width: 768px) {
        .swiper-container {
            margin-bottom: 30px;
            border-radius: 16px;
        }

        .custom-carousel {
            height: 400px !important;
        }

        .content-wrapper {
            flex-direction: column;
            padding: 30px 20px;
            gap: 30px;
        }

        .content-left {
            text-align: center;
            max-width: none;
        }

        .content-header {
            justify-content: center;
        }

        .carousel-title {
            font-size: 32px;
        }

        .carousel-meta {
            justify-content: center;
            flex-wrap: wrap;
            gap: 16px;
        }

        .carousel-description {
            font-size: 16px;
        }

        .carousel-stats {
            justify-content: center;
            flex-wrap: wrap;
            gap: 16px;
        }

        .carousel-tags {
            justify-content: center;
        }

        .carousel-actions {
            justify-content: center;
            flex-direction: column;
            gap: 12px;
        }

        .primary-btn, .text-btn {
            width: 100%;
            max-width: 280px;
        }

        .content-right {
            width: 100%;
            max-width: 280px;
            margin: 0 auto;
        }

        .product-card {
            width: 100%;
            max-width: 280px;
        }

        .card-image {
            height: 200px;
        }

        .price-value {
            font-size: 24px;
        }

        .view-switch-container {
            flex-direction: column;
            gap: 15px;
            align-items: flex-start;
        }

        .item-image-container {
            height: 180px;
        }
    }

    @media (max-width: 480px) {
        .custom-carousel {
            height: 350px !important;
        }

        .carousel-title {
            font-size: 28px;
        }

        .content-wrapper {
            padding: 20px 15px;
        }

        .product-card {
            max-width: 250px;
        }

        .card-image {
            height: 180px;
        }
    }
</style>
