# opentcs-plus
OpenTCS Plus 是基于 OpenTCS 核心思想构建的企业级AGV调度系统，在保留 OpenTCS 稳定调度内核的同时，提供了更现代化的架构、更友好的用户界面和更强大的功能扩展。

# 项目结构
```text
opentcs-plus/
├── opentcs-admin/                     # Web入口
├── opentcs-common/                    # 通用模块
├── opentcs-modules/                   # 业务模块
│   ├── opentcs-module-system/         # 系统管理
│   ├── opentcs-module-rcs/            # 调度核心
│   ├── opentcs-module-monitor/        # 系统监控
└── └── opentcs-module-algorithm/      # 算法模块

```