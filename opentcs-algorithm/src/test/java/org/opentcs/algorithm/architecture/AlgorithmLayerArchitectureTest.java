package org.opentcs.algorithm.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Algorithm 模块架构约束测试。
 * <p>
 * 守护原则：
 * <ul>
 *   <li>算法 SPI/接口层（spi/loader）不依赖具体实现</li>
 *   <li>算法实现层（strategies/builtin）不依赖 Spring/MyBatis</li>
 *   <li>gRPC 桥接层仅在 grpc-bridge profile 下编译</li>
 * </ul>
 * </p>
 */
@AnalyzeClasses(
    packages = "org.opentcs.algorithm",
    importOptions = ImportOption.DoNotIncludeTests.class
)
class AlgorithmLayerArchitectureTest {

    /**
     * SPI/接口层不应依赖具体算法实现（保持插件化特性）。
     */
    @ArchTest
    static final ArchRule spi_should_not_depend_on_implementations =
        noClasses()
            .that().resideInAnyPackage("org.opentcs.algorithm.spi..", "org.opentcs.algorithm.loader..")
            .should().dependOnClassesThat().resideInAnyPackage("org.opentcs.strategies..")
            .because("算法 SPI 层是接口契约，具体实现应通过依赖注入接入");

    /**
     * 算法实现层不应依赖 MyBatis（算法计算是纯内存操作）。
     */
    @ArchTest
    static final ArchRule strategies_should_not_depend_on_mybatis =
        noClasses()
            .that().resideInAPackage("org.opentcs.strategies..")
            .should().dependOnClassesThat().resideInAnyPackage("org.mybatis..", "com.baomidou..")
            .because("算法实现是纯内存计算，不得包含持久化依赖");

    /**
     * 算法实现层不应依赖 Spring 框架（保持可测试性）。
     */
    @ArchTest
    static final ArchRule strategies_should_not_depend_on_spring =
        noClasses()
            .that().resideInAPackage("org.opentcs.strategies..")
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework.boot..", "org.springframework.context..")
            .because("算法实现应为纯 POJO，通过 Spring AutoConfiguration 接入");
}