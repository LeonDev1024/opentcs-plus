# 车辆品牌管理产品设计文档

## 一、需求背景

### 1.1 业务背景

在 AGV/AMR 调度系统中，需要管理多个品牌的移动机器人。不同品牌的车辆在通信协议、物理参数、驱动方式等方面存在差异，需要清晰的品牌-类型-车辆三级结构来分类管理。

### 1.2 现状分析

**当前数据结构：**
- `VehicleEntity`（车辆表）：id, name, vinCode, vehicleTypeId, state, energyLevel 等
- `VehicleTypeEntity`（车辆类型表）：id, name, length, width, height, maxVelocity 等

**问题：**
1. 品牌与类型混在一起，name 字段既当品牌名又当类型名
2. 无法支持多品牌管理（logo、官网、联系方式等无处存储）
3. 品牌与类型的层级关系不清晰
4. 查询时需要额外处理品牌信息

---

## 二、数据模型设计

### 2.1 ER 图

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     brand       │       │   vehicle_type  │       │     vehicle     │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ id (PK)         │       │ id (PK)         │       │ id (PK)         │
│ name            │       │ brand_id (FK)   │◄──────│ vehicle_type_id │
│ code            │  1:n  │ name            │       │ (FK)            │
│ logo            │───────│ name            │       │ name            │
│ website         │       │ length          │       │ vin_code        │
│ description     │       │ width           │       │ current_position│
│ contact         │       │ height          │       │ next_position   │
│ enabled         │       │ max_velocity    │       │ state           │
│ sort            │       │ max_reverse_    │       │ energy_level    │
│ create_time     │       │ velocity        │       │ ...             │
│ create_by       │       │ ...             │       │                 │
└─────────────────┘       └─────────────────┘       └─────────────────┘
```

### 2.2 品牌表（brand）

```sql
CREATE TABLE `brand` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '品牌名称',
  `code` VARCHAR(50) NOT NULL COMMENT '品牌缩写代码',
  `logo` VARCHAR(500) COMMENT 'Logo URL',
  `website` VARCHAR(255) COMMENT '官网地址',
  `description` VARCHAR(1000) COMMENT '品牌描述',
  `contact` VARCHAR(255) COMMENT '联系方式',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` VARCHAR(50) COMMENT '更新人',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='品牌表';
```

**字段说明：**

| 字段 | 类型 | 必要性 | 说明 |
|------|------|--------|------|
| id | Long | 必填 | 主键，自增 |
| name | String(100) | 必填 | 品牌全称，如"海康机器人"、"极智嘉"、"快仓" |
| code | String(50) | 必填 | 品牌缩写，用于内部关联，如"HIK"、"GEEK+"、"QKM" |
| logo | String(500) | 可选 | Logo 图片 URL，建议尺寸 200x60px |
| website | String(255) | 可选 | 官网地址，用于跳转查看详情 |
| description | String(1000) | 可选 | 品牌描述，补充说明 |
| contact | String(255) | 可选 | 厂商联系方式（电话/邮箱） |
| enabled | Boolean | 必填 | 是否启用，默认 true |
| sort | Integer | 可选 | 排序权重，数字越小越靠前 |
| 审计字段 | - | 必填 | createTime, createBy, updateTime, updateBy, deleted |

**业务规则：**
- code 字段全局唯一，用于 API 关联
- 删除时使用逻辑删除（deleted = 1）
- 未启用品牌下的类型不可被新车辆引用

### 2.3 车辆类型表（vehicle_type）

**新增字段：**

```sql
ALTER TABLE `vehicle_type` ADD COLUMN `brand_id` BIGINT NOT NULL COMMENT '所属品牌ID' AFTER `id`;
ALTER TABLE `vehicle_type` ADD KEY `idx_brand_id` (`brand_id`);
```

**完整字段说明：**

| 字段 | 类型 | 必要性 | 说明 |
|------|------|--------|------|
| id | Long | 必填 | 主键 |
| brandId | Long | 必填 | 所属品牌ID，外键关联 brand.id |
| name | String(100) | 必填 | 类型名称，如"HIK-TP300"、"GEEK-AMR-500" |
| length | BigDecimal | 可选 | 车辆长度（米），如 0.8 |
| width | BigDecimal | 可选 | 车辆宽度（米），如 0.6 |
| height | BigDecimal | 可选 | 车辆高度（米），如 0.3 |
| maxVelocity | BigDecimal | 可选 | 最大速度（米/秒），如 1.5 |
| maxReverseVelocity | BigDecimal | 可选 | 最大倒车速度（米/秒），如 0.5 |
| energyLevel | BigDecimal | 可选 | 能量阈值，如 20（电量低于20%需要充电） |
| allowedOrders | JSON | 可选 | 允许的订单操作，如 ["TRANSPORT", "CHARGE"] |
| allowedPeripheralOperations | JSON | 可选 | 允许的外设操作 |
| properties | JSON | 可选 | 扩展属性 |

**业务规则：**
- brandId 为必填，建立品牌-类型的强关联
- 删除品牌时，需检查是否有类型关联；如有，阻止删除或级联处理
- 类型名称在同一个品牌下必须唯一

### 2.4 车辆表（vehicle）

**现有字段保持不变**，新增可选的 brandId 冗余字段（用于前端展示）：

```sql
-- 可选：为了方便查询，可以保留 vehicle_type_id 并通过类型关联到品牌
-- 也可以直接在 vehicle 表添加 brand_id 作为冗余字段
ALTER TABLE `vehicle` ADD COLUMN `brand_id` BIGINT COMMENT '所属品牌ID（冗余）' AFTER `vehicle_type_id`;
```

---

## 三、API 设计

### 3.1 品牌管理 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/v1/brands | 获取品牌列表（分页） |
| GET | /api/v1/brands/{id} | 获取品牌详情 |
| POST | /api/v1/brands | 创建品牌 |
| PUT | /api/v1/brands/{id} | 更新品牌 |
| DELETE | /api/v1/brands/{id} | 删除品牌 |
| GET | /api/v1/brands/all | 获取所有启用的品牌（下拉选择用） |

### 3.2 车辆类型管理 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/v1/vehicle-types | 获取类型列表（支持 brandId 筛选） |
| GET | /api/v1/vehicle-types/{id} | 获取类型详情 |
| POST | /api/v1/vehicle-types | 创建类型 |
| PUT | /api/v1/vehicle-types/{id} | 更新类型 |
| DELETE | /api/v1/vehicle-types/{id} | 删除类型 |
| GET | /api/v1/vehicle-types/by-brand/{brandId} | 获取某品牌下的所有类型 |

### 3.3 车辆管理 API

**现有 API 保持兼容**，返回数据中增加 brandName 字段：

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/v1/vehicles | 获取车辆列表（已有 brandName 字段） |
| GET | /api/v1/vehicles/{id} | 获取车辆详情 |
| POST | /api/v1/vehicles | 创建车辆 |
| PUT | /api/v1/vehicles/{id} | 更新车辆 |
| DELETE | /api/v1/vehicles/{id} | 删除车辆 |

---

## 四、前端设计

### 4.1 品牌管理页面

**入口：** 系统管理 → 品牌管理

**功能列表：**
1. 品牌列表（分页、搜索、启停状态筛选）
2. 新建品牌（表单弹窗）
3. 编辑品牌
4. 删除品牌
5. 启用/禁用品牌
6. 品牌 Logo 上传

**列表字段：** 排序、Logo、名称、Code、类型数量、状态、创建时间、操作

**表单字段：**
| 字段 | 组件 | 验证 |
|------|------|------|
| 品牌名称 | Input | 必填，最大100字符 |
| 品牌Code | Input | 必填，唯一，最大50字符 |
| Logo | ImageUpload | 可选，建议200x60px |
| 官网地址 | Input | 可选，URL格式 |
| 描述 | Textarea | 可选，最大1000字符 |
| 联系方式 | Input | 可选 |
| 排序 | InputNumber | 可选，默认0 |
| 状态 | Switch | 默认启用 |

### 4.2 车辆类型页面改造

**现状：** 车辆类型直接列表，无品牌关联

**改造后：**
1. 类型列表增加品牌列（Brand Name）
2. 新建/编辑类型时，选择所属品牌（下拉选择）
3. 品牌下拉支持搜索

**表单字段变更：**
- 新增：所属品牌（下拉必选）
- 其他字段保持不变

### 4.3 车辆管理页面

**现状：** 车辆列表显示 vehicleTypeName

**改造后：**
1. 列表增加"品牌"列（通过 vehicleType 关联获取）
2. 新建/编辑车辆时，显示车辆所属品牌信息（只读）

---

## 五、数据迁移方案

### 5.1 迁移步骤

```sql
-- 1. 创建 brand 表
CREATE TABLE `brand` (...);

-- 2. 迁移数据：将现有 vehicle_type 的 name 解析出品牌信息
-- 假设现有类型名格式为 "品牌-型号"，如 "海康-TP300"
INSERT INTO brand (name, code, create_time)
SELECT 
  SUBSTRING_INDEX(name, '-', 1) as name,
  UPPER(SUBSTRING_INDEX(name, '-', 1)) as code,
  NOW()
FROM vehicle_type
GROUP BY SUBSTRING_INDEX(name, '-', 1);

-- 3. 更新 vehicle_type 添加 brand_id
UPDATE vehicle_type vt
JOIN brand b ON SUBSTRING_INDEX(vt.name, '-', 1) = b.name
SET vt.brand_id = b.id;

-- 4. 处理未匹配的类型（无品牌前缀）
INSERT INTO brand (name, code, create_time)
VALUES ('未知品牌', 'UNKNOWN', NOW());

UPDATE vehicle_type 
SET brand_id = (SELECT id FROM brand WHERE code = 'UNKNOWN')
WHERE brand_id IS NULL;
```

### 5.2 回滚方案

- brand 表使用逻辑删除，可恢复
- vehicle_type.brand_id 可置空回退
- 旧数据 vehicle_type.name 保持不变

---

## 六、影响评估

### 6.1 需要修改的文件

| 模块 | 文件 | 改动 |
|------|------|------|
| 后端-Entity | VehicleTypeEntity.java | 新增 brandId 字段 |
| 后端-Entity | 新建 BrandEntity.java | 品牌表映射 |
| 后端-Service | 新建 BrandDomainService | 品牌业务逻辑 |
| 后端-Controller | 新建 BrandController | 品牌 CRUD API |
| 后端-Mapper | 新建 BrandMapper | 品牌数据访问 |
| 前端-页面 | 新建 BrandList.vue | 品牌管理页面 |
| 前端-API | 新建 brand.js | 品牌 API |
| 前端-组件 | VehicleTypeForm.vue | 增加品牌选择 |

### 6.2 兼容性考虑

- 现有 API 保持兼容，vehicleTypeId 关联不变
- 前端渐进式改造，先显示品牌列，再改新建流程
- 旧数据通过迁移脚本处理

---

## 七、优先级建议

| 阶段 | 内容 | 优先级 |
|------|------|--------|
| Phase 1 | 品牌表 Entity + CRUD API | P0 |
| Phase 2 | 车辆类型关联品牌 | P0 |
| Phase 3 | 品牌管理前端页面 | P1 |
| Phase 4 | 数据迁移脚本 | P1 |
| Phase 5 | 车辆/类型列表显示品牌 | P2 |

---

## 八、附录

### 8.1 典型品牌数据示例

| name | code | logo | website |
|------|------|------|---------|
| 海康机器人 | HIK | /logo/hik.png | https://robot.hikvision.com |
| 极智嘉 | GEEK | /logo/geek.png | https://www.geekplus.com |
| 快仓 | QKM | /logo/qkm.png | http://www.qkmtech.com |
| 牧星智能 | MX | /logo/mx.png | https://www.muxbot.com |

### 8.2 典型类型数据示例

| brand_id | name | length | width | max_velocity |
|----------|------|---------|-------|--------------|
| 1 | HIK-TP300 | 0.8 | 0.6 | 1.5 |
| 1 | HIK-TP500 | 1.2 | 0.8 | 2.0 |
| 2 | GEEK-AMR-500 | 0.8 | 0.65 | 1.8 |
| 3 | QKM-H500 | 0.9 | 0.7 | 1.6 |
