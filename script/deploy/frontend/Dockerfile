# 目标平台: linux/amd64
# 构建上下文: script/deploy/frontend/ (需先复制 dist/ 到该目录)
FROM nginx:1.25-alpine

COPY dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

RUN mkdir -p /tmp/nginx_cache && chown -R nginx:nginx /tmp/nginx_cache

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD test -s /var/run/nginx.pid && kill -0 "$(cat /var/run/nginx.pid)" || exit 1

CMD ["nginx", "-g", "daemon off;"]
