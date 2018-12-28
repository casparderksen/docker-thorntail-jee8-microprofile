package nl.casparderksen.persistence;

import nl.casparderksen.model.Event;
import nl.casparderksen.service.EventRepository;
import org.assertj.db.type.Table;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import static org.assertj.db.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class DatasourceIT extends AbstractPersistenceIT {

    @Inject
    EventRepository repository;

    @Inject
    UserTransaction utx;

    @PersistenceContext(unitName = "MyPU")
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        utx.begin();
        entityManager.joinTransaction();
        repository.create(new Event("foo"));
        utx.commit();
        entityManager.clear();
    }

    @Test
    public void dataShouldBePersisted() throws NamingException {
        Table table = new Table(getDatasource(), "EVENT");
        assertThat(table).row(0)
                .value("id").isEqualTo(1)
                .value("name").isEqualTo("foo");
    }
}