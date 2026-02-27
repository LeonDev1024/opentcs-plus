package org.opentcs.monitor.service;

import org.opentcs.vehicle.service.VehicleService;
import org.opentcs.vehicle.domain.entity.Vehicle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 车辆监控服务
 */
public class VehicleMonitorService {
    private boolean running;
    private ScheduledExecutorService executorService;
    private Map<String, Object> vehicleStatus;
    private VehicleService vehicleService;

    public VehicleMonitorService() {
        this.vehicleStatus = new HashMap<>();
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        // 这里应该注入 vehicleService，暂时使用 null
        this.vehicleService = null;
    }

    /**
     * 启动监控服务
     */
    public void start() {
        running = true;
        // 每2秒采集一次车辆状态
        executorService.scheduleAtFixedRate(this::collectVehicleStatus, 0, 2, TimeUnit.SECONDS);
    }

    /**
     * 停止监控服务
     */
    public void stop() {
        running = false;
        executorService.shutdown();
    }

    /**
     * 采集车辆状态
     */
    private void collectVehicleStatus() {
        if (!running) {
            return;
        }

        if (vehicleService != null) {
            // 从车辆服务获取所有车辆状态
            List<Vehicle> vehicles = vehicleService.getAllVehicleStatus();
            Map<String, Object> vehicleMap = new HashMap<>();

            for (Vehicle vehicle : vehicles) {
                Map<String, Object> vehicleInfo = new HashMap<>();
                vehicleInfo.put("id", vehicle.getId());
                vehicleInfo.put("name", vehicle.getName());
                vehicleInfo.put("state", vehicle.getState());
                vehicleInfo.put("currentPosition", vehicle.getCurrentPosition());
                vehicleInfo.put("nextPosition", vehicle.getNextPosition());
                vehicleInfo.put("energyLevel", vehicle.getEnergyLevel());
                vehicleInfo.put("currentTransportOrder", vehicle.getCurrentTransportOrder());
                vehicleInfo.put("integrationLevel", vehicle.getIntegrationLevel());

                vehicleMap.put(vehicle.getName(), vehicleInfo);
            }

            vehicleStatus.put("vehicles", vehicleMap);
        } else {
            // 模拟车辆数据
            simulateVehicleData();
        }

        // 计算车辆统计数据
        calculateVehicleStatistics();
    }

    /**
     * 模拟车辆数据
     */
    private void simulateVehicleData() {
        Map<String, Object> vehicleMap = new HashMap<>();

        // 模拟车辆1
        Map<String, Object> vehicle1 = new HashMap<>();
        vehicle1.put("id", 1);
        vehicle1.put("name", "AGV-001");
        vehicle1.put("state", "WORKING");
        vehicle1.put("currentPosition", "Point-A");
        vehicle1.put("nextPosition", "Point-B");
        vehicle1.put("energyLevel", 85.5);
        vehicle1.put("currentTransportOrder", "Order-001");
        vehicle1.put("integrationLevel", "TO_BE_UTILIZED");
        vehicleMap.put("AGV-001", vehicle1);

        // 模拟车辆2
        Map<String, Object> vehicle2 = new HashMap<>();
        vehicle2.put("id", 2);
        vehicle2.put("name", "AGV-002");
        vehicle2.put("state", "IDLE");
        vehicle2.put("currentPosition", "Point-C");
        vehicle2.put("nextPosition", null);
        vehicle2.put("energyLevel", 60.2);
        vehicle2.put("currentTransportOrder", null);
        vehicle2.put("integrationLevel", "TO_BE_UTILIZED");
        vehicleMap.put("AGV-002", vehicle2);

        // 模拟车辆3
        Map<String, Object> vehicle3 = new HashMap<>();
        vehicle3.put("id", 3);
        vehicle3.put("name", "AGV-003");
        vehicle3.put("state", "CHARGING");
        vehicle3.put("currentPosition", "Charging-Station");
        vehicle3.put("nextPosition", null);
        vehicle3.put("energyLevel", 20.0);
        vehicle3.put("currentTransportOrder", null);
        vehicle3.put("integrationLevel", "TO_BE_UTILIZED");
        vehicleMap.put("AGV-003", vehicle3);

        vehicleStatus.put("vehicles", vehicleMap);
    }

    /**
     * 计算车辆统计数据
     */
    private void calculateVehicleStatistics() {
        Map<String, Object> vehicles = (Map<String, Object>) vehicleStatus.getOrDefault("vehicles", new HashMap<>());
        int totalVehicles = vehicles.size();
        int workingVehicles = 0;
        int idleVehicles = 0;
        int chargingVehicles = 0;
        int errorVehicles = 0;
        int unavailableVehicles = 0;
        double totalEnergy = 0;

        for (Object vehicleObj : vehicles.values()) {
            Map<String, Object> vehicle = (Map<String, Object>) vehicleObj;
            String state = (String) vehicle.get("state");
            Double energyLevel = (Double) vehicle.get("energyLevel");

            switch (state) {
                case "WORKING":
                    workingVehicles++;
                    break;
                case "IDLE":
                    idleVehicles++;
                    break;
                case "CHARGING":
                    chargingVehicles++;
                    break;
                case "ERROR":
                    errorVehicles++;
                    break;
                case "UNAVAILABLE":
                    unavailableVehicles++;
                    break;
            }

            if (energyLevel != null) {
                totalEnergy += energyLevel;
            }
        }

        double averageEnergy = totalVehicles > 0 ? totalEnergy / totalVehicles : 0;
        double utilizationRate = totalVehicles > 0 ? (double) workingVehicles / totalVehicles * 100 : 0;

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalVehicles", totalVehicles);
        statistics.put("workingVehicles", workingVehicles);
        statistics.put("idleVehicles", idleVehicles);
        statistics.put("chargingVehicles", chargingVehicles);
        statistics.put("errorVehicles", errorVehicles);
        statistics.put("unavailableVehicles", unavailableVehicles);
        statistics.put("averageEnergy", averageEnergy);
        statistics.put("utilizationRate", utilizationRate);

        vehicleStatus.put("statistics", statistics);
    }

    /**
     * 获取车辆状态
     * @return 车辆状态
     */
    public Map<String, Object> getVehicleStatus() {
        return vehicleStatus;
    }

    /**
     * 获取单个车辆状态
     * @param vehicleId 车辆ID
     * @return 车辆状态
     */
    public Map<String, Object> getVehicleStatus(String vehicleId) {
        Map<String, Object> vehicles = (Map<String, Object>) vehicleStatus.getOrDefault("vehicles", new HashMap<>());
        return (Map<String, Object>) vehicles.getOrDefault(vehicleId, new HashMap<>());
    }

    /**
     * 获取车辆统计数据
     * @return 车辆统计数据
     */
    public Map<String, Object> getVehicleStatistics() {
        return (Map<String, Object>) vehicleStatus.getOrDefault("statistics", new HashMap<>());
    }
}
