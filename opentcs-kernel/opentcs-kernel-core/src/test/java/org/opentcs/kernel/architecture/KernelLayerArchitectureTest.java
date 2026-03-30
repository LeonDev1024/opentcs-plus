package org.opentcs.kernel.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * 内核应用层与领域层依赖约束（对应单轨架构清单「优化项 3 / 11」）。
 */
@AnalyzeClasses(
    packages = "org.opentcs.kernel",
    importOptions = ImportOption.DoNotIncludeTests.class
)
class KernelLayerArchitectureTest {

    @ArchTest
    static final ArchRule core_should_not_depend_on_mybatis =
        noClasses()
            .that().resideInAPackage("..kernel.core..")
            .should().dependOnClassesThat().resideInAnyPackage("org.mybatis..", "com.baomidou..");

    @ArchTest
    static final ArchRule core_should_not_depend_on_redisson =
        noClasses()
            .that().resideInAPackage("..kernel.core..")
            .should().dependOnClassesThat().resideInAnyPackage("org.redisson..");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_mybatis =
        noClasses()
            .that().resideInAPackage("..kernel.domain..")
            .should().dependOnClassesThat().resideInAnyPackage("org.mybatis..", "com.baomidou..");

    /**
     * 领域模型模块（kernel-domain）不依赖 Spring 具体实现类（允许 API 层 provided 的 spring 不在此模块）。
     */
    @ArchTest
    static final ArchRule domain_free_of_spring_context_impl =
        noClasses()
            .that().resideInAPackage("..kernel.domain..")
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework..");
}
