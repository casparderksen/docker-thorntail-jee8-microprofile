package org.myapp.documents.adapter.persistence;

import org.myapp.documents.domain.model.Document;
import org.myapp.documents.application.DocumentRepository;

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
        JpaDocument entity = entityManager.find(JpaDocument.class, uuid);
        return Optional.ofNullable(entity).map(JpaDocument::toDocument);
    }

    @Override
    public List<Document> findAll() {
        TypedQuery<JpaDocument> query = entityManager.createNamedQuery("JpaDocument.findAll", JpaDocument.class);
        return findDocuments(query);
    }


    @Override
    public List<Document> findRange(int start, int size) {
        TypedQuery<JpaDocument> query = entityManager.createNamedQuery("JpaDocument.findAll", JpaDocument.class);
        query.setFirstResult(start);
        query.setMaxResults(size);
        return findDocuments(query);
    }

    @Override
    public long count() {
        TypedQuery<Long> query = entityManager.createNamedQuery("JpaDocument.countAll", Long.class);
        return query.getSingleResult();
    }

    @Override
    public Document save(Document document) {
        final JpaDocument entity = JpaDocument.fromDocument(document);
        entityManager.persist(entity);
        return entity.toDocument();
    }

    @Override
    public Document update(Document document) {
        JpaDocument entity = entityManager.getReference(JpaDocument.class, document.getId());
        entity.update(document);
        entityManager.merge(entity);
        return entity.toDocument();
    }

    @Override
    public void deleteById(UUID id) {
        JpaDocument entity = entityManager.getReference(JpaDocument.class, id);
        entityManager.remove(entity);
    }

    private List<Document> findDocuments(TypedQuery<JpaDocument> query) {
        List<JpaDocument> entities = query.getResultList();
        return entities.stream().map(JpaDocument::toDocument).collect(toList());
    }
}
