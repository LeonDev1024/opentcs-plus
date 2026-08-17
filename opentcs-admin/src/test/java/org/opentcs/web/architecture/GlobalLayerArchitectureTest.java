package org.opentcs.web.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * 全局接口层分层约束测试。
 */
@Tag("dev")
@Tag("prod")
class GlobalLayerArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.opentcs.web");
    }

    @Test
    void admin_must_not_depend_on_persistence_entity_or_mapper() {
        noClasses()
                .that().resideInAPackage("org.opentcs.web..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.opentcs.vehicle.persistence.entity..",
                        "org.opentcs.vehicle.persistence.mapper..",
                        "org.opentcs.order.persistence.entity..",
                        "org.opentcs.order.persistence.mapper..",
                        "org.opentcs.map.persistence.entity..",
                        "org.opentcs.map.persistence.mapper..")
                .because("接口层（web）须通过 ApplicationService 访问数据，禁止直接引用持久化 Entity/Mapper")
                .check(classes);
    }

    @Test
    void admin_must_not_depend_on_mybatis() {
        noClasses()
                .that().resideInAPackage("org.opentcs.web..")
                .should().dependOnClassesThat().resideInAnyPackage("org.mybatis..", "com.baomidou..")
                .because("接口层不应操作数据库，数据访问须下沉到 ApplicationService/Repository 层")
                .check(classes);
    }

    @Test
    void admin_must_not_depend_on_algorithm_implementations() {
        noClasses()
                .that().resideInAPackage("org.opentcs.web..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.opentcs.strategies.builtin..",
                        "org.opentcs.algorithm.grpc..")
                .because("接口层只能依赖算法 SPI 接口（RoutingAlgorithm），不得直接引用算法实现类")
                .check(classes);
    }
}
