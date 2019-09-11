package org.my.app.documents.adapter.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.my.app.documents.domain.model.Document;
import org.my.util.domain.service.mappers.UUIDMapper;
import org.my.util.domain.service.validation.ValidUUID;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "document")
@Schema(name = "Document", description = "Datatype that represents documents")
public class DocumentDTO implements Serializable {

    @Schema(description = "UUID of the document")
    @ValidUUID
    private String id;

    @Schema(description = "Description of the document", required = true)
    private String name;

    static DocumentDTO fromDocument(Document document) {
        return DocumentMapper.INSTANCE.fromDocument(document);
    }

    Document toDocument(UUID id) {
        setId(Objects.requireNonNull(id).toString());
        return DocumentMapper.INSTANCE.toDocument(this);
    }

    @Mapper(uses = UUIDMapper.class)
    interface DocumentMapper {
        DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);
        Document toDocument(DocumentDTO documentDTO);
        DocumentDTO fromDocument(Document document);
    }
}
