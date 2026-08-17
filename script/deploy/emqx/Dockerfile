# EMQX 运行镜像；安装包由 build-package.sh 下载到 pkg/ 目录。
FROM --platform=linux/amd64 eclipse-temurin:17-jre-jammy

USER root
COPY pkg/emqx_5.7.2_amd64.deb /tmp/emqx.deb

RUN dpkg -i /tmp/emqx.deb \
    && rm -f /tmp/emqx.deb \
    && mkdir -p /opt/emqx \
    && ln -s /usr/lib/emqx/bin /opt/emqx/bin \
    && ln -s /var/lib/emqx /opt/emqx/data \
    && ln -s /var/log/emqx /opt/emqx/log \
    && chown -R emqx:emqx /etc/emqx /usr/lib/emqx /var/lib/emqx /var/log/emqx

ENV EMQX_ETC_DIR=/etc/emqx
WORKDIR /usr/lib/emqx
USER emqx

EXPOSE 1883 8083 8084 8883 18083
ENTRYPOINT ["/usr/lib/emqx/bin/emqx"]
CMD ["foreground"]
