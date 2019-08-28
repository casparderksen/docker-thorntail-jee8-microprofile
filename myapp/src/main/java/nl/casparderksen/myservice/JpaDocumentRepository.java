package nl.casparderksen.myservice;

import lombok.extern.slf4j.Slf4j;
import nl.casparderksen.model.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
class JpaDocumentRepository implements DocumentRepository {

    @PersistenceContext(unitName = "MyPU")
    private EntityManager entityManager;

    @Override
    public Document create(Document document) {
        JpaDocument jpaDocument = new JpaDocument().merge(document);
        entityManager.persist(jpaDocument);
        log.debug("created {}", jpaDocument);
        return jpaDocument.toDocument();
    }

    @Override
    public Optional<Document> get(long id) {
        JpaDocument jpaDocument = entityManager.find(JpaDocument.class, id);
        log.debug("retrieved {}", jpaDocument);
        return Optional.ofNullable(jpaDocument).map(JpaDocument::toDocument);
    }

    @Override
    public Optional<Document> update(long id, Document document) {
        JpaDocument jpaDocument = entityManager. find(JpaDocument.class, id);
        if (jpaDocument == null) {
            return Optional.empty();
        }
        jpaDocument.merge(document);
        entityManager.merge(jpaDocument);
        log.debug("updated {}", jpaDocument);
        return Optional.of(jpaDocument.toDocument());
    }

    @Override
    public boolean delete(long id) {
        JpaDocument jpaDocument = entityManager.find(JpaDocument.class, id);
        if (jpaDocument == null) {
            return false;
        }
        entityManager.remove(jpaDocument);
        log.debug("removed {}", jpaDocument);
        return true;
    }

    @Override
    public long getCount() {
        TypedQuery<Long> query = entityManager.createNamedQuery("JpaDocument.countAll", Long.class);
        return query.getSingleResult();
    }

    @Override
    public List<Document> getAll() {
        TypedQuery<JpaDocument> query = entityManager.createNamedQuery("JpaDocument.findAll", JpaDocument.class);
        return getDocuments(query);
    }

    @Override
    public List<Document> getRange(int start, int size) {
        TypedQuery<JpaDocument> query = entityManager.createNamedQuery("JpaDocument.findAll", JpaDocument.class);
        query.setFirstResult(start);
        query.setMaxResults(size);
        return getDocuments(query);
    }

    private List<Document> getDocuments(TypedQuery<JpaDocument> query) {
        List<JpaDocument> resultList = query.getResultList();
        return resultList.stream().map(JpaDocument::toDocument).collect(Collectors.toList());
    }
}
