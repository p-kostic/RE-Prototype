package nl.reprototyping;


import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Servlet extends HttpServlet {
    private static final String UUID_COOKIE_NAME = "uuid";

    private final MongoRepository mongoBean = new MongoRepository();
    private final Map<String, String> resourceMap = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Cookie[] cookies = req.getCookies();

        String uuid = null;
        if (cookies != null) {
            Optional<String> optionalUUID = Arrays.stream(cookies)
                                                  .filter(cookie -> cookie.getName().equals(UUID_COOKIE_NAME))
                                                  .map(Cookie::getValue)
                                                  .findFirst();
            if (optionalUUID.isPresent()) {
                uuid = optionalUUID.get();
            }
        }

        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            resp.setHeader("Set-Cookie", "uuid=" + uuid);
        }

        resp.setHeader("Cache-Control", "max-age=3600");

        String host = req.getParameter("host");
        String domain = getDomain(host);
        String variant = getVariant(req.getParameter("variant"));
        String css = getStyleSheet(variant, domain);

        mongoBean.saveRequest(
                req.getParameter("uuid"),
                req.getParameter("host"),
                new Date(),
                "",
                req.getParameter("enabled")
        );

        resp.setHeader("Content-Type", "text/css");
        resp.getWriter().println(String.format("/* %s: %s */", domain, variant));
        resp.getWriter().println(css);
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

        else return resourceMap.get("base");
    }

    private static String getStyleKey(String variant, String domain){
        return String.format("%s:%s", variant, domain);
    }

    @Override
    public void init() throws ServletException {
        super.init();

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
