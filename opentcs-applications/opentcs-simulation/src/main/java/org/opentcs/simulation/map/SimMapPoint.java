package org.opentcs.simulation.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仿真用地图点位（坐标单位：m，由 mm 转换而来）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimMapPoint {
    private String pointId;
    private String name;
    private double x; // m
    private double y; // m
}
