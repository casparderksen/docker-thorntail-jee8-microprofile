package org.my.app.documents.adapter.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.links.Link;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.my.app.documents.domain.model.Document;
import org.my.app.documents.domain.service.DocumentRepository;
import org.my.util.rest.Links;
import org.my.util.rest.Responses;
import org.my.util.validation.ValidUUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
@Transactional
@Path("/documents")
@Tag(name = "Document resource", description = "CRUD service for Document entities")
public class DocumentResource {

    @Inject
    private DocumentRepository documentRepository;

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(operationId = "getDocument", description = "Gets a document by id")
    @Parameter(name = "id", description = "id of the document", in = ParameterIn.PATH, required = true, schema = @Schema(type = SchemaType.STRING))
    @APIResponse(responseCode = "200",
            description = "Success, returns the value",
            content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.OBJECT, implementation = DocumentDTO.class)))
    @APIResponse(responseCode = "404", description = "Not found")
    public Response getDocument(@PathParam("id") @ValidUUID String id, @Context UriInfo uriInfo) {
        Optional<Document> documentOptional = documentRepository.findById(UUID.fromString(id));
        return Responses.getOkResponse(documentOptional.map(DocumentDTO::fromDocument), uriInfo);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(operationId = "getDocuments", description = "Gets the collection of document (optionally paginated)")
    @Parameter(name = "start", in = ParameterIn.PATH, schema = @Schema(type = SchemaType.INTEGER), description = "Start offset in collection")
    @Parameter(name = "count", in = ParameterIn.PATH, schema = @Schema(type = SchemaType.INTEGER), description = "Max chunk size of returned values")
    @APIResponse(responseCode = "200",
            description = "Success, returns the collection",
            content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = DocumentDTO.class)), links = {
            @Link(name = "prev", description = "previous page", operationId = "getDocuments"),
            @Link(name = "next", description = "next page", operationId = "getDocuments")})
    @APIResponse(responseCode = "400", description = "Bad request (invalid range)")
    public Response getDocuments(
            @QueryParam(Links.START) @DefaultValue("0") @Min(value = 0, message = "parameter 'start' must be at least {value}") int start,
            @QueryParam(Links.COUNT) @DefaultValue("20") @Min(value = 1, message = "parameter 'count' must be at least {value}") @Max(value = 100, message = "parameter 'count' must be at most {value}") int count,
            @Context UriInfo uriInfo) {
        final List<Document> documents = documentRepository.findRange(start, count);
        final List<DocumentDTO> documentDTOs = documents.stream().map(DocumentDTO::fromDocument).collect(toList());
        return Responses.getOkResponse(documentDTOs, start, count, uriInfo);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(operationId = "createDocument", description = "Creates a new document")
    @APIResponse(responseCode = "201",
            description = "Success, returns the value",
            content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.OBJECT, implementation = DocumentDTO.class)))
    public Response createDocument(@RequestBody(description = "new document") DocumentDTO documentDTO,
                                   @Context UriInfo uriInfo) {
        final Document document = documentRepository.save(documentDTO.toDocument(UUID.randomUUID()));
        return Responses.getCreatedResponse(DocumentDTO.fromDocument(document), document.getId(), uriInfo);
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(operationId = "updateDocument", description = "Updates the document with the specified id")
    @Parameter(name = "id", description = "id of the document", in = ParameterIn.PATH, required = true, schema = @Schema(type = SchemaType.STRING))
    @APIResponse(responseCode = "200",
            description = "Success, returns the new item",
            content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.OBJECT, implementation = DocumentDTO.class)))
    @APIResponse(responseCode = "404", description = "Not found")
    public Response updateDocument(@PathParam("id") @ValidUUID String id,
                                   @RequestBody(description = "updated document") DocumentDTO documentDTO,
                                   @Context UriInfo uriInfo) {
        Document document = documentRepository.update(documentDTO.toDocument(UUID.fromString(id)));
        return Responses.getOkResponse(DocumentDTO.fromDocument(document), uriInfo);
    }

    @DELETE
    @Path("{id}")
    @Operation(operationId = "deleteDocument", description = "Deletes the document with the specified id")
    @Parameter(name = "id", description = "id of the document", in = ParameterIn.PATH, required = true, schema = @Schema(type = SchemaType.STRING))
    @APIResponse(responseCode = "204", description = "Success, no content")
    @APIResponse(responseCode = "404", description = "Not found")
    public Response deleteDocument(@PathParam("id") @ValidUUID String id, @Context UriInfo uriInfo) {
        documentRepository.deleteById(UUID.fromString(id));
        return Responses.getNoContentResponse();
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(operationId = "getCount", description = "Counts the number of documents")
    @APIResponse(responseCode = "200",
            description = "Success, return number of values",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = SchemaType.INTEGER)))
    public long getCount(@Context UriInfo uriInfo) {
        return documentRepository.count();
    }
}
