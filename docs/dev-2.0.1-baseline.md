# Dev 2.0.1 基线冻结说明（I0）

> 冻结日期：2026-07-28  
> 目标：前后端版本对齐，作为后续 I1–I4 迭代的唯一开发基线

## 1. 版本与分支

| 仓库 | 分支 | 版本号 | 说明 |
|------|------|--------|------|
| `opentcs-plus` | `dev-2.0.1` | `2.0.1-SNAPSHOT` | Maven `revision` |
| `opentcs-plus-web` | `dev-2.0.1` | `2.0.1` | `package.json` version |

联调约定：前端 `dev-2.0.1` 只对接后端 `dev-2.0.1`，不跨版本混用。

## 2. 本基线已具备能力

- 分层架构稳定（架构评审 v2.0.0：90/100）
- 地图发布进入运行内核，订单绑定 `mapId/mapVersion`
- 订单草稿 / 提交 / 重启恢复 / `RECOVERING` 对账
- VDA5050 真实 MQTT 连接、订阅、发布、重连
- 派车综合评分策略 + 真实路径成本
- Flyway 数据库治理与部署集成
- 仿真监控基础画布 + 拓扑路径行驶
- 前端导航重组、场景监控、地图控制台

## 3. 已知缺口（不在 I0 修复）

按优先级进入后续迭代，I0 只登记不关闭：

| 优先级 | 缺口 | 目标迭代 |
|--------|------|----------|
| P0 | VDA 订单 state 回写未闭环（接收/执行/完成/拒绝） | I1 |
| P0 | 单车 A→B 现场/仿真验收未过 | I1 |
| P0 | 地图 Block / 路径限速 / 停靠朝向不可投产 | I2 |
| P1 | OpsAction 运维动作产品化与动作台 | I3 |
| P1 | 监控大屏 KPI / 告警中心最小可用 | I3 |
| P1 | 交通冲突检测与资源锁审计持久化 | I4 |
| P1 | 仿真回归场景矩阵默认进 CI | I4 |
| P2 | 前端自动化测试覆盖 | I4+ |

产品整体完整度评估约 **55%**；实施清单完成约 **60/118（50.8%）**。

## 4. I0 完成标准（DoD）

- [x] 后端分支与远端均为 `dev-2.0.1`，版本号 `2.0.1-SNAPSHOT`
- [x] 前端分支为 `dev-2.0.1`，版本号 `2.0.1` 并推送远端
- [x] 本基线说明文档落地
- [x] 后续迭代入口明确：下一迭代为 **I1 单车真实闭环验收**

## 5. I1 落地状态（单车真实闭环）

> 更新日期：2026-07-28

### 5.1 已完成

- [x] 派车后发布 `OrderAssignedEvent`，由 `OrderDispatchCommandListener` 调用 `driverRegistry.sendOrder`
- [x] `DriverOrderFactory` 将路径/步骤转换为 VDA nodes/edges，并携带 `traceId`
- [x] 订单创建自动写入 `properties.traceId`
- [x] VDA State 解析兼容嵌套与扁平格式，读取 `lastNodeId` / `nodeStates` / `actionStates`
- [x] 状态回写推进 `OrderStep`；FAULT / REJECTED → `FAILED`（不再误记为 `CANCELLED`）
- [x] 失败原因码：`OrderFailureReasons` + `properties.failureReasonCode`
- [x] `LOOPBACK` 驱动：无真实车时可回放节点到达与 IDLE，用于 A→B 联调

### 5.2 验收用法（Loopback）

1. 发布地图并加载运行时  
2. 注册车辆，`driverType=LOOPBACK`，连接后激活  
3. 创建 A→B 运输订单并提交  
4. 观察日志：`订单已下发` → `订单步骤完成` → `订单执行结果已上报`，DB 状态为 `FINISHED`  
5. 用同一 `traceId` 串起创建/下发/回传日志

### 5.3 仍开放（后续）

- [ ] 真实 VDA5050 车现场 A→B 验收  
- [ ] 重启恢复后的 node/action 细粒度对账  
- [ ] 订单状态枚举扩展 `DISPATCHED/EXECUTING`（当前用 `dispatchState` 属性）

## 6. I2 落地状态（地图可投产）

> 更新日期：2026-07-29

### 6.1 已完成

- [x] 路径 `maxVelocity` / `maxReverseVelocity` 保存/加载往返
- [x] 点位 `vehicleOrientationAngle`（度）与后端 `vehicleOrientation` 映射并落入运行时 Point
- [x] Flyway `V1.0.26` 重建 `tcs_block`
- [x] 地图编辑器 load/save 携带 Block；右侧「Block」面板支持新建/成员/类型/颜色
- [x] 路径右键「加入 Block」

### 6.2 验收要点

1. 编辑路径限速 → 保存 → 重新加载 → 值保持  
2. 编辑点位朝向角度 → 保存 → 重新加载 → 值保持；发布后运行时 Point.orientation 非 0  
3. 新建 Block，选中点/路径后「加入选中」→ 保存 → 重新加载成员仍在  

### 6.3 仍开放

- [ ] 地图热加载（冻结接单 → 切版本 → 恢复）  
- [x] 调度运行态真正消费 Block 互斥策略  

### 6.4 本迭代已附带打磨

- [x] Block 成员按颜色在画布描边/点位着色  
- [x] 点位右键「加入 Block」  

## 7. I3 落地状态（运维可运营 + 监控可用）

> 更新日期：2026-07-29

### 7.1 已完成

- [x] OpsAction 落库（`tcs_ops_action`）+ requestId 幂等
- [x] actionStates 回写 SUCCEEDED/FAILED/REJECTED + 超时 TIMEOUT
- [x] 预校验（在线/ERROR/忙碌风险确认）
- [x] AMR 运维动作台页面（模式/充电/切地图/移动）+ 执行时间线
- [x] 资源锁监控列表 + 强制释放
- [x] 告警中心 MVP（车辆 ERROR/OFFLINE + 锁 EXPIRED）
- [x] 场景监控 4s 轮询 + EXECUTING/WORKING 筛选对齐

### 7.2 验收要点

1. 动作台对在线车执行暂停/恢复，记录可查且可幂等重放  
2. 锁监控页能看到持有锁并可强制释放  
3. 场景监控顶栏告警可跳转告警中心；KPI「任务中」可筛出 EXECUTING 车辆  

### 7.3 仍开放

- [ ] WebSocket 实时推送（当前轮询）  
- [ ] 工厂维度过滤 statistics  
- [x] 调度运行态消费 Block / 冲突检测（I4）  

## 8. I4 落地状态（交通冲突 + 资源锁审计）

> 更新日期：2026-07-29

### 8.1 已完成

- [x] Block 运行态加载 + 进入/离开占用（POINT/BLOCK 锁）
- [x] TopologyConflictDetector：站点/Block/路径占用冲突，接入派车过滤
- [x] 资源锁当前态 + 审计表（`tcs_resource_lock` / `tcs_resource_lock_audit`）
- [x] FORCE_RELEASED / EXPIRED 审计与告警；启动恢复 HELD 锁
- [x] 车辆分配锁，修复并发重复分配；3×20 压测用例
- [x] 锁监控页审计流水 Tab

### 8.2 仍开放

- [ ] SAME_DIRECTION_ONLY 方向语义完整落地  
- [ ] 完整交叉口/死锁检测与消解  
- [ ] 仿真回归场景矩阵默认进 CI  
