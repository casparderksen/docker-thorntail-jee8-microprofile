package org.myapp.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

/**
 * Test for compliance to Onion Architecture.
 *
 * @see <a href="https://www.archunit.org/userguide/html/000_Index.html#_architectures">ArchUnit Manual</a>
 */
@AnalyzeClasses(packages = "org.myapp", importOptions = ImportOption.DoNotIncludeTests.class)
public class OnionArchitectureTest {

    @ArchTest
    static final ArchRule ONION_ARCHITECTURE_IS_RESPECTED = onionArchitecture()
            .domainModels("..domain.model..")
            .domainServices("..domain.service..")
            .applicationServices("..application..")
            .adapter("cli", "..adapter.cli..")
            .adapter("persistence", "..adapter.persistence..")
            .adapter("rest", "..adapter.rest..");
}
