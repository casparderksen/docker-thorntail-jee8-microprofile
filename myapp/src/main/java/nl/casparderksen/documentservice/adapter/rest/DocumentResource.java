package nl.casparderksen.documentservice.adapter.rest;

import nl.casparderksen.documentservice.application.DocumentRepository;
import nl.casparderksen.documentservice.domain.Document;
import nl.casparderksen.util.rest.Responses;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

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
import static nl.casparderksen.util.rest.Links.COUNT;
import static nl.casparderksen.util.rest.Links.START;

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
    @Operation(description = "Gets a document by id")
    @Parameter(name = "id", description = "id of the document")
    @APIResponse(responseCode = "200", description = "Success, returns the value")
    @APIResponse(responseCode = "404", description = "Not found")
    public Response getDocument(@PathParam("id") UUID id, @Context UriInfo uriInfo) {
        Optional<Document> documentOptional = documentRepository.findById(id);
        return Responses.getOkResponse(documentOptional.map(DocumentDTO::fromDocument), uriInfo);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(description = "Gets the collection of document (paginated)")
    @Parameter(name = "start", description = "start offset in collection")
    @Parameter(name = "count", description = "max chunk size of returned values")
    @APIResponse(responseCode = "200", description = "Success, returns the collection")
    @APIResponse(responseCode = "400", description = "Bad request (invalid range)")
    public Response getDocuments(
            @QueryParam(START) @DefaultValue("0")
            @Min(value = 0, message = "parameter 'start' must be at least {value}") int start,
            @QueryParam(COUNT) @DefaultValue("20")
            @Min(value = 1, message = "parameter 'count' must be at least {value}")
            @Max(value = 100, message = "parameter 'count' must be at most {value}") int count,
            @Context UriInfo uriInfo) {
        final List<Document> documents = documentRepository.findRange(start, count);
        final List<DocumentDTO> documentDTOs = documents.stream().map(DocumentDTO::fromDocument).collect(toList());
        return Responses.getOkResponse(documentDTOs, start, count, uriInfo);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(description = "Creates a new document")
    @APIResponse(responseCode = "201", description = "Success, returns the value")
    public Response createDocument(DocumentDTO documentDTO, @Context UriInfo uriInfo) {
        final Document document = documentRepository.save(documentDTO.toNewDocument());
        return Responses.getCreatedResponse(DocumentDTO.fromDocument(document), document.getId(), uriInfo);
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(description = "Updates the document with the specified id")
    @Parameter(name = "id", description = "id of the document")
    @APIResponse(responseCode = "200", description = "Success, returns the new item")
    @APIResponse(responseCode = "404", description = "Not found")
    public Response updateDocument(@PathParam("id") UUID id, DocumentDTO documentDTO, @Context UriInfo uriInfo) {
        documentDTO.setId(id);
        Document document = documentRepository.update(documentDTO.toDocument());
        return Responses.getOkResponse(DocumentDTO.fromDocument(document), uriInfo);
    }

    @DELETE
    @Path("{id}")
    @Operation(description = "Deletes the document with the specified id")
    @Parameter(name = "id", description = "id of the document")
    @APIResponse(responseCode = "205", description = "Success, no content")
    @APIResponse(responseCode = "404", description = "Not found")
    public Response deleteDocument(@PathParam("id") UUID id, @Context UriInfo uriInfo) {
        documentRepository.deleteById(id);
        return Responses.getNoContentResponse();
    }

    @GET
    @Path("count")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(description = "Counts the number of documents")
    @APIResponse(responseCode = "200", description = "Success, return number of values")
    public Response getCount(@Context UriInfo uriInfo) {
        final long count = documentRepository.count();
        return Response.ok(toCountJson(count)).build();
    }

    private static String toCountJson(long count) {
        return "{ \"count\": " + count + " }";
    }
}
