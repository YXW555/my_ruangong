图片与静态资源说明与上传指南
================================

目标
----
把商品示例图片放到项目静态目录，使得前端页面在本地开发与服务器部署时都能正常显示。

静态目录（推荐）
-----------------
- 本地开发与版本控制（推荐）：把图片放到仓库静态目录
  `backend/src/main/resources/static/images/items/`
  - Spring Boot 会把 `classpath:/static/` 映射为 `/`，因此放在该目录下的文件可以通过 `/images/items/<name>` 直接访问。
  - 示例访问 URL：`/images/items/laptop_01.jpg`

图片命名规范（建议）
--------------------
- 商品缩略图与轮播（建议尺寸）
  - `item_<id>_1.jpg`  400x600（主图/缩略）
  - `item_<id>_2.jpg`  800x600（展示图）
  - `item_<id>_3.jpg`  800x600（展示图可选）
- 用户头像
  - `avatar_user_<id>.jpg`  200x200
- 示例文件名（与 SQL 对应）
  - `/images/items/laptop_01.jpg`
  - `/images/items/laptop_02.jpg`
  - `/images/items/keyboard_01.jpg`
  - `/images/items/ball_01.jpg`
  - `/images/items/book_java_01.jpg`
  - ...

示例图片清单（请把以下图片放到上述目录）
------------------------------------------------
laptop_01.jpg
laptop_02.jpg
keyboard_01.jpg
keyboard_02.jpg
ball_01.jpg
book_java_01.jpg
book_java_02.jpg
thermos_01.jpg
phone_01.jpg
phone_02.jpg
shoes_01.jpg
tablet_01.jpg
lamp_01.jpg
guitar_01.jpg
guitar_02.jpg
avatar_user_1.jpg
avatar_user_2.jpg
avatar_user_3.jpg

部署时注意事项
----------------
1. 开发阶段（本地）  
   - 把图片放入 `backend/src/main/resources/static/images/items/` 并提交到仓库。启动后端应用（Spring Boot），访问 `http://localhost:8080/images/items/laptop_01.jpg` 可检查是否可见。

2. 生产部署（服务器）  
   - 有两种做法：
     A. 把图片随代码一起部署（把 static 目录包含在构建包中）。适合小量样本图片。  
     B. 把图片放到服务器外部目录（例如 `/opt/campus-trading-platform/static/images/items/`），并在 `application.properties` 或 `WebMvcConfig` 中添加静态资源映射：
        spring.resources.static-locations=file:/opt/campus-trading-platform/static/
   - 如果使用 nginx 反向代理，请确保 nginx 的配置允许 `/images/` 路径直接返回静态文件（不要代理到后端）。

3. 常见图片不显示原因排查（快速）
   - 文件实际未上传到服务器（最常见）→ 检查服务器目录是否存在对应文件。
   - 后端/框架没有映射静态目录 → 访问静态文件 URL 看是否 404。
   - nginx 等反向代理拦截或没有配置静态映射 → 检查 nginx 配置。
   - URL 路径不一致（SQL 中使用 `/image?imageName=...` 而实际静态路径为 `/images/items/...`）→ 统一替换为 `/images/items/...`。

我会在接下来的 SQL 中把图片 URL 统一写为 `/images/items/<name>`，你把图片放到上面目录并 push，部署时图片就会显示。

