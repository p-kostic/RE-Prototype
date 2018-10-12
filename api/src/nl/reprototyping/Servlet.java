package nl.reprototyping;


import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;


public class Servlet extends HttpServlet {
    private static final String UUID_COOKIE_NAME = "uuid";

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

        URL resource = getClass().getClassLoader().getResource("stylesheet/theme1.css");
        String cssString = IOUtils.toString(resource);

        resp.setHeader("Content-Type", "text/css");
        resp.getWriter().println(cssString);
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }
}
