package org.opentcs.algorithm.web;

import lombok.RequiredArgsConstructor;
import org.opentcs.algorithm.loader.AlgorithmPluginRegistry;
import org.opentcs.algorithm.spi.AlgorithmDescriptor;
import org.opentcs.common.core.domain.R;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 算法管理接口 — 支持运行时热切换路径规划算法。
 */
@RestController
@RequestMapping("/algorithm")
@RequiredArgsConstructor
@ConditionalOnBean(AlgorithmPluginRegistry.class)
public class AlgorithmManagementController {

    private final AlgorithmPluginRegistry algorithmPluginRegistry;

    @GetMapping("/list")
    public R<List<AlgorithmDescriptor>> listAlgorithms() {
        return R.ok(algorithmPluginRegistry.listAllDescriptors());
    }

    @GetMapping("/active")
    public R<AlgorithmDescriptor> getActive() {
        return R.ok(algorithmPluginRegistry.getActiveDescriptor());
    }

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
