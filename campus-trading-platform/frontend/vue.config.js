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
        port: 8082,  // 保持你现在的端口
        proxy: {
            '/api': {
                target: 'http://localhost:8080',  // IDEA 中直接运行的后端（Spring Boot 默认 8080）
                changeOrigin: true,
                pathRewrite: {
                    '^/api': ''  // 去掉 /api 前缀，直接转发到后端
                }
            }
        }
    }
};