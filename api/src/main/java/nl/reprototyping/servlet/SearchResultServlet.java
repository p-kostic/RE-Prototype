package nl.reprototyping.servlet;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import nl.reprototyping.bing.BingSearch;
import nl.reprototyping.bing.SearchResults;
import nl.reprototyping.bing.WebPage;
import nl.reprototyping.model.ResultsModel;
import org.glassfish.jersey.server.mvc.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Path("")
public class SearchResultServlet {
    private static final int                          COUNT     = 50;
    private static final int                          PAGE_SIZE = 10;
    private              Cache<String, SearchResults> cache;

    public SearchResultServlet() {
        cache = CacheBuilder.newBuilder()
                            .maximumSize(10000)
                            .expireAfterWrite(3, TimeUnit.HOURS)
                            .build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable get(@QueryParam("query") String query, @QueryParam("theme") String theme,
                        @QueryParam("page") int page) throws Exception {

        if (query != null) {
            SearchResults results;
            SearchResults cacheResultOrNull = cache.getIfPresent(query);
            if (cacheResultOrNull != null) {
                results = cacheResultOrNull;
            } else {
                results = BingSearch.SearchWeb(query, COUNT);
                cache.put(query, results);
            }

            List<WebPage> webPages = results.getWebPages().getValue();
            int offset = page * PAGE_SIZE;
            int lastIndex = webPages.size() - 1;
            if (offset < lastIndex) {
                int endIndex = offset + PAGE_SIZE;
                webPages = webPages.subList(offset, endIndex < lastIndex ? endIndex : lastIndex);
            }

            return new Viewable("/search.jsp", new ResultsModel(webPages, PAGE_SIZE, lastIndex));
        }

        return new Viewable("/search.jsp");
    }
}
