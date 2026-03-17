package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.api.dto.BlockDTO;
import org.opentcs.kernel.persistence.entity.BlockEntity;
import org.opentcs.kernel.persistence.service.BlockDomainService;
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

    private final BlockDomainService blockDomainService;

    /**
     * 分页查询区域列表
     * 支持按工厂ID和类型筛选
     */
    @GetMapping("/list")
    public TableDataInfo<BlockDTO> list(BlockEntity block, PageQuery pageQuery) {
        return blockDomainService.selectPageDTO(block, pageQuery);
    }

    /**
     * 根据工厂ID查询区域列表
     */
    @GetMapping("/listByFactory/{factoryId}")
    public R<List<BlockDTO>> listByFactory(@PathVariable Long factoryId) {
        return R.ok(blockDomainService.selectByFactoryModelIdDTO(factoryId));
    }

    /**
     * 根据工厂ID和类型查询区域列表
     */
    @GetMapping("/listByFactoryAndType/{factoryId}/{type}")
    public R<List<BlockDTO>> listByFactoryAndType(
            @PathVariable Long factoryId,
            @PathVariable String type) {
        return R.ok(blockDomainService.selectByFactoryModelIdAndTypeDTO(factoryId, type));
    }

    /**
     * 根据ID查询区域详情
     */
    @GetMapping("/{id}")
    public R<BlockDTO> getById(@PathVariable Long id) {
        return R.ok(blockDomainService.selectByIdDTO(id));
    }

    /**
     * 创建区域
     */
    @PostMapping("/create")
    public R<Boolean> create(@RequestBody BlockEntity block) {
        return R.ok(blockDomainService.create(block));
    }

    /**
     * 更新区域
     */
    @PutMapping("/update")
    public R<Boolean> update(@RequestBody BlockEntity block) {
        return R.ok(blockDomainService.update(block));
    }

    /**
     * 删除区域
     */
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(blockDomainService.delete(id));
    }

    /**
     * 获取区域类型枚举
     */
    @GetMapping("/types")
    public R<List<String>> getTypes() {
        return R.ok(List.of(
                "WORK",       // 工作区域
                "FORBIDDEN",  // 禁行区域
                "WAIT",       // 等待区域
                "CHARGE"     // 充电区域
        ));
    }
}
