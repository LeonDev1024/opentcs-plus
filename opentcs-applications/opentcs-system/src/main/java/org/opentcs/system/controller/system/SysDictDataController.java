package org.opentcs.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.excel.utils.ExcelUtil;
import org.opentcs.common.idempotent.annotation.RepeatSubmit;
import org.opentcs.common.log.annotation.Log;
import org.opentcs.common.log.enums.BusinessType;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.system.domain.bo.SysDictDataBo;
import org.opentcs.system.domain.vo.SysDictDataVo;
import org.opentcs.system.service.ISysDictDataService;
import org.opentcs.system.service.ISysDictTypeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据字典信息
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController {

    private final ISysDictDataService dictDataService;
    private final ISysDictTypeService dictTypeService;

    /**
     * 查询字典数据列表
     */
    @SaCheckPermission("system:dict:list")
    @GetMapping("/list")
    public TableDataInfo<SysDictDataVo> list(SysDictDataBo dictData, PageQuery pageQuery) {
        return dictDataService.selectPageDictDataList(dictData, pageQuery);
    }

    /**
     * 导出字典数据列表
     */
    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:dict:export")
    @PostMapping("/export")
    public void export(SysDictDataBo dictData, HttpServletResponse response) {
        List<SysDictDataVo> list = dictDataService.selectDictDataList(dictData);
        ExcelUtil.exportExcel(list, "字典数据", SysDictDataVo.class, response);
    }

    /**
     * 查询字典数据详细
     *
     * @param dictCode 字典code
     */
    @SaCheckPermission("system:dict:query")
    @GetMapping(value = "/{dictCode}")
    public R<SysDictDataVo> getInfo(@PathVariable Long dictCode) {
        return R.ok(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     *
     * @param dictType 字典类型
     */
    @GetMapping(value = "/type/{dictType}")
    public R<List<SysDictDataVo>> dictType(@PathVariable String dictType) {
        List<SysDictDataVo> data = dictTypeService.selectDictDataByType(dictType);
        if (ObjectUtil.isNull(data)) {
            data = new ArrayList<>();
        }
        return R.ok(data);
    }

    /**
     * 新增字典类型
     */
    @SaCheckPermission("system:dict:add")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysDictDataBo dict) {
        if (!dictDataService.checkDictDataUnique(dict)) {
            return R.fail("新增字典数据'" + dict.getDictValue() + "'失败，字典键值已存在");
        }
        dictDataService.insertDictData(dict);
        return R.ok();
    }

    /**
     * 修改保存字典类型
     */
    @SaCheckPermission("system:dict:edit")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysDictDataBo dict) {
        if (!dictDataService.checkDictDataUnique(dict)) {
            return R.fail("修改字典数据'" + dict.getDictValue() + "'失败，字典键值已存在");
        }
        dictDataService.updateDictData(dict);
        return R.ok();
    }

    /**
     * 删除字典类型
     *
     * @param dictCodes 字典code串
     */
    @SaCheckPermission("system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public R<Void> remove(@PathVariable Long[] dictCodes) {
        dictDataService.deleteDictDataByIds(Arrays.asList(dictCodes));
        return R.ok();
    }
}
