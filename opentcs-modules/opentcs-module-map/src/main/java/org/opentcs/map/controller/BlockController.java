package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.map.domain.entity.Block;
import org.opentcs.map.service.BlockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 区块管理
 * @author lyc
 */
@RestController
@RequestMapping("/block")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    /**
     * 查询所有区块
     */
    @GetMapping("/")
    public R<List<Block>> getAllBlocks() {
        return R.ok(blockService.list());
    }

    /**
     * 根据ID查询区块
     */
    @GetMapping("/{id}")
    public R<Block> getBlockById(@PathVariable Long id) {
        return R.ok(blockService.getById(id));
    }

    /**
     * 创建区块
     */
    @PostMapping("/")
    public R<Boolean> createBlock(@RequestBody Block block) {
        return R.ok(blockService.save(block));
    }

    /**
     * 更新区块
     */
    @PutMapping("/")
    public R<Boolean> updateBlock(@RequestBody Block block) {
        return R.ok(blockService.updateById(block));
    }

    /**
     * 删除区块
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deleteBlock(@PathVariable Long id) {
        return R.ok(blockService.removeById(id));
    }
}