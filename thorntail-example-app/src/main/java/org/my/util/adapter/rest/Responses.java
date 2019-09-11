package org.my.util.adapter.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public interface Responses {

    static <T> Response getOkResponse(T entity, UriInfo uriInfo) {
        final Link self = Links.getSelfLink(uriInfo);
        return Response.ok(entity).links(self).build();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static <T> Response getOkResponse(Optional<T> optional, UriInfo uriInfo) {
        if (optional.isPresent()) {
            return getOkResponse(optional.get(), uriInfo);
        }
        throw new WebApplicationException(NOT_FOUND);
    }

    static <T> Response getOkResponse(List<T> entities, int start, int count, UriInfo uriInfo) {
        final Link[] paginationLinks = Links.getPaginationLinks(start, count, entities.size(), uriInfo);
        return Response.ok(toEntity(entities)).links(paginationLinks).build();
    }

    static <T, I> Response getCreatedResponse(T entity, I id, UriInfo uriInfo) {
        final URI location = Links.getLocation(uriInfo, id);
        return Response.created(location).entity(entity).build();
    }

    static Response getNoContentResponse() {
        return Response.noContent().build();
    }

    static <T> GenericEntity<List<T>> toEntity(final List<T> list) {
        return new GenericEntity<List<T>>(list) {
        };
    }
}
