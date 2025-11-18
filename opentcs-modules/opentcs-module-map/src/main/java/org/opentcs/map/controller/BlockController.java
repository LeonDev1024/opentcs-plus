package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.map.domain.entity.Block;
import org.opentcs.map.service.BlockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 区块 Controller
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
    public List<Block> getAllBlocks() {
        return blockService.list();
    }

    /**
     * 根据ID查询区块
     */
    @GetMapping("/{id}")
    public Block getBlockById(@PathVariable Long id) {
        return blockService.getById(id);
    }

    /**
     * 创建区块
     */
    @PostMapping("/")
    public boolean createBlock(@RequestBody Block block) {
        return blockService.save(block);
    }

    /**
     * 更新区块
     */
    @PutMapping("/")
    public boolean updateBlock(@RequestBody Block block) {
        return blockService.updateById(block);
    }

    /**
     * 删除区块
     */
    @DeleteMapping("/{id}")
    public boolean deleteBlock(@PathVariable Long id) {
        return blockService.removeById(id);
    }
}