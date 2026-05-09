package org.opentcs.simulation.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仿真地图有向边（路径）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimMapEdge {
    /** 起点 pointId */
    private String fromId;
    /** 终点 pointId */
    private String toId;
    /** 路径长度（m） */
    private double length;
}
