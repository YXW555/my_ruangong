<template>
    <div>
        <app-head :nickname-value="userInfo.nickname"
                  :avatarValue="userInfo.avatar"></app-head>
        <app-body>
            <div v-show="!eidtAddress" class="profile-container">
                <!-- 用户信息卡片 -->
                <el-card class="user-profile-card" shadow="hover">
                    <div class="user-info-container">
                        <div class="user-info-details">
                            <!-- 头像上传区域 -->
                            <el-upload
                                    action="/api/file"
                                    :on-success="fileHandleSuccess"
                                    :file-list="imgFileList"
                                    :show-file-list="false"
                                    accept="image/*"
                                    class="avatar-uploader"
                                :before-upload="beforeAvatarUpload"
                            >
                                <el-avatar 
                                    :size="120" 
                                    :src="userInfo.avatar" 
                                    class="user-avatar">
                                    <i class="el-icon-plus avatar-uploader-icon"></i>
                                </el-avatar>
                                <div class="avatar-hint">点击更换头像</div>
                            </el-upload>

                            <!-- 用户信息文本区域 -->
                            <div class="user-info-details-text">
                                <div class="user-info-details-text-nickname">{{userInfo.nickname}}</div>
                                <div class="user-info-details-text-time">
                                    <i class="el-icon-time"></i> {{userInfo.signInTime}} 加入平台
                                </div>

                                <!-- 功能按钮组 -->
                                <div class="user-info-details-text-edit">
                                    <el-button type="primary" icon="el-icon-edit" round @click="userInfoDialogVisible = true">编辑个人信息</el-button>
                                    <el-button type="warning" icon="el-icon-location" round @click="eidtAddress=true">管理收货地址</el-button>
                                    <el-button 
                                        v-if="userInfo.userRole !== 1" 
                                        type="success" 
                                        icon="el-icon-s-shop" 
                                        round 
                                        @click="goToMerchantApply">
                                        商家认证
                                    </el-button>
                                    <el-button 
                                        v-if="userInfo.userRole === 1" 
                                        type="info" 
                                        icon="el-icon-s-operation" 
                                        round 
                                        @click="goToMerchantManage">
                                        店铺管理
                                    </el-button>
                                    <el-button 
                                        v-if="userInfo.userRole === 1"
                                        type="primary"
                                        icon="el-icon-chat-line-round"
                                        round
                                        @click="goToAutoReply">
                                        自动回复设置
                                    </el-button>
                                    <el-button 
                                        type="warning" 
                                        icon="el-icon-star-on" 
                                        round 
                                        @click="goToMembership">
                                        开通会员
                                    </el-button>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- 编辑个人信息弹窗 -->
                    <el-dialog
                            @close="finishEdit"
                            title="编辑个人信息"
                            :visible.sync="userInfoDialogVisible"
                            width="400px"
                        center
                        class="edit-dialog">
                        <div class="edit-form">
                            <div class="edit-form-item">
                                <div class="edit-tip">昵称</div>
                                <el-input
                                        v-model="userInfo.nickname"
                                        :disabled="notUserNicknameEdit"
                                        placeholder="请输入昵称"
                                        prefix-icon="el-icon-user"
                                    @change="saveUserNickname"
                                    class="nickname-input">
                                    <el-button slot="append" type="warning" icon="el-icon-edit"
                                            @click="notUserNicknameEdit = false">编辑
                                    </el-button>
                                </el-input>
                            </div>

                            <!-- 密码修改区域 -->
                            <div class="edit-form-item password-form-item" v-if="userPasswordEdit">
                                <div class="edit-tip">原密码</div>
                                <el-input v-model="userPassword1" placeholder="请输入原密码" prefix-icon="el-icon-lock" show-password class="password-input"></el-input>
                                <div class="edit-tip">新密码</div>
                                <el-input v-model="userPassword2" placeholder="请输入新密码（6-20位）" prefix-icon="el-icon-lock" show-password class="password-input"></el-input>
                                <div class="edit-tip">确认新密码</div>
                                <el-input v-model="userPassword3" placeholder="请再次输入新密码" prefix-icon="el-icon-lock" show-password class="password-input"></el-input>
                                <div class="edit-actions">
                                    <el-button type="primary" icon="el-icon-check" @click="savePassword">确认修改</el-button>
                                    <el-button icon="el-icon-close" @click="userPasswordEdit = false">取消</el-button>
                                </div>
                            </div>
                            <div class="edit-form-item" v-else>
                                <div class="edit-tip">密码</div>
                                <el-input
                                        value="******"
                                        :disabled="true"
                                        prefix-icon="el-icon-lock"
                                        show-password>
                                    <el-button slot="append" type="warning" icon="el-icon-edit"
                                            @click="userPasswordEdit = true">修改密码
                                    </el-button>
                                </el-input>
                            </div>
                        </div>
                        <span slot="footer" class="dialog-footer">
                            <el-button type="primary" @click="userInfoDialogVisible=false">完成</el-button>
                        </span>
                    </el-dialog>
                </el-card>

                <!-- 内容卡片 -->
                <el-card class="user-content-card" shadow="hover">
                    <div style="margin-bottom:16px;">
                        <el-card shadow="never" style="background:#fbfdff;">
                            <div style="display:flex;align-items:center;gap:12px;">
                                <i class="el-icon-s-opportunity" style="font-size:20px;color:#67C23A"></i>
                                <div>
                                    <div style="font-weight:700;font-size:16px">我的低碳贡献</div>
                                    <div style="color:#909399;margin-top:6px;">
                                        累计参与二手交易：<strong>{{ carbonSummary ? carbonSummary.orderCount : '—' }}</strong> 笔 · 累计减少碳排放：<strong>{{ carbonSummary ? carbonSummary.totalCarbonKg : '—' }}</strong> kg · 相当于节约用电：<strong>{{ carbonSummary ? carbonSummary.equivalentKwh : '—' }}</strong> 度
                                    </div>
                                </div>
                            </div>
                        </el-card>
                    </div>
                    <div class="idle-container">
                        <!-- 标签页 -->
                        <el-tabs v-model="activeName" @tab-click="handleClick" type="border-card" class="custom-tabs">
                            <el-tab-pane label="刚刚发布" name="1">
                                <i class="el-icon-upload tab-icon"></i> 刚刚发布
                            </el-tab-pane>
                            <el-tab-pane label="已经下架" name="2">
                                <i class="el-icon-download tab-icon"></i> 已经下架
                            </el-tab-pane>
                            <el-tab-pane label="我的购物车" name="3">
                                <i class="el-icon-shopping-cart-full tab-icon"></i> 我的购物车
                            </el-tab-pane>
                            <el-tab-pane label="出售记录" name="4">
                                <i class="el-icon-sold-out tab-icon"></i> 出售记录
                            </el-tab-pane>
                            <el-tab-pane label="购买记录" name="5">
                                <i class="el-icon-shopping-bag-1 tab-icon"></i> 购买记录
                            </el-tab-pane>
                        </el-tabs>
                        
                        <!-- 列表内容区域 -->
                        <div class="idle-container-list">
                            <!-- 列表项 -->
                            <div v-for="(item,index) in dataList[activeName-1]" class="idle-container-list-item" :key="index">
                                <div class="idle-container-list-item-detile" @click="toDetails(activeName,item)">
                                    <!-- 商品图片 -->
                                    <el-image
                                            style="width: 100px; height: 100px;"
                                            :src="item.imgUrl"
                                        fit="cover"
                                        class="item-image"
                                        :preview-src-list="[item.imgUrl]">
                                        <div slot="error" class="image-slot">
                                            <i class="el-icon-picture-outline">无图</i>
                                        </div>
                                    </el-image>

                                    <!-- 商品信息 -->
                                    <div class="idle-container-list-item-text">
                                        <div class="idle-container-list-title">
                                            {{item.idleName}}
                                        </div>
                                        <div class="idle-container-list-idle-details" v-html="item.idleDetails">
                                            {{item.idleDetails}}
                                        </div>
                                        <div class="idle-container-list-idle-time">{{item.timeStr}}</div>

                                        <!-- 价格和操作按钮 -->
                                        <div class="idle-item-foot">
                                            <div class="idle-prive">
                                                ￥{{item.idlePrice}}
                                                <span v-if="(activeName==='4'||activeName==='5')" class="order-status-tag" :class="`status-${item.orderStatus}`">
                                                    {{orderStatus[item.orderStatus]}}
                                                </span>
                                            </div>
                                            <el-button v-if="activeName!=='4'&&activeName!=='5'" type="danger" size="mini" slot="reference"
                                                    plain @click.stop="handle(activeName,item,index)">{{handleName[activeName-1]}}
                                            </el-button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- 空状态 -->
                            <div v-if="dataList[activeName-1].length === 0" class="empty-state">
                                <i :class="getEmptyIcon(activeName)" class="empty-icon"></i>
                                <p class="empty-text">{{getEmptyText(activeName)}}</p>
                            </div>
                        </div>
                    </div>
                </el-card>
            </div>
            
            <!-- 地址管理区域 -->
            <div v-show="eidtAddress" class="address-container">
                <el-card class="address-card" shadow="hover">
                    <!-- 头部返回 -->
                    <div slot="header" class="address-header">
                        <el-page-header @back="eidtAddress=false" content="收货地址管理" class="page-header"></el-page-header>
                    </div>
                    
                    <!-- 新增地址卡片 -->
                    <el-card class="new-address-card" shadow="hover">
                        <div slot="header" class="new-address-header">
                            <span><i class="el-icon-plus"></i> 新增收货地址</span>
                            <span class="address-limit-hint">最多添加5个地址</span>
                        </div>
                        
                        <el-form label-position="top" :model="addressInfo" class="address-form" :rules="addressRules" ref="addressForm">
                            <el-row :gutter="20">
                                <el-col :xs="24" :sm="12">
                                    <el-form-item label="收货人姓名" prop="consigneeName">
                                        <el-input 
                                            placeholder="请输入收货人姓名" 
                                            v-model="addressInfo.consigneeName" 
                                            maxlength="10"
                                            prefix-icon="el-icon-user"
                                            show-word-limit
                                            class="address-input">
                                        </el-input>
                                    </el-form-item>
                                </el-col>
                                <el-col :xs="24" :sm="12">
                                    <el-form-item label="手机号码" prop="consigneePhone">
                                        <el-input 
                                            placeholder="请输入收货人手机号" 
                                            v-model="addressInfo.consigneePhone"
                                            prefix-icon="el-icon-mobile-phone"
                                            onkeyup="this.value=this.value.replace(/[^\d.]/g,'');" 
                                            maxlength="11" 
                                            show-word-limit
                                            class="address-input">
                                        </el-input>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            
                            <el-form-item label="收货地址" prop="detailAddress">
                                <el-input 
                                    type="textarea"
                                    :rows="3"
                                    placeholder="请输入完整收货地址，例如：清华大学学生宿舍1号楼101" 
                                    v-model="addressInfo.detailAddress"
                                    maxlength="100" 
                                    show-word-limit
                                    class="address-textarea">
                                </el-input>
                                <div style="margin-top: 5px; color: #909399; font-size: 12px;">
                                    <i class="el-icon-info"></i> 建议格式：学校名称+详细地址，例如："清华大学学生宿舍1号楼101"或"北京大学图书馆附近"
                                </div>
                            </el-form-item>
                            
                            <el-form-item>
                                <el-checkbox v-model="addressInfo.defaultFlag">设置为默认地址</el-checkbox>
                            </el-form-item>
                            
                            <el-form-item class="address-actions">
                                <el-button type="primary" icon="el-icon-check" @click="saveAddress">保存地址</el-button>
                                <el-button icon="el-icon-refresh" @click="resetAddressForm">重置</el-button>
                            </el-form-item>
                        </el-form>
                    </el-card>
                    
                    <!-- 地址列表区域 -->
                    <div class="address-list-section">
                        <div class="address-list-header">
                            <i class="el-icon-location"></i> 已保存的地址
                            <span class="address-count">共 {{addressData.length}} 个</span>
                        </div>
                        
                        <!-- 空地址状态 -->
                        <div v-if="addressData.length === 0" class="empty-address">
                            <i class="el-icon-map-location"></i>
                            <p>您还没有添加任何收货地址</p>
                            <el-button type="primary" icon="el-icon-plus" @click="scrollToAddAddress">添加收货地址</el-button>
                        </div>
                        
                        <!-- 地址列表 -->
                        <el-row :gutter="20" v-else>
                            <el-col :xs="24" :sm="12" :md="8" v-for="(address, index) in addressData" :key="index">
                                <el-card class="address-item" :class="{'default-address': address.defaultFlag}" shadow="hover">
                                    <div class="address-item-header">
                                        <span class="address-name">{{address.consigneeName}}</span>
                                        <span class="address-phone">{{address.consigneePhone}}</span>
                                        <el-tag v-if="address.defaultFlag" size="small" type="success">默认</el-tag>
                                    </div>
                                    
                                    <div class="address-item-content">
                                        <i class="el-icon-location-information"></i>
                                        <span class="address-detail">{{address.detailAddressText}}</span>
                                    </div>
                                    
                                    <div class="address-item-actions">
                                        <el-button 
                                            size="mini" 
                                            type="primary" 
                                            plain
                                            icon="el-icon-edit"
                                            @click="handleEdit(index, address)">
                                            编辑
                                        </el-button>
                                        <el-button 
                                            v-if="!address.defaultFlag"
                                            size="mini" 
                                            type="success" 
                                            plain
                                            icon="el-icon-star-off"
                                            @click="handleSetDefault(index, address)">
                                            设为默认
                                        </el-button>
                                        <el-button 
                                            size="mini" 
                                            type="danger" 
                                            plain
                                            icon="el-icon-delete"
                                            @click="handleDelete(index, address)">
                                            删除
                                        </el-button>
                                    </div>
                                </el-card>
                            </el-col>
                        </el-row>
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
    // import options from '../common/country-data.js' // 已移除，不再使用级联选择器

    export default {
        name: "me",
        components: {
            AppHead,
            AppBody,
            AppFoot
        },
        data() {
            return {
                imgFileList: [],
                addressInfo: {
                    consigneeName: '',
                    consigneePhone: '',
                    provinceName: '',
                    cityName: '',
                    regionName: '',
                    detailAddress: '',
                    defaultFlag: false
                },
                activeName: '1',
                handleName: ['下架', '删除', '取消购物车', '', ''],
            dataList: [[],[],[],[],[]],
                orderStatus: ['待付款', '待发货', '待收货', '已完成', '已取消'],
                userInfoDialogVisible: false,
                notUserNicknameEdit: true,
                userPasswordEdit: false,
                userPassword1: '',
                userPassword2: '',
                userPassword3: '',
                eidtAddress: false,
                // selectedOptions和options已移除，现在直接使用输入框
                userInfo: {
                    accountNumber: "",
                    avatar: "",
                    nickname: "",
                    signInTime: "",
                    userRole: 0,
                },
            addressData: [],
            carbonSummary: null,
            // 表单验证规则
            addressRules: {
                consigneeName: [
                    { required: true, message: '请输入收货人姓名', trigger: 'blur' },
                    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' }
                ],
                consigneePhone: [
                    { required: true, message: '请输入手机号码', trigger: 'blur' },
                    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
                ],
                detailAddress: [
                    { required: true, message: '请输入收货地址', trigger: 'blur' },
                    { min: 5, message: '收货地址不能少于5个字符', trigger: 'blur' },
                    { max: 100, message: '收货地址不能超过100个字符', trigger: 'blur' }
                ]
            }
            };
        },
        created() {
            this.loadUserInfo();
            this.getAddressData();
            this.getIdleItemData();
            this.getMyOrder();
            this.getMySoldIdle();
            this.getMyFavorite();
            this.loadMyCarbon();
        },
        methods: {
        // 头像上传前验证
        beforeAvatarUpload(file) {
            const isImage = file.type.startsWith('image/');
            const isLt2M = file.size / 1024 / 1024 < 2;

            if (!isImage) {
                this.$message.error('上传头像只能是图片格式!');
                return false;
            }
            if (!isLt2M) {
                this.$message.error('上传头像大小不能超过 2MB!');
                return false;
            }
            return true;
        },

        // 滚动到添加地址区域
        scrollToAddAddress() {
            document.querySelector('.new-address-card').scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        },

            loadUserInfo() {
                this.$api.getUserInfo().then(res => {
                    if (res.status_code === 1) {
                        res.data.signInTime = res.data.signInTime.substring(0, 10);
                        // 强制更新全局用户信息
                        this.$globalData.userInfo = res.data;
                        // 更新当前页面的用户信息
                        this.userInfo = res.data;
                    }
                }).catch(() => {
                    // 如果获取失败，尝试从全局数据加载，以防万一
                    this.userInfo = this.$globalData.userInfo;
                });
            },
            loadMyCarbon() {
                this.$api.getMyCarbon().then(res => {
                    if (res.status_code === 1 && res.data) {
                        this.carbonSummary = res.data;
                    }
                }).catch(() => {
                    this.carbonSummary = null;
                });
            },
            getEmptyIcon(tabName) {
                const icons = {
                    '1': 'el-icon-upload',
                    '2': 'el-icon-download',
                    '3': 'el-icon-shopping-cart-full',
                    '4': 'el-icon-sold-out',
                    '5': 'el-icon-shopping-bag-1'
                };
                return icons[tabName] || 'el-icon-document';
            },
            getEmptyText(tabName) {
                const texts = {
                    '1': '您还没有发布任何商品',
                    '2': '没有已下架的商品',
                    '3': '您的购物车是空的',
                    '4': '您还没有出售记录',
                    '5': '您还没有购买记录'
                };
                return texts[tabName] || '暂无数据';
            },
            getMyFavorite(){
                this.$api.getMyFavorite().then(res=>{
                    console.log('getMyFavorite',res);
                    if (res.status_code === 1){
                        for (let i = 0; i < res.data.length; i++) {
                            if (res.data[i].idleItem && res.data[i].idleItem.pictureList) {
                                let pictureList = JSON.parse(res.data[i].idleItem.pictureList);
                                this.dataList[2].push({
                                    favoriteId:res.data[i].id,
                                    id:res.data[i].idleItem.id,
                                    imgUrl:pictureList.length > 0 ? pictureList[0] : '',
                                    idleName:res.data[i].idleItem.idleName,
                                    idleDetails:res.data[i].idleItem.idleDetails,
                                    timeStr:res.data[i].createTime.substring(0, 10) + " " + res.data[i].createTime.substring(11, 19),
                                    idlePrice:res.data[i].idleItem.idlePrice
                                });
                            }
                        }
                    }
                })
            },
            getMySoldIdle(){
                this.$api.getMySoldIdle().then(res=>{
                    if (res.status_code === 1){
                        console.log('getMySoldIdle',res.data);
                        for (let i = 0; i < res.data.length; i++) {
                            if (res.data[i].idleItem && res.data[i].idleItem.pictureList) {
                                let pictureList = JSON.parse(res.data[i].idleItem.pictureList);
                                this.dataList[3].push({
                                    id:res.data[i].id,
                                    imgUrl:pictureList.length > 0 ? pictureList[0] : '',
                                    idleName:res.data[i].idleItem.idleName,
                                    idleDetails:res.data[i].idleItem.idleDetails,
                                    timeStr:res.data[i].createTime.substring(0, 10) + " " + res.data[i].createTime.substring(11, 19),
                                    idlePrice:res.data[i].orderPrice,
                                    orderStatus:res.data[i].orderStatus
                                });
                            }
                        }
                    }
                })
            },
            getMyOrder(){
                this.$api.getMyOrder().then(res=>{
                    if (res.status_code === 1){
                        console.log('getMyOrder',res.data);
                        for (let i = 0; i < res.data.length; i++) {
                            if (res.data[i].idleItem && res.data[i].idleItem.pictureList) {
                                let pictureList = JSON.parse(res.data[i].idleItem.pictureList);
                                this.dataList[4].push({
                                    id:res.data[i].id,
                                    imgUrl:pictureList.length > 0 ? pictureList[0] : '',
                                    idleName:res.data[i].idleItem.idleName,
                                    idleDetails:res.data[i].idleItem.idleDetails,
                                    timeStr:res.data[i].createTime.substring(0, 10) + " " + res.data[i].createTime.substring(11, 19),
                                    idlePrice:res.data[i].orderPrice,
                                    orderStatus:res.data[i].orderStatus
                                });
                            }
                        }
                    }
                })
            },
            getIdleItemData() {
                this.$api.getAllIdleItem().then(res => {
                    console.log(res);
                    if (res.status_code === 1) {
                        for (let i = 0; i < res.data.length; i++) {
                            res.data[i].timeStr = res.data[i].releaseTime.substring(0, 10) + " " + res.data[i].releaseTime.substring(11, 19);
                            let pictureList = JSON.parse(res.data[i].pictureList);
                            res.data[i].imgUrl = pictureList.length > 0 ? pictureList[0] : '';
                            if (res.data[i].idleStatus === 1) {
                                this.dataList[0].push(res.data[i]);
                            } else if (res.data[i].idleStatus === 2) {
                                this.dataList[1].push(res.data[i]);
                            }
                        }
                    }
                })
            },
            getAddressData() {
                this.$api.getAddress().then(res => {
                    if (res.status_code === 1) {
                        let data = res.data;
                        for (let i = 0; i < data.length; i++) {
                            data[i].detailAddressText = data[i].provinceName + data[i].cityName + data[i].regionName + data[i].detailAddress;
                            data[i].defaultAddress = data[i].defaultFlag ? '默认地址' : '设为默认';
                        }
                        console.log(data);
                        this.addressData = data;
                    }
                })
            },
            handleClick(tab, event) {
                console.log(this.activeName);
            },
            saveUserNickname() {
                this.notUserNicknameEdit = true;
                this.$api.updateUserPublicInfo({
                    nickname: this.userInfo.nickname
                }).then(res => {
                    console.log(res);
                    this.$globalData.userInfo.nickname = this.userInfo.nickname;
                this.$message.success('昵称修改成功！');
                })
            },
            savePassword() {
                if (!this.userPassword1 || !this.userPassword2) {
                this.$message.error('密码不能为空！');
                return;
            }

            if (this.userPassword2.length < 6 || this.userPassword2.length > 20) {
                this.$message.error('新密码长度必须在6-20位之间！');
                return;
            }

            if (this.userPassword2 === this.userPassword3) {
                    this.$api.updatePassword({
                        oldPassword: this.userPassword1,
                        newPassword: this.userPassword2
                    }).then(res => {
                        if (res.status_code === 1) {
                            this.userPasswordEdit = false;
                            this.$message({
                                message: '修改成功！',
                                type: 'success'
                            });
                            this.userPassword1 = '';
                            this.userPassword2 = '';
                            this.userPassword3 = '';
                        } else {
                            this.$message.error('旧密码错误，修改失败！');
                        }
                    })
                } else {
                    this.$message.error('两次输入的密码不一致！');
                }
            },
            finishEdit() {
                this.notUserNicknameEdit = true;
                this.userInfoDialogVisible = false;
                this.userPasswordEdit = false;
            },
            // handleAddressChange方法已移除，现在直接使用输入框
            handleEdit(index, row) {
                console.log(index, row);
                this.addressInfo = JSON.parse(JSON.stringify(row));
                // selectedOptions已移除，现在直接使用detailAddress
                this.scrollToAddAddress();
            },
            handleDelete(index, row) {
                console.log(index, row);
                this.$confirm('是否确定删除该地址?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$api.deleteAddress(row).then(res => {
                        if (res.status_code === 1) {
                            this.$message({
                                message: '删除成功！',
                                type: 'success'
                            });
                            this.addressData.splice(index, 1);
                            if (row.defaultFlag && this.addressData.length > 0) {
                                this.addressData[0].defaultFlag = true;
                                this.addressData[0].defaultAddress = '默认地址';
                                this.update({
                                    id: this.addressData[0].id,
                                    defaultFlag: true
                                });
                            }
                        } else {
                            this.$message.error('系统异常，删除失败！')
                        }
                    }).catch(() => {
                        this.$message.error('网络异常！')
                    });
                }).catch(() => {
                });
            },
            handleSetDefault(index, row) {
                console.log(index, row);
            // 取消其他地址的默认状态
            this.addressData.forEach(item => {
                if (item.defaultFlag) {
                    item.defaultFlag = false;
                    this.update({
                        id: item.id,
                        defaultFlag: false
                    });
                }
            });

                row.defaultFlag = true;
                this.update(row);
            },
            toDetails(activeName, item) {
                if (activeName === '4'||activeName === '5') {
                    this.$router.push({path: '/order', query: {id: item.id}});
                } else {
                    this.$router.push({path: '/details', query: {id: item.id}});
                }
            },
            handle(activeName,item,index) {
                console.log(activeName,item,index);
            let confirmText = '';
            if(activeName === '1') confirmText = '是否确认下架该商品？';
            else if(activeName === '2') confirmText = '是否确认删除该商品？';
            else if(activeName === '3') confirmText = '是否确认取消收藏该商品？';

            this.$confirm(confirmText, '提示', {
                    confirmButtonText: '确认',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    if(activeName==='1'){
                        this.$api.updateIdleItem({
                            id:item.id,
                            idleStatus:2
                        }).then(res=>{
                            console.log(res);
                            if(res.status_code===1){
                                this.dataList[0].splice(index,1);
                                item.idleStatus=2;
                                this.dataList[1].unshift(item);
                            this.$message.success('商品已下架！');
                            }else {
                                this.$message.error(res.msg)
                            }
                        });
                    }else if(activeName==='2'){
                        this.$api.updateIdleItem({
                            id:item.id,
                            idleStatus:0
                        }).then(res=>{
                            console.log(res);
                            if(res.status_code===1){
                                this.dataList[1].splice(index,1);
                            this.$message.success('商品已删除！');
                            }else {
                                this.$message.error(res.msg)
                            }
                        });
                    }else if(activeName==='3'){
                        this.$api.deleteFavorite({
                            id: item.favoriteId
                        }).then(res=>{
                            console.log(res);
                            if(res.status_code===1){
                                this.$message({
                                    message: '已取消购物车！',
                                    type: 'success'
                                });
                                this.dataList[2].splice(index,1);
                            }else {
                                this.$message.error(res.msg)
                            }
                        }).catch(e=>{
                        })
                    }
                }).catch(() => {
                });
            },
            fileHandleSuccess(response, file, fileList) {
                console.log("file:", response, file, fileList);
                let imgUrl = response.data;
                this.imgFileList = [];
                this.$api.updateUserPublicInfo({
                    avatar: imgUrl
                }).then(res => {
                    console.log(res);
                    this.userInfo.avatar = imgUrl;
                    this.$globalData.userInfo.avatar = imgUrl;
                this.$message.success('头像上传成功！');
                })
            },
            update(data) {
                this.$api.updateAddress(data).then(res => {
                    if (res.status_code === 1) {
                        this.getAddressData();
                        this.$message({
                            message: '修改成功！',
                            type: 'success'
                        });
                    } else {
                        this.$message.error('系统异常，修改失败！')
                    }
                }).catch(() => {
                    this.$message.error('网络异常！')
                })
            },
            saveAddress() {
            // 表单验证
            this.$refs.addressForm.validate((valid) => {
                if (!valid) {
                    return false;
                }

                if (this.addressInfo.id) {
                    console.log('update:', this.addressInfo);
                    this.update(this.addressInfo);
                    this.resetAddressForm();
                } else {
                    if (this.addressData.length >= 5) {
                        this.$message.error('已达到最大地址数量！')
                    } else {
                        console.log(this.addressInfo);
                        this.$api.addAddress(this.addressInfo).then(res => {
                            if (res.status_code === 1) {
                                this.getAddressData();
                                this.$message({
                                    message: '新增成功！',
                                    type: 'success'
                                });
                                this.resetAddressForm();
                            } else {
                                this.$message.error('系统异常，新增失败！')
                            }
                        }).catch(e => {
                            this.$message.error('网络异常！')
                        })
                    }
                }
            });
            },
            resetAddressForm() {
                this.addressInfo = {
                    consigneeName: '',
                    consigneePhone: '',
                    provinceName: '',
                    cityName: '',
                    regionName: '',
                    detailAddress: '',
                    defaultFlag: false
                };
                // selectedOptions已移除
                if (this.$refs.addressForm) {
                    this.$refs.addressForm.resetFields();
                }
            },
            goToMerchantApply() {
                this.$router.push('/merchant/apply');
            },
            goToMerchantManage() {
                this.$router.push('/merchant/manage');
            },
            goToMembership() {
                this.$router.push('/membership');
        },
        goToAutoReply() {
            console.log('点击自动回复按钮，准备跳转到 /auto-reply');
            // 直接使用push，不使用replace
            const targetPath = '/auto-reply';
            console.log('准备跳转到:', targetPath);
            
            // 先检查路由是否存在
            const route = this.$router.resolve(targetPath);
            console.log('路由解析结果:', route);
            
            // 执行跳转
            this.$router.push(targetPath).then(() => {
                console.log('路由跳转成功，当前路径:', this.$route.path);
            }).catch(err => {
                console.error('路由跳转失败:', err);
                console.error('错误详情:', err.message, err.stack);
                this.$message.error('页面跳转失败: ' + (err.message || '未知错误'));
            });
            }
        }
    }
</script>

<style scoped>
/* 全局容器样式 */
    .profile-container {
        padding: 20px;
    max-width: 1400px;
    margin: 0 auto;
}

/* 用户信息卡片 */
.user-profile-card {
    margin-bottom: 24px;
    border-radius: 16px;
        overflow: hidden;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

.user-content-card {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

/* 用户信息容器 */
    .user-info-container {
        width: 100%;
    padding: 30px 20px;
        display: flex;
        align-items: center;
    justify-content: center;
    }

    .user-info-details {
        display: flex;
        align-items: center;
        width: 100%;
    max-width: 1000px;
    }
    
/* 头像上传样式 */
    .avatar-uploader {
        position: relative;
        cursor: pointer;
    transition: all 0.3s ease;
    margin-right: 40px;
    }
    
    .avatar-uploader:hover {
        transform: scale(1.05);
    }
    
    .user-avatar {
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
    border: 4px solid #ffffff;
    transition: all 0.3s ease;
}

.user-avatar:hover {
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    }
    
    .avatar-hint {
        position: absolute;
    bottom: -20px;
        left: 50%;
        transform: translateX(-50%);
    font-size: 13px;
        color: #909399;
        opacity: 0;
    transition: opacity 0.3s ease;
    background: #ffffff;
    padding: 2px 8px;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
    
    .avatar-uploader:hover .avatar-hint {
        opacity: 1;
    }

    .avatar-uploader-icon {
    font-size: 32px;
        color: #8c939d;
        width: 120px;
        height: 120px;
        line-height: 120px;
        text-align: center;
    }

/* 用户信息文本区域 */
    .user-info-details-text {
    flex: 1;
    }

    .user-info-details-text-nickname {
    font-size: 28px;
        font-weight: 600;
    margin: 0 0 15px 0;
        color: #303133;
    letter-spacing: 0.5px;
    }

    .user-info-details-text-time {
    font-size: 15px;
    margin-bottom: 20px;
        color: #909399;
        display: flex;
        align-items: center;
    }
    
    .user-info-details-text-time i {
    margin-right: 8px;
    color: #409EFF;
    }

/* 功能按钮组 */
    .user-info-details-text-edit {
        display: flex;
    gap: 12px;
    flex-wrap: wrap;
    margin-top: 15px;
    min-height: 50px; /* 确保有足够高度显示按钮 */
}

.user-info-details-text-edit .el-button {
    padding: 10px 20px;
    border-radius: 50px;
    font-weight: 500;
    transition: all 0.3s ease;
}

.user-info-details-text-edit .el-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 编辑弹窗样式 */
.edit-dialog {
    border-radius: 12px;
    }
    
    .edit-form {
    padding: 15px 0;
    }
    
    .edit-form-item {
    margin-bottom: 24px;
    }
    
    .edit-tip {
        font-size: 14px;
    margin: 0 0 8px 0;
        color: #606266;
    font-weight: 500;
}

.nickname-input, .password-input {
    border-radius: 8px;
}

.password-form-item {
    background: #f8f9fa;
    padding: 15px;
    border-radius: 8px;
    }
    
    .edit-actions {
    margin-top: 20px;
        display: flex;
        justify-content: flex-end;
    gap: 12px;
    }
    
/* 标签页样式 */
    .custom-tabs {
    margin-bottom: 24px;
    border-radius: 12px;
        overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.custom-tabs .el-tabs__header {
    background: #f8f9fa;
    margin: 0;
    padding: 0 15px;
}

.custom-tabs .el-tabs__item {
    padding: 0 20px;
    height: 50px;
    line-height: 50px;
    font-size: 15px;
    font-weight: 500;
}

.custom-tabs .el-tabs__item.is-active {
    color: #409EFF;
    }
    
    .tab-icon {
    margin-right: 8px;
    font-size: 16px;
    }
    
/* 空状态样式 */
    .empty-state {
    padding: 80px 0;
        text-align: center;
        color: #909399;
    background: #f8f9fa;
    border-radius: 12px;
    margin: 20px;
    }
    
    .empty-icon {
    font-size: 72px;
        color: #c0c4cc;
    margin-bottom: 16px;
    transition: all 0.3s ease;
}

.empty-state:hover .empty-icon {
    color: #409EFF;
    transform: scale(1.1);
    }
    
    .empty-text {
    font-size: 16px;
    margin-bottom: 24px;
    color: #606266;
}

.empty-action-btn {
    border-radius: 50px;
    padding: 10px 24px;
    font-weight: 500;
}

/* 列表容器 */
    .idle-container-list {
    min-height: 50vh;
    padding: 10px;
    }

/* 列表项样式 */
    .idle-container-list-item {
    border-bottom: 1px solid #f0f0f0;
        cursor: pointer;
    transition: all 0.3s ease;
    padding: 15px 0;
    }
    
    .idle-container-list-item:hover {
    background-color: #f8f9fa;
    transform: translateX(5px);
    }
    
    .idle-container-list-item:last-child {
        border-bottom: none;
    }

    .idle-container-list-item-detile {
        height: 120px;
        display: flex;
        align-items: center;
    padding: 10px 15px;
}

/* 商品图片 */
.item-image {
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
}

.item-image:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
    }

    .idle-container-list-item-text {
    margin-left: 20px;
        height: 100px;
        flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    }

    .idle-container-list-title {
        font-weight: 600;
        font-size: 18px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        color: #303133;
    }

    .idle-container-list-idle-details {
        font-size: 14px;
        color: #606266;
        padding-top: 5px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }

    .idle-container-list-idle-time {
        font-size: 13px;
        color: #909399;
    }

    .idle-item-foot {
        width: 100%;
        display: flex;
        justify-content: space-between;
        align-items: center;
    margin-top: 5px;
    }
    
    .idle-prive {
    font-size: 18px;
        color: #f56c6c;
        font-weight: 600;
    display: flex;
            align-items: center;
    gap: 10px;
}

/* 订单状态标签 */
.order-status-tag {
    font-size: 12px;
    padding: 2px 8px;
    border-radius: 4px;
    font-weight: normal;
}

.status-0 { background: #fff2f0; color: #f56c6c; border: 1px solid #ffccc7; }
.status-1 { background: #f0f9ff; color: #409EFF; border: 1px solid #b3d8ff; }
.status-2 { background: #f0f9ff; color: #409EFF; border: 1px solid #b3d8ff; }
.status-3 { background: #f0f9f0; color: #67c23a; border: 1px solid #b3e19d; }
.status-4 { background: #f5f5f5; color: #909399; border: 1px solid #dcdfe6; }

/* 地址管理区域 */
.address-container {
    padding: 20px;
    max-width: 1400px;
    margin: 0 auto;
    }

    .address-card {
    border-radius: 16px;
        overflow: hidden;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

.page-header {
    --el-page-header-text-color: #303133;
    --el-page-header-font-size: 18px;
    }

    .new-address-card {
        margin-bottom: 30px;
    border-radius: 12px;
    overflow: hidden;
    }

    .new-address-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
    font-weight: 600;
    color: #303133;
    }

    .address-limit-hint {
        font-size: 12px;
        color: #909399;
    font-weight: normal;
    }

    .address-form {
        padding: 10px 0;
    }

.address-input, .address-cascader, .address-textarea {
    border-radius: 8px;
}

    .address-actions {
        display: flex;
        justify-content: flex-end;
    margin-top: 20px;
    gap: 10px;
    }

/* 地址列表区域 */
    .address-list-section {
        margin-top: 20px;
    }

    .address-list-header {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 20px;
        color: #303133;
        padding-bottom: 10px;
    border-bottom: 2px solid #f0f0f0;
    display: flex;
    align-items: center;
    }

    .address-count {
        font-size: 13px;
        color: #909399;
        margin-left: 10px;
        font-weight: normal;
    }

/* 空地址状态 */
    .empty-address {
        text-align: center;
    padding: 60px 0;
        color: #909399;
    background: #f8f9fa;
    border-radius: 12px;
    margin: 20px 0;
    }

    .empty-address i {
    font-size: 72px;
        color: #c0c4cc;
    margin-bottom: 16px;
    transition: all 0.3s ease;
}

.empty-address:hover i {
    color: #409EFF;
    transform: scale(1.1);
}

.empty-address p {
    font-size: 16px;
    margin-bottom: 24px;
    color: #606266;
}

/* 地址项样式 */
    .address-item {
        margin-bottom: 20px;
    transition: all 0.3s ease;
    border-radius: 12px;
    overflow: hidden;
    height: 100%;
    }

    .address-item:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
    }

    .default-address {
    border: 2px solid #67c23a;
    }

    .address-item-header {
        display: flex;
        align-items: center;
        margin-bottom: 15px;
    flex-wrap: wrap;
    gap: 8px;
    }

    .address-name {
        font-weight: 600;
        color: #303133;
    font-size: 16px;
    }

    .address-phone {
        color: #606266;
    font-size: 14px;
    }

    .address-item-content {
    padding: 5px 0;
        display: flex;
        align-items: flex-start;
    margin-bottom: 15px;
    }

    .address-item-content i {
        color: #409EFF;
    margin-right: 8px;
        font-size: 16px;
        margin-top: 2px;
    }

    .address-detail {
        color: #606266;
    line-height: 1.6;
        word-break: break-all;
    font-size: 14px;
    }

    .address-item-actions {
        margin-top: 15px;
        display: flex;
        justify-content: flex-end;
    gap: 8px;
}

.address-item-actions .el-button {
    border-radius: 20px;
}

/* 响应式适配 */
@media (max-width: 768px) {
    .profile-container, .address-container {
        padding: 10px;
    }

    .user-info-details {
        flex-direction: column;
        align-items: center;
        text-align: center;
    }

    .avatar-uploader {
        margin-right: 0;
        margin-bottom: 20px;
    }

    .user-info-details-text {
        margin-left: 0;
        width: 100%;
    }

    .user-info-details-text-edit {
        justify-content: center;
        gap: 8px;
    }

    .user-info-details-text-edit .el-button {
        padding: 8px 16px;
        font-size: 12px;
    }

    .custom-tabs .el-tabs__item {
        padding: 0 10px;
        font-size: 14px;
    }

    .idle-container-list-item-detile {
        height: auto;
        flex-direction: column;
        align-items: flex-start;
    }

    .item-image {
        width: 100%;
        height: auto;
        margin-bottom: 10px;
    }

    .idle-container-list-item-text {
        margin-left: 0;
        height: auto;
        width: 100%;
    }

    .idle-item-foot {
        margin-top: 10px;
        flex-direction: column;
        align-items: flex-start;
        gap: 10px;
    }

    .address-item-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 5px;
    }

    .address-item-actions {
        justify-content: flex-start;
        flex-wrap: wrap;
    }
}

@media (max-width: 480px) {
    .user-info-details-text-edit {
        flex-direction: column;
    }

    .user-info-details-text-edit .el-button {
        width: 100%;
        margin-bottom: 8px;
    }

    .empty-icon, .empty-address i {
        font-size: 48px;
    }

    .empty-text, .empty-address p {
        font-size: 14px;
    }
    }
</style>