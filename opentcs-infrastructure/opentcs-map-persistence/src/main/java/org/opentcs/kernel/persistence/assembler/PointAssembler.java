package org.opentcs.kernel.persistence.assembler;

import org.opentcs.kernel.domain.routing.Point;
import org.opentcs.kernel.persistence.entity.PointEntity;

/**
 * 点位领域模型与数据模型转换器
 */
public class PointAssembler {

    /**
     * 将数据模型转换为领域模型
     *
     * @param entity 数据模型
     * @return 领域模型
     */
    public Point toDomain(PointEntity entity) {
        if (entity == null) {
            return null;
        }

        Point point = new Point(
            String.valueOf(entity.getId()),
            entity.getName(),
            entity.getXPosition() != null ? entity.getXPosition().doubleValue() : 0,
            entity.getYPosition() != null ? entity.getYPosition().doubleValue() : 0,
            entity.getZPosition() != null ? entity.getZPosition().doubleValue() : 0
        );

        return point;
    }

    /**
     * 从领域模型转换为数据模型
     *
     * @param domain  领域模型
     * @param entity  目标数据模型
     */
    public void toEntity(Point domain, PointEntity entity) {
        if (domain == null || entity == null) {
            return;
        }

        entity.setName(domain.getName());
        entity.setXPosition(domain.getX() != 0 ? java.math.BigDecimal.valueOf(domain.getX()) : null);
        entity.setYPosition(domain.getY() != 0 ? java.math.BigDecimal.valueOf(domain.getY()) : null);
        entity.setZPosition(domain.getZ() != 0 ? java.math.BigDecimal.valueOf(domain.getZ()) : null);
    }

    /**
     * 从领域模型复制属性到数据模型
     *
     * @param domain 领域模型
     * @param entity 目标数据模型
     */
    public void copyToDataModel(Point domain, PointEntity entity) {
        toEntity(domain, entity);
    }

    /**
     * 创建领域模型
     */
    public Point createDomain(String pointId, String name, double x, double y) {
        return new Point(pointId, name, x, y);
    }
}
