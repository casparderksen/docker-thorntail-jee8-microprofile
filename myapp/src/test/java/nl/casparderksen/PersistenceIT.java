package nl.casparderksen;

import lombok.extern.slf4j.Slf4j;
import nl.casparderksen.model.Document;
import nl.casparderksen.myservice.DocumentRepository;
import org.assertj.db.type.Table;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import static org.assertj.db.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("ArquillianDeploymentAbsent")
@RunWith(Arquillian.class)
@DefaultDeployment
@Slf4j
public class PersistenceIT {

    @ArquillianResource()
    private InitialContext context;

    @PersistenceContext(unitName = "MyPU")
    private EntityManager entityManager;

    @Inject
    private DocumentRepository repository;

    @Inject
    private UserTransaction utx;

    @Before
    public void setUp() throws Exception {
        utx.begin();
        entityManager.joinTransaction();
        repository.create(new Document("foo"));
        utx.commit();
        entityManager.clear();
    }

    @Test
    public void dataShouldBePersisted() throws NamingException {
        DataSource datasource = getDatasource();
        assertNotNull(datasource);
        Table table = new Table(datasource, "DOCUMENT");
        assertThat(table).row(0)
                .value("id").isEqualTo(1)
                .value("name").isEqualTo("foo");
    }

    private DataSource getDatasource() throws NamingException {
        return (DataSource) context.lookup("java:jboss/datasources/MyDS");
    }
}