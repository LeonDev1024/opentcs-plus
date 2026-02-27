package org.opentcs.simulation.controller;

import lombok.extern.slf4j.Slf4j;
import org.opentcs.simulation.core.SimulationEngine;
import org.opentcs.simulation.core.SimulationScene;
import org.opentcs.simulation.core.SimulationSceneManager;
import org.opentcs.simulation.order.OrderSimulator;
import org.opentcs.simulation.traffic.TrafficSimulator;
import org.opentcs.simulation.vehicle.VehicleSimulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
    
    /**
     * 启动仿真
     * @return 响应
     */
    @PostMapping("/start")
    public Map<String, Object> startSimulation() {
        log.info("Starting simulation...");
        
        try {
            // 初始化模拟器
            vehicleSimulator.initialize();
            orderSimulator.initialize();
            trafficSimulator.initialize();
            
            // 设置模拟器之间的依赖
            orderSimulator.setVehicleSimulator(vehicleSimulator);
            trafficSimulator.setVehicleSimulator(vehicleSimulator);
            
            // 添加模拟器到仿真引擎
            simulationEngine.addModule(vehicleSimulator);
            simulationEngine.addModule(orderSimulator);
            simulationEngine.addModule(trafficSimulator);
            
            // 启动仿真
            simulationEngine.start();
            
            return Map.of(
                "success", true,
                "message", "仿真启动成功"
            );
        } catch (Exception e) {
            log.error("启动仿真失败: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "message", "启动仿真失败: " + e.getMessage()
            );
        }
    }
    
    /**
     * 暂停仿真
     * @return 响应
     */
    @PostMapping("/pause")
    public Map<String, Object> pauseSimulation() {
        log.info("Pausing simulation...");
        
        try {
            simulationEngine.pause();
            return Map.of(
                "success", true,
                "message", "仿真暂停成功"
            );
        } catch (Exception e) {
            log.error("暂停仿真失败: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "message", "暂停仿真失败: " + e.getMessage()
            );
        }
    }
    
    /**
     * 继续仿真
     * @return 响应
     */
    @PostMapping("/resume")
    public Map<String, Object> resumeSimulation() {
        log.info("Resuming simulation...");
        
        try {
            simulationEngine.resume();
            return Map.of(
                "success", true,
                "message", "仿真继续成功"
            );
        } catch (Exception e) {
            log.error("继续仿真失败: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "message", "继续仿真失败: " + e.getMessage()
            );
        }
    }
    
    /**
     * 停止仿真
     * @return 响应
     */
    @PostMapping("/stop")
    public Map<String, Object> stopSimulation() {
        log.info("Stopping simulation...");
        
        try {
            simulationEngine.stop();
            return Map.of(
                "success", true,
                "message", "仿真停止成功"
            );
        } catch (Exception e) {
            log.error("停止仿真失败: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "message", "停止仿真失败: " + e.getMessage()
            );
        }
    }
    
    /**
     * 获取仿真状态
     * @return 响应
     */
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
            return Map.of(
                "success", false,
                "message", "获取仿真状态失败: " + e.getMessage()
            );
        }
    }
    
    /**
     * 创建仿真场景
     * @param sceneData 场景数据
     * @return 响应
     */
    @PostMapping("/scene/create")
    public Map<String, Object> createScene(@RequestBody Map<String, Object> sceneData) {
        try {
            String name = (String) sceneData.get("name");
            String description = (String) sceneData.get("description");
            
            // 参数验证
            if (name == null || name.trim().isEmpty()) {
                return Map.of(
                    "success", false,
                    "message", "场景名称不能为空"
                );
            }
            
            SimulationScene scene = sceneManager.createScene(name, description);
            
            return Map.of(
                "success", true,
                "message", "场景创建成功",
                "scene", scene
            );
        } catch (Exception e) {
            log.error("创建场景失败: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "message", "创建场景失败: " + e.getMessage()
            );
        }
    }
    
    /**
     * 获取所有场景
     * @return 响应
     */
    @GetMapping("/scenes")
    public Map<String, Object> getScenes() {
        try {
            List<SimulationScene> scenes = sceneManager.getScenes();
            return Map.of(
                "success", true,
                "scenes", scenes
            );
        } catch (Exception e) {
            log.error("获取场景列表失败: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "message", "获取场景列表失败: " + e.getMessage()
            );
        }
    }
    
    /**
     * 设置当前场景
     * @param sceneId 场景ID
     * @return 响应
     */
    @PostMapping("/scene/set-current/{sceneId}")
    public Map<String, Object> setCurrentScene(@PathVariable String sceneId) {
        try {
            // 参数验证
            if (sceneId == null || sceneId.trim().isEmpty()) {
                return Map.of(
                    "success", false,
                    "message", "场景ID不能为空"
                );
            }
            
            // 这里需要根据sceneId找到场景，暂时返回成功
            return Map.of(
                "success", true,
                "message", "场景设置成功"
            );
        } catch (Exception e) {
            log.error("设置场景失败: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "message", "设置场景失败: " + e.getMessage()
            );
        }
    }
    
    /**
     * 添加模拟车辆
     * @param vehicleData 车辆数据
     * @return 响应
     */
    @PostMapping("/vehicle/add")
    public Map<String, Object> addVehicle(@RequestBody Map<String, Object> vehicleData) {
        try {
            String vehicleId = (String) vehicleData.get("vehicleId");
            String name = (String) vehicleData.get("name");
            double maxSpeed = (Double) vehicleData.getOrDefault("maxSpeed", 1.0);
            double acceleration = (Double) vehicleData.getOrDefault("acceleration", 0.5);
            double deceleration = (Double) vehicleData.getOrDefault("deceleration", 0.5);
            double batteryCapacity = (Double) vehicleData.getOrDefault("batteryCapacity", 100.0);
            
            // 参数验证
            if (vehicleId == null || vehicleId.trim().isEmpty()) {
                return Map.of(
                    "success", false,
                    "message", "车辆ID不能为空"
                );
            }
            if (name == null || name.trim().isEmpty()) {
                return Map.of(
                    "success", false,
                    "message", "车辆名称不能为空"
                );
            }
            
            var vehicle = vehicleSimulator.createVehicle(vehicleId, name, maxSpeed, acceleration, deceleration, batteryCapacity);
            
            return Map.of(
                "success", true,
                "message", "车辆添加成功",
                "vehicle", vehicle
            );
        } catch (Exception e) {
            log.error("添加车辆失败: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "message", "添加车辆失败: " + e.getMessage()
            );
        }
    }
    
    /**
     * 获取所有模拟车辆
     * @return 响应
     */
    @GetMapping("/vehicles")
    public Map<String, Object> getVehicles() {
        try {
            var vehicles = vehicleSimulator.getVehicles();
            return Map.of(
                "success", true,
                "vehicles", vehicles
            );
        } catch (Exception e) {
            log.error("获取车辆列表失败: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "message", "获取车辆列表失败: " + e.getMessage()
            );
        }
    }
    
    /**
     * 获取所有模拟订单
     * @return 响应
     */
    @GetMapping("/orders")
    public Map<String, Object> getOrders() {
        try {
            var orders = orderSimulator.getOrders();
            return Map.of(
                "success", true,
                "orders", orders
            );
        } catch (Exception e) {
            log.error("获取订单列表失败: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "message", "获取订单列表失败: " + e.getMessage()
            );
        }
    }
}