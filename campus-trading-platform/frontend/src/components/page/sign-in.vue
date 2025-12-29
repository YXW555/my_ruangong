<template>
    <div class="sign-in-container">
      <div class="login-card">
        <div class="login-form-box">
          <div class="sign-in-title">注册</div>
          <el-form ref="form" :model="userInfo" size="large" class="login-form">
            <el-input 
              placeholder="请输入昵称..." 
              maxlength="30"  
              v-model="userInfo.nickname" 
              class="login-input" 
              clearable
            >
              <template slot="prepend">
                <i class="el-icon-user-solid"></i>
              </template>
            </el-input>
            <el-input 
              placeholder="请输入手机号..." 
              maxlength="11" 
              v-model="userInfo.accountNumber" 
              class="login-input" 
              clearable
            >
              <template slot="prepend">
                <i class="el-icon-phone"></i>
              </template>
            </el-input>
            <el-input 
              placeholder="请输入密码..." 
              show-password 
              maxlength="16" 
              v-model="userInfo.userPassword" 
              class="login-input" 
              clearable
            >
              <template slot="prepend">
                <i class="el-icon-lock"></i>
              </template>
            </el-input>
            <el-input 
              placeholder="请再次输入密码..." 
              show-password 
              maxlength="16" 
              v-model="userPassword2" 
              @keyup.enter.native="signIn" 
              class="login-input" 
              clearable
            >
              <template slot="prepend">
                <i class="el-icon-lock"></i>
              </template>
            </el-input>
            <div class="login-submit">
              <el-button type="primary" class="login-btn" @click="signIn">提交</el-button>
            </div>
            <div class="other-submit">
              <span @click="toLogin" class="sign-in-text">已有账号？立即登录</span>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: "sign-in",
    data() {
      return {
        userPassword2: '',
        userInfo: {
          accountNumber: '',
          userPassword: '',
          nickname: ''
        }
      };
    },
    methods: {
      toLogin() {
        this.$router.replace({ path: '/login' });
      },
      signIn() {
        if (this.userInfo.accountNumber && this.userInfo.userPassword && this.userInfo.nickname) {
          if (this.userInfo.accountNumber.length !== 11) {
            this.$message.error('手机号长度不符合要求!')
          } else if (this.userInfo.userPassword !== this.userPassword2) {
            this.$message.error('两次输入的密码不相同！');
          } else {
            this.$api.signIn(this.userInfo).then(res => {
              if (res.status_code === 1) {
                this.$message({ message: '注册成功！', type: 'success' });
                this.$router.replace({ path: '/login' });
              } else {
                this.$message.error('注册失败，用户已存！');
              }
            }).catch(e => {
              console.log(e);
              this.$message.error('注册失败，网络异常！');
            })
          }
        } else {
          this.$message.error('注册信息未填写完整！');
        }
      }
    }
  };
  </script>
  
  <style scoped>
  /* 根容器 - 背景图清晰显示 */
  .sign-in-container {
    height: 100vh;
    width: 100vw;
    overflow: hidden;
    background-image: url("../../img/注册背景图.jpg");
    background-size: cover;
    background-position: center;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  /* 和登录页完全一致的卡片 - 450px宽+固定高度 避免挤压 */
  .login-card {
    width: 450px;
    height: 550px; /* 增加高度容纳4个输入框 */
    background: rgba(255, 255, 255, 0.88);
    backdrop-filter: blur(6px);
    -webkit-backdrop-filter: blur(6px);
    border-radius: 20px;
    box-shadow: 0 10px 30px rgba(79, 192, 141, 0.12);
    border: 1px solid rgba(255, 255, 255, 0.95);
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;
  }
  .login-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 15px 40px rgba(79, 192, 141, 0.18);
  }
  
  .login-form-box {
    width: 90%;
    height: 90%;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  
  /* 标题样式 - 和登录页一致 */
  .sign-in-title {
    text-align: center;
    font-size: 30px;
    font-weight: 700;
    margin-bottom: 35px;
    letter-spacing: 2px;
    background: linear-gradient(120deg, #2d8259, #4fc08d);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
  }
  
  .login-form {
    width: 100%;
  }
  
  /* 输入框样式 - 和登录页完全一致 确保显示完整 */
  .login-input {
    width: 100%;
    margin-bottom: 25px;
    border-radius: 12px;
    --el-input-border-radius: 12px;
    --el-input-focus-border-color: #4fc08d;
  }
  
  .login-submit {
    width: 100%;
    display: flex;
    justify-content: center;
    margin-top: 10px;
    margin-bottom: 20px;
  }
  
  /* 按钮样式 - 和登录页一致 */
  .login-btn {
    width: 100%;
    height: 52px;
    font-size: 18px;
    font-weight: 600;
    border-radius: 12px;
    background: linear-gradient(90deg, #3ba674, #4fc08d) !important;
    border: none !important;
    box-shadow: 0 4px 12px rgba(79, 192, 141, 0.2);
    transition: all 0.3s ease;
  }
  .login-btn:hover {
    background: linear-gradient(90deg, #4fc08d, #3ba674) !important;
    transform: scale(1.02);
  }
  
  .other-submit {
    display: flex;
    justify-content: center;
    padding: 0 15px;
  }
  
  /* 链接样式 - 和登录页一致 */
  .sign-in-text {
    color: #3ba674;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;
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
  .sign-in-text:hover::after {
    width: 100%;
  }
  
  /* 穿透样式 - 强制覆盖Element默认样式 */
  ::v-deep .el-input__prefix {
    color: #4fc08d;
    font-size: 18px;
    padding-left: 5px;
  }
  ::v-deep .el-input__inner {
    height: 52px !important; /* 强制高度 */
    font-size: 17px !important;
    padding: 0 20px !important;
    border: 1.5px solid #e8f5e9 !important;
    background: rgba(255, 255, 255, 0.95) !important;
  }
  ::v-deep .el-input__inner:focus {
    border-color: #4fc08d !important;
    box-shadow: 0 0 0 3px rgba(79, 192, 141, 0.1) !important;
  }
  </style>