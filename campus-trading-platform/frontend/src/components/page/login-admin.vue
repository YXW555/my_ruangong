<template>
    <div class="container">
      <!-- 和学生端完全一样的布局结构 去掉el-card 彻底解决样式冲突 -->
      <div class="login-card">
        <div class="login-form-box">
          <div class="login-title">管理后台系统</div>
          <el-form ref="form" :model="userForm" size="large" class="login-form">
            <!-- ✅ 彻底修复图标语法错误 绝对正确 -->
            <el-input placeholder="请输入管理员账号" v-model="userForm.accountNumber" class="login-input" autocomplete="off">
              <template slot="prepend">
                <i class="el-icon-user-solid"></i>
              </template>
            </el-input>
            <el-input placeholder="请输入管理员密码" v-model="userForm.adminPassword" class="login-input"
                      @keyup.enter.native="login" show-password autocomplete="off">
              <template slot="prepend">
                <i class="el-icon-lock"></i>
              </template>
            </el-input>
            <div class="login-submit">
              <el-button type="primary" class="login-btn" @click="login">登录</el-button>
            </div>
            <div class="other-submit">
              <router-link to="/login" class="sign-in-text">学生登录</router-link>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: "login-admin",
    data() {
      return {
        userForm: {
          accountNumber: '',
          adminPassword: ''
        }
      };
    },
    methods: {
      login() {
        this.$api.adminLogin({
          accountNumber: this.userForm.accountNumber,
          adminPassword: this.userForm.adminPassword
        }).then(res => {
          console.log(res);
          if (res.status_code === 1) {
            this.$sta.isLogin = true;
            this.$sta.adminName = res.data.adminName;
            this.$router.replace({ path: '/platform-admin' });
          } else {
            this.$message.error('登录失败，账号或密码错误！');
          }
        });
      }
    }
  };
  </script>
  
  <style scoped>
  /* ✅ 和学生端一模一样的根容器 背景图清晰明亮 不变暗 全屏铺满 */
  .container {
    height: 100vh;
    width: 100vw;
    overflow: hidden;
    background-image: url("../../img/管理员用户登录背景图.jpg");
    background-size: cover;
    background-position: center center;
    background-repeat: no-repeat;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #333;
  }
  
  /* ✅ 核心：和学生端同尺寸大卡片 450px宽度 绝不缩小！毛玻璃+圆角+阴影 完美一致 */
  .login-card {
    width: 450px;
    height: 470px;
    background: rgba(255, 255, 255, 0.88);
    backdrop-filter: blur(6px);
    -webkit-backdrop-filter: blur(6px);
    border-radius: 20px;
    box-shadow: 0 10px 30px rgba(79, 192, 141, 0.12), 0 2px 10px rgba(79, 192, 141, 0.06);
    border: 1px solid rgba(255, 255, 255, 0.95);
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease-in-out;
    /* 响应式适配 小屏幕自动缩放 大屏保持450px大尺寸 */
    @media screen and (max-width: 500px) {
      width: 92%;
      height: auto;
      padding: 30px 0;
    }
  }
  /* ✅ 悬浮上浮动效 保留 */
  .login-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 15px 40px rgba(79, 192, 141, 0.18), 0 3px 15px rgba(79, 192, 141, 0.09);
  }
  
  .login-form-box {
    width: 90%;
    height: 90%;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  
  /* ✅ 标题完美适配：字号28px 不大不小 绿系渐变 和学生端同色系 间距刚好 */
  .login-title {
    text-align: center;
    font-size: 28px;
    font-weight: 700;
    margin-bottom: 40px;
    letter-spacing: 2px;
    background: linear-gradient(120deg, #2d8259, #4fc08d, #67c29c);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
  }
  
  .login-form {
    width: 100%;
  }
  
  /* ✅ 输入框彻底完整显示：高度52px 宽度100% 无挤压 圆角12px 绿系边框 hover/聚焦正常 */
  .login-input {
    width: 100%;
    margin-bottom: 28px;
    border-radius: 12px;
    transition: all 0.3s ease;
    --el-input-border-radius: 12px;
    --el-input-focus-border-color: #4fc08d;
    --el-input-hover-border-color: #67c29c;
  }
  
  .login-submit {
    width: 100%;
    display: flex;
    justify-content: center;
    margin-top: 10px;
    margin-bottom: 25px;
  }
  
  /* ✅ 登录按钮 和学生端一模一样的绿系渐变 宽度100% 高度52px 动效保留 */
  .login-btn {
    width: 100%;
    height: 52px;
    font-size: 18px;
    font-weight: 600;
    border-radius: 12px;
    letter-spacing: 2px;
    background: linear-gradient(90deg, #3ba674, #4fc08d) !important;
    border: none !important;
      box-shadow: 0 4px 12px rgba(79, 192, 141, 0.2);
    transition: all 0.3s ease;
  }
  .login-btn:hover {
    background: linear-gradient(90deg, #4fc08d, #3ba674) !important;
    transform: scale(1.02);
    box-shadow: 0 6px 18px rgba(79, 192, 141, 0.28);
  }
  .login-btn:active {
    transform: scale(0.98);
  }
  
  /* ✅ 学生登录链接 绿系渐变下划线 动效保留 */
  .other-submit {
    display: flex;
    justify-content: space-between;
    padding: 0 15px;
  }
  .sign-in-text {
    color: #3ba674;
    font-size: 16px;
    font-weight: 500;
    text-decoration: none;
    letter-spacing: 1px;
    transition: all 0.3s ease;
    position: relative;
  }
  .sign-in-text::after {
    content: '';
    position: absolute;
    bottom: -2px;
    left: 0;
    width: 0;
    height: 2px;
    background: linear-gradient(90deg, #3ba674, #4fc08d);
    transition: width 0.3s ease;
  }
  .sign-in-text:hover {
    background: linear-gradient(90deg, #2d8259, #4fc08d);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
  }
  .sign-in-text:hover::after {
    width: 100%;
  }
  
  /* ✅ 穿透样式 完美适配输入框图标/内框 无任何显示异常 */
  ::v-deep .el-input__prefix {
    color: #4fc08d;
    font-size: 18px;
    padding-left: 5px;
  }
  ::v-deep .el-input__inner {
    height: 52px;
    font-size: 17px;
    padding: 0 20px;
    border: 1.5px solid #e8f5e9;
    background: rgba(255, 255, 255, 0.95);
    transition: all 0.3s ease;
  }
  ::v-deep .el-input__inner:focus {
    border-color: #4fc08d;
    box-shadow: 0 0 0 3px rgba(79, 192, 141, 0.1);
  }
  ::v-deep .el-input__inner:hover {
    border-color: #67c29c;
  }
  ::v-deep .el-input__suffix-inner {
    color: #67c29c;
      font-size: 16px;
  }
  </style>