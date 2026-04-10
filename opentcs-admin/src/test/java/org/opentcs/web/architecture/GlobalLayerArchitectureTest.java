package org.opentcs.web.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * 全局接口层分层约束测试。
 * <p>
 * 守护规则：
 * <ul>
 *   <li>接口层（opentcs-admin web 包）不得直接引用 persistence 层 Entity/Mapper/Service</li>
 *   <li>接口层不得直接引用 MyBatis / MyBatis Plus（必须通过 ApplicationService）</li>
 *   <li>接口层不得直接引用算法 SPI 实现类（只能依赖 kernel-api 端口接口）</li>
 * </ul>
 * </p>
 */
@AnalyzeClasses(
        packages = "org.opentcs.web",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class GlobalLayerArchitectureTest {

    /**
     * 接口层不得直接引用各域的 persistence 层 Entity/Mapper。
     */
    @ArchTest
    static final ArchRule admin_must_not_depend_on_persistence_entity_or_mapper =
            noClasses()
                    .that().resideInAPackage("org.opentcs.web..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.opentcs.vehicle.persistence.entity..",
                            "org.opentcs.vehicle.persistence.mapper..",
                            "org.opentcs.order.persistence.entity..",
                            "org.opentcs.order.persistence.mapper..",
                            "org.opentcs.map.persistence.entity..",
                            "org.opentcs.map.persistence.mapper.."
                    )
                    .because("接口层（web）须通过 ApplicationService 访问数据，禁止直接引用持久化 Entity/Mapper");

    /**
     * 接口层不得使用 MyBatis / MyBatis Plus 注解操作数据库。
     */
    @ArchTest
    static final ArchRule admin_must_not_depend_on_mybatis =
            noClasses()
                    .that().resideInAPackage("org.opentcs.web..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.mybatis..",
                            "com.baomidou.."
                    )
                    .because("接口层不应操作数据库，数据访问须下沉到 ApplicationService/Repository 层");

    /**
     * 接口层不得直接引用算法 SPI 实现（只能依赖 kernel-domain/kernel-api 的算法接口）。
     */
    @ArchTest
    static final ArchRule admin_must_not_depend_on_algorithm_implementations =
            noClasses()
                    .that().resideInAPackage("org.opentcs.web..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.opentcs.strategies.builtin..",
                            "org.opentcs.algorithm.grpc.."
                    )
                    .because("接口层只能依赖算法 SPI 接口（RoutingAlgorithm），不得直接引用算法实现类");
}
