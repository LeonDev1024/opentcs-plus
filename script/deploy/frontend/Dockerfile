# 目标平台: linux/amd64 (Ubuntu 20.04 x86)
# ========== 构建阶段 ==========
FROM --platform=linux/amd64 node:20-focal AS builder

WORKDIR /build

RUN corepack enable && corepack prepare pnpm@latest --activate

COPY package.json pnpm-lock.yaml ./

RUN pnpm install --frozen-lockfile --registry=https://registry.npmmirror.com

COPY . .

RUN pnpm build:prod

# ========== 运行镜像 (nginx on Ubuntu 20.04 focal) ==========
FROM --platform=linux/amd64 nginx:1.24

COPY --from=builder /build/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/nginx.conf

RUN mkdir -p /tmp/nginx_cache && chown -R nginx:nginx /tmp/nginx_cache

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD curl -sf http://localhost/health || exit 1

CMD ["nginx", "-g", "daemon off;"]
