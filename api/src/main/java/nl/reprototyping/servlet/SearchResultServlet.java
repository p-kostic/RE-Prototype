package nl.reprototyping.servlet;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import nl.reprototyping.bing.BingSearch;
import nl.reprototyping.bing.SearchResults;
import nl.reprototyping.bing.WebPage;
import nl.reprototyping.bing.WebPages;
import nl.reprototyping.util.Theme;
import nl.reprototyping.util.ThemeService;
import org.glassfish.jersey.server.mvc.Viewable;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Stateless
@Path("")
public class SearchResultServlet {
    @Inject
    ThemeService themeService;

    private static final int                          COUNT     = 50;
    private static final int                          PAGE_SIZE = 10;
    private              Cache<String, SearchResults> cache;

    public SearchResultServlet() {
        cache = CacheBuilder.newBuilder()
                            .maximumSize(10000)
                            .expireAfterWrite(3, TimeUnit.HOURS)
                            .build();
    }

    @Path("results")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get(@QueryParam("query") String query, @QueryParam("page") int page,
                        @QueryParam("theme") String themeParam, @CookieParam("theme") String themeCookie) throws Exception {

        HashMap<String, Object> model = new HashMap<>();
        Response.ResponseBuilder builder = Response.ok();
        determineTheme(themeParam, themeCookie, model, builder);

        if (query != null && !query.equals("")) {
            SearchResults results;
            SearchResults cacheResultOrNull = cache.getIfPresent(query);
            if (cacheResultOrNull != null) {
                results = cacheResultOrNull;
            } else {
                results = BingSearch.SearchWeb(query, COUNT);
                cache.put(query, results);
            }

            WebPages resultsWebPages = results.getWebPages();
            if (resultsWebPages != null) {
                List<WebPage> webPages = resultsWebPages.getValue();
                int offset = page * PAGE_SIZE;
                int lastIndex = webPages.size() - 1;
                int endIndex = offset + PAGE_SIZE;
                webPages = webPages.subList(offset, endIndex < lastIndex ? endIndex : lastIndex);
                model.put("results", webPages);
                model.put("totalSize", lastIndex);
                model.put("pageSize", endIndex);

                return builder.entity(new Viewable("/results.jsp", model)).build();
            }
        }

        return builder.entity(new Viewable("/search.jsp", model)).build();
    }

    private void determineTheme(String themeParam, String themeCookie, HashMap<String, Object> model,
                                Response.ResponseBuilder builder) {
        if (themeParam != null) {
            builder.cookie(themeCookie(themeParam));
            model.put("theme", themeParam);
        } else if (themeCookie != null) {
            model.put("theme", themeCookie);
        } else {
            Theme theme = themeService.getTheme();
            model.put("theme", theme);
        }
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get(@QueryParam("theme") String themeParam, @CookieParam("theme") String themeCookie) {
        HashMap<String, Object> model = new HashMap<>();
        Response.ResponseBuilder builder = Response.ok();
        determineTheme(themeParam, themeCookie, model, builder);
        return Response.ok(new Viewable("/search.jsp", model)).build();
    }


    private static NewCookie themeCookie(String theme) {
        return new NewCookie("theme", theme, "/", null, null, 3400, false);
    }
}
