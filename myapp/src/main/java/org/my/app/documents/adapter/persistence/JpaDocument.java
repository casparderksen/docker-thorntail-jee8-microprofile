package org.my.app.documents.adapter.persistence;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.my.app.documents.domain.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "DOCUMENT")
@NamedQuery(name = "JpaDocument.findAll", query = "SELECT e FROM JpaDocument e")
@NamedQuery(name = "JpaDocument.countAll", query = "SELECT count(e) FROM JpaDocument e")
public class JpaDocument implements Serializable {

    @Id
    @NotNull
    private UUID id;

    @NotNull
    private String name;

    Document toDocument() {
        return DocumentMapper.INSTANCE.toDocument(this);
    }

    static JpaDocument fromDocument(Document document) {
        return DocumentMapper.INSTANCE.fromDocument(document);
    }

    void update(Document document) {
        this.name = document.getName();
    }

    @Mapper
    interface DocumentMapper {
        DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);
        Document toDocument(JpaDocument jpaDocument);
        JpaDocument fromDocument(Document document);
    }
}