package ua.com.tracktor.configuration;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Objects;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private Environment env;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/termsAndConditions").setViewName("termsAndConditions");
        registry.addViewController("/cabinet").setViewName("cabinet/index");
        //registry.addViewController("/login");
    }

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };

        if (Objects.requireNonNull(env.getProperty("server.ssl-redirection")).equalsIgnoreCase("true")) {
            tomcat.addAdditionalTomcatConnectors(redirectConnector());
        }

        return tomcat;
    }

    private Connector redirectConnector() {
        int portRedirectFrom = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.ssl-redirection-port")));
        int portRedirectTo   = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));

        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(portRedirectFrom);
        connector.setSecure(false);
        connector.setRedirectPort(portRedirectTo);
        return connector;
    }
}
