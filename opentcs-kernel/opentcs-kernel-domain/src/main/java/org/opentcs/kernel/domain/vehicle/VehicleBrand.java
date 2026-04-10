package org.opentcs.kernel.domain.vehicle;

import java.util.Objects;

/**
 * 车辆品牌聚合根。
 * <p>
 * 纯领域模型，无持久化依赖。
 * 品牌是车辆体系的顶层概念：品牌 → 车辆类型 → 车辆实例。
 * </p>
 */
public class VehicleBrand {

    private final String brandId;
    private String name;
    private String code;
    private String logo;
    private String website;
    private String description;
    private String contact;
    private boolean enabled;
    private int sort;

    public VehicleBrand(String brandId, String name, String code) {
        this.brandId = Objects.requireNonNull(brandId, "brandId不能为空");
        this.name = Objects.requireNonNull(name, "品牌名称不能为空");
        this.code = Objects.requireNonNull(code, "品牌缩写不能为空");
        this.enabled = true;
    }

    // ===== 领域行为 =====

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public void updateInfo(String name, String code, String logo, String website,
                           String description, String contact) {
        this.name = Objects.requireNonNull(name, "品牌名称不能为空");
        this.code = Objects.requireNonNull(code, "品牌缩写不能为空");
        this.logo = logo;
        this.website = website;
        this.description = description;
        this.contact = contact;
    }

    // ===== Getters =====

    public String getBrandId() { return brandId; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public boolean isEnabled() { return enabled; }
    public int getSort() { return sort; }
    public void setSort(int sort) { this.sort = sort; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehicleBrand)) return false;
        return Objects.equals(brandId, ((VehicleBrand) o).brandId);
    }

    @Override
    public int hashCode() { return Objects.hash(brandId); }

    @Override
    public String toString() {
        return "VehicleBrand{brandId='" + brandId + "', name='" + name + "', code='" + code + "', enabled=" + enabled + '}';
    }
}
