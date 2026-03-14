package org.opentcs.kernel.persistence.assembler;

import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.persistence.entity.PathEntity;

/**
 * 路径领域模型与数据模型转换器
 */
public class PathAssembler {

    /**
     * 将数据模型转换为领域模型
     *
     * @param entity 数据模型
     * @return 领域模型
     */
    public Path toDomain(PathEntity entity) {
        if (entity == null) {
            return null;
        }

        Path path = new Path(
            String.valueOf(entity.getId()),
            String.valueOf(entity.getSourcePointId()),
            String.valueOf(entity.getDestPointId()),
            entity.getLength() != null ? entity.getLength().doubleValue() : 0
        );

        return path;
    }

    /**
     * 从领域模型转换为数据模型
     *
     * @param domain  领域模型
     * @param entity  目标数据模型
     */
    public void toEntity(Path domain, PathEntity entity) {
        if (domain == null || entity == null) {
            return;
        }

        entity.setName(domain.getPathName());
        entity.setSourcePointId(domain.getSourcePointId() != null ? Long.parseLong(domain.getSourcePointId()) : null);
        entity.setDestPointId(domain.getDestPointId() != null ? Long.parseLong(domain.getDestPointId()) : null);
        entity.setLength(domain.getLength() > 0 ? java.math.BigDecimal.valueOf(domain.getLength()) : null);
    }

    /**
     * 从领域模型复制属性到数据模型
     *
     * @param domain 领域模型
     * @param entity 目标数据模型
     */
    public void copyToDataModel(Path domain, PathEntity entity) {
        toEntity(domain, entity);
    }

    /**
     * 创建领域模型
     */
    public Path createDomain(String pathId, String sourcePointId, String destPointId) {
        return new Path(pathId, sourcePointId, destPointId, 0);
    }
}
