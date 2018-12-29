package nl.casparderksen.rest;

import nl.casparderksen.model.Document;
import nl.casparderksen.service.DocumentRepository;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@ApplicationScoped
@Transactional
@Path("/documents")
@Tag(name = "Document service", description = "CRUD service for Document entities.")
public class DocumentEndpoint {

    @Inject
    private DocumentRepository documentRepository;

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDocuments(
            @QueryParam(Param.START) @DefaultValue("0") @Min(0) int start,
            @QueryParam(Param.COUNT) @DefaultValue("20") @Min(1) @Max(100) int count,
            @Context UriInfo uriInfo) {
        final List<Document> list = documentRepository.getRange(start, count);
        final Link[] paginationLinks = getPaginationLinks(start, count, list.size(), uriInfo);
        return Response.ok(getGenericEntity(list)).links(paginationLinks).build();
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDocument(@PathParam("id") long id, @Context UriInfo uriInfo) {
        Optional<Document> optionalEvent = documentRepository.get(id);
        return getOkOrNotFoundResponse(optionalEvent.orElse(null), uriInfo);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createDocument(Document documentDTO, @Context UriInfo uriInfo) {
        final Document document = documentRepository.create(documentDTO);
        final URI location = getLocation(uriInfo, document.getId());
        return Response.created(location).entity(document).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateDocument(@PathParam("id") long id, Document documentDTO, @Context UriInfo uriInfo) {
        Optional<Document> optionalEvent = documentRepository.update(id, documentDTO);
        return getOkOrNotFoundResponse(optionalEvent.orElse(null), uriInfo);
    }

    @DELETE
    @Path("{id}")
    public Response deleteDocument(@PathParam("id") long id, @Context UriInfo uriInfo) {
        if (documentRepository.delete(id)) {
            return Response.noContent().build();
        }
        return Response.status(NOT_FOUND).build();
    }

    @HEAD
    public Response getCount(@Context UriInfo uriInfo) {
        final long count = documentRepository.getCount();
        return Response.ok().header(Header.TOTAL_COUNT, count).build();
    }

    private static class Relation {
        static final String NEXT = "next";
        static final String PREV = "prev";
        static final String SELF = "self";
    }

    private static class Param {
        static final String COUNT = "count";
        static final String START = "start";
    }

    private static class Header {
        static final String TOTAL_COUNT = "X-Total-Count";
    }

    private static Link self(UriInfo uriInfo) {
        final URI uri = uriInfo.getAbsolutePath();
        return Link.fromUri(uri).rel(Relation.SELF).build();
    }

    private Response getOkOrNotFoundResponse(Object entity, @Context UriInfo uriInfo) {
        if (entity != null) {
            final Link self = self(uriInfo);
            return Response.ok(entity).links(self).build();
        }
        return Response.status(NOT_FOUND).build();
    }

    private static GenericEntity<List<Document>> getGenericEntity(final List<Document> list) {
        return new GenericEntity<List<Document>>(list) {
        };
    }

    private static URI getLocation(UriInfo uriInfo, long id) {
        return uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build();
    }

    private static Link[] getPaginationLinks(int start, int count, int nrAvailable, UriInfo uriInfo) {
        final List<Link> links = new ArrayList<>();
        if (count <= nrAvailable) {
            final URI next = uriInfo.getAbsolutePathBuilder()
                    .queryParam(Param.START, start + count)
                    .queryParam(Param.COUNT, count).build();
            links.add(Link.fromUri(next).rel(Relation.NEXT).build());
        }
        if (start > 0) {
            final URI prev = uriInfo.getAbsolutePathBuilder()
                    .queryParam(Param.START, Integer.max(start - count, 0))
                    .queryParam(Param.COUNT, count).build();
            links.add(Link.fromUri(prev).rel(Relation.PREV).build());
        }
        return links.toArray(new Link[0]);
    }
}
