package org.opentcs.simulation.controller;

import lombok.extern.slf4j.Slf4j;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.simulation.core.SimulationEngine;
import org.opentcs.simulation.core.SimulationScene;
import org.opentcs.simulation.core.SimulationSceneManager;
import org.opentcs.simulation.map.SimMapGraph;
import org.opentcs.simulation.map.SimMapPoint;
import org.opentcs.simulation.map.SimulationMapService;
import org.opentcs.simulation.order.OrderSimulator;
import org.opentcs.simulation.traffic.TrafficSimulator;
import org.opentcs.simulation.vehicle.VehicleSimulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.opentcs.simulation.order.SimulatedOrder;
import org.opentcs.simulation.vehicle.SimulatedVehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 仿真控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    @Autowired
    private SimulationEngine simulationEngine;

    @Autowired
    private SimulationSceneManager sceneManager;

    @Autowired
    private VehicleSimulator vehicleSimulator;

    @Autowired
    private OrderSimulator orderSimulator;

    @Autowired
    private TrafficSimulator trafficSimulator;

    @Autowired
    private SimulationMapService simulationMapService;

    /** 当前激活的工厂模型 ID（null 表示使用随机坐标模式） */
    private volatile Long currentFactoryModelId = null;
    /** 当前主导航地图的数字 ID（用于加载点位和元数据） */
    private volatile Long currentMapId = null;
    private volatile NavigationMapDTO currentMapInfo = null;
    /** 当前地图拓扑图（null 表示随机坐标模式） */
    private volatile SimMapGraph currentMapGraph = null;

    // ─── 地图管理 ──────────────────────────────────────────────

    /**
     * 查询工厂下所有可用地图
     */
    @GetMapping("/maps")
    public Map<String, Object> listMaps(@RequestParam(required = false) Long factoryId) {
        try {
            List<NavigationMapDTO> maps = factoryId != null
                    ? simulationMapService.listMapsByFactory(factoryId)
                    : List.of();
            return Map.of("success", true, "maps", maps);
        } catch (Exception e) {
            log.error("获取地图列表失败: {}", e.getMessage(), e);
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    /**
     * 设置仿真使用的地图（加载真实点位）
     */
    @PostMapping("/map/set")
    public Map<String, Object> setMap(@RequestBody Map<String, Object> body) {
        try {
            Long factoryModelId = body.get("mapId") != null
                    ? Long.parseLong(body.get("mapId").toString())
                    : null;
            if (factoryModelId == null) {
                currentFactoryModelId = null;
                currentMapId = null;
                currentMapInfo = null;
                currentMapGraph = null;
                orderSimulator.getOrderGenerator().setMapPoints(null);
                orderSimulator.setMapGraph(null);
                return Map.of("success", true, "message", "已切换为随机坐标模式");
            }

            // 通过工厂模型 ID 查询旗下所有导航地图
            List<NavigationMapDTO> navMaps = simulationMapService.listMapsByFactory(factoryModelId);
            if (navMaps.isEmpty()) {
                return Map.of("success", false, "message", "工厂场景下没有可用地图");
            }

            // 合并所有导航地图的点位
            List<SimMapPoint> allPoints = new ArrayList<>();
            for (NavigationMapDTO navMap : navMaps) {
                List<SimMapPoint> pts = simulationMapService.loadMapPoints(navMap.getId());
                allPoints.addAll(pts);
            }

            // 取第一张导航地图作为元数据（栅格图）
            NavigationMapDTO primaryMap = navMaps.get(0);
            currentFactoryModelId = factoryModelId;
            currentMapId = primaryMap.getId();
            currentMapInfo = primaryMap;
            orderSimulator.getOrderGenerator().setMapPoints(allPoints.isEmpty() ? null : allPoints);

            // 加载并注入地图拓扑图（路径规划用）
            SimMapGraph graph = simulationMapService.loadMapGraphForFactory(factoryModelId);
            currentMapGraph = graph;
            orderSimulator.setMapGraph(graph);

            // 如果仿真正在运行，将现有车辆重置到地图点位
            if (!vehicleSimulator.getVehicles().isEmpty() && !allPoints.isEmpty()) {
                vehicleSimulator.resetVehiclesToMapPoints(allPoints);
            }

            log.info("已设置仿真地图 factoryModelId={}, 导航地图数={}, 合并点位数={}",
                    factoryModelId, navMaps.size(), allPoints.size());

            // 返回第一张导航地图的字符串 mapId（供前端加载地图拓扑用）
            String navMapStringId = primaryMap.getMapId();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "地图设置成功");
            result.put("pointCount", allPoints.size());
            result.put("navMapCount", navMaps.size());
            result.put("navMapStringId", navMapStringId);
            return result;
        } catch (Exception e) {
            log.error("设置仿真地图失败: {}", e.getMessage(), e);
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    // ─── 仿真控制 ──────────────────────────────────────────────

    @PostMapping("/start")
    public Map<String, Object> startSimulation() {
        log.info("Starting simulation...");
        try {
            // 先强制停止旧的仿真（避免引擎已运行时 start() 直接返回）
            if (simulationEngine.getStatus() != SimulationEngine.SimulationStatus.STOPPED) {
                simulationEngine.stop();
                vehicleSimulator.stop();
                orderSimulator.stop();
                trafficSimulator.stop();
            }

            vehicleSimulator.initialize();
            orderSimulator.initialize();
            trafficSimulator.initialize();

            orderSimulator.setVehicleSimulator(vehicleSimulator);
            trafficSimulator.setVehicleSimulator(vehicleSimulator);

            // 如果有地图，重新注入点位和拓扑图（从工厂模型所有导航地图合并）
            if (currentFactoryModelId != null) {
                List<NavigationMapDTO> navMaps = simulationMapService.listMapsByFactory(currentFactoryModelId);
                List<SimMapPoint> allPoints = new ArrayList<>();
                for (NavigationMapDTO navMap : navMaps) {
                    allPoints.addAll(simulationMapService.loadMapPoints(navMap.getId()));
                }
                orderSimulator.getOrderGenerator().setMapPoints(allPoints.isEmpty() ? null : allPoints);
                if (currentMapGraph == null) {
                    currentMapGraph = simulationMapService.loadMapGraphForFactory(currentFactoryModelId);
                }
                orderSimulator.setMapGraph(currentMapGraph);
            } else {
                orderSimulator.setMapGraph(null);
            }

            simulationEngine.clearModules();
            simulationEngine.addModule(vehicleSimulator);
            simulationEngine.addModule(orderSimulator);
            simulationEngine.addModule(trafficSimulator);

            simulationEngine.start();
            return Map.of("success", true, "message", "仿真启动成功");
        } catch (Exception e) {
            log.error("启动仿真失败: {}", e.getMessage(), e);
            return Map.of("success", false, "message", "启动仿真失败: " + e.getMessage());
        }
    }

    @PostMapping("/pause")
    public Map<String, Object> pauseSimulation() {
        try {
            simulationEngine.pause();
            return Map.of("success", true, "message", "仿真暂停成功");
        } catch (Exception e) {
            log.error("暂停仿真失败: {}", e.getMessage(), e);
            return Map.of("success", false, "message", "暂停仿真失败: " + e.getMessage());
        }
    }

    @PostMapping("/resume")
    public Map<String, Object> resumeSimulation() {
        try {
            simulationEngine.resume();
            return Map.of("success", true, "message", "仿真继续成功");
        } catch (Exception e) {
            log.error("继续仿真失败: {}", e.getMessage(), e);
            return Map.of("success", false, "message", "继续仿真失败: " + e.getMessage());
        }
    }

    @PostMapping("/stop")
    public Map<String, Object> stopSimulation() {
        try {
            simulationEngine.stop();
            // 停止时同步清空车辆和订单，避免残留
            vehicleSimulator.stop();
            orderSimulator.stop();
            trafficSimulator.stop();
            return Map.of("success", true, "message", "仿真停止成功");
        } catch (Exception e) {
            log.error("停止仿真失败: {}", e.getMessage(), e);
            return Map.of("success", false, "message", "停止仿真失败: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public Map<String, Object> getSimulationStatus() {
        try {
            return Map.of(
                    "success", true,
                    "status", simulationEngine.getStatus().name(),
                    "currentTick", simulationEngine.getCurrentTick(),
                    "tickRate", simulationEngine.getTickRate()
            );
        } catch (Exception e) {
            log.error("获取仿真状态失败: {}", e.getMessage(), e);
            return Map.of("success", false, "message", "获取仿真状态失败: " + e.getMessage());
        }
    }

    // ─── 车辆管理 ──────────────────────────────────────────────

    @PostMapping("/vehicle/add")
    public Map<String, Object> addVehicle(@RequestBody Map<String, Object> vehicleData) {
        try {
            String vehicleId = (String) vehicleData.get("vehicleId");
            String name = (String) vehicleData.get("name");
            double maxSpeed = (Double) vehicleData.getOrDefault("maxSpeed", 1.0);
            double acceleration = (Double) vehicleData.getOrDefault("acceleration", 0.5);
            double deceleration = (Double) vehicleData.getOrDefault("deceleration", 0.5);
            double batteryCapacity = (Double) vehicleData.getOrDefault("batteryCapacity", 100.0);

            if (vehicleId == null || vehicleId.trim().isEmpty()) {
                return Map.of("success", false, "message", "车辆ID不能为空");
            }
            if (name == null || name.trim().isEmpty()) {
                return Map.of("success", false, "message", "车辆名称不能为空");
            }
            var vehicle = vehicleSimulator.createVehicle(vehicleId, name, maxSpeed, acceleration, deceleration, batteryCapacity);
            return Map.of("success", true, "message", "车辆添加成功", "vehicle", vehicle);
        } catch (Exception e) {
            log.error("添加车辆失败: {}", e.getMessage(), e);
            return Map.of("success", false, "message", "添加车辆失败: " + e.getMessage());
        }
    }

    @GetMapping("/vehicles")
    public Map<String, Object> getVehicles() {
        try {
            return Map.of("success", true, "vehicles", vehicleSimulator.getVehicles());
        } catch (Exception e) {
            log.error("获取车辆列表失败: {}", e.getMessage(), e);
            return Map.of("success", false, "message", "获取车辆列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/orders")
    public Map<String, Object> getOrders() {
        try {
            return Map.of("success", true, "orders", orderSimulator.getOrders());
        } catch (Exception e) {
            log.error("获取订单列表失败: {}", e.getMessage(), e);
            return Map.of("success", false, "message", "获取订单列表失败: " + e.getMessage());
        }
    }

    /**
     * 批量添加测试车辆（若有地图则初始化到地图点位）
     */
    @PostMapping("/vehicle/batch-add")
    public Map<String, Object> batchAddVehicles(@RequestBody Map<String, Object> body) {
        try {
            int count = ((Number) body.getOrDefault("count", 2)).intValue();
            double maxSpeed = ((Number) body.getOrDefault("maxSpeed", 2.0)).doubleValue();

            List<SimMapPoint> points = currentMapId != null
                    ? simulationMapService.loadMapPoints(currentMapId)
                    : List.of();

            for (int i = 0; i < count; i++) {
                int seq = vehicleSimulator.nextVehicleSeq();
                String vid = String.format("sim-v-%03d", seq);
                String vname = String.format("sim-%03d", seq);
                SimulatedVehicle v = vehicleSimulator.createVehicle(vid, vname, maxSpeed, 0.5, 0.5, 100.0);

                // 初始化位置到地图点位
                if (!points.isEmpty()) {
                    SimMapPoint p = points.get((int) (Math.random() * points.size()));
                    v.setX(p.getX());
                    v.setY(p.getY());
                }
            }
            return Map.of("success", true, "message", "已添加 " + count + " 辆仿真车辆");
        } catch (Exception e) {
            log.error("批量添加车辆失败: {}", e.getMessage(), e);
            return Map.of("success", false, "message", "批量添加车辆失败: " + e.getMessage());
        }
    }

    // ─── 快照 ──────────────────────────────────────────────────

    /**
     * 聚合快照：引擎状态 + 车辆列表 + 订单统计 + 当前地图元数据
     */
    @GetMapping("/snapshot")
    public Map<String, Object> getSnapshot() {
        try {
            List<Map<String, Object>> vehicleList = vehicleSimulator.getVehicles().stream()
                    .map(v -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("vehicleId", v.getVehicleId());
                        m.put("name", v.getName());
                        m.put("state", v.getState().name());
                        m.put("x", v.getX());
                        m.put("y", v.getY());
                        m.put("theta", v.getTheta());
                        m.put("targetX", v.getTargetX());
                        m.put("targetY", v.getTargetY());
                        m.put("distanceToTarget", Math.round(v.getDistanceToTarget() * 10.0) / 10.0);
                        m.put("currentSpeed", Math.round(v.getCurrentSpeed() * 1000.0) / 1000.0);
                        m.put("currentBattery", Math.round(v.getCurrentBattery() * 10.0) / 10.0);
                        // 剩余路径航点（供前端绘制虚线路径）
                        List<Map<String, Double>> route = v.getRemainingRoute().stream()
                                .map(wp -> {
                                    Map<String, Double> wm = new HashMap<>();
                                    wm.put("x", wp.getX());
                                    wm.put("y", wp.getY());
                                    return wm;
                                })
                                .collect(Collectors.toList());
                        m.put("route", route);
                        return m;
                    })
                    .collect(Collectors.toList());

            List<SimulatedOrder> orders = orderSimulator.getOrders();
            Map<String, Long> orderStats = orders.stream()
                    .collect(Collectors.groupingBy(o -> o.getState().name(), Collectors.counting()));

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("engineStatus", simulationEngine.getStatus().name());
            result.put("tick", simulationEngine.getCurrentTick());
            result.put("vehicles", vehicleList);
            result.put("orderStats", orderStats);
            result.put("orderTotal", orders.size());

            // 地图元数据（供前端渲染真实地图底图）
            if (currentMapInfo != null) {
                Map<String, Object> mapMeta = new HashMap<>();
                mapMeta.put("mapId", currentMapId);
                mapMeta.put("name", currentMapInfo.getName());
                mapMeta.put("rasterUrl", currentMapInfo.getRasterUrl());
                mapMeta.put("rasterResolution", currentMapInfo.getRasterResolution());
                mapMeta.put("rasterWidth", currentMapInfo.getRasterWidth());
                mapMeta.put("rasterHeight", currentMapInfo.getRasterHeight());
                mapMeta.put("mapOrigin", currentMapInfo.getMapOrigin());
                result.put("mapInfo", mapMeta);
            }

            return result;
        } catch (Exception e) {
            log.error("获取仿真快照失败: {}", e.getMessage(), e);
            return Map.of("success", false, "message", "获取仿真快照失败: " + e.getMessage());
        }
    }

    // ─── 场景管理（保留原有接口）────────────────────────────────

    @PostMapping("/scene/create")
    public Map<String, Object> createScene(@RequestBody Map<String, Object> sceneData) {
        try {
            String name = (String) sceneData.get("name");
            String description = (String) sceneData.get("description");
            if (name == null || name.trim().isEmpty()) {
                return Map.of("success", false, "message", "场景名称不能为空");
            }
            SimulationScene scene = sceneManager.createScene(name, description);
            return Map.of("success", true, "message", "场景创建成功", "scene", scene);
        } catch (Exception e) {
            log.error("创建场景失败: {}", e.getMessage(), e);
            return Map.of("success", false, "message", "创建场景失败: " + e.getMessage());
        }
    }

    @GetMapping("/scenes")
    public Map<String, Object> getScenes() {
        try {
            return Map.of("success", true, "scenes", sceneManager.getScenes());
        } catch (Exception e) {
            log.error("获取场景列表失败: {}", e.getMessage(), e);
            return Map.of("success", false, "message", "获取场景列表失败: " + e.getMessage());
        }
    }
}
