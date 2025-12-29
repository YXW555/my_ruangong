<template>
  <div class="container">
    <div class="login-card">
      <div class="login-form-box">
        <!-- ✅ 核心保留：拾光换物 + 校园二手交易商城 双标题 美观排版 -->
        <div class="login-title-box">
          <div class="login-title-top">拾光换物</div>
          <div class="login-title-bottom">校园二手交易商城</div>
        </div>
        <el-form ref="form" :model="userForm" size="large" class="login-form">
          <el-input
            placeholder="请输入手机号/账号"
            v-model="userForm.accountNumber"
            class="login-input"
            autocomplete="off"
          >
            <template slot="prepend">
              <i class="el-icon-user-solid"></i>
            </template>
          </el-input>
          <el-input
            placeholder="请输入密码"
            v-model="userForm.userPassword"
            class="login-input"
            @keyup.enter.native="login"
            show-password
            autocomplete="off"
          >
            <template slot="prepend">
              <i class="el-icon-lock"></i>
            </template>
          </el-input>
          <div class="login-submit">
            <el-button type="primary" class="login-btn" @click="login">登录</el-button>
          </div>
          <div class="other-submit">
            <router-link to="/sign-in" class="sign-in-text">用户注册</router-link>
            <router-link to="/login-admin" class="sign-in-text">管理员登录</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "login",
  data() {
    return {
      userForm: {
        accountNumber: '',
        userPassword: ''
      }
    };
  },
  methods: {
    login() {
      this.$api.userLogin({
        accountNumber: this.userForm.accountNumber,
        userPassword: this.userForm.userPassword
      }).then(res => {
        console.log(res);
        if (res.status_code === 1) {
          res.data.signInTime = res.data.signInTime.substring(0,10);
          this.$globalData.userInfo = res.data;
          this.$router.replace({path: '/index'});
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    toIndex() {
      this.$router.replace({path: '/index'});
    }
  }
};
</script>

<style scoped>
/* ✅ 根容器 - 背景图纯净清晰 无任何叠加层 亮度完全还原 不变暗！ */
.container {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  background-image: url("../../img/普通用户登录背景图.jpg");
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #333;
}

/* 登录卡片 - 毛玻璃+悬浮上浮+渐变阴影 所有高级样式保留 */
.login-card {
  width: 450px;
  height: 490px;
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
  @media screen and (max-width: 500px) {
    width: 92%;
    height: auto;
    padding: 30px 0;
  }
}
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

/* ✅ 核心：双标题样式 - 拾光换物 + 校园二手交易商城 都保留 渐变文字 排版绝美 */
.login-title-box {
  text-align: center;
  margin-bottom: 25px;
}
/* 拾光换物 - 主标题 渐变文字 更大气 */
.login-title-top {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 4px;
  background: linear-gradient(120deg, #2d8259, #4fc08d, #67c29c);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  line-height: 1.2;
}
/* 校园二手交易商城 - 副标题 渐变文字 呼应主标题 完美搭配 */
.login-title-bottom {
  font-size: 22px;
  font-weight: 600;
  letter-spacing: 2px;
  margin-top: 8px;
  background: linear-gradient(120deg, #3ba674, #67c29c);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

/* 输入框 渐变边框+hover光效+圆角 全部保留 */
.login-input {
  width: 100%;
  margin-bottom: 28px;
  border-radius: 12px;
  transition: all 0.3s ease;
  --el-input-border-radius: 12px;
  --el-input-focus-border-color: #4fc08d;
  --el-input-hover-border-color: #67c29c;
}

/* 登录按钮 - 渐变按钮+悬浮缩放+阴影动效 全部保留 */
.login-submit {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-top: 10px;
  margin-bottom: 25px;
}
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

/* 注册/管理员链接 - 渐变文字+下划线展开动效 全部保留 */
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

/* ElementUI内置样式穿透美化 全部保留 */
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