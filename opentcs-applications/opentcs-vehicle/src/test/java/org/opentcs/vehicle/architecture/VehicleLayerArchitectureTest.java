package org.opentcs.vehicle.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Vehicle 模块分层约束测试。
 * <p>
 * 守护规则：
 * <ul>
 *   <li>Controller 和 ApplicationService 不得直接引用 persistence 层的 Entity/Mapper</li>
 *   <li>Controller 不得绕过 ApplicationService 直接调用 Repository</li>
 *   <li>应用层不得引用 Spring Data Repository（须通过 kernel-api 端口接口）</li>
 * </ul>
 * </p>
 */
@AnalyzeClasses(
        packages = "org.opentcs.vehicle",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class VehicleLayerArchitectureTest {

    /**
     * Controller 不得直接引用 persistence 层 Entity 或 Mapper。
     */
    @ArchTest
    static final ArchRule controller_must_not_depend_on_persistence_entity_or_mapper =
            noClasses()
                    .that().resideInAPackage("..vehicle.controller..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.opentcs.vehicle.persistence.entity..",
                            "org.opentcs.vehicle.persistence.mapper.."
                    )
                    .because("Controller 只能调用 ApplicationService，不得直接访问持久化层 Entity/Mapper");

    /**
     * ApplicationService 不得直接引用 persistence 层 Entity 或 Mapper（须通过 kernel-api 端口接口）。
     */
    @ArchTest
    static final ArchRule application_service_must_not_depend_on_persistence_entity_or_mapper =
            noClasses()
                    .that().resideInAPackage("..vehicle.application..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.opentcs.vehicle.persistence.entity..",
                            "org.opentcs.vehicle.persistence.mapper.."
                    )
                    .because("ApplicationService 应通过 kernel-api 端口接口访问数据，不得直接依赖持久化 Entity/Mapper");

    /**
     * Controller 不得直接调用 persistence 层 Repository（须通过 ApplicationService 中转）。
     */
    @ArchTest
    static final ArchRule controller_must_not_call_domain_service_directly =
            noClasses()
                    .that().resideInAPackage("..vehicle.controller..")
                    .should().dependOnClassesThat().resideInAPackage(
                            "org.opentcs.vehicle.persistence.service.."
                    )
                    .because("Controller 须通过 ApplicationService 调用业务逻辑，不得直接引用 Repository");

    /**
     * Vehicle 应用层不得引入 MyBatis / MyBatis Plus 注解（持久化细节应封装在 infrastructure 层）。
     */
    @ArchTest
    static final ArchRule application_layer_must_not_depend_on_mybatis =
            noClasses()
                    .that().resideInAnyPackage("..vehicle.controller..", "..vehicle.application..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.mybatis..",
                            "com.baomidou.."
                    )
                    .because("应用层不应知晓 MyBatis 实现细节，持久化须通过 kernel-api 端口接口隔离");
}
