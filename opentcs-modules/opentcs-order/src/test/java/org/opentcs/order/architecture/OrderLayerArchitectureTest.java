package org.opentcs.order.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Order 模块分层约束（对齐 RuoYi modules：Controller 不碰 Entity）。
 */
@Tag("dev")
@Tag("prod")
class OrderLayerArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.opentcs.order");
    }

    @Test
    void controller_must_not_depend_on_persistence_entity_or_mapper() {
        noClasses()
                .that().resideInAPackage("..order.controller..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.opentcs.order.persistence.entity..",
                        "org.opentcs.order.persistence.mapper..")
                .because("Controller 只能调用 ApplicationService，不得直接访问持久化层 Entity/Mapper")
                .check(classes);
    }

    @Test
    void order_must_not_depend_on_kernel_core() {
        noClasses()
                .that().resideInAPackage("org.opentcs.order..")
                .should().dependOnClassesThat().resideInAPackage("org.opentcs.kernel.application..")
                .because("modules 应通过 kernel-domain.port / kernel-api 协作")
                .check(classes);
    }
}
