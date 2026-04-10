package org.opentcs.kernel.api.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

/**
 * 车辆数据传输对象
 */
public class VehicleDTO {

    @NotBlank(message = "车辆ID不能为空")
    private String vehicleId;

    private String name;

    /** 车辆类型 ID（对应 VehicleType.typeId） */
    private String typeId;

    /** @deprecated 使用 {@link #typeId} 替代 */
    @Deprecated
    private String vehicleType;

    private VehicleStateDTO state;

    private PositionDTO position;

    private String currentOrderId;

    private Double energyLevel;

    private Map<String, String> properties;

    private Long createTime;

    private Long updateTime;

    // Getters and Setters
    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    /** @deprecated 使用 {@link #getTypeId()} */
    @Deprecated
    public String getVehicleType() {
        return typeId;
    }

    /** @deprecated 使用 {@link #setTypeId(String)} */
    @Deprecated
    public void setVehicleType(String vehicleType) {
        this.typeId = vehicleType;
    }

    public VehicleStateDTO getState() {
        return state;
    }

    public void setState(VehicleStateDTO state) {
        this.state = state;
    }

    public PositionDTO getPosition() {
        return position;
    }

    public void setPosition(PositionDTO position) {
        this.position = position;
    }

    public String getCurrentOrderId() {
        return currentOrderId;
    }

    public void setCurrentOrderId(String currentOrderId) {
        this.currentOrderId = currentOrderId;
    }

    public Double getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(Double energyLevel) {
        this.energyLevel = energyLevel;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}
