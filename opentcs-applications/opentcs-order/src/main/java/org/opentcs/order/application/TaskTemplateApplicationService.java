package org.opentcs.order.application;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.order.application.bo.TaskTemplateBO;
import org.opentcs.order.persistence.entity.TaskTemplateEntity;
import org.opentcs.order.persistence.service.TaskTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务模板应用服务（极简 CRUD）
 */
@Service
@RequiredArgsConstructor
public class TaskTemplateApplicationService {

    private final TaskTemplateRepository taskTemplateRepository;

    public TableDataInfo<TaskTemplateBO> list(TaskTemplateBO query, PageQuery pageQuery) {
        TableDataInfo<TaskTemplateEntity> entityPage =
                taskTemplateRepository.selectPage(toEntity(query), pageQuery);
        TableDataInfo<TaskTemplateBO> result = new TableDataInfo<>();
        result.setTotal(entityPage.getTotal());
        result.setCode(entityPage.getCode());
        result.setMsg(entityPage.getMsg());
        result.setRows(entityPage.getRows() == null ? List.of()
                : entityPage.getRows().stream().map(this::toBO).collect(Collectors.toList()));
        return result;
    }

    public List<TaskTemplateBO> listEnabled() {
        return taskTemplateRepository.selectEnabledList().stream()
                .map(this::toBO)
                .collect(Collectors.toList());
    }

    public TaskTemplateBO getById(Long id) {
        return toBO(taskTemplateRepository.getById(id));
    }

    public boolean create(TaskTemplateBO bo) {
        validateRequired(bo);
        ensureCodeUnique(bo.getCode(), null);
        TaskTemplateEntity entity = toEntity(bo);
        entity.setId(null);
        if (entity.getEnabled() == null) {
            entity.setEnabled(true);
        }
        if (entity.getPriority() == null) {
            entity.setPriority(0);
        }
        entity.setDelFlag("0");
        return taskTemplateRepository.save(entity);
    }

    public boolean update(TaskTemplateBO bo) {
        if (bo == null || bo.getId() == null) {
            throw new IllegalArgumentException("模板 ID 不能为空");
        }
        validateRequired(bo);
        ensureCodeUnique(bo.getCode(), bo.getId());
        TaskTemplateEntity existing = taskTemplateRepository.getById(bo.getId());
        if (existing == null || !"0".equals(existing.getDelFlag())) {
            throw new IllegalArgumentException("任务模板不存在");
        }
        TaskTemplateEntity entity = toEntity(bo);
        entity.setDelFlag("0");
        return taskTemplateRepository.updateById(entity);
    }

    public boolean delete(Long id) {
        TaskTemplateEntity entity = taskTemplateRepository.getById(id);
        if (entity == null) {
            return false;
        }
        entity.setDelFlag("2");
        return taskTemplateRepository.updateById(entity);
    }

    public boolean changeStatus(Long id, Boolean enabled) {
        if (id == null || enabled == null) {
            throw new IllegalArgumentException("状态参数不完整");
        }
        TaskTemplateEntity entity = new TaskTemplateEntity();
        entity.setId(id);
        entity.setEnabled(enabled);
        return taskTemplateRepository.updateById(entity);
    }

    private void validateRequired(TaskTemplateBO bo) {
        if (bo == null) {
            throw new IllegalArgumentException("任务模板不能为空");
        }
        if (!StringUtils.hasText(bo.getCode())) {
            throw new IllegalArgumentException("模板号不能为空");
        }
        if (!StringUtils.hasText(bo.getName())) {
            throw new IllegalArgumentException("模板名称不能为空");
        }
        if (bo.getNavigationMapId() == null) {
            throw new IllegalArgumentException("所属地图不能为空");
        }
        if (!StringUtils.hasText(bo.getSourcePoint())) {
            throw new IllegalArgumentException("起点不能为空");
        }
        if (!StringUtils.hasText(bo.getDestPoint())) {
            throw new IllegalArgumentException("终点不能为空");
        }
        if (bo.getRemark() != null && bo.getRemark().length() > 200) {
            throw new IllegalArgumentException("备注最长 200 字符");
        }
    }

    private void ensureCodeUnique(String code, Long excludeId) {
        TaskTemplateEntity existing = taskTemplateRepository.getByCode(code);
        if (existing != null && (excludeId == null || !excludeId.equals(existing.getId()))) {
            throw new IllegalArgumentException("模板号已存在: " + code.trim());
        }
    }

    private TaskTemplateBO toBO(TaskTemplateEntity entity) {
        if (entity == null) {
            return null;
        }
        TaskTemplateBO bo = new TaskTemplateBO();
        bo.setId(entity.getId());
        bo.setCode(entity.getCode());
        bo.setName(entity.getName());
        bo.setNavigationMapId(entity.getNavigationMapId());
        bo.setSourcePoint(entity.getSourcePoint());
        bo.setDestPoint(entity.getDestPoint());
        bo.setPriority(entity.getPriority());
        bo.setEnabled(entity.getEnabled());
        bo.setRemark(entity.getRemark());
        bo.setCreateTime(entity.getCreateTime());
        bo.setUpdateTime(entity.getUpdateTime());
        return bo;
    }

    private TaskTemplateEntity toEntity(TaskTemplateBO bo) {
        if (bo == null) {
            return null;
        }
        TaskTemplateEntity entity = new TaskTemplateEntity();
        entity.setId(bo.getId());
        entity.setCode(trimToNull(bo.getCode()));
        entity.setName(trimToNull(bo.getName()));
        entity.setNavigationMapId(bo.getNavigationMapId());
        entity.setSourcePoint(trimToNull(bo.getSourcePoint()));
        entity.setDestPoint(trimToNull(bo.getDestPoint()));
        entity.setPriority(bo.getPriority());
        entity.setEnabled(bo.getEnabled());
        entity.setRemark(trimToNull(bo.getRemark()));
        entity.setKeyword(trimToNull(bo.getKeyword()));
        return entity;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
