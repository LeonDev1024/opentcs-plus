package org.opentcs.kernel.api.dto;

/**
 * 路径段数据传输对象
 */
public class PathDTO {

    private String pathId;

    private String pathName;

    private String sourcePointId;

    private String destPointId;

    private Double length;

    private Double maxVelocity;

    private Double maxReverseVelocity;

    // Getters and Setters
    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
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

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(Double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public Double getMaxReverseVelocity() {
        return maxReverseVelocity;
    }

    public void setMaxReverseVelocity(Double maxReverseVelocity) {
        this.maxReverseVelocity = maxReverseVelocity;
    }
}
