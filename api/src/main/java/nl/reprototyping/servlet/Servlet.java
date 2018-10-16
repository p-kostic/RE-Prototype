package nl.reprototyping.servlet;


import nl.reprototyping.MongoRepository;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/api")
public class Servlet {
    private final MongoRepository     mongoBean   = new MongoRepository();
    public static final String ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private final Map<String, String> resourceMap = new HashMap<>();

    @GET
    @Produces("text/css")
    public Response doGet(@QueryParam("uuid") String uuid, @QueryParam("variant") String variant,
                          @QueryParam("host") String host, @QueryParam("enabled") String enabled) {
        String domain = getDomain(host);

        String css = getStyleSheet(getVariant(variant), domain);
        if (css == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mongoBean.saveRequest(
                uuid,
                host,
                new Date(),
                "",
                enabled
        );

        return Response.ok(css).header(ALLOW_ORIGIN, "*").build();
    }

    private String getVariant(String variant) {
        if(variant == null || !resourceMap.containsKey(getStyleKey(variant, "base"))){
            return "dark";
        }
        return variant;
    }

    private static String getDomain(String host) {
        if (host == null || host.equals("")) {
            return "";
        }
        Pattern pattern = Pattern.compile("(?:www\\.)?(.+)\\.\\w+$");

        Matcher matcher = pattern.matcher(host);
        boolean found = matcher.find();
        return found ? matcher.group(1) : "";
    }

    private String getStyleSheet(String variant, String domain) {
        String key = getStyleKey(variant, domain);
        if (resourceMap.containsKey(key)) {
            return resourceMap.get(key);
        }

        else return null;
    }

    private static String getStyleKey(String variant, String domain){
        return String.format("%s:%s", variant, domain);
    }

    public Servlet() {
        URL resource = getClass().getClassLoader().getResource("stylesheet");

        assert resource != null;

        File styles = new File(resource.getPath());

        assert styles != null;

        String[] variants = styles.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });

        for(String variant: variants){

            File subdir = new File(styles, variant);

            assert subdir != null;

            File[] files = subdir.listFiles();

            assert files != null;
            for (File file : files) {
                String fileName = file.getName().replaceAll("(.*)\\.\\w+","$1");
                try {
                    String cssString = IOUtils.toString(file.toURI());
                    String key = getStyleKey(variant, fileName);
                    resourceMap.put(key, cssString);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
