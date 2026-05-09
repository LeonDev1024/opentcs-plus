package org.opentcs.kernel.domain.routing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 路径
 */
public class Path {

    private final String pathId;
    private final String pathName;
    private final String sourcePointId;
    private final String destPointId;
    private final double length;
    private final Double maxVelocity;
    private final Double maxReverseVelocity;
    private boolean locked;
    private boolean blocked;
    private final java.util.Map<String, String> properties;

    public Path(String pathId, String sourcePointId, String destPointId, double length) {
        this(pathId, sourcePointId, destPointId, length, null, null);
    }

    public Path(String pathId, String sourcePointId, String destPointId, double length,
                Double maxVelocity, Double maxReverseVelocity) {
        this.pathId = Objects.requireNonNull(pathId, "pathId不能为空");
        this.sourcePointId = Objects.requireNonNull(sourcePointId, "sourcePointId不能为空");
        this.destPointId = Objects.requireNonNull(destPointId, "destPointId不能为空");
        this.pathName = pathId;
        this.length = length;
        this.maxVelocity = maxVelocity;
        this.maxReverseVelocity = maxReverseVelocity;
        this.properties = new java.util.HashMap<>();
    }

    /**
     * 检查是否可通行
     */
    public boolean isTraversable() {
        return !locked && !blocked && !isTruthy(properties.get("temporaryBlocked"));
    }

    public boolean isBidirectional() {
        if (isTruthy(properties.get("oneWay"))) {
            return false;
        }
        if (isFalsy(properties.get("bidirectional"))) {
            return false;
        }
        String routingType = properties.get("routingType");
        if (routingType == null || routingType.isBlank()) {
            return true;
        }
        return !List.of("ONE_WAY", "ONEWAY", "DIRECTED", "FORWARD")
                .contains(routingType.trim().toUpperCase());
    }

    public double travelCost() {
        if (!isTraversable()) {
            return Double.MAX_VALUE;
        }
        double safeLength = length > 0 ? length : 1;
        if (maxVelocity == null || maxVelocity <= 0) {
            return applyDynamicCost(safeLength);
        }
        return applyDynamicCost(safeLength / maxVelocity);
    }

    public Path reverseCopy() {
        Path reverse = new Path(
                pathId + "_reverse",
                destPointId,
                sourcePointId,
                length,
                maxReverseVelocity,
                maxVelocity
        );
        reverse.properties.putAll(properties);
        if (locked) {
            reverse.lock();
        }
        if (blocked) {
            reverse.block();
        }
        return reverse;
    }

    /**
     * 锁定路径
     */
    public void lock() {
        this.locked = true;
    }

    /**
     * 解锁路径
     */
    public void unlock() {
        this.locked = false;
    }

    /**
     * 阻塞路径
     */
    public void block() {
        this.blocked = true;
    }

    /**
     * 解除阻塞
     */
    public void unblock() {
        this.blocked = false;
    }

    // Getters
    public String getPathId() {
        return pathId;
    }

    public String getPathName() {
        return pathName;
    }

    public String getSourcePointId() {
        return sourcePointId;
    }

    public String getDestPointId() {
        return destPointId;
    }

    public double getLength() {
        return length;
    }

    public Double getMaxVelocity() {
        return maxVelocity;
    }

    public Double getMaxReverseVelocity() {
        return maxReverseVelocity;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public java.util.Map<String, String> getProperties() {
        return properties;
    }

    private boolean isTruthy(String value) {
        return value != null && List.of("true", "1", "yes", "y").contains(value.trim().toLowerCase());
    }

    private boolean isFalsy(String value) {
        return value != null && List.of("false", "0", "no", "n").contains(value.trim().toLowerCase());
    }

    private double applyDynamicCost(double baseCost) {
        double cost = baseCost * doubleProperty("costMultiplier", 1.0);
        cost *= doubleProperty("congestionMultiplier", 1.0);
        cost *= doubleProperty("slowZoneMultiplier", 1.0);
        cost += doubleProperty("costPenalty", 0.0);
        cost += doubleProperty("congestionCost", 0.0);
        cost += doubleProperty("resourceCost", 0.0);
        return Math.max(cost, 0.0);
    }

    private double doubleProperty(String key, double defaultValue) {
        String value = properties.get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return Objects.equals(pathId, path.pathId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathId);
    }
}
