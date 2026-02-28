package org.opentcs.algorithm.traffic;

import java.util.*;

/**
 * 交通控制算法
 */
public class TrafficControlAlgorithm {

    /**
     * 检测路径冲突
     * @param paths 所有车辆的路径
     * @return 冲突信息
     */
    public List<Conflict> detectConflicts(List<VehiclePath> paths) {
        List<Conflict> conflicts = new ArrayList<>();

        // 检查每对路径之间的冲突
        for (int i = 0; i < paths.size(); i++) {
            for (int j = i + 1; j < paths.size(); j++) {
                VehiclePath path1 = paths.get(i);
                VehiclePath path2 = paths.get(j);

                // 检查路径是否有冲突
                Conflict conflict = checkPathConflict(path1, path2);
                if (conflict != null) {
                    conflicts.add(conflict);
                }
            }
        }

        return conflicts;
    }

    /**
     * 检查两条路径之间的冲突
     * @param path1 第一条路径
     * @param path2 第二条路径
     * @return 冲突信息，如果没有冲突则返回null
     */
    private Conflict checkPathConflict(VehiclePath path1, VehiclePath path2) {
        // 检查是否有共同的节点
        Set<String> nodes1 = new HashSet<>();
        for (NodeTime nodeTime : path1.getNodeTimes()) {
            nodes1.add(nodeTime.getNodeId());
        }

        for (NodeTime nodeTime : path2.getNodeTimes()) {
            if (nodes1.contains(nodeTime.getNodeId())) {
                // 检查时间是否重叠
                for (NodeTime nt1 : path1.getNodeTimes()) {
                    if (nt1.getNodeId().equals(nodeTime.getNodeId())) {
                        if (checkTimeOverlap(nt1, nodeTime)) {
                            return new Conflict(path1.getVehicleId(), path2.getVehicleId(), nodeTime.getNodeId());
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * 检查时间是否重叠
     * @param nt1 第一个节点时间
     * @param nt2 第二个节点时间
     * @return 是否重叠
     */
    private boolean checkTimeOverlap(NodeTime nt1, NodeTime nt2) {
        return !(nt1.getEndTime() <= nt2.getStartTime() || nt2.getEndTime() <= nt1.getStartTime());
    }

    /**
     * 解决冲突
     * @param conflicts 冲突列表
     * @param paths 所有车辆的路径
     * @return 解决后的路径
     */
    public List<VehiclePath> resolveConflicts(List<Conflict> conflicts, List<VehiclePath> paths) {
        // 这里实现简单的冲突解决策略：让后到达的车辆等待
        for (Conflict conflict : conflicts) {
            VehiclePath path1 = findPathByVehicleId(paths, conflict.getVehicle1Id());
            VehiclePath path2 = findPathByVehicleId(paths, conflict.getVehicle2Id());

            if (path1 != null && path2 != null) {
                NodeTime nt1 = findNodeTimeByNodeId(path1, conflict.getNodeId());
                NodeTime nt2 = findNodeTimeByNodeId(path2, conflict.getNodeId());

                if (nt1 != null && nt2 != null) {
                    // 让后到达的车辆等待
                    if (nt1.getStartTime() < nt2.getStartTime()) {
                        // 调整path2的时间
                        adjustPathTime(path2, nt1.getEndTime() - nt2.getStartTime());
                    } else {
                        // 调整path1的时间
                        adjustPathTime(path1, nt2.getEndTime() - nt1.getStartTime());
                    }
                }
            }
        }

        return paths;
    }

    /**
     * 根据车辆ID查找路径
     * @param paths 路径列表
     * @param vehicleId 车辆ID
     * @return 路径
     */
    private VehiclePath findPathByVehicleId(List<VehiclePath> paths, String vehicleId) {
        for (VehiclePath path : paths) {
            if (path.getVehicleId().equals(vehicleId)) {
                return path;
            }
        }
        return null;
    }

    /**
     * 根据节点ID查找节点时间
     * @param path 路径
     * @param nodeId 节点ID
     * @return 节点时间
     */
    private NodeTime findNodeTimeByNodeId(VehiclePath path, String nodeId) {
        for (NodeTime nodeTime : path.getNodeTimes()) {
            if (nodeTime.getNodeId().equals(nodeId)) {
                return nodeTime;
            }
        }
        return null;
    }

    /**
     * 调整路径的时间
     * @param path 路径
     * @param delay 延迟时间
     */
    private void adjustPathTime(VehiclePath path, double delay) {
        for (NodeTime nodeTime : path.getNodeTimes()) {
            nodeTime.setStartTime(nodeTime.getStartTime() + delay);
            nodeTime.setEndTime(nodeTime.getEndTime() + delay);
        }
    }

    /**
     * 检测死锁
     * @param paths 所有车辆的路径
     * @return 是否存在死锁
     */
    public boolean detectDeadlock(List<VehiclePath> paths) {
        // 这里实现简单的死锁检测：检查是否存在循环等待
        // 构建资源分配图
        Map<String, Set<String>> resourceMap = new HashMap<>();
        Map<String, Set<String>> waitMap = new HashMap<>();

        // 初始化
        for (VehiclePath path : paths) {
            resourceMap.put(path.getVehicleId(), new HashSet<>());
            waitMap.put(path.getVehicleId(), new HashSet<>());
        }

        // 构建资源分配和等待关系
        List<Conflict> conflicts = detectConflicts(paths);
        for (Conflict conflict : conflicts) {
            // 假设vehicle1正在使用资源，vehicle2在等待
            waitMap.get(conflict.getVehicle2Id()).add(conflict.getVehicle1Id());
            resourceMap.get(conflict.getVehicle1Id()).add(conflict.getNodeId());
        }

        // 检查是否存在循环等待
        for (String vehicle : waitMap.keySet()) {
            if (hasCycle(vehicle, waitMap, new HashSet<>())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查是否存在循环等待
     * @param current 当前车辆
     * @param waitMap 等待关系图
     * @param visited 已访问的车辆
     * @return 是否存在循环
     */
    private boolean hasCycle(String current, Map<String, Set<String>> waitMap, Set<String> visited) {
        if (visited.contains(current)) {
            return true;
        }

        visited.add(current);
        for (String neighbor : waitMap.get(current)) {
            if (hasCycle(neighbor, waitMap, new HashSet<>(visited))) {
                return true;
            }
        }

        return false;
    }
}
