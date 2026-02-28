#!/bin/bash

# OpenTCS Plus 部署脚本
# 功能：构建、启动、停止服务

# 进入项目根目录
cd "$(dirname "$0")/.."

# 显示帮助信息
show_help() {
    echo "OpenTCS Plus 部署脚本"
    echo "使用方法: ./script/deploy.sh [命令]"
    echo ""
    echo "命令选项:"
    echo "  build     构建项目和Docker镜像"
    echo "  start     启动服务"
    echo "  stop      停止服务"
    echo "  restart   重启服务（先停止，再构建，再启动）"
    echo "  help      显示此帮助信息"
    echo ""
}

# 构建项目和Docker镜像
build_project() {
    echo "开始构建项目..."
    
    # 使用Maven构建项目
    mvn clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        echo "项目构建成功！"
        
        # 进入opentcs-admin目录
        cd opentcs-admin
        
        echo "开始构建Docker镜像..."
        # 构建Docker镜像
        docker build -t opentcs-admin:latest .
        
        if [ $? -eq 0 ]; then
            echo "Docker镜像构建成功！"
            return 0
        else
            echo "Docker镜像构建失败！"
            return 1
        fi
    else
        echo "项目构建失败！"
        return 1
    fi
}

# 启动服务
start_service() {
    echo "开始启动OpenTCS Plus服务..."
    
    # 停止并删除旧容器
    docker stop opentcs-admin 2>/dev/null
    docker rm opentcs-admin 2>/dev/null
    
    # 启动新容器
    docker run -d \
      --name opentcs-admin \
      -p 8080:8080 \
      -v "$(pwd)/logs:/app/logs" \
      --restart unless-stopped \
      opentcs-admin:latest
    
    if [ $? -eq 0 ]; then
        echo "OpenTCS Plus服务启动成功！"
        echo "服务地址: http://localhost:8080"
        return 0
    else
        echo "OpenTCS Plus服务启动失败！"
        return 1
    fi
}

# 停止服务
stop_service() {
    echo "开始停止OpenTCS Plus服务..."
    
    # 停止并删除容器
    docker stop opentcs-admin 2>/dev/null
    docker rm opentcs-admin 2>/dev/null
    
    if [ $? -eq 0 ]; then
        echo "OpenTCS Plus服务停止成功！"
        return 0
    else
        echo "OpenTCS Plus服务停止失败！"
        return 1
    fi
}

# 重启服务
restart_service() {
    echo "开始重启OpenTCS Plus服务..."
    
    # 停止服务
    stop_service
    
    # 构建项目
    build_project
    
    if [ $? -eq 0 ]; then
        # 启动服务
        start_service
    else
        echo "重启失败，构建过程出现错误！"
        return 1
    fi
}

# 主函数
main() {
    case "$1" in
        build)
            build_project
            ;;
        start)
            start_service
            ;;
        stop)
            stop_service
            ;;
        restart)
            restart_service
            ;;
        help|
        *)
            show_help
            ;;
    esac
}

# 执行主函数
main "$@"