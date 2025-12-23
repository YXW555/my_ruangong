<template>
    <div>
        <app-head></app-head>
        <app-body>
            <div class="chat-container">
                <!-- 会话列表 -->
                <div class="chat-session-list">
                    <div class="session-list-header">
                        <span>消息</span>
                    </div>
                    <div v-if="sessionList.length === 0" class="session-empty">
                        暂无会话，去商品页发起私聊吧～
                    </div>
                    <div v-else>
                        <div
                                v-for="(session,index) in sessionList"
                                :key="session.id"
                                :class="['session-item', currentSessionKey === getSessionKey(session) ? 'active' : '']"
                                @click="selectSession(session)"
                        >
                            <el-image
                                    class="session-avatar"
                                    :src="getDisplayUser(session).avatar"
                                    fit="cover"
                            ></el-image>
                            <div class="session-info">
                                <div class="session-top">
                                    <span class="session-name">{{ getDisplayUser(session).nickname }}</span>
                                    <span class="session-time">{{ formatTime(session.createTime) }}</span>
                                </div>
                                <div class="session-bottom">
                                    <span class="session-last">{{ session.content }}</span>
                                    <span v-if="session.idle" class="session-goods-name">
                                        · {{ session.idle.idleName }}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 聊天窗口 -->
                <div class="chat-session-panel">
                    <div class="chat-header" v-if="currentChatTarget">
                        <div class="chat-header-left">
                            <el-avatar :size="40" :src="currentChatTarget.avatar"></el-avatar>
                            <div class="chat-header-text">
                                <div class="chat-header-name">{{ currentChatTarget.nickname }}</div>
                                <div class="chat-header-sub" v-if="currentIdle">
                                    正在沟通：{{ currentIdle.idleName }}
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="chat-header" v-else>
                        <div class="chat-header-empty">
                            请选择左侧的会话或从商品详情页点击“私聊”发起会话
                        </div>
                    </div>

                    <div class="chat-message-list" ref="messageListRef">
                        <div v-if="messageList.length === 0" class="chat-message-empty">
                            暂无聊天记录
                        </div>
                        <div
                                v-else
                                v-for="msg in messageList"
                                :key="msg.id"
                                :class="['chat-message-row', msg.fromUser == currentUserId ? 'self' : 'other']"
                        >
                            <el-avatar
                                    :size="32"
                                    :src="msg.fromU && msg.fromU.avatar"
                                    class="chat-message-avatar"
                            ></el-avatar>
                            <div class="chat-message-bubble">
                                <div class="chat-message-content" v-html="msg.content"></div>
                                <div class="chat-message-time">{{ formatTime(msg.createTime) }}</div>
                            </div>
                        </div>
                    </div>

                    <div class="chat-input-bar">
                        <el-input
                                type="textarea"
                                :rows="3"
                                placeholder="请输入要发送的内容..."
                                v-model="inputContent"
                                maxlength="200"
                                show-word-limit
                                @keyup.enter.native.exact.prevent="handleSend"
                        ></el-input>
                        <div class="chat-input-actions">
                            <el-button type="primary" icon="el-icon-s-promotion" @click="handleSend" :disabled="!currentTargetUserId">
                                发送
                            </el-button>
                        </div>
                    </div>
                </div>
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
        name: "chat",
        components: {
            AppHead,
            AppBody,
            AppFoot
        },
        data() {
            return {
                sessionList: [],
                messageList: [],
                inputContent: '',
                currentUserId: null,
                currentSessionKey: '',
                currentTargetUserId: null,
                currentIdleId: null,
                currentChatTarget: null,
                currentIdle: null
            };
        },
        created() {
            this.currentUserId = this.getCookie('shUserId');
            const targetUserId = this.$route.query.targetUserId;
            const idleId = this.$route.query.idleId;
            this.loadSessionList().then(() => {
                if (targetUserId) {
                    this.openSessionByTarget(targetUserId, idleId);
                }
            });
        },
        methods: {
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
                return timeStr.toString().substring(0, 16);
            },
            getSessionKey(session) {
                const from = session.fromUser;
                const to = session.toUser;
                const idleId = session.idleId || 0;
                const small = from < to ? from : to;
                const big = from < to ? to : from;
                return `${small}_${big}_${idleId}`;
            },
            getDisplayUser(session) {
                if (!session) return {};
                if (!this.currentUserId) return session.toU || session.fromU || {};
                if (session.fromUser == this.currentUserId) {
                    return session.toU || {};
                }
                return session.fromU || {};
            },
            async loadSessionList() {
                try {
                    const res = await this.$api.getChatSessionList();
                    if (res.status_code === 1 && res.data) {
                        this.sessionList = res.data;
                    }
                } catch (e) {
                }
            },
            async loadSessionDetail(targetUserId, idleId) {
                try {
                    const res = await this.$api.getChatSessionDetail({
                        targetUserId: targetUserId,
                        idleId: idleId
                    });
                    if (res.status_code === 1 && res.data) {
                        this.messageList = res.data;
                        this.$nextTick(() => {
                            this.scrollToBottom();
                        });
                    }
                } catch (e) {
                }
            },
            selectSession(session) {
                this.currentSessionKey = this.getSessionKey(session);
                this.currentTargetUserId = (session.fromUser == this.currentUserId) ? session.toUser : session.fromUser;
                this.currentIdleId = session.idleId || null;
                this.currentChatTarget = this.getDisplayUser(session);
                this.currentIdle = session.idle || null;
                this.loadSessionDetail(this.currentTargetUserId, this.currentIdleId);
            },
            openSessionByTarget(targetUserId, idleId) {
                // 尝试在现有会话中找到
                let found = null;
                for (let i = 0; i < this.sessionList.length; i++) {
                    const s = this.sessionList[i];
                    const otherId = (s.fromUser == this.currentUserId) ? s.toUser : s.fromUser;
                    const idle = s.idleId || null;
                    if (otherId == targetUserId && (idleId == null || idle == idleId)) {
                        found = s;
                        break;
                    }
                }
                if (found) {
                    this.selectSession(found);
                } else {
                    // 没有历史记录时，仅设置当前会话上下文，等待第一条消息发送
                    this.currentTargetUserId = Number(targetUserId);
                    this.currentIdleId = idleId ? Number(idleId) : null;
                    this.currentChatTarget = null;
                    this.currentIdle = null;
                    this.messageList = [];
                }
            },
            scrollToBottom() {
                const box = this.$refs.messageListRef;
                if (box) {
                    box.scrollTop = box.scrollHeight;
                }
            },
            async handleSend() {
                const content = this.inputContent.trim();
                if (!content) {
                    this.$message.error("发送内容不能为空！");
                    return;
                }
                if (!this.currentTargetUserId) {
                    this.$message.error("请选择会话或从商品详情页发起私聊！");
                    return;
                }
                try {
                    const contentList = content.split(/\r?\n/);
                    let html = contentList[0];
                    for (let i = 1; i < contentList.length; i++) {
                        html += '<br>' + contentList[i];
                    }
                    const res = await this.$api.sendChatMessage({
                        toUser: this.currentTargetUserId,
                        idleId: this.currentIdleId,
                        content: html
                    });
                    if (res.status_code === 1) {
                        this.inputContent = '';
                        await this.loadSessionDetail(this.currentTargetUserId, this.currentIdleId);
                        await this.loadSessionList();
                    } else {
                        this.$message.error("发送失败：" + res.msg);
                    }
                } catch (e) {
                    this.$message.error("发送失败！");
                }
            }
        }
    }
</script>

<style scoped>
    .chat-container {
        min-height: 85vh;
        display: flex;
        border: 1px solid #ebeef5;
        border-radius: 8px;
        overflow: hidden;
    }

    .chat-session-list {
        width: 320px;
        border-right: 1px solid #ebeef5;
        background-color: #fafafa;
        display: flex;
        flex-direction: column;
    }

    .session-list-header {
        padding: 16px;
        font-size: 16px;
        font-weight: 600;
        border-bottom: 1px solid #ebeef5;
    }

    .session-empty {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #909399;
        font-size: 13px;
    }

    .session-item {
        display: flex;
        padding: 10px 16px;
        cursor: pointer;
        transition: background-color .2s;
    }

    .session-item:hover {
        background-color: #f0f2f5;
    }

    .session-item.active {
        background-color: #e6f7ff;
    }

    .session-avatar {
        width: 46px;
        height: 46px;
        border-radius: 50%;
    }

    .session-info {
        flex: 1;
        margin-left: 10px;
        display: flex;
        flex-direction: column;
    }

    .session-top {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 4px;
    }

    .session-name {
        font-size: 14px;
        font-weight: 600;
        color: #303133;
        max-width: 140px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .session-time {
        font-size: 12px;
        color: #909399;
    }

    .session-bottom {
        font-size: 13px;
        color: #606266;
        display: flex;
        align-items: center;
    }

    .session-last {
        max-width: 170px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .session-goods-name {
        margin-left: 4px;
        color: #909399;
        max-width: 110px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .chat-session-panel {
        flex: 1;
        display: flex;
        flex-direction: column;
        background-color: #ffffff;
    }

    .chat-header {
        height: 60px;
        border-bottom: 1px solid #ebeef5;
        display: flex;
        align-items: center;
        padding: 0 20px;
    }

    .chat-header-left {
        display: flex;
        align-items: center;
    }

    .chat-header-text {
        margin-left: 10px;
    }

    .chat-header-name {
        font-size: 15px;
        font-weight: 600;
        color: #303133;
    }

    .chat-header-sub {
        margin-top: 4px;
        font-size: 13px;
        color: #909399;
    }

    .chat-header-empty {
        font-size: 13px;
        color: #909399;
    }

    .chat-message-list {
        flex: 1;
        padding: 16px 20px;
        overflow-y: auto;
        background-color: #f5f7fa;
    }

    .chat-message-empty {
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #909399;
        font-size: 13px;
    }

    .chat-message-row {
        display: flex;
        margin-bottom: 12px;
    }

    .chat-message-row.self {
        flex-direction: row-reverse;
    }

    .chat-message-avatar {
        margin: 0 8px;
    }

    .chat-message-bubble {
        max-width: 60%;
        padding: 10px 12px;
        border-radius: 4px;
        background-color: #ffffff;
        box-shadow: 0 1px 3px rgba(0, 0, 0, .06);
    }

    .chat-message-row.self .chat-message-bubble {
        background-color: #c6e2ff;
    }

    .chat-message-content {
        font-size: 14px;
        color: #303133;
        word-break: break-word;
    }

    .chat-message-time {
        margin-top: 4px;
        font-size: 11px;
        color: #909399;
        text-align: right;
    }

    .chat-input-bar {
        border-top: 1px solid #ebeef5;
        padding: 10px 16px;
        background-color: #ffffff;
    }

    .chat-input-actions {
        display: flex;
        justify-content: flex-end;
        margin-top: 6px;
    }

    @media (max-width: 900px) {
        .chat-container {
            flex-direction: column;
        }

        .chat-session-list {
            width: 100%;
            border-right: none;
            border-bottom: 1px solid #ebeef5;
        }
    }
</style>


