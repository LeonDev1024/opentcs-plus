package org.opentcs.order.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.order.persistence.entity.TaskTemplateEntity;

import java.util.List;

/**
 * 任务模板仓储
 */
public interface TaskTemplateRepository extends IService<TaskTemplateEntity> {

    TableDataInfo<TaskTemplateEntity> selectPage(TaskTemplateEntity query, PageQuery pageQuery);

    List<TaskTemplateEntity> selectEnabledList();

    TaskTemplateEntity getByCode(String code);
}
