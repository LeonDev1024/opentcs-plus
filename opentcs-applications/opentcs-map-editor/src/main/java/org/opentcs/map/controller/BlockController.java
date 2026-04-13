package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.api.dto.BlockDTO;
import org.opentcs.map.application.MapFacadeApplicationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 区域管理
 * 用于工作区域、禁行区域、等待区域、充电区域等交通规则配置
 */
@Validated
@RestController
@RequestMapping("/block")
@RequiredArgsConstructor
public class BlockController extends BaseController {

    private final MapFacadeApplicationService mapFacadeApplicationService;

    /**
     * 分页查询区域列表
     * 支持按工厂ID和类型筛选
     */
    @GetMapping("/list")
    public TableDataInfo<BlockDTO> list(BlockDTO block, PageQuery pageQuery) {
        return mapFacadeApplicationService.listBlocks(block, pageQuery);
    }

    /**
     * 根据工厂ID查询区域列表
     */
    @GetMapping("/listByFactory/{factoryId}")
    public R<List<BlockDTO>> listByFactory(@PathVariable Long factoryId) {
        return R.ok(mapFacadeApplicationService.listBlocksByFactory(factoryId));
    }

    /**
     * 根据工厂ID和类型查询区域列表
     */
    @GetMapping("/listByFactoryAndType/{factoryId}/{type}")
    public R<List<BlockDTO>> listByFactoryAndType(
            @PathVariable Long factoryId,
            @PathVariable String type) {
        return R.ok(mapFacadeApplicationService.listBlocksByFactoryAndType(factoryId, type));
    }

    /**
     * 根据ID查询区域详情
     */
    @GetMapping("/{id}")
    public R<BlockDTO> getById(@PathVariable Long id) {
        return R.ok(mapFacadeApplicationService.getBlockById(id));
    }

    /**
     * 创建区域
     */
    @PostMapping("/create")
    public R<Boolean> create(@RequestBody BlockDTO block) {
        return R.ok(mapFacadeApplicationService.createBlock(block));
    }

    /**
     * 更新区域
     */
    @PutMapping("/update")
    public R<Boolean> update(@RequestBody BlockDTO block) {
        return R.ok(mapFacadeApplicationService.updateBlock(block));
    }

    /**
     * 删除区域
     */
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(mapFacadeApplicationService.deleteBlock(id));
    }

    /**
     * 获取区域类型枚举（对齐 openTCS Block.Type 语义）
     */
    @GetMapping("/types")
    public R<List<String>> getTypes() {
        return R.ok(List.of(
                "SINGLE_VEHICLE_ONLY",  // 单车互斥：同一时刻只允许一辆车占用 Block 内资源
                "SAME_DIRECTION_ONLY"   // 同向通行：多车可同时占用，但进入方向必须相同
        ));
    }

}
