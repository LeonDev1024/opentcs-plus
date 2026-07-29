package org.opentcs.order.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.order.persistence.entity.TaskTemplateEntity;
import org.opentcs.order.persistence.mapper.TaskTemplateMapper;
import org.opentcs.order.persistence.service.TaskTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 任务模板仓储实现
 */
@Service
public class TaskTemplateRepositoryImpl extends ServiceImpl<TaskTemplateMapper, TaskTemplateEntity>
        implements TaskTemplateRepository {

    @Override
    public TableDataInfo<TaskTemplateEntity> selectPage(TaskTemplateEntity query, PageQuery pageQuery) {
        LambdaQueryWrapper<TaskTemplateEntity> wrapper = buildWrapper(query);
        wrapper.orderByDesc(TaskTemplateEntity::getCreateTime);
        Page<TaskTemplateEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        IPage<TaskTemplateEntity> result = this.page(page, wrapper);
        return TableDataInfo.build(result);
    }

    @Override
    public List<TaskTemplateEntity> selectEnabledList() {
        return this.list(new LambdaQueryWrapper<TaskTemplateEntity>()
                .eq(TaskTemplateEntity::getEnabled, true)
                .eq(TaskTemplateEntity::getDelFlag, "0")
                .orderByAsc(TaskTemplateEntity::getCode));
    }

    @Override
    public TaskTemplateEntity getByCode(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return this.getOne(new LambdaQueryWrapper<TaskTemplateEntity>()
                .eq(TaskTemplateEntity::getCode, code.trim())
                .eq(TaskTemplateEntity::getDelFlag, "0")
                .last("LIMIT 1"));
    }

    private LambdaQueryWrapper<TaskTemplateEntity> buildWrapper(TaskTemplateEntity query) {
        LambdaQueryWrapper<TaskTemplateEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskTemplateEntity::getDelFlag, "0");
        if (query == null) {
            return wrapper;
        }
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            wrapper.and(w -> w.like(TaskTemplateEntity::getCode, keyword)
                    .or()
                    .like(TaskTemplateEntity::getName, keyword));
        } else {
            wrapper.like(StringUtils.hasText(query.getCode()), TaskTemplateEntity::getCode, query.getCode())
                    .like(StringUtils.hasText(query.getName()), TaskTemplateEntity::getName, query.getName());
        }
        wrapper.eq(query.getEnabled() != null, TaskTemplateEntity::getEnabled, query.getEnabled());
        return wrapper;
    }
}
