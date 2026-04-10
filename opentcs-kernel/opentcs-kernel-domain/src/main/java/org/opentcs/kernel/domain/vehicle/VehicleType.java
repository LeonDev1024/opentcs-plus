package org.opentcs.kernel.domain.vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 车辆类型聚合根。
 * <p>
 * 纯领域模型，无持久化依赖。
 * 车辆类型隶属于某个品牌，定义该类型车辆的物理规格和能力边界。
 * 层级关系：VehicleBrand → VehicleType → Vehicle
 * </p>
 */
public class VehicleType {

    private final String typeId;
    private final String brandId;
    private String name;
    private VehicleTypeSpec spec;
    private List<String> allowedOrderTypes;
    private List<String> allowedPeripheralOperations;
    private boolean enabled;

    public VehicleType(String typeId, String brandId, String name, VehicleTypeSpec spec) {
        this.typeId = Objects.requireNonNull(typeId, "typeId不能为空");
        this.brandId = Objects.requireNonNull(brandId, "brandId不能为空");
        this.name = Objects.requireNonNull(name, "类型名称不能为空");
        this.spec = Objects.requireNonNull(spec, "规格不能为空");
        this.allowedOrderTypes = new ArrayList<>();
        this.allowedPeripheralOperations = new ArrayList<>();
        this.enabled = true;
    }

    // ===== 领域行为 =====

    public void enable() { this.enabled = true; }

    public void disable() { this.enabled = false; }

    public void updateSpec(VehicleTypeSpec newSpec) {
        this.spec = Objects.requireNonNull(newSpec, "规格不能为空");
    }

    public void updateName(String name) {
        this.name = Objects.requireNonNull(name, "类型名称不能为空");
    }

    public void addAllowedOrderType(String orderType) {
        if (!allowedOrderTypes.contains(orderType)) {
            allowedOrderTypes.add(orderType);
        }
    }

    public void removeAllowedOrderType(String orderType) {
        allowedOrderTypes.remove(orderType);
    }

    /**
     * 检查该类型车辆是否支持指定订单类型。
     * 若允许列表为空，默认支持所有订单类型。
     */
    public boolean isCompatibleWithOrderType(String orderType) {
        return allowedOrderTypes.isEmpty() || allowedOrderTypes.contains(orderType);
    }

    /**
     * 判断当前能量水平是否需要充电。
     */
    public boolean needsCharging(double currentEnergyLevel) {
        return spec.isCriticalEnergy(currentEnergyLevel);
    }

    // ===== Getters =====

    public String getTypeId() { return typeId; }
    public String getBrandId() { return brandId; }
    public String getName() { return name; }
    public VehicleTypeSpec getSpec() { return spec; }
    public boolean isEnabled() { return enabled; }

    public List<String> getAllowedOrderTypes() {
        return Collections.unmodifiableList(allowedOrderTypes);
    }

    public void setAllowedOrderTypes(List<String> types) {
        this.allowedOrderTypes = new ArrayList<>(types);
    }

    public List<String> getAllowedPeripheralOperations() {
        return Collections.unmodifiableList(allowedPeripheralOperations);
    }

    public void setAllowedPeripheralOperations(List<String> ops) {
        this.allowedPeripheralOperations = new ArrayList<>(ops);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehicleType)) return false;
        return Objects.equals(typeId, ((VehicleType) o).typeId);
    }

    @Override
    public int hashCode() { return Objects.hash(typeId); }

    @Override
    public String toString() {
        return "VehicleType{typeId='" + typeId + "', brandId='" + brandId + "', name='" + name + "'}";
    }
}
