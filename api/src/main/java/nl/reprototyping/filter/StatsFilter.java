package nl.reprototyping.filter;


import nl.reprototyping.MongoRepository;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.util.Date;


@Provider
public class StatsFilter implements ContainerRequestFilter {
    @Inject
    MongoRepository mongoRepository;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        MultivaluedMap<String, String> queryParameters = requestContext.getUriInfo().getQueryParameters();
        String theme = queryParameters.getFirst("variant");
        if (theme == null) {
            theme = queryParameters.getFirst("theme");
        }

        Cookie uuidCookie = requestContext.getCookies().get("uuid");
        Cookie disabledCookie = requestContext.getCookies().get("disabled");
        mongoRepository.saveRequest(uuidCookie != null ? uuidCookie.getValue() : null,
                                    queryParameters.getFirst("host"), new Date(),
                                    theme,
                                    queryParameters.getFirst("enabled"),
                                    disabledCookie != null ? disabledCookie.getValue() : null);
    }
}
