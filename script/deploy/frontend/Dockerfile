# ========== 构建阶段 ==========
FROM node:20-alpine AS builder

WORKDIR /build

# 安装 pnpm
RUN corepack enable && corepack prepare pnpm@latest --activate

# 复制依赖文件
COPY package.json pnpm-lock.yaml ./

# 安装依赖
RUN pnpm install --frozen-lockfile --registry=https://registry.npmmirror.com

# 复制源码
COPY . .

# 构建生产版本
RUN pnpm build:prod

# ========== 运行镜像 ==========
FROM nginx:alpine

# 复制构建产物
COPY --from=builder /build/dist /usr/share/nginx/html

# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/nginx.conf

# 创建缓存目录
RUN mkdir -p /tmp/nginx_cache && chown -R nginx:nginx /tmp/nginx_cache

# 暴露端口
EXPOSE 80 443

# 健康检查
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost/health || exit 1

CMD ["nginx", "-g", "daemon off;"]
