package org.opentcs.map.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * 应用层边界守护：Controller 不直接依赖 persistence entity/mapper。
 */
@Tag("dev")
@Tag("prod")
class ApplicationPersistenceBoundaryArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.opentcs.map");
    }

    @Test
    void controller_should_not_depend_on_persistence_entity_or_mapper() {
        noClasses()
                .that().resideInAPackage("..map.controller..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.opentcs.map.persistence.entity..",
                        "org.opentcs.map.persistence.mapper..")
                .because("Controller 只能调用 ApplicationService，不得直接访问持久化层 Entity/Mapper")
                .check(classes);
    }

    @Test
    void map_must_not_depend_on_kernel_core() {
        noClasses()
                .that().resideInAPackage("org.opentcs.map..")
                .should().dependOnClassesThat().resideInAPackage("org.opentcs.kernel.application..")
                .because("modules 应通过 kernel-domain.port / kernel-api 协作")
                .check(classes);
    }
}
