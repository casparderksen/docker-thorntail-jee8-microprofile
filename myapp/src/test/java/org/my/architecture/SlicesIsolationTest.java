package org.my.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "org.my.app")
class SlicesIsolationTest {

    @ArchTest
    static final ArchRule SLICES_SHOULD_NOT_DEPEND_ON_EACH_OTHER =
            SlicesRuleDefinition.slices().matching("org.my.app.(*)..").should().notDependOnEachOther();

    // TODO test not working
    @ArchTest
    static final ArchRule UTILS_SHOULD_NOT_DEPEND_ON_APP = layeredArchitecture()
            .layer("Utilities").definedBy("org.my.util..")
            .layer("Application").definedBy("org.my.app..")
            .whereLayer("Application").mayNotBeAccessedByAnyLayer();
}
