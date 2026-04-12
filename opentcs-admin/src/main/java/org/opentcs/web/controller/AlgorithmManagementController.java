package org.opentcs.web.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.algorithm.loader.AlgorithmPluginRegistry;
import org.opentcs.algorithm.spi.AlgorithmDescriptor;
import org.opentcs.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 算法管理接口 — 支持运行时热切换路径规划算法。
 */
@RestController
@RequestMapping("/algorithm")
@RequiredArgsConstructor
public class AlgorithmManagementController {

    private final AlgorithmPluginRegistry algorithmPluginRegistry;

    /**
     * 查询所有已注册的算法插件。
     */
    @GetMapping("/list")
    public R<List<AlgorithmDescriptor>> listAlgorithms() {
        return R.ok(algorithmPluginRegistry.listAllDescriptors());
    }

    /**
     * 查询当前激活的算法插件。
     */
    @GetMapping("/active")
    public R<AlgorithmDescriptor> getActive() {
        return R.ok(algorithmPluginRegistry.getActiveDescriptor());
    }

    /**
     * 运行时切换算法（热切换，无需重启服务）。
     *
     * @param body 请求体，包含 {@code provider} 字段（算法插件名）
     */
    @PostMapping("/switch")
    public R<String> switchAlgorithm(@RequestBody Map<String, String> body) {
        String provider = body.get("provider");
        if (provider == null || provider.isBlank()) {
            return R.fail("provider 不能为空");
        }
        algorithmPluginRegistry.switchProvider(provider);
        return R.ok("算法已切换为: " + algorithmPluginRegistry.getActiveDescriptor().getName());
    }
}
