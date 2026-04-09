package org.opentcs.kernel.api.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

/**
 * 订单规格数据传输对象
 * 用于创建订单时的请求参数
 */
public class OrderSpecDTO {

    private String name;

    @NotEmpty(message = "订单步骤不能为空")
    private List<OrderStepDTO> steps;

    private String intendedVehicle;  // 指定车辆（可选）

    private String sourcePointId;  // 起点（用于路径规划）

    private String destPointId;  // 终点（用于路径规划）

    private Long deadline;  // 截止时间戳

    private Map<String, String> properties;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OrderStepDTO> getSteps() {
        return steps;
    }

    public void setSteps(List<OrderStepDTO> steps) {
        this.steps = steps;
    }

    public String getIntendedVehicle() {
        return intendedVehicle;
    }

    public void setIntendedVehicle(String intendedVehicle) {
        this.intendedVehicle = intendedVehicle;
    }

    public String getSourcePointId() {
        return sourcePointId;
    }

    public void setSourcePointId(String sourcePointId) {
        this.sourcePointId = sourcePointId;
    }

    public String getDestPointId() {
        return destPointId;
    }

    public void setDestPointId(String destPointId) {
        this.destPointId = destPointId;
    }

    public Long getDeadline() {
        return deadline;
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
