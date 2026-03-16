# 工厂模型架构实现行动计划

## 概述

本文档为工厂模型架构的具体实施行动计划。

---

## 一、数据库准备

### 1.1 执行 SQL 脚本
- **文件**: `script/mysql/opentcs_factory_model_v1.0.sql`
- **操作**: 在 MySQL 数据库中执行该脚本

---

## 二、坐标系统设计说明

### 2.1 设计原则

```
前端画布: 原点固定 (0,0)

NavigationMap:
└── originX / originY: 仅用于数据导入时识别PGM地图的原点位置

Point 存储:
└── xPosition / yPosition / zPosition:
    - 前端显示用
    - 后端跨楼层计算用
    - 存储时保持一致，减少转换开销
```

### 2.2 字段说明

| 字段 | 位置 | 用途 |
|------|------|------|
| `FactoryModel.scale` | 工厂模型 | 比例尺 px/m |
| `NavigationMap.originX/Y` | 导航地图 | PGM导入时使用 |
| `Point.x/y/z` | 点位 | 统一坐标，前后端共用 |

---

## 三、代码开发任务清单

### 3.1 实体类开发 ✅ 已完成
| 文件 | 状态 |
|------|------|
| FactoryModelEntity (含 scale/coordinateSystem) | ✅ |
| NavigationMapEntity (含 originX/Y) | ✅ |
| PointEntity (统一坐标) | ✅ |
| CrossLayerConnectionEntity | ✅ |
| ElevatorScheduleEntity | ✅ |

### 3.2 Mapper 开发 ✅ 已完成

### 3.3 Service 开发 ✅ 已完成

### 3.4 Controller 开发 ✅ 已完成

### 3.5 路由算法开发 ✅ 已完成
| 文件 | 说明 |
|------|------|
| GlobalRoutingGraph | 全局路由图 |
| LocalRoutingGraph | 局部路由图 |
| CrossFloorRouter | 跨楼层路径规划 |

---

## 四、构建验证

```bash
mvn clean compile -DskipTests
```

---

## 五、前置依赖

1. **数据库**: MySQL 8.0+
2. **开发环境**: JDK 21, Maven 3.8+
