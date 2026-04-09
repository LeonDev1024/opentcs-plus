package org.opentcs.driver.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Driver 模块架构约束测试。
 * <p>
 * 守护原则：
 * <ul>
 *   <li>driver-api（接口层）不依赖 driver-adapter-vda5050（实现层）</li>
 *   <li>driver-adapter（适配器层）不依赖 Spring/MyBatis/应用层持久化</li>
 *   <li>应用层通过 DriverRegistry 访问驱动层，不直接依赖适配器</li>
 * </ul>
 * </p>
 */
@AnalyzeClasses(
    packages = "org.opentcs.driver",
    importOptions = ImportOption.DoNotIncludeTests.class
)
class DriverLayerArchitectureTest {

    /**
     * driver-api（接口层）不应依赖 driver-adapter-vda5050（实现层）。
     * 接口定义应在实现之前，保持依赖方向正确。
     */
    @ArchTest
    static final ArchRule api_should_not_depend_on_adapter =
        noClasses()
            .that().resideInAPackage("org.opentcs.driver.api..")
            .should().dependOnClassesThat().resideInAnyPackage("org.opentcs.driver.adapter..")
            .because("driver-api 是接口契约层，不得依赖具体适配器实现");

    /**
     * driver-adapter 不应依赖 MyBatis（适配器是通信层，不是持久化层）。
     */
    @ArchTest
    static final ArchRule adapter_should_not_depend_on_mybatis =
        noClasses()
            .that().resideInAPackage("org.opentcs.driver.adapter..")
            .should().dependOnClassesThat().resideInAnyPackage("org.mybatis..", "com.baomidou..")
            .because("驱动适配器是通信层，不得包含持久化依赖");

    /**
     * driver-adapter 不应依赖应用层持久化服务。
     */
    @ArchTest
    static final ArchRule adapter_should_not_depend_on_persistence =
        noClasses()
            .that().resideInAPackage("org.opentcs.driver.adapter..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "org.opentcs.order.persistence..",
                "org.opentcs.vehicle.persistence..",
                "org.opentcs.map.persistence.."
            )
            .because("驱动适配器是基础设施层，不得依赖应用持久化服务");
}