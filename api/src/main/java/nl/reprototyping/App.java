package nl.reprototyping;


import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;


public class App extends ResourceConfig {
    public App() {
        packages("nl.reprototyping.servlet");
        register(JspMvcFeature.class);
        property(JspMvcFeature.TEMPLATE_BASE_PATH, "/WEB-INF/jsp");
    }
}
