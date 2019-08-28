package nl.casparderksen.myservice;

import nl.casparderksen.model.Document;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository {

    Document create(Document document);

    Optional<Document> get(long id);

    Optional<Document> update(long id, Document document);

    boolean delete(long id);

    long getCount();

    List<Document> getAll();

    List<Document> getRange(int start, int size);
}
