package org.opentcs.algorithm.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Algorithm 模块架构约束测试。
 * <p>
 * 守护原则：
 * <ul>
 *   <li>算法 SPI/接口层（spi/loader）不依赖具体实现</li>
 *   <li>算法实现层（strategies/builtin）不依赖 Spring/MyBatis</li>
 * </ul>
 * </p>
 */
@Tag("dev")
@Tag("prod")
class AlgorithmLayerArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.opentcs.algorithm", "org.opentcs.strategies");
    }

    @Test
    void spi_should_not_depend_on_implementations() {
        noClasses()
                .that().resideInAPackage("org.opentcs.algorithm.spi..")
                .should().dependOnClassesThat().resideInAnyPackage("org.opentcs.strategies..")
                .because("算法 SPI 层是接口契约，具体实现应通过依赖注入接入；loader 可装配 strategies")
                .check(classes);
    }

    @Test
    void strategies_should_not_depend_on_mybatis() {
        noClasses()
                .that().resideInAPackage("org.opentcs.strategies..")
                .should().dependOnClassesThat().resideInAnyPackage("org.mybatis..", "com.baomidou..")
                .because("算法实现是纯内存计算，不得包含持久化依赖")
                .check(classes);
    }

    @Test
    void strategies_should_not_depend_on_spring() {
        noClasses()
                .that().resideInAPackage("org.opentcs.strategies..")
                .and().resideOutsideOfPackage("..strategies..config..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework.boot..", "org.springframework.context..")
                .because("算法实现应为纯 POJO，装配配置放在 strategies.*.config")
                .check(classes);
    }
}
