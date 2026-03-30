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
        this.pathId = Objects.requireNonNull(pathId, "pathId不能为空");
        this.sourcePointId = Objects.requireNonNull(sourcePointId, "sourcePointId不能为空");
        this.destPointId = Objects.requireNonNull(destPointId, "destPointId不能为空");
        this.pathName = pathId;
        this.length = length;
        this.maxVelocity = null;
        this.maxReverseVelocity = null;
        this.properties = new java.util.HashMap<>();
    }

    /**
     * 检查是否可通行
     */
    public boolean isTraversable() {
        return !locked && !blocked;
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
