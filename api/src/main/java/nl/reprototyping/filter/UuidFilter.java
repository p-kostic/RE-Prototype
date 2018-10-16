package nl.reprototyping.filter;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.UUID;


@Provider
public class UuidFilter implements ContainerResponseFilter {

    private static boolean disabled() {
        double d = Math.random();
        return (d < .07);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String uuid = requestContext.getUriInfo().getQueryParameters().getFirst("uuid");
        if (uuid == null) {
            if (requestContext.getHeaders().getFirst("uuid") == null) {
                uuid = UUID.randomUUID().toString();
                if (disabled()) {
                    responseContext.getHeaders().add("Set-Cookie", "disabled=true");
                }
                responseContext.getHeaders().add("Set-Cookie", "uuid=" + uuid);
            }
        }
    }
}
