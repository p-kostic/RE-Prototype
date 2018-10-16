package nl.reprototyping;


import nl.reprototyping.util.ThemeService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;


public class App extends ResourceConfig {
    public App() {
        packages("nl.reprototyping");
        register(JspMvcFeature.class);
        property(JspMvcFeature.TEMPLATE_BASE_PATH, "/WEB-INF/jsp");
        ThemeService themeService = new ThemeService();
        MongoRepository mongoRepository = new MongoRepository();
        registerInstances(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(themeService).to(ThemeService.class);
                bind(mongoRepository).to(MongoRepository.class);
            }
        });
    }
}
