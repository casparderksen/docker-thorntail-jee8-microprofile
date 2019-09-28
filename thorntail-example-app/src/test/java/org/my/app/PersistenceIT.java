package org.my.app;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.my.app.documents.domain.service.DocumentRepository;
import org.my.app.documents.domain.model.Document;
import org.assertj.db.type.Table;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.transaction.*;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.db.api.Assertions.assertThat;

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

    private final UUID uuid = UUID.randomUUID();

    @Test
    public void runTests() {
        shouldSaveDocument();
        shouldFindDocument();
        shouldUpdateDocument();
        shouldDeleteDocument();
    }

    public void shouldSaveDocument() {
        transactional(() -> repository.save(Document.builder().id(uuid).name("foo").build()));
        assertThat(getDocumentTable()).row(0)
                .value("id").isEqualTo(strip(uuid))
                .value("name").isEqualTo("foo");
    }

    public void shouldFindDocument() {
        var documentOptional = transactional(() -> repository.findById(uuid));
        org.assertj.core.api.Assertions.assertThat(documentOptional).get().extracting("id").isEqualTo(uuid);
    }

    public void shouldUpdateDocument() {
        transactional(() -> repository.update(Document.builder().id(uuid).name("bar").build()));
        assertThat(getDocumentTable()).row(0)
                .value("id").isEqualTo(strip(uuid))
                .value("name").isEqualTo("bar");
    }

    public void shouldDeleteDocument() {
        transactional(() -> repository.deleteById(uuid));
        assertThat(getDocumentTable()).hasNumberOfRows(0);
    }

    @SneakyThrows
    private void transactional(Runnable function) {
        startTransaction();
        function.run();
        endTransaction();
    }

    @SneakyThrows
    private <T> T transactional(Supplier<T> function) {
        startTransaction();
        var result = function.get();
        endTransaction();
        return result;
    }

    private void startTransaction() throws NotSupportedException, SystemException {
        utx.begin();
        entityManager.joinTransaction();
    }

    private void endTransaction() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException {
        utx.commit();
        entityManager.clear();
    }

    @SneakyThrows
    private Table getDocumentTable() {
        var datasource = getDatasource();
        return new Table(datasource, "DOCUMENT");
    }

    private DataSource getDatasource() throws NamingException {
        return (DataSource) context.lookup("java:jboss/datasources/MyDS");
    }

    private String strip(UUID id) {
        return id.toString().replace("-", "");
    }
}