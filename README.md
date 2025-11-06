# 机器人调度管理系统
OpenTCS Plus 是基于 OpenTCS 核心思想构建的企业级AGV调度系统，在保留 OpenTCS 稳定调度内核的同时，提供了更现代化的架构、更友好的用户界面和更强大的功能扩展。

# 项目结构
考虑仓储物流核心私有化部署的场景，目第一阶段实现单机的模式调度系统，模块化划分，后面也可以扩展为微服务集群的模式。


```text
opentcs-plus/
├── opentcs-admin/                     # Web入口
├── opentcs-common/                    # 通用模块
├── opentcs-modules/                   # 业务模块
│   ├── opentcs-module-algorithm/      # 算法模块
│   ├── opentcs-module-driver/         # 车辆驱动模块
│   ├── opentcs-module-job/            # job任务管理模块
│   ├── opentcs-module-map/            # 地图模块
│   ├── opentcs-module-monitor/        # 监控模块
│   ├── opentcs-module-system/         # 系统管理模块
│   └── opentcs-module-task/           # 订单任务模块
└── └── opentcs-module-vehicles/       # 车辆管理模块

```

