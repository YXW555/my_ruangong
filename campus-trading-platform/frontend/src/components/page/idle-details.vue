<template>
    <div>
        <app-head></app-head>
        <app-body>
            <div class="idle-details-container">
                <!-- 商品详情卡片 -->
                <el-card class="product-card" shadow="hover">
                    <!-- 用户信息与价格区域 -->
                    <div class="details-header">
                        <div class="details-header-user-info">
                            <el-avatar 
                                :size="80" 
                                :src="idleItemInfo.user.avatar" 
                                class="user-avatar">
                            </el-avatar>
                            <div class="user-info-text">
                                <div class="details-header-user-info-nickname">{{idleItemInfo.user.nickname}}</div>
                                <div class="details-header-user-info-time">
                                    <i class="el-icon-time"></i> {{idleItemInfo.user.signInTime.substring(0,10)}} 加入平台
                                </div>
                                <div class="details-header-user-info-rating" v-if="sellerRatingStats">
                                    <el-rate
                                        v-model="sellerRatingStats.averageRating"
                                        disabled
                                        show-score
                                        text-color="#ff9900"
                                        score-template="{value}">
                                    </el-rate>
                                    <span class="rating-count">({{sellerRatingStats.ratingCount}}条评价)</span>
                                </div>
                                <div class="details-header-user-info-rating" v-else-if="sellerRatingStats === null">
                                    <span class="no-rating">暂无评价</span>
                                </div>
                            </div>
                        </div>
                        <div class="details-header-buy" :class="{'owner-controls': isMaster}">
                            <div v-show="idleItemInfo.idlePrice !== 0" class="product-price">
                                <span class="price-symbol">¥</span>{{idleItemInfo.idlePrice}}
                            </div>
                            <div v-if="!isMaster && idleItemInfo.idleStatus!==1" class="product-status">
                                <i class="el-icon-warning-outline"></i> 闲置已下架或删除
                            </div>
                            <div class="action-buttons">
                                <el-button 
                                    v-show="idleItemInfo.idlePrice > 0" 
                                    v-if="!isMaster && idleItemInfo.idleStatus===1" 
                                    type="danger" 
                                    icon="el-icon-shopping-cart-2" 
                                    round 
                                    @click="buyButton(idleItemInfo)">
                                    立即购买
                                </el-button>
                                <el-button 
                                    v-show="idleItemInfo.idlePrice > 0" 
                                    v-if="!isMaster && idleItemInfo.idleStatus===1" 
                                    :type="isFavorite ? 'warning' : 'primary'" 
                                    :icon="isFavorite ? 'el-icon-star-on' : 'el-icon-star-off'" 
                                    round 
                                    @click="favoriteButton(idleItemInfo)">
                                    {{isFavorite ? '取消购物车' : '加入购物车'}}
                                </el-button>
                                <el-button
                                        v-if="!isMaster && idleItemInfo.idleStatus===1"
                                        type="primary"
                                        icon="el-icon-chat-dot-round"
                                        round
                                        @click="startChat">
                                    私聊
                                </el-button>
                                <el-button 
                                    v-if="isMaster && idleItemInfo.idleStatus===1" 
                                    type="danger" 
                                    icon="el-icon-download" 
                                    round 
                                    @click="changeStatus(idleItemInfo,2)">
                                    下架
                                </el-button>
                                <el-button 
                                    v-if="isMaster && idleItemInfo.idleStatus===2" 
                                    type="success" 
                                    icon="el-icon-upload2" 
                                    round 
                                    @click="changeStatus(idleItemInfo,1)">
                                    重新上架
                                </el-button>
                                <el-button 
                                    v-if="isMaster && idleItemInfo.idleStatus===1 && canPin" 
                                    :type="idleItemInfo.isPinned ? 'warning' : 'primary'" 
                                    :icon="idleItemInfo.isPinned ? 'el-icon-top' : 'el-icon-top'" 
                                    round 
                                    @click="handlePinItem">
                                    {{idleItemInfo.isPinned ? '取消置顶' : '置顶商品'}}
                                </el-button>
                            </div>
                        </div>
                    </div>

                    <!-- 分隔线 -->
                    <el-divider></el-divider>

                    <!-- 商品详情区域 -->
                    <div class="details-info">
                        <div class="details-info-title">
                            <span class="product-name">{{idleItemInfo.idleName}}</span>
                            <el-tag 
                                v-if="idleItemInfo.isPinned" 
                                type="warning" 
                                size="small" 
                                class="pin-tag">
                                <i class="el-icon-top"></i> 置顶中
                            </el-tag>
                            <el-tag 
                                v-if="idleItemInfo.idleLabel" 
                                :type="getTagType(idleItemInfo.idleLabel)" 
                                size="medium" 
                                class="product-tag">
                                {{getCategoryName(idleItemInfo.idleLabel)}}
                            </el-tag>
                        </div>
                        
                        <div class="product-location" v-if="idleItemInfo.idlePlace">
                            <i class="el-icon-location"></i> {{idleItemInfo.idlePlace}}
                        </div>
                        
                        <div class="details-info-main" v-html="idleItemInfo.idleDetails">
                            {{idleItemInfo.idleDetails}}
                        </div>
                    </div>

                    <!-- 商品图片区域 -->
                    <div class="details-picture">
                        <el-carousel 
                            v-if="idleItemInfo.pictureList && idleItemInfo.pictureList.length > 0" 
                            :interval="4000" 
                            type="card" 
                            height="400px" 
                            indicator-position="outside"
                            class="image-carousel">
                            <el-carousel-item v-for="(imgUrl, i) in idleItemInfo.pictureList" :key="i">
                                <el-image 
                                    :src="imgUrl" 
                                    fit="contain" 
                                    class="carousel-image"
                                    :preview-src-list="idleItemInfo.pictureList">
                                </el-image>
                            </el-carousel-item>
                        </el-carousel>
                        
                        <div v-else class="no-images">
                            <i class="el-icon-picture-outline"></i>
                            <p>暂无图片</p>
                        </div>
                    </div>
                </el-card>

                <!-- 留言板区域 -->
                <el-card class="message-card" shadow="hover" id="replyMessageLocation">
                    <div slot="header" class="message-header">
                        <span><i class="el-icon-chat-line-round"></i> 留言板</span>
                        <el-badge :value="messageList.length" :max="99" class="message-badge" type="primary"/>
                    </div>
                    
                    <!-- 发送留言区域 -->
                    <div class="message-send">
                        <div v-if="isReply" class="reply-info">
                            <el-tag type="info" closable @close="cancelReply">
                                回复：{{replyData.toMessage}} @{{replyData.toUserNickname}}
                            </el-tag>
                        </div>
                        <el-input
                                type="textarea"
                                :rows="3"
                                placeholder="在此留言提问..."
                                v-model="messageContent"
                                maxlength="200"
                                show-word-limit
                                class="message-input">
                        </el-input>
                        <div class="message-send-button">
                            <el-button type="primary" icon="el-icon-s-promotion" @click="sendMessage" round>发送留言</el-button>
                        </div>
                    </div>
                    
                    <!-- 留言列表 -->
                    <div class="message-list">
                        <div v-if="messageList.length === 0" class="no-messages">
                            <i class="el-icon-chat-dot-square"></i>
                            <p>暂无留言，快来留言吧!</p>
                        </div>
                        
                        <el-timeline v-else>
                            <el-timeline-item
                                v-for="(mes, index) in messageList"
                                :key="index"
                                :timestamp="mes.createTime"
                                placement="top"
                                :color="index % 2 === 0 ? '#409EFF' : '#67C23A'">
                                
                                <el-card class="message-item-card" shadow="hover">
                                    <div class="message-container-list">
                                        <div class="message-container-list-left">
                                            <el-avatar
                                                :size="50"
                                                :src="mes.fromU.avatar"
                                                class="message-avatar">
                                            </el-avatar>
                                            <div class="message-container-list-text">
                                                <div class="message-nickname">
                                                    {{mes.fromU.nickname}}
                                                    <span v-if="mes.toU.nickname" class="reply-to">
                                                        回复 <strong>@{{mes.toU.nickname}}</strong>：
                                                        <span class="quoted-message">{{mes.toM.content.substring(0,10)}}{{mes.toM.content.length>10?'...':''}}</span>
                                                    </span>
                                                </div>
                                                <div class="message-content" v-html="mes.content">{{mes.content}}</div>
                                            </div>
                                        </div>
                                        <div class="message-container-list-right">
                                            <el-button type="text" icon="el-icon-chat-dot-round" @click="replyMessage(index)">回复</el-button>
                                        </div>
                                    </div>
                                </el-card>
                            </el-timeline-item>
                        </el-timeline>
                    </div>
                </el-card>
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
        name: "idle-details",
        components: {
            AppHead,
            AppBody,
            AppFoot
        },
        data() {
            return {
                messageContent:'',
                toUser:null,
                toMessage:null,
                isReply:false,
                replyData:{
                    toUserNickname:'',
                    toMessage:''
                },
                messageList:[],
                idleItemInfo:{
                    id:'',
                    idleName:'',
                    idleDetails:'',
                    pictureList:[],
                    idlePrice:0,
                    idlePlace:'',
                    idleLabel:'',
                    idleStatus:-1,
                    userId:'',
                    user:{
                        avatar:'',
                        nickname:'',
                        signInTime:''
                    },
                },
                isMaster:false,
                isFavorite:true,
                favoriteId:0,
                sellerRatingStats: null, // 卖家信誉评分统计 {averageRating, ratingCount}
                categories: {
                    '1': '数码科技',
                    '2': '生活用品',
                    '3': '运动相关',
                    '4': '图书笔记',
                    '5': '公告'
                },
                membershipStatus: null // 会员状态
            };
        },
        computed: {
            canPin() {
                // 检查用户是否是会员且可以置顶
                return this.membershipStatus && this.membershipStatus.isValid && this.membershipStatus.canPin;
            }
        },
        created(){
            // 加载会员状态
            this.loadMembershipStatus();
            
            let id=this.$route.query.id;
            this.$api.getIdleItem({
                id:id
            }).then(res=>{
                console.log(res);
                if(res.data){
                    let list=res.data.idleDetails.split(/\r?\n/);
                    let str='';
                    for(let i=0;i<list.length;i++){
                        str+='<p>'+list[i]+'</p>';
                    }
                    res.data.idleDetails=str;
                    res.data.pictureList=JSON.parse(res.data.pictureList);
                    this.idleItemInfo=res.data;
                    console.log(this.idleItemInfo);
                    let userId=this.getCookie('shUserId');
                    console.log('userid',userId)
                    if(userId == this.idleItemInfo.userId){
                        console.log('isMaster');
                        this.isMaster=true;
                    }
                    this.checkFavorite();
                    this.getAllIdleMessage();
                    // 加载卖家信誉评分
                    this.loadSellerRating();
                }
                $('html,body').animate({
                    scrollTop: 0
                }, {duration: 500, easing: "swing"});
            });
        },
        methods: {
            getCategoryName(label) {
                return this.categories[label] || '其他';
            },
            getTagType(label) {
                const types = {
                    '1': 'primary',
                    '2': 'success',
                    '3': 'warning',
                    '4': 'info',
                    '5': 'danger'
                };
                return types[label] || 'info';
            },
            getAllIdleMessage(){
                this.$api.getAllIdleMessage({
                    idleId:this.idleItemInfo.id
                }).then(res=>{
                    console.log('getAllIdleMessage',res.data);
                    if(res.status_code===1){
                        this.messageList=res.data;
                    }
                }).catch(()=>{
                })
            },
            checkFavorite(){
                this.$api.checkFavorite({
                    idleId:this.idleItemInfo.id
                }).then(res=>{
                    if(!res.data){
                        this.isFavorite=false;
                    }else {
                        this.favoriteId=res.data;
                    }
                })
            },
            getCookie(cname){
                var name = cname + "=";
                var ca = document.cookie.split(';');
                for(var i=0; i<ca.length; i++)
                {
                    var c = ca[i].trim();
                    if (c.indexOf(name)===0) return c.substring(name.length,c.length);
                }
                return "";
            },
            startChat(){
                const userId = this.getCookie('shUserId');
                if(!userId){
                    this.$message.error("请先登录后再发起私聊");
                    this.$router.push('/login');
                    return;
                }
                if(userId == this.idleItemInfo.userId){
                    this.$message.info("这是您自己发布的闲置哦");
                    return;
                }
                this.$router.push({
                    path:'/chat',
                    query:{
                        targetUserId:this.idleItemInfo.userId,
                        idleId:this.idleItemInfo.id
                    }
                });
            },
            replyMessage(index){
                $('html,body').animate({
                    scrollTop: $("#replyMessageLocation").offset().top-100
                }, {duration: 500, easing: "swing"});
                this.isReply=true;
                this.replyData.toUserNickname=this.messageList[index].fromU.nickname;
                this.replyData.toMessage=this.messageList[index].content.substring(0,10)+(this.messageList[index].content.length>10?'...':'');
                this.toUser=this.messageList[index].userId;
                this.toMessage=this.messageList[index].id;
            },
            changeStatus(idle,status){
                this.$api.updateIdleItem({
                    id:idle.id,
                    idleStatus:status
                }).then(res=>{
                    console.log(res);
                    if(res.status_code===1){
                        this.idleItemInfo.idleStatus=status;
                        this.$message({
                            message: status === 1 ? '商品已重新上架' : '商品已下架',
                            type: 'success'
                        });
                    }else {
                        this.$message.error(res.msg)
                    }
                });
            },
            buyButton(idleItemInfo){
                this.$api.addOrder({
                    idleId:idleItemInfo.id,
                    orderPrice:idleItemInfo.idlePrice,
                }).then(res=>{
                    console.log(res);
                    if(res.status_code===1){
                        this.$router.push({path: '/order', query: {id: res.data.id}});
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e=>{

                })
            },
            loadMembershipStatus() {
                this.$api.getMembershipStatus().then(res => {
                    if (res.status_code === 1) {
                        this.membershipStatus = res.data;
                    }
                }).catch(() => {
                    // 静默失败
                });
            },
            handlePinItem() {
                if (!this.canPin) {
                    this.$message.warning('您还不是会员，无法使用置顶功能！');
                    this.$router.push('/membership');
                    return;
                }

                if (this.idleItemInfo.isPinned) {
                    // 取消置顶（这里需要后端支持取消置顶的接口）
                    this.$message.info('取消置顶功能开发中，置顶将在到期后自动取消');
                    return;
                }

                // 置顶商品
                this.$confirm('置顶商品可以让您的商品在列表中优先显示，是否确认置顶？', '确认置顶', {
                    confirmButtonText: '确认',
                    cancelButtonText: '取消',
                    type: 'info'
                }).then(() => {
                    // 默认置顶7天
                    this.$api.pinItem({
                        idleItemId: this.idleItemInfo.id,
                        durationDays: 7
                    }).then(res => {
                        if (res.status_code === 1) {
                            this.$message.success('置顶成功！');
                            // 刷新商品信息
                            this.$api.getIdleItem({
                                id: this.idleItemInfo.id
                            }).then(refreshRes => {
                                if (refreshRes.data) {
                                    refreshRes.data.pictureList = JSON.parse(refreshRes.data.pictureList);
                                    this.idleItemInfo = refreshRes.data;
                                }
                            });
                            // 刷新会员状态
                            this.loadMembershipStatus();
                        } else {
                            this.$message.error(res.msg || '置顶失败');
                        }
                    }).catch(() => {
                        this.$message.error('网络错误，请稍后重试');
                    });
                }).catch(() => {});
            },
            favoriteButton(idleItemInfo){
                if(this.isFavorite){
                    this.$api.deleteFavorite({
                        id: this.favoriteId
                    }).then(res=>{
                        console.log(res);
                        if(res.status_code===1){
                            this.$message({
                                message: '已取消购物车！',
                                type: 'success'
                            });
                            this.isFavorite=false;
                        }else {
                            this.$message.error(res.msg)
                        }
                    }).catch(e=>{
                    })
                }else {
                    this.$api.addFavorite({
                        idleId:idleItemInfo.id
                    }).then(res=>{
                        console.log(res);
                        if(res.status_code===1){
                            this.$message({
                                message: '已加入购物车！',
                                type: 'success'
                            });
                            this.isFavorite=true;
                            this.favoriteId=res.data;
                        }else {
                            this.$message.error(res.msg)
                        }
                    }).catch(e=>{
                    })
                }
            },
            cancelReply(){
                this.isReply=false;
                this.toUser=this.idleItemInfo.userId;
                this.toMessage=null;
                this.replyData.toUserNickname='';
                this.replyData.toMessage='';
            },
            sendMessage(){
                let content=this.messageContent.trim();
                if(this.toUser==null){
                    this.toUser=this.idleItemInfo.userId;
                }
                if(content){
                    let contentList=content.split(/\r?\n/);
                    let contenHtml=contentList[0];
                    for(let i=1;i<contentList.length;i++){
                        contenHtml+='<br>'+contentList[i];
                    }
                    this.$api.sendMessage({
                        idleId:this.idleItemInfo.id,
                        content:contenHtml,
                        toUser:this.toUser,
                        toMessage:this.toMessage
                    }).then(res=>{
                        if(res.status_code===1){
                            this.$message({
                                message: '留言成功！',
                                type: 'success'
                            });
                            this.messageContent='';
                            this.cancelReply();
                            this.getAllIdleMessage();
                        }else {
                            this.$message.error("留言失败！"+res.msg);
                        }
                    }).catch(()=>{
                        this.$message.error("留言失败！");
                    });

                }else{
                    this.$message.error("留言为空！");
                }
            },
            loadSellerRating() {
                if (!this.idleItemInfo.userId) {
                    return;
                }
                this.$api.getSellerStats({
                    sellerId: this.idleItemInfo.userId
                }).then(res => {
                    if (res.status_code === 1 && res.data) {
                        // 确保评分是数字类型，保留一位小数
                        const avgRating = parseFloat(res.data.averageRating || 0);
                        this.sellerRatingStats = {
                            averageRating: avgRating > 0 ? avgRating : 0,
                            ratingCount: res.data.ratingCount || 0
                        };
                    } else {
                        this.sellerRatingStats = null;
                    }
                }).catch(() => {
                    this.sellerRatingStats = null;
                });
            }
        },
    }
</script>

<style scoped>
    .idle-details-container {
        min-height: 85vh;
        padding: 20px 0;
    }

    /* 商品卡片样式 */
    .product-card, .message-card {
        margin-bottom: 30px;
        border-radius: 8px;
        overflow: hidden;
    }
    
    /* 用户信息与价格区域 */
    .details-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10px 0;
        flex-wrap: wrap;
    }

    .details-header-user-info {
        display: flex;
        align-items: center;
    }

    .user-avatar {
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        border: 2px solid #fff;
    }

    .user-info-text {
        margin-left: 15px;
    }

    .details-header-user-info-nickname {
        font-weight: 600;
        font-size: 18px;
        margin-bottom: 10px;
        color: #303133;
    }

    .details-header-user-info-time {
        font-size: 14px;
        color: #909399;
        display: flex;
        align-items: center;
        margin-top: 8px;
    }

    .details-header-user-info-time i {
        margin-right: 5px;
    }

    .details-header-user-info-rating {
        margin-top: 8px;
        display: flex;
        align-items: center;
        font-size: 14px;
    }

    .rating-count {
        margin-left: 8px;
        color: #909399;
        font-size: 13px;
    }

    .no-rating {
        color: #c0c4cc;
        font-size: 13px;
    }

    .details-header-buy {
        display: flex;
        flex-direction: column;
        align-items: flex-end;
    }

    .owner-controls {
        align-items: center;
    }

    .product-price {
        font-size: 24px;
        font-weight: 700;
        color: #f56c6c;
        margin-bottom: 15px;
    }

    .price-symbol {
        font-size: 16px;
        font-weight: normal;
        margin-right: 2px;
    }

    .product-status {
        color: #f56c6c;
        font-size: 16px;
        margin-bottom: 15px;
        display: flex;
        align-items: center;
    }

    .product-status i {
        margin-right: 5px;
    }

    .action-buttons {
        display: flex;
        gap: 10px;
    }

    /* 商品详情区域 */
    .details-info {
        padding: 20px 0;
    }

    .details-info-title {
        display: flex;
        align-items: center;
        margin-bottom: 15px;
    }

    .product-name {
        font-size: 24px;
        font-weight: 600;
        color: #303133;
        margin-right: 10px;
    }

    .product-tag {
        margin-left: 10px;
    }
    
    .pin-tag {
        margin-left: 10px;
        background: #ffc107;
        color: #fff;
        border: none;
    }
    
    .pin-tag i {
        margin-right: 4px;
    }

    .product-location {
        font-size: 14px;
        color: #606266;
        margin-bottom: 20px;
        display: flex;
        align-items: center;
    }

    .product-location i {
        margin-right: 5px;
        color: #e6a23c;
    }

    .details-info-main {
        font-size: 16px;
        color: #606266;
        line-height: 1.8;
        background-color: #f8f9fa;
        padding: 20px;
        border-radius: 4px;
        margin-bottom: 20px;
    }

    /* 商品图片区域 */
    .details-picture {
        margin: 10px 0 30px;
    }

    .image-carousel {
        margin: 0 auto;
    }

    .carousel-image {
        height: 100%;
        width: 100%;
    }

    .no-images {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        background-color: #f5f7fa;
        height: 300px;
        color: #909399;
        border-radius: 4px;
    }

    .no-images i {
        font-size: 50px;
        margin-bottom: 10px;
    }
    
    /* 留言板样式 */
    .message-header {
        display: flex;
        align-items: center;
        font-size: 18px;
        font-weight: 600;
    }

    .message-badge {
        margin-left: 10px;
    }

    .message-send {
        margin-bottom: 30px;
    }

    .reply-info {
        margin-bottom: 10px;
    }

    .message-input {
        margin-bottom: 10px;
    }

    .message-send-button {
        display: flex;
        justify-content: flex-end;
    }

    /* 留言列表样式 */
    .message-list {
        padding: 10px 0;
    }

    .no-messages {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 150px;
        color: #909399;
    }

    .no-messages i {
        font-size: 40px;
        margin-bottom: 10px;
    }

    .message-item-card {
        margin-bottom: 10px;
    }

    .message-container-list {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
    }

    .message-container-list-left {
        display: flex;
        flex: 1;
    }

    .message-avatar {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        border: 2px solid #fff;
    }

    .message-container-list-text {
        margin-left: 15px;
        flex: 1;
    }

    .message-nickname {
        font-weight: 600;
        font-size: 16px;
        color: #303133;
        margin-bottom: 8px;
    }

    .reply-to {
        font-size: 14px;
        font-weight: normal;
        color: #606266;
    }

    .quoted-message {
        color: #909399;
        font-style: italic;
    }

    .message-content {
        font-size: 15px;
        color: #606266;
        margin-bottom: 10px;
        line-height: 1.6;
        word-break: break-word;
    }

    .message-container-list-right {
        min-width: 60px;
        display: flex;
        justify-content: flex-end;
        margin-left: 15px;
    }

    /* 响应式调整 */
    @media (max-width: 768px) {
        .details-header {
            flex-direction: column;
            align-items: flex-start;
        }

        .details-header-buy {
            margin-top: 20px;
            align-items: flex-start;
            width: 100%;
        }

        .action-buttons {
            flex-wrap: wrap;
            margin-top: 10px;
        }

        .message-container-list {
            flex-direction: column;
        }

        .message-container-list-right {
            margin-top: 10px;
            margin-left: 0;
            align-self: flex-end;
        }
    }
</style>
</style>