package org.my.util.rest;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public interface Links {

    static <T> URI getLocation(UriInfo uriInfo, T id) {
        return uriInfo.getAbsolutePathBuilder().path(id.toString()).build();
    }

    static Link getSelfLink(UriInfo uriInfo) {
        final URI uri = uriInfo.getAbsolutePath();
        return Link.fromUri(uri).rel(SELF).build();
    }

    static Link[] getPaginationLinks(int start, int count, int nrAvailable, UriInfo uriInfo) {
        final List<Link> links = new ArrayList<>();
        if (count <= nrAvailable) {
            final URI next = uriInfo.getAbsolutePathBuilder()
                    .queryParam(START, start + count)
                    .queryParam(COUNT, count).build();
            links.add(Link.fromUri(next).rel(NEXT).build());
        }
        if (start > 0) {
            final URI prev = uriInfo.getAbsolutePathBuilder()
                    .queryParam(START, Integer.max(start - count, 0))
                    .queryParam(COUNT, count).build();
            links.add(Link.fromUri(prev).rel(PREV).build());
        }
        return links.toArray(new Link[0]);
    }

    String NEXT = "next";
    String PREV = "prev";
    String SELF = "self";
    String COUNT = "count";
    String START = "start";
}
