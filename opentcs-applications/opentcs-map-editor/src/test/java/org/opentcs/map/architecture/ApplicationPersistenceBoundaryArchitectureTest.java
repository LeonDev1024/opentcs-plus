package org.opentcs.map.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * 应用层边界守护：controller/application 不直接依赖 persistence entity/mapper。
 */
@AnalyzeClasses(
    packages = "org.opentcs.map",
    importOptions = ImportOption.DoNotIncludeTests.class
)
class ApplicationPersistenceBoundaryArchitectureTest {

    @ArchTest
    static final ArchRule controller_and_application_should_not_depend_on_persistence_entity_or_mapper =
        noClasses()
            .that().resideInAnyPackage("..map.controller..", "..map.application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "org.opentcs.kernel.persistence.entity..",
                "org.opentcs.kernel.persistence.mapper.."
            );
}
