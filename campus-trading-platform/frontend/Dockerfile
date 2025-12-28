# 多阶段构建 Dockerfile

# 第一阶段：构建
FROM node:14-alpine AS build
WORKDIR /app

# 配置 npm 使用淘宝镜像源（加速依赖下载）
RUN npm config set registry https://registry.npmmirror.com

# 复制 package 文件并安装依赖
COPY package*.json ./
RUN npm ci --legacy-peer-deps

# 复制源代码并构建
COPY . .
RUN npm run build

# 第二阶段：运行
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# 复制构建产物
COPY --from=build /app/dist .

# 复制 nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80

# 启动 nginx
CMD ["nginx", "-g", "daemon off;"]

