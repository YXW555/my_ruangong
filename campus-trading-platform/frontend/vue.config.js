const webpack = require('webpack')

module.exports = {
    publicPath: './',
    assetsDir: 'static',
    productionSourceMap: false,
    configureWebpack: {
        plugins: [
            new webpack.ProvidePlugin({
                $: "jquery",
                jQuery: "jquery",
                "windows.jQuery": "jquery"
            })
        ]
    },
    devServer: {
        port: 8082,  // 前端开发服务器端口
        proxy: {
            '/api': {
                target: 'http://localhost:8080',  // 后端服务地址（Spring Boot 默认 8080）
                changeOrigin: true,
                ws: true,  // 支持websocket
                pathRewrite: {
                    '^/api': ''  // 去掉 /api 前缀，直接转发到后端
                },
                // 添加错误处理和日志
                onError: function(err, req, res) {
                    console.log('代理错误:', err.message);
                },
                onProxyReq: function(proxyReq, req, res) {
                    console.log('代理请求:', req.method, req.url, '->', proxyReq.path);
                }
            },
            '/images': {
                target: 'http://localhost:8080',  // 后端服务地址
                changeOrigin: true,
                pathRewrite: {
                    '^/images': '/images'  // 保持 /images 前缀，直接转发到后端
                },
                onError: function(err, req, res) {
                    console.log('图片代理错误:', err.message);
                },
                onProxyReq: function(proxyReq, req, res) {
                    console.log('图片代理请求:', req.method, req.url);
                }
            }
        }
    }
};
