package nl.casparderksen.arquillian;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

public class AbstractArquillianIT {

    @Deployment
    public static WebArchive createDeploymentPackage() {
        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .resolve("org.assertj:assertj-core", "org.assertj:assertj-db")
                .withTransitivity()
                .as(File.class);
        return ShrinkWrap.create(WebArchive.class, "ArquillianIT.war")
                .addAsLibraries(libs)
                .addAsResource("META-INF/microprofile-config.properties")
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("project-testing.yml", "project-defaults.yml")
                .addAsResource(new File("src/main/resources/db"), "db")
                .addPackages(true, "nl.casparderksen");
    }
}
