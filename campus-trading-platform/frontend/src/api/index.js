import request from '../utils/request';

const api = {

    // 用户功能 对应usercontroller
    userLogin(query) {
        return request({
            url: '/user/login',
            method: 'get',
            params: query
        });
    },
    logout(query) {
        return request({
            url: '/user/logout',
            method: 'get',
            params: query
        });
    },
    signIn(data) {
        return request({
            url: '/user/sign-in',
            method: 'post',
            data: data
        });
    },
    getUserInfo(query) {
        return request({
            url: '/user/info',
            method: 'get',
            params: query
        });
    },
    updateUserPublicInfo(data) {
        return request({
            url: '/user/info',
            method: 'post',
            data: data
        });
    },
    updatePassword(query) {
        return request({
            url: '/user/password',
            method: 'get',
            params: query
        });
    },

    //  地址功能  对应addresscontroller
    addAddress(data) {
        return request({
            url: '/address/add',
            method: 'post',
            data: data
        });
    },
    getAddress(query) {
        return request({
            url: '/address/info',
            method: 'get',
            params: query
        });
    },
    updateAddress(data) {
        return request({
            url: '/address/update',
            method: 'post',
            data: data
        });
    },
    deleteAddress(data) {
        return request({
            url: '/address/delete',
            method: 'post',
            data: data
        });
    },

    // 商品操作   对应idleitemController
    addIdleItem(data) {
        return request({
            url: '/idle/add',
            method: 'post',
            data: data
        });
    },
    getIdleItem(query) {
        return request({
            url: '/idle/info',
            method: 'get',
            params: query
        });
    },
    getAllIdleItem(query) {
        return request({
            url: '/idle/all',
            method: 'get',
            params: query
        });
    },
    findIdleTiem(query) {
        return request({
            url: '/idle/find',
            method: 'get',
            params: query
        });
    },
    findIdleTiemByLable(query) {
        return request({
            url: '/idle/lable',
            method: 'get',
            params: query
        });
    },
    updateIdleItem(data) {
        return request({
            url: '/idle/update',
            method: 'post',
            data: data
        });
    },
    pinItem(data) {
        return request({
            url: '/idle/pin',
            method: 'post',
            params: data
        });
    },

    // 个人中心的功能  ordercontroller
    addOrder(data) {
        return request({
            url: '/order/add',
            method: 'post',
            data: data
        });
    },
    getOrder(query) {
        return request({
            url: '/order/info',
            method: 'get',
            params: query
        });
    },
    updateOrder(data) {
        return request({
            url: '/order/update',
            method: 'post',
            data: data
        });
    },
    getMyOrder(query) {
        return request({
            url: '/order/my',
            method: 'get',
            params: query
        });
    },
    getMySoldIdle(query) {
        return request({
            url: '/order/my-sold',
            method: 'get',
            params: query
        });
    },
    // 订单地址功能
    getOrderAddress(query) {
        return request({
            url: '/order-address/info',
            method: 'get',
            params: query
        });
    },

    // 订单的地址信息   orderAddressController
    addOrderAddress(data) {
        return request({
            url: '/order-address/add',
            method: 'post',
            data: data
        });
    },
    updateOrderAddress(data) {
        return request({
            url: '/order-address/update',
            method: 'post',
            data: data
        });
    },

    // 收藏功能就是购物车    favoriteController
    addFavorite(data) {
        return request({
            url: '/favorite/add',
            method: 'post',
            data: data
        });
    },
    getMyFavorite(query) {
        return request({
            url: '/favorite/my',
            method: 'get',
            params: query
        });
    },
    deleteFavorite(query) {
        return request({
            url: '/favorite/delete',
            method: 'get',
            params: query
        });
    },
    checkFavorite(query) {
        return request({
            url: '/favorite/check',
            method: 'get',
            params: query
        });
    },

    // 留言功能   messagecontroller
    sendMessage(data) {
        return request({
            url: '/message/send',
            method: 'post',
            data: data
        });
    },
    getMessage(query) {
        return request({
            url: '/message/info',
            method: 'get',
            params: query
        });
    },
    getAllIdleMessage(query) {
        return request({
            url: '/message/idle',
            method: 'get',
            params: query
        });
    },
    getAllMyMessage(query) {
        return request({
            url: '/message/my',
            method: 'get',
            params: query
        });
    },
    deleteMessage(query) {
        return request({
            url: '/message/delete',
            method: 'get',
            params: query
        });
    },

    // 站内信私聊功能   ChatMessageController
    sendChatMessage(data) {
        return request({
            url: '/chat/send',
            method: 'post',
            data: data
        });
    },
    sendChatImage(data) {
        return request({
            url: '/chat/sendImage',
            method: 'post',
            data: data
        });
    },
    getChatSessionList(query) {
        return request({
            url: '/chat/session/list',
            method: 'get',
            params: query
        });
    },
    getChatSessionDetail(query) {
        return request({
            url: '/chat/session/detail',
            method: 'get',
            params: query
        });
    },
    // 获取未读消息数量（私信）
    getChatUnreadCount(query) {
        return request({
            url: '/chat/unread/count',
            method: 'get',
            params: query
        });
    },

    // 自动回复模板相关操作 ✅ 第一个文件新增的功能
    getAutoReplyTemplateList() {
        return request({
            url: '/auto-reply/template/list',
            method: 'get'
        });
    },
    createOrUpdateAutoReplyTemplate(data) {
        return request({
            url: '/auto-reply/template',
            method: 'post',
            data: data
        });
    },
    updateAutoReplyTemplate(data) {
        return request({
            url: '/auto-reply/template',
            method: 'put',
            data: data
        });
    },
    deleteAutoReplyTemplate(id) {
        return request({
            url: `/auto-reply/template/${id}`,
            method: 'delete'
        });
    },

    // 管理员相关操作
    getGoods(query) {
        return request({
            url: '/admin/idleList',
            method: 'get',
            params: query
        });
    },
    updateGoods(query) {
        return request({
            url: '/admin/updateIdleStatus',
            method: 'get',
            params: query
        });
    },
    getOrderList(query) {
        return request({
            url: '/admin/orderList',
            method: 'get',
            params: query
        });
    },
    deleteOrder(query) {
        return request({
            url: '/admin/deleteOrder',
            method: 'get',
            params: query
        });
    },
    getUserData(query) {
        return request({
            url: '/admin/userList',
            method: 'get',
            params: query
        });
    },
    getUserManage(query) {
        return request({
            url: '/admin/list',
            method: 'get',
            params: query
        });
    },
    updateUserStatus(query){
        return request({
            url: '/admin/updateUserStatus',
            method: 'get',
            params: query
        });
    },
    regAdministrator(data){
        return request({
            url: '/admin/add',
            method: 'post',
            data: data
        });
    },
    adminLogin(query) {
        return request({
            url: '/admin/login',
            method: 'get',
            params: query
        });
    },
    loginOut(query) {
        return request({
            url: '/admin/loginOut',
            method: 'get',
            params: query
        });
    },
    queryIdle(query) {
        return request({
            url: '/admin/queryIdle',
            method: 'get',
            params: query
        });
    },
    queryOrder(query) {
        return request({
            url: '/admin/queryOrder',
            method: 'get',
            params: query
        });
    },
    queryUser(query) {
        return request({
            url: '/admin/queryUser',
            method: 'get',
            params: query
        });
    },
    updateAlipay(query) {
        return request({
            url: '/alipay/pay',
            method: 'get',
            params: query
        });
    },
    // 模拟支付接口
    simulatePay(data) {
        return request({
            url: '/alipay/simulatePay',
            method: 'post',
            params: data
        });
    },

    // 数据统计相关API
    getTradingData(query) {
        return request({
            url: '/statistics/trading-data',
            method: 'get',
            params: query
        });
    },
    getMonthlyStatistics(query) {
        return request({
            url: '/statistics/monthly-statistics',
            method: 'get',
            params: query
        });
    },
    getCategoryStatistics(query) {
        return request({
            url: '/statistics/category-statistics',
            method: 'get',
            params: query
        });
    },
    getUserStatistics(query) {
        return request({
            url: '/statistics/user-statistics',
            method: 'get',
            params: query
        });
    },
    getEnvironmentalBenefits(query) {
        return request({
            url: '/statistics/environmental-benefits',
            method: 'get',
            params: query
        });
    },

    // 评价功能   RatingController
    addRating(data) {
        return request({
            url: '/rating/add',
            method: 'post',
            data: data
        });
    },
    getRatingByOrderId(query) {
        return request({
            url: '/rating/by-order',
            method: 'get',
            params: query
        });
    },
    getRatingsBySellerId(query) {
        return request({
            url: '/rating/by-seller',
            method: 'get',
            params: query
        });
    },
    getSellerStats(query) {
        return request({
            url: '/rating/seller-stats',
            method: 'get',
            params: query
        });
    },

    // 商家认证功能   MerchantApplicationController
    submitMerchantApplication(data) {
        return request({
            url: '/merchant/apply',
            method: 'post',
            data: data
        });
    },
    getMyMerchantApplication(query) {
        return request({
            url: '/merchant/my-application',
            method: 'get',
            params: query
        });
    },
    adminGetPendingApplicationCount(query) {
        return request({
            url: '/merchant/admin/pending-count',
            method: 'get',
            params: query
        });
    },
    adminGetAllApplications(query) {
        return request({
            url: '/merchant/admin/list',
            method: 'get',
            params: query
        });
    },
    adminReviewApplication(data) {
        return request({
            url: '/merchant/admin/review',
            method: 'post',
            params: data
        });
    },

    // 经营性卖家管理功能   MerchantController
    batchAddItems(data) {
        return request({
            url: '/merchant/manage/batch-add',
            method: 'post',
            data: data
        });
    },
    updateStock(data) {
        return request({
            url: '/merchant/manage/update-stock',
            method: 'post',
            data: data
        });
    },
    getShopStatistics(query) {
        return request({
            url: '/merchant/manage/statistics',
            method: 'get',
            params: query
        });
    },
    getShopItems(query) {
        return request({
            url: '/merchant/manage/items',
            method: 'get',
            params: query
        });
    },
    getShopOrders(query) {
        return request({
            url: '/merchant/manage/orders',
            method: 'get',
            params: query
        });
    },

    // 会员功能   MembershipController
    createMembershipOrder(data) {
        return request({
            url: '/membership/create-order',
            method: 'post',
            params: data
        });
    },
    activateMembership(data) {
        return request({
            url: '/membership/activate',
            method: 'post',
            params: data
        });
    },
    getMembershipStatus(query) {
        return request({
            url: '/membership/status',
            method: 'get',
            params: query
        });
    },
    getMembershipOrders(query) {
        return request({
            url: '/membership/orders',
            method: 'get',
            params: query
        });
    },
    // 管理员：会员统计
    getMembershipRevenueStats(query) {
        return request({
            url: '/membership/admin/revenue-stats',
            method: 'get',
            params: query
        });
    },
    getMembershipCountStats(query) {
        return request({
            url: '/membership/admin/count-stats',
            method: 'get',
            params: query
        });
    },

    // 售后服务功能   AfterSaleController ✅ 第二个文件新增核心亮点
    applyAfterSale(data) {
        return request({
            url: '/after-sale/apply',
            method: 'post',
            data: data
        });
    },
    getAfterSaleByOrderId(query) {
        return request({
            url: '/after-sale/by-order',
            method: 'get',
            params: query
        });
    },
    getMyAfterSales(query) {
        return request({
            url: '/after-sale/my',
            method: 'get',
            params: query
        });
    },
    getPendingAfterSales(query) {
        return request({
            url: '/after-sale/pending',
            method: 'get',
            params: query
        });
    },
    reviewAfterSale(data) {
        return request({
            url: '/after-sale/review',
            method: 'post',
            data: data
        });
    },
    // 管理员审核售后申请
    getAdminPendingAfterSales(query) {
        return request({
            url: '/after-sale/admin/pending',
            method: 'get',
            params: query
        });
    },
    getAdminAfterSaleDetail(query) {
        return request({
            url: '/after-sale/admin/detail',
            method: 'get',
            params: query
        });
    },
    adminReviewAfterSale(data) {
        return request({
            url: '/after-sale/admin/review',
            method: 'post',
            data: data
        });
    },

    // 推荐功能   RecommendController ✅ 第二个文件新增核心亮点 (老师要的推荐算法)
    getPersonalizedRecommendations(query) {
        return request({
            url: '/recommend/personalized',
            method: 'get',
            params: query
        });
    },
    getSimilarItems(query) {
        return request({
            url: '/recommend/similar',
            method: 'get',
            params: query
        });
    },
    getHotRecommendations(query) {
        return request({
            url: '/recommend/hot',
            method: 'get',
            params: query
        });
    },

    // 交易纠纷功能   DisputeController ✅ 第二个文件新增核心亮点 (核心业务闭环)
    // 用户申请纠纷
    applyDispute(data) {
        return request({
            url: '/dispute/apply',
            method: 'post',
            data: data
        });
    },
    // 用户查看我的纠纷
    getMyDisputes(query) {
        return request({
            url: '/dispute/my',
            method: 'get',
            params: query
        });
    },
    // 用户查看纠纷详情
    getDisputeInfo(query) {
        return request({
            url: '/dispute/info',
            method: 'get',
            params: query
        });
    },
    // 管理员获取纠纷列表
    adminGetDisputeList(query) {
        return request({
            url: '/dispute/admin/list',
            method: 'get',
            params: query
        });
    },
    // 管理员处理纠纷
    adminHandleDispute(data) {
        return request({
            url: '/dispute/admin/handle',
            method: 'post',
            data: data
        });
    },
};

export default api;