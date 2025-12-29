<template>
    <div>
        <app-head></app-head>
        <app-body>
            <div style="min-height: 85vh;">
                <!-- 轮播图组件 -->
                <div class="swiper-container">
                    <el-carousel height="200px" indicator-position="outside" arrow="hover" class="custom-carousel">
                        <el-carousel-item>
                            <div class="carousel-item">
                                <h3>毕业季闲置专场</h3>
                                <p>低价清仓 好物不浪费</p>
                            </div>
                        </el-carousel-item>
                        <el-carousel-item>
                            <div class="carousel-item item2">
                                <h3>正品数码优选</h3>
                                <p>验货保真 放心交易</p>
                            </div>
                        </el-carousel-item>
                        <el-carousel-item>
                            <div class="carousel-item item3">
                                <h3>图书笔记专区</h3>
                                <p>学长学姐干货 助力学业</p>
                            </div>
                        </el-carousel-item>
                    </el-carousel>
                </div>

                <!-- 页面标题和介绍 -->
                <div class="page-header">
                    <h1 class="page-title">校园二手闲置<span class="highlight">交易市场</span></h1>
                    <p class="page-description">发现校园里的二手宝贝，让闲置物品焕发新生</p>
                </div>

                <!-- 搜索表单组件 -->
                <div class="search-form-container">
                    <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent="handleSearch">
                        <el-form-item>
                            <el-input 
                                v-model="searchForm.keyword" 
                                placeholder="请输入商品名称/关键词" 
                                clearable
                                prefix-icon="el-icon-search"
                                class="search-input"
                            ></el-input>
                        </el-form-item>
                        <el-form-item>
                            <el-select 
                                v-model="searchForm.category" 
                                placeholder="商品分类" 
                                clearable
                                class="search-select"
                            >
                                <el-option label="全部分类" value="0"></el-option>
                                <el-option label="数码科技" value="1"></el-option>
                                <el-option label="生活用品" value="2"></el-option>
                                <el-option label="运动相关" value="3"></el-option>
                                <el-option label="图书笔记" value="4"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item>
                            <el-select 
                                v-model="searchForm.sort" 
                                placeholder="排序方式" 
                                clearable
                                class="search-select"
                            >
                                <el-option label="最新发布" value="new"></el-option>
                                <el-option label="价格从低到高" value="price_asc"></el-option>
                                <el-option label="价格从高到低" value="price_desc"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" class="search-btn" @click="handleSearch">搜索</el-button>
                        </el-form-item>
                    </el-form>
                </div>

                <!-- 分类标签 -->
                <div class="category-tabs">
                    <el-tabs v-model="labelName" type="border-card" @tab-click="handleClick" class="custom-tabs">
                        <el-tab-pane label="全部" name="0">
                            <<<i class="el-icon-s-grid tab-icon"></</</i> 全部
                        </el-tab-pane>
                        <el-tab-pane label="数码科技" name="1">
                            <<<i class="el-icon-mobile-phone tab-icon"></</</i> 数码科技
                        </el-tab-pane>
                        <el-tab-pane label="生活用品" name="2">
                            <<<i class="el-icon-toilet-paper tab-icon"></</</i> 生活用品
                        </el-tab-pane>
                        <el-tab-pane label="运动相关" name="3">
                            <<<i class="el-icon-basketball tab-icon"></</</i> 运动相关
                        </el-tab-pane>
                        <el-tab-pane label="图书笔记" name="4">
                            <<<i class="el-icon-notebook-2 tab-icon"></</</i> 图书笔记
                        </el-tab-pane>
                        <el-tab-pane label="公告展示" name="5">
                            <<<i class="el-icon-bell tab-icon"></</</i> 公告展示
                        </el-tab-pane>
                    </el-tabs>
                </div>

                <!-- 视图切换 -->
                <div class="view-switch-container">
                    <span class="view-title">共 {{totalItem}} 件商品</span>
                    <el-button-group size="small">
                        <el-button 
                            icon="el-icon-grid" 
                            type="primary" 
                            plain
                            :disabled="viewMode === 'card'"
                            @click="viewMode = 'card'"
                        >卡片视图</el-button>
                        <el-button 
                            icon="el-icon-table-lamp" 
                            type="primary" 
                            plain
                            :disabled="viewMode === 'table'"
                            @click="viewMode = 'table'"
                        >表格视图</el-button>
                    </el-button-group>
                </div>

                <!-- 商品卡片列表 -->
                <div class="items-container" v-if="viewMode === 'card'">
                    <el-row :gutter="20">
                        <el-col :xs="24" :sm="12" :md="6" v-for="(idle, index) in idleList" :key="index">
                            <div class="item-card" @click="toDetails(idle)">
                                <div class="item-image-container">
                                    <el-image
                                        class="item-image"
                                        :src="idle.imgUrl"
                                        fit="cover">
                                        <div slot="error" class="image-slot">
                                            <<<i class="el-icon-picture-outline"></</</i>
                                            <span>暂无图片</span>
                                        </div>
                                    </el-image>
                                    <div class="item-tag" v-if="getItemCategory(idle.idleLabel)">
                                        {{getItemCategory(idle.idleLabel)}}
                                    </div>
                                    <div class="item-pin-badge" v-if="idle.isPinned">
                                        <<<i class="el-icon-top"></</</i> 置顶
                                    </div>
                                </div>
                                <div class="item-content">
                                    <h3 class="item-title">{{idle.idleName}}</h3>
                                    <div class="item-meta">
                                        <div class="item-price" v-show="idle.idlePrice !== 0">¥{{idle.idlePrice}}</div>
                                        <div class="item-place"><<<i class="el-icon-location"></</</i> {{idle.idlePlace}}</div>
                                    </div>
                                    <div class="item-time"><<<i class="el-icon-time"></</</i> {{idle.timeStr}}</div>
                                    <div class="user-info" v-if="idle.user">
                                        <el-avatar :size="30" :src="idle.user && idle.user.avatar || ''"></el-avatar>
                                        <div class="user-nickname">{{idle.user && idle.user.nickname || '未知用户'}}</div>
                                    </div>
                                </div>
                            </div>
                        </el-col>
                    </el-row>
                </div>

                <!-- 商品表格视图（修复可选链语法） -->
                <div class="table-container" v-else>
                    <el-table 
                        :data="idleList" 
                        border 
                        stripe
                        style="width: 100%;"
                        @row-click="toDetails"
                        highlight-current-row
                    >
                        <el-table-column prop="idleName" label="商品名称" min-width="200" align="center">
                            <template slot-scope="scope">
                                <div class="table-title">{{scope.row.idleName}}</div>
                            </template>
                        </el-table-column>
                        <el-table-column label="商品图片" width="100" align="center">
                            <template slot-scope="scope">
                                <el-image :src="scope.row.imgUrl" width="60" height="60" fit="cover">
                                    <div slot="error" class="table-img-slot">
                                        <<<i class="el-icon-picture-outline"></</</i>
                                    </div>
                                </el-image>
                            </template>
                        </el-table-column>
                        <el-table-column prop="idlePrice" label="价格" width="100" align="center">
                            <template slot-scope="scope">
                                <span class="table-price">¥{{scope.row.idlePrice || '免费'}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="分类" width="120" align="center">
                            <template slot-scope="scope">
                                <span class="table-category">{{getItemCategory(scope.row.idleLabel)}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="idlePlace" label="交易地点" width="150" align="center"></el-table-column>
                        <el-table-column prop="timeStr" label="发布时间" width="180" align="center"></el-table-column>
                        <el-table-column label="发布人" width="120" align="center">
                            <template slot-scope="scope">
                                <div class="table-user">
                                    <el-avatar :size="24" :src="scope.row.user && scope.row.user.avatar || ''"></el-avatar>
                                    <span>{{scope.row.user && scope.row.user.nickname || '未知用户'}}</span>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column label="置顶" width="80" align="center">
                            <template slot-scope="scope">
                                <el-tag type="warning" v-if="scope.row.isPinned">是</el-tag>
                                <span v-else>否</span>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>

                <!-- 数据统计表格 -->
                <div class="data-stat-container" v-if="totalItem > 0">
                    <div class="stat-title">📊 校园闲置商品数据统计</div>
                    <el-table :data="statData" border style="width:100%;margin-top:10px;" size="small">
                        <el-table-column prop="category" label="商品分类" align="center"></el-table-column>
                        <el-table-column prop="count" label="商品数量" align="center"></el-table-column>
                        <el-table-column prop="avgPrice" label="均价(¥)" align="center"></el-table-column>
                        <el-table-column prop="desc" label="说明" align="center"></el-table-column>
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
                searchForm: {
                    keyword: '',
                    category: '0',
                    sort: ''
                },
                statData: [
                    { category: '数码科技', count: '28+', avgPrice: '326', desc: '交易量最高' },
                    { category: '生活用品', count: '42+', avgPrice: '68', desc: '品类最丰富' },
                    { category: '运动相关', count: '19+', avgPrice: '125', desc: '性价比高' },
                    { category: '图书笔记', count: '35+', avgPrice: '22', desc: '全新居多' },
                    { category: '其他闲置', count: '16+', avgPrice: '56', desc: '品类繁杂' }
                ]
            };
        },
        created() {
            this.findIdleTiem(1)
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
                        this.idleList = list;
                        this.totalItem=res.data.count;
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
                        this.idleList = list;
                        this.totalItem=res.data.count;
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
            handleSearch() {
                this.currentPage = 1;
                this.labelName = this.searchForm.category;
                this.$router.replace({query: {page: 1,labelName:this.labelName}});
                this.$message.success(`已筛选：${this.searchForm.keyword || '无关键词'} - ${this.getItemCategory(this.searchForm.category)}`);
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

    .swiper-container {
        margin: 0 0 20px 0;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 2px 12px 0 rgba(79, 192, 141, 0.15);
    }
    .custom-carousel {
        --el-carousel-indicator-active-color: var(--main-color);
    }
    .carousel-item {
        width: 100%;
        height: 100%;
        background: linear-gradient(120deg, #e8f5e9, #d4edda);
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        color: var(--main-deep);
        font-weight: 600;
    }
    .carousel-item.item2 {
        background: linear-gradient(120deg, #d1fae5, #a7f3d0);
    }
    .carousel-item.item3 {
        background: linear-gradient(120deg, #c7f0db, #94e2bc);
    }
    .carousel-item h3 {
        font-size: 24px;
        margin-bottom: 8px;
    }
    .carousel-item p {
        font-size: 16px;
        opacity: 0.9;
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

    .search-form-container {
        margin-bottom: 20px;
        padding: 15px;
        background: #fff;
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(79, 192, 141, 0.1);
    }
    .search-form {
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        justify-content: center;
        gap: 10px;
    }
    .search-input {
        width: 280px;
        --el-input-border-color: var(--main-light);
        --el-input-focus-border-color: var(--main-color);
    }
    .search-select {
        width: 150px;
        --el-select-border-color: var(--main-light);
        --el-select-focus-border-color: var(--main-color);
    }
    .search-btn {
        background: linear-gradient(90deg, var(--main-deep), var(--main-color)) !important;
        border: none !important;
        padding: 8px 20px;
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
        margin: 10px 0 20px 0;
        padding: 0 10px;
    }
    .view-title {
        font-size: 16px;
        color: var(--text-light);
        font-weight: 500;
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
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 2px 12px 0 rgba(79, 192, 141, 0.1);
        transition: all 0.3s ease;
        margin-bottom: 25px;
        background: #fff;
        cursor: pointer;
        border: 1px solid #f8f9fa;
    }
    .item-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 20px rgba(79, 192, 141, 0.2);
        border-color: var(--main-light);
    }
    .item-image-container {
        position: relative;
        height: 180px;
        overflow: hidden;
    }
    .item-image {
        width: 100%;
        height: 100%;
        transition: transform 0.3s ease;
    }
    .item-card:hover .item-image {
        transform: scale(1.05);
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
        padding: 15px;
    }
    .item-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-color);
        margin: 0 0 10px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        line-height: 1.4;
    }
    .item-meta {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8px;
    }
    .item-price {
        font-size: 18px;
        font-weight: 600;
        color: var(--price-color);
    }
    .item-place {
        font-size: 13px;
        color: var(--text-light);
    }
    .item-time {
        font-size: 12px;
        color: var(--text-gray);
        margin-bottom: 10px;
    }
    .user-info {
        display: flex;
        align-items: center;
	padding-top: 10px;
        border-top: 1px solid #f0f0f0;
    }
    .user-nickname {
        margin-left: 10px;
        font-size: 14px;
        color: var(--text-light);
    }

    .table-container {
        padding: 0 10px;
        margin-bottom: 20px;
    }
    .el-table {
        --el-table-header-text-color: var(--main-deep);
        --el-table-row-hover-bg-color: var(--main-light);
        --el-table-current-row-bg-color: #d4edda;
        border-radius: 8px;
        overflow: hidden;
    }
    .table-title {
        font-size: 14px;
        color: var(--text-color);
        font-weight: 500;
    }
    .table-price {
        color: var(--price-color);
        font-weight: 600;
    }
    .table-category {
        background: var(--main-light);
        color: var(--main-deep);
        padding: 2px 6px;
        border-radius: 4px;
        font-size: 12px;
    }
    .table-user {
        display: flex;
        align-items: center;
        gap: 5px;
        font-size: 13px;
    }
    .table-img-slot {
        width: 60px;
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f5f7fa;
        color: var(--text-gray);
    }

    .data-stat-container {
        margin: 30px 10px 20px 10px;
        padding: 15px;
        background: #fff;
        border-radius: 12px;
        box-shadow: 0 2px 12px 0 rgba(79, 192, 141, 0.1);
    }
    .stat-title {
        font-size: 18px;
        color: var(--main-deep);
        font-weight: 600;
        text-align: center;
        margin-bottom: 5px;
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
        .page-title {
            font-size: 24px;
        }
        .page-description {
            font-size: 14px;
        }
        .item-image-container {
            height: 150px;
        }
        .search-input {
            width: 100%;
        }
        .search-select {
            width: 45%;
        }
        .view-switch-container {
            flex-direction: column;
            gap: 10px;
            align-items: flex-start;
        }
    }
</style>