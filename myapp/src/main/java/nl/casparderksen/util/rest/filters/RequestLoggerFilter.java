package nl.casparderksen.util.rest.filters;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Slf4j
@Provider
public class RequestLoggerFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (log.isDebugEnabled()) {
            log.debug("request {} {} {}", requestContext.getMethod(),
                    requestContext.getUriInfo().getPath(), requestContext.getHeaders());
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (log.isDebugEnabled()) {
            log.debug("response {} {} <{}> {}", requestContext.getMethod(),
                    requestContext.getUriInfo().getPath(), responseContext.getStatus(), responseContext.getHeaders());
        }
    }
}
