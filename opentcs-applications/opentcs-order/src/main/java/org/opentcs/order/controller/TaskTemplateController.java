package org.opentcs.order.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.order.application.TaskTemplateApplicationService;
import org.opentcs.order.application.bo.TaskTemplateBO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务模板管理（极简）
 */
@RestController
@RequestMapping("/task/template")
@RequiredArgsConstructor
public class TaskTemplateController extends BaseController {

    private final TaskTemplateApplicationService taskTemplateApplicationService;

    @GetMapping("/list")
    public TableDataInfo<TaskTemplateBO> list(TaskTemplateBO query, PageQuery pageQuery) {
        return taskTemplateApplicationService.list(query, pageQuery);
    }

    /** 启用模板下拉（创建任务用） */
    @GetMapping("/all")
    public R<List<TaskTemplateBO>> listEnabled() {
        return R.ok(taskTemplateApplicationService.listEnabled());
    }

    @GetMapping("/{id}")
    public R<TaskTemplateBO> getById(@PathVariable Long id) {
        return R.ok(taskTemplateApplicationService.getById(id));
    }

    @PostMapping("/add")
    public R<Boolean> create(@RequestBody TaskTemplateBO template) {
        return R.ok(taskTemplateApplicationService.create(template));
    }

    @PutMapping("/edit")
    public R<Boolean> update(@RequestBody TaskTemplateBO template) {
        return R.ok(taskTemplateApplicationService.update(template));
    }

    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(taskTemplateApplicationService.delete(id));
    }

    @PutMapping("/changeStatus")
    public R<Boolean> changeStatus(@RequestBody TaskTemplateBO template) {
        return R.ok(taskTemplateApplicationService.changeStatus(template.getId(), template.getEnabled()));
    }
}
