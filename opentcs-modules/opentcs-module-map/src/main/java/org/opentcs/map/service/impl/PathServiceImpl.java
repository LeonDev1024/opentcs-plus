package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.entity.Path;
import org.opentcs.map.mapper.PathMapper;
import org.opentcs.map.service.PathService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 路径 Service 实现类
 */
@Service
public class PathServiceImpl extends ServiceImpl<PathMapper, Path> implements PathService {

    @Override
    public TableDataInfo<Path> selectPagePath(Path path, PageQuery pageQuery) {
        return this.getBaseMapper().selectPagePath(path, pageQuery);
    }

    @Override
    public List<Path> selectAllPathByPlantModelId(Long plantModelId) {

        return this.list(new LambdaQueryWrapper<>(Path.class)
                .eq(Path::getPlantModelId, plantModelId)
        );
    }
}