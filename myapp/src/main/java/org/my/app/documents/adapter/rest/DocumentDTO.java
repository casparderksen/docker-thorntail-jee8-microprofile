package org.my.app.documents.adapter.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.my.app.documents.domain.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "document")
public class DocumentDTO implements Serializable {

    private UUID id;
    private String name;

    Document toDocument() {
        return DocumentMapper.INSTANCE.toDocument(this);
    }

    Document toNewDocument() {
        setId(null);
        return DocumentMapper.INSTANCE.toDocument(this);
    }

    static DocumentDTO fromDocument(Document document) {
        return DocumentMapper.INSTANCE.fromDocument(document);
    }

    @Mapper(imports = UUID.class)
    interface DocumentMapper {
        DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

        @Mapping(target = "id", source = "id", defaultExpression = "java( UUID.randomUUID() )")
        Document toDocument(DocumentDTO documentDTO);
        DocumentDTO fromDocument(Document document);
    }
}
