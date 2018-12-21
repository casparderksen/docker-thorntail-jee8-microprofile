package nl.casparderksen.db;

import org.assertj.db.type.Table;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.File;

import static org.assertj.db.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class DatasourceIT {

    @ArquillianResource()
    private InitialContext context;

    @Deployment
    public static WebArchive createDeploymentPackage() {
        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .resolve("org.assertj:assertj-core", "org.assertj:assertj-db")
                .withTransitivity()
                .as(File.class);
        return ShrinkWrap.create(WebArchive.class, "DatasourceIT.war")
                .addAsLibraries(libs)
                .addAsManifestResource("META-INF/microprofile-config.properties")
                .addAsResource("project-testing.yml", "project-defaults.yml")
                .addAsResource(new File("src/main/resources/db"), "db")
                .addAsResource("db", "db");
    }

    @Test
    public void dataSourceShouldBeBound() throws NamingException {
        DataSource datasource = (DataSource) context.lookup("java:jboss/datasources/MyDS");
        Assert.assertNotNull(datasource);
        org.assertj.core.api.Assertions.assertThat(datasource).isNotNull();
    }

    @Test
    public void dataShouldBeDefined() throws NamingException {
        DataSource datasource = (DataSource) context.lookup("java:jboss/datasources/MyDS");
        Table table = new Table(datasource, "event");
        assertThat(table).row(0)
                .value("id").isEqualTo(1)
                .value("name").isEqualTo("foo");

    }
}
