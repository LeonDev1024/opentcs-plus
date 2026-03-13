package org.opentcs.kernel.api.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * 运输订单数据传输对象
 */
public class TransportOrderDTO {

    private String orderId;

    @NotBlank(message = "订单名称不能为空")
    private String name;

    private String orderNo;

    private OrderStateDTO state;

    private String intendedVehicle;

    private String processingVehicle;

    private List<OrderStepDTO> steps;

    private Long creationTime;

    private Long finishedTime;

    private Long deadline;

    private Map<String, String> properties;

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public OrderStateDTO getState() {
        return state;
    }

    public void setState(OrderStateDTO state) {
        this.state = state;
    }

    public String getIntendedVehicle() {
        return intendedVehicle;
    }

    public void setIntendedVehicle(String intendedVehicle) {
        this.intendedVehicle = intendedVehicle;
    }

    public String getProcessingVehicle() {
        return processingVehicle;
    }

    public void setProcessingVehicle(String processingVehicle) {
        this.processingVehicle = processingVehicle;
    }

    public List<OrderStepDTO> getSteps() {
        return steps;
    }

    public void setSteps(List<OrderStepDTO> steps) {
        this.steps = steps;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public Long getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(Long finishedTime) {
        this.finishedTime = finishedTime;
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
