<template>
  <div>
      <app-head></app-head>
      <app-body>
          <div class="payment-success-container">
              <!-- 加载状态 -->
              <div v-if="isLoading" class="loading-container">
                  <el-loading-spinner></el-loading-spinner>
                  <p>正在加载订单信息...</p >
              </div>

              <!-- 错误状态 -->
              <div v-else-if="hasError" class="error-card">
                  <div class="error-icon">
                      <i class="el-icon-circle-close"></i>
                  </div>
                  <h1 class="error-title">订单信息加载失败</h1>
                  <p class="error-desc">请返回重新操作，或联系客服协助处理</p >
                  <el-button type="primary" round @click="goToHome" icon="el-icon-s-home">返回首页</el-button>
              </div>

              <!-- 成功状态 -->
              <div v-else class="success-card">
                  <div class="success-icon">
                      <i class="el-icon-circle-check"></i>
                  </div>
                  <h1 class="success-title">支付成功</h1>

                  <!-- 订单信息 -->
                  <div class="order-info">
                      <p class="order-number">订单号: {{ orderNo || '未知订单' }}</p >
                      <p class="order-amount">支付金额: <span class="price">¥{{ price }}</span></p >
                  </div>

                  <!-- 描述信息 -->
                  <div class="success-description">
                      <p v-if="orderType === 'membership'">
                          会员订单支付成功，<span class="highlight">会员权益已立即生效</span>！
                      </p >
                      <template v-else>
                          <p>您的订单已支付成功，感谢您的购买！</p >
                          <p>卖家将在<span class="highlight">24小时内</span>为您处理订单~</p >
                      </template>
                  </div>

                  <!-- 操作按钮 -->
                  <div class="action-buttons">
                      <el-button
                              v-if="orderType !== 'membership'"
                              type="primary"
                              round
                              @click="goToOrder"
                              icon="el-icon-document"
                              :disabled="!orderId"
                              class="primary-btn"
                      >
                          查看订单
                      </el-button>
                      <el-button
                              v-if="orderType === 'membership'"
                              type="primary"
                              round
                              @click="goToMembership"
                              icon="el-icon-star-on"
                              class="primary-btn"
                      >
                          查看会员
                      </el-button>
                      <el-button
                              round
                              @click="goToHome"
                              icon="el-icon-s-home"
                              class="secondary-btn"
                      >
                          返回首页
                      </el-button>
                  </div>

                  <!-- 倒计时提示 -->
                  <div class="auto-redirect" v-if="countdown > 0">
                      <p>将在 <span class="countdown-number">{{ countdown }}</span> 秒后自动返回首页</p >
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
  name: 'AlipaySuccess',
  components: {
      AppHead,
      AppBody,
      AppFoot
  },
  data() {
      return {
          orderNo: '未知订单',
          price: '0.00',
          orderId: '',
          orderType: '',
          isLoading: true, // 加载状态
          hasError: false, // 错误状态
          countdown: 10, // 自动跳转倒计时
          timer: null // 倒计时定时器
      }
  },
  mounted() {
      this.parseUrlParams();
      this.initCountdown(); // 初始化自动跳转
  },
  beforeDestroy() {
      // 清除定时器
      if (this.timer) clearInterval(this.timer);
  },
  methods: {
      // 解析URL参数（增强健壮性）
      parseUrlParams() {
          try {
              // 优先用路由参数，再用URL查询参数
              const params = { ...this.$route.query, ...this.getUrlQueryParams() };
              console.log('合并后的参数:', params);

              // 赋值并校验
              this.orderId = params.id || params.orderId || '';
              this.orderNo = params.no || params.orderNumber || '未知订单';
              this.orderType = params.type || 'normal';

              // 处理价格格式
              const priceNum = parseFloat(params.price || params.amount || 0);
              this.price = isNaN(priceNum) ? '0.00' : priceNum.toFixed(2);

              // 会员订单激活
              if (this.orderType === 'membership' && this.orderNo !== '未知订单') {
                  this.activateMembership();
              }

              this.isLoading = false; // 加载完成
          } catch (error) {
              console.error('解析URL参数出错:', error);
              this.isLoading = false;
              this.hasError = true;
              this.$message.error('订单信息解析失败，请返回重试');
          }
      },
      // 单独提取URL查询参数解析（兼容非路由场景）
      getUrlQueryParams() {
          const queryPart = window.location.href.split('?')[1] || '';
          return queryPart.split('&').reduce((acc, item) => {
              if (item.includes('=')) {
                  const [key, value] = item.split('=');
                  acc[key] = decodeURIComponent(value || '');
              }
              return acc;
          }, {});
      },
      // 激活会员（增加错误提示）
      async activateMembership() {
          try {
              const res = await this.$api.activateMembership({ orderNumber: this.orderNo });
              if (res.status_code === 1) {
                  this.$message.success('会员权益已成功开通！');
                  await this.refreshUserInfo(); // 刷新用户信息（异步处理）
              } else {
                  this.$message.warning('会员开通延迟，请稍后查看会员页面');
              }
          } catch (err) {
              console.error('会员激活失败:', err);
              this.$message.warning('会员权益将稍后自动开通，请耐心等待');
          }
      },
      // 刷新用户信息（单独封装）
      async refreshUserInfo() {
          if (!this.$globalData) return;
          try {
              const userRes = await this.$api.getUserInfo();
              if (userRes.status_code === 1) {
                  this.$globalData.userInfo = userRes.data;
              }
          } catch (err) {
              console.error('刷新用户信息失败:', err);
          }
      },
      // 查看订单（优化路由跳转）
      goToOrder() {
          if (!this.orderId) {
              this.$message.error('订单ID不存在，无法查看订单详情');
              return;
          }
          // 优先用路由跳转，兼容hash模式
          this.$router.push({ path: '/order', query: { id: this.orderId } }).catch(err => {
              // 路由跳转失败则用window.location
              window.location.href = `${window.location.origin}/#/order?id=${this.orderId}`;
          });
      },
      // 返回首页
      goToHome() {
          this.$router.push('/').catch(() => {
              window.location.href = `${window.location.origin}/#/`;
          });
      },
      // 查看会员
      goToMembership() {
          this.$router.push('/membership').catch(() => {
              window.location.href = `${window.location.origin}/#/membership`;
          });
      },
      // 自动跳转倒计时
      initCountdown() {
          this.timer = setInterval(() => {
              this.countdown--;
              if (this.countdown <= 0) {
                  clearInterval(this.timer);
                  this.goToHome();
              }
          }, 1000);
      }
  }
}
</script>

<style scoped>
/* 全局容器 - 浅绿色主调背景 */
.payment-success-container {
  min-height: 85vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(120deg, #f0f9f2 0%, #e8f5e9 100%);
  padding: 20px;
}

/* 加载状态样式 */
.loading-container {
  text-align: center;
  color: #4a7c59;
}
.loading-container .el-loading-spinner {
  font-size: 40px;
  margin-bottom: 10px;
  color: #66bb6a;
}
.loading-container p {
  font-size: 16px;
  margin-top: 15px;
}

/* 错误状态样式 - 搭配浅绿色主调的错误提示 */
.error-card {
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(102, 187, 106, 0.1);
  padding: 50px 40px;
  text-align: center;
  width: 100%;
  max-width: 500px;
  border: 1px solid #e8f5e9;
}
.error-icon {
  font-size: 80px;
  color: #ef5350;
  margin-bottom: 20px;
  opacity: 0.8;
}
.error-title {
  font-size: 28px;
  color: #2e7d32;
  margin-bottom: 10px;
  font-weight: 600;
}
.error-desc {
  color: #558b2f;
  margin-bottom: 30px;
  font-size: 16px;
  line-height: 1.5;
}

/* 成功卡片样式 - 浅绿色主调核心样式 */
.success-card {
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(102, 187, 106, 0.15);
  padding: 50px 40px;
  text-align: center;
  width: 100%;
  max-width: 500px;
  border: 1px solid #dcedc8;
  position: relative;
  overflow: hidden;
}
/* 卡片顶部装饰条 */
.success-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 6px;
  background: linear-gradient(90deg, #66bb6a 0%, #81c784 50%, #a5d6a7 100%);
}
.success-icon {
  font-size: 90px;
  color: #66bb6a;
  margin-bottom: 25px;
  animation: bounce 0.8s ease;
  filter: drop-shadow(0 4px 8px rgba(102, 187, 106, 0.2));
}
.success-title {
  font-size: 32px;
  color: #2e7d32;
  margin-bottom: 25px;
  font-weight: 600;
  letter-spacing: 0.5px;
}
.order-info {
  background-color: #f9fcf9;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 25px;
  border: 1px solid #e8f5e9;
  border-left: 4px solid #66bb6a;
}
.order-number, .order-amount {
  margin: 12px 0;
  color: #2e7d32;
  font-size: 16px;
}
.price {
  color: #2e7d32;
  font-weight: 700;
  font-size: 20px;
}
.success-description {
  color: #4a7c59;
  margin-bottom: 35px;
  line-height: 1.8;
  font-size: 16px;
}
.highlight {
  color: #66bb6a;
  font-weight: 600;
}
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-bottom: 25px;
  flex-wrap: wrap;
}
/* 主按钮样式 - 浅绿色系 */
.primary-btn {
  background-color: #66bb6a !important;
  border-color: #66bb6a !important;
  color: white !important;
  padding: 12px 30px !important;
  border-radius: 50px !important;
  font-size: 15px !important;
  font-weight: 500 !important;
  box-shadow: 0 4px 12px rgba(102, 187, 106, 0.25) !important;
  transition: all 0.3s ease !important;
}
.primary-btn:hover {
  background-color: #558b2f !important;
  border-color: #558b2f !important;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 187, 106, 0.3) !important;
}
.primary-btn:disabled {
  background-color: #a5d6a7 !important;
  border-color: #a5d6a7 !important;
  box-shadow: none !important;
  transform: none !important;
}
/* 次要按钮样式 */
.secondary-btn {
  background-color: #ffffff !important;
  border-color: #c8e6c9 !important;
  color: #2e7d32 !important;
  padding: 12px 30px !important;
  border-radius: 50px !important;
  font-size: 15px !important;
  font-weight: 500 !important;
  transition: all 0.3s ease !important;
}
.secondary-btn:hover {
  background-color: #f1f8e9 !important;
  border-color: #a5d6a7 !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 187, 106, 0.15) !important;
}
/* 自动跳转样式 */
.auto-redirect {
  color: #81c784;
  font-size: 14px;
  margin-top: 10px;
}
.countdown-number {
  color: #2e7d32;
  font-weight: 700;
  margin: 0 4px;
  font-size: 15px;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .success-card, .error-card {
      padding: 35px 25px;
      margin: 10px;
  }
  .success-icon, .error-icon {
      font-size: 70px;
  }
  .success-title, .error-title {
      font-size: 26px;
  }
  .action-buttons {
      flex-direction: column;
      gap: 12px;
  }
  .primary-btn, .secondary-btn {
      width: 100%;
  }
}

/* 成功图标动画 */
@keyframes bounce {
  0% { transform: scale(0.8); opacity: 0.7; }
  50% { transform: scale(1.1); opacity: 1; }
  100% { transform: scale(1); opacity: 1; }
}

/* 按钮点击反馈 */
.el-button:focus {
  outline: none !important;
  box-shadow: 0 0 0 3px rgba(102, 187, 106, 0.2) !important;
}
</style>