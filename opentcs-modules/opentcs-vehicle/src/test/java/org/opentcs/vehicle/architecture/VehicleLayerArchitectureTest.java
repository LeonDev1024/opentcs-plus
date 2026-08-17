package org.opentcs.vehicle.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Vehicle 模块分层约束（对齐 RuoYi modules：Controller 不碰 Entity；ApplicationService 可走 Repository）。
 */
@Tag("dev")
@Tag("prod")
class VehicleLayerArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.opentcs.vehicle");
    }

    @Test
    void controller_must_not_depend_on_persistence_entity_or_mapper() {
        noClasses()
                .that().resideInAPackage("..vehicle.controller..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.opentcs.vehicle.persistence.entity..",
                        "org.opentcs.vehicle.persistence.mapper..")
                .because("Controller 只能调用 ApplicationService，不得直接访问持久化层 Entity/Mapper")
                .check(classes);
    }

    @Test
    void controller_must_not_call_repository_directly() {
        noClasses()
                .that().resideInAPackage("..vehicle.controller..")
                .should().dependOnClassesThat().resideInAPackage(
                        "org.opentcs.vehicle.persistence.service..")
                .because("Controller 须通过 ApplicationService 调用业务逻辑，不得直接引用 Repository")
                .check(classes);
    }

    @Test
    void vehicle_must_not_depend_on_driver_adapters() {
        noClasses()
                .that().resideInAPackage("org.opentcs.vehicle..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.opentcs.driver.vda5050..",
                        "org.opentcs.driver.adapter..")
                .because("业务 modules 禁止依赖 driver-adapter-*，只通过 DriverRegistry / driver-api 协作")
                .check(classes);
    }

    @Test
    void vehicle_must_not_depend_on_monitor() {
        noClasses()
                .that().resideInAPackage("org.opentcs.vehicle..")
                .should().dependOnClassesThat().resideInAPackage("org.opentcs.monitor..")
                .because("监控聚合单向依赖 vehicle，禁止反向依赖")
                .check(classes);
    }

    @Test
    void vehicle_must_not_depend_on_kernel_core() {
        noClasses()
                .that().resideInAPackage("org.opentcs.vehicle..")
                .should().dependOnClassesThat().resideInAPackage("org.opentcs.kernel.application..")
                .because("modules 应通过 kernel-domain.port / kernel-api 协作，不直接依赖 kernel.application 实现")
                .check(classes);
    }
}
