package org.my.app.documents.adapter.persistence;

import org.my.app.documents.domain.model.Document;
import org.my.app.documents.domain.service.DocumentRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class JpaDocumentRepository implements DocumentRepository {

    @PersistenceContext(unitName = "MyPU")
    private EntityManager entityManager;

    @Override
    public Optional<Document> findById(UUID uuid) {
        var entity = entityManager.find(JpaDocument.class, uuid);
        return Optional.ofNullable(entity).map(JpaDocument::toDocument);
    }

    @Override
    public List<Document> findAll() {
        var query = entityManager.createNamedQuery("JpaDocument.findAll", JpaDocument.class);
        return findDocuments(query);
    }


    @Override
    public List<Document> findRange(int start, int size) {
        var query = entityManager.createNamedQuery("JpaDocument.findAll", JpaDocument.class);
        query.setFirstResult(start);
        query.setMaxResults(size);
        return findDocuments(query);
    }

    @Override
    public long count() {
        var query = entityManager.createNamedQuery("JpaDocument.countAll", Long.class);
        return query.getSingleResult();
    }

    @Override
    public Document save(Document document) {
        final var entity = JpaDocument.fromDocument(document);
        entityManager.persist(entity);
        return entity.toDocument();
    }

    @Override
    public Document update(Document document) {
        var entity = entityManager.getReference(JpaDocument.class, document.getId());
        entity.update(document);
        entityManager.merge(entity);
        return entity.toDocument();
    }

    @Override
    public void deleteById(UUID id) {
        var entity = entityManager.getReference(JpaDocument.class, id);
        entityManager.remove(entity);
    }

    private List<Document> findDocuments(TypedQuery<JpaDocument> query) {
        var entities = query.getResultList();
        return entities.stream().map(JpaDocument::toDocument).collect(toList());
    }
}
