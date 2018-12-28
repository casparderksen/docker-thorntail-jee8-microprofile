package nl.casparderksen.persistence;

import nl.casparderksen.arquillian.AbstractArquillianIT;
import org.jboss.arquillian.test.api.ArquillianResource;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

class AbstractPersistenceIT extends AbstractArquillianIT {

    @ArquillianResource()
    private InitialContext context;

    DataSource getDatasource() throws NamingException {
        return (DataSource) context.lookup("java:jboss/datasources/MyDS");
    }
}
