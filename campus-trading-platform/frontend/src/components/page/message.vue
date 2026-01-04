<template>
    <div>
        <app-head></app-head>
        <app-body>
            <div class="message-container">
                <div class="message-header">
                    <h2 class="message-title">我的消息</h2>
                    <el-tabs v-model="activeTab" @tab-click="handleTabClick" class="message-tabs">
                        <el-tab-pane label="全部" name="all">
                            <span slot="label">
                                <i class="el-icon-message"></i> 全部
                            </span>
                        </el-tab-pane>
                        <el-tab-pane label="商品留言" name="comment">
                            <span slot="label">
                                <i class="el-icon-chat-line-round"></i> 商品留言
                            </span>
                        </el-tab-pane>
                        <el-tab-pane label="私聊消息" name="chat">
                            <span slot="label">
                                <i class="el-icon-chat-dot-round"></i> 私聊消息
                            </span>
                        </el-tab-pane>
                    </el-tabs>
                </div>

                <!-- 商品留言列表 -->
                <div v-if="activeTab === 'all' || activeTab === 'comment'">
                    <div v-if="commentList.length === 0 && activeTab === 'comment'" class="empty-state">
                        <i class="el-icon-chat-line-round empty-icon"></i>
                        <p class="empty-text">暂无商品留言</p>
                        <p class="empty-hint">当有人在你发布的商品下留言时，会显示在这里</p>
                        <el-button type="primary" @click="goToIndex">去逛逛商品</el-button>
                    </div>
                    <div v-else>
                        <div v-for="(mes,index) in commentList" :key="'comment-' + index" 
                             class="message-item comment-item" 
                             @click="toDetails(mes.idle.id)">
                            <div class="message-item-left">
                        <el-image
                                    class="message-avatar"
                                :src="mes.fromU.avatar"
                                    fit="cover">
                                    <div slot="error" class="avatar-error">
                                        <i class="el-icon-user"></i>
                                    </div>
                                </el-image>
                                <div class="message-content-wrapper">
                                    <div class="message-header-info">
                                        <span class="message-nickname">{{mes.fromU.nickname}}</span>
                                        <span class="message-type-tag comment-tag">
                                            <i class="el-icon-chat-line-round"></i> 商品留言
                                        </span>
                                    </div>
                            <div class="message-content">{{mes.content}}</div>
                                    <div class="message-footer">
                                        <span class="message-time">{{formatTime(mes.createTime)}}</span>
                                        <span class="message-goods">商品：{{mes.idle.idleName}}</span>
                                    </div>
                                </div>
                            </div>
                            <div class="message-item-right">
                                <el-image
                                    class="message-goods-image"
                                    :src="mes.idle.imgUrl"
                                    fit="cover">
                                    <div slot="error" class="image-error">
                                        <i class="el-icon-picture-outline"></i>
                                    </div>
                                </el-image>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 私聊会话列表 -->
                <div v-if="activeTab === 'all' || activeTab === 'chat'">
                    <div v-if="chatList.length === 0 && activeTab === 'chat'" class="empty-state">
                        <i class="el-icon-chat-dot-round empty-icon"></i>
                        <p class="empty-text">暂无私聊消息</p>
                        <p class="empty-hint">当有人给你发送私聊消息时，会显示在这里</p>
                        <el-button type="primary" @click="goToChat">去私聊页面</el-button>
                    </div>
                    <div v-else>
                        <div v-for="(session,index) in chatList" :key="'chat-' + index" 
                             class="message-item chat-item" 
                             @click="toChat(session)">
                            <div class="message-item-left">
                                <el-image
                                    class="message-avatar"
                                    :src="getDisplayUser(session).avatar"
                                    fit="cover">
                                    <div slot="error" class="avatar-error">
                                        <i class="el-icon-user"></i>
                                    </div>
                                </el-image>
                                <div class="message-content-wrapper">
                                    <div class="message-header-info">
                                        <span class="message-nickname">{{getDisplayUser(session).nickname}}</span>
                                        <span class="message-type-tag chat-tag">
                                            <i class="el-icon-chat-dot-round"></i> 私聊
                                        </span>
                                        <el-badge 
                                            v-if="getUnreadCountForSession(session) > 0"
                                            :value="getUnreadCountForSession(session)" 
                                            class="unread-badge">
                                        </el-badge>
                                    </div>
                                    <div class="message-content" :class="{'unread-message': getUnreadCountForSession(session) > 0}">{{session.content}}</div>
                                    <div class="message-footer">
                                        <span class="message-time">{{formatTime(session.createTime)}}</span>
                                        <span v-if="session.idle" class="message-goods">商品：{{session.idle.idleName}}</span>
                                    </div>
                                </div>
                            </div>
                            <div class="message-item-right" v-if="session.idle">
                        <el-image
                                    class="message-goods-image"
                                    :src="getGoodsImage(session.idle)"
                                    fit="cover">
                                    <div slot="error" class="image-error">
                                        <i class="el-icon-picture-outline"></i>
                                    </div>
                                </el-image>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 全部为空的状态 -->
                <div v-if="activeTab === 'all' && commentList.length === 0 && chatList.length === 0" class="empty-state">
                    <i class="el-icon-message empty-icon"></i>
                    <p class="empty-text">暂无消息</p>
                    <p class="empty-hint">当有人给你留言或发送私聊消息时，会显示在这里</p>
                    <div class="empty-actions">
                        <el-button type="primary" @click="goToIndex">去逛逛商品</el-button>
                        <el-button @click="goToChat">查看私聊</el-button>
                    </div>
                </div>
            </div>
            <app-foot></app-foot>
        </app-body>
    </div>
</template>

<script>
    import AppHead from '../common/AppHeader.vue';
    import AppBody from '../common/AppPageBody.vue'
    import AppFoot from '../common/AppFoot.vue'

    export default {
        name: "message",
        components: {
            AppHead,
            AppBody,
            AppFoot
        },
        data(){
            return{
                commentList: [],  // 商品留言列表
                chatList: [],     // 私聊会话列表
                activeTab: 'all', // 当前选中的标签页
                currentUserId: null
            };
        },
        created(){
            this.currentUserId = this.getCookie('shUserId');
            this.loadAllMessages();
        },
        methods:{
            getCookie(cname) {
                const name = cname + "=";
                const ca = document.cookie.split(';');
                for (let i = 0; i < ca.length; i++) {
                    let c = ca[i].trim();
                    if (c.indexOf(name) === 0) return c.substring(name.length, c.length);
                }
                return "";
            },
            formatTime(timeStr) {
                if (!timeStr) return '';
                const time = new Date(timeStr);
                const now = new Date();
                const diff = now - time;
                const minutes = Math.floor(diff / 60000);
                const hours = Math.floor(diff / 3600000);
                const days = Math.floor(diff / 86400000);
                
                if (minutes < 1) return '刚刚';
                if (minutes < 60) return `${minutes}分钟前`;
                if (hours < 24) return `${hours}小时前`;
                if (days < 7) return `${days}天前`;
                return timeStr.toString().substring(0, 16);
            },
            async loadAllMessages() {
                // 加载商品留言
                try {
                    const commentRes = await this.$api.getAllMyMessage();
                    if (commentRes.status_code === 1 && commentRes.data) {
                        this.commentList = commentRes.data.map(item => {
                            // 处理图片
                            let imgUrl = '';
                            try {
                                const imgList = JSON.parse(item.idle.pictureList);
                                imgUrl = imgList && imgList.length > 0 ? imgList[0] : '';
                            } catch (e) {
                                imgUrl = '';
                            }
                            item.idle.imgUrl = imgUrl;
                            
                            // 处理内容（移除HTML标签，只保留文本）
                            let content = item.content;
                            try {
                                const contentList = content.split('<br>');
                                content = contentList[0];
                                for (let i = 1; i < contentList.length; i++) {
                                    content += ' ' + contentList[i];
                                }
                                // 移除HTML标签
                                content = content.replace(/<[^>]+>/g, '');
                            } catch (e) {
                                // 如果处理失败，保持原样
                            }
                            item.content = content;
                            
                            return item;
                        });
                    }
                } catch (e) {
                    console.error('加载商品留言失败:', e);
                }

                // 加载私聊会话
                try {
                    const chatRes = await this.$api.getChatSessionList();
                    if (chatRes.status_code === 1 && chatRes.data) {
                        this.chatList = chatRes.data.map(session => {
                            // 处理商品图片
                            if (session.idle && session.idle.pictureList) {
                                try {
                                    const imgList = JSON.parse(session.idle.pictureList);
                                    session.idle.imgUrl = imgList && imgList.length > 0 ? imgList[0] : '';
                                } catch (e) {
                                    session.idle.imgUrl = '';
                                }
                            }
                            // 计算该会话的未读消息数量（如果当前用户是接收方且消息未读）
                            if (this.currentUserId) {
                                const currentUserIdNum = parseInt(this.currentUserId);
                                // 如果消息是发送给当前用户的，且未读，则计数
                                if (session.toUser == currentUserIdNum && session.isRead === 0) {
                                    session.unreadCount = 1; // 会话列表只显示最新一条，所以最多是1
                                } else {
                                    session.unreadCount = 0;
                                }
                            } else {
                                session.unreadCount = 0;
                            }
                            return session;
                        });
                    }
                } catch (e) {
                    console.error('加载私聊会话失败:', e);
                }
            },
            getUnreadCountForSession(session) {
                return session.unreadCount || 0;
            },
            getDisplayUser(session) {
                if (!session) return {};
                if (!this.currentUserId) return session.toU || session.fromU || {};
                if (session.fromUser == this.currentUserId) {
                    return session.toU || {};
                }
                return session.fromU || {};
            },
            getGoodsImage(idle) {
                if (!idle) return '';
                if (idle.imgUrl) return idle.imgUrl;
                if (idle.pictureList) {
                    try {
                        const imgList = JSON.parse(idle.pictureList);
                        return imgList && imgList.length > 0 ? imgList[0] : '';
                    } catch (e) {
                        return '';
                    }
                }
                return '';
            },
            handleTabClick(tab) {
                // 标签切换时不需要额外操作，数据已经加载好了
            },
            toDetails(id){
                this.$router.push({path: '/details', query: {id: id}});
            },
            toChat(session) {
                const targetUserId = session.fromUser == this.currentUserId ? session.toUser : session.fromUser;
                const idleId = session.idleId || null;
                this.$router.push({
                    path: '/chat',
                    query: {
                        targetUserId: targetUserId,
                        idleId: idleId
                    }
                });
            },
            goToIndex() {
                this.$router.push({path: '/index'});
            },
            goToChat() {
                this.$router.push({path: '/chat'});
            }
        }
    }
</script>

<style scoped>
    .message-container {
        min-height: 85vh;
        padding: 20px;
        max-width: 1200px;
        margin: 0 auto;
    }
    .message-header {
        margin-bottom: 20px;
    }
    .message-title {
        font-size: 24px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 20px;
    }
    .message-tabs {
        margin-bottom: 20px;
    }
    .message-tabs :deep(.el-tabs__item) {
        font-size: 15px;
        padding: 0 20px;
    }
    .message-tabs :deep(.el-tabs__item i) {
        margin-right: 5px;
    }

    .message-item {
        cursor: pointer;
        padding: 20px;
        margin-bottom: 12px;
        background: #fff;
        border-radius: 12px;
        border: 1px solid #ebeef5;
        display: flex;
        justify-content: space-between;
        align-items: center;
        transition: all 0.3s ease;
    }
    .message-item:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
        border-color: #4fc08d;
        transform: translateY(-2px);
    }
    .message-item-left {
        flex: 1;
        display: flex;
        align-items: flex-start;
        min-width: 0;
    }
    .message-avatar {
        width: 55px;
        height: 55px;
        border-radius: 8px;
        flex-shrink: 0;
        margin-right: 15px;
    }
    .avatar-error {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f5f7fa;
        color: #909399;
        font-size: 24px;
    }
    .message-content-wrapper {
        flex: 1;
        min-width: 0;
    }
    .message-header-info {
        display: flex;
        align-items: center;
        margin-bottom: 8px;
        gap: 10px;
    }
    .message-nickname {
        font-weight: 600;
        font-size: 16px;
        color: #303133;
    }
    .message-type-tag {
        font-size: 12px;
        padding: 2px 8px;
        border-radius: 12px;
        display: inline-flex;
        align-items: center;
        gap: 4px;
    }
    .comment-tag {
        background: #e8f5e9;
        color: #2d8259;
    }
    .chat-tag {
        background: #e3f2fd;
        color: #1976d2;
    }
    .message-type-tag i {
        font-size: 12px;
    }
    .message-content {
        font-size: 14px;
        color: #606266;
        margin-bottom: 8px;
        line-height: 1.5;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
    }
    .message-content.unread-message {
        font-weight: 600;
        color: #303133;
    }
    .unread-badge {
        margin-left: 8px;
    }
    .message-footer {
        display: flex;
        align-items: center;
        gap: 15px;
        font-size: 12px;
        color: #909399;
    }
    .message-goods {
        color: #4fc08d;
    }
    .message-item-right {
        flex-shrink: 0;
        margin-left: 15px;
    }
    .message-goods-image {
        width: 100px;
        height: 100px;
        border-radius: 8px;
        object-fit: cover;
    }
    .image-error {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f5f7fa;
        color: #909399;
        font-size: 24px;
    }

    /* 空状态样式 */
    .empty-state {
        text-align: center;
        padding: 80px 20px;
        background: #fff;
        border-radius: 12px;
        border: 1px dashed #dcdfe6;
    }
    .empty-icon {
        font-size: 64px;
        color: #c0c4cc;
        margin-bottom: 20px;
    }
    .empty-text {
        font-size: 18px;
        color: #606266;
        margin-bottom: 10px;
        font-weight: 500;
    }
    .empty-hint {
        font-size: 14px;
        color: #909399;
        margin-bottom: 30px;
    }
    .empty-actions {
        display: flex;
        justify-content: center;
        gap: 15px;
    }

    /* 响应式 */
    @media (max-width: 768px) {
        .message-container {
            padding: 15px;
        }
        .message-item {
            padding: 15px;
            flex-direction: column;
            align-items: flex-start;
        }
        .message-item-right {
            margin-left: 0;
            margin-top: 15px;
            width: 100%;
        }
        .message-goods-image {
            width: 100%;
            height: 150px;
        }
        .message-content {
            -webkit-line-clamp: 3;
        }
    }
</style>