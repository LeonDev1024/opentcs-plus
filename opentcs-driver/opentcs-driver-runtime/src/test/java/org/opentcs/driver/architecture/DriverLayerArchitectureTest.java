package org.opentcs.driver.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Driver 模块架构约束测试。
 */
@Tag("dev")
@Tag("prod")
class DriverLayerArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.opentcs.driver");
    }

    @Test
    void api_should_not_depend_on_adapter_impl() {
        noClasses()
                .that().resideInAPackage("org.opentcs.driver.api..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.opentcs.driver.vda5050..",
                        "org.opentcs.driver.adapter..")
                .because("driver-api 是接口契约层，不得依赖具体适配器实现")
                .check(classes);
    }

    @Test
    void runtime_should_not_depend_on_adapter_impl() {
        noClasses()
                .that().resideInAnyPackage(
                        "org.opentcs.driver.registry..",
                        "org.opentcs.driver.gateway..",
                        "org.opentcs.driver.core..",
                        "org.opentcs.driver.loopback..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.opentcs.driver.vda5050..",
                        "org.opentcs.driver.adapter..")
                .because("driver-runtime 不得依赖具体协议 adapter 实现")
                .check(classes);
    }

    @Test
    void runtime_should_not_depend_on_mybatis() {
        noClasses()
                .that().resideInAPackage("org.opentcs.driver..")
                .should().dependOnClassesThat().resideInAnyPackage("org.mybatis..", "com.baomidou..")
                .because("驱动层是通信/运行时，不得包含持久化依赖")
                .check(classes);
    }
}
