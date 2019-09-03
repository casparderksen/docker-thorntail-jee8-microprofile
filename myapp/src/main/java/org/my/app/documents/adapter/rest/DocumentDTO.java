package org.my.app.documents.adapter.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
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
@Schema(name="Document", description="Datatype that represents documents")
public class DocumentDTO implements Serializable {

    @Schema(description = "UUID of the document")
    private UUID id;

    @Schema(description = "Description of the document", required = true)
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
