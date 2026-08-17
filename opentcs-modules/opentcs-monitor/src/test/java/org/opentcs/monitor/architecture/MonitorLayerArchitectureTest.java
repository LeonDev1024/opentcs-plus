package org.opentcs.monitor.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * 监控聚合模块分层约束。
 */
@Tag("dev")
@Tag("prod")
class MonitorLayerArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.opentcs.monitor");
    }

    @Test
    void controller_must_not_depend_on_persistence() {
        noClasses()
                .that().resideInAPackage("..monitor.controller..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.opentcs.vehicle.persistence..",
                        "org.opentcs.order.persistence..",
                        "org.opentcs.map.persistence..")
                .because("监控 Controller 只调用本模块 ApplicationService")
                .check(classes);
    }

    @Test
    void monitor_must_not_depend_on_kernel_core() {
        noClasses()
                .that().resideInAPackage("org.opentcs.monitor..")
                .should().dependOnClassesThat().resideInAPackage("org.opentcs.kernel.application..")
                .because("monitor 通过 kernel-domain.port 聚合，不依赖 kernel-core 实现")
                .check(classes);
    }

    @Test
    void monitor_must_not_depend_on_driver_adapters() {
        noClasses()
                .that().resideInAPackage("org.opentcs.monitor..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.opentcs.driver.vda5050..",
                        "org.opentcs.driver.adapter..")
                .because("监控模块禁止依赖 driver-adapter-*")
                .check(classes);
    }

    @Test
    void monitor_must_not_depend_on_other_module_persistence() {
        noClasses()
                .that().resideInAPackage("org.opentcs.monitor..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.opentcs.vehicle.persistence..",
                        "org.opentcs.order.persistence..",
                        "org.opentcs.map.persistence..")
                .because("锁审计等查询走 vehicle 公开 ApplicationService")
                .check(classes);
    }
}
