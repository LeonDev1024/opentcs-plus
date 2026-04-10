package org.opentcs.order.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Order 模块分层约束测试。
 * <p>
 * 守护规则：
 * <ul>
 *   <li>Controller 和 ApplicationService 不得直接引用 order-persistence 层的 Entity/Mapper</li>
 *   <li>应用层不得依赖 MyBatis 实现细节</li>
 * </ul>
 * </p>
 */
@AnalyzeClasses(
        packages = "org.opentcs.order",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class OrderLayerArchitectureTest {

    /**
     * Controller 不得直接引用 persistence 层 Entity 或 Mapper。
     */
    @ArchTest
    static final ArchRule controller_must_not_depend_on_persistence_entity_or_mapper =
            noClasses()
                    .that().resideInAPackage("..order.controller..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.opentcs.order.persistence.entity..",
                            "org.opentcs.order.persistence.mapper.."
                    )
                    .because("Controller 只能调用 ApplicationService，不得直接访问持久化层 Entity/Mapper");

    /**
     * ApplicationService 不得直接引用 persistence 层 Entity 或 Mapper。
     */
    @ArchTest
    static final ArchRule application_service_must_not_depend_on_persistence_entity_or_mapper =
            noClasses()
                    .that().resideInAPackage("..order.application..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.opentcs.order.persistence.entity..",
                            "org.opentcs.order.persistence.mapper.."
                    )
                    .because("ApplicationService 应通过 kernel-api 端口接口访问数据");

    /**
     * Order 应用层不得依赖 MyBatis / MyBatis Plus。
     */
    @ArchTest
    static final ArchRule application_layer_must_not_depend_on_mybatis =
            noClasses()
                    .that().resideInAnyPackage("..order.controller..", "..order.application..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.mybatis..",
                            "com.baomidou.."
                    )
                    .because("应用层不应知晓 MyBatis 实现细节");
}
