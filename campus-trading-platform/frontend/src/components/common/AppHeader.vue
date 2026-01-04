<template>
    <div class="header">
        <div class="header-container">
            <div class="app-name">
                <router-link to="/">
                    <i class="el-icon-s-shop"></i>
                    <span class="app-name-text">拾光换物-校园二手交易商城</span>
                </router-link>
            </div>
            <div class="search-container">
                <div class="search-wrapper">
                    <el-autocomplete
                        v-model="searchValue"
                        :fetch-suggestions="querySearch"
                        placeholder="搜索你想要的商品..."
                        :trigger-on-focus="false"
                        @select="handleSelect"
                        @keyup.enter.native="searchIdle"
                        prefix-icon="el-icon-search"
                        clearable
                        class="search-input"
                        popper-class="search-suggestions">
                        <template slot-scope="{ item }">
                            <div class="suggestion-item">
                                <i class="el-icon-goods"></i>
                                <span class="suggestion-text">{{ item.value }}</span>
                            </div>
                        </template>
                    </el-autocomplete>
                    <el-button icon="el-icon-search" @click="searchIdle" class="search-btn">搜索</el-button>
                </div>
            </div>
            <div class="action-buttons">
                <el-button type="primary" icon="el-icon-plus" class="publish-btn" @click="toRelease">{{ releaseButtonText }}</el-button>
                <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="message-badge">
                    <el-button type="info" icon="el-icon-chat-dot-round" class="message-btn" @click="toMessage">消息</el-button>
                </el-badge>
                
                <router-link v-if="!isLogin" class="user-name-text login-btn" to="/login">
                    <i class="el-icon-user"></i> 登录
                </router-link>
                
                <el-dropdown trigger="click" v-else class="user-dropdown">
                    <div class="user-info">
                        <div class="user-name">{{nicknameValue?nicknameValue:nickname}}</div>
                        <el-avatar :size="32" :src="avatarValue?avatarValue:avatar" class="user-avatar"></el-avatar>
                    </div>
                    <el-dropdown-menu slot="dropdown" class="dropdown-menu">
                        <el-dropdown-item>
                            <div @click="toMe" class="dropdown-item">
                                <i class="el-icon-user-solid"></i> 个人中心
                            </div>
                        </el-dropdown-item>
                        <el-dropdown-item divided>
                            <div @click="loginOut" class="dropdown-item logout">
                                <i class="el-icon-switch-button"></i> 退出登录
                            </div>
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </div>
        </div>
    </div>
</template>
<script>

    export default {
        name: 'Header',
        props: ['searchInput','nicknameValue','avatarValue'],
        data() {
            return {
                searchValue: this.searchInput,
                nickname:'登录',
                avatar:'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
                isLogin:false,
                unreadCount: 0, // 未读消息数量
                unreadTimer: null, // 未读消息定时器
                // 搜索建议关键词（可以从后端获取热门搜索词）
                searchSuggestions: [
                    { value: '笔记本电脑' },
                    { value: '手机' },
                    { value: '耳机' },
                    { value: '书籍' },
                    { value: '运动鞋' },
                    { value: '自行车' },
                    { value: '键盘' },
                    { value: '鼠标' }
                ]
            };
        },
        created(){
            // console.log("header");
            if(! this.$globalData.userInfo.nickname){
                this.$api.getUserInfo().then(res=>{
                    console.log('Header getUserInfo:',res);
                    if(res.status_code===1){
                        this.nickname=res.data.nickname;
                        this.avatar=res.data.avatar;
                        
                        res.data.signInTime=res.data.signInTime.substring(0,10);
                        this.$globalData.userInfo=res.data;
                        this.isLogin=true;
                    }
                })
            }else {
                this.nickname=this.$globalData.userInfo.nickname;
                this.avatar=this.$globalData.userInfo.avatar;
                
                this.isLogin=true;
            }
            // 加载未读消息数量
            this.loadUnreadCount();
            // 每30秒刷新一次未读消息数量
            this.unreadTimer = setInterval(() => {
                this.loadUnreadCount();
            }, 30000);
        },
        beforeDestroy() {
            // 清除定时器
            if (this.unreadTimer) {
                clearInterval(this.unreadTimer);
            }
        },
        computed: {
            userRole() {
                return this.$globalData.userInfo.userRole || 0;
            },
            releaseButtonText() {
                // userRole: 1 代表经营性卖家
                return this.userRole === 1 ? '店铺管理' : '发布闲置';
            }
        },
        methods: {
            querySearch(queryString, cb) {
                // 根据输入提供搜索建议
                const suggestions = this.searchSuggestions;
                const results = queryString 
                    ? suggestions.filter(item => item.value.toLowerCase().includes(queryString.toLowerCase()))
                    : suggestions;
                // 限制显示数量
                cb(results.slice(0, 8));
            },
            handleSelect(item) {
                this.searchValue = item.value;
                this.searchIdle();
            },
            searchIdle() {
                if(this.searchValue && this.searchValue.trim() !== '') {
                    if ('/search' !== this.$route.path) {
                        this.$router.push({path: '/search', query: {searchValue: this.searchValue}});
                    } else {
                        this.$router.replace({path: '/search', query: {searchValue: this.searchValue}});
                        this.$router.go(0);
                    }
                } else {
                    this.$message.warning('请输入搜索关键词');
                }
            },
            toMe() {
                if ('/me' !== this.$route.path) {
                    this.$router.push({path: '/me'});
                }
            },
            toMessage(){
                if ('/message' !== this.$route.path) {
                    this.$router.push({path: '/message'});
                }
            },
            toRelease(){
                // userRole: 1 代表经营性卖家
                if (this.userRole === 1) {
                    if ('/merchant/manage' !== this.$route.path) {
                        this.$router.push({path: '/merchant/manage'});
                    }
                } else {
                if ('/release' !== this.$route.path) {
                    this.$router.push({path: '/release'});
                    }
                }
            },
            loginOut(){
                this.$api.logout().then(res=>{
                    if(res.status_code===1){
                        this.$globalData.userInfo={};
                        console.log("login out");
                        this.$message.success('退出登录成功');
                        if ('/index' === this.$route.path) {
                            this.$router.go(0);
                        }else {
                            this.$router.push({path: '/index'});
                        }
                    }else {
                        this.$message.error('网络或系统异常，退出登录失败！');
                    }
                });

            },
            // 加载未读消息数量
            loadUnreadCount() {
                if (!this.isLogin) {
                    this.unreadCount = 0;
                    return;
                }
                
                this.$api.getChatUnreadCount().then(res => {
                    if (res.status_code === 1 && res.data !== undefined) {
                        this.unreadCount = res.data || 0;
                    } else {
                        this.unreadCount = 0;
                    }
                }).catch(err => {
                    console.error('加载未读消息数量失败:', err);
                    this.unreadCount = 0;
                });
            }
        }
    };
</script>
<style scoped>
    .header {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        width: 100%;
        height: 60px;
        background: #ffffff;
        display: flex;
        justify-content: center;
        border-bottom: 1px solid #ebeef5;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        z-index: 1000;
    }

    .header-container {
        width: 1200px;
        max-width: 95%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;
    }

    .app-name {
        flex-shrink: 0;
        margin-right: 15px;
    }

    .app-name a {
        color: #409EFF;
        font-size: 20px;
        font-weight: 600;
        text-decoration: none;
        display: flex;
        align-items: center;
        transition: all 0.3s;
    }

    .app-name a:hover {
        color: #66b1ff;
    }

    .app-name i {
        margin-right: 8px;
        font-size: 24px;
    }
    .app-name-text {
        white-space: nowrap;
    }

    .search-container {
        flex-grow: 1;
        max-width: 600px;
        margin: 0 20px;
    }
    .search-wrapper {
        display: flex;
        align-items: center;
        background: #fff;
        border-radius: 25px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        overflow: hidden;
        transition: all 0.3s ease;
    }
    .search-wrapper:focus-within {
        box-shadow: 0 4px 12px rgba(79, 192, 141, 0.2);
    }
    .search-input {
        flex: 1;
    }
    .search-input :deep(.el-input__inner) {
        border: none;
        border-radius: 25px 0 0 25px;
        height: 40px;
        font-size: 14px;
        padding-left: 40px;
        transition: all 0.3s ease;
    }
    .search-input :deep(.el-input__inner):focus {
        box-shadow: none;
    }
    .search-input :deep(.el-input__prefix) {
        left: 12px;
    }
    .search-btn {
        border-radius: 0 25px 25px 0;
        height: 40px;
        padding: 0 30px;
        background: linear-gradient(90deg, #2d8259, #4fc08d) !important;
        border: none !important;
        font-weight: 600;
        transition: all 0.3s ease;
        margin-left: 0;
    }
    .search-btn:hover {
        background: linear-gradient(90deg, #3ba674, #5fd19f) !important;
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(79, 192, 141, 0.3);
    }
    /* 搜索建议下拉框样式 */
    :deep(.search-suggestions) {
        border-radius: 12px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
        border: 1px solid #e4e7ed;
        margin-top: 5px;
    }
    :deep(.search-suggestions .el-autocomplete-suggestion__list) {
        padding: 8px 0;
    }
    .suggestion-item {
        display: flex;
        align-items: center;
        padding: 10px 20px;
        cursor: pointer;
        transition: background-color 0.2s;
    }
    .suggestion-item:hover {
        background-color: #f5f7fa;
    }
    .suggestion-item i {
        color: #4fc08d;
        margin-right: 10px;
        font-size: 16px;
    }
    .suggestion-text {
        color: #606266;
        font-size: 14px;
    }

    .action-buttons {
        display: flex;
        align-items: center;
        flex-shrink: 0;
    }

    .message-badge {
        margin-right: 10px;
    }
    .publish-btn, .message-btn {
        margin-right: 10px;
        border-radius: 20px;
    }

    .user-name-text {
        font-size: 16px;
        font-weight: 600;
        color: #409EFF;
        cursor: pointer;
        text-decoration: none;
        display: flex;
        align-items: center;
        transition: all 0.3s;
    }

    .user-name-text:hover {
        color: #66b1ff;
    }

    .login-btn i {
        margin-right: 5px;
    }

    .user-dropdown {
        cursor: pointer;
    }

    .user-info {
        display: flex;
        align-items: center;
        padding: 0 5px;
        border-radius: 20px;
        transition: all 0.3s;
    }

    .user-info:hover {
        background-color: #f5f7fa;
    }

    .user-name {
        font-size: 15px;
        color: #409EFF;
        margin-right: 8px;
    }

    .user-avatar {
        border: 2px solid #ebeef5;
    }

    .dropdown-menu {
        min-width: 130px;
    }

    .dropdown-item {
        display: flex;
        align-items: center;
        font-size: 14px;
    }

    .dropdown-item i {
        margin-right: 5px;
    }

    .logout {
        color: #f56c6c;
    }

    /* 响应式调整 */
    @media (max-width: 992px) {
        .app-name a {
            font-size: 18px;
        }
        
        .search-container {
            max-width: 300px;
        }
        
        .publish-btn, .message-btn {
            padding: 9px 12px;
        }
    }

    @media (max-width: 768px) {
        .app-name a span {
            display: none;
        }
        
        .app-name i {
            margin-right: 0;
            font-size: 22px;
        }
        
        .search-container {
            max-width: 200px;
        }
        
        .publish-btn, .message-btn {
            padding: 7px 10px;
            font-size: 12px;
        }
        
        .user-name {
            font-size: 14px;
            margin-right: 5px;
        }
    }
</style>
