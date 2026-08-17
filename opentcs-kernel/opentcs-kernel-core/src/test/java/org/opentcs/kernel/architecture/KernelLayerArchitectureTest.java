package org.opentcs.kernel.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Kernel 模块分层约束测试。
 * <p>
 * 守护原则：
 * <ul>
 *   <li>kernel-domain：纯领域模型，无 Spring/MyBatis/Redisson 依赖</li>
 *   <li>kernel-core（application/config）：不直接依赖 MyBatis</li>
 *   <li>kernel-domain：不依赖算法 SPI 实现</li>
 * </ul>
 * </p>
 */
@Tag("dev")
@Tag("prod")
class KernelLayerArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.opentcs.kernel");
    }

    @Test
    void core_should_not_depend_on_mybatis() {
        noClasses()
                .that().resideInAnyPackage("..kernel.application..", "..kernel.config..")
                .should().dependOnClassesThat().resideInAnyPackage("org.mybatis..", "com.baomidou..")
                .because("kernel-core 不直接依赖持久化实现；持久化由业务 modules 通过端口接口注入")
                .check(classes);
    }

    @Test
    void core_should_not_depend_on_redisson() {
        noClasses()
                .that().resideInAnyPackage("..kernel.application..", "..kernel.config..")
                .should().dependOnClassesThat().resideInAnyPackage("org.redisson..")
                .because("kernel-core 不应直接操作 Redis，缓存抽象应通过端口接口隔离")
                .check(classes);
    }

    @Test
    void domain_should_not_depend_on_mybatis() {
        noClasses()
                .that().resideInAPackage("..kernel.domain..")
                .should().dependOnClassesThat().resideInAnyPackage("org.mybatis..", "com.baomidou..")
                .because("kernel-domain 是纯领域模型层，不得包含持久化框架依赖")
                .check(classes);
    }

    @Test
    void domain_free_of_spring_context_impl() {
        noClasses()
                .that().resideInAPackage("..kernel.domain..")
                .should().dependOnClassesThat().resideInAnyPackage("org.springframework..")
                .because("kernel-domain 应为纯 POJO，不得依赖 Spring，保证领域模型可独立测试")
                .check(classes);
    }

    @Test
    void domain_should_not_depend_on_redisson() {
        noClasses()
                .that().resideInAPackage("..kernel.domain..")
                .should().dependOnClassesThat().resideInAnyPackage("org.redisson..")
                .because("kernel-domain 不得包含基础设施依赖")
                .check(classes);
    }

    @Test
    void domain_should_not_depend_on_algorithm_implementations() {
        noClasses()
                .that().resideInAPackage("..kernel.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.opentcs.strategies..",
                        "org.opentcs.algorithm.grpc..")
                .because("kernel-domain 只定义 RoutingAlgorithm 接口，具体算法实现须在 strategies / algorithm 层")
                .check(classes);
    }
}
