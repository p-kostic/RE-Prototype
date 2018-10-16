package nl.reprototyping.servlet;


import nl.reprototyping.MongoRepository;

import javax.inject.Inject;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


@Path("api")
public class FeedbackServlet {
    @Inject
    MongoRepository mongoRepository;

    @Path("submit")
    @POST
    public Response submit(@FormParam("stain") int strain, @FormParam("environlight") int light,
                           @FormParam("message") String feedback, @CookieParam("uuid") String uuid) {

        mongoRepository.saveFeedback(strain, light, feedback, uuid);
        return Response.ok().build();
    }
}
