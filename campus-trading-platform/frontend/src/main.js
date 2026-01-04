import Vue from 'vue';
import App from './App.vue';
import router from './router';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import 'babel-polyfill';

import api from './api/index.js';
Vue.prototype.$api = api;

let globalData={
    userInfo:{
        nickname:''
    }
};
let sta={
    isLogin:false,
    adminName:''
};
Vue.prototype.$sta = sta;
Vue.prototype.$globalData=globalData;

// 关闭生产提示
Vue.config.productionTip = false;

// 全局引入ElementUI
Vue.use(ElementUI, {
    size: 'medium'
});

// 路由守卫 登录拦截
router.beforeEach((to, from, next) => {
    document.title = `${to.meta.title}`;
    console.log('路由守卫 - 目标路径:', to.path, '来源路径:', from.path);

    const nickname = Vue.prototype.$globalData.userInfo.nickname;

    // 需要登录的页面列表
    const authRequiredPaths = ['/me', '/message', '/release', '/order', '/auto-reply'];

    // 检查是否需要登录
    if (authRequiredPaths.includes(to.path)) {
        console.log('需要登录验证的页面:', to.path, '当前用户昵称:', nickname);
        if (!nickname) {
            // 用户未登录，尝试获取用户信息
            console.log('用户未登录，尝试获取用户信息...');
            api.getUserInfo().then(res=>{
                console.log('getUserInfo响应:',res);
                if(res.status_code!==1){
                    // 未登录，跳转到登录页
                    console.log('用户未登录，跳转到登录页');
                    next('/login');
                }else {
                    // 已登录，更新用户信息并继续
                    res.data.signInTime=res.data.signInTime.substring(0,10);
                    Vue.prototype.$globalData.userInfo=res.data;
                    console.log('用户已登录，允许访问:', to.path);
                    next(); // 允许访问目标页面
                }
            }).catch(e=>{
                console.error('获取用户信息失败:', e);
                // 获取用户信息失败，跳转到登录页
                next('/login');
            });
        } else {
            // 用户已登录，直接允许访问
            console.log('用户已登录，直接允许访问:', to.path);
            next();
        }
    } else {
        // 不需要登录的页面，直接允许访问
        next();
    }
});

// 创建Vue实例并挂载，仅此一份！
new Vue({
    router,
    render: h => h(App)
}).$mount('#app');
