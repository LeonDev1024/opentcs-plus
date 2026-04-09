package org.opentcs.kernel.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Kernel 模块分层约束测试。
 * <p>
 * 守护原则：
 * <ul>
 *   <li>kernel-domain：纯领域模型，无 Spring/MyBatis/Redisson 依赖</li>
 *   <li>kernel-core：应用服务层，无 MyBatis/Redisson 持久化依赖</li>
 *   <li>kernel-domain：不依赖算法 SPI 实现（RoutingAlgorithm 接口可以在 domain，但实现不能被 domain 引用）</li>
 * </ul>
 * </p>
 */
@AnalyzeClasses(
    packages = "org.opentcs.kernel",
    importOptions = ImportOption.DoNotIncludeTests.class
)
class KernelLayerArchitectureTest {

    // ===== kernel-core 约束 =====

    @ArchTest
    static final ArchRule core_should_not_depend_on_mybatis =
        noClasses()
            .that().resideInAPackage("..kernel.core..")
            .should().dependOnClassesThat().resideInAnyPackage("org.mybatis..", "com.baomidou..")
            .because("kernel-core 是应用服务层，持久化实现须在 infrastructure 层通过端口接口注入");

    @ArchTest
    static final ArchRule core_should_not_depend_on_redisson =
        noClasses()
            .that().resideInAPackage("..kernel.core..")
            .should().dependOnClassesThat().resideInAnyPackage("org.redisson..")
            .because("kernel-core 不应直接操作 Redis，缓存抽象应通过端口接口隔离");

    // ===== kernel-domain 约束 =====

    @ArchTest
    static final ArchRule domain_should_not_depend_on_mybatis =
        noClasses()
            .that().resideInAPackage("..kernel.domain..")
            .should().dependOnClassesThat().resideInAnyPackage("org.mybatis..", "com.baomidou..")
            .because("kernel-domain 是纯领域模型层，不得包含持久化框架依赖");

    /**
     * 领域层不得依赖 Spring 框架（保持 POJO 纯净，可独立于 Spring 测试）。
     */
    @ArchTest
    static final ArchRule domain_free_of_spring_context_impl =
        noClasses()
            .that().resideInAPackage("..kernel.domain..")
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework..")
            .because("kernel-domain 应为纯 POJO，不得依赖 Spring，保证领域模型可独立测试");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_redisson =
        noClasses()
            .that().resideInAPackage("..kernel.domain..")
            .should().dependOnClassesThat().resideInAnyPackage("org.redisson..")
            .because("kernel-domain 不得包含基础设施依赖");

    /**
     * 领域层不得引用算法具体实现（strategies-default / algorithm-bridge-grpc）。
     * kernel-domain 只持有 RoutingAlgorithm 接口，不持有实现。
     */
    @ArchTest
    static final ArchRule domain_should_not_depend_on_algorithm_implementations =
        noClasses()
            .that().resideInAPackage("..kernel.domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.opentcs.strategies..",
                    "org.opentcs.algorithm.grpc.."
            )
            .because("kernel-domain 只定义 RoutingAlgorithm 接口，具体算法实现须在 strategies / algorithm 层");
}
