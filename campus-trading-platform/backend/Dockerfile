# 多阶段构建 Dockerfile

# 第一阶段：构建
FROM maven:3.8.6-eclipse-temurin-8 AS build
WORKDIR /app

# 复制 Maven 配置文件（使用国内镜像源）
COPY .docker/maven-settings.xml /root/.m2/settings.xml

# 复制 pom.xml 并下载依赖（利用 Docker 缓存）
COPY pom.xml .
# 如果 go-offline 失败，继续构建（依赖会在编译时下载）
RUN mvn dependency:go-offline -B -s /root/.m2/settings.xml || echo "依赖预下载失败，将在编译时下载"

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests -s /root/.m2/settings.xml

# 第二阶段：运行
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app

# 复制构建产物
COPY --from=build /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]

